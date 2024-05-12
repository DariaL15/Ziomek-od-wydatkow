package com.example.budgetbuddy
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.handleCoroutineException
import android.content.Context
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FirebaseRepository (private val context: Context){
    private val db = FirebaseFirestore.getInstance()


    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    fun getBudget(onSuccess: (Double) -> Unit, onFailure: (Exception) -> Unit)
    {
        if(userId != null)
        {
            val budgetRef = db.collection(userId).document("budget")
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
            val budgetRef = db.collection(userId).document("budget")
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

    fun getExpensesVal(onSuccess: (Double) -> Unit, onFailure: (Exception) -> Unit)
    {
        if(userId != null)
        {
            val budgetRef = db.collection(userId).document("budget")
            budgetRef.get()
                .addOnSuccessListener { document ->
                    if(document != null && document.exists() )
                    {
                        val budget = document.getDouble("expensesV") ?: 0.0
                        onSuccess(budget)
                    }
                    else
                    {
                        onFailure(Exception("Nie znaleziono danych o wydatkach użytkownika"))
                    }
                }
                .addOnFailureListener {
                    onFailure(Exception("Błąd pobierania wydatkow"))
                }
        }
        else {
            onFailure(Exception("Użytkownik niezalogowany"))
        }
    }

    fun getName(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit)
    {
        if(userId != null)
        {
            val userRef = db.collection(userId).document("user")
            userRef.get()
                .addOnSuccessListener { document ->
                    if(document != null && document.exists() )
                    {
                        val name = document.getString("name") ?: "Imię"
                        onSuccess(name)
                    }
                    else
                    {
                        onFailure(Exception("Nie znaleziono danych z imieniem użytkownika"))
                    }
                }
                .addOnFailureListener {
                    onFailure(Exception("Błąd pobierania imienia"))
                }
        }
        else {
            onFailure(Exception("Użytkownik niezalogowany"))
        }
    }

    fun getSurname(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit)
    {
        if(userId != null)
        {
            val userRef = db.collection(userId).document("user")
            userRef.get()
                .addOnSuccessListener { document ->
                    if(document != null && document.exists() )
                    {
                        val surname = document.getString("surname") ?: "Nazwisko"
                        onSuccess(surname)
                    }
                    else
                    {
                        onFailure(Exception("Nie znaleziono danych z nazwiskiem użytkownika"))
                    }
                }
                .addOnFailureListener {
                    onFailure(Exception("Błąd pobierania nazwiska"))
                }
        }
        else {
            onFailure(Exception("Użytkownik niezalogowany"))
        }
    }


    fun getEmail(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit)
    {
        if(userId != null)
        {
            val userRef = db.collection(userId).document("user")
            userRef.get()
                .addOnSuccessListener { document ->
                    if(document != null && document.exists() )
                    {
                        val email = document.getString("email") ?: "email"
                        onSuccess(email)
                    }
                    else
                    {
                        onFailure(Exception("Nie znaleziono danych z emailem użytkownika"))
                    }
                }
                .addOnFailureListener {
                    onFailure(Exception("Błąd pobierania emaila"))
                }
        }
        else {
            onFailure(Exception("Użytkownik niezalogowany"))
        }
    }


    fun getTotalExpenseFromCategory(
        category: String,
        dateFrom: Date,
        dateEnd: Date,
        callback: (Double) -> Unit
    ) {
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        var totalAmountTemp = 0.0
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val budgetRef = db.collection(userId).document("budget").collection(category)

        budgetRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val expense = document.toObject(Model::class.java)
                    var date: Date? = null
                    val amount: Double? = expense.amount
                    try {
                        if (expense.date.isNotEmpty()) {
                            date = dateFormatter.parse(expense.date)
                        }
                    } catch (e: ParseException) {
                        Log.e("FirebaseRepository", "Błąd pobierania daty", e)
                        continue
                    }

                    if (date != null && date > dateFrom && date < dateEnd && amount != null) {
                        totalAmountTemp += if (amount < 0) -amount else amount
                    }
                }

                callback(totalAmountTemp)
            }
    }


}