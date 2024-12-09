package gmu.cs477.project2.interfaces

import gmu.cs477.project2.data.models.ItemOrder
import gmu.cs477.project2.data.models.Order

interface IOrderRepository {
    suspend fun addOrder(order: Order): Long
    suspend fun deleteOrder(orderId: Int): Boolean
    suspend fun addItemToOrder(itemOrder: ItemOrder): Long
    suspend fun deleteItemFromOrder(orderId: Int, itemId: Int): Boolean
    suspend fun deleteItemFromOrder(itemOrderId: Int): Boolean
    suspend fun getAllItemsOnOrder(orderId: Int): List<ItemOrder>
}