package com.example.budgetbuddy

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FixedExpenseAdapter(private var fixedExpenseList: ArrayList<ModelReminderItem>) :
    RecyclerView.Adapter<FixedExpenseAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_payment_reminder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fixedExpense = fixedExpenseList[position]
        holder.bind(fixedExpense)
    }

    override fun getItemCount(): Int {

        return fixedExpenseList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.zlecenie_stale_text)
        private val amountTextView: TextView = itemView.findViewById(R.id.zlecenie_stale_amount)
        private val dateTextView: TextView = itemView.findViewById(R.id.zlecenie_stale_date)
        private val confirmCheck: TextView = itemView.findViewById(R.id.zlecenie_stale_checkBox)

        fun bind(fixedExpense: ModelReminderItem) {
            nameTextView.text = fixedExpense.name
            amountTextView.text = fixedExpense.amount.toString()
            dateTextView.text = fixedExpense.nextDate
            confirmCheck.text = if (fixedExpense.isChecked) "Confirmed" else "Not Confirmed"

            itemView.setOnClickListener {
                fixedExpense.isChecked = !fixedExpense.isChecked
                notifyItemChanged(adapterPosition)
            }
        }
    }

    fun updateList(newList: ArrayList<ModelReminderItem>) {
        fixedExpenseList.clear()
        fixedExpenseList.addAll(newList)

        notifyDataSetChanged()
    }

    fun getSelectedExpenses(): List<ModelReminderItem> {
        val selectedExpenses = mutableListOf<ModelReminderItem>()

        for (expense in fixedExpenseList) {
            if (expense.isChecked) {
                selectedExpenses.add(expense)
            }
        }
        return selectedExpenses
    }

}
