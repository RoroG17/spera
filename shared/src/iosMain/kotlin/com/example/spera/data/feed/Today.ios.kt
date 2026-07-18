package com.example.spera.data.feed

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter

actual fun todayIso(): String {
    val formatter = NSDateFormatter()
    formatter.dateFormat = "yyyy-MM-dd"
    return formatter.stringFromDate(NSDate())
}
