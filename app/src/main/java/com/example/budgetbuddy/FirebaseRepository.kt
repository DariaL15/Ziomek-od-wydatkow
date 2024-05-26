package com.example.budgetbuddy
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.handleCoroutineException
import android.content.Context
import android.util.Log
import androidx.compose.ui.text.substring
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FirebaseRepository (private val context: Context) {
    private val db = FirebaseFirestore.getInstance()


    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    fun getBudget(onSuccess: (Double) -> Unit, onFailure: (Exception) -> Unit) {
        if (userId != null) {
            val budgetRef = db.collection(userId).document("budget")
            budgetRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val budget = document.getDouble("budgetV") ?: 0.0
                        onSuccess(budget)
                    } else {
                        onFailure(Exception("Nie znaleziono danych budżetu użytkownika"))
                    }
                }
                .addOnFailureListener {
                    onFailure(Exception("Błąd pobierania budżetu"))
                }
        } else {
            onFailure(Exception("Użytkownik niezalogowany"))
        }
    }

    fun getSavings(onSuccess: (Double) -> Unit, onFailure: (Exception) -> Unit) {
        if (userId != null) {
            val budgetRef = db.collection(userId).document("budget")
            budgetRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val budget = document.getDouble("savingsV") ?: 0.0
                        onSuccess(budget)
                    } else {
                        onFailure(Exception("Nie znaleziono danych o oszczędnościach użytkownika"))
                    }
                }
                .addOnFailureListener {
                    onFailure(Exception("Błąd pobierania oszczędności"))
                }
        } else {
            onFailure(Exception("Użytkownik niezalogowany"))
        }
    }

    fun getExpensesVal(onSuccess: (Double) -> Unit, onFailure: (Exception) -> Unit) {
        if (userId != null) {
            val budgetRef = db.collection(userId).document("budget")
            budgetRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val budget = document.getDouble("expensesV") ?: 0.0
                        onSuccess(budget)
                    } else {
                        onFailure(Exception("Nie znaleziono danych o wydatkach użytkownika"))
                    }
                }
                .addOnFailureListener {
                    onFailure(Exception("Błąd pobierania wydatkow"))
                }
        } else {
            onFailure(Exception("Użytkownik niezalogowany"))
        }
    }

    fun getName(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        if (userId != null) {
            val userRef = db.collection(userId).document("user")
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: "Imię"
                        onSuccess(name)
                    } else {
                        onFailure(Exception("Nie znaleziono danych z imieniem użytkownika"))
                    }
                }
                .addOnFailureListener {
                    onFailure(Exception("Błąd pobierania imienia"))
                }
        } else {
            onFailure(Exception("Użytkownik niezalogowany"))
        }
    }

    fun getSurname(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        if (userId != null) {
            val userRef = db.collection(userId).document("user")
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val surname = document.getString("surename") ?: "Nazwisko"
                        onSuccess(surname)
                    } else {
                        onFailure(Exception("Nie znaleziono danych z nazwiskiem użytkownika"))
                    }
                }
                .addOnFailureListener {
                    onFailure(Exception("Błąd pobierania nazwiska"))
                }
        } else {
            onFailure(Exception("Użytkownik niezalogowany"))
        }
    }


    fun getEmail(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        if (userId != null) {
            val userRef = db.collection(userId).document("user")
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val email = document.getString("email") ?: "email"
                        onSuccess(email)
                    } else {
                        onFailure(Exception("Nie znaleziono danych z emailem użytkownika"))
                    }
                }
                .addOnFailureListener {
                    onFailure(Exception("Błąd pobierania emaila"))
                }
        } else {
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

                    if (date != null && date > dateFrom && date <= dateEnd && amount != null) {
                        totalAmountTemp += if (amount < 0.0) -amount else 0.0
                    }
                }

                callback(totalAmountTemp)
            }
    }


    fun getTotalIncomeFromCategory(
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

                    if (date != null && date > dateFrom && date <= dateEnd && amount != null) {
                        totalAmountTemp += if (amount > 0.0) amount else 0.0
                    }
                }
                Log.d("TotalExpense", "Całkowita kwota wydatków: $totalAmountTemp ")
                callback(totalAmountTemp)
            }
    }

    fun getTotalExpenseFromCategoryMonth(
        category: String,
        year: Int, month: Int,
        callback: (Double) -> Unit
    ) {
        var totalAmountTemp = 0.0
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val budgetRef = db.collection(userId).document("budget").collection(category)
        budgetRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val expense = document.toObject(Model::class.java)
                    val date: String = expense.date
                    val amount: Double? = expense.amount
                    date.let {
                        val docYear = it.substring(6, 10).toInt()
                        val docMonth = it.substring(3, 5).toInt()

                        if (docYear == year && docMonth == month && amount != null) {
                            totalAmountTemp += if (amount < 0) -amount else amount
                        }
                    }
                }
                callback(totalAmountTemp)
            }.addOnFailureListener { e ->
                callback(0.0)
            }
    }


    fun getExpenseMonth(
        category: String,
        year: Int,
        month: Int,
        onSuccess: (Double) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        var totalAmountTemp = 0.0
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val budgetRef = db.collection(userId).document("budget").collection(category)
            budgetRef.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val expense = document.toObject(Model::class.java)
                        val date: String = expense.date
                        val amount: Double? = expense.amount

                        val docYear = date.substring(6, 10).toInt()
                        val docMonth = date.substring(3, 5).toInt()

                        if (docYear == year && docMonth == month && amount!! < 0) {
                            totalAmountTemp += -amount
                        }

                    }
                    onSuccess(totalAmountTemp)
                }
                .addOnFailureListener {
                    onFailure(Exception("Błąd pobierania budżetu"))
                }
        } else {
            onFailure(Exception("Użytkownik niezalogowany"))
        }
    }


    fun getExpenseFromDates(date1: Date, date2: Date, callback: (Double) -> Unit) {
        var totalAmount = 0.0
        var totalCount = 0

        val categories = listOf(
            "car", "house", "clothes", "shopping", "transport", "sport",
            "health", "entertainment", "relax", "restaurant", "gift", "education"
        )

        for (category in categories) {
            getTotalExpenseFromCategory(category, date1, date2) { totalExpense ->
                totalAmount += totalExpense
                totalCount++

                if (totalCount == categories.size) {
                    Log.d("TotalExpense", "Całkowita kwota wydatków: $totalAmount")
                    callback(totalAmount)
                }
            }
        }
    }


    fun getIncomesFromDates(date1: Date, date2: Date, callback: (Double) -> Unit) {
        var totalAmount = 0.0
        var totalCount = 0

        val categories = listOf(
            "car", "house", "clothes", "shopping", "transport", "sport",
            "health", "entertainment", "relax", "restaurant", "gift", "education"
        )

        for (category in categories) {
            getTotalIncomeFromCategory(category, date1, date2) { totalExpense ->
                totalAmount += totalExpense
                totalCount++

                if (totalCount == categories.size) {
                    Log.d("TotalExpense", "Całkowita kwota wydatków: $totalAmount")
                    callback(totalAmount)
                }
            }
        }

    }
}