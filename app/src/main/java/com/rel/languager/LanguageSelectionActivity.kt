package com.rel.languager

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class LanguageSelectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var packageNameText: TextView
    private lateinit var appNameText: TextView
    private lateinit var searchEditText: EditText
    private var packageName: String = ""
    private var appName: String = ""
    private var currentLanguage: String = ""
    private var allLanguages: List<Locale> = emptyList()

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_CURRENT_LANGUAGE = "extra_current_language"
        const val RESULT_LANGUAGE_CODE = "result_language_code"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_selection)

        // Get data from intent
        packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        appName = intent.getStringExtra(EXTRA_APP_NAME) ?: ""
        currentLanguage = intent.getStringExtra(EXTRA_CURRENT_LANGUAGE) ?: Constants.DEFAULT_LANGUAGE

        if (packageName.isEmpty()) {
            finish()
            return
        }

        // Setup toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.select_language)

        // Initialize views
        recyclerView = findViewById(R.id.language_list)
        packageNameText = findViewById(R.id.package_name)
        appNameText = findViewById(R.id.app_name)
        searchEditText = findViewById(R.id.search_languages)

        // Set app info
        packageNameText.text = packageName
        appNameText.text = appName

        // Get all available languages
        allLanguages = LanguageUtils.getAvailableLanguages()

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = LanguageSelectionAdapter(
            this,
            allLanguages,
            currentLanguage
        ) { selectedLanguage ->
            val resultIntent = Intent()
            resultIntent.putExtra(RESULT_LANGUAGE_CODE, selectedLanguage)
            resultIntent.putExtra(EXTRA_PACKAGE_NAME, packageName)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
        recyclerView.adapter = adapter

        // Setup search functionality
        setupSearchFunctionality()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupSearchFunctionality() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterLanguages(s?.toString())
            }
        })
    }

    private fun filterLanguages(query: String?) {
        val adapter = recyclerView.adapter as? LanguageSelectionAdapter ?: return

        if (query.isNullOrBlank()) {
            adapter.updateLanguages(allLanguages)
            return
        }

        val searchQuery = query.lowercase()
        val filteredLanguages = allLanguages.filter { locale ->
            locale.displayName.lowercase().contains(searchQuery) ||
            locale.toLanguageTag().lowercase().contains(searchQuery)
        }

        adapter.updateLanguages(filteredLanguages)
    }
}
