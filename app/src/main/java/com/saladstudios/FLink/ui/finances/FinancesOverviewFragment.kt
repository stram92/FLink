package com.saladstudios.FLink.ui.finances

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saladstudios.FLink.R
import com.saladstudios.FLink.databinding.FragmentFinancesOverviewBinding
import com.saladstudios.FLink.utility.json.addJsonEntryLocal
import com.saladstudios.FLink.utility.json.readJsonFileLocal

class FinancesOverviewFragment : Fragment() {
    private lateinit var binding: FragmentFinancesOverviewBinding
    private lateinit var financesNewIntent: Intent

    private var LAUNCH_NEW_ENTRY = 1

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

/*
        wipeJsonEntriesLocal(view.context)


        addJsonEntryLocal(view.context,"S","Kino","- 15,00 €","01.12.2023","B","Denise: - 7,50 €")
        addJsonEntryLocal(view.context,"D","Einkaufen","- 142,12 €","01.08.2023","B","Sascha: - 71,06 €")
        addJsonEntryLocal(view.context,"D","Pfeile","- 300,00 €","01.09.2023","D","")
        addJsonEntryLocal(view.context,"D","Gehalt","+ 3.000,00 €","02.09.2023","D","Denise: + 3.000 €")
        addJsonEntryLocal(view.context,"B","Kredit","- 2.000,00 €","01.09.2023","B","")
        addJsonEntryLocal(view.context,"S","Lachgummi","- 3,00 €","22.09.2023","S","")
 */


        financesRecyclerView.adapter = refreshFinances (view.context)


        binding.buttonFinancesAdd.setOnClickListener { financesAdd(view.context) }

    }

    private fun refreshFinances (context: Context) : FinancesEntryAdapter{
        val financesData = ArrayList<FinancesItemsViewModel>()
        val jsonArray = readJsonFileLocal(context)

        if (jsonArray != null) {
            for (i in jsonArray.length()-1 downTo 0)  {
                val jsonObject = jsonArray.getJSONObject(i)
                financesData.add(
                    FinancesItemsViewModel(
                        jsonObject.getString("payer"),
                        jsonObject.getString("description"),
                        jsonObject.getString("amount"),
                        jsonObject.getString("entryDate"),
                        jsonObject.getString("payedFor"),
                        jsonObject.getString("payedForAmount")
                    )
                )
            }
        }
        return FinancesEntryAdapter(financesData) { financesAdd(context) }
    }

    private fun financesAdd(context: Context) {
        financesNewIntent = Intent(context, FinancesNewEntry::class.java)
        startActivityForResult(financesNewIntent,LAUNCH_NEW_ENTRY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==LAUNCH_NEW_ENTRY) {
            if (resultCode== Activity.RESULT_OK){
                    addJsonEntryLocal(requireContext(),
                        data!!.getStringExtra("payer").toString(),
                        data.getStringExtra("description").toString(),
                        data.getStringExtra("amount").toString(),
                        data.getStringExtra("entryDate").toString(),
                        data.getStringExtra("payedFor").toString(),
                        data.getStringExtra("payedForAmount").toString()
                    )

            }
        }

        financesRecyclerView.adapter = refreshFinances(binding.root.context)
    }
}