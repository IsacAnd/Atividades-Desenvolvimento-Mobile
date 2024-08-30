package com.example.livrosv2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.livrosv2.databinding.ActivityEditUserBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storage

class EditUser : AppCompatActivity() {

    private lateinit var binding: ActivityEditUserBinding
    private var imageUri: Uri? = null

    private val imageContract = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username = intent.getStringExtra("username")
        binding.edtUsername.setText(username)

        binding.btnEditUser.setOnClickListener {
            editUser()
        }

        binding.btnAddImage.setOnClickListener {
            imageContract.launch("image/*")
        }
    }

    private fun editUser() {
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore

        if (user != null) {
            if (imageUri != null) {
                uploadImage { imageUrl ->
                    db.collection("users").document(user.uid).update(
                        mapOf(
                            "username" to binding.edtUsername.text.toString(),
                            "imageUrl" to imageUrl
                        )
                    ).addOnSuccessListener {
                        Toast.makeText(this, "Informações atualizadas com sucesso.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, BooksMain::class.java)
                        startActivity(intent)
                        finish()
                    }.addOnFailureListener { e ->
                        Log.w(TAG, "Erro ao atualizar o usuário", e)
                        Toast.makeText(this, "Erro ao atualizar o usuário.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                db.collection("users").document(user.uid).update(
                    mapOf(
                        "username" to binding.edtUsername.text.toString()
                    )
                ).addOnSuccessListener {
                    Toast.makeText(this, "Informações atualizadas com sucesso.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, BooksMain::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener { e ->
                    Log.w(TAG, "Erro ao atualizar o usuário", e)
                    Toast.makeText(this, "Erro ao atualizar o usuário.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadImage(callback: (String) -> Unit) {
        val storageRef = Firebase.storage.reference
        val fileRef = storageRef.child("user_images/${System.currentTimeMillis()}.jpg")

        fileRef.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao fazer upload da imagem", e)
                Toast.makeText(this, "Erro ao fazer upload da imagem.", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "EditUser"
    }
}