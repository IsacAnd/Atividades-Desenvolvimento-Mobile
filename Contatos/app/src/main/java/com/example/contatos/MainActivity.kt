package com.example.contatos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var conAdapter: ContactAdapter

    override fun onResume() {
        super.onResume()
        updateContactList()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val cons = DBHelper(this).getAllContacts()
        conAdapter = ContactAdapter(cons) { con ->
            val intent = Intent(this, ContactInterface::class.java)
            intent.putExtra("name", con.name)
            intent.putExtra("email", con.email)
            intent.putExtra("number", con.number)
            intent.putExtra("id", con.id)
            startActivity(intent)
        }

        recyclerView.adapter = conAdapter

        val btnSearch: Button = findViewById(R.id.btnSearch)
        btnSearch.setOnClickListener {
            val editTextSearch: EditText = findViewById(R.id.searchView)
            val query = editTextSearch.text.toString()
            searchContacts(query)
        }

        val btn: Button = findViewById(R.id.button3)
        btn.setOnClickListener {
            add()
        }
    }

    private fun searchContacts(query: String) {
        val db = DBHelper(this)
        val contactList = db.getAllContacts()
        val filteredList = contactList.filter { con ->
            con.name.contains(query, ignoreCase = true) ||
                    con.number.contains(query, ignoreCase = true) ||
                    con.email.contains(query, ignoreCase = true)
        }
        conAdapter.updateList(filteredList)
    }

    private fun updateContactList() {
        val db = DBHelper(this)
        val cons = db.getAllContacts()
        conAdapter.updateList(cons)
        db.close()
    }

    private fun add() {
        val intent = Intent(this, AddContact::class.java)
        startActivity(intent)
    }
}
