package com.example.budgetbuddy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.databinding.FragmentListOfFixedExpensesBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ListOfFixedExpensesFragment : Fragment() {

    private lateinit var recview: RecyclerView
    private lateinit var adapter: MyAdapterFixed
    private lateinit var binding: FragmentListOfFixedExpensesBinding
    private lateinit var  datalist: ArrayList<ModelFixed>
    private lateinit var db: FirebaseFirestore
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentListOfFixedExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val toolbarBack = activity?.findViewById<Toolbar>(R.id.toolbar_back)
        val toolbarMenu = requireActivity().findViewById<Toolbar>(R.id.toolbar)

        val textViewName = toolbarMenu?.findViewById<TextView>(R.id.nameofpageHP)
        textViewName?.text = "Zlecenia stałe"
        toolbarMenu?.visibility = View.VISIBLE
        toolbarBack?.visibility = View.GONE

        val addButton = view.findViewById<FloatingActionButton>(R.id.fab_add)
        addButton.setOnClickListener(null)
        addButton.setOnClickListener{
            val fragmentNext = FixedExpenseFragment.newInstance()
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, fragmentNext)
                ?.addToBackStack(null)
                ?.commit()
        }

        recview = binding.recviewList
        recview.layoutManager=  LinearLayoutManager(requireContext())
        datalist=ArrayList()
        adapter = MyAdapterFixed(datalist)
        recview.adapter = adapter
        db=FirebaseFirestore.getInstance()
        db.collection(userId).document("fixedExpenses").collection("documents").get()
            .addOnSuccessListener{querySnapshot->
                val list = querySnapshot.documents
                for(document in list)
                {
                    val obj = document.toObject(ModelFixed::class.java)

                    obj?.let{
                        it.documentId = document.id
                        datalist.add(it)}
                }
                datalist.reverse()
                adapter.notifyDataSetChanged()
            }

    }



    companion object{
        fun newInstance(): ListOfFixedExpensesFragment {
            return ListOfFixedExpensesFragment()
        }
    }
}