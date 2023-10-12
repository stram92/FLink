package com.saladstudios.FLink.ui.finances

data class FinancesItemsViewModel(val id: Int, val payer: String, val description: String, val sign: String, val amount: String, val entryDate: String, val payedFor: String, val payedForAmount: String?, val category: String?)
