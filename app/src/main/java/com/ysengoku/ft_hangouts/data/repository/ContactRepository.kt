package com.ysengoku.ft_hangouts.data.repository

import android.content.ContentValues
import com.ysengoku.ft_hangouts.data.DatabaseHelper
import com.ysengoku.ft_hangouts.data.getStringOrNull
import com.ysengoku.ft_hangouts.data.model.Contact
import com.ysengoku.ft_hangouts.data.model.ContactSummary
import java.time.LocalDate

class ContactRepository(private val dbHelper: DatabaseHelper) {
    private val tableName = "contacts"

    fun getPage(limit: Int = 10, offset: Int = 0): List<ContactSummary> {
        val query = """
            SELECT id, first_name, last_name, picture
            FROM ${tableName}
            ORDER BY first_name ASC
            LIMIT ? OFFSET ?
        """.trimIndent()

        val cursor = dbHelper.readableDatabase.rawQuery(
            query,
            arrayOf(limit.toString(), offset.toString())
        )

        val res = mutableListOf<ContactSummary>()
        cursor.use {
            while (it.moveToNext()) {
                res.add(
                    ContactSummary(
                        id = it.getLong(it.getColumnIndexOrThrow("id")),
                        firstName = it.getString(it.getColumnIndexOrThrow   ("first_name")),
                        lastName = it.getString(it.getColumnIndexOrThrow    ("last_name")),
                        picture = it.getStringOrNull(it.getColumnIndexOrThrow("picture"))
                    )
                )
            }
        }
        return res
    }

    fun getById(id: Long): Contact? {
        val query = """
            SELECT *
            FROM ${tableName}
            WHERE id = ?
        """.trimIndent()

        val cursor = dbHelper.readableDatabase.rawQuery(
            query,
            arrayOf(id.toString())
        )

        cursor.use {
            if (!it.moveToFirst()) {
              return null
            }
            return Contact(
                id = it.getLong(it.getColumnIndexOrThrow("id")),
                firstName = it.getString(it.getColumnIndexOrThrow("first_name")),
                lastName = it.getString(it.getColumnIndexOrThrow("last_name")),
                company = it.getStringOrNull(it.getColumnIndexOrThrow("company")),
                phone = it.getString(it.getColumnIndexOrThrow("phone")),
                address = it.getStringOrNull(it.getColumnIndexOrThrow("address")),
                birthday = it.getStringOrNull(it.getColumnIndexOrThrow("birthday"))?.let { s -> LocalDate.parse(s) },
                note = it.getStringOrNull(it.getColumnIndexOrThrow("note")),
                picture = it.getStringOrNull(it.getColumnIndexOrThrow("picture")),
            )
        }
    }

    fun create(contact: Contact): Long {
        val id = dbHelper.writableDatabase.insert(tableName, null, contact.toContentValues())
        return id
    }

    fun update(contact: Contact): Int {
        return dbHelper.writableDatabase.update(
            tableName,
            contact.toContentValues(),
            "id = ?",
            arrayOf(contact.id.toString())
        )
    }

    fun delete(id: Long): Int {
        return dbHelper.writableDatabase.delete(
            tableName,
            "id = ?",
            arrayOf(id.toString())
        )
    }

    private fun Contact.toContentValues() = ContentValues().apply {
        put("first_name", firstName)
        put("last_name" , lastName)
        put("company", company)
        put("phone", phone)
        put("address", address)
        put("birthday", birthday?.toString())
        put("note", note)
        put("picture", picture)
    }
}
