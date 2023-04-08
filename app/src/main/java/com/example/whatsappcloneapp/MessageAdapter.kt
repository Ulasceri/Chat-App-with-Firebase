package com.example.whatsappcloneapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth


class MessageAdapter(val context: Context, var data: List<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val RECEIVE = 1
    val SENT = 2

    inner class ReceiveViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val receiveMessage = view.findViewById<TextView>(R.id.receive)
    }

    inner class sentViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val sentMessage = view.findViewById<TextView>(R.id.sent)
    }

    fun addData(message: Message) {
        data = data + message
        notifyItemInserted(data.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1){
            val view: View = LayoutInflater.from(context).inflate(
                R.layout.receive,
                parent,
                false
            )
            return ReceiveViewHolder(view)
        }else{
            val view : View = LayoutInflater.from(context).inflate(
                R.layout.sent,
                parent,
                false
            )
            return sentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = data[position]

        if (holder.javaClass == sentViewHolder::class.java){
            val sentHolder = holder as sentViewHolder
            holder.sentMessage.text = currentMessage.message
        }else if(holder.javaClass == ReceiveViewHolder::class.java){
            val receiveHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun getItemViewType(position: Int): Int {
        val currentMessage = data[position]

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return SENT
        }else{
            return RECEIVE
        }
    }

}