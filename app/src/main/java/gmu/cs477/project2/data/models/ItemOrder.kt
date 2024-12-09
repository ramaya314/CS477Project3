package gmu.cs477.project2.data.models

import android.database.Cursor
import android.util.Log
import gmu.cs477.project2.data.db.DatabaseContract

data class ItemOrder(
    var itemId: Int = 0,
    var orderId: Int = 0,
    var quantity: Int = 0
) {

    var _id: Int = 0
        private set

    companion object {
        const val LOG_CAT = "ItemOrder"

        fun fromCursor(cursor: Cursor): ItemOrder? {
            return try {
                ItemOrder(
                    itemId =    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ItemOrder.COLUMN_ITEM_ID)),
                    orderId =   cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ItemOrder.COLUMN_ORDER_ID)),
                    quantity =  cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ItemOrder.COLUMN_QUANTITY))
                ).apply {
                    _id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ItemOrder.COLUMN_ID))
                }
            } catch (ex: Exception) {
                Log.e(LOG_CAT, "fromCursor() : EXCEPTION : ${ex.message}")
                null
            }
        }
    }
}