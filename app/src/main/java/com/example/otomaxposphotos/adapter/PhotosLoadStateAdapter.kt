package com.example.otomaxposphotos.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.otomaxposphotos.R

class PhotosLoadStateAdapter(val retry: () -> Unit) : LoadStateAdapter<PhotosLoadStateAdapter.MovieLoadStateViewHolder>() {

    class MovieLoadStateViewHolder (val itemView: View, val retry: () -> Unit) : RecyclerView.ViewHolder(itemView) {
        val textViewError: TextView = itemView.findViewById(R.id.text_view_error)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressbar)
        val buttonRetry: TextView = itemView.findViewById(R.id.button_retry)

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                textViewError.text = loadState.error.localizedMessage
            }

            progressBar.isVisible = loadState is LoadState.Loading
            buttonRetry.isVisible = loadState is LoadState.Error
            textViewError.isVisible = loadState is LoadState.Error

            buttonRetry.setOnClickListener {
                retry()
            }

            progressBar.visibility = View.VISIBLE
        }
    }

    override fun onBindViewHolder(holder: MovieLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): MovieLoadStateViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_loading_state, parent, false)
        return MovieLoadStateViewHolder(layoutInflater, retry)
    }
}