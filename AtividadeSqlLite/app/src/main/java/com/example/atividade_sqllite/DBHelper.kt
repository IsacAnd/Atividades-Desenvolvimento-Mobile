package com.example.atividade_sqllite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        // Executa uma query para criar tabela
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, " +
                NAME_COl + " TEXT," +
                AGE_COL + " TEXT" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        //Deleta uma tabela
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addName(name : String, age : String ){
        //Adicionar valores de nome idade a tabela
        val values = ContentValues()
        values.put(NAME_COl, name)
        values.put(AGE_COL, age)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getName(): Cursor? {
        //Ler todo os valores da tabela
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
    }

    fun updateUser(oldName: String, oldAge: String, newName: String, newAge: String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(NAME_COl, newName)
            put(AGE_COL, newAge)
        }

        // Critério de seleção: nome e idade antigos
        val selection = "$NAME_COl = ? AND $AGE_COL = ?"
        val selectionArgs = arrayOf(oldName, oldAge)

        // Atualiza e retorna o número de linhas afetadas
        return db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }

    companion object {
        private val DATABASE_NAME = "GEEKS_FOR_GEEKS"
        private val DATABASE_VERSION = 1
        val TABLE_NAME = "gfg_table"
        val ID_COL = "id"
        val NAME_COl = "name"
        val AGE_COL = "age"
    }

}
