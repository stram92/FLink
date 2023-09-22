package com.saladstudios.FLink.ui.finances

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.saladstudios.FLink.R
import com.saladstudios.FLink.databinding.FinancesNewEntryBinding

class FinancesNewEntry : AppCompatActivity() {
    private lateinit var binding: FinancesNewEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FinancesNewEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.finances_new_entry_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }


}