package com.example.contatos

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddContact : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtNumber: EditText
    private lateinit var imageView: ImageView

    private var selectedImageUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            // Aqui você pode fazer algo com a imagem, como exibi-la em um ImageView
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_contact2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val addBtn: Button = findViewById(R.id.button)

        addBtn.setOnClickListener() {
            addContact()
        }
    }

    private fun addContact() {
        val db = DBHelper(this)
        val intent = Intent(this, MainActivity::class.java)
        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtNumber = findViewById(R.id.edtNumber)
        selectedImageUri?.toString()

        val ret = db.addContact(edtName.text.toString(), edtNumber.text.toString(), edtEmail.text.toString())

        if (ret != -1L) {
            Toast.makeText(this, "Contato salvo com sucesso!", Toast.LENGTH_SHORT).show()
            return startActivity(intent)
        }

        return Toast.makeText(this, "Contato não foi salvo!", Toast.LENGTH_SHORT).show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_IMAGE)
    }

    companion object {
        private const val REQUEST_CODE_IMAGE = 1001
    }
}