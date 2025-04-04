package com.rel.languager

import java.util.Locale
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.content.Context
import android.widget.ImageView
import android.view.LayoutInflater
import android.content.Intent
import android.app.Activity
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import androidx.recyclerview.widget.RecyclerView

class AppLanguageAdapter(
    private val context: Context,
    private var appList: List<ApplicationInfo>,
    private val languageMappings: MutableMap<String, String>,
    private val onLanguageSelected: (String, String) -> Unit
) : RecyclerView.Adapter<AppLanguageAdapter.ViewHolder>() {
    companion object {
        const val REQUEST_LANGUAGE_SELECTION = 1001
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appIcon: ImageView = view.findViewById(R.id.app_icon)
        val appName: TextView = view.findViewById(R.id.app_name)
        val packageName: TextView = view.findViewById(R.id.package_name)
        val languageText: TextView = view.findViewById(R.id.language_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_app_language, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = appList[position]
        val packageManager = context.packageManager

        holder.appIcon.setImageDrawable(app.loadIcon(packageManager))
        holder.appName.text = app.loadLabel(packageManager)
        holder.packageName.text = app.packageName

        // Get the current language for this app
        val currentLanguageCode = languageMappings[app.packageName] ?: Constants.DEFAULT_LANGUAGE

        // Set the language display text
        if (currentLanguageCode == Constants.DEFAULT_LANGUAGE) {
            holder.languageText.text = context.getString(R.string.system_default)
        } else {
            // Try to find a matching locale to display the full name and code
            val locale = Locale.forLanguageTag(currentLanguageCode)
            holder.languageText.text = "${locale.displayName} (${currentLanguageCode})"
        }

        // Set click listener to open language selection activity
        holder.itemView.setOnClickListener {
            if (context is Activity) {
                val intent = Intent(context, LanguageSelectionActivity::class.java).apply {
                    putExtra(LanguageSelectionActivity.EXTRA_PACKAGE_NAME, app.packageName)
                    putExtra(LanguageSelectionActivity.EXTRA_APP_NAME, app.loadLabel(packageManager).toString())
                    putExtra(LanguageSelectionActivity.EXTRA_CURRENT_LANGUAGE, currentLanguageCode)
                }
                context.startActivityForResult(intent, REQUEST_LANGUAGE_SELECTION)
            }
        }
    }

    override fun getItemCount() = appList.size

    fun updateList(newList: List<ApplicationInfo>) {
        appList = newList
        notifyDataSetChanged()
    }
}
