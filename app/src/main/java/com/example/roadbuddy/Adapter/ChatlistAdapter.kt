package com.example.roadbuddy.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roadbuddy.R

data class Chat(val helpername: String, val helperphone: String, val helperId: String)
class ChatlistAdapter(private var chatList: List<Chat>, private val onItemClick: (Chat) -> Unit) :
    RecyclerView.Adapter<ChatlistAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val NameTextView: TextView = itemView.findViewById(R.id.NameTextView)
        val PhoneTextView: TextView = itemView.findViewById(R.id.PhoneTextView)
        
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(chatList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_tile, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        // Bind chat data to views
        holder.NameTextView.text = chat.helpername
        holder.PhoneTextView.text = chat.helperphone
    }

    override fun getItemCount(): Int = chatList.size

    fun updateChatList(newList: List<Chat>) {
        chatList = newList
        notifyDataSetChanged()
    }
}
