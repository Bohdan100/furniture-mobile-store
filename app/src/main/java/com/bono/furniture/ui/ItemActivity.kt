package com.bono.furniture.ui

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.stripe.android.view.CardInputWidget
import com.stripe.android.PaymentConfiguration
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import com.bono.furniture.db.DbHelper
import com.bono.furniture.models.PaymentRequest
import com.bono.furniture.network.ApiService
import com.bono.furniture.R

class ItemActivity : AppCompatActivity() {
    private lateinit var stripe: Stripe
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51Qv6iCA1EFIrwutav585fXYIbv4TsJaSvxSr0D3aHqL3yVnxdMf39NjfwRAxSJdOAyDu3eX1KneImXIGyTWyq5U800NKNcTziN"
        )
        stripe = Stripe(
            applicationContext,
            PaymentConfiguration.getInstance(applicationContext).publishableKey
        )

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://server_url/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        apiService = retrofit.create(ApiService::class.java)

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

        val title: TextView = findViewById(R.id.item_page_title)
        val text: TextView = findViewById(R.id.item_page_text)
        val price: TextView = findViewById(R.id.item_page_price)
        val image: ImageView = findViewById(R.id.item_page_image)
        val buttonBuy: Button = findViewById(R.id.button_buy)
        val buttonAddToCart: Button = findViewById(R.id.button_add_to_cart)
        val buttonBackToList: Button = findViewById(R.id.button_back_to_list)
        val cardInputWidget: CardInputWidget = findViewById(R.id.cardInputWidget)

        title.text = intent.getStringExtra("itemTitle")
        text.text = intent.getStringExtra("itemText")
        price.text = intent.getStringExtra("itemPrice")

        val imageId = intent.getIntExtra("itemImage", 0)
        if (imageId != 0) {
            image.setImageResource(imageId)
        } else {
            image.setImageResource(R.drawable.no_product_image)
        }

        buttonAddToCart.setOnClickListener {
            val productId = intent.getIntExtra("itemId", -1)
            if (productId != -1 && userData != null) {
                if (dbHelper.addToCart(userData.id, productId)) {
                    Toast.makeText(this, "Item added to cart", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Item is already in the cart", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Failed to add item to cart", Toast.LENGTH_SHORT).show()
            }
        }

        buttonBuy.setOnClickListener {
            val paymentMethodParams = cardInputWidget.paymentMethodCreateParams
            if (paymentMethodParams != null) {
                processPayment(paymentMethodParams)
            } else {
                Toast.makeText(this, "Invalid card data", Toast.LENGTH_SHORT).show()
            }
        }

        buttonBackToList.setOnClickListener {
            val intent = Intent(this, ItemsActivity::class.java)
            intent.putExtra("user_email", userEmail)
            startActivity(intent)
            finish()
        }
    }

    private fun processPayment(paymentMethodParams: PaymentMethodCreateParams) {
        stripe.createPaymentMethod(
            paymentMethodParams,
            callback = object : ApiResultCallback<PaymentMethod> {
                override fun onSuccess(result: PaymentMethod) {
                    val paymentMethodId = result.id
                    if (paymentMethodId != null) {
                        sendPaymentMethodToServer(paymentMethodId)
                    } else {
                        Toast.makeText(
                            this@ItemActivity,
                            "Payment failed: PaymentMethod ID is null",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onError(e: Exception) {
                    Toast.makeText(
                        this@ItemActivity,
                        "Payment failed: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }

    private fun sendPaymentMethodToServer(paymentMethodId: String) {
        val userEmail = intent.getStringExtra("user_email")
        val dbHelper = DbHelper(this, null)
        val userData = dbHelper.getUserByEmail(userEmail ?: "")
        val productId = intent.getIntExtra("itemId", -1)

        if (userData != null && productId != -1) {
            if (dbHelper.isProductInCart(userData.id, productId)) {
                dbHelper.removeFromCart(userData.id, productId)
                Toast.makeText(
                    this,
                    "PaymentMethod ID: $paymentMethodId. Item was removed from the cart.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this,
                "PaymentMethod ID: $paymentMethodId. Failed to process cart item.",
                Toast.LENGTH_SHORT
            ).show()
        }

        val paymentRequest = PaymentRequest(paymentMethodId)
    }
}