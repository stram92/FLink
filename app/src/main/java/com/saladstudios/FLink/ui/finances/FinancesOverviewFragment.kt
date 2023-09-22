package com.saladstudios.FLink.ui.finances

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saladstudios.FLink.R
import com.saladstudios.FLink.utility.json.readJsonFile

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

        val jsonArray = readJsonFile(financesView.context, R.raw.financesdata)

        for (i in 1 .. 3) {
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
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
        }

        val financesAdapter = FinancesEntryAdapter(financesData)

        financesRecyclerView.adapter = financesAdapter

        // Inflate the layout for this fragment
        return financesView
    }

}