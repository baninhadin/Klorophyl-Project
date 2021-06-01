package com.example.sociogreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ListLeaderboardAdapter(private val listUser: ArrayList<User>) : RecyclerView.Adapter<ListLeaderboardAdapter.ListViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_leaderboard, viewGroup, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = listUser[position]
        Glide.with(holder.itemView.context)
            .load("https://klorophyl-project.herokuapp.com/${user.avatar}")
            .apply(RequestOptions().override(60, 60))
            .into(holder.imgPhoto)
        holder.tvUsername.text = user.username
        holder.tvPoints.text = user.points.toString()
    }

    override fun getItemCount(): Int = listUser.size

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView = itemView.findViewById(R.id.username_list)
        var tvPoints: TextView = itemView.findViewById(R.id.poin_list)
        var imgPhoto: ImageView = itemView.findViewById(R.id.avatar_list)
    }
}