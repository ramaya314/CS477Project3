package gmu.cs477.project2.data.models

import android.database.Cursor
import android.util.Log
import gmu.cs477.project2.data.db.DatabaseContract
import java.util.Date

data class Order(
    val orderDate: Date = Date(),
    var userId: String? = null
) {

    var _id: Int = 0
        private set

    companion object {
        const val LOG_CAT = "Order"

        fun fromCursor(cursor: Cursor): Order? {
            return try {
                Order(
                    orderDate = Date(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.Order.COLUMN_DATE)))
                ).apply {
                    _id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.Order.COLUMN_ID))
                }
            } catch (ex: Exception) {
                Log.e(LOG_CAT, "fromCursor() : EXCEPTION : ${ex.message}")
                null
            }
        }
    }
}