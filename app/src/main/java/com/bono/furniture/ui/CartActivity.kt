package com.bono.furniture.ui

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import com.bono.furniture.db.DbHelper
import com.bono.furniture.adapters.ItemsAdapter
import com.bono.furniture.R

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userId = intent.getIntExtra("user_id", 0)
        val userEmail = intent.getStringExtra("user_email")
        val dbHelper = DbHelper(this, null)
        val userData = dbHelper.getUserByEmail(userEmail ?: "")

        val userName: TextView = findViewById(R.id.user_name)
        userName.text = userData?.login ?: "User"

        val logoutIcon: ImageView = findViewById(R.id.logout_icon)
        logoutIcon.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        val cartItems = dbHelper.getCartItems(userId)
        val cartList: RecyclerView = findViewById(R.id.cart_list)
        cartList.layoutManager = LinearLayoutManager(this)
        cartList.adapter = ItemsAdapter(cartItems, this, userId)

        val buttonBackToList: Button = findViewById(R.id.button_back_to_list)
        buttonBackToList.setOnClickListener {
            val intent = Intent(this, ItemsActivity::class.java)
            intent.putExtra("user_email", userEmail)
            startActivity(intent)
            finish()
        }
    }
}