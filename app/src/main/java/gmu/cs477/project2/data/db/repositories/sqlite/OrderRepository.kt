package gmu.cs477.project2.data.db.repositories.sqlite

import android.content.ContentValues

import gmu.cs477.project2.data.db.DatabaseHelper
import gmu.cs477.project2.data.models.ItemOrder
import gmu.cs477.project2.data.models.Order
import gmu.cs477.project2.data.db.DatabaseContract
import gmu.cs477.project2.interfaces.IOrderRepository
import javax.inject.Inject

class OrderRepository @Inject constructor(dbHelper: DatabaseHelper) : BaseRepository(dbHelper), IOrderRepository {
    override val LOG_CAT: String = "OrderRepository"

    override suspend fun addOrder(order: Order): Long {
        val cv = ContentValues().apply {
            put(DatabaseContract.Order.COLUMN_DATE, order.orderDate.time)
        }
        return insert(DatabaseContract.Order.TABLE_NAME, cv)
    }

    override suspend fun deleteOrder(orderId: Int): Boolean {
        return delete(DatabaseContract.Order.TABLE_NAME, "${DatabaseContract.Order.COLUMN_ID}=?", arrayOf(orderId.toString())) > 0
    }

    override suspend fun addItemToOrder(itemOrder: ItemOrder): Long {
        val cv = ContentValues().apply {
            put(DatabaseContract.ItemOrder.COLUMN_ITEM_ID, itemOrder.itemId)
            put(DatabaseContract.ItemOrder.COLUMN_ORDER_ID, itemOrder.orderId)
            put(DatabaseContract.ItemOrder.COLUMN_QUANTITY, itemOrder.quantity)
        }
        return insert(DatabaseContract.ItemOrder.TABLE_NAME, cv)
    }

    override suspend fun deleteItemFromOrder(orderId: Int, itemId: Int): Boolean {
        return delete(DatabaseContract.ItemOrder.TABLE_NAME, "${DatabaseContract.ItemOrder.COLUMN_ORDER_ID}=? AND ${DatabaseContract.ItemOrder.COLUMN_ITEM_ID}=?", arrayOf(orderId.toString(), itemId.toString())) > 0
    }

    override suspend fun deleteItemFromOrder(itemOrderId: Int): Boolean {
        return delete(DatabaseContract.ItemOrder.TABLE_NAME, "${DatabaseContract.ItemOrder.COLUMN_ID}=?", arrayOf(itemOrderId.toString())) > 0
    }

    override suspend fun getAllItemsOnOrder(orderId: Int): List<ItemOrder> {
        return getMany(DatabaseContract.ItemOrder.TABLE_NAME, "${DatabaseContract.ItemOrder.COLUMN_ORDER_ID}=?", arrayOf(orderId.toString())) { cursor ->
            ItemOrder.fromCursor(cursor)
        }
    }
}