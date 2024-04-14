package com.example.budgetbuddy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbuddy.databinding.FragmentCarHistoryBinding
import com.example.budgetbuddy.databinding.FragmentHomeHistoryBinding
import com.example.budgetbuddy.databinding.FragmentZakupyHistoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ZakupyHistoryFragment : Fragment() {



    private lateinit var recview: RecyclerView
    private lateinit var adapter: MyAdapter
    private lateinit var binding: FragmentZakupyHistoryBinding
    private lateinit var  datalist: ArrayList<Model>
    private lateinit var db: FirebaseFirestore
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentZakupyHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recview = binding.recviewZak
        recview.layoutManager=  LinearLayoutManager(requireContext())
        datalist=ArrayList()
        adapter = MyAdapter(datalist)
        recview.adapter = adapter
        db=FirebaseFirestore.getInstance()
        db.collection(userId).document("budget").collection("shopping").get()
            .addOnSuccessListener{querySnapshot->
                val list = querySnapshot.documents
                for(document in list)
                {
                    val obj = document.toObject(Model::class.java)
                    obj?.let{
                        it.collection="shopping"
                        datalist.add(it)}
                }
                datalist.reverse()
                adapter.notifyDataSetChanged()
            }


    }

    companion object{
        fun newInstance(): ZakupyHistoryFragment {
            return ZakupyHistoryFragment()
        }
    }


}