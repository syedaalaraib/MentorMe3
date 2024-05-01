package com.laraib.i210865


import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.laraib.i210865.Chat
import com.laraib.i210865.R
import java.io.IOException

class Message_Adapter(private val mChat: List<Chat>, private val mContext: Context, private val imageURL: String, var onclick:onClickMessage) :
    RecyclerView.Adapter<Message_Adapter.ViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private val MSG_TYPE_LEFT = 0
    private val MSG_TYPE_RIGHT = 1
    private var fuser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MSG_TYPE_RIGHT) {
            val view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false)
            ViewHolder(view)
        } else {
            val view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false)
            ViewHolder(view)
        }
    }




    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = mChat[position]
        var isSent = false
        holder.show_message.text = chat.message
        if (chat.imageUrl.isNullOrEmpty()) {
            // If imageUrl is null or empty, display text message
            holder.show_message.visibility = View.VISIBLE
            holder.chatImage.visibility = View.GONE
            holder.show_message.text = chat.message
            isSent = true
        } else {
            // If imageUrl is not empty, display image
            holder.show_message.visibility = View.GONE
            holder.chatImage.visibility = View.VISIBLE
            Glide.with(mContext).load(chat.imageUrl).into(holder.chatImage)
        }

        // Check if audioUrl is not null or empty to show the voice note icon
        if (!chat.audioUrl.isNullOrEmpty()) {
            holder. Voicenoteurl.visibility = View.VISIBLE
            // Set click listener to play audio when the voice note icon is clicked
            holder. Voicenoteurl.setOnClickListener {
                val audioUrl = chat.audioUrl
                if (!audioUrl.isNullOrEmpty()) {
                    playAudio(audioUrl)
                }
            }
        } else {
            // Hide the voice note icon if voice note is not sent
            holder. Voicenoteurl.visibility = View.GONE
        }

        holder.btn_more.setOnClickListener {
            onclick.onItemClick(mChat[position].key,isSent,holder.btn_more)
        }
    }

    private fun playAudio(audioUrl: String) {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
                reset()
            }
            setDataSource(audioUrl)
            prepare()
            start()
        } ?: run {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioUrl)
                prepare()
                start()
            }
        }
    }



    public interface onClickMessage {
        public fun onItemClick(key: String,isSent:Boolean,view: ImageView)
    }

    override fun getItemCount(): Int {
        return mChat.size
    }

    override fun getItemViewType(position: Int): Int {
        fuser = FirebaseAuth.getInstance().currentUser
        return if (mChat[position].sender == fuser?.uid) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatImage: ImageView = itemView.findViewById(R.id.profileimage)
        var show_message: TextView = itemView.findViewById(R.id.showmessage)
        var imageURL: ImageView = itemView.findViewById(R.id.profileimage)
        var btn_more: ImageView = itemView.findViewById(R.id.btn_send)
        var Voicenoteurl: ImageView = itemView.findViewById(R.id.btn_send)


    }
}