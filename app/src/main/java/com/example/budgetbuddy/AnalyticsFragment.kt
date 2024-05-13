package com.example.budgetbuddy

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.graphics.Color.*
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.example.budgetbuddy.databinding.FragmentAnalyticsBinding
import java.text.SimpleDateFormat
import java.util.Locale
import com.db.williamchart.data.Label
import com.db.williamchart.data.Scale
import com.db.williamchart.data.shouldDisplayAxisX
import java.util.Date


class AnalyticsFragment : Fragment() {

    private lateinit var binding: FragmentAnalyticsBinding
    private lateinit var firebaseRepository: FirebaseRepository
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        val currentDate = Calendar.getInstance()
        val day = currentDate.get(Calendar.DAY_OF_MONTH)
        val month = currentDate.get(Calendar.MONTH) + 1
        val year = currentDate.get(Calendar.YEAR)

        binding.dateTo.text = String.format("%02d.%02d.%d", day, month, year)
        binding.dateFrom.text = String.format("%02d.%02d.%d", day, month-1, year)



        binding.barChartCategoryExpenses.animation.duration = animationDuraction
        binding.barChartCategoryExpenses.labelsSize=20F


        binding.barChartExpenses.animation.duration = animationDuraction
        binding.barChartExpenses.donutColors = intArrayOf(
            parseColor("#7CAA74"),
            parseColor("#BF4747")
        )

        binding.barChartCategoryExpenses.barsColorsList = listOf(
            parseColor("#6598BA"),
            parseColor("#A86E51"),
            parseColor("#F9BB69"),
            parseColor("#CE755D"),
            parseColor("#939399"),
            parseColor("#8DC9C3"),
            parseColor("#BF4747"),
            parseColor("#F2D85A"),
            parseColor("#7CAA74"),
            parseColor("#364D84"),
            parseColor("#B56F95"),
            parseColor("#6375B7"),
        )


        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateFrom = dateFormatter.parse(binding.dateFrom.text.toString())
        val dateEnd = dateFormatter.parse(binding.dateTo.text.toString())


        firebaseRepository = FirebaseRepository(requireContext())



        firebaseRepository.getBudget(
            onSuccess = {budget ->
                val formattedBudget = String.format("%.2f", budget).replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1 ")
                val updatedBudgetSet = barSetBuget.toMutableList()
                updatedBudgetSet[0] = budget.toFloat()
                barSetBuget = updatedBudgetSet.toList()
                binding.barChartExpenses.animate(barSetBuget.map { it })
                binding.budgetAmount.text=formattedBudget
            },
            onFailure = {

            }
        )

