package com.saladstudios.FLink.ui.finances

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saladstudios.FLink.R
import com.saladstudios.FLink.utility.json.addJsonEntryLocal
import com.saladstudios.FLink.utility.json.readJsonFileLocal
import com.saladstudios.FLink.utility.json.removeJsonEntryLocal
import com.saladstudios.FLink.utility.json.wipeJsonEntriesLocal

class FinancesOverviewFragment : Fragment() {

    @Override
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val financesView = inflater.inflate(R.layout.fragment_finances_overview, container, false)

        val financesRecyclerView = financesView.findViewById<RecyclerView>(R.id.financesRecyclerView)

        financesRecyclerView.layoutManager = LinearLayoutManager(view?.context)
        financesRecyclerView.setHasFixedSize(true)

        val financesData = ArrayList<FinancesItemsViewModel>()

        wipeJsonEntriesLocal(financesView.context)

        addJsonEntryLocal(financesView.context,"S","Kino","- 15,00 €","01.12.2023","B","Denise: - 7,50 €")
        addJsonEntryLocal(financesView.context,"D","Einkaufen","- 142,12 €","01.08.2023","B","Sascha: - 71,06 €")
        addJsonEntryLocal(financesView.context,"D","Pfeile","- 300,00 €","01.09.2023","D","")
        addJsonEntryLocal(financesView.context,"D","Gehalt","+ 3.000,00 €","02.09.2023","D","Denise: + 3.000 €")
        addJsonEntryLocal(financesView.context,"B","Kredit","- 2.000,00 €","01.09.2023","B","")
        addJsonEntryLocal(financesView.context,"S","Lachgummi","- 3,00 €","22.09.2023","S","")

        val jsonArray = readJsonFileLocal(financesView.context)

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

        val financesAdapter = FinancesEntryAdapter(financesData)

        financesRecyclerView.adapter = financesAdapter

        // Inflate the layout for this fragment
        return financesView
    }

}