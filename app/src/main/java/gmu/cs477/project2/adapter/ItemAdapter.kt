package gmu.cs477.project2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import gmu.cs477.project2.R
import gmu.cs477.project2.data.models.Item

class ItemAdapter(
    public var items: List<Item>,
    private val onItemClick: (Item) -> Unit,
    private val onItemDeleteClick: (Item) -> Unit,
    private val onItemLongClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cell, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemName.text = item.name
        holder.itemCost.text = "Cost per item: $${item.cost}"
        holder.itemStock.text = "${item.stock} in stock"
        holder.itemView.setOnClickListener { onItemClick(item) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(item)
            true
        }
        holder.deleteButton.setOnClickListener { onItemDeleteClick(item)}
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView =        itemView.findViewById(R.id.itemName)
        val itemCost: TextView =        itemView.findViewById(R.id.itemCost)
        val itemStock: TextView =       itemView.findViewById(R.id.itemStock)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteItemButton)
    }
}