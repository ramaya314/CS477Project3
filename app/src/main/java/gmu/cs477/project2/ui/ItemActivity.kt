package gmu.cs477.project2.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import gmu.cs477.project2.R
import gmu.cs477.project2.data.db.DatabaseHelper
import gmu.cs477.project2.data.db.repositories.sqlite.ItemRepository
import gmu.cs477.project2.data.models.Item
import gmu.cs477.project2.interfaces.IItemRepository
import javax.inject.Inject


import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class ItemActivity : AppCompatActivity() {

    @Inject
    lateinit var itemRepository: IItemRepository

    private val itemNameLabel: TextView         by lazy { findViewById(R.id.itemNameTextView) }
    private val itemNameEdit: EditText          by lazy { findViewById(R.id.itemNameEdit) }
    private val itemDescriptionEdit: EditText   by lazy { findViewById(R.id.itemDescriptionEdit) }
    private val itemDescriptionLabel: TextView  by lazy { findViewById(R.id.itemDescriptionTextView) }
    private val itemCostEdit: EditText          by lazy { findViewById(R.id.itemCostEdit) }
    private val itemStockEdit: EditText         by lazy { findViewById(R.id.itemStockEdit) }
    private val saveButton: Button              by lazy { findViewById(R.id.saveButton) }
    private val toolbar: Toolbar                by lazy { findViewById(R.id.toolbar) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setUpToolBar()

        val itemId = intent.getIntExtra("item_id", -1)
        if(itemId == -1) {
            itemNameLabel.visibility = View.INVISIBLE
            itemNameEdit.visibility = View.VISIBLE
        }
        else {

            lifecycleScope.launch { withContext(Dispatchers.IO) {
                val item = itemRepository.getItemById(itemId)
                withContext(Dispatchers.Main) {
                    item?.let {
                        itemNameLabel.text = it.name
                        itemNameEdit.setText(it.name)
                        itemDescriptionEdit.setText(it.description)
                        itemCostEdit.setText(it.cost.toString())
                        itemStockEdit.setText(it.stock.toString())
                    }
                    itemNameLabel.visibility = View.VISIBLE
                    itemNameEdit.visibility = View.INVISIBLE
                }
            }}
        }

        saveButton.setOnClickListener {
            val name = itemNameEdit.text.toString()
            val description = itemDescriptionEdit.text.toString()
            val costText = itemCostEdit.text.toString()
            val stockText = itemStockEdit.text.toString()

            val context = this;
            if (name.isEmpty() || description.isEmpty() || costText.isEmpty() || stockText.isEmpty()) {
                val alertDialog = AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Please fill in all the values.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                alertDialog.show()
            } else {
                val updatedItem = Item(
                    name = name,
                    description = description,
                    cost = costText.toFloat(),
                    stock = stockText.toInt()
                )

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            if (itemId == -1) {
                                itemRepository.addItem(updatedItem)
                            } else {
                                itemRepository.updateItem(itemId, updatedItem)
                            }
                            withContext(Dispatchers.Main) {
                                finish()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                val alertDialog = AlertDialog.Builder(context)
                                    .setTitle("Error")
                                    .setMessage(e.message)
                                    .setPositiveButton("OK") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .create()
                                alertDialog.show()
                            }
                        }
                    }
                }
            }
        }
    }


    private fun setUpToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Edit Item"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}