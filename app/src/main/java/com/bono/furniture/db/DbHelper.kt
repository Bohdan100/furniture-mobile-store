package com.bono.furniture.db

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.content.Context
import android.annotation.SuppressLint

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.mindrot.jbcrypt.BCrypt

import com.bono.furniture.models.User
import com.bono.furniture.models.UserData
import com.bono.furniture.models.Item
import com.bono.furniture.R

class DbHelper(private val context: Context, private val factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "mobileNote", factory, 1) {

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL("PRAGMA foreign_keys = ON")

        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT, email TEXT, password TEXT)")
        db.execSQL("CREATE TABLE products(id INTEGER PRIMARY KEY AUTOINCREMENT, image TEXT, title TEXT, description TEXT, text TEXT, price INT)")
        db.execSQL("""
        CREATE TABLE carts(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INT,
            product_id INT,
            FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
            FOREIGN KEY(product_id) REFERENCES products(id) ON DELETE CASCADE
        )
    """)

        val initialProducts = loadProductsFromJson(context)
        for (product in initialProducts) {
            val values = ContentValues().apply {
                put("image", product.image)
                put("title", product.title)
                put("description", product.desc)
                put("text", product.text)
                put("price", product.price)
            }

            db.insert("products", null, values)
        }
    }

    private fun loadProductsFromJson(context: Context): List<Item> {
        val inputStream = context.resources.openRawResource(R.raw.products)
        val json = inputStream.bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Item>>() {}.type
        return Gson().fromJson(json, type)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS products")
        db.execSQL("DROP TABLE IF EXISTS carts")

        onCreate(db)
    }

    fun addUser(user: User) {
        val hashedPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())

        val values = ContentValues()
        values.put("login", user.login)
        values.put("email", user.email)
        values.put("password", hashedPassword)

        val db = this.writableDatabase
        db.insert("users", null, values)
        db.close()
    }

    @SuppressLint("Recycle")
    fun isUserExists(email: String): Boolean {
        val db = this.readableDatabase
        val result = db.rawQuery("SELECT * FROM users WHERE email = '$email'", null)
        return result.moveToFirst()
    }

    fun getUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT password FROM users WHERE email = '$email'", null)

        return if (cursor.moveToFirst()) {
            val hashedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"))
            cursor.close()
            BCrypt.checkpw(password, hashedPassword)
        } else {
            cursor.close()
            false
        }
    }

    fun getAllProducts(): List<Item> {
        val products = mutableListOf<Item>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM products", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val image = cursor.getString(cursor.getColumnIndexOrThrow("image"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val text = cursor.getString(cursor.getColumnIndexOrThrow("text"))
                val price = cursor.getInt(cursor.getColumnIndexOrThrow("price"))
                products.add(Item(id, image, title, desc, text, price))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return products
    }

    fun getUserByEmail(email: String): UserData? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id, login FROM users WHERE email = '$email'", null)

        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val login = cursor.getString(cursor.getColumnIndexOrThrow("login"))
            UserData(id, login).also { cursor.close() }
        } else {
            cursor.close()
            null
        }
    }

    fun addToCart(userId: Int, productId: Int): Boolean {
        if (isProductInCart(userId, productId)) {
            return false
        }

        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("user_id", userId)
            put("product_id", productId)
        }
        db.insert("carts", null, values)
        db.close()
        return true
    }

    fun isProductInCart(userId: Int, productId: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM carts WHERE user_id = ? AND product_id = ?", arrayOf(userId.toString(), productId.toString()))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun removeFromCart(userId: Int, productId: Int) {
        val db = this.writableDatabase
        db.delete("carts", "user_id = ? AND product_id = ?", arrayOf(userId.toString(), productId.toString()))
        db.close()
    }

    fun getCartItems(userId: Int): List<Item> {
        val cartItems = mutableListOf<Item>()
        val db = this.readableDatabase
        val query = """
        SELECT products.* FROM carts
        INNER JOIN products ON carts.product_id = products.id
        WHERE carts.user_id = $userId
    """
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val image = cursor.getString(cursor.getColumnIndexOrThrow("image"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val text = cursor.getString(cursor.getColumnIndexOrThrow("text"))
                val price = cursor.getInt(cursor.getColumnIndexOrThrow("price"))
                cartItems.add(Item(id, image, title, desc, text, price))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return cartItems
    }
}