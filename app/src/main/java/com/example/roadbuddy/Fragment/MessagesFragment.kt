package com.example.helperbuddy.Fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roadbuddy.R

class MessagesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private lateinit var messagesAdapter: MessagesAdapter
    private val messagesList = mutableListOf<String>()  // Placeholder for messages

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMessages)
        editTextMessage = view.findViewById(R.id.editTextMessage)
        buttonSend = view.findViewById(R.id.buttonSend)

        // Initialize the RecyclerView and Adapter
        messagesAdapter = MessagesAdapter(messagesList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = messagesAdapter

        // Send button click listener
        buttonSend.setOnClickListener {
            Log.d("MessagesFragment", "Send button clicked")
            val message = editTextMessage.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                editTextMessage.text.clear()
                Toast.makeText(context, "Message sent: $message", Toast.LENGTH_SHORT).show()
                respondWithBotMessage(message)  // Call to respond with bot message
            } else {
                Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun sendMessage(message: String) {
        messagesList.add("You: $message")  // Add user message to list
        messagesAdapter.notifyDataSetChanged()
        recyclerView.scrollToPosition(messagesList.size - 1)  // Scroll to the latest message
    }

    private fun respondWithBotMessage(userMessage: String) {
        // Delay the bot's reply to simulate typing
        Handler(Looper.getMainLooper()).postDelayed({
            val botMessage = getBotResponse(userMessage)
            messagesList.add("Buddy: $botMessage")  // Add bot's response
            messagesAdapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(messagesList.size - 1)  // Scroll to the latest message
        }, 1000)  // 1-second delay to simulate thinking
    }

    // Generate bot response based on user input
    private fun getBotResponse(message: String): String {
        return when {
            message.contains("hello", ignoreCase = true) -> "Hello! How can I assist you today?"
            message.contains("hlo", ignoreCase = true) -> "Hello! How can I assist you today?"
            message.contains("hey", ignoreCase = true) -> "Hello! How can I assist you today?"
            message.contains("hi", ignoreCase = true) -> "Hello! How can I assist you today?"
            message.contains("hii", ignoreCase = true) -> "Hello! How can I assist you today?"
            message.contains("help", ignoreCase = true) -> "Sure! Please tell me what you need help with."
            message.contains("help me", ignoreCase = true) -> "Sure! Please tell me what you need help with."

            else -> "I’m sorry, I didn’t quite understand. Could you please rephrase?"
        }
    }

    // Adapter for displaying messages
    class MessagesAdapter(private val messagesList: List<String>) :
        RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            return MessageViewHolder(view)
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            holder.bind(messagesList[position])
        }

        override fun getItemCount(): Int {
            return messagesList.size
        }

        class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(message: String) {
                (itemView as TextView).text = message
            }
        }
    }
}
