package gmu.cs477.project2.data.db.repositories.firebase

import com.google.firebase.auth.FirebaseAuth
import gmu.cs477.project2.data.models.Item
import gmu.cs477.project2.interfaces.IItemRepository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ItemRepository: IItemRepository {

    private val db = FirebaseFirestore.getInstance()
    private val itemsCollection = db.collection("items")


    override suspend fun addItem(item: Item): Long {

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        item.userId = userId

        val nameIsAvailable = itemsCollection
            .whereEqualTo("name", item.name)
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .isEmpty

        if (!nameIsAvailable) {
            throw IllegalArgumentException("You cannot add items with duplicate names.")
        }

        return try {
            val documentRef = itemsCollection.add(item).await()
            val newId = documentRef.id.hashCode().toLong() // Generate a unique integer ID based on document ID hash
            itemsCollection.document(documentRef.id).update("_id", newId).await() // Update the document with the generated ID
            newId
        } catch (e: Exception) {
            e.printStackTrace()
            -1L
        }
    }

    override suspend fun addItems(items: List<Item>) {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            items.forEach { item ->
                item.userId = userId
                val documentRef = itemsCollection.add(item).await()
                val newId = documentRef.id.hashCode().toLong()
                itemsCollection.document(documentRef.id).update("_id", newId).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateItem(id: Int, item: Item): Int {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            item.userId = userId
            val documentId = itemsCollection.whereEqualTo("_id", id).get().await().documents.firstOrNull()?.id
                ?: return -1
            itemsCollection.document(documentId).set(item).await()
            1 // Return 1 for success
        } catch (e: Exception) {
            e.printStackTrace()
            -1 // Return -1 for failure
        }
    }

    override suspend fun deleteItem(itemId: Int): Boolean {
        return try {
            val documentId = itemsCollection.whereEqualTo("_id", itemId).get().await().documents.firstOrNull()?.id
                ?: return false
            itemsCollection.document(documentId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getItemById(id: Int): Item? {
        return try {
            val document = itemsCollection.whereEqualTo("_id", id).get().await().documents.firstOrNull()
            document?.toObject(Item::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getAllItems(): List<Item> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val query = itemsCollection.whereEqualTo("userId", userId).get().await()
            query.toObjects(Item::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}