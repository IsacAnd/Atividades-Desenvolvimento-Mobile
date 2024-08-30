package com.example.livrosv2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.livrosv2.databinding.ActivityBooksMainBinding
import com.example.livrosv2.databinding.ActivityUserInterfaceBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class UserInterface : AppCompatActivity() {

    private lateinit var binding: ActivityUserInterfaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInterfaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUser()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnEditUser.setOnClickListener {
           val intent = Intent(this, EditUser::class.java)
            intent.putExtra("username", binding.nameView.text.toString())
            startActivity(intent)
        }
    }

    private fun getUser() {
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore

        if (user != null) {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val imageUrl = document.getString("imageUrl")
                        val username = document.getString("username")
                        val email = document.getString("email")

                        binding.nameView.text = username
                        binding.emailView.text = email

                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(imageUrl)
                                .circleCrop()
                                .error(binding.userImage)
                                .into(binding.userImage)
                            return@addOnSuccessListener
                        } else {
                            // Se a imagem não existir, use a imagem padrão
                            Glide.with(this)
                                .load(R.drawable.default_image)  // Substitua pelo recurso da imagem padrão
                                .circleCrop()
                                .into(binding.userImage)
                        }
                    }
                }
        }
        return
    }
}