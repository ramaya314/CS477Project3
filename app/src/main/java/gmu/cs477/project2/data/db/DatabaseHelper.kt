package gmu.cs477.project2.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import javax.inject.Inject

class DatabaseHelper @Inject constructor(context: Context)
    : SQLiteOpenHelper(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION) {

    private val schemaManager: SchemaManager by lazy { SchemaManager() }
    override fun onCreate(db: SQLiteDatabase) {
        schemaManager.createTables(db)
        schemaManager.insertInitialData(db)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        schemaManager.dropTables(db)
        onCreate(db)
    }
}