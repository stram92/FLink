package com.saladstudios.FLink.utility.format

import java.text.DecimalFormat
import java.util.*

fun prettyPrintNumberWithCurrency (amount: String): String {
    return if (amount != null && amount != "") {
        var formatter = DecimalFormat.getInstance(Locale.ROOT)
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        formatter.format(amount.toDouble()) + " €"
    } else {
        ""
    }
}

fun prettyPrintNumber (amount: String): String {
    return if (amount != null && amount != "") {
        var formatter = DecimalFormat.getInstance(Locale.ROOT)
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        formatter.format(amount.toDouble())
    } else {
        ""
    }
}