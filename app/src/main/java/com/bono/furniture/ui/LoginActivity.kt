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
import com.bono.furniture.utils.Validator
import com.bono.furniture.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userEmail: EditText = findViewById(R.id.user_email_auth)
        val userPassword: EditText = findViewById(R.id.user_password_auth)
        val button: Button = findViewById(R.id.button_log)
        val linkToReg: TextView = findViewById(R.id.link_to_reg)

        linkToReg.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            val email = userEmail.text.toString().trim()
            val password = userPassword.text.toString().trim()

            when {
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
            val isAuth = db.getUser(email, password)

            if (isAuth) {
                Toast.makeText(
                    this,
                    "The user account '$email' successfully authenticated.",
                    Toast.LENGTH_LONG
                ).show()

                userEmail.text.clear()
                userPassword.text.clear()

                val intent = Intent(this, ItemsActivity::class.java)
                intent.putExtra("user_email", email)
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "Invalid email or password.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}