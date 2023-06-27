package com.example.wevote.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.wevote.R
import com.example.wevote.data.Candidate

class CandidateAdapter(private val listener:(Candidate) -> Unit) :
    androidx.recyclerview.widget.ListAdapter<Candidate,CandidateAdapter.ViewHolder>(DiffCallback()){

    inner class ViewHolder(containerView:View)
        :RecyclerView.ViewHolder(containerView){

            init {

                itemView.setOnClickListener {
                    listener.invoke(getItem(adapterPosition))
                }

            }

        private val candidateImage:ImageView = containerView.findViewById(R.id.CandidateProfile)
        private val candidateName:TextView = containerView.findViewById(R.id.CandidateName)
        private val partyName:TextView = containerView.findViewById(R.id.CandidateParty)

        fun bind(countryData:Candidate){
            with(countryData){
                candidateImage.setImageResource(imageId)
                candidateName.text = CandidateName
                partyName.text = PartyName
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.candidate_list,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class DiffCallback : DiffUtil.ItemCallback<Candidate>() {
    override fun areItemsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
        return oldItem.imageId == newItem.imageId
    }

    override fun areContentsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }
}