package com.ysengoku.ft_hangouts.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME , null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "ft_hangouts.db"
        private const val DATABASE_VERSION = 1

        object TABLE {
            const val CONTACTS = "contacts"
            const val MESSAGES = "messages"
        }

        private const val CREATE_CONTACTS =
            "CREATE TABLE ${TABLE.CONTACTS} (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT" +
                    "firstName TEXT NOT NULL" +
                    "lastName TEXT NOT NULL" +
                    "company TEXT" +
                    "phone TEXT NOT NULL" +
                    "address TEXT" +
                    "birthday TEXT" +
                    "note TEXT" +
                    "picture TEXT )"

        private const val CREATE_MESSAGES =
            "CREATE TABLE ${TABLE.MESSAGES} (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT" +
                    "contactId: INTENGER" +
                    "isIncoming: INTEGER" +
                    "createdAt: INTEGER" +
                    "content: TEXT )"
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
