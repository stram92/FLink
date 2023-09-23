package com.saladstudios.FLink.ui.finances

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.saladstudios.FLink.R
import com.saladstudios.FLink.databinding.FinancesNewEntryBinding
import java.util.*

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

        binding.financesNewEntrySave.setOnClickListener { newEntrySave() }
    }

    fun newEntrySave() {
        var returnIntent = Intent()
        var payer = ""
        var payedfor = ""
        var payedforamount = ""
        val dateMillis: Long = binding.financesNewEntryCalendar.date
        val date = Date(dateMillis)
        var entryDate = ""

        if (binding.financesNewEntrySwitchSascha.isChecked) {
            payer = "S"
        } else if (binding.financesNewEntrySwitchDenise.isChecked) {
            payer = "D"
        } else {
            payer = "B"
        }

        if (binding.financesNewEntryForSascha.isChecked && !binding.financesNewEntryForDenise.isChecked) {
            payedfor = "S"
        } else if (!binding.financesNewEntryForSascha.isChecked && binding.financesNewEntryForDenise.isChecked) {
            payedfor = "D"
        } else {
            payedfor = "B"
        }

        if (payedfor.equals("S")) {
            payedforamount=binding.financesNewEntryAmountSaschaInput.text.toString()
        } else if (payedfor.equals("D")) {
            payedforamount=binding.financesNewEntryAmountDeniseInput.text.toString()
        }

        entryDate = DateFormat.format("dd", date).toString() + "." + DateFormat.format("MM",date).toString() + "." + DateFormat.format("yyyy",date).toString()


        returnIntent.putExtra("payer", payer)
        returnIntent.putExtra("description", binding.financesNewEntryDescriptionInput.text.toString())
        returnIntent.putExtra("amount", binding.financesNewEntryAmountInput.text.toString())
        returnIntent.putExtra("entryDate", entryDate)
        returnIntent.putExtra("payedFor", payedfor)
        returnIntent.putExtra("payedForAmount", payedforamount)

        setResult(Activity.RESULT_OK,returnIntent)

        finish()
    }
}