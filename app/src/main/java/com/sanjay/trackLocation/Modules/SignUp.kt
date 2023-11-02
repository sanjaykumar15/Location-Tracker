package com.sanjay.trackLocation.Modules

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.sanjay.trackLocation.Database.OperationModel
import com.sanjay.trackLocation.R

class SignUp : AppCompatActivity() {

    private lateinit var nameET: EditText
    private lateinit var emailET: EditText
    private lateinit var passET: EditText
    private lateinit var cPassET: EditText
    private lateinit var contactET: EditText
    private lateinit var signUpBtn: Button

    private lateinit var operationModel: OperationModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        nameET = findViewById(R.id.nameSignupET)
        emailET = findViewById(R.id.emailSignupET)
        passET = findViewById(R.id.passSignupET)
        cPassET = findViewById(R.id.cPassSignupET)
        contactET = findViewById(R.id.contactSignupET)
        signUpBtn = findViewById(R.id.signUpBtn)

        operationModel = ViewModelProvider(this)[OperationModel::class.java]
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("data", Context.MODE_PRIVATE)

        signUpBtn.setOnClickListener {

            val name = nameET.text.toString()
            val email = emailET.text.toString()
            val pass = passET.text.toString()
            val cPass = cPassET.text.toString()
            val contact = contactET.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show()
            } else if (email.isEmpty()) {
                Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()
            } else if (pass.isEmpty()) {
                Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show()
            } else if (cPass.isEmpty()) {
                Toast.makeText(this, "Please Enter Confirm Password", Toast.LENGTH_SHORT).show()
            } else if (pass != cPass) {
                Toast.makeText(
                    this,
                    "Password and Confirm Password should be same",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (contact.isEmpty()) {
                Toast.makeText(this, "Please Enter Contact Number", Toast.LENGTH_SHORT).show()
            } else {
                operationModel.signUp(name, email, pass, contact)
                operationModel.userId.observe(this) {
                    HomePage.loggedInUserID = it
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putInt("USER_ID", it)
                    editor.putString("USERNAME", name)
                    editor.putString("EMAIL", email)
                    editor.apply()
                    editor.commit()
                    val i = Intent(this, HomePage::class.java)
                    startActivity(i)
                    finish()
                }
            }

        }

    }
}