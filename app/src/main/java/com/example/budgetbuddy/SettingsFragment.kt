package com.example.budgetbuddy

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.net.Uri
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.Manifest
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.budgetbuddy.databinding.FragmentSettingsBinding
import com.google.firebase.Firebase
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.storage
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var db = Firebase.firestore

    private lateinit var firebaseAuth: FirebaseAuth
    private var imageUri: Uri?= null
    private var pickImage = 100
    private val storage = Firebase.storage
    private lateinit var firebaseRepository: FirebaseRepository
    lateinit var imageView: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        firebaseRepository = FirebaseRepository(requireContext())

        firebaseAuth = FirebaseAuth.getInstance()
        loadImageFromFirebase()

        binding.changeImgButton.setOnClickListener{
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2){
                if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    !=PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1
                    )

                }else{
                    pickImageFromGallery()
                }
                }else{
                    if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                        !=PackageManager.PERMISSION_GRANTED){
                        requestPermissions(
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),1
                        )
                    }else{
                        pickImageFromGallery()
                    }
            }
        }

        val documentId = arguments?.getString("documentId")
        firebaseRepository.getName(
            onSuccess = { name->
                binding.nameChange.setText(name)
            },
            onFailure = {
                binding.nameChange.setText("Imię")
            }
        )

        firebaseRepository.getSurname(
            onSuccess = { surname->
                binding.changeLastname.setText(surname)
            },
            onFailure = {
                binding.changeLastname.setText("Nazwisko")
            }
        )


        binding.saveButton.setOnClickListener {
            val name = binding.nameChange.text.toString()
            val surname = binding.changeLastname.text.toString()
                updateUser( name, surname)
                val home  = HomeFragment.newInstance()
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.fragment_container, home)
                transaction.addToBackStack(null)
                transaction.commit()

        }

        return binding.root
    }


    private fun loadImageFromFirebase() {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/$userId/profile.jpg")
        storageRef.metadata.addOnSuccessListener {_ ->
            storageRef.downloadUrl.addOnSuccessListener {  uri ->
                val imageUrl = uri.toString()
                imageUri = uri
                Glide.with(this)
                    .load(imageUrl)
                    .transform(CircleCrop())
                    .into(binding.changeImgButton)
            }.addOnFailureListener { exception ->
                Log.e("Settings", "Nie udało się pobrać zdjęcie", exception)
            }
        }.addOnFailureListener { exception ->
            if ((exception as StorageException).errorCode == StorageException.ERROR_OBJECT_NOT_FOUND){
                Log.e("Settings", "Zdjęcie nie istnieje", exception)
            }else{
                Log.e("Settings", "Błąd sprzwdzenia czy zdjęcie istnieje", exception)
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, pickImage)
    }

    private fun updateUser( name: String, surname: String) {
        if (name.isNotEmpty() && surname.isNotEmpty()) {
            if (imageUri!=null)
            {
                val storageRef = storage.reference.child("image/$userId/profile.jpg")
                storageRef.putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                         db.collection(userId).document("user")
                             .update(
                                 mapOf(
                                     "name" to name,
                                     "surename" to surname,
                                     "imageUrl" to imageUrl
                                 )
                             ).addOnSuccessListener {  Log.d(TAG, "Zmiany zostały zapisane") }
                             .addOnFailureListener { e -> Log.w(TAG, "Zmiany nie zostały zapisane", e) }
                    }

                }.addOnFailureListener{ exception ->
                    Log.e(TAG, "Błąd przy przesyłaniu zdjęcia", exception)
                }
            }else{

                db.collection(userId).document("user")
                    .update(
                        mapOf(
                            "name" to name,
                            "surename" to surname,
                        )
                    ).addOnSuccessListener { Log.d(TAG, "Zmiany zostały zapisane") }
                    .addOnFailureListener{ e -> Log.w(TAG, "Zmiany nie zostały zapisane", e)}
            }

        }
        else {
            Toast.makeText(requireContext(), "Wypełnij wszytskie pola", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val toolbarMenu = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val toolbarBack = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_back)


        val textViewName = toolbarMenu?.findViewById<TextView>(R.id.nameofpageHP)
        textViewName?.text = "Edytuj profil"
        toolbarMenu?.visibility = View.VISIBLE
        toolbarBack?.visibility = View.GONE



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == pickImage && resultCode == Activity.RESULT_OK){

            imageUri = data?.data

            Glide.with(this)
                .load(imageUri)
                .transform(CircleCrop())
                .into(binding.changeImgButton)
            binding.changeImgText.text="ZDJĘCIE ZMIENIONE"
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                pickImageFromGallery()
            } else {

                Toast.makeText(requireContext(), "Uprawnienie nie zostało przyznane", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {

        //const val REQUEST_IMAGE_PICK =2

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}