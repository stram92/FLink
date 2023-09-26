package com.saladstudios.FLink.ui.finances

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.saladstudios.FLink.R
import com.saladstudios.FLink.databinding.FinancesNewEntryBinding
import com.saladstudios.FLink.utility.format.prettyPrintNumber
import java.util.*
import kotlin.math.round

class FinancesNewEntry : AppCompatActivity() {
    private lateinit var binding: FinancesNewEntryBinding

    private lateinit var date: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FinancesNewEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.finances_new_entry_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        binding.financesNewEntrySave.setOnClickListener { newEntrySave() }
        binding.financesNewEntrySwitchSascha.setOnClickListener { switchSascha() }
        binding.financesNewEntrySwitchDenise.setOnClickListener { switchDenise() }
        binding.financesNewEntryAmountInput.setOnFocusChangeListener{ _ , hasFocus -> if (!hasFocus) sharePrice()}
        binding.financesNewEntryAmountDeniseInput.setOnFocusChangeListener{ _ , hasFocus -> if (!hasFocus) reDistributePriceDenise()}
        binding.financesNewEntryAmountSaschaInput.setOnFocusChangeListener{ _ , hasFocus -> if (!hasFocus) reDistributePriceSascha()}
        binding.financesNewEntryForSascha.setOnClickListener { checkSascha() }
        binding.financesNewEntryForDenise.setOnClickListener { checkDenise() }
        binding.financesNewEntryCalendar.setOnDateChangeListener() { calView: CalendarView, year: Int, month: Int, dayOfMonth: Int -> dateChanged(calView,year,month,dayOfMonth) }
    }

    fun newEntrySave() {
        if (!binding.financesNewEntryForSascha.isChecked && !binding.financesNewEntryForDenise.isChecked) {
            val toast = Toast.makeText(this, "Für wen wurde bezahlt? Bitte auswählen", Toast.LENGTH_SHORT)
            toast.show()
        } else if (binding.financesNewEntryDescriptionInput.text.toString().isEmpty()) {
            val toast = Toast.makeText(this, "Beschreibung darf nicht leer sein!", Toast.LENGTH_SHORT)
            toast.show()
        } else if (binding.financesNewEntryAmountInput.text.isEmpty()){
            val toast = Toast.makeText(this, "Wert darf nicht leer sein!", Toast.LENGTH_SHORT)
            toast.show()
        } else {
            var returnIntent = Intent()
            var payer = ""
            var payedfor = ""
            var payedforamount = ""
            val dateMillis: Long = binding.financesNewEntryCalendar.date
            val date = Date(dateMillis)
            var entryDate = ""

            var amount = prettyPrintNumber(binding.financesNewEntryAmountInput.text.toString())

            if (binding.financesNewEntrySwitchSascha.isChecked) {
                payer = "S"
            } else if (binding.financesNewEntrySwitchDenise.isChecked) {
                payer = "D"
            } else {
                payer = "B"
            }

            payedfor = if (binding.financesNewEntryForSascha.isChecked && !binding.financesNewEntryForDenise.isChecked) {
                "S"
            } else if (!binding.financesNewEntryForSascha.isChecked && binding.financesNewEntryForDenise.isChecked) {
                "D"
            } else {
                "B"
            }

            if (payedfor.equals("B")) {
                if (payer.equals("S")) {
                    payedforamount = "Denise: "+ prettyPrintNumber(binding.financesNewEntryAmountDeniseInput.text.toString())
                } else if (payer.equals("D")) {
                    payedforamount = "Sascha: "+ prettyPrintNumber(binding.financesNewEntryAmountSaschaInput.text.toString())
                }
            }

            entryDate = DateFormat.format("dd", date).toString() + "." + DateFormat.format("MM", date)
                .toString() + "." + DateFormat.format("yyyy", date).toString()

            returnIntent.putExtra("payer", payer)
            returnIntent.putExtra(
                "description",
                binding.financesNewEntryDescriptionInput.text.toString()
            )
            returnIntent.putExtra("amount", amount)
            returnIntent.putExtra("entryDate", entryDate)
            returnIntent.putExtra("payedFor", payedfor)
            returnIntent.putExtra("payedForAmount", payedforamount)

            setResult(Activity.RESULT_OK, returnIntent)

            finish()
        }
    }

    fun sharePrice() {
        if (!binding.financesNewEntryAmountInput.text.isEmpty()) {
            binding.financesNewEntryAmountInput.setText((round(binding.financesNewEntryAmountInput.text.toString().toDouble()*100)/100).toString())
            if (binding.financesNewEntryForDenise.isChecked && binding.financesNewEntryForSascha.isChecked) {
                binding.financesNewEntryAmountDeniseInput.setText((round(((binding.financesNewEntryAmountInput.text.toString().toDouble()/2)*100))/100).toString())
                binding.financesNewEntryAmountSaschaInput.setText((round((((binding.financesNewEntryAmountInput.text.toString().toDouble()-round(((binding.financesNewEntryAmountInput.text.toString().toDouble()/2)*100))/100))*100))/100).toString())
            } else if (!binding.financesNewEntryForDenise.isChecked && binding.financesNewEntryForSascha.isChecked) {
                binding.financesNewEntryAmountSaschaInput.setText(binding.financesNewEntryAmountInput.text)
                binding.financesNewEntryAmountDeniseInput.setText("0")
            } else if (binding.financesNewEntryForDenise.isChecked && !binding.financesNewEntryForSascha.isChecked) {
                binding.financesNewEntryAmountDeniseInput.setText(binding.financesNewEntryAmountInput.text)
                binding.financesNewEntryAmountSaschaInput.setText("0")
            }
        } else {
            binding.financesNewEntryAmountDeniseInput.setText("0")
            binding.financesNewEntryAmountSaschaInput.setText("0")
        }
    }

    fun reDistributePriceSascha() {
        if (!binding.financesNewEntryAmountInput.text.isEmpty()) {
            if (!binding.financesNewEntryAmountSaschaInput.text.isEmpty()) {
                binding.financesNewEntryAmountSaschaInput.setText((round(binding.financesNewEntryAmountSaschaInput.text.toString().toDouble() * 100) / 100).toString())
                if (binding.financesNewEntryAmountSaschaInput.text.toString().toDouble() < binding.financesNewEntryAmountInput.text.toString().toDouble()) {
                    binding.financesNewEntryAmountDeniseInput.setText((round((((binding.financesNewEntryAmountInput.text.toString().toDouble()-round(((binding.financesNewEntryAmountSaschaInput.text.toString().toDouble())*100))/100))*100))/100).toString())
                }
            }
        }
    }

    fun reDistributePriceDenise() {
        if (!binding.financesNewEntryAmountInput.text.isEmpty()) {
            if (!binding.financesNewEntryAmountDeniseInput.text.isEmpty()) {
                binding.financesNewEntryAmountDeniseInput.setText((round(binding.financesNewEntryAmountDeniseInput.text.toString().toDouble() * 100) / 100).toString())
                if (binding.financesNewEntryAmountDeniseInput.text.toString().toDouble() < binding.financesNewEntryAmountInput.text.toString().toDouble()) {
                    binding.financesNewEntryAmountSaschaInput.setText((round((((binding.financesNewEntryAmountInput.text.toString().toDouble()-round(((binding.financesNewEntryAmountDeniseInput.text.toString().toDouble())*100))/100))*100))/100).toString())
                }
            }
        }
    }

    fun checkSascha() {
        binding.financesNewEntryAmountSaschaInput.isEnabled=binding.financesNewEntryForSascha.isChecked

        if (!binding.financesNewEntryAmountSaschaInput.isEnabled) {
            binding.financesNewEntryAmountSaschaInput.setText("0")

            if (binding.financesNewEntryAmountInput.text.isNotEmpty() && binding.financesNewEntryForDenise.isChecked) {
                binding.financesNewEntryAmountDeniseInput.setText(binding.financesNewEntryAmountInput.text.toString())
            }
        } else if (binding.financesNewEntryForDenise.isChecked) {
            if (binding.financesNewEntryAmountInput.text.isNotEmpty()) {
                binding.financesNewEntryAmountSaschaInput.setText((round(((binding.financesNewEntryAmountInput.text.toString().toDouble()/2)*100))/100).toString())
                binding.financesNewEntryAmountDeniseInput.setText((round((((binding.financesNewEntryAmountInput.text.toString().toDouble()-round(((binding.financesNewEntryAmountSaschaInput.text.toString().toDouble())*100))/100))*100))/100).toString())
            }
        } else {
            binding.financesNewEntryAmountSaschaInput.setText(binding.financesNewEntryAmountInput.text.toString())
        }
    }

    fun checkDenise() {
        binding.financesNewEntryAmountDeniseInput.isEnabled=binding.financesNewEntryForDenise.isChecked

        if (!binding.financesNewEntryAmountDeniseInput.isEnabled) {
            binding.financesNewEntryAmountDeniseInput.setText("0")

            if (binding.financesNewEntryAmountInput.text.isNotEmpty() && binding.financesNewEntryForSascha.isChecked) {
                binding.financesNewEntryAmountSaschaInput.setText(binding.financesNewEntryAmountInput.text.toString())
            }
        } else if (binding.financesNewEntryForSascha.isChecked) {
            if (binding.financesNewEntryAmountInput.text.isNotEmpty()) {
                binding.financesNewEntryAmountDeniseInput.setText((round(((binding.financesNewEntryAmountInput.text.toString().toDouble()/2)*100))/100).toString())
                binding.financesNewEntryAmountSaschaInput.setText((round((((binding.financesNewEntryAmountInput.text.toString().toDouble()-round(((binding.financesNewEntryAmountDeniseInput.text.toString().toDouble())*100))/100))*100))/100).toString())
            }
        } else {
            binding.financesNewEntryAmountDeniseInput.setText(binding.financesNewEntryAmountInput.text.toString())
        }
    }

    fun switchSascha () {
        if (binding.financesNewEntrySwitchSascha.isChecked) {
            binding.financesNewEntrySwitchDenise.isChecked=false
        } else if (!binding.financesNewEntrySwitchSascha.isChecked) {
            binding.financesNewEntrySwitchDenise.isChecked=true
        }

    }

    fun switchDenise () {
        if (binding.financesNewEntrySwitchDenise.isChecked) {
            binding.financesNewEntrySwitchSascha.isChecked=false
        } else if (!binding.financesNewEntrySwitchDenise.isChecked) {
            binding.financesNewEntrySwitchSascha.isChecked=true
        }
    }

    private fun dateChanged(calView: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
        // Create calender object with which will have system date time.
        val calender: Calendar = Calendar.getInstance()

        // Set attributes in calender object as per selected date.
        calender.set(year, month, dayOfMonth)

        // Now set calenderView with this calender object to highlight selected date on UI.
        calView.setDate(calender.timeInMillis, true, true)
    }
}