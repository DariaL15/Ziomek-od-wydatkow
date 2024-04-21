package com.example.budgetbuddy

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyAdapterFixed (private val dataList: ArrayList<ModelFixed>) : RecyclerView.Adapter<MyAdapterFixed.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.singlerow_zlecenie, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem =dataList[position]
        val formattedAmount  = "%.2f zł".format(currentItem.amount ?:0.0)

        holder.t1.text = currentItem.name
        holder.t2.text = currentItem.repeatFrequency
        holder.t3.text=formattedAmount


        when (currentItem.category){
            "house"->holder.imageView.setImageResource(R.drawable.icon_dom)
            "car"->holder.imageView.setImageResource(R.drawable.icon_auto)
            "health"->holder.imageView.setImageResource(R.drawable.icon_zdrowie)
            "shopping"->holder.imageView.setImageResource(R.drawable.icon_zakupy2)
            "sport"->holder.imageView.setImageResource(R.drawable.icon_sport)
            "transport"->holder.imageView.setImageResource(R.drawable.icon_transport)
            "relax"->holder.imageView.setImageResource(R.drawable.icon_wypoczynek)
            "clothes"->holder.imageView.setImageResource(R.drawable.icon_ubrania)
            "entertainment"->holder.imageView.setImageResource(R.drawable.icon_rozrywka)
            "restaurant"->holder.imageView.setImageResource(R.drawable.icon_restauracje)
            "gift"->holder.imageView.setImageResource(R.drawable.icon_prezent2)
            "education"->holder.imageView.setImageResource(R.drawable.icon_edukacja)
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