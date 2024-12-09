package gmu.cs477.project2.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import gmu.cs477.project2.adapter.ItemAdapter
import gmu.cs477.project2.data.db.DatabaseHelper
import gmu.cs477.project2.R
import gmu.cs477.project2.data.db.repositories.sqlite.ItemRepository
import gmu.cs477.project2.data.models.Item
import gmu.cs477.project2.interfaces.IItemRepository
import javax.inject.Inject


@AndroidEntryPoint
class InventoryActivity : AppCompatActivity() {

    private val itemRecyclerView: RecyclerView by lazy { findViewById(R.id.itemRecyclerView) }
    private val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }

    private lateinit var itemAdapter: ItemAdapter

    @Inject
    lateinit var itemRepository: IItemRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inventory)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        itemRecyclerView.layoutManager = LinearLayoutManager(this)
        itemRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        loadItems()
        setUpToolBar()
    }

    override fun onResume() {
        super.onResume()
        loadItems()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.inventory_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_item_toolbar_button -> {
                val intent = Intent(this, ItemActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Current Inventory"
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
                itemRepository.getAllItems()
            }
            if (::itemAdapter.isInitialized) {
                itemAdapter.updateItems(items)
            } else {
                setUpAdapter(items)
            }
        }
    }


    private suspend fun setUpAdapter(items: List<Item>) {
        val dialogBuilder = AlertDialog.Builder(this)
        itemAdapter = ItemAdapter(items, onItemClick = { item ->
            Toast.makeText(this@InventoryActivity, item.description, Toast.LENGTH_SHORT).show()
        }, onItemDeleteClick = { item ->
            dialogBuilder.setTitle("Delete Item")
            dialogBuilder.setMessage("Are you sure you want to delete this item?")
            dialogBuilder.setPositiveButton("Yes") { dialog, _ ->
                lifecycleScope.launch {
                    val success = withContext(Dispatchers.IO) {
                        itemRepository.deleteItem(item._id) //suspend call
                    }
                    if (success) {
                        loadItems()
                    } else {
                        Toast.makeText(this@InventoryActivity, "Failed to delete item", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
            }
            dialogBuilder.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            val deleteConfirmDialog = dialogBuilder.create()
            deleteConfirmDialog.show()
        }, onItemLongClick = { item ->
            val intent = Intent(this@InventoryActivity, ItemActivity::class.java)
            intent.putExtra("item_id", item._id)
            startActivity(intent)
        })
        itemRecyclerView.adapter = itemAdapter
    }
}