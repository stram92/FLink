package com.saladstudios.FLink.ui.finances


import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saladstudios.FLink.R

class FinancesEntryAdapter(private val mList: List<FinancesItemsViewModel>) : RecyclerView.Adapter<FinancesEntryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.finances_entry_design, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemViewModel = mList[position]
        val res = holder.itemView.context.resources

        holder.payer.text = ItemViewModel.payer
        holder.description.text = ItemViewModel.description
        holder.payedForAmount.text = ItemViewModel.payedForAmount
        holder.amount.text = ItemViewModel.amount
        holder.entryDate.text = ItemViewModel.entryDate

        if (holder.payer.text.equals("D")) {
            holder.framePayer.background = res.getDrawable(R.drawable.frame_denise)
            holder.financesFrame.background = res.getDrawable(R.drawable.frame_denise)
            holder.payer.visibility = View.VISIBLE
            holder.financesBoth.visibility = View.GONE
        } else if (holder.payer.text.equals("S")) {
            holder.framePayer.background = res.getDrawable(R.drawable.frame_sascha)
            holder.financesFrame.background = res.getDrawable(R.drawable.frame_sascha)
            holder.payer.visibility = View.VISIBLE
            holder.financesBoth.visibility = View.GONE
        } else {
            holder.framePayer.background = res.getDrawable(R.drawable.frame_both)
            holder.financesFrame.background = res.getDrawable(R.drawable.frame_both)
            holder.payer.text = null
            holder.payer.visibility = View.GONE
            holder.financesBoth.visibility = View.VISIBLE
        }

        if (holder.payedForAmount.text.isEmpty()) {
            holder.payedForAmount.visibility = View.GONE
        } else if (holder.payedForAmount.text.contains("-")) {
            holder.payedForAmount.visibility = View.VISIBLE
            holder.payedForAmount.setTextColor(Color.RED)
        } else {
            holder.payedForAmount.visibility = View.VISIBLE
            holder.payedForAmount.setTextColor(Color.GREEN)
        }


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
        val framePayer: FrameLayout = itemView.findViewById(R.id.financesEntryIcon)
        val financesFrame: FrameLayout = itemView.findViewById(R.id.financesFrame)
        val financesBoth: ImageView = itemView.findViewById(R.id.financesBoth)
    }
}
