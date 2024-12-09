package gmu.cs477.project2.interfaces

import gmu.cs477.project2.data.models.Item

interface IItemRepository {
    suspend fun addItem(item: Item): Long
    suspend fun addItems(items: List<Item>)
    suspend fun updateItem(id: Int, item: Item): Int
    suspend fun deleteItem(itemId: Int): Boolean
    suspend fun getItemById(id: Int): Item?
    suspend fun getAllItems(): List<Item>
}