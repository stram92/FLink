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
import com.google.firebase.storage.ktx.storage
import com.saladstudios.FLink.R
import com.saladstudios.FLink.databinding.FragmentFinancesOverviewBinding
import com.saladstudios.FLink.utility.format.prettyPrintNumberWithCurrency
import com.saladstudios.FLink.utility.json.*
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class FinancesOverviewFragment : Fragment() {
    private lateinit var binding: FragmentFinancesOverviewBinding
    private lateinit var financesIntent: Intent

    private lateinit var entryAdapter: FinancesEntryAdapter

    private var LAUNCH_NEW_ENTRY = 1
    private var LAUNCH_EDIT_ENTRY = 2

    private val family = "development"
    //private val family = "FLink"
    private val module = "finances"

    private val database = Firebase.database("https://flink-3c91d-default-rtdb.europe-west1.firebasedatabase.app/")
    private val storage = Firebase.storage("gs://flink-3c91d.appspot.com/")

    private val flatBase = database.getReference("$family/$module/entries")
    private var financeEntries: JSONArray = JSONArray()

    private var financesHistoryStorage = storage.reference

    private var loadedArchive = ""
    private var loadedArchiveNumber = 0
    private var downloadFinished = true
    private var files = mutableListOf<String>()
    private val archiveFileBase = financesHistoryStorage.child("$family/$module/entries/")

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

        val linearLayoutManager = LinearLayoutManager(view.context)
        financesRecyclerView.layoutManager = linearLayoutManager
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
                financeEntries = sortJsonArray(financeEntries)

                entryAdapter = FinancesEntryAdapter(refreshFinances()) { item ->
                    financesEdit(
                        view.context,
                        item
                    )
                }

                financesRecyclerView.adapter=entryAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FLinkWarning", "loadPost: onCancelled", error.toException())
            }
        }

        flatBase.addValueEventListener(postListener)
        entryAdapter = FinancesEntryAdapter(refreshFinances()) { item ->
            financesEdit(
                view.context,
                item
            )
        }

        financesRecyclerView.adapter = entryAdapter

        financesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (downloadFinished) {
                    if (linearLayoutManager.findLastVisibleItemPosition() == entryAdapter.itemCount-1) {

                        downloadFinished = false

                        if (loadedArchive == "") {
                            archiveFileBase.listAll().addOnSuccessListener { it ->
                                for (item in it.items) {
                                    files.add(item.toString().substringAfter("entries/"))
                                }

                                files.sortDescending()
                                loadedArchive=files[loadedArchiveNumber]

                                val downloadArchive = financesHistoryStorage.child("$family/$module/entries/$loadedArchive")

                                downloadArchive.getBytes(1024*1024).addOnSuccessListener { it1 ->
                                    val jsonString = String(it1,StandardCharsets.UTF_8)
                                    val jsonArray = getJsonArray(jsonString)

                                    if (jsonArray != null) {
                                        entryAdapter.addItems(jsonArray)
                                    }

                                    downloadFinished = true
                                }
                            }
                        } else {
                            if (loadedArchiveNumber+1 < files.size) {
                                loadedArchiveNumber++
                                loadedArchive=files[loadedArchiveNumber]

                                val downloadArchive = financesHistoryStorage.child("$family/$module/entries/$loadedArchive")

                                downloadArchive.getBytes(1024*1024).addOnSuccessListener {
                                    val jsonString = String(it,StandardCharsets.UTF_8)
                                    val jsonArray = getJsonArray(jsonString)

                                    if (jsonArray != null) {
                                        entryAdapter.addItems(jsonArray)
                                    }

                                    downloadFinished = true
                                }
                            }
                        }
                    }
                }
            }
        })

        binding.buttonFinancesAdd.setOnClickListener { financesAdd(view.context) }
        binding.buttonFinancesCashUp.setOnClickListener { financesCashUp(view.context) }

    }

    private fun refreshFinances () : ArrayList<FinancesItemsViewModel>{
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
        } else {
            binding.textFinancesDebtor.text = ""
            binding.textFinancesDebts.text = prettyPrintNumberWithCurrency("0.00")
        }
        binding.textFinancesDebts.setAutoSizeTextTypeUniformWithConfiguration(1,24,1,TypedValue.COMPLEX_UNIT_DIP)
        binding.textFinancesDebtor.setAutoSizeTextTypeUniformWithConfiguration(1,24,1,TypedValue.COMPLEX_UNIT_DIP)

        return financesData
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

        entryAdapter = FinancesEntryAdapter(refreshFinances()) { item ->
            financesEdit(
                binding.root.context,
                item
            )
        }
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
        val entryDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        var jsonArray = JSONArray()

        var cashUpArrays = mutableListOf<JSONArray>()
        var months = mutableListOf<String>()

        //cashUpFinanceData.putBytes(financeEntries.toString().toByteArray())

        for (i in financeEntries.length()-1 downTo 0) {
            val jsonObject = financeEntries.getJSONObject(i)
            val date = LocalDate.parse(jsonObject.getString("entryDate"),entryDateFormat)

            if (!months.contains(date.year.toString()+date.monthValue.toString())) {
                months.add(date.year.toString()+date.monthValue.toString().padStart(2,'0'))

                if (i != financeEntries.length()-1) {
                    cashUpArrays.add(jsonArray)
                }

                jsonArray = JSONArray()
            }

            jsonArray.put(jsonObject)
        }

        cashUpArrays.add(jsonArray)

        for (i in 0 until months.size) {
            val month = months[i]
            val cashUpFinanceData = financesHistoryStorage.child("$family/$module/entries/$month.json")

            cashUpFinanceData.getBytes(1024*1024).addOnSuccessListener {
                val jsonString = String(it,StandardCharsets.UTF_8)
                var jsonArray = getJsonArray(jsonString)

                jsonArray = jsonArray?.let { it1 -> mergeJsonArrays(it1,cashUpArrays[i]) }

                cashUpFinanceData.delete().addOnSuccessListener {
                    if (jsonArray!=null) {
                        cashUpFinanceData.putBytes(jsonArray.toString().toByteArray())

                        for (i in jsonArray!!.length()-1 downTo 0) {
                            val jsonObject = jsonArray!!.getJSONObject(i)
                            flatBase.child(jsonObject.getString("id")).removeValue()
                        }
                    }
                }.addOnFailureListener {
                    AlertDialog.Builder(context)
                        .setTitle(R.string.cash_up_error_title)
                        .setMessage((R.string.cash_up_error_message).toString() + month)
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton(R.string.ok,null)
                        .show()
                }
            }.addOnFailureListener {
                if (it.toString() == "com.google.firebase.storage.StorageException: Object does not exist at location.") {
                    cashUpFinanceData.putBytes(cashUpArrays[i].toString().toByteArray())
                } else {
                    AlertDialog.Builder(context)
                        .setTitle(R.string.cash_up_error_title)
                        .setMessage((R.string.cash_up_error_message).toString() + month)
                        .setIcon(android.R.drawable.ic_delete)
                        .setPositiveButton(R.string.ok,null)
                        .show()
                }
            }

        }

        AlertDialog.Builder(context)
            .setTitle(R.string.cash_up_notification_title)
            .setMessage(R.string.cash_up_notification_done)
            .setIcon(android.R.drawable.checkbox_on_background)
            .setPositiveButton(R.string.ok,null)
            .show()
    }
}