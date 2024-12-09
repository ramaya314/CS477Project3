package gmu.cs477.project2.data.models

import android.database.Cursor
import android.util.Log
import gmu.cs477.project2.data.db.DatabaseContract

data class Item(
    var name: String? = null,
    var description: String = "",
    var cost: Float = 0.0f,
    var stock: Int = 0,
    var userId: String? = null
) {

    var _id: Int = 0
        private set

    companion object {
        const val LOG_CAT = "Item"

        fun fromCursor(cursor: Cursor): Item? {
            return try {
                Item(
                    name =          cursor.getString(   cursor.getColumnIndexOrThrow(DatabaseContract.Item.COLUMN_NAME)),
                    description =   cursor.getString(   cursor.getColumnIndexOrThrow(DatabaseContract.Item.COLUMN_DESCRIPTION)),
                    cost =          cursor.getFloat(    cursor.getColumnIndexOrThrow(DatabaseContract.Item.COLUMN_COST)),
                    stock =         cursor.getInt(      cursor.getColumnIndexOrThrow(DatabaseContract.Item.COLUMN_STOCK))
                ).apply {
                    _id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.Item.COLUMN_ID))
                }
            } catch (ex: Exception) {
                Log.e(LOG_CAT, "fromCursor() : EXCEPTION : ${ex.message}")
                null
            }
        }
    }
}