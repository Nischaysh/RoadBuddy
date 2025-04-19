package com.example.roadbuddy.Activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roadbuddy.Adapter.MessageAdapter
import com.example.roadbuddy.Model.Message
import com.example.roadbuddy.databinding.ActivityChatBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.apply
import kotlin.collections.forEach
import kotlin.text.isNotEmpty
import kotlin.text.trim
import kotlin.to

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var currentUserId: String
    private lateinit var otherUserId: String
    private lateinit var chatId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get current user ID with proper null check
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please sign in to continue", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        currentUserId = currentUser.uid

        // Get the other user's ID from intent with proper null check
        otherUserId = intent.getStringExtra("USER_ID").toString()
        if (otherUserId.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid chat", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Create a unique chat ID
        chatId = if (currentUserId < otherUserId) {
            "$currentUserId-$otherUserId"
        } else {
            "$otherUserId-$currentUserId"
        }

        setupRecyclerView()
        setupClickListeners()
        loadMessages()
        loadOtherUserName()
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(currentUserId)
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            adapter = messageAdapter
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.sendButton.setOnClickListener {
            val messageText = binding.messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                binding.messageInput.text.clear()
            }
        }
    }

    private fun sendMessage(text: String) {
        val message = hashMapOf(
            "text" to text,
            "senderId" to currentUserId,
            "timestamp" to System.currentTimeMillis(),
            "seen" to false
        )

        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .add(message)
            .addOnSuccessListener {
                // Message sent successfully
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error sending message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadMessages() {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(this, "Error loading messages: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val messages = mutableListOf<Message>()
                snapshot?.documents?.forEach { doc ->
                    val text = doc.getString("text") ?: ""
                    val senderId = doc.getString("senderId") ?: ""
                    val timestamp = doc.getLong("timestamp") ?: 0L
                    val seen = doc.getBoolean("seen") ?: false
                    messages.add(Message(text, senderId, "", timestamp, seen))
                }
                messageAdapter.updateMessages(messages)
                binding.messagesRecyclerView.scrollToPosition(messages.size - 1)

                // Mark messages as seen
                markMessagesAsSeen()
            }
    }

    private fun markMessagesAsSeen() {
        // Get all unread messages from the other user
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .whereEqualTo("senderId", otherUserId)
            .whereEqualTo("seen", false)
            .get()
            .addOnSuccessListener { documents ->
                // Update each message to mark as seen
                documents.forEach { doc ->
                    doc.reference.update("seen", true)
                }
            }
    }

    private fun loadOtherUserName() {
        db.collection("users")
            .document(otherUserId)
            .get()
            .addOnSuccessListener { document ->
                val name = document.getString("name") ?: "User"
                binding.chatUserName.text = name
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading user name: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
} 