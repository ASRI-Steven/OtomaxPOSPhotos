package com.example.otomaxposphotos.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.otomaxposphotos.BaseApplication
import com.example.otomaxposphotos.R
import com.example.otomaxposphotos.adapter.PhotosAdapter
import com.example.otomaxposphotos.adapter.PhotosLoadStateAdapter
import com.example.otomaxposphotos.model.Data
import com.example.otomaxposphotos.utils.PhotosListener
import com.example.otomaxposphotos.viewmodel.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.lang.RuntimeException

class MainActivity : AppCompatActivity(), PhotosListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var buttonShare: Button
    private lateinit var title: TextView
    private lateinit var photosAdapter: PhotosAdapter
    private lateinit var search: EditText
    private lateinit var textViewNoData: TextView
    private lateinit var layoutChip: LinearLayout
    private lateinit var chipGroup: ChipGroup
    private lateinit var textViewReset: TextView
    private lateinit var textViewLoading: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var layoutManager: GridLayoutManager
    private var searchKategori: String = ""
    private var searchRing: String = ""
    private var searchLebar: String = ""
    private var searchEt: String = ""
    private var searchPcd: String = ""
    private var searchUkuranban: String = ""
    private var searchAksesoris: String = ""
    private var doubleBackToExitPressedOnce = false
    private var uriList: ArrayList<Uri> = ArrayList()
    private var categoriesMap = mutableMapOf<Int, String>()
    private var ringMap = mutableMapOf<Int, String>()
    private var lebarMap = mutableMapOf<Int, String>()
    private var etMap = mutableMapOf<Int, String>()
    private var pcdMap = mutableMapOf<Int, String>()
    private var ukuranbanMap = mutableMapOf<Int, String>()
    private var aksesorisMap = mutableMapOf<Int, String>()
    private var filterPhotos: ArrayList<String> = ArrayList()
    private var filterPhotosName: ArrayList<String> = ArrayList()
    private val cartPhotosViewModel: CartPhotosViewModel by viewModels {
        CartPhotosViewModelFactory(
            (this@MainActivity.application as BaseApplication).database.photosDao()
        )
    }

    @SuppressLint("DiscouragedPrivateApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseAnalytics.getInstance(this)
        throw RuntimeException("Test Crash")

        recyclerView = findViewById(R.id.recyclerView)
        buttonShare = findViewById(R.id.buttonShare)
        title = findViewById(R.id.title)
        search = findViewById(R.id.editText)
        textViewNoData = findViewById(R.id.textViewNoData)
        layoutChip = findViewById(R.id.layoutChip)
        chipGroup = findViewById(R.id.chipGroup)
        textViewReset = findViewById(R.id.textViewReset)
        textViewLoading = findViewById(R.id.textViewLoading)
        toolbar = findViewById(R.id.toolbar)

        toolbar.title = ""
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)

        val photosViewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
        val searchPhotosViewModel = ViewModelProvider(this).get(SearchPhotosViewModel::class.java)
        val filterPhotosViewModel = ViewModelProvider(this).get(FilterPhotosViewModel::class.java)
        val categoriesPhotosViewModel = ViewModelProvider(this).get(CategoriesPhotosViewModel::class.java)
        val ringPhotosViewModel = ViewModelProvider(this).get(RingPhotosViewModel::class.java)
        val lebarPhotosViewModel = ViewModelProvider(this).get(LebarPhotosViewModel::class.java)
        val etPhotosViewModel = ViewModelProvider(this).get(EtPhotosViewModel::class.java)
        val pcdPhotosViewModel = ViewModelProvider(this).get(PcdPhotosViewModel::class.java)
        val ukuranbanPhotosViewModel = ViewModelProvider(this).get(UkuranbanPhotosViewModel::class.java)
        val aksesorisPhotosViewModel = ViewModelProvider(this).get(AksesorisPhotosViewModel::class.java)

        photosAdapter = PhotosAdapter(this)
        val footerAdapter = PhotosLoadStateAdapter {
            photosAdapter.retry()
        }
        layoutManager = GridLayoutManager(applicationContext, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.smoothScrollToPosition(0)
        layoutManager.scrollToPositionWithOffset(0, 0)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == photosAdapter.itemCount  && footerAdapter.itemCount > 0) {
                    2
                } else {
                    1
                }
            }
        }
        recyclerView.adapter = photosAdapter.withLoadStateFooter(
            footerAdapter
        )
        recyclerView.setHasFixedSize(true)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                photosViewModel.photos.collectLatest { pagedData ->
                    photosAdapter.submitData(pagedData)
                }
            }
        }

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                recyclerView.smoothScrollToPosition(0)
                layoutManager.scrollToPositionWithOffset(0, 0)
                photosViewModel.changeSelect(false)
                if (s!!.isEmpty() && filterPhotos.isEmpty()) {
                    lifecycleScope.launch {
                        photosViewModel.photos.collectLatest { pagedData ->
                            photosAdapter.submitData(pagedData)
                            photosAdapter.setAllSelectedtoFalse()
                            buttonShare.visibility = View.GONE
                            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                            title.text = getString(R.string.otomaxpos_photos)
                        }
                    }
                } else if (s.isEmpty() && filterPhotos.isNotEmpty()) {
                    val result = filterPhotos.filter { filter -> filter.isNotBlank() }.joinToString(", ")
                    lifecycleScope.launch {
                        filterPhotosViewModel.getFilterPhotos(result).collectLatest { pagedData ->
                            photosAdapter.submitData(pagedData)
                            photosAdapter.setAllSelectedtoFalse()
                            buttonShare.visibility = View.GONE
                            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                            title.text = getString(R.string.otomaxpos_photos)
                        }
                    }
                } else if (s.isNotEmpty() && filterPhotos.isEmpty()) {
                    lifecycleScope.launch {
                        searchPhotosViewModel.getSearchPhotos(s.toString(), null).collectLatest { pagedData ->
                            photosAdapter.submitData(pagedData)
                        }
                    }
                    lifecycleScope.launch {
                        photosAdapter.loadStateFlow.distinctUntilChangedBy {
                            it.refresh
                        }.collect {
                            if (photosAdapter.snapshot().items.isEmpty()) {
                                textViewNoData.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                            } else {
                                textViewNoData.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE
                                photosAdapter.setAllSelectedtoFalse()
                                buttonShare.visibility = View.GONE
                                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                                title.text = getString(R.string.otomaxpos_photos)
                            }
                        }
                    }
                } else if (s.isNotEmpty() && filterPhotos.isNotEmpty()) {
                    lifecycleScope.launch {
                        searchPhotosViewModel.getSearchPhotos(s.toString(), filterPhotos.filter { filter -> filter.isNotBlank() }.joinToString(", ")).collectLatest { pagedData ->
                            photosAdapter.submitData(pagedData)
                        }
                    }
                    lifecycleScope.launch {
                        photosAdapter.loadStateFlow.distinctUntilChangedBy {
                            it.refresh
                        }.collect {
                            if (photosAdapter.snapshot().items.isEmpty()) {
                                textViewNoData.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                            } else {
                                textViewNoData.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE
                                photosAdapter.setAllSelectedtoFalse()
                                buttonShare.visibility = View.GONE
                                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                                title.text = getString(R.string.otomaxpos_photos)
                            }
                        }
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        buttonShare.setOnClickListener {
            if (checkPermission()) {
                buttonShare.isEnabled = false
                buttonShare.text = getString(R.string.loading)
                search.visibility = View.GONE
                layoutChip.visibility = View.GONE
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                recyclerView.visibility = View.GONE
                textViewLoading.visibility = View.VISIBLE
                convertUrlToUri(photosAdapter.snapshot().items.filter {
                    it.isSelected!!
                })
            } else {
                requestPermission()
            }
        }

        categoriesPhotosViewModel.categories.observe(this) {
            for (photo in it.data) {
                categoriesMap[photo.id!!] = photo.name!!
            }
        }

        ringPhotosViewModel.ring.observe(this) {
            for (photo in it.data) {
                ringMap[photo.id!!] = photo.name!!
            }
        }

        lebarPhotosViewModel.lebar.observe(this) {
            for (photo in it.data) {
                lebarMap[photo.id!!] = photo.name!!
            }
        }

        etPhotosViewModel.et.observe(this) {
            for (photo in it.data) {
                etMap[photo.id!!] = photo.name!!
            }
        }

        pcdPhotosViewModel.pcd.observe(this) {
            for (photo in it.data) {
                pcdMap[photo.id!!] = photo.name!!
            }
        }

        ukuranbanPhotosViewModel.ukuranban.observe(this) {
            for (photo in it.data) {
                ukuranbanMap[photo.id!!] = photo.name!!
            }
        }

        aksesorisPhotosViewModel.aksesoris.observe(this) {
            for (photo in it.data) {
                aksesorisMap[photo.id!!] = photo.name!!
            }
        }

        textViewReset.setOnClickListener {
            chipGroup.removeAllViews()
            layoutChip.visibility = View.GONE
            textViewNoData.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            filterPhotos = ArrayList()
            filterPhotosName = ArrayList()
            if (search.text.toString().isNotBlank()) {
                lifecycleScope.launch {
                    searchPhotosViewModel.getSearchPhotos(search.text.toString(), filterPhotos.filter { filter -> filter.isNotBlank() }.joinToString(", ")).collectLatest { pagedData ->
                        photosAdapter.submitData(pagedData)
                        photosAdapter.setAllSelectedtoFalse()
                        buttonShare.visibility = View.GONE
                        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                        title.text = getString(R.string.otomaxpos_photos)
                    }
                }
            } else {
                lifecycleScope.launch {
                    photosViewModel.photos.collectLatest { pagedData ->
                        photosAdapter.submitData(pagedData)
                        photosAdapter.setAllSelectedtoFalse()
                        buttonShare.visibility = View.GONE
                        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                        title.text = getString(R.string.otomaxpos_photos)
                    }
                }
            }
            recyclerView.smoothScrollToPosition(0)
            layoutManager.scrollToPositionWithOffset(0, 0)
        }

        requestPermission()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val photosViewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
        photosViewModel.isSelect.observe(this) {
            menu!!.clear()
            val inflater = menuInflater
            if (it) {
                inflater.inflate(R.menu.menu_two, menu)
            }
            else {
                inflater.inflate(R.menu.menu_one, menu)
                val menuCart = menu.findItem(R.id.cart)
                val actionView = menuCart.actionView
                val textCartitemCount = actionView.findViewById(R.id.cart_badge) as TextView
                cartPhotosViewModel.totalPhotos.observe(this) {
                    if (it == 0L) {
                        textCartitemCount.visibility = View.GONE
                    } else if (it > 999L){
                        textCartitemCount.text = "999+"
                        textCartitemCount.visibility = View.VISIBLE
                    } else {
                        textCartitemCount.text = it.toString()
                        textCartitemCount.visibility = View.VISIBLE
                    }
                }
                actionView.setOnClickListener {
                    onOptionsItemSelected(menuCart)
                }
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            val photosViewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
            photosViewModel.changeSelect(false)
            photosAdapter.setAllSelectedtoFalse()
            buttonShare.visibility = View.GONE
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            title.text = getString(R.string.otomaxpos_photos)
            true
        }
        R.id.cart -> {
            startActivity(Intent(this, CartActivity::class.java))
            true
        }
        R.id.filter -> {
            this.currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
            val builder = AlertDialog.Builder(this)
            val dialogView = LayoutInflater.from(this@MainActivity).inflate(R.layout.custom_dialog, null)
            builder.setView(dialogView)
            builder.setCancelable(true)
            builder.setIcon(R.mipmap.ic_launcher)

            val categoriesList = categoriesMap.toList().map { categories ->
                categories.second
            }
            val ringList = ringMap.toList().map { ring ->
                ring.second
            }
            val lebarList = lebarMap.toList().map { lebar ->
                lebar.second
            }
            val etList = etMap.toList().map { et ->
                et.second
            }
            val pcdList = pcdMap.toList().map { pcd ->
                pcd.second
            }
            val ukuranbanList = ukuranbanMap.toList().map { ukuranban ->
                ukuranban.second
            }
            val aksesorisList = aksesorisMap.toList().map { aksesoris ->
                aksesoris.second
            }

            val spinnerKategori: AutoCompleteTextView = dialogView.findViewById(R.id.spinnerKategori)
            val spinnerRing: AutoCompleteTextView = dialogView.findViewById(R.id.spinnerRing)
            val spinnerLebar: AutoCompleteTextView = dialogView.findViewById(R.id.spinnerLebar)
            val spinnerEt: AutoCompleteTextView = dialogView.findViewById(R.id.spinnerEt)
            val spinnerPcd: AutoCompleteTextView = dialogView.findViewById(R.id.spinnerPcd)
            val spinnerUkuranban: AutoCompleteTextView = dialogView.findViewById(R.id.spinnerUkuranban)
            val spinnerAksesoris: AutoCompleteTextView = dialogView.findViewById(R.id.spinnerAksesoris)

            val adapterKategori = ArrayAdapter(this, R.layout.list_item_dialog, categoriesList)
            val adapterRing = ArrayAdapter(this, R.layout.list_item_dialog, ringList)
            val adapterLebar = ArrayAdapter(this, R.layout.list_item_dialog, lebarList)
            val adapterEt = ArrayAdapter(this, R.layout.list_item_dialog, etList)
            val adapterPcd = ArrayAdapter(this, R.layout.list_item_dialog, pcdList)
            val adapterUkuranban = ArrayAdapter(this, R.layout.list_item_dialog, ukuranbanList)
            val adapterAksesoris = ArrayAdapter(this, R.layout.list_item_dialog, aksesorisList)

            spinnerKategori.setAdapter(adapterKategori)
            spinnerRing.setAdapter(adapterRing)
            spinnerLebar.setAdapter(adapterLebar)
            spinnerEt.setAdapter(adapterEt)
            spinnerPcd.setAdapter(adapterPcd)
            spinnerUkuranban.setAdapter(adapterUkuranban)
            spinnerAksesoris.setAdapter(adapterAksesoris)

            searchKategori = ""
            searchRing = ""
            searchLebar = ""
            searchEt = ""
            searchPcd = ""
            searchUkuranban = ""
            searchAksesoris = ""
            filterPhotos = ArrayList()
            filterPhotosName = ArrayList()

            spinnerKategori.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                searchKategori = parent.getItemAtPosition(position).toString()
            }
            spinnerRing.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                searchRing = parent.getItemAtPosition(position).toString()
            }
            spinnerLebar.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                searchLebar = parent.getItemAtPosition(position).toString()
            }
            spinnerEt.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                searchEt = parent.getItemAtPosition(position).toString()
            }
            spinnerPcd.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                searchPcd = parent.getItemAtPosition(position).toString()
            }
            spinnerUkuranban.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                searchUkuranban = parent.getItemAtPosition(position).toString()
            }
            spinnerAksesoris.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                searchAksesoris = parent.getItemAtPosition(position).toString()
            }

            builder.setPositiveButton("yes") { dialog, _ ->
                val searchPhotosViewModel = ViewModelProvider(this).get(SearchPhotosViewModel::class.java)
                val filterPhotosViewModel = ViewModelProvider(this).get(FilterPhotosViewModel::class.java)
                chipGroup.removeAllViews()
                recyclerView.smoothScrollToPosition(0)
                layoutManager.scrollToPositionWithOffset(0, 0)
                val categories = categoriesMap.filterValues { c ->
                    c == searchKategori
                }.keys as MutableSet<Int>

                val ring = ringMap.filterValues { r ->
                    r == searchRing
                }.keys as MutableSet<Int>

                val lebar = lebarMap.filterValues { l ->
                    l == searchLebar
                }.keys as MutableSet<Int>

                val et = etMap.filterValues { e ->
                    e == searchEt
                }.keys as MutableSet<Int>

                val pcd = pcdMap.filterValues { p ->
                    p == searchPcd
                }.keys as MutableSet<Int>

                val ukuranban = ukuranbanMap.filterValues { u ->
                    u == searchUkuranban
                }.keys as MutableSet<Int>

                val aksesoris = aksesorisMap.filterValues { a ->
                    a == searchAksesoris
                }.keys as MutableSet<Int>

                if (categories.size > 0) {
                    filterPhotos.add(categories.elementAt(0).toString())
                    filterPhotosName.add(searchKategori)
                }

                if (ring.size > 0) {
                    filterPhotos.add(ring.elementAt(0).toString())
                    filterPhotosName.add(searchRing)
                }

                if (lebar.size > 0) {
                    filterPhotos.add(lebar.elementAt(0).toString())
                    filterPhotosName.add(searchLebar)
                }

                if (et.size > 0) {
                    filterPhotos.add(et.elementAt(0).toString())
                    filterPhotosName.add(searchEt)
                }

                if (pcd.size > 0) {
                    filterPhotos.add(pcd.elementAt(0).toString())
                    filterPhotosName.add(searchPcd)
                }

                if (ukuranban.size > 0) {
                    filterPhotos.add(ukuranban.elementAt(0).toString())
                    filterPhotosName.add(searchUkuranban)
                }

                if (aksesoris.size > 0) {
                    filterPhotos.add(aksesoris.elementAt(0).toString())
                    filterPhotosName.add(searchAksesoris)
                }

                val result = filterPhotos.filter { filter -> filter.isNotBlank() }.joinToString(", ")
                val resultName = filterPhotosName.joinToString(", ")

                if (search.text.toString().isBlank() && result.isNotBlank()) {
                    layoutChip.visibility = View.VISIBLE
                    val texts = resultName.split(", ")
                    for (text in texts) {
                        val chip = Chip(this)
                        chip.text = text
                        chipGroup.addView(chip)
                    }

                    lifecycleScope.launch {
                        filterPhotosViewModel.getFilterPhotos(result).collectLatest { pagedData ->
                            photosAdapter.submitData(pagedData)
                            photosAdapter.setAllSelectedtoFalse()
                            buttonShare.visibility = View.GONE
                            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                            title.text = getString(R.string.otomaxpos_photos)
                        }
                    }

                    lifecycleScope.launch {
                        photosAdapter.loadStateFlow.distinctUntilChangedBy { data ->
                            data.refresh
                        }.collect {
                            Log.v("testrun", photosAdapter.snapshot().items.isEmpty().toString())
                            if (photosAdapter.snapshot().items.isEmpty()) {
                                textViewNoData.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                            } else {
                                textViewNoData.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE
                                photosAdapter.setAllSelectedtoFalse()
                                buttonShare.visibility = View.GONE
                                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                                title.text = getString(R.string.otomaxpos_photos)
                            }
                        }
                    }
                } else if (search.text.toString().isNotBlank() && result.isNotBlank()) {
                    layoutChip.visibility = View.VISIBLE
                    val texts = resultName.split(", ")
                    for (text in texts) {
                        val chip = Chip(this)
                        chip.text = text
                        chipGroup.addView(chip)
                    }

                    lifecycleScope.launch {
                        searchPhotosViewModel.getSearchPhotos(search.text.toString(), filterPhotos.filter { filter -> filter.isNotBlank() }.joinToString(", ")).collectLatest { pagedData ->
                            photosAdapter.submitData(pagedData)
                        }
                    }
                    lifecycleScope.launch {
                        photosAdapter.loadStateFlow.distinctUntilChangedBy { data ->
                            data.refresh
                        }.collect {
                            if (photosAdapter.snapshot().items.isEmpty()) {
                                textViewNoData.visibility = View.VISIBLE
                                recyclerView.visibility = View.GONE
                            } else {
                                textViewNoData.visibility = View.GONE
                                recyclerView.visibility = View.VISIBLE
                                photosAdapter.setAllSelectedtoFalse()
                                buttonShare.visibility = View.GONE
                                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                                title.text = getString(R.string.otomaxpos_photos)
                            }
                        }
                    }
                }

                dialog.dismiss()
            }

            builder.setNegativeButton("no") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
            true
        }
        R.id.harga -> {
            startActivity(Intent(this, HargaActivity::class.java))
            true
        }
        R.id.select -> {
            photosAdapter.setAllSelectedtoTrue()
            lifecycleScope.launch {
                photosAdapter.loadStateFlow.distinctUntilChangedBy { data ->
                    data.refresh
                }.collect {
                    title.text = getString(R.string.image_choosed, photosAdapter.snapshot().items.filter { it.image.toString().isNotBlank() }.size)
                }
            }
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            1
        )
    }

    private fun checkPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return permission == PackageManager.PERMISSION_GRANTED
    }

    override fun onPhotosShowAction(isSelected: Boolean) {
        val photosViewModel = ViewModelProvider(this).get(PhotosViewModel::class.java)
        if (isSelected) {
            buttonShare.visibility = View.VISIBLE
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            photosViewModel.changeSelect(true)
        } else {
            buttonShare.visibility = View.GONE
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            photosViewModel.changeSelect(false)
        }
    }

    override fun onPhotosCountResult(result: Int) {
        if (result != 0) {
            title.text = getString(R.string.image_choosed, result)
        } else {
            title.text = getString(R.string.otomaxpos_photos)
        }
    }

    override fun onPhotosSendCart(data: Data) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (cartPhotosViewModel.getPhotosById(data.id).isNotEmpty()) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        Toast.makeText(applicationContext, "Data sudah ada", Toast.LENGTH_SHORT).show()
                    }, 500)
                } else {
                    cartPhotosViewModel.insertPhotos(data)
                    Handler(Looper.getMainLooper()).postDelayed({
                        Toast.makeText(applicationContext, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    }, 500)
                }
            }
        }
    }

    private fun convertUrlToUri(filesPath: List<Data>?) {
        for (i in filesPath!!.indices) {
            Glide.with(this)
                .asBitmap()
                .load(filesPath[i].image)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val values = ContentValues().apply {
                                put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis().toString() + filesPath[i].name + ".jpeg")
                                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "movie")
                                put(MediaStore.Images.Media.MIME_TYPE, "image/*")
                                put(MediaStore.Images.Media.IS_PENDING, 1)
                            }

                            val resolver = contentResolver
                            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                            val fos = uri?.let { resolver.openOutputStream(it) }

                            fos?.use {
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, it)
                            }

                            values.clear()
                            values.put(MediaStore.Images.Media.IS_PENDING, 0)
                            resolver.update(uri!!, values, null, null)

                            uriList.add(uri)
                        } else {
                            val file = File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                filesPath[i].name + ".jpeg"
                            )
                            val fos = FileOutputStream(file)
                            fos.use {
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, it)
                            }
                            uriList.add(
                                FileProvider.getUriForFile(this@MainActivity, applicationContext.packageName + ".provider", File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                    System.currentTimeMillis().toString() + filesPath[i].name + ".jpeg"
                            )))
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        }

        Handler(Looper.getMainLooper()).postDelayed({
            shareImages(uriList)
        }, 3000)
    }

    private fun shareImages(dataList: ArrayList<Uri>) {
        if (dataList.isNotEmpty()) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                type = "*/*"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, dataList)
            }

            try {
                resultLauncher.launch(Intent.createChooser(shareIntent, resources.getString(R.string.share_via)))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            recyclerView.visibility = View.VISIBLE
            buttonShare.isEnabled = true
            buttonShare.text = getString(R.string.share_image)
            if (filterPhotos.isNotEmpty()) {
                layoutChip.visibility = View.VISIBLE
            }

            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            search.visibility = View.VISIBLE
            textViewLoading.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uriList.clear()
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        return@withContext try {
                            val c: Cursor? = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media.DISPLAY_NAME), null, null, null)

                            while (c!!.moveToNext()) {
                                val name: String = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                                val resolver = contentResolver
                                resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "_display_name=?", arrayOf(name))
                            }

                            c.close()
                        } catch (e: Exception) {

                        }
                    }
                }
            } else {
                for (i in uriList) {
                    contentResolver.delete(i, null, null)
                }
                uriList.clear()
            }
        } else {
            recyclerView.visibility = View.VISIBLE
            buttonShare.isEnabled = true
            buttonShare.text = getString(R.string.share_image)
            if (filterPhotos.isNotEmpty()) {
                layoutChip.visibility = View.VISIBLE
            }

            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            textViewLoading.visibility = View.GONE
            search.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                uriList.clear()
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        return@withContext try {
                            val c: Cursor? = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Images.Media.DISPLAY_NAME), null, null, null)

                            while (c!!.moveToNext()) {
                                val name: String = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                                val resolver = contentResolver
                                resolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "_display_name=?", arrayOf(name))
                            }

                            c.close()
                        } catch (e: Exception) {

                        }
                    }
                }
            } else {
                for (i in uriList) {
                    contentResolver.delete(i, null, null)
                }
                uriList.clear()
            }
        }
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}