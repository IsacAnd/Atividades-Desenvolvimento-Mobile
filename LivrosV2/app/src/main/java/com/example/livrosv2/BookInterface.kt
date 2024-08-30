package com.example.livrosv2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.livrosv2.databinding.ActivityBookInterfaceBinding
import com.example.livrosv2.databinding.ActivityBooksMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class BookInterface : AppCompatActivity() {

    private lateinit var binding: ActivityBookInterfaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookInterfaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        printBook()

        binding.btnEdit.setOnClickListener {
            val title = intent.getStringExtra("title")
            val publisher = intent.getStringExtra("publisher")
            val genre = intent.getStringExtra("genre")
            val id = intent.getStringExtra("id")

            val isRead = intent.getBooleanExtra("isRead", false)
            val rating = intent.getFloatExtra("rating", 0f)

            val intent = Intent(this, EditBook::class.java)

            intent.putExtra("title", title)
            intent.putExtra("publisher", publisher)
            intent.putExtra("genre", genre)
            intent.putExtra("id", id)
            intent.putExtra("rating", rating)
            intent.putExtra("isRead", isRead)
            startActivity(intent)
            finish()
        }
    }

    private fun printBook() {
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore

        binding.txtTitle.text = intent.getStringExtra("title")
        binding.txtPubliser.text = intent.getStringExtra("publisher")
        binding.txtGenre.text = intent.getStringExtra("genre")
        binding.txtName.append(intent.getStringExtra("title"))
        val id = intent.getStringExtra("id")!!

        if (user != null) {
            db.collection("users").document(user.uid).collection("books").document(id).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val imageUrl = document.getString("imageUrl")

                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(imageUrl)
                                .error(binding.bookImage)
                                .into(binding.bookImage)
                            return@addOnSuccessListener
                        } else {
                            // Se a imagem não existir, use a imagem padrão
                            Glide.with(this)
                                .load(R.drawable.default_book_image)  // Substitua pelo recurso da imagem padrão
                                .into(binding.bookImage)
                        }
                    }
                }
        }
    }
}