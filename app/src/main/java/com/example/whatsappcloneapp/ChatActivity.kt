package com.example.whatsappcloneapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private lateinit var messageList: ArrayList<Message>
    private lateinit var adapter: MessageAdapter
    private lateinit var boxMessage: EditText
    private lateinit var sendMessage: ImageView
    private lateinit var dbRef: DatabaseReference


    var receiveRoom: String? = null
    var sentRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        recycler = findViewById(R.id.chatRecycler)
        messageList = ArrayList()
        adapter = MessageAdapter(this@ChatActivity,messageList)
        boxMessage = findViewById(R.id.message)
        sendMessage = findViewById(R.id.send)
        dbRef = FirebaseDatabase.getInstance().reference

        val name = intent.getStringExtra("name")
        val receiveUid = intent.getStringExtra("uid")
        val sentUid = FirebaseAuth.getInstance().currentUser?.uid

        receiveRoom = sentUid + receiveUid
        sentRoom = receiveUid + sentUid

        supportActionBar?.title = name
        recycler.layoutManager = LinearLayoutManager(this@ChatActivity)
        recycler.adapter = adapter






        dbRef.child("chats").child(sentRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{


                override fun onDataChange(snapshot: DataSnapshot) {
                    val newMessageList = ArrayList<Message>()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        newMessageList.add(message!!)
                    }

                    adapter.data = newMessageList
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // İptal durumunda yapılacak işlemler
                }
            })

        dbRef.child("chats").child(sentRoom!!).child("messages")
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Message::class.java)

                    // Veri listesine yeni mesajı ekle
                    message?.let { adapter.addData(it) }

                    // Adapter'a yeniden atama yap
                    adapter.notifyDataSetChanged()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // Değiştirilme durumunda yapılacak işlemler
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    // Silinme durumunda yapılacak işlemler
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    // Taşınma durumunda yapılacak işlemler
                }

                override fun onCancelled(error: DatabaseError) {
                    // İptal durumunda yapılacak işlemler
                }
            })
        sendMessage.setOnClickListener {
                if (boxMessage.text.isEmpty() || boxMessage.text.isBlank()){
                sendMessage.setImageResource(R.drawable.send)
            }else{
                sendMessage.setImageResource(R.drawable.send)
                val message = boxMessage.text.toString()
                val messageObject = Message(message,sentUid)

                dbRef.child("chats").child(sentRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        dbRef.child("chats").child(receiveRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                boxMessage.setText("")
            }
        }
    }
}