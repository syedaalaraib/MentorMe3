package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class LOGIN : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.enteremail)
        password = findViewById(R.id.enterpassword)
        loginButton = findViewById(R.id.login)

        loginButton.setOnClickListener {
            val txt_email = email.text.toString()
            val txt_password = password.text.toString()
            loginuser(txt_email, txt_password)
        }

        val forgotButton = findViewById<Button>(R.id.forgotpassword)
        forgotButton.setOnClickListener {
            val intent = Intent(this, forgotpassword::class.java)
            startActivity(intent)
        }

        val signUpButton = findViewById<Button>(R.id.signup)
        signUpButton.setOnClickListener {
            val intent = Intent(this, signup::class.java)
            startActivity(intent)
        }

        val content = SpannableString("Sign up?")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        signUpButton.text = content
    }

//    private fun loginuser(txtEmail: String, txtPassword: String) {
//        auth.signInWithEmailAndPassword(txtEmail, txtPassword)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
//                    val user = auth.currentUser
//                    val intent = Intent(this, Hello::class.java)
//                    intent.extras?.putString("userid", user?.uid)
//                    startActivity(intent)
//
////                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
////                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                    //updateUI(null)
//                }
//            }
//
//    }

    private fun loginuser(txtEmail: String, txtPassword: String) {
        val url = "http://192.168.10.8/smd3/login.php"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                val jsonResponse = JSONObject(response)
                val message = jsonResponse.getString("message")
                val status = jsonResponse.getString("status")
                if (status == "1") {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    val userId = jsonResponse.getInt("userId") // Retrieve user ID from response

                    // Create intent and pass user ID to the next activity
                    //----------------------------------------------------------
                    val intent = Intent(this, Hello::class.java)
                    //----------------------------------------------------------
                    intent.putExtra("currentid", userId)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["email"] = txtEmail
                params["password"] = txtPassword
                return params
            }
        }
        requestQueue.add(stringRequest)
    }


}
