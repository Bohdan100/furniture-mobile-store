package com.bono.furniture.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

import com.bono.furniture.db.DbHelper
import com.bono.furniture.adapters.ItemsAdapter
import com.bono.furniture.R

class ItemsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_items)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userEmail = intent.getStringExtra("user_email")
        val dbHelper = DbHelper(this, null)
        val userData = dbHelper.getUserByEmail(userEmail ?: "")

        val userName: TextView = findViewById(R.id.user_name)
        userName.text = userData?.login ?: "User"

        val cartIcon: ImageView = findViewById(R.id.cart_icon)
        cartIcon.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("user_id", userData?.id ?: 0)
            intent.putExtra("user_email", userEmail)
            startActivity(intent)
        }

        val logoutIcon: ImageView = findViewById(R.id.logout_icon)
        logoutIcon.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        val itemsList: RecyclerView = findViewById(R.id.items_list)
        val items = dbHelper.getAllProducts()
        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = ItemsAdapter(items, this, 0, userEmail)
    }
}