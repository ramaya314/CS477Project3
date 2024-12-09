package gmu.cs477.project2.ui

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import gmu.cs477.project2.R
import gmu.cs477.project2.data.db.DatabaseHelper
import gmu.cs477.project2.data.db.repositories.sqlite.ItemRepository
import gmu.cs477.project2.data.db.repositories.sqlite.OrderRepository
import gmu.cs477.project2.data.models.ItemOrder
import gmu.cs477.project2.data.models.Order
import gmu.cs477.project2.interfaces.IItemRepository
import gmu.cs477.project2.interfaces.IOrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class OrderActivity : AppCompatActivity() {


    @Inject
    lateinit var itemRepository: IItemRepository

    @Inject
    lateinit var orderRepository: IOrderRepository

    private val toolbar: Toolbar                by lazy { findViewById(R.id.toolbar) }
    private val itemSpinner: Spinner            by lazy { findViewById(R.id.itemSpinner) }
    private val quantityEditText: EditText      by lazy { findViewById(R.id.quantityEditText) }
    private val addButton: Button               by lazy { findViewById(R.id.addButton) }
    private val removeButton: Button            by lazy { findViewById(R.id.removeButton) }
    private val finishButton: Button            by lazy { findViewById(R.id.finishButton) }
    private val orderSummaryTextView: TextView  by lazy { findViewById(R.id.orderSummaryTextView) }

    private val currentOrder = mutableListOf<ItemOrder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_order)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpToolBar()
        loadItems()
        setUpEventHandlers()
    }

    private fun setUpToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Create Order"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadItems() {
        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) {
                itemRepository.getAllItems().filter { it.stock > 0 }
            }
            val itemNames = items.map { it.name }
            val adapter = ArrayAdapter(this@OrderActivity, android.R.layout.simple_spinner_item, itemNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            itemSpinner.adapter = adapter
        }
    }

    private fun setUpEventHandlers() {
        addButton.setOnClickListener { addItemToOrder() }
        removeButton.setOnClickListener { removeItemFromOrder() }
        finishButton.setOnClickListener { finishOrder() }
    }


    private fun addItemToOrder() {
        val selectedItemName = itemSpinner.selectedItem as String?
        val quantityText = quantityEditText.text.toString()
        if (selectedItemName != null && quantityText.isNotEmpty()) {
            val quantity = quantityText.toIntOrNull()
            if (quantity == null || quantity <= 0) {
                val alertDialog = AlertDialog.Builder(this@OrderActivity)
                    .setTitle("Error")
                    .setMessage("The quantity entered is invalid")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                alertDialog.show()
                return
            }
            lifecycleScope.launch {
                val item = withContext(Dispatchers.IO) {
                    itemRepository.getAllItems().firstOrNull { it.name == selectedItemName }
                }
                item?.let {
                    if (quantity > it.stock) {

                        val alertDialog = AlertDialog.Builder(this@OrderActivity)
                            .setTitle("Error")
                            .setMessage("Not enough stock (available stock: ${it.stock})")
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                        alertDialog.show()
                    } else {
                        val existingItemOrder = currentOrder.firstOrNull { it.itemId == item._id }
                        if (existingItemOrder != null) {
                            existingItemOrder.quantity += quantity
                        } else {
                            currentOrder.add(ItemOrder(itemId = item._id, orderId = 0, quantity = quantity))
                        }
                        updateOrderSummary()
                    }
                }
            }
        }
    }


    private fun removeItemFromOrder() {
        val selectedItemName = itemSpinner.selectedItem as String?
        val quantityText = quantityEditText.text.toString()
        if (selectedItemName != null && quantityText.isNotEmpty()) {
            val quantity = quantityText.toInt()
            getItemIdByName(selectedItemName) { itemId ->
                val itemOrder = currentOrder.firstOrNull { it.itemId == itemId }
                if (itemOrder != null) {
                    itemOrder.quantity -= quantity
                    if (itemOrder.quantity <= 0) {
                        currentOrder.remove(itemOrder)
                    }
                    updateOrderSummary()
                }
            }
        }
    }


    private fun finishOrder() {
        if (currentOrder.isNotEmpty()) {
            lifecycleScope.launch {
                val orderId = withContext(Dispatchers.IO) {
                    orderRepository.addOrder(Order(orderDate = Date()))
                }
                currentOrder.forEach { itemOrder ->
                    itemOrder.orderId = orderId.toInt()
                    withContext(Dispatchers.IO) {
                        orderRepository.addItemToOrder(itemOrder)
                        val item = itemRepository.getItemById(itemOrder.itemId)
                        item?.let {
                            it.stock -= itemOrder.quantity
                            itemRepository.updateItem(it._id, it)
                        }
                    }
                }
                finish()
            }
        }
    }

    private fun updateOrderSummary() {
        val summaryBuilder = StringBuilder("Current Order:\n")
        var totalCost = 0.0f
        lifecycleScope.launch {
            currentOrder.forEach { itemOrder ->
                val item = withContext(Dispatchers.IO) { itemRepository.getItemById(itemOrder.itemId) }
                item?.let {
                    val cost = it.cost * itemOrder.quantity
                    totalCost += cost
                    summaryBuilder.append("${it.name} (${itemOrder.quantity}) $cost\n")
                }
            }
            summaryBuilder.append("TOTAL COST: $totalCost")
            orderSummaryTextView.text = summaryBuilder.toString()
        }
    }


    private fun getItemIdByName(name: String, callback: (Int?) -> Unit) {
        lifecycleScope.launch {
            val items = withContext(Dispatchers.IO) {
                itemRepository.getAllItems() // Suspend function call to get all items
            }
            val itemId = items.firstOrNull { it.name == name }?._id
            callback(itemId)
        }
    }


}