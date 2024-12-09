package gmu.cs477.project2.data.db.repositories.firebase

import com.google.firebase.auth.FirebaseAuth
import gmu.cs477.project2.data.models.Item
import gmu.cs477.project2.data.models.ItemOrder
import gmu.cs477.project2.data.models.Order
import gmu.cs477.project2.interfaces.IOrderRepository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class OrderRepository : IOrderRepository {

    private val db = FirebaseFirestore.getInstance()
    private val ordersCollection = db.collection("orders")

    override suspend fun addOrder(order: Order): Long {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            order.userId = userId
            val doc = ordersCollection.add(order).await()
            val newId = doc.id.hashCode().toLong() // Generate a unique integer ID based on document ID hash
            ordersCollection.document(doc.id).update("_id", newId).await() // Update the document with the generated ID
            newId
        } catch (e: Exception) {
            e.printStackTrace()
            -1L
        }
    }

    override suspend fun deleteOrder(orderId: Int): Boolean {
        return try {
            val documentId = ordersCollection.whereEqualTo("_id", orderId).get().await().documents.firstOrNull()?.id
                ?: return false
            ordersCollection.document(documentId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun addItemToOrder(itemOrder: ItemOrder): Long {
        return try {
            val orderDocumentId = ordersCollection.whereEqualTo("_id", itemOrder.orderId).get().await().documents.firstOrNull()?.id
                ?: return -1L
            val orderDocument = ordersCollection.document(orderDocumentId)
            val itemOrderDocument = orderDocument.collection("items").add(itemOrder).await()
            val newId = itemOrderDocument.id.hashCode().toLong()
            itemOrderDocument.update("_id", newId).await()
            newId
        } catch (e: Exception) {
            e.printStackTrace()
            -1L
        }
    }

    override suspend fun deleteItemFromOrder(orderId: Int, itemId: Int): Boolean {
        return try {
            val orderDocumentId = ordersCollection.whereEqualTo("_id", orderId).get().await().documents.firstOrNull()?.id
                ?: return false
            val orderDocument = ordersCollection.document(orderDocumentId)
            val itemOrderDocumentId = orderDocument.collection("items").whereEqualTo("itemId", itemId).get().await().documents.firstOrNull()?.id
                ?: return false
            orderDocument.collection("items").document(itemOrderDocumentId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteItemFromOrder(itemOrderId: Int): Boolean {
        return try {
            return true

            //val orderDocumentId = ordersCollection.whereEqualTo("_id", orderId.toString()).get().await().documents.firstOrNull()?.id
            //    ?: return false
            //var orderDocument = ordersCollection.document(orderDocumentId)
            //val itemOrderDocumentId = orderDocument.collection("items").whereEqualTo("_id", itemOrderId).get().await()?.documents?.firstOrNull()?.id
            //    ?: return false
            //orderDocument.collection("items").document(itemOrderDocumentId).delete().await()
            //true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getAllItemsOnOrder(orderId: Int): List<ItemOrder> {
        return try {
            val orderDocumentId = ordersCollection.whereEqualTo("_id", orderId).get().await().documents.firstOrNull()?.id
                ?: return emptyList()
            val orderDocument = ordersCollection.document(orderDocumentId)
            val querySnapshot = orderDocument.collection("items").get().await()
            querySnapshot.toObjects(ItemOrder::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}