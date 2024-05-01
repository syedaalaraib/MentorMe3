package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import mentors_adapter

class Hello : AppCompatActivity() {

    private lateinit var toprv: RecyclerView
    private lateinit var educationrv: RecyclerView
    private lateinit var recentrv: RecyclerView
    val mentorsList = mutableListOf<Item_RV>()
    val adapter = mentors_adapter(mentorsList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello)

        val currentid = intent.getIntExtra("currentid",1)

        val f1 = findViewById<ImageView>(R.id.home)

        f1.setOnClickListener {
            val intent = Intent(this, Hello::class.java)
            intent.putExtra("currentid",currentid)
            startActivity(intent)
        }


        val Button11 = findViewById<ImageView>(R.id.profile)

        // Set OnClickListener for the Button
        Button11.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, profile::class.java)
            intent.putExtra("currentid",currentid)
            startActivity(intent)
        }

        val Button112 = findViewById<TextView>(R.id.profile_)

        // Set OnClickListener for the Button
        Button112.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, profile::class.java)
            intent.putExtra("currentid",currentid)
            startActivity(intent)
        }



        val Button1 = findViewById<ImageView>(R.id.chat)

        // Set OnClickListener for the Button
        Button1.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, chats::class.java)
            intent.putExtra("currentid",currentid)
            startActivity(intent)
        }

        val loginButton = findViewById<ImageView>(R.id.search)

        // Set OnClickListener for the Button
        loginButton.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, letsfind::class.java)
            intent.putExtra("currentid",currentid)
            startActivity(intent)
        }


        val B = findViewById<ImageView>(R.id.plus)

        // Set OnClickListener for the Button
        B.setOnClickListener {
            // Navigate to a new page here
            val intent = Intent(this, newmentor::class.java)
            intent.putExtra("currentid",currentid)
            startActivity(intent)
        }

        // Initialize RecyclerViews
        toprv = findViewById(R.id.top_mentors_rv)
        toprv.setHasFixedSize(true)
        toprv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        educationrv = findViewById(R.id.education_mentors_rv)
        educationrv.setHasFixedSize(true)
        educationrv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recentrv = findViewById(R.id.recent_mentors_rv)
        recentrv.setHasFixedSize(true)
        recentrv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        // Create and set adapter
        retrieveMentorsFromWebService()
        toprv.adapter = adapter
        educationrv.adapter = adapter
        recentrv.adapter = adapter


    }
    private fun retrieveMentorsFromWebService() {
        val url = "http://192.168.10.8/smd3/viewmentors.php"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                mentorsList.clear()
                val jsonArray = response.getJSONArray("mentors")
                for (i in 0 until jsonArray.length()) {
                    val mentorObject = jsonArray.getJSONObject(i)
                    val mentor = Item_RV()
                    mentor.name = mentorObject.getString("name")
                    mentor.description = mentorObject.getString("description")
                    mentor.price = mentorObject.getString("chargesPerSession")
                    mentor.status = mentorObject.getString("status")

                    mentorsList.add(mentor)
                }
                adapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(
                    this@Hello,
                    "Error retrieving data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            })

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(request)
    }

}














//package com.laraib.i210865
//
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import mentors_adapter
//
//class Hello : AppCompatActivity() {
//
//    private lateinit var toprv: RecyclerView
//    private lateinit var educationrv: RecyclerView
//    private lateinit var recentrv: RecyclerView
//    val mentorsList = mutableListOf<Item_RV>()
//    val adapter = mentors_adapter(mentorsList)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_hello)
//
//        val f1 = findViewById<ImageView>(R.id.home)
//
//        f1.setOnClickListener {
//            val intent = Intent(this, Hello::class.java)
//            startActivity(intent)
//        }
//
//
//        val Button11 = findViewById<ImageView>(R.id.profile)
//
//        // Set OnClickListener for the Button
//        Button11.setOnClickListener {
//            // Navigate to a new page here
//            val intent = Intent(this, profile::class.java)
//            startActivity(intent)
//        }
//
//        val Button112 = findViewById<TextView>(R.id.profile_)
//
//        // Set OnClickListener for the Button
//        Button112.setOnClickListener {
//            // Navigate to a new page here
//            val intent = Intent(this, profile::class.java)
//            startActivity(intent)
//        }
//
//
//
//        val Button1 = findViewById<ImageView>(R.id.chat)
//
//        // Set OnClickListener for the Button
//        Button1.setOnClickListener {
//            // Navigate to a new page here
//            val intent = Intent(this, chats::class.java)
//            startActivity(intent)
//        }
//
//        val loginButton = findViewById<ImageView>(R.id.search)
//
//        // Set OnClickListener for the Button
//        loginButton.setOnClickListener {
//            // Navigate to a new page here
//            val intent = Intent(this, letsfind::class.java)
//            startActivity(intent)
//        }
//
//
//        val B = findViewById<ImageView>(R.id.plus)
//
//        // Set OnClickListener for the Button
//        B.setOnClickListener {
//            // Navigate to a new page here
//            val intent = Intent(this, newmentor::class.java)
//            startActivity(intent)
//        }
//
//        // Initialize RecyclerViews
//        toprv = findViewById(R.id.top_mentors_rv)
//        toprv.setHasFixedSize(true)
//        toprv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        educationrv = findViewById(R.id.education_mentors_rv)
//        educationrv.setHasFixedSize(true)
//        educationrv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        recentrv = findViewById(R.id.recent_mentors_rv)
//        recentrv.setHasFixedSize(true)
//        recentrv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//
//
//        // Create and set adapter
//        retrieveMentorsFromRealtimeDatabase()
//        toprv.adapter = adapter
//        educationrv.adapter = adapter
//        recentrv.adapter = adapter
//
//
//    }
//    private fun retrieveMentorsFromRealtimeDatabase() {
//        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("mentors")
//
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                mentorsList.clear()
//                for (snapshot in dataSnapshot.children) {
//                    val mentor = snapshot.getValue(Item_RV::class.java)
//                    mentor?.let { mentorsList.add(it) }
//                }
//                adapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Toast.makeText(
//                    this@Hello,
//                    "Error retrieving data: ${databaseError.message} ",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        })
//    }
//}