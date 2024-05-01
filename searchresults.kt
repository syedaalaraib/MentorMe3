package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide

class searchresults : AppCompatActivity() {
    private lateinit var userIntent: Intent
    private lateinit var mentorName_: TextView
    private lateinit var mentorDescription_: TextView
    private lateinit var mentorStatus_: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchresults)

        // Initialize TextViews
        mentorName_ = findViewById(R.id.name)
        mentorDescription_ = findViewById(R.id.description)
        mentorStatus_ = findViewById(R.id.status)

        userIntent = intent
        val mentorId = userIntent.getIntExtra("MENTOR_ID", 1)
        if (mentorId != null) {
            Toast.makeText(this, "Mentor ID: $mentorId", Toast.LENGTH_SHORT).show()
            getMentorById(mentorId.toInt())
        } else {
            Toast.makeText(this, "Mentor ID is null", Toast.LENGTH_SHORT).show()
        }

        val f1 = findViewById<ImageView>(R.id.home)
        f1.setOnClickListener {
            val intent = Intent(this, Hello::class.java)
            startActivity(intent)
        }

        val f2 = findViewById<ImageView>(R.id.search)
        f2.setOnClickListener {
            val intent = Intent(this, letsfind::class.java)
            startActivity(intent)
        }

        val f3 = findViewById<ImageView>(R.id.chat)
        f3.setOnClickListener {
            val intent = Intent(this, chats::class.java)
            startActivity(intent)
        }

        val f4 = findViewById<ImageView>(R.id.profile)
        f4.setOnClickListener {
            val intent = Intent(this, profile::class.java)
            startActivity(intent)
        }

        val profilepic = findViewById<ImageView>(R.id.profilepic)
        profilepic.setOnClickListener {
            val intent = Intent(this, dropreview::class.java)
            intent.putExtra("MENTOR_ID", mentorId)
            startActivity(intent)
        }
    }

    private fun getMentorById(mentorId: Int) {
        val url = "http://192.168.10.8/smd3/getmentorbyid.php?mentorId=$mentorId"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val mentorName = response.optString("name")
                val mentorDescription = response.optString("description")
                val mentorStatus = response.optString("status")
                val mentorChargesPerSession = response.optDouble("chargesPerSession")
                val pic = response.getString("photoLink")

                // Set retrieved data to TextViews
                mentorName_.text = mentorName
                mentorDescription_.text = mentorDescription
                mentorStatus_.text = mentorStatus

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















//class searchresults : AppCompatActivity() {
//    private lateinit var userIntent: Intent
//    private lateinit var mentorName_: String
//    private lateinit var mentorDescription_: String
//    private lateinit var mentorStatus_: String
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_searchresults)
//
//        userIntent = intent
//        val mentorId = userIntent.getIntExtra("MENTOR_ID",1)
//        if (mentorId != null) {
//            Toast.makeText(this, "Mentor ID: $mentorId", Toast.LENGTH_SHORT).show()
//            getMentorById(mentorId.toInt())
//        } else {
//            Toast.makeText(this, "Mentor ID is null", Toast.LENGTH_SHORT).show()
//        }
//
//
//
//
//        val f1 = findViewById<ImageView>(R.id.home)
//
//        f1.setOnClickListener {
//            val intent = Intent(this, Hello::class.java)
//            startActivity(intent)
//        }
//
//        val f2 = findViewById<ImageView>(R.id.search)
//
//        f2.setOnClickListener {
//            val intent = Intent(this, letsfind::class.java)
//            startActivity(intent)
//        }
//
//        val f3 = findViewById<ImageView>(R.id.chat)
//
//        f3.setOnClickListener {
//            val intent = Intent(this, chats::class.java)
//            startActivity(intent)
//        }
//
//        val f4 = findViewById<ImageView>(R.id.profile)
//
//        f4.setOnClickListener {
//            val intent = Intent(this, profile::class.java)
//            startActivity(intent)
//        }
//    }
//
//    private fun getMentorById(mentorId: Int) {
//        val url = "http://192.168.10.8/smd3/getmentorbyid.php?mentorId=$mentorId"
//
//        val request = JsonObjectRequest(
//            Request.Method.GET, url, null,
//            { response ->
//                var mentorName = response.optString("name")
//                val mentorDescription = response.optString("description")
//                val mentorStatus = response.optString("status")
//                val mentorChargesPerSession = response.optDouble("chargesPerSession")
//                val mentorPhotoLink = response.optString("photoLink")
//
//                mentorName_=findViewById<TextView>(R.id.name)
//                mentorDescription_=findViewById<TextView>(R.id.description)
//                mentorStatus_=findViewById<TextView>(R.id.status)
//
//                // Set retrieved data to TextViews
//                mentorName_.text = mentorName
//                mentorDescription_.text = mentorDescription
//                mentorStatus_.text = mentorStatus
//
//                // Store data for future use
//                mentorName_ = mentorName
//                mentorDescription_ = mentorDescription
//                mentorStatus_ = mentorStatus
//
//                // You can also use the other mentor data as needed
//
//            },
//            { error ->
//                Toast.makeText(
//                    this,
//                    "Error retrieving mentor information: ${error.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            })
//
//        // Add the request to the RequestQueue
//        Volley.newRequestQueue(this).add(request)
//    }
//
//
//}