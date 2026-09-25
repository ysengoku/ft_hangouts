package com.ysengoku.ft_hangouts.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME , null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "ft_hangouts.db"
        private const val DATABASE_VERSION = 1

        @Volatile // ensures writes to `instance` are visible to all threads immediately (double-checked locking)
        private var instance: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper =
            instance ?: synchronized(this) {
                instance ?: DatabaseHelper(context.applicationContext).also { instance = it }
            }

        object TABLE {
            const val CONTACTS = "contacts"
            const val MESSAGES = "messages"
        }

        private const val CREATE_CONTACTS =
            "CREATE TABLE ${TABLE.CONTACTS} (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "first_name TEXT NOT NULL," +
            "last_name TEXT NOT NULL," +
            "company TEXT," +
            "phone TEXT NOT NULL," +
            "address TEXT," +
            "birthday TEXT," +
            "note TEXT," +
            "picture TEXT )"

        private const val CREATE_MESSAGES =
            "CREATE TABLE ${TABLE.MESSAGES} (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "contact_id INTEGER REFERENCES ${TABLE.CONTACTS}(id) ON DELETE CASCADE," +
            "is_incoming INTEGER," +
            "created_at INTEGER," +
            "content TEXT )"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_CONTACTS)
        db.execSQL(CREATE_MESSAGES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS messages")
        db.execSQL("DROP TABLE IF EXISTS contacts")
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }
}
