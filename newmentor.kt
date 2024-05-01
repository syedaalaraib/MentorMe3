package com.laraib.i210865
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

// Define a class to represent a mentor
data class Mentor(
    val name: String,
    val description: String,
    val status: String,
    val chargesPerSession: Double,
    var photoLink: String
)

class newmentor : AppCompatActivity() {

    lateinit var chooseimg: ImageView
    var fileuri: Uri? = null
    private var encodedImage: String = ""
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newmentor)

        chooseimg = findViewById(R.id.camera)
        chooseimg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111)
        }

        val countrySpinner: Spinner = findViewById(R.id.statusSpinner)
        val countries = listOf("Select Status", "Available", "Busy", "On leave")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        countrySpinner.adapter = adapter

        val uploadButton = findViewById<TextView>(R.id.upload)
        uploadButton.setOnClickListener {

            // Check if an image is selected
            if (fileuri == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get mentor data from input fields
            val nameText = findViewById<EditText>(R.id.entername).text.toString()
            val descriptionText = findViewById<EditText>(R.id.enterdescription).text.toString()
            val chargesText = findViewById<EditText>(R.id.entercash).text.toString().toDoubleOrNull() ?: 0.0
            val statusText = countrySpinner.selectedItem.toString()


            // Create mentor object
            val mentor = Mentor(nameText, descriptionText, statusText, chargesText, fileuri.toString())


            // Save image to Firebase Storage
            val editmap = mapOf(
                "name" to nameText,
                "description" to descriptionText,
                "status" to statusText,
                "chargesPerSession" to chargesText,
                "photoLink" to encodedImage
            )
//            saveMentorData(editmap, encodedImage)
            saveMentorData(nameText, descriptionText, statusText, chargesText.toString(),encodedImage)
            val intent = Intent(this@newmentor, Hello::class.java)
            startActivity(intent)
        }
    }

    private fun saveMentorData(name: String, description: String, status: String, chargesPerSession: String, encodedImage: String) {
        val url = "http://192.168.10.8/smd3/addmentors.php"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
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
                params["description"] = description
                params["status"] = status
                params["chargesPerSession"] = chargesPerSession
                params["image"] = encodedImage
                return params


            }
        }
        requestQueue.add(stringRequest)
    }

