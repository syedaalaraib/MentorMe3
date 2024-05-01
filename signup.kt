package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.laraib.i210865.Hello
import com.laraib.i210865.R
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class signup : AppCompatActivity() {
    private lateinit var signUpButton: Button
    private lateinit var loginButton: Button
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var phone: EditText
    private lateinit var countrySpinner: Spinner
    private lateinit var citySpinner: Spinner
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationcode: String
    private lateinit var Force: PhoneAuthProvider.ForceResendingToken
    private lateinit var number: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signUpButton = findViewById(R.id.signup)
        loginButton = findViewById(R.id.login)
        name = findViewById(R.id.entername)
        email = findViewById(R.id.enteremail)
        password = findViewById(R.id.password)
        phone = findViewById(R.id.entercontactnumber)
        countrySpinner = findViewById(R.id.countrySpinner)
        citySpinner = findViewById(R.id.citySpinner)

        auth = FirebaseAuth.getInstance()
        verificationcode = ""

        // Check if user is already signed in
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            startActivity(Intent(this, Hello::class.java))
//            finish()
//        }

        signUpButton.setOnClickListener {
            val txt_name = name.text.toString()
            val txt_email = email.text.toString()
            val txt_password = password.text.toString()
            val txt_phone = phone.text.toString()
            number= "+92$txt_phone"
            val txt_country = countrySpinner.selectedItem.toString()
            val txt_city = citySpinner.selectedItem.toString()

            if (txt_name.isEmpty() || txt_email.isEmpty() || txt_password.isEmpty() || txt_phone.isEmpty() || txt_country.isEmpty() || txt_city.isEmpty()) {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(txt_name, txt_email, txt_password, txt_phone, txt_country, txt_city)
            }
        }

        // Set underline for the text
        val content = SpannableString("Login")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        loginButton.text = content

        // Define lists of countries and cities
        val countries = listOf("Select Country", "Pakistan", "Switzerland", "Saudi Arabia")
        val citiesMap = mapOf(
            "Select Country" to listOf("Select City"),
            "Pakistan" to listOf("Select City", "Islamabad", "Karachi", "Lahore"),
            "Switzerland" to listOf("Select City", "Zurich", "Geneva", "Bern"),
            "Saudi Arabia" to listOf("Select City", "Riyadh", "Jeddah", "Dammam")
        )

        // Create an ArrayAdapter for countries
        val countryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = countryAdapter

        // Create an ArrayAdapter for cities (initially with a default "Select City" item)
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Select City"))
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = cityAdapter

        // Set the OnItemSelectedListener for the countrySpinner
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
                // Get the selected country
                val selectedCountry = parentView.getItemAtPosition(position).toString()

                // Update the list of cities based on the selected country
                val selectedCities = citiesMap[selectedCountry] ?: listOf("Select City")
                updateCitySpinner(selectedCities)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Do nothing here
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String, phone: String, country: String, city: String) {
        val url = "http://192.168.10.8/smd3/signup.php"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                val jsonResponse = JSONObject(response)
                val message = jsonResponse.getString("message")
                val status = jsonResponse.getString("status")
                if (status == "1") {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                // Log the error message
                Log.e("Volley Error", "Error: $error")

                // Display a toast with the error message
                Toast.makeText(this, "Connection error. Please try again later.", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["name"] = name
                params["email"] = email
                params["contact"] = phone
                params["country"] = country
                params["city"] = city
                params["password"] = password
                return params
            }
        }
        requestQueue.add(stringRequest)
    }


    private fun updateCitySpinner(cities: List<String>) {
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = cityAdapter
    }
}
