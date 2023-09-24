package com.saladstudios.FLink.utility.format

import java.text.DecimalFormat
import java.util.*

fun prettyPrintNumber (amount: String): String {
    var formatter = DecimalFormat.getInstance(Locale.ROOT)
    formatter.minimumFractionDigits=2
    formatter.maximumFractionDigits=2

    return "- "+formatter.format(amount.toDouble())+" €"
}