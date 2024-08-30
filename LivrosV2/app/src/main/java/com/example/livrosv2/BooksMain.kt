package com.example.livrosv2

import BooksAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.livrosv2.databinding.ActivityBooksMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BooksMain : AppCompatActivity() {

    private lateinit var binding: ActivityBooksMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBooksMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserImage()

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AddBook::class.java)
            startActivity(intent)
        }

        binding.userImage.setOnClickListener {
            val intent = Intent(this, UserInterface::class.java)
            startActivity(intent)
        }

        getAllBooks { books ->
            if (books != null) {
                setupRecyclerView(books)
            } else {
                Log.w(TAG, "Nenhum livro encontrado ou ocorreu um erro.")
            }
        }
    }

    private fun getUserImage() {
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore

        if (user != null && user.uid != null) {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val imageUrl = document.getString("imageUrl")

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

    private fun setupRecyclerView(books: List<Map<String, Any>>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = BooksAdapter(books) { book ->
            val intent = Intent(this, BookInterface::class.java)
            intent.putExtra("title", book["title"].toString())
            intent.putExtra("publisher", book["publisher"].toString())
            intent.putExtra("genre", book["genre"].toString())
            intent.putExtra("id", book["id"].toString()) // Passa o id para a próxima tela
            intent.putExtra("imageUrl", book["imageUrl"].toString())
            intent.putExtra("isRead", book["isRead"] as? Boolean ?: false) // Passa o status de leitura
            intent.putExtra("rating", (book["rating"] as? Number)?.toFloat() ?: 0) // Passa a nota

            startActivity(intent)
        }
    }

    private fun getAllBooks(callback: (List<Map<String, Any>>?) -> Unit) {
        val auth = Firebase.auth
        val user = auth.currentUser
        val db = Firebase.firestore

        if (user == null) {
            Log.w(TAG, "Usuário não autenticado.")
            callback(null)
            return
        }

        try {
            db.collection("users").document(user.uid).collection("books").get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val books = querySnapshot.documents.map { document ->
                            val book = document.data?.toMutableMap() ?: mutableMapOf()
                            book["id"] = document.id // Adiciona o id do documento ao mapa
                            book
                        }
                        callback(books)
                    } else {
                        Log.d(TAG, "No books found.")
                        callback(null)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                    callback(null)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching books", e)
            callback(null)
        }
    }

    companion object {
        private const val TAG = "BooksMain"
    }
}
