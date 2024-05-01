package com.laraib.i210865

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class letsfind : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letsfind)

        val find = findViewById<EditText>(R.id.find)
        find.setOnClickListener {
            val mentorName = find.text.toString()
            searchMentorByName(mentorName)
        }


        val f = findViewById<Button>(R.id.searchbutton)
        // Set OnClickListener for the Button
        f.setOnClickListener {
            // Navigate to a new page here
            val mentorName = find.text.toString()
            searchMentorByName(mentorName)
//            val intent = Intent(this, searchresults::class.java)
//            startActivity(intent)
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
    }

    private fun searchMentorByName(name: String) {
        val url = "http://192.168.10.8/smd3/letsfind.php?searchQuery=$name"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val mentorId = response.optInt("mentorId", -1)
                if (mentorId != -1) {
                    // Mentor found, navigate to search results activity
                    Toast.makeText(this, "Mentor ID: $mentorId", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, searchresults::class.java).apply {
                        putExtra("MENTOR_ID", mentorId)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Mentor not found", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(
                    this,
                    "Error searching mentor: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            })

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(request)
    }

}