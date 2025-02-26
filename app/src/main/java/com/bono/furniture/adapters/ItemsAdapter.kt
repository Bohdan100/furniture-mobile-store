package com.bono.furniture.adapters

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.content.Context
import android.content.Intent
import android.annotation.SuppressLint

import com.bono.furniture.db.DbHelper
import com.bono.furniture.models.Item
import com.bono.furniture.ui.ItemActivity
import com.bono.furniture.R

class ItemsAdapter(
    private var items: List<Item>,
    private var context: Context,
    private var userId: Int = 0,
    private var userEmail: String? = null
) :
    RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.item_list_title)
        val desc: TextView = view.findViewById(R.id.item_list_desc)
        val price: TextView = view.findViewById(R.id.item_list_price)
        val image: ImageView = view.findViewById(R.id.item_list_image)
        val btn: Button = view.findViewById(R.id.item_list_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    @SuppressLint("SetTextI18n", "DiscouragedApi", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = items[position].title
        holder.desc.text = items[position].desc
        holder.price.text = items[position].price.toString()

        val imageId = context.resources.getIdentifier(
            items[position].image,
            "drawable",
            context.packageName
        )
        holder.image.setImageResource(imageId)

        if (userId != 0) {
            holder.btn.text = "Remove"

            holder.btn.setOnClickListener {
                val dbHelper = DbHelper(context, null)
                dbHelper.removeFromCart(userId, items[position].id)
                Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show()
                items = dbHelper.getCartItems(userId)
                notifyDataSetChanged()
            }
        } else {
            holder.btn.text = "View"
            holder.btn.setOnClickListener {
                val intent = Intent(context, ItemActivity::class.java)
                intent.putExtra("itemTitle", items[position].title)
                intent.putExtra("itemText", items[position].text)
                intent.putExtra("itemPrice", items[position].price.toString())
                intent.putExtra("itemImage", imageId)
                intent.putExtra("itemId", items[position].id)
                intent.putExtra("user_email", userEmail)
                context.startActivity(intent)
            }
        }
    }
}