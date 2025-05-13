package com.stockbubble.dev.network.data

import java.io.IOException

class FetchException(message: String, ex: Throwable? = null): IOException(message, ex)