package com.sanjay.trackLocation.Modules

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sanjay.trackLocation.Adapter.LoggedInUserRVAdapter
import com.sanjay.trackLocation.Database.CredentialObject
import com.sanjay.trackLocation.Database.OperationModel
import com.sanjay.trackLocation.R

class Profile : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var userTV: TextView
    private lateinit var msgTV: TextView
    private lateinit var userNameIconTV: TextView
    private lateinit var userNameTV: TextView
    private lateinit var emailTV: TextView
    private lateinit var logOutBtn: Button
    private lateinit var addUserBtn: Button
    private lateinit var loggedInUserRV: RecyclerView

    private lateinit var loggedUsersList: List<CredentialObject>

    private lateinit var operationModel: OperationModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initValue()

        toolbar.title = "Profile"
        setSupportActionBar(toolbar)

        userTV.visibility = View.GONE

        operationModel = ViewModelProvider(this)[OperationModel::class.java]

        getLoggedInUsers()

        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("data", Context.MODE_PRIVATE)

        val userName = sharedPreferences.getString("USERNAME", "")
        val email = sharedPreferences.getString("EMAIL", "")

        userNameIconTV.text = userName?.get(0).toString()
        userNameTV.text = userName
        emailTV.text = email

        loggedInUserRV.layoutManager = LinearLayoutManager(this)
        loggedInUserRV.setHasFixedSize(true)

        operationModel.loggedUserResultData.observe(this) {
            loggedUsersList = it
            if (loggedUsersList.size == 1) {
                msgTV.visibility = View.VISIBLE
                loggedInUserRV.visibility = View.GONE
            } else {
                msgTV.visibility = View.GONE
                loggedInUserRV.visibility = View.VISIBLE
                loggedInUserRV.adapter = LoggedInUserRVAdapter(it) { selectedUser ->
                    HomePage.loggedInUserID = selectedUser.id
                    userNameIconTV.text = selectedUser.name.get(0).toString()
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putInt("USER_ID", selectedUser.id)
                    editor.putString("USERNAME", selectedUser.name)
                    editor.putString("EMAIL", selectedUser.email)
                    editor.apply()
                    editor.commit()
                    goToHomePage()
                }
            }
        }

        logOutBtn.setOnClickListener {
            if (loggedUsersList.size == 1) {
                logoutUpdate()
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("USER_ID", 0)
                editor.putString("USERNAME", "")
                editor.putString("EMAIL", "")
                editor.apply()
                editor.commit()
                goToLoginPage()
            } else {
                logoutUpdate()
                getLoggedInUsers()
                val updatedList = operationModel.loggedUserResultData.value
                HomePage.loggedInUserID = updatedList!![0].id
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putInt("USER_ID", updatedList[0].id)
                editor.putString("USERNAME", updatedList[0].name)
                editor.putString("EMAIL", updatedList[0].email)
                editor.apply()
                editor.commit()
                goToHomePage()
            }
        }

        addUserBtn.setOnClickListener {
            showDialog()
        }

    }

    fun initValue() {

        toolbar = findViewById(R.id.toolbar)

        userTV = findViewById(R.id.userTV)
        msgTV = findViewById(R.id.msgTV)
        userNameIconTV = findViewById(R.id.userNameIconTV)
        userNameTV = findViewById(R.id.userNameTV)
        emailTV = findViewById(R.id.emailTV)

        logOutBtn = findViewById(R.id.logOutBtn)
        addUserBtn = findViewById(R.id.addUserBtn)

        loggedInUserRV = findViewById(R.id.loggedInUserRV)

    }

    private fun showDialog() {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.switch_user_dia_template)

        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("data", Context.MODE_PRIVATE)

        val emailET = dialog.findViewById<EditText>(R.id.emailLoginET)
        val passET = dialog.findViewById<EditText>(R.id.passLoginET)
        val logInBtn = dialog.findViewById<Button>(R.id.logInBtn)
        val cancelIcon = dialog.findViewById<ImageView>(R.id.cancelIcon)

        logInBtn.setOnClickListener {

            val email = emailET.text.toString()
            val pass = passET.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()
            } else if (pass.isEmpty()) {
                Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show()
            } else {
                operationModel.logIn(email, pass)
                operationModel.logInResultData.observe(this) { resultData ->
                    if (resultData.isNotEmpty()) {
                        HomePage.loggedInUserID = resultData[0].id
                        operationModel.updateIsLoggedIn(resultData[0].id, true)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putInt("USER_ID", resultData[0].id)
                        editor.putString("USERNAME", resultData[0].name)
                        editor.putString("EMAIL", resultData[0].email)
                        editor.apply()
                        editor.commit()
                        dialog.dismiss()
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

        cancelIcon.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    private fun getLoggedInUsers() {
        operationModel.getLoggedInUser()
    }

    private fun logoutUpdate() {
        operationModel.updateIsLoggedIn(HomePage.loggedInUserID, false)
    }

    private fun goToLoginPage() {
        val i = Intent(this, LogIn::class.java)
        startActivity(i)
        finish()
    }

    private fun goToHomePage() {
        val i = Intent(this, HomePage::class.java)
        startActivity(i)
        finish()
    }

}