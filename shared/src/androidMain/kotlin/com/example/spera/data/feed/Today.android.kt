package com.example.spera.data.feed

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// SimpleDateFormat plutôt que java.time : compatible minSdk 24 sans desugaring.
actual fun todayIso(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
