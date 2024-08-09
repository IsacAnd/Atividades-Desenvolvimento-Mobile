package com.example.atividade_sqllite

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity2 : AppCompatActivity() {

    private lateinit var edtNome: EditText
    private  lateinit var edtIdade: EditText
    private lateinit var btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btn = findViewById(R.id.button)

        btn.setOnClickListener {
            editUser()
        }
    }

    private fun editUser() {
        val db = DBHelper(this)
        edtNome = findViewById(R.id.edtNome)
        edtIdade = findViewById(R.id.edtIdade)
        val userName = intent.getStringExtra("USER_NAME") ?: "Nome não fornecido"
        val userAge = intent.getStringExtra("USER_AGE") ?: "Idade não fornecida"

        if (edtNome.toString().isNullOrEmpty()) {
            Toast.makeText(this,"Nome não pode estar vazio!", Toast.LENGTH_LONG).show()
        }

        val res = db.updateUser(userName, userAge, edtNome.text.toString(), edtIdade.text.toString())

        if (res > 0) {
            Toast.makeText(this,"Dados editados com sucesso!", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}