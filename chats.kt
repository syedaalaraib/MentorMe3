package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import user_adapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class chats : AppCompatActivity() {

    private lateinit var toprv: RecyclerView
    val userList = mutableListOf<User_rv>()
    private var currentid: Int = 1
    lateinit var adapter: user_adapter

//    val adapter = user_adapter(userList, this,currentid)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)


        currentid = intent.getIntExtra("currentid", 1)

        // Initialize adapter with 'currentid'
        adapter = user_adapter(userList, this, currentid)

        val f = findViewById<ImageView>(R.id.back)

        f.setOnClickListener {
            val intent = Intent(this, Hello::class.java)
            startActivity(intent)
        }

        val fi = findViewById<ImageView>(R.id.search)

        fi.setOnClickListener {
            val intent = Intent(this, letsfind::class.java)
            startActivity(intent)
        }


//        val fip = findViewById<TextView>(R.id.johncooper)
//
//        fip.setOnClickListener {
//            val intent = Intent(this, privatemessage::class.java)
//            startActivity(intent)
//        }

        val fip1 = findViewById<TextView>(R.id.community)

        fip1.setOnClickListener {
            val intent = Intent(this, communitymessage::class.java)
            startActivity(intent)
        }


        val f1 = findViewById<ImageView>(R.id.home)

        f1.setOnClickListener {
            val intent = Intent(this, Hello::class.java)
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
        val f5 = findViewById<ImageView>(R.id.plus)

        f5.setOnClickListener {
            val intent = Intent(this, newmentor::class.java)
            startActivity(intent)
        }

        // Initialize RecyclerViews
        toprv = findViewById(R.id.top_mentors_rv)
        toprv.setHasFixedSize(true)
        toprv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        // Create and set adapter
        retrieveMentorsFromWebService()
        toprv.adapter = adapter

        // Rest of your code for handling button clicks
    }

    private fun retrieveMentorsFromWebService() {
        // Replace "YOUR_WEB_SERVICE_URL_HERE" with the actual URL of your web service
        val url = "http://192.168.10.8/smd3/showmentors.php"

        // Create a JSON array request to fetch data from the web service
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                // Clear the existing list of users
                userList.clear()

                // Parse the JSON response
                for (i in 0 until response.length()) {
                    val userJson = response.getJSONObject(i)

                    // Extract user data from JSON
                    val name = userJson.getString("name")
                    val uid = userJson.getString("uid")
//                    val photoLink = userJson.getInt("photolink")

                    // Create a User_rv object and add it to the list
                    val user = User_rv()
                    user.name = name
                    user.uid = uid
//                    user.photolink = photoLink
                    userList.add(user)
                }

                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged()
            },
            { error ->
                // Handle error
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        // Add the request to the request queue
        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }

}
























//package com.laraib.i210865
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
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
//import user_adapter
//
//class chats : AppCompatActivity() {
//
//    private lateinit var toprv: RecyclerView
//    val userList = mutableListOf<User_rv>()
//    val adapter = user_adapter(userList, this)
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_chats)
//
//        val f = findViewById<ImageView>(R.id.back)
//
//        f.setOnClickListener {
//            val intent = Intent(this, Hello::class.java)
//            startActivity(intent)
//        }
//
//        val fi = findViewById<ImageView>(R.id.search)
//
//        fi.setOnClickListener {
//            val intent = Intent(this, letsfind::class.java)
//            startActivity(intent)
//        }
//
//
////        val fip = findViewById<TextView>(R.id.johncooper)
////
////        fip.setOnClickListener {
////            val intent = Intent(this, privatemessage::class.java)
////            startActivity(intent)
////        }
//
//        val fip1 = findViewById<TextView>(R.id.community)
//
//        fip1.setOnClickListener {
//            val intent = Intent(this, communitymessage::class.java)
//            startActivity(intent)
//        }
//
//
//        val f1 = findViewById<ImageView>(R.id.home)
//
//        f1.setOnClickListener {
//            val intent = Intent(this, Hello::class.java)
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
//        val f5 = findViewById<ImageView>(R.id.plus)
//
//        f5.setOnClickListener {
//            val intent = Intent(this, newmentor::class.java)
//            startActivity(intent)
//        }
//
//        // Initialize RecyclerViews
//        toprv = findViewById(R.id.top_mentors_rv)
//        toprv.setHasFixedSize(true)
//        toprv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//
//        // Create and set adapter
//        retrieveMentorsFromRealtimeDatabase()
//        toprv.adapter = adapter
//    }
//    private fun retrieveMentorsFromRealtimeDatabase() {
//        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
//
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                userList.clear()
//                for (snapshot in dataSnapshot.children) {
//                    // Log the snapshot key to check if it contains the user ID
//                    Log.d("SnapshotKey", "Snapshot key: ${snapshot.key}")
//
//                    val mentor = snapshot.getValue(User_rv::class.java)
//
//                    // Get the user ID from the snapshot key
//                    val userId = snapshot.key
//
//                    // Set the user ID to the mentor object if not null
//                    mentor?.let {
//                        it.uid = userId
//                        userList.add(it)
//                    }
//                }
//                adapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Toast.makeText(
//                    this@chats,
//                    "Error retrieving data: ${databaseError.message} ",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        })
//    }
//
//
//}