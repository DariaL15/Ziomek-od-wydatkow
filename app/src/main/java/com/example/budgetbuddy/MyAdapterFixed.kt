package com.example.budgetbuddy

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyAdapterFixed (private val dataList: ArrayList<ModelFixed>) : RecyclerView.Adapter<MyAdapterFixed.MyViewHolder>() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userId: String = FirebaseAuth.getInstance().currentUser!!.uid


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


        holder.imageView.setImageResource(getCategoryIcon(currentItem.category))

        holder.deleteImageView.setOnClickListener {

            val alertDialogBuilder= androidx.appcompat.app.AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Usunąć element")
            alertDialogBuilder.setMessage("Czy pewno chcesz usunąć ten element?")
            alertDialogBuilder.setPositiveButton("Tak") { dialogInterface: DialogInterface, i: Int ->

                deleteItemFromDatabase(currentItem.documentId!!)

                dataList.removeAt(position)
                notifyItemRemoved(position)
                dialogInterface.dismiss()
            }
            alertDialogBuilder.setNegativeButton("Nie") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
            alertDialogBuilder.show()
        }



        holder.editImageView.setOnClickListener {
            val fragment =EditFixedExpensesFragment()
            val args =Bundle()
            args.putString("documentId", currentItem.documentId)
            args.putString("name", currentItem.name)
            args.putDouble("amount", currentItem.amount ?: 0.0)
            args.putString("repeatFrequency", currentItem.repeatFrequency)
            args.putString("category", currentItem.category)
            args.putString("beginDate", currentItem.beginDate)
            args.putString("nextDate", currentItem.nextDate)
            args.putInt("amountOfTransfers", currentItem.amountOfTransfers ?: 0)
            args.putString("endDayOfTransfers", currentItem.endDayOfTransfers)
            args.putInt("amountOfTransfersTemp", currentItem.amountOfTransfersTemp?: 0)
            args.putString("whenStopFixedExpense", currentItem.whenStopFixedExpense)


            fragment.arguments = args

            val activity = holder.itemView.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()

        }

    }

    private fun deleteItemFromDatabase(documentId: String){
        db.collection(userId).document("fixedExpenses").collection("documents")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.w(TAG, "Element został usunięty")
            }
            .addOnFailureListener{e->
                Log.w(TAG, "Błąd w usunięciu elementa", e)
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
        val editImageView : ImageView = itemView.findViewById(R.id.edit)
        val deleteImageView : ImageView = itemView.findViewById(R.id.delete)
    }

    private fun getCategoryIcon(category: String): Int{
        return  when (category){
            "house"->R.drawable.icon_dom
            "car"->R.drawable.icon_auto
            "health"->R.drawable.icon_zdrowie
            "shopping"->R.drawable.icon_zakupy2
            "sport"->R.drawable.icon_sport
            "transport"->R.drawable.icon_transport
            "relax"->R.drawable.icon_wypoczynek
            "clothes"->R.drawable.icon_ubrania
            "entertainment"->R.drawable.icon_rozrywka
            "restaurant"->R.drawable.icon_restauracje
            "gift"->R.drawable.icon_prezent2
            "education"->R.drawable.icon_edukacja
            else -> R.drawable.circle
        }
    }
}






