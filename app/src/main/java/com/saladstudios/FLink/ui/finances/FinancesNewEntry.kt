package com.saladstudios.FLink.ui.finances

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.KeyEvent
import android.view.View
import android.widget.CalendarView
import android.widget.EditText
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
    private var id: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FinancesNewEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.finances_new_entry_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        var extras: Bundle? = intent.extras
        if (extras != null) {
            id = extras.getInt("id")

            if (extras.getString("payer") == "S") {
                binding.financesNewEntrySwitchSascha.isChecked=true
                binding.financesNewEntrySwitchDenise.isChecked=false
            } else if (extras.getString("payer") == "D") {
                binding.financesNewEntrySwitchSascha.isChecked=false
                binding.financesNewEntrySwitchDenise.isChecked=true
            }

            if (extras.getString("payedForAmount")=="") {
                if(extras.getString("payer")=="S" ) {
                    binding.financesNewEntryForSascha.isChecked=true
                    binding.financesNewEntryForDenise.isChecked=false
                    binding.financesNewEntryAmountSaschaInput.setText(extras.getString("amount"))
                    binding.financesNewEntryAmountDeniseInput.setText("0.00")
                } else if (extras.getString("payer")=="D") {
                    binding.financesNewEntryForSascha.isChecked=false
                    binding.financesNewEntryForDenise.isChecked=true
                    binding.financesNewEntryAmountDeniseInput.setText(extras.getString("amount"))
                    binding.financesNewEntryAmountSaschaInput.setText("0.00")
                }
            } else {
                binding.financesNewEntryForSascha.isChecked=true
                binding.financesNewEntryForDenise.isChecked=true

                if(extras.getString("payedFor")=="S") {
                    binding.financesNewEntryAmountSaschaInput.setText(extras.getString("payedForAmount"))
                    binding.financesNewEntryAmountDeniseInput.setText(prettyPrintNumber((extras.getString("amount")!!.toDouble() - extras.getString("payedForAmount")!!.toDouble()).toString()))
                } else {
                    binding.financesNewEntryAmountDeniseInput.setText(extras.getString("payedForAmount"))
                    binding.financesNewEntryAmountSaschaInput.setText(prettyPrintNumber((extras.getString("amount")!!.toDouble() - extras.getString("payedForAmount")!!.toDouble()).toString()))
                }
            }

            if (extras.getString("entryDate") != null) {
                dateChanged(
                    binding.financesNewEntryCalendar,
                    extras.getString("entryDate")!!.substring(6,10).toInt(),
                    extras.getString("entryDate")!!.substring(3,5).toInt()-1,
                    extras.getString("entryDate")!!.substring(0,2).toInt()
                )
            }

            binding.financesNewEntryDeductionAddition.isChecked = extras.getString("sign")=="+"

            binding.financesNewEntryDescriptionInput.setText(extras.getString("description"))
            binding.financesNewEntryAmountInput.setText(prettyPrintNumber(extras.getString("amount")!!))

        }

        binding.financesNewEntrySave.setOnClickListener { newEntrySave() }
        binding.financesNewEntrySwitchSascha.setOnClickListener { switchSascha() }
        binding.financesNewEntrySwitchDenise.setOnClickListener { switchDenise() }
        binding.financesNewEntryAmountInput.setOnFocusChangeListener{ _ , hasFocus -> if (!hasFocus) prettyPrintAmout(binding.financesNewEntryAmountInput)}
        binding.financesNewEntryAmountInput.setOnKeyListener{ _, _, event -> sharePrice(event)}
        binding.financesNewEntryAmountDeniseInput.setOnFocusChangeListener{ _ , hasFocus -> if (!hasFocus) prettyPrintAmout(binding.financesNewEntryAmountDeniseInput)}
        binding.financesNewEntryAmountDeniseInput.setOnKeyListener{ amountInput: View, keyCode: Int, event: KeyEvent -> reDistributePriceDenise(
            event
        )}
        binding.financesNewEntryAmountSaschaInput.setOnFocusChangeListener{ _ , hasFocus -> if (!hasFocus) prettyPrintAmout(binding.financesNewEntryAmountSaschaInput)}
        binding.financesNewEntryAmountSaschaInput.setOnKeyListener{ amountInput: View, keyCode: Int, event: KeyEvent -> reDistributePriceSascha(
            event
        )}
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
        } else if ((binding.financesNewEntryAmountSaschaInput.text.toString().ifBlank { "0.00" }.toDouble()+
                    binding.financesNewEntryAmountDeniseInput.text.toString().ifBlank { "0.00" }.toDouble()) >
                    binding.financesNewEntryAmountInput.text.toString().ifBlank { "0.00" }.toDouble()) {
            val toast = Toast.makeText(this, "Wertverteilung zu Hoch!", Toast.LENGTH_SHORT)
            toast.show()
        } else {
            var returnIntent = Intent()
            var payer: String
            var payedforamount = ""
            val dateMillis: Long = binding.financesNewEntryCalendar.date
            val date = Date(dateMillis)

            var sign: String

            var amount = binding.financesNewEntryAmountInput.text.toString()

            if (binding.financesNewEntrySwitchSascha.isChecked) {
                payer = "S"
            } else if (binding.financesNewEntrySwitchDenise.isChecked) {
                payer = "D"
            } else {
                payer = "B"
            }

            var payedfor: String = if (payer == "D" && binding.financesNewEntryForSascha.isChecked) {
                "S"
            } else if (payer == "S" && binding.financesNewEntryForDenise.isChecked) {
                "D"
            } else {
                ""
            }

            if (payedfor=="S") {
                payedforamount=binding.financesNewEntryAmountSaschaInput.text.toString()
            } else if (payedfor=="D") {
                payedforamount=binding.financesNewEntryAmountDeniseInput.text.toString()
            } else {
                payedforamount=""
            }

            if (binding.financesNewEntryDeductionAddition.isEnabled) {
                sign = "-"
            } else {
                sign = "+"
            }

            var entryDate: String =
                DateFormat.format("dd", date).toString() + "." + DateFormat.format("MM", date)
                .toString() + "." + DateFormat.format("yyyy", date).toString()

            if (id != -1) {
                returnIntent.putExtra("id",id)
            }

            returnIntent.putExtra("payer", payer)
            returnIntent.putExtra("description",binding.financesNewEntryDescriptionInput.text.toString())
            returnIntent.putExtra("sign",sign)
            returnIntent.putExtra("amount", amount)
            returnIntent.putExtra("entryDate", entryDate)
            returnIntent.putExtra("payedFor", payedfor)
            returnIntent.putExtra("payedForAmount", payedforamount)

            setResult(Activity.RESULT_OK, returnIntent)

            finish()
        }
    }

    fun sharePrice(event: KeyEvent): Boolean {

        if (event.action == KeyEvent.ACTION_UP)
        {
            if (!binding.financesNewEntryAmountInput.text.isEmpty()) {
                if (binding.financesNewEntryForDenise.isChecked && binding.financesNewEntryForSascha.isChecked) {
                    binding.financesNewEntryAmountDeniseInput.setText(String.format(Locale.US,"%.2f",binding.financesNewEntryAmountInput.text.toString().toDouble()/2))
                    binding.financesNewEntryAmountSaschaInput.setText(String.format(Locale.US,"%.2f",binding.financesNewEntryAmountInput.text.toString().toDouble()-binding.financesNewEntryAmountDeniseInput.text.toString().toDouble()))
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
        return false
    }

    fun prettyPrintAmout(AmountInput: EditText) {
        if (AmountInput.text.isNotEmpty()) {
            AmountInput.setText(String.format(Locale.US,"%.2f", (AmountInput.text.toString().toDouble())))
        }
    }

    fun reDistributePriceSascha(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP)
        {
            if (!binding.financesNewEntryAmountInput.text.isEmpty()) {
                if (!binding.financesNewEntryAmountSaschaInput.text.isEmpty()) {
                    if (binding.financesNewEntryAmountSaschaInput.text.toString().toDouble() <= binding.financesNewEntryAmountInput.text.toString().toDouble()) {
                        binding.financesNewEntryAmountDeniseInput.setText(String.format(Locale.US,"%.2f",binding.financesNewEntryAmountInput.text.toString().toDouble()-binding.financesNewEntryAmountSaschaInput.text.toString().toDouble()))
                    }
                }
            }
        }
        return false
    }

    fun reDistributePriceDenise(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP)
        {
            if (!binding.financesNewEntryAmountInput.text.isEmpty()) {
                if (!binding.financesNewEntryAmountDeniseInput.text.isEmpty()) {
                    if (binding.financesNewEntryAmountDeniseInput.text.toString().toDouble() <= binding.financesNewEntryAmountInput.text.toString().toDouble()) {
                        binding.financesNewEntryAmountSaschaInput.setText(String.format(Locale.US,"%.2f",binding.financesNewEntryAmountInput.text.toString().toDouble()-binding.financesNewEntryAmountDeniseInput.text.toString().toDouble()))
                    }
                }
            }
        }
        return false
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