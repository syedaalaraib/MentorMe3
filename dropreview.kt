package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide

class dropreview : AppCompatActivity() {

    private lateinit var userIntent: Intent
    private lateinit var mentorName_: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dropreview)

        mentorName_ = findViewById(R.id.name)
        userIntent = intent
        val mentorId = userIntent.getIntExtra("MENTOR_ID", 1)
        if (mentorId != null) {
            Toast.makeText(this, "Mentor ID: $mentorId", Toast.LENGTH_SHORT).show()
            getMentorById(mentorId.toInt())
        } else {
            Toast.makeText(this, "Mentor ID is null", Toast.LENGTH_SHORT).show()
        }


        val f = findViewById<ImageView>(R.id.back)

        f.setOnClickListener {
            val intent = Intent(this, john::class.java)
            startActivity(intent)
        }

        val f5 = findViewById<TextView>(R.id.submit)

        f5.setOnClickListener {
            addreview(mentorId, findViewById<TextView>(R.id.review).text.toString())
        }


    }

    private fun addreview(userId: Int?, review: String) {
        val request = object : StringRequest(
            Method.POST, "http://192.168.10.8/smd3/addreview.php",
            Response.Listener { response ->
                // Handle response
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                // Optionally, you can navigate to another activity after adding the review
                 val intent = Intent(this, Hello::class.java)
                 startActivity(intent)
            },
            Response.ErrorListener { error ->
                // Handle error
                Toast.makeText(this, "Error adding review: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["id"] = userId?.toString() ?: ""  // Use "mentorId" instead of "id"
                params["review"] = review
                return params
            }
        }

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(request)
    }


    private fun getMentorById(mentorId: Int) {
        val url = "http://192.168.10.8/smd3/getmentorbyid.php?mentorId=$mentorId"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val mentorName = response.optString("name")
                val pic = response.getString("photoLink")

                // Set retrieved data to TextViews
                mentorName_.text = mentorName

                val profilepic = findViewById<ImageView>(R.id.profilepic)
                // You can also use the other mentor data as needed

                if (!pic.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(pic)
                        .placeholder(R.drawable.customer) // Optional placeholder image
                        .error(R.drawable.baseline_circle_24) // Optional error image
                        .into(profilepic)
                }

            },
            { error ->
                Toast.makeText(
                    this,
                    "Error retrieving mentor information: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            })

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(request)
    }
}