package com.sanjay.trackLocation.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.sanjay.trackLocation.Database.CredentialObject
import com.sanjay.trackLocation.Modules.HomePage
import com.sanjay.trackLocation.R

class LoggedInUserRVAdapter(
    private val list: List<CredentialObject>,
    val selectedUser: (CredentialObject) -> Unit
) : RecyclerView.Adapter<LoggedInUserRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.logged_user_rv_template, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        if (item.id != HomePage.loggedInUserID) {
            holder.displayLoggedUserNameTV.text = item.name
        } else {
            holder.userCardItem.visibility = View.GONE
        }

        holder.userCardItem.setOnClickListener {
            selectedUser(item)
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userCardItem: CardView = itemView.findViewById(R.id.userCardItem)
        val displayLoggedUserNameTV: TextView = itemView.findViewById(R.id.displayLoggedUserNameTV)
    }

}