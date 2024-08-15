package com.example.contatos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ContactInterface : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_contact_interface)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = intent.getStringExtra("name").toString()
        val email = intent.getStringExtra("email").toString()
        val number = intent.getStringExtra("number").toString()
        val extras = intent.extras
        val id: Int = extras!!.getInt("id")
//
        val txtName: TextView = findViewById(R.id.txtName)
        val txtEmail: TextView = findViewById(R.id.txtEmail)
        val txtNumber: TextView = findViewById(R.id.txtNumber)

        txtName.append(name)
        txtEmail.append(email)
        txtNumber.append(number)

        val delBtn: Button = findViewById(R.id.delete)
        val editBtn: Button = findViewById(R.id.edit)

        delBtn.setOnClickListener {
            deleteContact()
        }

        editBtn.setOnClickListener {
            editContact(name, email, number, id)
        }
    }

    private fun deleteContact() {
        val db = DBHelper(this)
        val extras = intent.extras
        val id: Int = extras!!.getInt("id")

        val result = db.deleteContact(id)
        if (result > 0) {
            Toast.makeText(this, "Usuário deletado com sucesso!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Não foi possível excluir o contato!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editContact(name: String, email: String, number: String, id: Int) {
        val intent = Intent(this, EditContact::class.java)

        intent.putExtra("name", name)
        intent.putExtra("email", email)
        intent.putExtra("number", number)
        intent.putExtra("id", id)

        return startActivity(intent)
    }
}