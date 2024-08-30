package com.example.livrosv2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.livrosv2.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.firestore.ktx.firestore

class Register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private var imageUri: Uri? = null

    private val imageContract = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            Toast.makeText(this, "Imagem adicionada com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val edtUsername = binding.edtUsername
        val edtEmail = binding.edtEmailR
        val edtPassword = binding.edtPasswordR
        val edtPassword2 = binding.edtPasswordRR

        binding.btnRegister.setOnClickListener {
            when {
                edtUsername.text.isBlank() -> {
                    Toast.makeText(this, "Preencha o campo de usuário!", Toast.LENGTH_SHORT).show()
                }
                edtPassword.text.toString() != edtPassword2.text.toString() -> {
                    Toast.makeText(this, "Senhas não correspondem!", Toast.LENGTH_SHORT).show()
                }
                edtEmail.text.isBlank() -> {
                    Toast.makeText(this, "Email inválido!", Toast.LENGTH_SHORT).show()
                }
                edtPassword.text.isBlank() || edtPassword2.text.isBlank() -> {
                    Toast.makeText(this, "Campos de senha devem ser preenchidos!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    addUser(edtUsername.text.toString(), edtEmail.text.toString(),edtPassword.text.toString())
                }
            }
        }

        binding.btnAddPhoto.setOnClickListener {
            imageContract.launch("image/*")
        }
    }

    private fun addUser(username: String, email: String, password: String) {
        if (imageUri != null) {
            val storageRef = Firebase.storage.reference
            val fileRef = storageRef.child("user_images/${System.currentTimeMillis()}.jpg")

            fileRef.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        createUser(username, email, password, uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Erro ao fazer upload da imagem", e)
                    Toast.makeText(this, "Erro ao fazer upload da imagem.", Toast.LENGTH_SHORT).show()
                }
        } else {
            createUser(username, email, password, null)
        }
    }

    private fun createUser(username: String, email: String, password: String, imageUrl: String?) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid

                    if (uid != null) {
                        val db = Firebase.firestore
                        val userMap = hashMapOf(
                            "username" to username,
                            "email" to email,
                            "imageUrl" to imageUrl
                        )

                        db.collection("users").document(uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                Log.d(TAG, "User document created successfully")
                                Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding user document", e)
                                Toast.makeText(
                                    baseContext,
                                    "Erro ao criar usuário.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Falha na autenticação.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    companion object {
        private const val TAG = "EmailAndPasswordR"
    }
}
