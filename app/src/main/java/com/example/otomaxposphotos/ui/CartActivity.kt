package com.example.otomaxposphotos.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.otomaxposphotos.BaseApplication
import com.example.otomaxposphotos.R
import com.example.otomaxposphotos.adapter.CartPhotosAdapter
import com.example.otomaxposphotos.model.CartPhotos
import com.example.otomaxposphotos.utils.CartPhotosListener
import com.example.otomaxposphotos.utils.Utils
import com.example.otomaxposphotos.viewmodel.CartPhotosViewModel
import com.example.otomaxposphotos.viewmodel.CartPhotosViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartActivity : AppCompatActivity(), CartPhotosListener {

    private lateinit var buttonDelete: Button
    private lateinit var buttonReset: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var title: TextView
    private lateinit var cartPhotosAdapter: CartPhotosAdapter
    private lateinit var textViewSubtotalHargaJual: TextView
    private val cartPhotosViewModel: CartPhotosViewModel by viewModels {
        CartPhotosViewModelFactory(
            (this@CartActivity.application as BaseApplication).database.photosDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.recyclerViewCart)
        buttonDelete = findViewById(R.id.buttonDeleteCart)
        buttonReset = findViewById(R.id.buttonResetCart)
        textViewSubtotalHargaJual = findViewById(R.id.textViewSubtotalHargaJualCart)
        toolbar = findViewById(R.id.toolbar)
        title = findViewById(R.id.title)

        cartPhotosAdapter = CartPhotosAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)

        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        title.text = resources.getString(R.string.cart)

        cartPhotosViewModel.cartPhotos.observe(this) {
            if (it.isNotEmpty()) {
                cartPhotosAdapter.submitList(it)
                buttonReset.visibility = View.VISIBLE
                textViewSubtotalHargaJual.text = resources.getString(R.string.subtotal, Utils.convertNumberToThreeDots(it.sumOf { price ->
                    price.totalSellprice!!
                }))
                textViewSubtotalHargaJual.visibility = View.VISIBLE
            } else {
                cartPhotosAdapter.submitList(it)
                buttonReset.visibility = View.GONE
                buttonDelete.visibility = View.GONE
                textViewSubtotalHargaJual.visibility = View.GONE
            }
        }

        recyclerView.adapter = cartPhotosAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, LinearLayoutManager(applicationContext).orientation))

        buttonReset.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    cartPhotosViewModel.deleteAllPhotos()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            startActivity(Intent(this@CartActivity, MainActivity::class.java))
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onUpdateCartPhoto(cartPhotos: CartPhotos) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                cartPhotosViewModel.updatePhoto(cartPhotos)
            }
        }
    }

    override fun onDeleteCartPhotos(isSelected: Boolean, id: ArrayList<Int>) {
        if (isSelected && id.isNotEmpty()) {
            buttonDelete.visibility = View.VISIBLE
            buttonReset.visibility = View.GONE

            buttonDelete.setOnClickListener {
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        cartPhotosViewModel.deletePhotos(id)
                    }
                }
                buttonReset.visibility = View.VISIBLE
                it.visibility = View.GONE
                finish()
                startActivity(intent)
            }
        } else {
            buttonDelete.visibility = View.GONE
            buttonReset.visibility = View.VISIBLE
        }
    }
}