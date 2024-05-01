package com.laraib.i210865

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import java.io.ByteArrayOutputStream
import java.io.InputStream

data class User(
    val name: String,
    val city: String,
    val country: String,
    val email: String,
    var phone: String,
    var photoLink: String
)

class editprofile : AppCompatActivity() {

    private var encodedImage: String = ""
    private lateinit var bitmap: Bitmap


    lateinit var chooseimg: ImageView
    var fileuri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        Toast.makeText(this, "welcome", Toast.LENGTH_SHORT).show()


        val userId = intent.getIntExtra("currentid",1)
        Toast.makeText(this, "User ID: $userId", Toast.LENGTH_SHORT).show()


        chooseimg = findViewById(R.id.profilepic)
        chooseimg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111)
        }

        // Find the Spinners by ID
        val countrySpinner = findViewById<Spinner>(R.id.countrySpinner)
        val citySpinner = findViewById<Spinner>(R.id.citySpinner)

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
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
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

        if (userId != null) {
            showProfile(userId)
        }

        val f5 = findViewById<TextView>(R.id.update)
        f5.setOnClickListener {

            if (fileuri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nameText = findViewById<TextView>(R.id.name).text.toString()
            val emailText = findViewById<TextView>(R.id.email).text.toString()
            val phoneText = findViewById<TextView>(R.id.number).text.toString()
            val countryText = countrySpinner.selectedItem.toString()
            val cityText = citySpinner.selectedItem.toString()

            val editmap = mapOf(
                "name" to nameText,
                "email" to emailText,
                "phone" to phoneText,
                "country" to countryText,
                "city" to cityText,
                "photoLink" to encodedImage
            )

            updateProfile(userId, editmap, encodedImage)


            Toast.makeText(this, "profile updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfile(userId: Int?, editMap: Map<String, String>, encodedImage: String) {
        val nameText = editMap["name"] ?: ""
        val cityText = editMap["city"] ?: ""
        val countryText = editMap["country"] ?: ""
        val emailText = editMap["email"] ?: ""
        val phoneText = editMap["phone"] ?: ""

        val request = object : StringRequest(
            Method.POST, "http://192.168.10.8/smd3/update.php",
            Response.Listener { response ->
                // Handle response
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Hello::class.java)
                startActivity(intent)
            },
            Response.ErrorListener { error ->
                // Handle error
                Toast.makeText(this, "Failed to update user profile: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("VolleyError", "Failed to update user profile", error) // Log the error message
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = userId?.toString() ?: ""  // Convert userId to String
                params["name"] = nameText
                params["city"] = cityText
                params["country"] = countryText
                params["email"] = emailText
                params["phone"] = phoneText
                params["image"] = encodedImage // Add image data to the request
                return params
            }
        }

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(request)
    }




    private fun showProfile(id: Int?) {
        // Make sure userId is not null
        id?.let {
            val url = "http://192.168.10.8/smd3/showprofile.php?id=1" // Replace with your actual API endpoint URL

            val request = JsonObjectRequest(
                Request.Method.GET, url, null,
                { response ->
                    // Handle successful response
                    val name = response.getString("name")
                    val email = response.getString("email")
                    val contact = response.getString("contact")
                    val country = response.getString("country")
                    val city = response.getString("city")
                    val pic = response.getString("photoLink")
                    Toast.makeText(this@editprofile, "Photo link: $pic", Toast.LENGTH_SHORT).show()


                    val nameText = findViewById<TextView>(R.id.name)
                    val emailText = findViewById<TextView>(R.id.email)
                    val phoneText = findViewById<TextView>(R.id.number)
                    val countrySpinner = findViewById<Spinner>(R.id.countrySpinner)
                    val citySpinner = findViewById<Spinner>(R.id.citySpinner)
                    val profilepic = findViewById<ImageView>(R.id.profilepic)

                    // Update UI with retrieved data
                    nameText.text = name
                    emailText.text = email
                    phoneText.text = contact
//                    profilepic.setImageURI(Uri.parse(pic))

                    Toast.makeText(this, pic, Toast.LENGTH_SHORT).show()
                    // Load image using Glide if URI is not empty
                    if (!pic.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(pic)
                            .placeholder(R.drawable.customer) // Optional placeholder image
                            .error(R.drawable.baseline_circle_24) // Optional error image
                            .into(profilepic)
                    }

                    // Find the index of country and city in their respective lists
                    val countryIndex = (countrySpinner.adapter as? ArrayAdapter<String>)?.getPosition(country)
                    val cityIndex = (citySpinner.adapter as? ArrayAdapter<String>)?.getPosition(city)

                    // Set the selection for spinners if indices are valid
                    countryIndex?.takeIf { it != -1 }?.let { countrySpinner.setSelection(it) }
                    cityIndex?.takeIf { it != -1 }?.let { citySpinner.setSelection(it) }
                },
                { error ->
                    // Handle error
                    Toast.makeText(this, "Failed to retrieve user data: ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e("NetworkError", "Failed to retrieve user data: ${error.message}")                }
            )

            // Add the request to the RequestQueue
            Volley.newRequestQueue(this).add(request)
        } ?: run {
            // Handle null user ID
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
        }
    }



    // Function to update the city spinner with a new list of cities
    private fun updateCitySpinner(cities: List<String>) {
        val citySpinner = findViewById<Spinner>(R.id.citySpinner)
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = cityAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            fileuri = data.data // Update fileuri with the selected image URI
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(fileuri!!)
                bitmap = BitmapFactory.decodeStream(inputStream)
                chooseimg.setImageBitmap(bitmap)
                encodeBitmapImage(bitmap)
            } catch (ex: Exception) {
                // Handle exception
                ex.printStackTrace()
            }
        }
    }


    private fun encodeBitmapImage(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val bytesofimage: ByteArray = byteArrayOutputStream.toByteArray()
        encodedImage = android.util.Base64.encodeToString(bytesofimage, Base64.DEFAULT)
    }


}




































//package com.laraib.i210865
//
//import android.content.Intent
//import android.net.Uri
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.provider.MediaStore
//import android.view.View
//import android.widget.*
//import com.android.volley.Response
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley
//import com.bumptech.glide.Glide
//import com.google.firebase.FirebaseApp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.auth.UserProfileChangeRequest
//import com.google.firebase.database.*
//
//import com.google.firebase.storage.FirebaseStorage
//import java.util.UUID
//
//data class User(
//    val name: String,
//    val city: String,
//    val country: String,
//    val email: String,
//    var phone: String,
//    var photoLink: String
//)
//
//class editprofile : AppCompatActivity() {
//
//    lateinit var chooseimg: ImageView
//    var fileuri: Uri? = null
//    private lateinit var database: DatabaseReference
//    private lateinit var auth: FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_editprofile)
//
//        Toast.makeText(this, "welcome", Toast.LENGTH_SHORT).show()
//        FirebaseApp.initializeApp(this)
//        database = FirebaseDatabase.getInstance().reference
//        auth = FirebaseAuth.getInstance()
//
//        chooseimg = findViewById(R.id.profilepic)
//        chooseimg.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111)
//        }
//
//        // Find the Spinners by ID
//        val countrySpinner = findViewById<Spinner>(R.id.countrySpinner)
//        val citySpinner = findViewById<Spinner>(R.id.citySpinner)
//
//        // Define lists of countries and cities
//        val countries = listOf("Select Country", "Pakistan", "Switzerland", "Saudi Arabia")
//        val citiesMap = mapOf(
//            "Select Country" to listOf("Select City"),
//            "Pakistan" to listOf("Select City", "Islamabad", "Karachi", "Lahore"),
//            "Switzerland" to listOf("Select City", "Zurich", "Geneva", "Bern"),
//            "Saudi Arabia" to listOf("Select City", "Riyadh", "Jeddah", "Dammam")
//        )
//
//        // Create an ArrayAdapter for countries
//        val countryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
//        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        countrySpinner.adapter = countryAdapter
//
//        // Create an ArrayAdapter for cities (initially with a default "Select City" item)
//        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Select City"))
//        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        citySpinner.adapter = cityAdapter
//
//        // Set the OnItemSelectedListener for the countrySpinner
//        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parentView: AdapterView<*>,
//                selectedItemView: View?,
//                position: Int,
//                id: Long
//            ) {
//                // Get the selected country
//                val selectedCountry = parentView.getItemAtPosition(position).toString()
//
//                // Update the list of cities based on the selected country
//                val selectedCities = citiesMap[selectedCountry] ?: listOf("Select City")
//                updateCitySpinner(selectedCities)
//            }
//
//            override fun onNothingSelected(parentView: AdapterView<*>) {
//                // Do nothing here
//            }
//        }
//
//        // Set the OnItemSelectedListener for the citySpinner
//        auth = FirebaseAuth.getInstance()
//        val fbuser = auth.currentUser
//        showProfile(fbuser)
//
//        val f5 = findViewById<TextView>(R.id.update)
//        f5.setOnClickListener {
//
//            if (fileuri == null) {
//                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            //val currentUser = FirebaseAuth.getInstance().currentUser
//
//            val nameText = findViewById<TextView>(R.id.name).text.toString()
//            val emailText = findViewById<TextView>(R.id.email).text.toString()
//            val phoneText = findViewById<TextView>(R.id.number).text.toString()
//            val countryText = countrySpinner.selectedItem.toString()
//            val cityText = citySpinner.selectedItem.toString()
//
//            val editmap = mapOf(
//                "name" to nameText,
//                "email" to emailText,
//                "phone" to phoneText,
//                "country" to countryText,
//                "city" to cityText
//            )
//
//            updateProfile(fbuser, editmap)
//
//            val user = User(nameText, cityText, countryText, emailText, phoneText, fileuri.toString())
//            uploadImage(user)
//
//            Toast.makeText(this, "profile updated", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun updateProfile(fbUser: FirebaseUser?, editMap: Map<String, String>) {
//        val nameText = findViewById<TextView>(R.id.name).text.toString()
//        val currentUser = FirebaseAuth.getInstance().currentUser
//
//        val writeUserDetails = User(
//            editMap["name"] ?: "",
//            editMap["city"] ?: "",
//            editMap["country"] ?: "",
//            editMap["email"] ?: "",
//            editMap["phone"] ?: "",
//            editMap["photoLink"] ?: ""
//        )
//
//        val uid = fbUser?.uid
//        if (uid != null) {
//            val ref = FirebaseDatabase.getInstance().getReference("users").child(uid)
//            ref.setValue(writeUserDetails).addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val profileUpdates = UserProfileChangeRequest.Builder()
//                        .setDisplayName(nameText)
//                        .build()
//                    currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
//                        if (profileTask.isSuccessful) {
//                            Toast.makeText(this, "Your data is updated successfully", Toast.LENGTH_SHORT).show()
//                            val intent = Intent(this, Hello::class.java)
//                            startActivity(intent)
//                        } else {
//                            Toast.makeText(this, "Failed to update user profile", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                } else {
//                    Toast.makeText(this, "Failed to update user data", Toast.LENGTH_SHORT).show()
//                }
//            }
//        } else {
//            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun showProfile(fbUser: FirebaseUser?) {
//        val uid = fbUser?.uid
//        if (uid != null) {
//            val refProfile = FirebaseDatabase.getInstance().getReference("users")
//            refProfile.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val name = snapshot.child("name").getValue(String::class.java)
//                    val email = snapshot.child("email").getValue(String::class.java)
//                    val phone = snapshot.child("phone").getValue(String::class.java)
//                    val country = snapshot.child("country").getValue(String::class.java)
//                    val city = snapshot.child("city").getValue(String::class.java)
//                    val pic = snapshot.child("photoLink").getValue(String::class.java)
//
//                    // Get references to UI elements
//                    val nameText = findViewById<TextView>(R.id.name)
//                    val emailText = findViewById<TextView>(R.id.email)
//                    val phoneText = findViewById<TextView>(R.id.number)
//                    val countrySpinner = findViewById<Spinner>(R.id.countrySpinner)
//                    val citySpinner = findViewById<Spinner>(R.id.citySpinner)
//                    val profilepic = findViewById<ImageView>(R.id.profilepic)
//
//                    // Update UI with retrieved data
//                    nameText.text = name
//                    emailText.text = email
//                    phoneText.text = phone
//
//                    // Load image using Glide if URI is not empty
//                    if (!pic.isNullOrEmpty()) {
//                        Glide.with(this@editprofile)
//                            .load(pic)
//                            .placeholder(R.drawable.customer) // Optional placeholder image
//                            .error(R.drawable.baseline_circle_24) // Optional error image
//                            .into(profilepic)
//                    }
//
//                    // Find the index of country and city in their respective lists
//                    val countryIndex = (countrySpinner.adapter as? ArrayAdapter<String>)?.getPosition(country)
//                    val cityIndex = (citySpinner.adapter as? ArrayAdapter<String>)?.getPosition(city)
//
//                    // Set the selection for spinners if indices are valid
//                    countryIndex?.takeIf { it != -1 }?.let { countrySpinner.setSelection(it) }
//                    cityIndex?.takeIf { it != -1 }?.let { citySpinner.setSelection(it) }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    // Handle database error
//                    Toast.makeText(this@editprofile, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
//                }
//            })
//        } else {
//            // Handle null user ID
//            Toast.makeText(this@editprofile, "User ID is null", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // Function to update the city spinner with a new list of cities
//    private fun updateCitySpinner(cities: List<String>) {
//        val citySpinner = findViewById<Spinner>(R.id.citySpinner)
//        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
//        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        citySpinner.adapter = cityAdapter
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 111 && resultCode == RESULT_OK && data != null) {
//            fileuri = data.data
//            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileuri)
//                chooseimg.setImageBitmap(bitmap)
//            } catch (e: Exception) {
//                Toast.makeText(this, "Exception", Toast.LENGTH_SHORT).show()
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private fun uploadImage(user: User) {
//        val ref = FirebaseStorage.getInstance().getReference().child("mentorImages/" + UUID.randomUUID().toString())
//        ref.putFile(fileuri!!)
//            .addOnSuccessListener { taskSnapshot ->
//                // Get the download URL from the task snapshot
//                ref.downloadUrl.addOnSuccessListener { uri ->
//                    user.photoLink = uri.toString()
//                    saveUserData(user)
//                }.addOnFailureListener { exception ->
//                    Toast.makeText(this, "Failed to retrieve download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
//                }
//            }.addOnFailureListener { exception ->
//                Toast.makeText(this, "Image Upload Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun saveUserData(user: User) {
//        val currentUser = FirebaseAuth.getInstance().currentUser
//        if (currentUser != null) {
//            val userId = currentUser.uid
//            database.child("users").child(userId).setValue(user)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "Your data is updated successfully", Toast.LENGTH_SHORT).show()
//                    val intent = Intent(this, Hello::class.java)
//                    startActivity(intent)
//                }.addOnFailureListener {
//                    Toast.makeText(this, "Failed to update user data", Toast.LENGTH_SHORT).show()
//                }
//        } else {
//            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
//        }
//    }
//}
//
