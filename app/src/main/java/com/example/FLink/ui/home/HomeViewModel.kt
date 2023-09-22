package com.example.FLink.ui.home

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val _dateTimeText = MutableLiveData<String>().apply {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

        handler.postDelayed(object : Runnable {
            override fun run() {
                value = LocalDateTime.now().format(formatter).toString()
                handler.postDelayed(this, 500)
            }
        }, 0)
    }
    val dateTimeText: LiveData<String> = _dateTimeText

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
}