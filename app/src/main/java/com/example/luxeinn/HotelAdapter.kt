package com.example.luxeinn

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Hotel2(
    val name: String,
    val location: String,
    val services: String,
    val imageUrl: String
)

class HotelAdapter(private val context: MainActivity, private val hotelList: List<Hotel2>) : RecyclerView.Adapter<HotelAdapter.HotelViewHolder>() {

    inner class HotelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hotelName: TextView = itemView.findViewById(R.id.tv_hotel_name)
        val hotelLocation: TextView = itemView.findViewById(R.id.tv_hotel_location)
        val hotelServices: TextView = itemView.findViewById(R.id.tv_hotel_services)
        val hotelImage: ImageView = itemView.findViewById(R.id.iv_hotel_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_hotel, parent, false)
        return HotelViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        val hotel2 = hotelList[position]

        holder.hotelName.text = hotel2.name
        holder.hotelLocation.text = hotel2.location
        holder.hotelServices.text = hotel2.services

        // Cargar la imagen desde el recurso drawable
        val resourceId = context.resources.getIdentifier(hotel2.imageUrl, "drawable", context.packageName)
        if (resourceId != 0) {
            holder.hotelImage.setImageResource(resourceId)
        } else {
            holder.hotelImage.setImageResource(R.drawable.imagen1)  // Imagen predeterminada
        }

        // Configurar clic en el botón "Ver más"
        holder.itemView.findViewById<Button>(R.id.VerMas).setOnClickListener {
            val intent = Intent(context, HotelDescription::class.java)
            intent.putExtra("hotel_name", hotel2.name) // Pasar el nombre del hotel como parámetro
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return hotelList.size
    }
}