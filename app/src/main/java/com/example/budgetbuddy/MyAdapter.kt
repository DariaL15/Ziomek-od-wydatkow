package com.example.budgetbuddy

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.api.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyAdapter(private val dataList: ArrayList<Model>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userId: String = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.singlerow, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem =dataList[position]
        val formattedAmount  = "%.2f zł".format(currentItem.amount ?:0.0)

        holder.t1.text = currentItem.notes.split(" ")[0]
        holder.t2.text = currentItem.date
        holder.t3.text=formattedAmount


        holder.imageView.setImageResource(getCategoryIcon(currentItem.collection))


        holder.deleteImageView.setOnClickListener {

            val alertDialogBuilder= androidx.appcompat.app.AlertDialog.Builder(holder.itemView.context)
            alertDialogBuilder.setTitle("Usunąć element")
            alertDialogBuilder.setMessage("Czy pewno chcesz usunąć ten element?")
            alertDialogBuilder.setPositiveButton("Tak") { dialogInterface: DialogInterface, i: Int ->


                val budgetRef = db.collection(userId).document("budget")
                budgetRef.get().addOnSuccessListener { document->
                    val currentBudget = document.getDouble("budgetV")?:0.0
                    val currentExpenses = document.getDouble("expensesV") ?: 0.0
                    val updatedBudget : Double
                    val updatedExpenses: Double
                    if (currentItem.amount!=null && currentItem.amount!!<0){
                         updatedBudget = currentBudget + currentItem.amount!!
                         updatedExpenses = currentExpenses - currentItem.amount!!
                    }
                    else{
                         updatedBudget = currentBudget - currentItem.amount!!
                        updatedExpenses = currentExpenses + currentItem.amount!!
                    }

                    budgetRef.update(
                        mapOf(
                            "budgetV" to updatedBudget,
                            "expensesV" to updatedExpenses
                        )
                    ). addOnSuccessListener {
                        Log.w(TAG, "Budżet został zaktualizowany")
                    }
                }
                deleteItemFromDatabase(currentItem.documentId!!, currentItem.collection)
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
            val fragment: Fragment = if(currentItem.amount!=null && currentItem.amount!!<0){
                EditExpensesFragment()
            } else{
                EditIncomesFragment()
            }
            val args = Bundle()
            args.putString("documentId", currentItem.documentId)
            args.putString("notes", currentItem.notes)
            args.putDouble("amount", currentItem.amount ?: 0.0)
            args.putString("date", currentItem.date)
            args.putString("collection", currentItem.collection)
            fragment.arguments = args

            val activity = holder.itemView.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()

        }
        if (currentItem.amount !=null && currentItem.amount!! <0){
            holder.t3.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
        }

    }


    override fun getItemCount(): Int {
        return dataList.size
    }


    private fun deleteItemFromDatabase(documentId: String, itemCategory: String){
        db.collection(userId).document("budget").collection(itemCategory)
            .document(documentId)
            .delete()
            .addOnSuccessListener {
               Log.w(TAG, "Element został usunięty")
            }
            .addOnFailureListener{e->
                Log.w(TAG, "Błąd w usunięciu elementa", e)
            }
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