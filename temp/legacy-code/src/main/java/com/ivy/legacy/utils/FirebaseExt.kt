package com.ivy.legacy.utils

import timber.log.Timber

fun sendToCrashlytics(
    msg: String
) {
    DeveloperException(msg).sendToCrashlytics(msg)
}

fun Exception.sendToCrashlytics(
    clarification: String? = null
) {
    if (clarification != null) {
        Timber.e(this, "Error: %s", clarification)
    } else {
        Timber.e(this)
    }
}

fun logToCrashlytics(msg: String) {
    Timber.d("LocalLog: %s", msg)
}

class DeveloperException(msg: String) : Exception(msg)
