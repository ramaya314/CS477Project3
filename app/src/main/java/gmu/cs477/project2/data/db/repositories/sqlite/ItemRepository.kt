package gmu.cs477.project2.data.db.repositories.sqlite

import android.content.ContentValues

import gmu.cs477.project2.data.db.DatabaseHelper
import gmu.cs477.project2.data.models.Item
import gmu.cs477.project2.data.db.DatabaseContract
import gmu.cs477.project2.interfaces.IItemRepository
import javax.inject.Inject

class ItemRepository @Inject constructor(dbHelper: DatabaseHelper) : BaseRepository(dbHelper), IItemRepository {
    override val LOG_CAT: String = "ItemRepository"

    override suspend fun addItem(item: Item): Long {
        val cv = ContentValues().apply {
            put(DatabaseContract.Item.COLUMN_NAME,          item.name)
            put(DatabaseContract.Item.COLUMN_DESCRIPTION,   item.description)
            put(DatabaseContract.Item.COLUMN_COST,          item.cost)
            put(DatabaseContract.Item.COLUMN_STOCK,         item.stock)
        }
        return insert(DatabaseContract.Item.TABLE_NAME, cv)
    }

    override suspend fun addItems(items: List<Item>) {
        val contentValues = items.map { item ->
            ContentValues().apply {
                put(DatabaseContract.Item.COLUMN_NAME,          item.name)
                put(DatabaseContract.Item.COLUMN_DESCRIPTION,   item.description)
                put(DatabaseContract.Item.COLUMN_COST,          item.cost)
                put(DatabaseContract.Item.COLUMN_STOCK,         item.stock)
            }
        }
        insertMany(DatabaseContract.Item.TABLE_NAME, contentValues)
    }

    override suspend fun updateItem(id: Int, item: Item): Int {
        val cv = ContentValues().apply {
            put(DatabaseContract.Item.COLUMN_NAME,          item.name)
            put(DatabaseContract.Item.COLUMN_DESCRIPTION,   item.description)
            put(DatabaseContract.Item.COLUMN_COST,          item.cost)
            put(DatabaseContract.Item.COLUMN_STOCK,         item.stock)
        }
        return update(DatabaseContract.Item.TABLE_NAME, cv, "${DatabaseContract.Item.COLUMN_ID}=?", arrayOf(id.toString()))
    }

    override suspend fun deleteItem(itemId: Int): Boolean {
        return delete(DatabaseContract.Item.TABLE_NAME, "${DatabaseContract.Item.COLUMN_ID}=?", arrayOf(itemId.toString())) > 0
    }

    override suspend fun getItemById(itemId: Int): Item? {
        return getSingle(DatabaseContract.Item.TABLE_NAME, "${DatabaseContract.Item.COLUMN_ID}=?", arrayOf(itemId.toString())) { cursor ->
            Item.fromCursor(cursor)
        }
    }

    override suspend fun getAllItems(): List<Item> {
        return getMany(DatabaseContract.Item.TABLE_NAME, null, null) { cursor ->
            Item.fromCursor(cursor)
        }
    }
}
