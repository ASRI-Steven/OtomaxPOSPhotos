package com.example.otomaxposphotos.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.otomaxposphotos.R
import com.example.otomaxposphotos.utils.PhotosListener
import com.squareup.picasso.Picasso
import com.example.otomaxposphotos.model.Data
import com.example.otomaxposphotos.utils.Utils
import java.text.NumberFormat
import java.util.*

class PhotosAdapter(val photosListener: PhotosListener) : PagingDataAdapter<Data, PhotosAdapter.ViewHolder>(PhotosComparator) {

    lateinit var context: Context

    fun setAllSelectedtoTrue() {
        snapshot().items.onEach {
            if (it.image.toString().isNotBlank()) it.isSelected = true
        }
        notifyDataSetChanged()
    }

    fun setAllSelectedtoFalse() {
        snapshot().items.onEach {
            it.isSelected = false
        }
        notifyDataSetChanged()
    }

    object PhotosComparator : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_photos, parent, false)
        return ViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photos = getItem(position)
        photos?.let {
            holder.bind(it)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }

    fun getSelectedPhotos(): List<Data> {
        val selectedPhotos: MutableList<Data> = mutableListOf()
        for (photos in snapshot().items) {
            if (photos.isSelected!!) selectedPhotos.add(photos)
        }
        return selectedPhotos
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.text)
        val cardView: CardView = itemView.findViewById(R.id.cardView)

        fun bind(data: Data) {
            if (data.image.toString().isNotBlank()) {
                Picasso.get().load(data.image).placeholder(R.drawable.placeholder).into(imageView)
                textView.text = data.name
                if (data.isSelected!!) cardView.setCardBackgroundColor(Color.parseColor("#F5EF89"))
                else cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))

                itemView.setOnClickListener {
                    if (data.isSelected!!) {
                        cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
                        data.isSelected = false
                        photosListener.onPhotosCountResult(getSelectedPhotos().size)
                        if (getSelectedPhotos().isEmpty()) {
                            photosListener.onPhotosShowAction(false)
                        }
                    } else {
                        cardView.setCardBackgroundColor(Color.parseColor("#F5EF89"))
                        data.isSelected = true
                        photosListener.onPhotosShowAction(true)
                        photosListener.onPhotosCountResult(getSelectedPhotos().size)
//                        if (getSelectedPhotos().size <= 29) {
//                            cardView.setCardBackgroundColor(Color.parseColor("#F5EF89"))
//                            data.isSelected = true
//                            photosListener.onPhotosShowAction(true)
//                            photosListener.onPhotosCountResult(getSelectedPhotos().size)
//                        } else {
//                            Toast.makeText(context, "Tidak bisa lebih dari 30 gambar", Toast.LENGTH_SHORT).show()
//                        }
                    }
                }

                itemView.setOnLongClickListener {
                    val builder = AlertDialog.Builder(context).create()
                    val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_adapter_dialog, null)
                    builder.setView(dialogView)
                    builder.setCancelable(true)
                    builder.setIcon(R.mipmap.ic_launcher)
                    val imageViewDetail: ImageView = dialogView.findViewById(R.id.imageViewDetail)
                    val textViewNama: TextView = dialogView.findViewById(R.id.textViewNama)
                    val textViewHargaBeli: TextView = dialogView.findViewById(R.id.textViewHargaBeli)
                    val textViewHargaJual: TextView = dialogView.findViewById(R.id.textViewHargaJual)
                    val buttonAddCart: Button = dialogView.findViewById(R.id.buttonAddCart)

                    Picasso.get().load(data.image).placeholder(R.drawable.placeholder).into(imageViewDetail)
                    textViewNama.text = data.name
                    textViewHargaBeli.text = Utils.convertNumberThreeDots(data.buyprice!!)
                    textViewHargaJual.text = Utils.convertNumberThreeDots(data.sellprice!!)
                    buttonAddCart.setOnClickListener {
                        photosListener.onPhotosSendCart(data)
                        builder.dismiss()
                    }
                    builder.show()

                    true
                }
            } else {
                Picasso.get().load(R.drawable.placeholder).placeholder(R.drawable.placeholder).into(imageView)
                textView.text = data.name

                cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))

                itemView.setOnClickListener(null)

                itemView.setOnLongClickListener {
                    val builder = AlertDialog.Builder(context).create()
                    val dialogView = LayoutInflater.from(context).inflate(R.layout.custom_adapter_dialog, null)
                    builder.setView(dialogView)
                    builder.setCancelable(true)
                    builder.setIcon(R.mipmap.ic_launcher)
                    val imageViewDetail: ImageView = dialogView.findViewById(R.id.imageViewDetail)
                    val textViewNama: TextView = dialogView.findViewById(R.id.textViewNama)
                    val textViewHargaBeli: TextView = dialogView.findViewById(R.id.textViewHargaBeli)
                    val textViewHargaJual: TextView = dialogView.findViewById(R.id.textViewHargaJual)
                    val buttonAddCart: Button = dialogView.findViewById(R.id.buttonAddCart)

                    Picasso.get().load(R.drawable.placeholder).placeholder(R.drawable.placeholder).into(imageViewDetail)
                    textViewNama.text = data.name
                    textViewHargaBeli.text = Utils.convertNumberThreeDots(data.buyprice!!)
                    textViewHargaJual.text = Utils.convertNumberThreeDots(data.sellprice!!)
                    buttonAddCart.setOnClickListener {
                        photosListener.onPhotosSendCart(data)
                        builder.dismiss()
                    }
                    builder.show()

                    true
                }
            }
        }
    }
}