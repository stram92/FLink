package com.example.heartlink.ui.cooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShoppingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is shopping Fragment"
    }
    val text: LiveData<String> = _text
}