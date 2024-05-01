package com.laraib.i210865

import VolleyMultipartRequest
import android.annotation.SuppressLint
import android.app.Activity.ScreenCaptureCallback
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import messageadapter
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.webkit.MimeTypeMap
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

data class User_(
    val name: String = "", // Default values for properties
    var photoLink: String = ""
) {
    // No-argument constructor required by Firebase
    constructor() : this("", "")
}

data class DataPart(
    val fileName: String,
    val data: ByteArray,
    val mimeType: String
)




@SuppressLint("NewApi")
class privatemessage : AppCompatActivity(){

    private var encodedImage: String = ""
    private lateinit var bitmap: Bitmap
    lateinit var chooseimg: ImageView
    var fileuri: Uri? = null

    private var outputFile: File? = null
    private lateinit var recorder: MediaRecorder
    private var isRecording = false
    private lateinit var mediaPlayer: MediaPlayer


    private var mediaRecorder: MediaRecorder? = null
    private var outputFileName: String = ""

    private val screenCaptureCallback = ScreenCaptureCallback {
        Toast.makeText(this, "screenshot detected", Toast.LENGTH_SHORT).show()
    }

    private lateinit var btnsend: ImageView

    private lateinit var imageView: CircleImageView
    private lateinit var username: TextView
    private lateinit var btn_send: ImageButton
    private lateinit var text_send: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var fuser: FirebaseUser
    private lateinit var madapter: messageadapter
    private var mchat = mutableListOf<ChatRV>()
    private var imageurl: String = ""
    private var audiourl: String = ""
    private val GALLERY_REQUEST_CODE=1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privatemessage)

        chooseimg = findViewById(R.id.uploadpic)
        chooseimg.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111)
        }


        val screenCaptureCallback = ScreenCaptureCallback {
            Log.d("ScreenCaptureCallback", "Screenshot detected")
            runOnUiThread {
                Toast.makeText(this, "Screenshot detected", Toast.LENGTH_LONG).show()
            }
        }

        val attachFileImageView = findViewById<ImageView>(R.id.imageView25)
        attachFileImageView.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)

        }


        mediaPlayer = MediaPlayer()
        recorder = MediaRecorder()
        btnsend     = findViewById(R.id.btnsend)

        btnsend.setOnClickListener {
            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
        }


        // Register the screen capture callback
        registerScreenCaptureCallback(mainExecutor, screenCaptureCallback)

        // Initialize media recorder and output file name
        outputFileName = "${externalCacheDir?.absolutePath}/recording.3gp"
        mediaRecorder = MediaRecorder()



        val Button11 = findViewById<ImageView>(R.id.video)
        Button11.setOnClickListener {
            val intent = Intent(this, videocall::class.java)
            startActivity(intent) }

        val Button112 = findViewById<ImageView>(R.id.call)
        Button112.setOnClickListener {
            val intent = Intent(this, voicecall::class.java)
            startActivity(intent) }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        username = findViewById(R.id.name)
        imageView = findViewById(R.id.image)
        btn_send = findViewById(R.id.btn_send)
        text_send = findViewById(R.id.text_send)


        val currentid = intent.getIntExtra("currentid",1)
        Toast.makeText(this, "Current ID: $currentid", Toast.LENGTH_SHORT).show()

        val userId = intent.getIntExtra("userid",2)
        Toast.makeText(this, "user id: $userId", Toast.LENGTH_SHORT).show()


        val userName = intent.getStringExtra("username")
        username.text = userName

        //camera
        val camera = findViewById<ImageView>(R.id.camera)
        camera.setOnClickListener {
            Dexter.withContext(applicationContext)
                .withPermissions(
                    android.Manifest.permission.CAMERA
                ).withListener(object : MultiplePermissionsListener { // Explicitly define the listener type
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        // Check if all permissions were granted
                        if (report != null && report.areAllPermissionsGranted()) {
                            // Start the camera activity
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent, 101)
                        } else {
                            Toast.makeText(this@privatemessage, "Camera permission is required", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                        // Handle rationale for permissions
                        token?.continuePermissionRequest()
                    }
                }).check()
        }


        btn_send.setOnClickListener {
            // Send message
            val msg = text_send.text.toString()
            if (msg.isNotBlank()) {
                if (userId != null) {
                    sendmessage(currentid, userId, msg)
                } else {
                    Toast.makeText(this@privatemessage, "Receiver ID is null", Toast.LENGTH_SHORT).show()
                }
            } else if (fileuri != null) {
                sendMessageWithImage(currentid, userId, encodedImage!!)
            } else {
                Toast.makeText(this@privatemessage, "You can't send an empty message", Toast.LENGTH_SHORT).show()
            }
            text_send.text = ""
        }

        readmessage(currentid, userId,imageurl)
    }

    override fun onStart() {
        super.onStart()
        registerScreenCaptureCallback(mainExecutor, screenCaptureCallback)
    }

    override fun onStop() {
        super.onStop()
        unregisterScreenCaptureCallback(screenCaptureCallback)
//        screenshotDetectionDelegate.stopScreenshotDetection()
    }

    private fun sendmessage(sender: Int, receiver: Int, message: String) {
        val url = "http://192.168.10.8/smd3/send_message.php"

        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                // Handle response
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                // Handle error
                Toast.makeText(this, "Failed to send message: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["sender"] = sender.toString()
                params["receiver"] = receiver.toString()
                params["message"] = message
                return params
            }
        }

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(request)
    }

    private fun readmessage(myid: Int, userid: Int, imageurl: String) {
        Toast.makeText(this, "Fetching messages for user ID: $userid", Toast.LENGTH_SHORT).show()
        val url = "http://192.168.10.8/smd3/readmessage.php"
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                // Handle response
                try {
                    val jsonArray = JSONArray(response)
                    mchat.clear()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val sender = jsonObject.getInt("sender")
                        val receiver = jsonObject.getInt("receiver")
                        val message = jsonObject.getString("message")
                        val image = jsonObject.getString("image")


                        // Log the contents of the chat message
                        Log.d("ChatMessage", "Sender: $sender, Receiver: $receiver, Message: $message, Image: $image")

                        val chat = ChatRV(sender, receiver, message, image)
                        mchat.add(chat)
                    }
                    // Pass mchat to the adapter after setting voiceNoteUrl for each ChatRV object
                    madapter = messageadapter(this@privatemessage, mchat, imageurl, myid)
                    recyclerView.adapter = madapter
                    // Notify the adapter that the data set has changed
                    madapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this@privatemessage, "Failed to parse JSON", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                // Handle error
                Toast.makeText(this@privatemessage, "Failed to retrieve chat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["myid"] = myid.toString()
                params["userid"] = userid.toString()
                return params
            }
        }

        // Add the request to the RequestQueue
        Volley.newRequestQueue(this).add(request)
    }


    private fun startRecording() {
        try {
            // Create a temporary file to store the recorded voice note
            outputFile = File.createTempFile("voice_note", ".3gp", cacheDir)
            // Set up the MediaRecorder
            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                outputFile?.let { setOutputFile(it.absolutePath) }
                prepare()
                start()
            }
            isRecording = true
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.e("MainActivity15", "Error starting recording: ${e.message}", e)
            // Handle IOException
        } catch (e: IllegalStateException) {
            Log.e("MainActivity15", "Error starting recording: ${e.message}", e)
            // Handle IllegalStateException
        } catch (e: Exception) {
            Log.e("MainActivity15", "Error starting recording: ${e.message}", e)
            // Handle other exceptions
        }
    }

    private fun stopRecording() {
        try {
            recorder.stop()
            recorder.release()
            isRecording = false
            outputFile?.let { outputFile ->
                // Upload the recorded audio file to Firebase Storage
                val currentid = intent.getIntExtra("currentid",1)
                Toast.makeText(this, "Current ID: $currentid", Toast.LENGTH_SHORT).show()

                val userId = intent.getIntExtra("userid",2)
                Toast.makeText(this, "user id: $userId", Toast.LENGTH_SHORT).show()
                sendMessageWithAudio(currentid, userId,outputFile)
            }
        } catch (e: IllegalStateException) {
            Log.e("MainActivity15", "Error stopping recording: ${e.message}", e)
            // Handle IllegalStateException properly
        } catch (e: Exception) {
            Log.e("MainActivity15", "Error stopping recording: ${e.message}", e)
            // Handle other exceptions
        }
    }

    // Function to upload recorded audio to your web service
