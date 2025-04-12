package com.example.roadbuddy.Fragment

import Request
import RequestAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roadbuddy.Activity.ChatActivity
import com.example.roadbuddy.Adapter.Chat
import com.example.roadbuddy.Adapter.ChatlistAdapter
import com.example.roadbuddy.R
import com.example.roadbuddy.databinding.FragmentChatlistBinding
import com.example.roadbuddy.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatlistFragment : Fragment() {

    private var _binding: FragmentChatlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatlistAdapter: ChatlistAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Firestore and Auth
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatlistBinding.inflate(inflater, container, false)

        // Set up RecyclerView after inflating the view
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatlistAdapter = ChatlistAdapter(listOf()) { chat ->
            // Handle item click
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra("USER_ID", chat.helperId)
            startActivity(intent)
        }
        binding.recyclerView.adapter = chatlistAdapter

        fetchchatlist()
        return binding.root
    }

    private fun fetchchatlist() {
        val currentUserId = auth.currentUser?.uid ?: return

        db.collection("requests")
            .whereNotEqualTo("helperId", null) // Make sure helperId exists
            .orderBy("timestamp", Query.Direction.DESCENDING) // Order by newest first
            .addSnapshotListener { documents, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Error fetching chats: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (documents != null) {
                    // Use a Map to track unique helpers and their most recent chat
                    val uniqueChats = mutableMapOf<String, Chat>()

                    documents.forEach { doc ->
                        val userId = doc.getString("userId")
                        val helperId = doc.getString("helperId") ?: ""

                        // Check if current user is involved and helperId is not empty
                        if (userId == currentUserId && helperId.isNotEmpty()) {
                            val helperName = doc.getString("helperName") ?: "Not assigned"
                            val helperPhone = doc.getString("helperPhone") ?: "Not assigned"
                            
                            // Only add if we haven't seen this helper before
                            if (!uniqueChats.containsKey(helperId)) {
                                uniqueChats[helperId] = Chat(helperName, helperPhone, helperId)
                            }
                        }
                    }

                    // Convert map values to list and update adapter
                    chatlistAdapter.updateChatList(uniqueChats.values.toList())
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}