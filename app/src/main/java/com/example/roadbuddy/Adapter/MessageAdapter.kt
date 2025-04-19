package com.example.roadbuddy.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roadbuddy.Model.Message
import com.example.roadbuddy.R
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private val currentUserId: String) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val messages = mutableListOf<Message>()

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.messageText)
        val messageTime: TextView = itemView.findViewById(R.id.messageTime)
        val messageStatus: TextView = itemView.findViewById(R.id.messageStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        try {
            val layout = if (viewType == 1) R.layout.item_message_sent else R.layout.item_message_received
            val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
            return MessageViewHolder(view)
        } catch (e: Exception) {
            // Fallback to a simple layout if there's an error
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return MessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        try {
            val message = messages[position]
            holder.messageText.text = message.text
            holder.messageTime.text = formatTime(message.timestamp)

            // Show seen status only for sent messages
            if (message.senderId == currentUserId) {
                holder.messageStatus?.visibility = View.VISIBLE
                holder.messageStatus?.text = if (message.seen) "Seen" else "Sent"
            } else {
                holder.messageStatus?.visibility = View.GONE
            }
        } catch (e: Exception) {
            // Handle any binding errors gracefully
            holder.messageText.text = "Error loading message"
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) 1 else 0
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun updateMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    private fun formatTime(timestamp: Long): String {
        val time = Date(timestamp)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(time)
    }
} 