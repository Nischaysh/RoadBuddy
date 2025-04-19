package com.example.roadbuddy.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roadbuddy.R

data class Chat(
    val helpername: String,
    val helperphone: String,
    val helperId: String
)

class ChatlistAdapter(
    private var chatList: List<Chat>,
    private val onItemClick: (Chat) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_CHAT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_CHAT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_EMPTY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.empty_chatlist_state, parent, false)
                EmptyViewHolder(view)
            }
            VIEW_TYPE_CHAT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_tile, parent, false)
                ChatViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChatViewHolder -> {
                val chat = chatList[position]
                holder.bind(chat)
            }
            is EmptyViewHolder -> {
                // No binding needed for empty state
            }
        }
    }

    override fun getItemCount(): Int {
        return if (chatList.isEmpty()) 1 else chatList.size
    }

    fun updateChatList(newList: List<Chat>) {
        chatList = newList
        notifyDataSetChanged()
    }

    private inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val helperName: TextView = itemView.findViewById(R.id.NameTextView)
        private val helperPhone: TextView = itemView.findViewById(R.id.PhoneTextView)

        fun bind(chat: Chat) {
            helperName.text = chat.helpername
            helperPhone.text = chat.helperphone
            itemView.setOnClickListener { onItemClick(chat) }
        }
    }
}
