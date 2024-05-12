package com.example.budgetbuddy

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.graphics.Color.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import com.example.budgetbuddy.databinding.FragmentAnalyticsBinding
import java.text.SimpleDateFormat
import java.util.Locale
import com.db.williamchart.data.Label
import com.db.williamchart.data.Scale
import com.db.williamchart.data.shouldDisplayAxisX


class AnalyticsFragment : Fragment() {

    private lateinit var binding: FragmentAnalyticsBinding
    private lateinit var firebaseRepository: FirebaseRepository
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        binding.barChartCategoryExpenses.animation.duration = animationDuraction
        binding.barChartCategoryExpenses.labelsSize=20F


        binding.barChartExpenses.animation.duration = animationDuraction
        binding.barChartExpenses.donutColors = intArrayOf(
            parseColor("#90EE90"),
            parseColor("#EE9090")
        )

        binding.barChartCategoryExpenses.barsColorsList = listOf(
            parseColor("#90EE90"),
            parseColor("#90EE90"),
            parseColor("#90EE90"),
            parseColor("#90EE90"),
            parseColor("#90EE90"),
            parseColor("#90EE90"),
            parseColor("#90EE90"),
            parseColor("#90EE90"),
            parseColor("#90EE90"),
            parseColor("#90EE90"),
            parseColor("#90EE90"),
            parseColor("#90EE90"),
        )


        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateFrom = dateFormatter.parse("20.01.2024")
        val dateEnd = dateFormatter.parse("25.05.2024")


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

        firebaseRepository.getTotalExpenseFromCategory("entertainment", dateFrom, dateEnd) { totalExpense ->
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

        firebaseRepository.getTotalExpenseFromCategory("restaurant", dateFrom, dateEnd) { totalExpense ->
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
            binding.barChartExpenses.donutTotal= barSetBuget.toMutableList()[0] + barSetBuget.toMutableList()[1]
            binding.barChartCategoryExpenses.animate(entries)
        }



        binding.barChartExpenses.animate(barSetBuget)

        val entries = barSet.map { it.first to it.second.toFloat() }
        binding.barChartCategoryExpenses.animate(entries)




        return binding.root
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
            2.00F,
            50.00F
        )

        fun newInstance() = AnalyticsFragment()
    }
}

