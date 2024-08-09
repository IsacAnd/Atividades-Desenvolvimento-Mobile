package com.example.atividade_sqllite

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtAge: EditText
    private lateinit var txtName: TextView
    private lateinit var txtAge: TextView
    private lateinit var searchButton: Button
    private lateinit var printButton: Button
    private lateinit var addButton: Button
    private lateinit var editButton: Button

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

        edtName = findViewById(R.id.edtName)
        edtAge = findViewById(R.id.edtAge)
        txtName = findViewById(R.id.txtName)
        txtAge = findViewById(R.id.txtAge)
        searchButton = findViewById(R.id.searchButton)
        printButton = findViewById(R.id.printButton)
        addButton = findViewById(R.id.addButton)
        editButton = findViewById(R.id.editButton)

        addButton.setOnClickListener {
            addName()
        }

        searchButton.setOnClickListener {
            search()
        }

        printButton.setOnClickListener {
            printName()
        }

        editButton.setOnClickListener {
            edit()
        }
    }

    @SuppressLint("Range")
    fun edit() {
        val db = DBHelper(this)
        val cursor = db.getName()
        cursor!!.moveToFirst()

        if (cursor!!.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl)).toString()
                val age = cursor.getString(cursor.getColumnIndex(DBHelper.AGE_COL)).toString()

                if (edtName.text.toString() == name) {
                    val intent = Intent(this, MainActivity2::class.java).apply {
                        putExtra("USER_NAME", name)
                        putExtra("USER_AGE", age)
                    }
                    startActivity(intent)
                    return
                }
            } while (cursor.moveToNext())
        }

        Toast.makeText(this,"Nome não encontrado!", Toast.LENGTH_LONG).show()
    }

    @SuppressLint("Range")
    fun search() {
        val db = DBHelper(this)
        val cursor = db.getName()
        var flag = true

        if (cursor!!.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl)).toString()

                if (edtName.text.toString() == name) {
                    txtName.text = null
                    txtAge.text = null
                    txtName.append(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl)) + "\n")
                    txtAge.append(cursor.getString(cursor.getColumnIndex(DBHelper.AGE_COL)) + "\n")
                    flag = false
                }
            } while (cursor.moveToNext())
        }

        clearField(edtName)
        clearField(edtAge)

        if (flag) Toast.makeText(this,"Nome não encontrado!", Toast.LENGTH_LONG).show()
        cursor.close()
        return
    }

    @SuppressLint("Range")
    fun printName(){
        val db = DBHelper(this)
        val cursor = db.getName()
        cursor!!.moveToFirst()
        txtName.append(cursor.getString
            (cursor.getColumnIndex(DBHelper.NAME_COl)) + "\n")
        txtAge.append(cursor.getString
            (cursor.getColumnIndex(DBHelper.AGE_COL)) + "\n")
        while(cursor.moveToNext()){
            txtName.append(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl)) + "\n")
            txtAge.append(cursor.getString
                (cursor.getColumnIndex(DBHelper.AGE_COL)) + "\n")
        }
        cursor.close()
    }

    private fun addName(){
        val db = DBHelper(this)
        val name = edtName.text.toString()
        val age = edtAge.text.toString()

        if (name.isEmpty() || name.isBlank()) {
            Toast.makeText(this, name + "Preencha o campo de nome!", Toast.LENGTH_LONG).show()
            return
        }

        db.addName(name, age)
        Toast.makeText(this, name + " Adicionado com sucesso!", Toast.LENGTH_LONG).show()
        clearField(edtName)
        clearField(edtAge)
    }

    private fun clearField(editText: EditText){
        editText.text.clear()
    }
}