package com.example.FLink.ui.finances

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FinancesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is finances Fragment"
    }
    val text: LiveData<String> = _text
}