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
            "last_name TEXT," +
            "company TEXT," +
            "phone TEXT NOT NULL UNIQUE," +
            "phone_country TEXT NOT NULL," +
            "address TEXT," +
            "birthday TEXT," +
            "note TEXT," +
            "picture TEXT )"

        private const val CREATE_MESSAGES =
            "CREATE TABLE ${TABLE.MESSAGES} (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "contact_id INTEGER NOT NULL REFERENCES ${TABLE.CONTACTS}(id) ON DELETE CASCADE," +
            "is_incoming INTEGER NOT NULL," +
            "created_at INTEGER NOT NULL," +
            "content TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_CONTACTS)
        db.execSQL(CREATE_MESSAGES)
    }

    /**
     * Apply migrations step by step when DATABASE_VERSION is increased,
     * for example:
     * if (oldVersion < 2) {
     *     db.execSQL("ALTER TABLE contacts ADD COLUMN email TEXT")
     * }
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }
}
