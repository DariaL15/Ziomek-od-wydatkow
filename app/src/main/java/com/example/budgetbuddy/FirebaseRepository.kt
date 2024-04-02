package com.example.budgetbuddy
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.handleCoroutineException
import android.content.Context

class FirebaseRepository (private val context: Context){
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    fun getBudget(onSuccess: (Double) -> Unit, onFailure: (Exception) -> Unit)
    {
        if(userId != null)
        {
            val budgetRef = db.collection("budget").document(userId)
            budgetRef.get()
                .addOnSuccessListener { document ->
                    if(document != null && document.exists() )
                    {
                        val budget = document.getDouble("budgetV") ?: 0.0
                        onSuccess(budget)
                    }
                    else
                    {
                        onFailure(Exception("Nie znaleziono danych budżetu użytkownika"))
                    }
                }
                .addOnFailureListener {
                    onFailure(Exception("Błąd pobierania budżetu"))
                }
        }
        else {
            onFailure(Exception("Użytkownik niezalogowany"))
        }
    }

    fun getSavings(onSuccess: (Double) -> Unit, onFailure: (Exception) -> Unit)
    {
        if(userId != null)
        {
            val budgetRef = db.collection("budget").document(userId)
            budgetRef.get()
                .addOnSuccessListener { document ->
                    if(document != null && document.exists() )
                    {
                        val budget = document.getDouble("savingsV") ?: 0.0
                        onSuccess(budget)
                    }
                    else
                    {
                        onFailure(Exception("Nie znaleziono danych o oszczędnościach użytkownika"))
                    }
                }
                .addOnFailureListener {
                    onFailure(Exception("Błąd pobierania oszczędności"))
                }
        }
        else {
            onFailure(Exception("Użytkownik niezalogowany"))
        }
    }

}