//    private fun saveMentorData(editMap: Map<String, Any>, encodedImage: String) {
//        val nameText = editMap["name"] ?: ""
//        val descriptionText = editMap["description"] ?: ""
//        val statusText = editMap["status"] ?: ""
//        val chargesPerSessionText = editMap["chargesPerSession"] ?: ""
//
//        val request = object : StringRequest(
//            Method.POST, "http://192.168.10.8/smd3/addmentors.php",
//            Response.Listener { response ->
//                // Handle response
//                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, Hello::class.java)
//                startActivity(intent)
//            },
//            Response.ErrorListener { error ->
//                // Handle error
//                Toast.makeText(this, "Failed to update user profile: ${error.message}", Toast.LENGTH_SHORT).show()
//                Log.e("VolleyError", "Failed to update user profile", error) // Log the error message
//            }) {
//            override fun getParams(): MutableMap<String, String> {
//                val params = HashMap<String, String>()
//                params["name"] = nameText.toString()
//                params["description"] = descriptionText.toString()
//                params["status"] = statusText.toString()
//                params["chargesPerSession"] = chargesPerSessionText.toString()
//                params["image"] = encodedImage // Add image data to the request
//                return params
//            }
//        }
//
//        // Add the request to the RequestQueue
//        Volley.newRequestQueue(this).add(request)
//    }

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
//import android.content.Intent
//import android.net.Uri
//import android.os.AsyncTask
//import android.os.Bundle
//import android.provider.MediaStore
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import org.json.JSONObject
//import java.io.*
//import java.net.HttpURLConnection
//import java.net.URL
//import java.util.*
//
//// Define a class to represent a mentor
//data class Mentor(
//    val name: String,
//    val description: String,
//    val status: String,
//    val chargesPerSession: Double,
//    var photoLink: String
//)
//
//class newmentor : AppCompatActivity() {
//
//    lateinit var chooseImg: ImageView
//    var fileUri: Uri? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_newmentor)
//
//        chooseImg = findViewById(R.id.camera)
//        chooseImg.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111)
//        }
//
//        val countrySpinner: Spinner = findViewById(R.id.statusSpinner)
//        val countries = listOf("Select Status", "Available", "Busy", "On leave")
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        countrySpinner.adapter = adapter
//
//        val uploadButton = findViewById<TextView>(R.id.upload)
//        uploadButton.setOnClickListener {
//
//            // Check if an image is selected
//            if (fileUri == null) {
//                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // Get mentor data from input fields
//            val nameText = findViewById<EditText>(R.id.entername).text.toString()
//            val descriptionText = findViewById<EditText>(R.id.enterdescription).text.toString()
//            val chargesText = findViewById<EditText>(R.id.entercash).text.toString().toDoubleOrNull() ?: 0.0
//            val statusText = countrySpinner.selectedItem.toString()
//
//            // Create mentor object
//            val mentor = Mentor(nameText, descriptionText, statusText, chargesText, fileUri.toString())
//
//            // Upload mentor data to server
//            UploadMentorTask().execute(mentor)
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 111 && resultCode == RESULT_OK && data != null) {
//            fileUri = data.data
//            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
//                chooseImg.setImageBitmap(bitmap)
//            } catch (e: Exception) {
//                Toast.makeText(this, "Exception", Toast.LENGTH_SHORT).show()
//                e.printStackTrace()
//            }
//        }
//    }
//
//    private inner class UploadMentorTask : AsyncTask<Mentor, Void, String>() {
//        override fun doInBackground(vararg mentors: Mentor): String {
//            val mentor = mentors[0]
//            val url = URL("http://192.168.10.8/smd3/addmentors.php")
//
//            val urlConnection = url.openConnection() as HttpURLConnection
//            urlConnection.requestMethod = "POST"
//            urlConnection.connectTimeout = 5000
//            urlConnection.doOutput = true
//
//            // Create parameter string
//            val postData = StringBuilder()
//            postData.append("name=").append(mentor.name)
//            postData.append("&description=").append(mentor.description)
//            postData.append("&status=").append(mentor.status)
//            postData.append("&chargesPerSession=").append(mentor.chargesPerSession)
//            postData.append("&photo=").append(mentor.photoLink)
//
//            // Write parameters to connection
//            val out = BufferedWriter(OutputStreamWriter(urlConnection.outputStream, "UTF-8"))
//            out.write(postData.toString())
//            out.flush()
//            out.close()
//
//            // Get response from server
//            val responseCode = urlConnection.responseCode
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                // Successful upload
//                return "Mentor added successfullyyyyyyy"
//            } else {
//                // Error handling
//                return "ErrorYYYYGYGYGY: HTTP $responseCode"
//            }
//        }
//
//        override fun onPostExecute(result: String) {
//            Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
//            // Navigate back to the previous activity
//            val intent = Intent(applicationContext, Hello::class.java)
//            startActivity(intent)
//        }
//    }
//
//}






















//
//
//package com.laraib.i210865
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.FirebaseApp
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.storage
//import java.util.*
//
//// Define a class to represent a mentor
//data class Mentor(
//    val name: String,
//    val description: String,
//    val status: String,
//    val chargesPerSession: Double,
//    var photoLink: String
//)
//
//class newmentor : AppCompatActivity() {
//
//    lateinit var chooseimg: ImageView
//    var fileuri: Uri? = null
////    private var storageRef = FirebaseStorage.getInstance().reference
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_newmentor)
//
//        chooseimg = findViewById(R.id.camera)
//        chooseimg.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "image/*"
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111)
//        }
//
//        val countrySpinner: Spinner = findViewById(R.id.statusSpinner)
//        val countries = listOf("Select Status", "Available", "Busy", "On leave")
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        countrySpinner.adapter = adapter
//
//        val uploadButton = findViewById<TextView>(R.id.upload)
//        uploadButton.setOnClickListener {
//
//            // Check if an image is selected
//            if (fileuri == null) {
//                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // Get mentor data from input fields
//            val nameText = findViewById<EditText>(R.id.entername).text.toString()
//            val descriptionText = findViewById<EditText>(R.id.enterdescription).text.toString()
//            val chargesText = findViewById<EditText>(R.id.entercash).text.toString().toDoubleOrNull() ?: 0.0
//            val statusText = countrySpinner.selectedItem.toString()
//
//
//            // Create mentor object
//            val mentor = Mentor(nameText, descriptionText, statusText, chargesText, fileuri.toString())
//
//
//            // Save image to Firebase Storage
//            uploadImage(mentor)
//        }
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
//    private fun uploadImage(mentor: Mentor) {
//        val ref = FirebaseStorage.getInstance().getReference().child("mentorImages/" + UUID.randomUUID().toString())
//        ref.putFile(fileuri!!)
//            .addOnSuccessListener { taskSnapshot ->
//                // Get the download URL from the task snapshot
//                ref.downloadUrl.addOnSuccessListener { uri ->
//                    mentor.photoLink = uri.toString()
//                    saveMentorData(mentor)
//                }
//                    .addOnFailureListener { exception ->
//                        Toast.makeText(this, "Failed to retrieve download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
//                    }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(this, "Image Upload Failed: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//
//    private fun saveMentorData(mentor: Mentor) {
//        FirebaseApp.initializeApp(this)
//        val database = FirebaseDatabase.getInstance()
//        val mentorsRef = database.getReference("mentors")
//        mentorsRef.push().setValue(mentor)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Mentor data saved successfully", Toast.LENGTH_SHORT).show()
//                // Navigate back to the previous activity
//                val intent = Intent(this, Hello::class.java)
//                startActivity(intent)
//            }
//            .addOnFailureListener {
//                Toast.makeText(this, "Failed to save mentor data", Toast.LENGTH_SHORT).show()
//            }
//
//        mentorsRef.keepSynced(true)
//
//    }
//}