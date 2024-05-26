package com.example.budgetbuddy


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.app.DatePickerDialog
import android.content.Context
import android.widget.Button
import android.graphics.Color.*

import com.example.budgetbuddy.databinding.FragmentHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class HomeFragment : Fragment() {


    private lateinit var firebaseRepository: FirebaseRepository

    private lateinit var binding: FragmentHomeBinding
    private lateinit var paymentReminderDialogFragment: PaymentReminderDialogFragment


    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dateButton: Button

    private var selectedYear = 0
    private var selectedMonth = 0

    private var barSetBuget = listOf ( 0.00F, 0.00F,0.00F,0.00F,0.00F,0.00F,0.00F,0.00F,0.00F,0.00F,0.00F,0.00F )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupDonutChart()
        dateButton  = binding.datePickerButton
        dateButton.text = getCurrentMonthYear()

        firebaseRepository = FirebaseRepository(requireContext())

        loadSavedDate()
        dateButton.text = makeDateString(selectedMonth, selectedYear)
        initDatePicker()

        dateButton.setOnClickListener { openDatePicker(it) }

         updateChart(selectedYear, selectedMonth)

        binding.expenseButton.setOnClickListener {
            val expensesAddingFragment = ExpensesAddingFragment.newInstance(null,0,null, null)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, expensesAddingFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        binding.incomeButton.setOnClickListener {
            val incomesAddingFragment = IncomesAddingFragment.newInstance(null,null,null, null)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, incomesAddingFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        binding.catDom.setOnClickListener {
            val homeHistoryFragment = HomeHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, homeHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.catAuto.setOnClickListener {
            val autoHistoryFragment = CarHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, autoHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.catTransport.setOnClickListener{
            val transportHistoryFragment =TransportHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, transportHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        binding.catSport.setOnClickListener {
            val sportHistoryFragment = SportHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, sportHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.catUbrania.setOnClickListener{
            val ubranieHistoryFragment =UbranieHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, ubranieHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.catZdrowie.setOnClickListener{
            val healthHistoryFragment =HealthHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, healthHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.catPrezent.setOnClickListener {
            val prezentHistoryFragment = PrezentHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, prezentHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.catEdukacja.setOnClickListener {
            val edukacjaHistoryFragment = EdukacjaHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, edukacjaHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.catRestauracje.setOnClickListener {
            val restauracjaHistoryFragment = RestauracjaHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, restauracjaHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.catRozrywka.setOnClickListener {
            val rozrywkaHistoryFragment = RozrywkaHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, rozrywkaHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.catZakupy.setOnClickListener {
            val zakupyHistoryFragment = ZakupyHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, zakupyHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.catWypoczynek.setOnClickListener {
            val wypoczynekHistoryFragment = WypoczynekHistoryFragment.newInstance()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, wypoczynekHistoryFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


        val view = binding.root
        firebaseRepository = FirebaseRepository(requireContext())
        val budgetmonthV = view.findViewById<TextView>(R.id.budgetmonth)
        firebaseRepository.getBudget(
            onSuccess = {budget ->
                val formattedBudget = String.format("%.2f", budget).replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1 ")
                budgetmonthV.text = formattedBudget

            },
            onFailure = {
                budgetmonthV.text = "0.00"
            }
        )

        val expensesV = view.findViewById<TextView>(R.id.expenses)
        firebaseRepository.getExpensesVal(
            onSuccess = {expenses ->
                val formattedSavings = String.format("%.2f", expenses).replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1 ")
                expensesV.text = formattedSavings

            },
            onFailure = {
                expensesV.text = "0.00"
            }
        )

        val paymentReminderDialog = PaymentReminderDialogFragment()
            paymentReminderDialog.show(childFragmentManager, "PaymentReminderDialog")

        binding.barChart.animate(barSetBuget)

        return view
    }

    private fun loadSavedDate() {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        selectedYear = prefs.getInt(PREF_SELECTED_YEAR, Calendar.getInstance().get(Calendar.YEAR))
        selectedMonth = prefs.getInt(PREF_SELECTED_MONTH, Calendar.getInstance().get(Calendar.MONTH)+1)
    }

    private fun saveSelectedDate(year: Int, month: Int){
        val prefs= requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()){
            putInt(PREF_SELECTED_YEAR, year )
            putInt(PREF_SELECTED_MONTH, month)
            apply()
        }
    }

    private fun updateChart(year: Int, month: Int) {
        val categories = listOf("car", "house", "clothes","shopping", "transport", "sport", "health", "entertainment", "relax", "restaurant", "gift", "education")
        val updatedBudgetSet = MutableList(categories.size) { 0f }

        var completedRequests = 0
        val totalRequests = categories.size

        categories.forEachIndexed { index, category ->
            firebaseRepository.getExpenseMonth(category, year, month, onSuccess = { expenses ->
                updatedBudgetSet[index] = expenses.toFloat()

                completedRequests++
                if (completedRequests == totalRequests) {
                    updateDonutChart(updatedBudgetSet)
                }
            }, onFailure = {
                completedRequests++
                if (completedRequests == totalRequests) {
                    updateDonutChart(updatedBudgetSet)
                }
            })
        }
    }

    private fun updateDonutChart(budgetSet: List<Float>) {
        barSetBuget = budgetSet
        binding.barChart.donutTotal = budgetSet.sum()
        binding.barChart.animate(barSetBuget)
    }

    private fun setupDonutChart() {
        binding.barChart.animation.duration = animationDuraction

        binding.barChart.donutColors = intArrayOf(
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
            parseColor("#6375B7")
        )
    }

    private fun getCurrentMonthYear(): String {

        val cal = Calendar.getInstance()

        selectedMonth = cal.get(Calendar.MONTH)+1
        selectedYear = cal.get(Calendar.YEAR)
        return makeDateString(selectedYear, selectedMonth)
    }

    private fun initDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, _ ->
            selectedMonth = month+1
            selectedYear = year
            val date = makeDateString(selectedMonth, year)
            dateButton.text= date
            saveSelectedDate(year, month+1)
            updateChart(selectedYear, selectedMonth)
        }

        val cal = Calendar.getInstance()
        val year=cal.get(Calendar.YEAR)
        val month=cal.get(Calendar.MONTH)

        val style = AlertDialog.THEME_HOLO_LIGHT

        val locale=Locale("pl", "PL")
        Locale.setDefault(locale)

        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        datePickerDialog= DatePickerDialog(requireContext(), style, dateSetListener, year, month, 1 )
        datePickerDialog.datePicker.findViewById<View>(
            resources.getIdentifier("android:id/day",null,null)
        ).visibility=View.GONE

        datePickerDialog.datePicker.maxDate=cal.timeInMillis

    }

    private fun makeDateString(month: Int, year: Int): String {

        return "${getMonthFormat(month)} $year   "
    }


    private fun getMonthFormat(month: Int): String {
        return when (month){
            1->"STYCZEŃ"
            2->"LUTY"
            3->"MARZEC"
            4->"KWIECIEŃ"
            5->"MAJ"
            6->"CZERWIEC"
            7->"LIPIEC"
            8->"SIERPIEŃ"
            9->"WRZESIEŃ"
            10->"PAŹDZIERNIK"
            11->"LISTOPAD"
            12->"GRUDZIEŃ"
            else -> "STYCZEŃ"
        }
    }

    fun openDatePicker(view: View){
        datePickerDialog.show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentReminderDialogFragment = PaymentReminderDialogFragment()

        val toolbarMenu = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val toolbarBack = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_back)


        val textViewName = toolbarMenu?.findViewById<TextView>(R.id.nameofpageHP)
        textViewName?.text = "Strona główna"
        toolbarMenu?.visibility = View.VISIBLE
        toolbarBack?.visibility = View.GONE

    }


    override fun onDestroy() {
        super.onDestroy()
        clearSavedDate()
    }

    private fun clearSavedDate() {
        val prefs=requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    companion object {

        private const val animationDuraction = 100L

        private const val PREFS_NAME = "HomeFragmentPrefs"
        private const val PREF_SELECTED_YEAR = "selectedYear"
        private const val PREF_SELECTED_MONTH = "selectedMonth"


        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}