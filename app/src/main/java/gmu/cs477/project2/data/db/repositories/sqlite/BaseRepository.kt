package gmu.cs477.project2.data.db.repositories.sqlite

import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import gmu.cs477.project2.data.db.DatabaseHelper

abstract class BaseRepository(protected val dbHelper: DatabaseHelper) {

    protected abstract val LOG_CAT: String

    protected fun insert(tableName: String, contentValues: ContentValues): Long {
        return try {
            dbHelper.writableDatabase.use { db ->
                db.insert(tableName, null, contentValues)
            }
        } catch (ex: Exception) {
            Log.e(LOG_CAT, "insert() EXCEPTION: ${ex.message}")
            -1 //failed operation
        }
    }

    //batch insert for efficiency
    protected fun insertMany(tableName: String, items: List<ContentValues>) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            items.forEach { contentValues ->
                db.insert(tableName, null, contentValues)
            }
            db.setTransactionSuccessful()
        } catch (ex: Exception) {
            Log.e(LOG_CAT, "insertMany() EXCEPTION: ${ex.message}")
        } finally {
            db.endTransaction()
        }
    }

    protected fun update(tableName: String, contentValues: ContentValues, whereClause: String, whereArgs: Array<String>): Int {
        return try {
            dbHelper.writableDatabase.use { db ->
                db.update(tableName, contentValues, whereClause, whereArgs)
            }
        } catch (ex: Exception) {
            Log.e(LOG_CAT, "update() EXCEPTION: ${ex.message}")
            0 //no items updated
        }
    }

    protected fun delete(tableName: String, whereClause: String, whereArgs: Array<String>): Int {
        return try {
            dbHelper.writableDatabase.use { db ->
                db.delete(tableName, whereClause, whereArgs)
            }
        } catch (ex: Exception) {
            Log.e(LOG_CAT, "delete() EXCEPTION: ${ex.message}")
            0 //no items deleted
        }
    }

    protected fun <T> getSingle(tableName: String, selection: String, selectionArgs: Array<String>, cursorToObject: (Cursor) -> T?): T? {
        return try {
            dbHelper.readableDatabase.use { db ->
                db.query(tableName, null, selection, selectionArgs, null, null, null).use { cursor ->
                    if (cursor.moveToFirst()) {
                        return cursorToObject(cursor)
                    }
                }
            }
            null
        } catch (ex: Exception) {
            Log.e(LOG_CAT, "getSingle() EXCEPTION: ${ex.message}")
            null
        }
    }

    protected fun <T> getMany(tableName: String, selection: String?, selectionArgs: Array<String>?, cursorToObject: (Cursor) -> T?): List<T> {
        val results = mutableListOf<T>()
        try {
            dbHelper.readableDatabase.use { db ->
                db.query(tableName, null, selection, selectionArgs, null, null, null).use { cursor ->
                    while (cursor.moveToNext()) {
                        cursorToObject(cursor)?.let { results.add(it) }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(LOG_CAT, "getMany() EXCEPTION: ${ex.message}")
        }
        return results
    }
}
