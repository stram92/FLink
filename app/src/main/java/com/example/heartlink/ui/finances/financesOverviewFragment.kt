package com.example.heartlink.ui.finances

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.heartlink.R

class financesOverviewFragment : Fragment() {

    @Override
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val financesView = inflater.inflate(R.layout.fragment_finances_overview, container, false)

        val financesRecyclerView = financesView.findViewById<RecyclerView>(R.id.financesRecyclerView)

        financesRecyclerView.layoutManager = LinearLayoutManager(view?.context)
        financesRecyclerView.setHasFixedSize(true)

        val financesData = ArrayList<financesItemsViewModel>()

        for (i in 1 .. 20) {
            financesData.add (financesItemsViewModel("S","Kino",(10.0).toFloat(),"01.09","B",(10.0).toFloat()))
        }

        val financesAdapter = financesEntryAdapter(financesData)

        financesRecyclerView.adapter = financesAdapter

        // Inflate the layout for this fragment
        return financesView
    }

}