package com.example.movietime.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.movietime.R
import com.example.movietime.data.remote.api.MovieAPI.Companion.API_KEY
import com.example.movietime.data.remote.api.MovieAPI.Companion.BASE_URL
import com.example.movietime.data.remote.dto.MovieDto
import com.example.movietime.data.remote.dto.MovieListDto
import com.example.movietime.domain.usecase.SearchResultAdapter
import com.google.gson.Gson
import java.net.URLEncoder

class SearchResultActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var searchQueryText: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyStateView: LinearLayout
    private lateinit var errorStateView: LinearLayout
    private lateinit var errorMessageView: TextView
    private lateinit var retryButton: Button

    private lateinit var requestQueue: RequestQueue
    private var currentQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        requestQueue = Volley.newRequestQueue(this)

        initViews()
        setupListeners()

        // Отримуємо запит пошуку з Intent
        currentQuery = intent.getStringExtra("SEARCH_QUERY") ?: ""
        if (currentQuery.isNotEmpty()) {
            searchEditText.setText(currentQuery)
            searchQueryText.text = getString(R.string.results_for, currentQuery)
            performSearch(currentQuery)
        }
    }

    private fun initViews() {
        // Знаходимо всі View через findViewById
        searchEditText = findViewById(R.id.search_edit_text)
        searchQueryText = findViewById(R.id.search_query_text)
        recyclerView = findViewById(R.id.search_results)
        progressBar = findViewById(R.id.progress_bar)
        emptyStateView = findViewById(R.id.empty_state)
        errorStateView = findViewById(R.id.error_state)
        errorMessageView = findViewById(R.id.error_message)
        retryButton = findViewById(R.id.retry_button)

        // Ініціалізуємо RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Встановлюємо початковий текст для пошукового запиту
        searchQueryText.text = getString(R.string.results_for, "")
    }

    private fun setupListeners() {
        // Кнопка "Назад"
        findViewById<View>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }

        // Слухач для пошукового поля
        searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val query = v.text.toString().trim()
                if (query.isNotEmpty()) {
                    currentQuery = query
                    searchQueryText.text = getString(R.string.results_for, query)
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }

        // Кнопка повторної спроби
        retryButton.setOnClickListener {
            if (currentQuery.isNotEmpty()) {
                performSearch(currentQuery)
            }
        }
    }

    fun performSearch(query: String) {
        showLoading()

        // Кодуємо запит для URL
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "${BASE_URL}search/movie?api_key=${API_KEY}&language=en-US&query=$encodedQuery&page=1"

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                try {
                    val gson = Gson()
                    val movieListDto = gson.fromJson(response, MovieListDto::class.java)
                    val movies = movieListDto.results

                    if (movies.isEmpty()) {
                        showEmptyState()
                    } else {
                        showResults(movies)
                    }
                } catch (e: Exception) {
                    showErrorState(e.message ?: "Error parsing response")
                }
            },
            { error ->
                showErrorState(error.localizedMessage ?: "Network error")
            }
        )

        requestQueue.add(request)
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyStateView.visibility = View.GONE
        errorStateView.visibility = View.GONE
    }

    private fun showResults(movies: List<MovieDto>) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        emptyStateView.visibility = View.GONE
        errorStateView.visibility = View.GONE

        // Створюємо та встановлюємо адаптер
        val adapter = SearchResultAdapter(movies) { movie ->
            // Обробка кліку на фільм (наприклад, перехід на деталі фільму)
            Toast.makeText(this, "Обрано фільм: ${movie.title}", Toast.LENGTH_SHORT).show()
            // TODO: Додати перехід на екран деталей фільму
            // val intent = Intent(this, MovieDetailActivity::class.java)
            // intent.putExtra("MOVIE_ID", movie.id)
            // startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun showEmptyState() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyStateView.visibility = View.VISIBLE
        errorStateView.visibility = View.GONE
    }

    private fun showErrorState(message: String) {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyStateView.visibility = View.GONE
        errorStateView.visibility = View.VISIBLE

        errorMessageView.text = message
    }
}