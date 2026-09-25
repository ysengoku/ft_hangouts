package com.ysengoku.ft_hangouts.data.repository

import android.content.ContentValues
import com.ysengoku.ft_hangouts.data.DatabaseHelper
import com.ysengoku.ft_hangouts.data.getStringOrNull
import com.ysengoku.ft_hangouts.data.model.Message

class MessageRepository(private val dbHelper: DatabaseHelper) {
    private val tableName = "messages"

    fun getPageByContactId(contactId: Long, limit: Int = 10, offset: Int = 0): List<Message> {
        val query = """
            SELECT *
            FROM ${tableName}
            WHERE contact_id = ?
            ORDER BY created_at DESC
            LIMIT ? OFFSET ?
        """.trimIndent()

        val cursor = dbHelper.readableDatabase.rawQuery(
            query,
            arrayOf(contactId.toString(), limit.toString(), offset.toString())
        )

        val res = mutableListOf<Message>()
        cursor.use {
            while (it.moveToNext()) {
                res.add(
                    Message(
                        id = it.getLong(it.getColumnIndexOrThrow("id")),
                        contactId = it.getLong(it.getColumnIndexOrThrow("contact_id")),
                        isIncoming = it.getInt(it.getColumnIndexOrThrow("is_incoming")) != 0,
                        createdAt = it.getLong(it.getColumnIndexOrThrow("created_at")),
                        content = it.getString(it.getColumnIndexOrThrow("content"))
                    )
                )
            }
        }
        return res
    }

    fun create(message: Message): Long {
        val values = ContentValues().apply {
            put("contact_id", message.contactId)
            put("is_incoming", message.isIncoming)
            put("created_at", message.createdAt)
            put("content", message.content)
        }

        return dbHelper.writableDatabase.insert(tableName, null, values)
    }
}
