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
import com.example.livrosv2.databinding.ActivityEditBookBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EditBook : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var binding: ActivityEditBookBinding
    private var imageUri: Uri? = null

    private val imageContract = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Preenche os campos com as informações do livro
        binding.edtTitle.setText(intent.getStringExtra("title"))
        binding.edtPublisher.setText(intent.getStringExtra("publisher"))
        binding.edtGenre.setText(intent.getStringExtra("genre"))
        val checked = intent.getBooleanExtra("isRead", false)
        binding.ratingBar2.rating = intent.getFloatExtra("rating", 0f)
        binding.checkBox.isChecked = intent.getBooleanExtra("isRead", false)

        binding.btnEditBook.setOnClickListener {
            editBook()
        }

        binding.btnAddImage.setOnClickListener {
            imageContract.launch("image/*")
        }
    }

    private fun editBook() {
        val auth = Firebase.auth
        val user = auth.currentUser
        val id = intent.getStringExtra("id")

        if (user == null) {
            Log.w(TAG, "Usuário não autenticado.")
            Toast.makeText(this, "Você precisa estar logado para editar um livro.", Toast.LENGTH_SHORT).show()
            return
        }

        val title = binding.edtTitle.text.toString()
        val publisher = binding.edtPublisher.text.toString()
        val genre = binding.edtGenre.text.toString()
        val rating = binding.ratingBar2.rating
        val read = binding.checkBox.isChecked

        if (title.isBlank() || publisher.isBlank() || genre.isBlank()) {
            Toast.makeText(this, "Todos os campos devem ser preenchidos.", Toast.LENGTH_SHORT).show()
            return
        }

        val book = hashMapOf(
            "title" to title,
            "publisher" to publisher,
            "genre" to genre,
            "rating" to rating,
            "isRead" to read
        )

        if (imageUri != null) {
            uploadImage { imageUrl ->
                book["imageUrl"] = imageUrl
                updateBookInFirestore(user.uid, id, book)
            }
        } else {
            updateBookInFirestore(user.uid, id, book)
        }
    }

    private fun uploadImage(callback: (String) -> Unit) {
        val storageRef = Firebase.storage.reference
        val fileRef = storageRef.child("user_images/${System.currentTimeMillis()}.jpg")

        imageUri?.let {
            fileRef.putFile(it)
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
    }

    private fun updateBookInFirestore(
        userId: String,
        bookId: String?,
        book: Map<String, Any>
    ) {
        if (bookId != null) {
            db.collection("users").document(userId).collection("books").document(bookId)
                .update(book)
                .addOnSuccessListener {
                    Toast.makeText(this, "Livro atualizado com sucesso.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, BooksMain::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Erro ao atualizar o livro", e)
                    Toast.makeText(this, "Erro ao atualizar o livro: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.w(TAG, "ID do livro é nulo.")
            Toast.makeText(this, "Erro ao tentar atualizar o livro.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "EditBook"
    }
}
