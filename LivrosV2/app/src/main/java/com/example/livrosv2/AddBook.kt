package com.example.livrosv2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.livrosv2.databinding.ActivityAddBookBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AddBook : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAddBookBinding
    private var imageUri: Uri? = null
    private val imageContract = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            Toast.makeText(this, "Imagem adicionada com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnAddImage.setOnClickListener {
            imageContract.launch("image/*")
        }

        binding.btnAddBook.setOnClickListener {
            when {
                binding.edtTitle.text.isBlank() -> {
                    Toast.makeText(this, "Preencha o título do livro!", Toast.LENGTH_SHORT).show()
                }
                binding.edtPublisher.text.isBlank() -> {
                    Toast.makeText(this, "Preencha a editora do livro!", Toast.LENGTH_SHORT).show()
                }
                binding.edtGenre.text.isBlank() -> {
                    Toast.makeText(this, "Preencha o gênero do livro!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    addBook(
                        binding.edtTitle.text.toString(),
                        binding.edtPublisher.text.toString(),
                        binding.edtGenre.text.toString(),
                        binding.checkBox.isChecked,
                        binding.ratingBar2.rating
                    )
                }
            }
        }
    }

    private fun addBook(title: String, publisher: String, genre: String, isRead: Boolean, rating: Float) {
        val user = auth.currentUser
        if (user != null) {
            val db = Firebase.firestore
            val book: HashMap<String, Any?> = hashMapOf(
                "title" to title,
                "publisher" to publisher,
                "genre" to genre,
                "isRead" to isRead,
                "rating" to rating.toDouble(), // Explicita o tipo como Double
                "imageUrl" to null
            )

            if (imageUri != null) {
                val storageRef = Firebase.storage.reference
                val fileRef = storageRef.child("book_images/${System.currentTimeMillis()}.jpg")

                fileRef.putFile(imageUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                            book["imageUrl"] = uri.toString()
                            saveBookToFirestore(user.uid, book)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Erro ao fazer upload da imagem", e)
                        Toast.makeText(this, "Erro ao fazer upload da imagem.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                saveBookToFirestore(user.uid, book)
            }
        }
    }

    private fun saveBookToFirestore(userId: String, book: HashMap<String, Any?>) {
        val db = Firebase.firestore
        db.collection("users").document(userId).collection("books")
            .add(book)
            .addOnSuccessListener {
                Log.d(TAG, "Livro adicionado com sucesso")
                Toast.makeText(this, "Livro adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, BooksMain::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erro ao adicionar o livro", e)
                Toast.makeText(this, "Erro ao adicionar o livro.", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "AddBook"
    }
}
