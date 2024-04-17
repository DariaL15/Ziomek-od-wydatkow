package com.example.budgetbuddy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.api.Context

class MyAdapter(private val dataList: ArrayList<Model>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.singlerow, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem =dataList[position]
        holder.t1.text = currentItem.notes
        holder.t2.text = currentItem.date
        holder.t3.text=currentItem.amount.toString()

        if (currentItem.amount !=null && currentItem.amount!! <0){
            holder.t3.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
        }
        when (currentItem.collection){
            "house"->holder.imageView.setImageResource(R.drawable.icon_dom)
            "car"->holder.imageView.setImageResource(R.drawable.icon_auto)
            "health"->holder.imageView.setImageResource(R.drawable.icon_zdrowie)
            "shopping"->holder.imageView.setImageResource(R.drawable.icon_zakupy)
            "sport"->holder.imageView.setImageResource(R.drawable.icon_sport)
            "transport"->holder.imageView.setImageResource(R.drawable.icon_transport)
            "relax"->holder.imageView.setImageResource(R.drawable.icon_wypoczynek)
            "clothes"->holder.imageView.setImageResource(R.drawable.icon_ubrania)
            "entertainment"->holder.imageView.setImageResource(R.drawable.icon_rozrywka)
            else -> holder.imageView.setImageResource(R.drawable.circle)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val t1: TextView = itemView.findViewById(R.id.t1)
        val t2: TextView = itemView.findViewById(R.id.t2)
        val t3: TextView = itemView.findViewById(R.id.t3)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)

    }
}