package com.example.travellog

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterClass(val context: Context, val record: ArrayList<RecordModel>): RecyclerView.Adapter<AdapterClass.ViewHolderClass>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return record.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {

        val currentCard = record[position]
        val bitmap = BitmapFactory.decodeByteArray(currentCard.image, 0, currentCard.image.size)
        holder.cardImage.setImageBitmap(bitmap)
        holder.cardTitle.text = currentCard.title
        holder.cardContinent.text = currentCard.continent
        holder.cardCountry.text = currentCard.country
        holder.cardDate.text = currentCard.date
        holder.cardTime.text = currentCard.time
    }

    class ViewHolderClass(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardImage: ImageView = itemView.findViewById(R.id.image)
        val cardTitle: TextView = itemView.findViewById(R.id.card_title)
        val cardContinent: TextView = itemView.findViewById(R.id.continent_card_label)
        val cardCountry: TextView = itemView.findViewById(R.id.country_card_label)
        val cardDate: TextView = itemView.findViewById(R.id.date_card_label)
        val cardTime: TextView = itemView.findViewById(R.id.time_card_label)
    }

}