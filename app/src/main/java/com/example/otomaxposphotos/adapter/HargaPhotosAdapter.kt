package com.example.otomaxposphotos.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.otomaxposphotos.R
import com.example.otomaxposphotos.model.CartPhotos
import com.example.otomaxposphotos.model.Data
import com.example.otomaxposphotos.utils.CartPhotosListener
import com.example.otomaxposphotos.utils.Utils

class HargaPhotosAdapter(val cartPhotosListener: CartPhotosListener) : ListAdapter<CartPhotos, HargaPhotosAdapter.HargaPhotosViewHolder>(DiffCallback) {

    var isSelectMode = false
    var selectedId: ArrayList<Int> = arrayListOf()
    var selectedItems: ArrayList<CartPhotos> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HargaPhotosViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_harga, parent, false)
        return HargaPhotosViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: HargaPhotosViewHolder, position: Int) {
        val cartPhotos = getItem(position)
        cartPhotos.let {
            holder.bind(it)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<CartPhotos>() {

        override fun areItemsTheSame(oldItem: CartPhotos, newItem: CartPhotos): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartPhotos, newItem: CartPhotos): Boolean {
            return oldItem.name == newItem.name
        }

    }

    inner class HargaPhotosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleCart: TextView = itemView.findViewById(R.id.titleCart)
        val textViewHargaBeliCart: TextView = itemView.findViewById(R.id.textViewHargaBeliCart)
        val textViewHargaJualCart: TextView = itemView.findViewById(R.id.textViewHargaJualCart)
        val buttonMinus: ImageButton = itemView.findViewById(R.id.buttonMinus)
        val textViewJumlah: TextView = itemView.findViewById(R.id.textViewJumlah)
        val buttonPlus: ImageButton = itemView.findViewById(R.id.buttonPlus)

        fun bind(cartPhotos: CartPhotos) {
            titleCart.text = cartPhotos.name
            textViewHargaBeliCart.text = Utils.convertNumberToThreeDots(cartPhotos.totalBuyprice!!)
            textViewHargaJualCart.text = Utils.convertNumberToThreeDots(cartPhotos.totalSellprice!!)
            textViewJumlah.text = cartPhotos.quantity.toString()
            buttonMinus.setOnClickListener {
                if (cartPhotos.quantity!! <= 1) {
                    cartPhotos.quantity = 1
                } else {
                    cartPhotos.quantity = cartPhotos.quantity?.minus(1)
                    cartPhotos.totalBuyprice = cartPhotos.buyprice?.times(cartPhotos.quantity!!)
                    cartPhotos.totalSellprice = cartPhotos.sellprice?.times(cartPhotos.quantity!!)
                    cartPhotosListener.onUpdateCartPhoto(cartPhotos)
                    textViewHargaBeliCart.text = Utils.convertNumberToThreeDots(cartPhotos.totalBuyprice!!)
                    textViewHargaJualCart.text = Utils.convertNumberToThreeDots(cartPhotos.totalSellprice!!)
                }
                textViewJumlah.text = cartPhotos.quantity.toString()
            }

            buttonPlus.setOnClickListener {
                cartPhotos.quantity = cartPhotos.quantity?.plus(1)
                cartPhotos.totalBuyprice = cartPhotos.buyprice?.times(cartPhotos.quantity!!)
                cartPhotos.totalSellprice = cartPhotos.sellprice?.times(cartPhotos.quantity!!)
                cartPhotosListener.onUpdateCartPhoto(cartPhotos)
                textViewHargaBeliCart.text = Utils.convertNumberToThreeDots(cartPhotos.totalBuyprice!!)
                textViewHargaJualCart.text = Utils.convertNumberToThreeDots(cartPhotos.totalSellprice!!)
                textViewJumlah.text = cartPhotos.quantity.toString()
            }

            itemView.setOnLongClickListener {
                isSelectMode = true

                if (selectedItems.contains(getItem(absoluteAdapterPosition))) {
                    itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    selectedId.remove(getItem(absoluteAdapterPosition).id!!)
                    selectedItems.remove(getItem(absoluteAdapterPosition))
                    cartPhotosListener.onDeleteCartPhotos(isSelectMode, selectedId)
                } else {
                    itemView.setBackgroundColor(Color.parseColor("#F5EF89"))
                    selectedId.add(getItem(absoluteAdapterPosition).id!!)
                    selectedItems.add(getItem(absoluteAdapterPosition))
                    cartPhotosListener.onDeleteCartPhotos(isSelectMode, selectedId)
                }

                if (selectedItems.isEmpty()) {
                    isSelectMode = false
                    cartPhotosListener.onDeleteCartPhotos(isSelectMode, selectedId)
                }

                true
            }

            itemView.setOnClickListener {
                Log.v("testse", isSelectMode.toString())
                if (isSelectMode) {
                    if (selectedItems.contains(getItem(absoluteAdapterPosition))) {
                        itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                        selectedId.remove(getItem(absoluteAdapterPosition).id!!)
                        selectedItems.remove(getItem(absoluteAdapterPosition))
                        cartPhotosListener.onDeleteCartPhotos(isSelectMode, selectedId)
                    } else {
                        itemView.setBackgroundColor(Color.parseColor("#F5EF89"))
                        selectedId.add(getItem(absoluteAdapterPosition).id!!)
                        selectedItems.add(getItem(absoluteAdapterPosition))
                        cartPhotosListener.onDeleteCartPhotos(isSelectMode, selectedId)
                    }

                    if (selectedItems.isEmpty()) {
                        isSelectMode = false
                        cartPhotosListener.onDeleteCartPhotos(isSelectMode, selectedId)
                    }
                }
            }
        }
    }
}