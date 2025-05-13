package com.stockbubble.dev.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.stockbubble.dev.R
import com.stockbubble.dev.databinding.FragmentHomeBinding
import com.stockbubble.dev.isNetworkOnline
import com.stockbubble.dev.network.data.Quote
import com.stockbubble.dev.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private val binding by viewBinding(FragmentHomeBinding::inflate)
    private val adapter = EmitenAdapter(onItemClick = ::showOpenQuoteDialog)

    private val openQuoteDialog by lazy { DialogOpenItemQuote() }
    private fun showOpenQuoteDialog(quote: Quote?) {
        openQuoteDialog.showWithSymbol(childFragmentManager, quote)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updateLayoutParams<MarginLayoutParams> {
                topMargin = insets.top
            }
            WindowInsetsCompat.CONSUMED
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.rvData) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updatePadding(bottom = insets.bottom)
            WindowInsetsCompat.CONSUMED
        }

        var timerJob: Job? = null
        var startTime: Long = 0
        viewModel.nextFetchMs.observe(viewLifecycleOwner) { future ->
            startTime = viewModel.lasFetchedMs
            binding.progressHorizontal.run {
                isIndeterminate = false
                progress = 0

                timerJob?.cancel()
                timerJob = lifecycleScope.launch(Dispatchers.Main) {
                    while (isActive) {
                        val now = System.currentTimeMillis()
                        val total = future - startTime
                        val elapsed = now - startTime

                        val progressVal = ((elapsed.toDouble() / total) * 100).coerceIn(0.0, 100.0)
                        progress = progressVal.roundToInt()

                        if (progress >= 100.0)
                            break

                        delay(100)
                    }
                }
            }
        }

        binding.rvData.adapter = adapter

        viewModel.fetchState.observe(viewLifecycleOwner) {
            binding.progressHorizontal.isIndeterminate = false
        }

        binding.iclHeader.run {
            tvName.setOnClickListener(::onHeaderClick)
            tvRegularPrice.setOnClickListener(::onHeaderClick)
            tvRegularMarketCap.setOnClickListener(::onHeaderClick)
            tvRegularMarketVolume.setOnClickListener(::onHeaderClick)
            tvRegularMarketChange.setOnClickListener(::onHeaderClick)
            tv50DayChangePct.setOnClickListener(::onHeaderClick)
            tv52WeekLowChangePct.setOnClickListener(::onHeaderClick)
            tv52WeekHighChangePct.setOnClickListener(::onHeaderClick)
            tv200DayAvgChangePct.setOnClickListener(::onHeaderClick)
            tvAvgVolume10Day.setOnClickListener(::onHeaderClick)
            tvAvgVolume3Mo.setOnClickListener(::onHeaderClick)
            tvDividendYield.setOnClickListener(::onHeaderClick)
            tvRating.setOnClickListener(::onHeaderClick)

            tvRating.setOnLongClickListener {
                fetch()
                true
            }
        }
    }


    private var job: Job? = null
    private fun onHeaderClick(v: View) {
        job?.cancel()
        job = lifecycleScope.launch(Dispatchers.IO) {
            when (v.id) {
                R.id.tv_name -> sorting(v.id, Quote::symbol)
                R.id.tv_regular_price -> sorting(v.id, Quote::regularMarketPrice)
                R.id.tv_regular_market_cap -> sorting(v.id, Quote::marketCap)
                R.id.tv_regular_market_volume -> sorting(v.id, Quote::regularMarketVolume)
                R.id.tv_regular_market_change -> sorting(v.id, Quote::changeInPercent)
                R.id.tv_50_day_change_pct -> sorting(v.id, Quote::fiftyDayAverageChangePercent)
                R.id.tv_200_day_avg_change_pct ->
                    sorting(v.id, Quote::twoHundredDayAverageChangePercent)

                R.id.tv_52_week_low_change_pct -> sorting(v.id, Quote::fiftyTwoWeekLowChangePercent)
                R.id.tv_52_week_high_change_pct ->
                    sorting(v.id, Quote::fiftyTwoWeekHighChangePercent)

                R.id.tv_avg_volume_10_day -> sorting(v.id, Quote::averageDailyVolume10Day)
                R.id.tv_avg_volume_3_mo -> sorting(v.id, Quote::averageDailyVolume3Month)
                R.id.tv_dividend_yield -> sorting(v.id, Quote::annualDividendYield)
                R.id.tv_rating -> sorting(v.id, Quote::averageAnalystRating)
            }
        }
    }

    private var lastSortId: Int = 0
    private fun <R : Comparable<R>> sorting(id: Int, selector: (Quote) -> R?) {
        if (lastSortId != id) {
            adapter.currentList.sortedBy(selector).also {
                lastSortId = id
                adapter.submitList(it)
            }
        } else {
            adapter.currentList.sortedByDescending(selector).also {
                lastSortId = 0
                adapter.submitList(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (adapter.currentList.isEmpty()) {
            fetch()
        }
    }


    private var attemptingFetch = false
    private var fetchCount = 0
    private val MAX_FETCH_COUNT = 3
    private fun fetch() {
        if (!attemptingFetch) {
            if (requireActivity().isNetworkOnline()) {
                fetchCount++
                binding.progressHorizontal.isIndeterminate = true
                // Don't attempt to make many requests in a row if the stocks don't fetch.
                if (fetchCount <= MAX_FETCH_COUNT) {
                    attemptingFetch = true
                    viewModel.fetch().observe(viewLifecycleOwner) { success ->
                        attemptingFetch = false
                        binding.progressHorizontal.isIndeterminate = false
                        if (success) {
                            update()
                        }
                    }
                } else {
                    attemptingFetch = false
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                    binding.progressHorizontal.isIndeterminate = false
                }
            } else {
                attemptingFetch = false
                Toast.makeText(requireContext(), "No Network", Toast.LENGTH_SHORT).show()
                binding.progressHorizontal.isIndeterminate = false
            }
        }
    }

    private fun update() {
        updateHeader()
        adapter.submitList(viewModel.quotes.value)
        fetchCount = 0
    }

    private fun updateHeader() {

    }
}