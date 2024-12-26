package com.example.movietime.ui.dashboard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.movietime.databinding.ActivityMainBinding
import com.example.movietime.domain.model.SliderItem
import com.example.movietime.domain.usecase.SliderAdapter
import com.example.movietime.R
import com.example.movietime.data.remote.api.MovieAPI.Companion.API_KEY
import com.example.movietime.data.remote.api.MovieAPI.Companion.BASE_URL
import com.example.movietime.data.remote.dto.MovieListDto
import com.example.movietime.domain.usecase.FilmListAdapter
import com.google.gson.Gson
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private lateinit var view: ActivityMainBinding
    private lateinit var viewPager2: ViewPager2
    private val slideHandler: Handler = Handler(Looper.getMainLooper())

    private lateinit var adapterBestMovies: RecyclerView.Adapter<*>
    private lateinit var adapterUpcoming: RecyclerView.Adapter<*>
    private lateinit var adapterCategory: RecyclerView.Adapter<*>

    private lateinit var recyclerViewBestMovies: RecyclerView
    private lateinit var recyclerViewUpcoming: RecyclerView
    private lateinit var recyclerViewCategory: RecyclerView

    private lateinit var mRequestQueue: RequestQueue
    private lateinit var mRequest: StringRequest
    private lateinit var mRequest2: StringRequest
    private lateinit var mRequest3: StringRequest

    private lateinit var loading1: ProgressBar
    private lateinit var loading2: ProgressBar
    private lateinit var loading3: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root)

        ViewCompat.setOnApplyWindowInsetsListener(view.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        banners()
        sendRequestPopular()
        sendRequestUpcoming()
    }

    private fun sendRequestPopular() {
        // Використовуємо вже ініціалізований mRequestQueue
        mRequestQueue = Volley.newRequestQueue(this)
        loading1.visibility = View.VISIBLE

        val url = "${BASE_URL}movie/popular?api_key=${API_KEY}&language=en-US&page=1"

        val mStringRequest = StringRequest(Request.Method.GET, url, { response ->
            val gson = Gson()
            loading1.visibility = View.GONE
            val items = gson.fromJson(response, MovieListDto::class.java)
            adapterBestMovies = FilmListAdapter(items.results)
            recyclerViewBestMovies.adapter = adapterBestMovies
        }, { error ->
            loading1.visibility = View.GONE
            //Log.i("UiLover", "onErrorResponse: ${error.toString()}")
        })

        mRequestQueue.add(mStringRequest)
    }

    private fun sendRequestUpcoming() {
        // Використовуємо вже ініціалізований mRequestQueue
        mRequestQueue = Volley.newRequestQueue(this)
        loading3.visibility = View.VISIBLE

        val url = "${BASE_URL}movie/upcoming?api_key=${API_KEY}&language=en-US&page=1"

        val mStringRequest3 = StringRequest(Request.Method.GET, url, { response ->
            val gson = Gson()
            loading3.visibility = View.GONE
            val items = gson.fromJson(response, MovieListDto::class.java)
            adapterUpcoming = FilmListAdapter(items.results)
            recyclerViewUpcoming.adapter = adapterUpcoming
        }, { error ->
            loading3.visibility = View.GONE
            //Log.i("UiLover", "onErrorResponse: ${error.toString()}")
        })

        mRequestQueue.add(mStringRequest3)
    }



    private fun banners() {
        val sliderItems = mutableListOf(
            SliderItem(R.drawable.wide),
            SliderItem(R.drawable.wide1),
            SliderItem(R.drawable.wide3)
        )

        viewPager2.adapter = SliderAdapter(sliderItems, viewPager2)
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 3
        viewPager2.getChildAt(0)?.let {
            it.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
            addTransformer(ViewPager2.PageTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
            })
        }

        viewPager2.setPageTransformer(compositePageTransformer)
        viewPager2.currentItem = 1

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                slideHandler.removeCallbacks(slideRunnable)
                slideHandler.postDelayed(slideRunnable, 3000)
            }
        })
    }

    private val slideRunnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    override fun onPause() {
        super.onPause()
        slideHandler.removeCallbacks(slideRunnable)
    }

    override fun onResume() {
        super.onResume()
        slideHandler.postDelayed(slideRunnable, 2000)
    }

    private fun initView() {
        viewPager2 = view.viewPager
        recyclerViewBestMovies = view.view1
        recyclerViewBestMovies.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewUpcoming = view.view2
        recyclerViewUpcoming.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCategory = view.view3
        recyclerViewCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        loading1 = view.progressBar1
        loading2 = view.progressBar2
        loading3 = view.progressBar3
    }
}

