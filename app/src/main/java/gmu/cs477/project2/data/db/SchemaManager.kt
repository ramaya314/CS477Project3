package gmu.cs477.project2.data.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class SchemaManager {
    fun createTables(db: SQLiteDatabase) {
        val itemTableCommand = "CREATE TABLE ${DatabaseContract.Item.TABLE_NAME} (" +
                "${DatabaseContract.Item.COLUMN_ID}             INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${DatabaseContract.Item.COLUMN_NAME}           TEXT NOT NULL, " +
                "${DatabaseContract.Item.COLUMN_DESCRIPTION}    TEXT, " +
                "${DatabaseContract.Item.COLUMN_COST}           REAL NOT NULL," +
                "${DatabaseContract.Item.COLUMN_STOCK}          INTEGER NOT NULL)"
        db.execSQL(itemTableCommand)

        val orderTableCommand = "CREATE TABLE ${DatabaseContract.Order.TABLE_NAME} (" +
                "${DatabaseContract.Order.COLUMN_ID}            INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${DatabaseContract.Order.COLUMN_DATE}          INTEGER)"
        db.execSQL(orderTableCommand)

        val itemOrderTableCommand = "CREATE TABLE ${DatabaseContract.ItemOrder.TABLE_NAME} (" +
                "${DatabaseContract.ItemOrder.COLUMN_ID}        INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${DatabaseContract.ItemOrder.COLUMN_ITEM_ID}   INTEGER NOT NULL, " +
                "${DatabaseContract.ItemOrder.COLUMN_ORDER_ID}  INTEGER NOT NULL, " +
                "${DatabaseContract.ItemOrder.COLUMN_QUANTITY}  INTEGER NOT NULL)"
        db.execSQL(itemOrderTableCommand)
    }

    fun insertInitialData(db: SQLiteDatabase) {
        val items = listOf(
            ContentValues().apply {
                put(DatabaseContract.Item.COLUMN_NAME, "Chocolate Delight")
                put(DatabaseContract.Item.COLUMN_DESCRIPTION, "These chocolate things will delight you a lot")
                put(DatabaseContract.Item.COLUMN_COST, 2.99f)
                put(DatabaseContract.Item.COLUMN_STOCK, 25)
            },
            ContentValues().apply {
                put(DatabaseContract.Item.COLUMN_NAME, "Melty Mints")
                put(DatabaseContract.Item.COLUMN_DESCRIPTION, "These mints are super melty")
                put(DatabaseContract.Item.COLUMN_COST, 1.99f)
                put(DatabaseContract.Item.COLUMN_STOCK, 105)
            },
            ContentValues().apply {
                put(DatabaseContract.Item.COLUMN_NAME, "Peanut Butter Treasure")
                put(DatabaseContract.Item.COLUMN_DESCRIPTION, "These peanut butter things are a treasure to behold")
                put(DatabaseContract.Item.COLUMN_COST, 3.99f)
                put(DatabaseContract.Item.COLUMN_STOCK, 5)
            }
        )

        items.forEach { contentValues ->
            db.insert(DatabaseContract.Item.TABLE_NAME, null, contentValues)
        }
    }

    fun dropTables(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.Item.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.Order.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.ItemOrder.TABLE_NAME}")
    }
}