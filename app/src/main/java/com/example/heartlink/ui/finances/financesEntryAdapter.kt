package com.example.heartlink.ui.finances


import com.example.heartlink.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class financesEntryAdapter(private val mList: List<financesItemsViewModel>) : RecyclerView.Adapter<financesEntryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.finances_entry_design, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        /*if (ItemsViewModel.payer.equals("B")) {
            holder.heart.setImageResource(ItemsViewModel.heart)
        } else {
            holder.payer.text = ItemsViewModel.payer
        }*/

        holder.payer.text = ItemsViewModel.payer
        holder.description.text = ItemsViewModel.description
        holder.payedForAmount.text = ItemsViewModel.payedForAmount.toString()
        holder.amount.text = ItemsViewModel.amount.toString()
        holder.entryDate.text = ItemsViewModel.entryDate
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val payer: TextView = itemView.findViewById(R.id.financesEntryPayer)
        val description: TextView = itemView.findViewById(R.id.financesEntryDescription)
        val payedForAmount: TextView = itemView.findViewById(R.id.financesEntryPayedForAmount)
        val amount: TextView = itemView.findViewById(R.id.financesEntryAmount)
        val entryDate: TextView = itemView.findViewById(R.id.financesEntryEffectiveDate)
    }
}
