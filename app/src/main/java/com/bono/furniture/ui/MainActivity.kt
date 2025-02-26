package com.bono.furniture.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.bono.furniture.db.DbHelper
import com.bono.furniture.models.User
import com.bono.furniture.utils.Validator
import com.bono.furniture.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userLogin: EditText = findViewById(R.id.user_login)
        val userEmail: EditText = findViewById(R.id.user_email)
        val userPassword: EditText = findViewById(R.id.user_password)
        val button: Button = findViewById(R.id.button_reg)
        val linkToLog: TextView = findViewById(R.id.link_to_log)

        linkToLog.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val email = userEmail.text.toString().trim()
            val password = userPassword.text.toString().trim()

            when {
                login.length < 2 -> {
                    Toast.makeText(
                        this,
                        "Login must be at least 2 characters long",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                !Validator.isValidEmail(email) -> {
                    Toast.makeText(
                        this,
                        "Email must contain '@' and have at least 1 character before and 2 after '@'",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }

                !Validator.isValidPassword(password) -> {
                    Toast.makeText(
                        this,
                        "Password must be at least 4 characters long and contain both letters and numbers",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
            }

            val db = DbHelper(this, null)
            if (db.isUserExists(email)) {
                Toast.makeText(this, "User with this email already exists", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            val user = User(login, email, password)
            db.addUser(user)
            Toast.makeText(
                this,
                "The user account '$login' has been successfully created.",
                Toast.LENGTH_LONG
            ).show()

            userLogin.text.clear()
            userEmail.text.clear()
            userPassword.text.clear()

            val intent = Intent(this, ItemsActivity::class.java)
            intent.putExtra("user_email", email)
            startActivity(intent)
        }
    }
}