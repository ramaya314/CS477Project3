package gmu.cs477.project2.data.db

object DatabaseContract {

    const val DATABASE_NAME = "item_store.db"
    const val DATABASE_VERSION = 2

    object Item {
        const val TABLE_NAME = "items"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_COST = "cost"
        const val COLUMN_STOCK = "stock"
    }

    object Order {
        const val TABLE_NAME = "orders"
        const val COLUMN_ID = "_id"
        const val COLUMN_DATE = "date"
    }

    object ItemOrder {
        const val TABLE_NAME = "itemOrders"
        const val COLUMN_ID = "_id"
        const val COLUMN_ITEM_ID = "itemId"
        const val COLUMN_ORDER_ID = "orderId"
        const val COLUMN_QUANTITY = "quantity"
    }

}