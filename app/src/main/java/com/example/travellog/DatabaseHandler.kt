package com.example.travellog

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_VERSION = 1
        private const val DB_NAME = "RecordDatabase"
        private const val TABLE_NAME = "RecordTable"

        // Attributes of the record
        private const val PRIMARY_KEY = "_id"
        private const val CARD_TITLE = "title"
        private const val CARD_CONTINENT = "continent"
        private const val CARD_COUNTRY = "country"
        private const val CARD_DATE = "date"
        private const val CARD_TIME = "time"
        private const val ADDITIONAL_INFO = "additionalInfo"
        private const val CARD_IMAGE_PATH = "imgPath"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            ("CREATE TABLE " + TABLE_NAME + "(" + PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + CARD_IMAGE_PATH + " TEXT,"
                    + CARD_TITLE + " TEXT,"
                    + CARD_CONTINENT + " TEXT,"
                    + CARD_COUNTRY + " TEXT,"
                    + CARD_DATE + " TEXT,"
                    + CARD_TIME + " TEXT,"
                    + ADDITIONAL_INFO + " TEXT" + ")")

        db?.execSQL(createTable)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVer: Int, newVer: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Function to add data
    fun addRecord(record: RecordModel): Long {
        val db = this.writableDatabase

        val content = ContentValues()
        content.put(CARD_IMAGE_PATH, record.imgPath)
        content.put(CARD_TITLE, record.title)
        content.put(CARD_CONTINENT, record.continent)
        content.put(CARD_COUNTRY, record.country)
        content.put(CARD_DATE, record.date)
        content.put(CARD_TIME, record.time)
        content.put(ADDITIONAL_INFO, record.additionalInfo)

        // Insertion into database
        val insertion = db.insert(TABLE_NAME, null, content)
        db.close()
        return insertion
    }

    // Function to read data
    fun viewRecord(): ArrayList<RecordModel> {
        val recordList: ArrayList<RecordModel> = ArrayList<RecordModel>()

        val sqlQuery = "SELECT * FROM $TABLE_NAME"

        val db = this.readableDatabase
        var cursor: Cursor?

        try {
            cursor = db.rawQuery(sqlQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(sqlQuery)
            return ArrayList()

        }

        var id: Int
        var imgPath: String
        var title: String
        var continent: String
        var country: String
        var date: String
        var time: String
        var additionalInfo: String

        if(cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(PRIMARY_KEY))
                imgPath = cursor.getString(cursor.getColumnIndex(CARD_IMAGE_PATH))
                title = cursor.getString(cursor.getColumnIndex(CARD_TITLE))
                continent = cursor.getString(cursor.getColumnIndex(CARD_CONTINENT))
                country = cursor.getString(cursor.getColumnIndex(CARD_COUNTRY))
                date = cursor.getString(cursor.getColumnIndex(CARD_DATE))
                time = cursor.getString(cursor.getColumnIndex(CARD_TIME))
                additionalInfo = cursor.getString(cursor.getColumnIndex(ADDITIONAL_INFO))

                val record = RecordModel(id = id, imgPath = imgPath, title = title, continent = continent, country = country, date = date, time = time, additionalInfo = additionalInfo)
                recordList.add(record)
            } while(cursor.moveToNext())
        }
        return recordList
    }

    // Function to update data
    fun updateRecord(record: RecordModel): Int {
        val db = this.writableDatabase
        val content = ContentValues()
        content.put(CARD_IMAGE_PATH, record.imgPath)
        content.put(CARD_TITLE, record.title)
        content.put(CARD_CONTINENT, record.continent)
        content.put(CARD_COUNTRY, record.country)
        content.put(CARD_DATE, record.date)
        content.put(CARD_TIME, record.time)
        content.put(ADDITIONAL_INFO, record.additionalInfo)

        val update = db.update(TABLE_NAME, content, PRIMARY_KEY + "=" + record.id, null)

        db.close()
        return update
    }

    // Function to delete data
    fun deleteRecord(record: RecordModel): Int {
        val db = this.writableDatabase
        val content = ContentValues()
        content.put(PRIMARY_KEY, record.id)

        // Delete Row
        val delete = db.delete(TABLE_NAME, PRIMARY_KEY + "=" + record.id, null)

        db.close()
        return delete
    }

}