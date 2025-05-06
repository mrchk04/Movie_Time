package com.example.movietime.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
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
import com.example.movietime.data.remote.api.MovieAPI.Companion.API_KEY
import com.example.movietime.data.remote.api.MovieAPI.Companion.BASE_URL
import com.example.movietime.data.remote.dto.GenreDto
import com.example.movietime.data.remote.dto.GenreListDto
import com.example.movietime.data.remote.dto.MovieDto
import com.example.movietime.data.remote.dto.MovieListDto
import com.example.movietime.data.remote.dto.PopularMoviesResponse
import com.example.movietime.domain.usecase.CategoryListAdapter
import com.example.movietime.domain.usecase.FilmListAdapter
import com.example.movietime.ui.search.SearchResultActivity
import com.google.gson.Gson
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private lateinit var view: ActivityMainBinding
    private lateinit var viewPager2: ViewPager2
    private val slideHandler: Handler = Handler(Looper.getMainLooper())

    private lateinit var recyclerViewBestMovies: RecyclerView
    private lateinit var recyclerViewUpcoming: RecyclerView
    private lateinit var recyclerViewCategory: RecyclerView

    private lateinit var mRequestQueue: RequestQueue

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

        mRequestQueue = Volley.newRequestQueue(this)

        initView()
        banners()
        setupSearchButton()
        fetchData<MovieDto, MovieListDto>(
            "${BASE_URL}movie/popular?api_key=${API_KEY}&language=en-US&page=1",
            loading1,
            recyclerViewBestMovies,
            listExtractor = { it.results }  // Витягуємо список фільмів
        ) { movies -> FilmListAdapter(movies) }  // Передаємо адаптер

        fetchData<MovieDto, MovieListDto>(
            "${BASE_URL}movie/upcoming?api_key=${API_KEY}&language=en-US&page=1",
            loading3,
            recyclerViewUpcoming,
            listExtractor = { it.results }  // Витягуємо список фільмів
        ) { movies -> FilmListAdapter(movies) }  // Передаємо адаптер

        fetchData<GenreDto, GenreListDto>(
            "${BASE_URL}genre/movie/list?api_key=${API_KEY}&language=en-US",
            loading2,
            recyclerViewCategory,
            listExtractor = { it.genres }  // Витягуємо список жанрів
        ) { genres -> CategoryListAdapter(genres) }  // Передаємо адаптер
    }

    open fun getRequestQueue(): RequestQueue {
        return mRequestQueue
    }

    private inline fun <reified T, reified R> fetchData(
        url: String,
        loadingIndicator: ProgressBar,
        recyclerView: RecyclerView,
        crossinline listExtractor: (R) -> List<T>,
        crossinline adapterSetter: (List<T>) -> RecyclerView.Adapter<*>
    ) {
        loadingIndicator.visibility = View.VISIBLE

        val request = StringRequest(Request.Method.GET, url, { response ->
            loadingIndicator.visibility = View.GONE

            val items = Gson().fromJson(response, R::class.java)
            val list = listExtractor(items)

            recyclerView.adapter = adapterSetter(list)

        }, { error ->
            loadingIndicator.visibility = View.GONE
            Log.i("MT", "onErrorResponse: ${error}")
        })

        mRequestQueue.add(request)
    }



     fun banners() {
        val url = "${BASE_URL}movie/popular?api_key=${API_KEY}&language=en-US&page=1"

        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            try {
                val gson = Gson()
                val popularMovies = gson.fromJson(response, PopularMoviesResponse::class.java)

                // Отримуємо перші 5 фільмів із списку
                val topMovies = popularMovies.results.take(5)

                // Формуємо список постерів для адаптера
                val sliderItems = topMovies.mapNotNull { movie ->
                    movie.backdrop_path.let { backdropPath ->
                        SliderItem("https://image.tmdb.org/t/p/w780${backdropPath}", movie.id)
                    }
                }

                setupViewPager(sliderItems)

            } catch (e: Exception) {
                Log.e("MT", "JSON Parsing Error: ${e.localizedMessage}")
            }
        }, { error ->
            Log.e("MT", "onErrorResponse: ${error.localizedMessage}")
        })

        requestQueue.add(stringRequest)
    }
    private fun setupViewPager(sliderItems: List<SliderItem>) {
        viewPager2.adapter = SliderAdapter(sliderItems as MutableList<SliderItem>, viewPager2)
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

    public fun initView() {
        viewPager2 = view.viewPager
        recyclerViewBestMovies = view.view1
        recyclerViewBestMovies.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewUpcoming = view.view3
        recyclerViewUpcoming.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewCategory = view.view2
        recyclerViewCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        loading1 = view.progressBar1
        loading2 = view.progressBar2
        loading3 = view.progressBar3
    }

    private fun setupSearchButton() {
        view.editTextText2.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val query = v.text.toString()
                if (query.isNotEmpty()) {
                    val intent = Intent(this, SearchResultActivity::class.java)
                    intent.putExtra("SEARCH_QUERY", query)
                    startActivity(intent)
                }
                true
            } else {
                false
            }
        }
    }
}

