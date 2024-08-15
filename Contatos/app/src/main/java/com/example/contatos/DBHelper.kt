package com.example.contatos

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ContactsDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_CONTACTS = "Contacts"

        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_NUMBER = "number"
        private const val COLUMN_EMAIL = "email"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_CONTACTS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_NAME TEXT,"
                + "$COLUMN_NUMBER TEXT,"
                + "$COLUMN_EMAIL TEXT,"+")")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    // Função para adicionar contato
    fun addContact(name: String, number: String, email: String): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_NUMBER, number)
            put(COLUMN_EMAIL, email)
        }
        return db.insert(TABLE_CONTACTS, null, values).also {
            db.close()
        }
    }

    // Função para atualizar contato
    fun updateContact(id: Int, name: String?, number: String?, email: String?): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            if (name != null) put(COLUMN_NAME, name)
            if (number != null) put(COLUMN_NUMBER, number)
            if (email != null) put(COLUMN_EMAIL, email)
        }
        return db.update(TABLE_CONTACTS, values, "$COLUMN_ID=?", arrayOf(id.toString())).also {
            db.close()
        }
    }

    // Função para excluir contato
    fun deleteContact(id: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_CONTACTS, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close() // Fecha o banco de dados após a operação
        return result
    }

    fun getAllContacts(): MutableList<Contact> {
        val contactList = ArrayList<Contact>()
        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery(selectQuery, null)

        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    val contact = Contact(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        number = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NUMBER)),
                        email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    )
                    contactList.add(contact)
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        db.close()
        return contactList
    }


}