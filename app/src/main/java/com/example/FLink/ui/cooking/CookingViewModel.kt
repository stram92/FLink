package com.example.FLink.ui.cooking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CookingViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is cooking Fragment"
    }
    val text: LiveData<String> = _text
}