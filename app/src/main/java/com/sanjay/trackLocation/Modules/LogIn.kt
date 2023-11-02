package com.sanjay.trackLocation.Modules

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.sanjay.trackLocation.Database.CredentialObject
import com.sanjay.trackLocation.Database.OperationModel
import com.sanjay.trackLocation.R

class LogIn : AppCompatActivity() {

    private lateinit var emailET: EditText
    private lateinit var passET: EditText
    private lateinit var signUpTV: TextView
    private lateinit var logInBtn: Button

    private lateinit var resultData: List<CredentialObject>
    private lateinit var operationModel: OperationModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        emailET = findViewById(R.id.emailLoginET)
        passET = findViewById(R.id.passLoginET)
        signUpTV = findViewById(R.id.signUpTv)
        logInBtn = findViewById(R.id.logInBtn)

        operationModel = ViewModelProvider(this)[OperationModel::class.java]

        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("data", Context.MODE_PRIVATE)
        val USER_ID: Int = sharedPreferences.getInt("USER_ID", 0)
        if (USER_ID != 0) {
            HomePage.loggedInUserID = USER_ID
            goToHomePage()
        }

        logInBtn.setOnClickListener {

            val email = emailET.text.toString()
            val pass = passET.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()
            } else if (pass.isEmpty()) {
                Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show()
            } else {
                operationModel.logIn(email, pass)
                operationModel.logInResultData.observe(this) {
                    resultData = it
                    if (resultData.isNotEmpty()) {
                        HomePage.loggedInUserID = resultData[0].id
                        operationModel.updateIsLoggedIn(resultData[0].id, true)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putInt("USER_ID", resultData[0].id)
                        editor.putString("USERNAME", resultData[0].name)
                        editor.putString("EMAIL", resultData[0].email)
                        editor.apply()
                        editor.commit()
                        goToHomePage()
                    } else {
                        Toast.makeText(
                            this,
                            "Email or Password doesn't match.\nKindly try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        signUpTV.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

    }

    private fun goToHomePage() {
        val i = Intent(this, HomePage::class.java)
        startActivity(i)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }

}