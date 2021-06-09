package com.example.klorophyl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.klorophyl.R
import com.example.klorophyl.model.Challenge

class ChallengeAdapter (private val list: ArrayList<Challenge>) : RecyclerView.Adapter<ChallengeAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback (onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_challenge, viewGroup, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val challenge = list[position]
        holder.tvName.text = challenge.name
        holder.tvDesc.text = challenge.description
        holder.tvPoints.text = challenge.points.toString()
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(list[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = list.size

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_challenge_name)
        var tvDesc: TextView = itemView.findViewById(R.id.tv_challenge_desc)
        var tvPoints: TextView = itemView.findViewById(R.id.tv_challenge_point)
    }

    interface OnItemClickCallback {
        fun onItemClicked(challenge: Challenge)
    }
}