//    private fun uploadAudio(audioFile: File) {
//        val url = "http://your_domain.com/upload_audio.php" // Replace with your PHP script URL
//
//        val request = object : VolleyMultipartRequest(
//            Request.Method.POST, url,
//            Response.Listener { response ->
//                try {
//                    val jsonResponse = JSONObject(response.toString())
//                    val success = jsonResponse.getInt("success")
//                    val message = jsonResponse.getString("message")
//                    if (success == 1) {
//                        val audioUrl = jsonResponse.getString("audio_url")
//                        // Once audio is uploaded, send message with audio URL
//                        sendMessageWithAudio(currentid, userId, audioUrl)
//                    } else {
//                        // Handle error
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                    // Handle JSON exception
//                }
//            },
//            Response.ErrorListener { error ->
//                // Handle error
//            }) {
//            override fun getByteData(): MutableMap<String, DataPart> {
//                val params = HashMap<String, DataPart>()
//                val audioData = audioFile.readBytes()
//                params["audio"] = DataPart("audio.3gp", audioData, "audio/3gp")
//                return params
//            }
//        }
//
//        Volley.newRequestQueue(this).add(request)
//    }


//    private fun sendMessageWithAudio(sender: String, receiver: String, audioUrl: String) {
//        val url = "http://your_domain.com/send_message_with_audio.php" // Replace with your PHP script URL
//
//        val request = object : StringRequest(
//            Method.POST, url,
//            Response.Listener { response ->
//                // Handle response
//                try {
//                    val jsonResponse = JSONObject(response)
//                    val success = jsonResponse.getInt("success")
//                    val message = jsonResponse.getString("message")
//                    if (success == 1) {
//                        // Message sent successfully
//                    } else {
//                        // Handle error
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                    // Handle JSON exception
//                }
//            },
//            Response.ErrorListener { error ->
//                // Handle error
//            }) {
//            override fun getParams(): Map<String, String> {
//                val params = HashMap<String, String>()
//                params["sender"] = sender
//                params["receiver"] = receiver
//                params["audioUrl"] = audioUrl
//                return params
//            }
//        }
//
//        Volley.newRequestQueue(this).add(request)
//    }


    private fun sendMessageWithAudio(sender: Int, receiver: Int, audioFile: File) {
        val url = "http://192.168.10.8/smd3/upload_audio.php"

        Toast.makeText(this, "Audio file is: $audioFile", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Audio Name: $audioFile")

        val request = object : VolleyMultipartRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonResponse = JSONObject(response.toString())
                    val success = jsonResponse.getInt("success")
                    val message = jsonResponse.getString("message")
                    if (success == 1) {
                        // Handle success
                        Toast.makeText(this@privatemessage, "Message sent successfully", Toast.LENGTH_SHORT).show()

                    } else {
                        // Handle error
                        Toast.makeText(this@privatemessage, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                // Handle error
                Toast.makeText(this@privatemessage, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error: ${error.message}", error)
            }) {
            override fun getByteData(): MutableMap<String, DataPart> {
                val params = HashMap<String, DataPart>()
                val audioData = audioFile.readBytes()
                params["audio"] = DataPart(audioFile.name, audioData, "audio/3gp") // Use the original file name
                return params
            }

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["sender"] = sender.toString()
                params["receiver"] = receiver.toString()
                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }




    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        recorder.release()
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun sendMessageWithImage(sender: Int, receiver: Int, image: String) {
        val url = "http://192.168.10.8/smd3/sendmessagewithimage.php"
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                // Handle response
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                // Handle error
                Toast.makeText(this, "Failed to send message: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["sender"] = sender.toString()
                params["receiver"] = receiver.toString()
                params["image"] = image // Send the Base64 encoded image data
                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            fileuri = data.data // Get the URI of the selected image
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(fileuri!!)
                bitmap = BitmapFactory.decodeStream(inputStream)
                chooseimg.setImageBitmap(bitmap)
                encodeBitmapImage(bitmap)

                // Send the image to be stored on the web
                val currentid = intent.getIntExtra("currentid", 1)
                val userId = intent.getIntExtra("userid", 2)
                sendMessageWithImage(currentid, userId, encodedImage)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        else if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            // Get the captured image
            val imageBitmap = data.extras?.get("data") as Bitmap

            // Convert the captured image to Base64
            val outputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val byteArray = outputStream.toByteArray()
            val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

            // Send the image to be stored on the web
            val currentid = intent.getIntExtra("currentid", 1)
            val userId = intent.getIntExtra("userid", 2)
            sendMessageWithImage(currentid, userId, encodedImage)
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
//import android.annotation.SuppressLint
//import android.app.Activity.ScreenCaptureCallback
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.media.MediaPlayer
//import android.media.MediaRecorder
//import android.os.Bundle
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.google.firebase.FirebaseApp
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.database.*
//import de.hdodenhof.circleimageview.CircleImageView
//import messageadapter
//import android.net.Uri
//import android.provider.MediaStore
//import android.util.Log
//import android.view.inputmethod.InputMethodManager
//import android.webkit.MimeTypeMap
//import com.google.firebase.storage.FirebaseStorage
//import java.io.File
//import java.io.IOException
//
//data class User_(
//    val name: String = "", // Default values for properties
//    var photoLink: String = ""
//) {
//    // No-argument constructor required by Firebase
//    constructor() : this("", "")
//}
//
//@SuppressLint("NewApi")
//class privatemessage : AppCompatActivity(){
//
//    private var outputFile: File? = null
//    private lateinit var recorder: MediaRecorder
//    private var isRecording = false
//    private lateinit var mediaPlayer: MediaPlayer
//
//
//    private var mediaRecorder: MediaRecorder? = null
//    private var outputFileName: String = ""
//
//    private val screenCaptureCallback = ScreenCaptureCallback {
//        Toast.makeText(this, "screenshot detected", Toast.LENGTH_SHORT).show()
//    }
//
//    private lateinit var btnsend: ImageView
//
//    private lateinit var imageView: CircleImageView
//    private lateinit var username: TextView
//    private lateinit var btn_send: ImageButton
//    private lateinit var text_send: TextView
//    private lateinit var recyclerView: RecyclerView
//
//    private lateinit var fuser: FirebaseUser
//    private lateinit var database: DatabaseReference
//
//    private lateinit var userIntent: Intent
//    private lateinit var madapter: messageadapter
//    private var mchat = mutableListOf<ChatRV>()
//    private var imageurl: String = ""
//    private var audiourl: String = ""
//    private val GALLERY_REQUEST_CODE=1001
//
////    private lateinit var mediaRecorder: MediaRecorder
////    var tempMediaOutput:String = ""
////    var mediaState:Boolean = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_privatemessage)
//
//        val screenCaptureCallback = ScreenCaptureCallback {
//            Log.d("ScreenCaptureCallback", "Screenshot detected")
//            runOnUiThread {
//                Toast.makeText(this, "Screenshot detected", Toast.LENGTH_LONG).show()
//            }
//        }
//
//        val attachFileImageView = findViewById<ImageView>(R.id.imageView25)
//        attachFileImageView.setOnClickListener {
//            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
//
//        }
//
//
//        mediaPlayer = MediaPlayer()
//        recorder = MediaRecorder()
//
//        btnsend     = findViewById(R.id.btnsend)
//
//        btnsend.setOnClickListener {
//            if (isRecording) {
//                stopRecording()
//            } else {
//                startRecording()
//            }
//        }
//
//
//        // Register the screen capture callback
//        registerScreenCaptureCallback(mainExecutor, screenCaptureCallback)
//
//        // Initialize media recorder and output file name
//        outputFileName = "${externalCacheDir?.absolutePath}/recording.3gp"
//        mediaRecorder = MediaRecorder()
//
//
//
//        val Button11 = findViewById<ImageView>(R.id.video)
//        Button11.setOnClickListener {
//            val intent = Intent(this, videocall::class.java)
//            startActivity(intent) }
//
//        val Button112 = findViewById<ImageView>(R.id.call)
//        Button112.setOnClickListener {
//            val intent = Intent(this, voicecall::class.java)
//            startActivity(intent) }
//
//        FirebaseApp.initializeApp(this)
//        database = FirebaseDatabase.getInstance().reference
//        fuser = FirebaseAuth.getInstance().currentUser!!
//
//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.title = ""
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        toolbar.setNavigationOnClickListener {
//            finish()
//        }
//
//        recyclerView = findViewById(R.id.recyclerView)
//        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        username = findViewById(R.id.name)
//        imageView = findViewById(R.id.image)
//        btn_send = findViewById(R.id.btn_send)
//        text_send = findViewById(R.id.text_send)
//
//
//
//
//        userIntent = intent
//        val receiverId = userIntent.getStringExtra("currentuserid")
//       /* val userId = userIntent.getStringExtra("userid")
//        val userName = userIntent.getStringExtra("username")*/
//
//
//        //camera
//        val camera = findViewById<ImageView>(R.id.camera)
//        camera.setOnClickListener {
//            val intent = Intent(this, photo::class.java).apply {
//                putExtra("currentuserid", receiverId)
//            }
//            startActivity(intent)
//        }
//
//
//        // Retrieve receiver's information from Firebase
//        if (receiverId != null) {
//            val refProfile = FirebaseDatabase.getInstance().getReference("users").child(receiverId)
//            refProfile.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val user = snapshot.getValue(User_::class.java)
//                    if (user != null) {
//                        // Display receiver's name
//                        username.text = user.name
//                        // Load receiver's image using Glide
//                        imageurl = user.photoLink
//                        if (!imageurl.isNullOrEmpty()) {
//                            Glide.with(this@privatemessage)
//                                .load(user.photoLink)
//                                .placeholder(R.drawable.customer)
//                                .error(R.drawable.baseline_circle_24)
//                                .into(imageView)
//                        }
//                    } else {
//                        Toast.makeText(this@privatemessage, "Receiver data is null", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@privatemessage, "Failed to retrieve receiver data", Toast.LENGTH_SHORT).show()
//                }
//            })
//        } else {
//            Toast.makeText(this@privatemessage, "Receiver ID is null", Toast.LENGTH_SHORT).show()
//        }
//
//        btn_send.setOnClickListener {
//            // Send message
//            val msg = text_send.text.toString()
//            if (msg.isNotBlank()) {
//                if (receiverId != null) {
//                    sendmessage(fuser.uid, receiverId, msg)
//                } else {
//                    Toast.makeText(this@privatemessage, "Receiver ID is null", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(this@privatemessage, "You can't send an empty message", Toast.LENGTH_SHORT).show()
//            }
//            text_send.text = ""
//        }
//
//        readmessage(fuser.uid, receiverId ?: "",imageurl,)
//    }
//
//    override fun onStart() {
//        super.onStart()
//        registerScreenCaptureCallback(mainExecutor, screenCaptureCallback)
//    }
//
//    override fun onStop() {
//        super.onStop()
//        unregisterScreenCaptureCallback(screenCaptureCallback)
////        screenshotDetectionDelegate.stopScreenshotDetection()
//    }
//
//    private fun sendmessage(sender: String, receiver: String, message: String) {
//        val reference = FirebaseDatabase.getInstance().getReference("Chats")
//        val hashMap: HashMap<String, String> = HashMap()
//        hashMap["sender"] = sender
//        hashMap["receiver"] = receiver
//        hashMap["message"] = message
//        reference.push().setValue(hashMap)
//        reference.keepSynced(true)
//        val kh =
//            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        kh.hideSoftInputFromWindow(currentFocus?.windowToken,0)
//
//}
//
//private fun readmessage(myid: String, userid: String, imageurl: String) {
//val reference = FirebaseDatabase.getInstance().getReference("Chats")
//reference.addValueEventListener(object : ValueEventListener {
//    override fun onDataChange(snapshot: DataSnapshot) {
//        mchat.clear()
//        for (snapshot in snapshot.children) {
//            val chat = snapshot.getValue(ChatRV::class.java)
//            if (chat != null) {
//                if (chat.receiver == myid && chat.sender == userid || chat.receiver == userid && chat.sender == myid) {
//                    mchat.add(chat)
//                }
//            }
//        }
//        // Fetch voice note URLs and set them in ChatRV objects
//        for (chat in mchat) {
//            val voiceNoteUrl = chat.voiceNoteUrl // Fetch voice note URL from Firebase based on your data model
//            // Set voice note URL in the ChatRV object
//            chat.voiceNoteUrl = voiceNoteUrl
//        }
//        // Pass mchat to the adapter after setting voiceNoteUrl for each ChatRV object
//        madapter = messageadapter(this@privatemessage, mchat, imageurl)
//        recyclerView.adapter = madapter
//    }
//
//    override fun onCancelled(error: DatabaseError) {
//        Toast.makeText(this@privatemessage, "Failed to retrieve chat data", Toast.LENGTH_SHORT).show()
//    }
//})
//
//reference.keepSynced(true)
//}
//
//private fun startRecording() {
//try {
//    // Create a temporary file to store the recorded voice note
//    outputFile = File.createTempFile("voice_note", ".3gp", cacheDir)
//    // Set up the MediaRecorder
//    recorder.apply {
//        setAudioSource(MediaRecorder.AudioSource.MIC)
//        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//        outputFile?.let { setOutputFile(it.absolutePath) }
//        prepare()
//        start()
//    }
//    isRecording = true
//    Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show()
//} catch (e: IOException) {
//    Log.e("MainActivity15", "Error starting recording: ${e.message}", e)
//    // Handle IOException
//} catch (e: IllegalStateException) {
//    Log.e("MainActivity15", "Error starting recording: ${e.message}", e)
//    // Handle IllegalStateException
//} catch (e: Exception) {
//    Log.e("MainActivity15", "Error starting recording: ${e.message}", e)
//    // Handle other exceptions
//}
//}
//
//
//private fun stopRecording() {
//try {
//    recorder.stop()
//    recorder.release()
//    isRecording = false
//    outputFile?.let { outputFile ->
//        // Upload the recorded audio file to Firebase Storage
//        uploadAudio(outputFile)
//    }
//} catch (e: IllegalStateException) {
//    Log.e("MainActivity15", "Error stopping recording: ${e.message}", e)
//    // Handle IllegalStateException properly
//} catch (e: Exception) {
//    Log.e("MainActivity15", "Error stopping recording: ${e.message}", e)
//    // Handle other exceptions
//}
//}
//
//private fun uploadAudio(audioFile: File) {
//val userId = intent.getStringExtra("currentuserid")!!
//val storageReference = FirebaseStorage.getInstance().reference.child("audio")
//val audioFileName = "${System.currentTimeMillis()}.3gp"
//val audioRef = storageReference.child(audioFileName)
//
//val uploadTask = audioRef.putFile(Uri.fromFile(audioFile))
//uploadTask.continueWithTask { task ->
//    if (!task.isSuccessful) {
//        task.exception?.let {
//            throw it
//        }
//    }
//    audioRef.downloadUrl
//}.addOnCompleteListener { task ->
//    if (task.isSuccessful) {
//        val downloadUri = task.result
//        // Once audio is uploaded, send message with audio URL
//        sendMessageWithAudio(fuser.uid, userId, downloadUri.toString())
//    } else {
//        // Handle failures
//    }
//}
//}
//
//
//private fun sendMessageWithAudio(sender: String, receiver: String, audioUrl: String) {
//val reference = FirebaseDatabase.getInstance().reference
//val hashMap = HashMap<String, Any>()
//hashMap["sender"] = sender
//hashMap["receiver"] = receiver
//hashMap["audioUrl"] = audioUrl
//reference.child("Chats").push().setValue(hashMap)
//}
//
//
//override fun onDestroy() {
//super.onDestroy()
//mediaPlayer.release()
//recorder.release()
//}
//
//
//private fun uploadImage(imageUri: Uri) {
//val userId = intent.getStringExtra("currentuserid")!!
//val storageReference = FirebaseStorage.getInstance().reference.child("images")
//val imageFileName = "${System.currentTimeMillis()}.${getFileExtension(imageUri)}"
//val imageRef = storageReference.child(imageFileName)
//
//val uploadTask = imageRef.putFile(imageUri)
//uploadTask.continueWithTask { task ->
//    if (!task.isSuccessful) {
//        task.exception?.let {
//            throw it
//        }
//    }
//    imageRef.downloadUrl
//}.addOnCompleteListener { task ->
//    if (task.isSuccessful) {
//        val downloadUri = task.result
//        // Once image is uploaded, send message with image URL
//        sendMessageWithImage(fuser.uid, userId, downloadUri.toString())
//    } else {
//        // Handle failures
//    }
//}
//}
//
//    private fun getFileExtension(uri: Uri): String? {
//        val contentResolver = contentResolver
//        val mimeTypeMap = MimeTypeMap.getSingleton()
//        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
//    }
//
//    private fun sendMessageWithImage(sender: String, receiver: String, image: String) {
//        val reference = FirebaseDatabase.getInstance().reference
//
//        val hashMap = HashMap<String, Any>()
//        hashMap["sender"] = sender
//        hashMap["receiver"] = receiver
//        hashMap["image"] = image
//
//        reference.child("Chats").push().setValue(hashMap)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
//            val selectedImageUri = data.data
//            if (selectedImageUri != null) {
//                // Upload the selected image to Firebase Storage
//                uploadImage(selectedImageUri)
//            }
//        }
//    }
//}
