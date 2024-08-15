package com.example.contatos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class EditContact : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_contact)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val edtName: EditText = findViewById(R.id.editName)
        val edtEmail: EditText = findViewById(R.id.editEmail)
        val edtNumber: EditText = findViewById(R.id.editNumber)
        val editBtn: Button = findViewById(R.id.editBtn)

        val name = intent.getStringExtra("name").toString()
        val email = intent.getStringExtra("email").toString()
        val number = intent.getStringExtra("number").toString()
        val extras = intent.extras
        val id: Int = extras!!.getInt("id")

        edtName.setText(name)
        edtEmail.setText(email)
        edtNumber.setText(number)

        editBtn.setOnClickListener {
            editContact(id)
        }
    }

    private fun editContact(id: Int) {
        val db = DBHelper(this)
        val intent = Intent(this, MainActivity::class.java)
        val edtName: EditText = findViewById(R.id.editName)
        val edtEmail: EditText = findViewById(R.id.editEmail)
        val edtNumber: EditText = findViewById(R.id.editNumber)

        val ret = db.updateContact(id, edtName.text.toString(), edtEmail.text.toString(), edtNumber.text.toString())

        if (ret > 0) {
            Toast.makeText(this, "Contato editado com sucesso!", Toast.LENGTH_SHORT)
            return startActivity(intent)
        }

        return startActivity(intent)
    }
}