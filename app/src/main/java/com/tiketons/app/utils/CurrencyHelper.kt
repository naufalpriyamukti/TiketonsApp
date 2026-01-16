package com.tiketons.app.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyHelper {
    fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return format.format(amount).replace("Rp", "Rp ").substringBeforeLast(",00")
    }
}