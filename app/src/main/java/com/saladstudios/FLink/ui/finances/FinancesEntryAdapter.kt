package com.saladstudios.FLink.ui.finances


import android.app.ActionBar.LayoutParams
import android.content.ClipData.Item
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.saladstudios.FLink.R
import com.saladstudios.FLink.utility.format.prettyPrintNumberWithCurrency
import org.json.JSONArray
import java.util.ArrayList

class FinancesEntryAdapter(private var mList: MutableList<FinancesItemsViewModel>,
                           private val listener: (FinancesItemsViewModel) -> Unit) : RecyclerView.Adapter<FinancesEntryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.finances_entry_design, parent, false)

        val lp = RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.setMargins(0,20,0,0)
        view.layoutParams = lp

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemViewModel = mList[position]

        val res = holder.itemView.context.resources
        val scale = res.displayMetrics.density


        holder.payer.text = itemViewModel.payer
        holder.description.text = itemViewModel.description
        holder.payedForAmount.text = itemViewModel.payedForAmount
        holder.amount.text = itemViewModel.payedForAmount
        holder.entryDate.text = itemViewModel.entryDate

        holder.framePayer.visibility = View.VISIBLE
        holder.payer.visibility = View.VISIBLE
        holder.payedForAmount.visibility = View.VISIBLE
        holder.financesBoth.visibility = View.VISIBLE
        holder.financesAmountLayout.visibility = View.VISIBLE

        //Calculation of DP using display scale
        holder.financesEntryText.layoutParams.width = ((200*scale + 0.5f).toInt())

        if (holder.payer.text.equals("D")) {
            holder.framePayer.background = res.getDrawable(R.drawable.frame_denise)
            holder.financesFrame.background = res.getDrawable(R.drawable.frame_denise)
            holder.financesBoth.visibility = View.GONE
        } else if (holder.payer.text.equals("S")) {
            holder.framePayer.background = res.getDrawable(R.drawable.frame_sascha)
            holder.financesFrame.background = res.getDrawable(R.drawable.frame_sascha)
            holder.financesBoth.visibility = View.GONE
        } else if (holder.payer.text.equals("CASHUP")) {
            holder.financesFrame.background = res.getDrawable(R.drawable.frame_cashup)
            holder.financesAmountLayout
            holder.framePayer.visibility = View.GONE
            holder.payedForAmount.visibility = View.GONE
            holder.financesAmountLayout.visibility = View.GONE
            holder.financesEntryText.layoutParams.width = ((105*scale + 0.5f).toInt())
        }
        else {
            holder.framePayer.background = res.getDrawable(R.drawable.frame_both)
            holder.financesFrame.background = res.getDrawable(R.drawable.frame_both)
            holder.payer.text = null
            holder.payer.visibility = View.GONE
        }

        if (holder.payedForAmount.text.isEmpty()) {
            holder.payedForAmount.visibility = View.GONE
        }

        if (itemViewModel.sign == "-") {
            holder.payedForAmount.setTextColor(Color.RED)
        } else {
            holder.payedForAmount.setTextColor(Color.GREEN)
        }

        holder.bind(itemViewModel)

        if (!holder.payer.text.equals("CASHUP")) {
            holder.itemView.setOnClickListener { listener(itemViewModel) }
        } else {
            holder.itemView.setOnClickListener { null }
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
        val financesAmountLayout: ConstraintLayout = itemView.findViewById(R.id.financesAmountLayout)
        val financesEntryText: ConstraintLayout = itemView.findViewById(R.id.financesEntryText)

        fun bind (item: FinancesItemsViewModel) {
            payer.text = item.payer
            description.text = item.description

            if (item.payedForAmount!="") {
                if (item.payedFor == "S") {
                    payedForAmount.text =
                        "Sascha: " + item.sign + " " + item.payedForAmount?.let { prettyPrintNumberWithCurrency(it) }
                } else if (item.payedFor == "D") {
                    payedForAmount.text =
                        "Denise: " + item.sign + " " + item.payedForAmount?.let { prettyPrintNumberWithCurrency(it) }
                }
            }
            amount.text = item.sign + " " + prettyPrintNumberWithCurrency(item.amount)
            entryDate.text = item.entryDate
        }

    }

    fun addItems(newItems: JSONArray) {
        val newData = ArrayList<FinancesItemsViewModel>()

        for (i in newItems.length()-1 downTo 0)  {
            val jsonObject = newItems.getJSONObject(i)

            mList.add(FinancesItemsViewModel(jsonObject.getString("id"),
                jsonObject.getString("payer"),
                jsonObject.getString("description"),
                jsonObject.getString("sign"),
                jsonObject.getString("amount"),
                jsonObject.getString("entryDate"),
                jsonObject.optString("payedFor"),
                jsonObject.optString("payedForAmount"),
                jsonObject.optString("category","…")))
        }

        notifyDataSetChanged()
    }
}