        firebaseRepository.getExpensesVal(
            onSuccess = {expenses ->
                val formattedBudget = String.format("%.2f", expenses).replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1 ")
                val updatedBudgetSet = barSetBuget.toMutableList()
                updatedBudgetSet[1] = expenses.toFloat()
                barSetBuget = updatedBudgetSet.toList()
                binding.barChartExpenses.donutTotal = barSetBuget[0] + barSetBuget[1]
                binding.barChartExpenses.animate(barSetBuget.map { it })
                binding.expensesAmount.text=formattedBudget
            },
            onFailure = {

            }
        )
fun updateChar (dateFrom: Date, dateEnd: Date) {
    firebaseRepository.getTotalExpenseFromCategory("car", dateFrom, dateEnd) { totalExpense ->
        val updatedBarSet = barSet.toMutableList()
        updatedBarSet[0] = "Samochód" to totalExpense
        barSet = updatedBarSet.toList()
        val entries = barSet.map { it.first to it.second.toFloat() }

        binding.barChartCategoryExpenses.animate(entries)
    }
    firebaseRepository.getTotalExpenseFromCategory("house", dateFrom, dateEnd) { totalExpense ->
        val updatedBarSet = barSet.toMutableList()
        updatedBarSet[1] = "Dom" to totalExpense
        barSet = updatedBarSet.toList()
        val entries = barSet.map { it.first to it.second.toFloat() }
        binding.barChartCategoryExpenses.animate(entries)


    }
    firebaseRepository.getTotalExpenseFromCategory("clothes", dateFrom, dateEnd) { totalExpense ->
        val updatedBarSet = barSet.toMutableList()
        updatedBarSet[2] = "Ubrania" to totalExpense
        barSet = updatedBarSet.toList()
        val entries = barSet.map { it.first to it.second.toFloat() }
        binding.barChartCategoryExpenses.animate(entries)
    }

    firebaseRepository.getTotalExpenseFromCategory("shopping", dateFrom, dateEnd) { totalExpense ->
        val updatedBarSet = barSet.toMutableList()
        updatedBarSet[3] = "Zakupy" to totalExpense
        barSet = updatedBarSet.toList()
        val entries = barSet.map { it.first to it.second.toFloat() }
        binding.barChartCategoryExpenses.animate(entries)
    }

    firebaseRepository.getTotalExpenseFromCategory("transport", dateFrom, dateEnd) { totalExpense ->
        val updatedBarSet = barSet.toMutableList()
        updatedBarSet[4] = "Transport" to totalExpense
        barSet = updatedBarSet.toList()
        val entries = barSet.map { it.first to it.second.toFloat() }
        binding.barChartCategoryExpenses.animate(entries)
    }

    firebaseRepository.getTotalExpenseFromCategory("sport", dateFrom, dateEnd) { totalExpense ->
        val updatedBarSet = barSet.toMutableList()
        updatedBarSet[5] = "Sport" to totalExpense
        barSet = updatedBarSet.toList()
        val entries = barSet.map { it.first to it.second.toFloat() }
        binding.barChartCategoryExpenses.animate(entries)
    }

    firebaseRepository.getTotalExpenseFromCategory("health", dateFrom, dateEnd) { totalExpense ->
        val updatedBarSet = barSet.toMutableList()
        updatedBarSet[6] = "Zdrowie" to totalExpense
        barSet = updatedBarSet.toList()
        val entries = barSet.map { it.first to it.second.toFloat() }
        binding.barChartCategoryExpenses.animate(entries)
    }

    firebaseRepository.getTotalExpenseFromCategory(
        "entertainment",
        dateFrom,
        dateEnd
    ) { totalExpense ->
        val updatedBarSet = barSet.toMutableList()
        updatedBarSet[7] = "Rozrywka" to totalExpense
        barSet = updatedBarSet.toList()
        val entries = barSet.map { it.first to it.second.toFloat() }
        binding.barChartCategoryExpenses.animate(entries)
    }

    firebaseRepository.getTotalExpenseFromCategory("relax", dateFrom, dateEnd) { totalExpense ->
        val updatedBarSet = barSet.toMutableList()
        updatedBarSet[8] = "Relax" to totalExpense
        barSet = updatedBarSet.toList()
        val entries = barSet.map { it.first to it.second.toFloat() }
        binding.barChartCategoryExpenses.animate(entries)
    }

    firebaseRepository.getTotalExpenseFromCategory(
        "restaurant",
        dateFrom,
        dateEnd
    ) { totalExpense ->
        val updatedBarSet = barSet.toMutableList()
        updatedBarSet[9] = "Restauracje" to totalExpense
        barSet = updatedBarSet.toList()
        val entries = barSet.map { it.first to it.second.toFloat() }
        binding.barChartCategoryExpenses.animate(entries)
    }

    firebaseRepository.getTotalExpenseFromCategory("gift", dateFrom, dateEnd) { totalExpense ->
        val updatedBarSet = barSet.toMutableList()
        updatedBarSet[10] = "Prezenty" to totalExpense
        barSet = updatedBarSet.toList()
        val entries = barSet.map { it.first to it.second.toFloat() }
        binding.barChartCategoryExpenses.animate(entries)
    }

    firebaseRepository.getTotalExpenseFromCategory("education", dateFrom, dateEnd) { totalExpense ->
        val updatedBarSet = barSet.toMutableList()
        updatedBarSet[11] = "Edukacja" to totalExpense
        barSet = updatedBarSet.toList()
        val entries = barSet.map { it.first to it.second.toFloat() }
        binding.barChartExpenses.donutTotal =
            barSetBuget.toMutableList()[0] + barSetBuget.toMutableList()[1]
        binding.barChartCategoryExpenses.animate(entries)
    }

    val entries = barSet.map { it.first to it.second.toFloat() }
    binding.barChartCategoryExpenses.animate(entries)
}
        updateChar(dateFrom,dateEnd)
        binding.chooseCalendarButton1.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, monthOfYear)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val selectedDateFormatted = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(selectedDate.time)
                    binding.dateFrom.text=selectedDateFormatted
                    updateChar(dateFormatter.parse(selectedDateFormatted),dateEnd)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
            updateChar(dateFrom,dateEnd)



        }

        binding.chooseCalendarButton2.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, monthOfYear)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val selectedDateFormatted = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(selectedDate.time)
                    binding.dateTo.text=selectedDateFormatted
                    updateChar(dateFrom,dateFormatter.parse(selectedDateFormatted))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)

            )
            datePickerDialog.show()



        }


        binding.barChartExpenses.animate(barSetBuget)






        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val toolbarMenu = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val toolbarBack = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_back)


        val textViewName = toolbarMenu?.findViewById<TextView>(R.id.nameofpageHP)
        textViewName?.text = "Podsumowanie"
        toolbarMenu?.visibility = View.VISIBLE
        toolbarBack?.visibility = View.GONE



    }
    companion object {
        private const val animationDuraction = 100L



        private var barSet = listOf(
            "Samochód" to 0.00,
            "Dom" to 0.00,
            "Ubrania" to 0.00,
            "Zakupy" to 0.00,
            "Transport" to 0.00,
            "Sport" to 0.00,
            "Zdrowie" to 0.00,
            "Rozrywka" to 0.00,
            "Relax" to 0.00,
            "Restauracje" to 0.00,
            "Prezenty" to 0.00,
            "Edukacja" to 0.00
        )

        private var barSetBuget = listOf(
            0.00F,
            0.00F
        )


        fun newInstance() = AnalyticsFragment()
    }
}

