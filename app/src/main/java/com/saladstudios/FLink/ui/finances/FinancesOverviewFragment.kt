package com.saladstudios.FLink.ui.finances

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.saladstudios.FLink.R
import com.saladstudios.FLink.databinding.FragmentFinancesOverviewBinding
import com.saladstudios.FLink.utility.format.prettyPrintNumberWithCurrency
import com.saladstudios.FLink.utility.json.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.EmptyMap.entries

class FinancesOverviewFragment : Fragment() {
    private lateinit var binding: FragmentFinancesOverviewBinding
    private lateinit var financesIntent: Intent

    private var LAUNCH_NEW_ENTRY = 1
    private var LAUNCH_EDIT_ENTRY = 2

    private val family = "development"
    private val module = "finances"

    private val database = Firebase.database("https://flink-3c91d-default-rtdb.europe-west1.firebasedatabase.app/")
    private val flatBase = database.getReference("$family/$module/entries")
    private var financeEntries: JSONArray = JSONArray()

    private lateinit var financesRecyclerView: RecyclerView

    @Override
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Inflate the layout XML file and return a binding object instance
        binding = FragmentFinancesOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        financesRecyclerView = view.findViewById(R.id.financesRecyclerView)

        financesRecyclerView.layoutManager = LinearLayoutManager(view.context)
        financesRecyclerView.setHasFixedSize(true)

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                financeEntries = JSONArray()
                for (financeEntry in snapshot.children) {
                    val newEntry = JSONObject()
                    newEntry.put("id", financeEntry.key.toString())
                    for (values in financeEntry.children) {
                        newEntry.put(values.key.toString(), values.value.toString())
                    }
                    financeEntries.put(newEntry)
                }
                financeEntries = sortJsonObject(financeEntries)
                financesRecyclerView.adapter=refreshFinances(view.context)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FLinkWarning", "loadPost: onCancelled", error.toException())
            }
        }

        flatBase.addValueEventListener(postListener)

        financesRecyclerView.adapter = refreshFinances (view.context)

        binding.buttonFinancesAdd.setOnClickListener { financesAdd(view.context) }
        binding.buttonFinancesCashUp.setOnClickListener { financesCashUp(view.context) }

    }

    private fun refreshFinances (context: Context) : FinancesEntryAdapter{
        val financesData = ArrayList<FinancesItemsViewModel>()
        var payedAmount = 0.00

        for (i in financeEntries.length()-1 downTo 0)  {
            val jsonObject = financeEntries.getJSONObject(i)
            financesData.add(FinancesItemsViewModel(jsonObject.getString("id"),
                    jsonObject.getString("payer"),
                    jsonObject.getString("description"),
                    jsonObject.getString("sign"),
                    jsonObject.getString("amount"),
                    jsonObject.getString("entryDate"),
                    jsonObject.getString("payedFor"),
                    jsonObject.getString("payedForAmount"),
                    jsonObject.optString("category",getString(R.string.not_available))))

            if (jsonObject.getString("payedFor")!="") {
                if (jsonObject.getString("payedFor")=="S") {
                    if (jsonObject.getString("sign")=="-") {
                        payedAmount += jsonObject.getString("payedForAmount").toDouble()
                    } else {
                        payedAmount -= jsonObject.getString("payedForAmount").toDouble()
                    }
                } else if (jsonObject.getString("payedFor")=="D"){
                    if (jsonObject.getString("sign")=="-") {
                        payedAmount -= jsonObject.getString("payedForAmount").toDouble()
                    } else {
                        payedAmount += jsonObject.getString("payedForAmount").toDouble()
                    }
                }
            }
        }

        if (payedAmount<0) {
            binding.textFinancesDebtor.text = getString(R.string.denise_colon)
            binding.textFinancesDebts.text = prettyPrintNumberWithCurrency(payedAmount.toString())
        } else if (payedAmount>0) {
            binding.textFinancesDebtor.text = getString(R.string.sascha_colon)
            payedAmount *= -1
            binding.textFinancesDebts.text = prettyPrintNumberWithCurrency(payedAmount.toString())
        }
        binding.textFinancesDebts.setAutoSizeTextTypeUniformWithConfiguration(1,24,1,TypedValue.COMPLEX_UNIT_DIP)
        binding.textFinancesDebtor.setAutoSizeTextTypeUniformWithConfiguration(1,24,1,TypedValue.COMPLEX_UNIT_DIP)

        return FinancesEntryAdapter(financesData) { item -> financesEdit(context,item) }
    }

    private fun financesAdd(context: Context) {
        financesIntent = Intent(context, FinancesNewEntry::class.java)
        startActivityForResult(financesIntent,LAUNCH_NEW_ENTRY)
    }

    private fun financesEdit(context: Context, item: FinancesItemsViewModel) {
        financesIntent = Intent(context,FinancesNewEntry::class.java)
        financesIntent.putExtra("id",item.id)
        financesIntent.putExtra("payer", item.payer)
        financesIntent.putExtra("description",item.description)
        financesIntent.putExtra("sign",item.sign)
        financesIntent.putExtra("amount",item.amount)
        financesIntent.putExtra("entryDate",item.entryDate)
        financesIntent.putExtra("payedFor",item.payedFor)
        financesIntent.putExtra("payedForAmount",item.payedForAmount)
        financesIntent.putExtra("category",item.category)
        startActivityForResult(financesIntent,LAUNCH_EDIT_ENTRY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK){
            if (requestCode==LAUNCH_NEW_ENTRY) {
                if (data!!.getStringExtra("payer")!=null) {
                    flatBase.push().setValue(FinancesItemsViewModel(null,
                        data!!.getStringExtra("payer").toString(),
                        data.getStringExtra("description").toString(),
                        data.getStringExtra("sign").toString(),
                        data.getStringExtra("amount").toString(),
                        data.getStringExtra("entryDate").toString(),
                        data.getStringExtra("payedFor").toString(),
                        data.getStringExtra("payedForAmount").toString(),
                        data.getStringExtra("category").toString()))
                }
            } else if (requestCode==LAUNCH_EDIT_ENTRY) {
                data!!.getStringExtra("id")?.let { flatBase.child(it).removeValue() }

                if (data.getStringExtra("payer") != null) {
                    flatBase.push().setValue(FinancesItemsViewModel(null,data!!.getStringExtra("payer").toString(),
                        data.getStringExtra("description").toString(),
                        data.getStringExtra("sign").toString(),
                        data.getStringExtra("amount").toString(),
                        data.getStringExtra("entryDate").toString(),
                        data.getStringExtra("payedFor").toString(),
                        data.getStringExtra("payedForAmount").toString(),
                        data.getStringExtra("category").toString()))
                }
            }
        }

        financesRecyclerView.adapter = refreshFinances(binding.root.context)
    }

    private fun financesCashUp (context: Context) {
        AlertDialog.Builder(context)
            .setTitle(R.string.cash_up_alert_title)
            .setMessage(R.string.cash_up_alert_message)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setNegativeButton(R.string.no,null)
            .setPositiveButton(R.string.yes) { _, _ ->
                financesDoCashUp(context)
            }
            .show()
    }

    private fun financesDoCashUp(context: Context) {
        var financesHistoryStorage = storage.reference

        AlertDialog.Builder(context)
            .setTitle(R.string.cash_up_notification_title)
            .setMessage(R.string.cash_up_notification_done)
            .setIcon(android.R.drawable.checkbox_on_background)
            .setPositiveButton(R.string.ok,null)
            .show()
    }
}