package com.rel.languager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class LanguageSelectionAdapter(
    private val context: Context,
    private var languages: List<Locale>,
    private val currentLanguageCode: String,
    private val onLanguageSelected: (String) -> Unit
) : RecyclerView.Adapter<LanguageSelectionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val languageName: TextView = view.findViewById(R.id.language_name)
        val languageCode: TextView = view.findViewById(R.id.language_code)
        val radioButton: RadioButton = view.findViewById(R.id.radio_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_language_selection, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val locale = languages[position]
        val languageTag = locale.toLanguageTag()
        
        // For system default language
        if (position == 0 && languageTag == Locale.getDefault().toLanguageTag()) {
            holder.languageName.text = context.getString(R.string.system_default)
            // Store language code but hide it in UI
            holder.languageCode.text = "(${locale.displayName})"
        } else {
            // For all other languages
            holder.languageName.text = locale.displayName
            // Store language code but hide it in UI
            holder.languageCode.text = languageTag
        }

        // Set radio button state
        val isSelected = if (position == 0 && currentLanguageCode == Constants.DEFAULT_LANGUAGE) {
            true
        } else {
            languageTag == currentLanguageCode
        }
        holder.radioButton.isChecked = isSelected

        // Set click listener for the entire item
        holder.itemView.setOnClickListener {
            val code = if (position == 0) Constants.DEFAULT_LANGUAGE else languageTag
            onLanguageSelected(code)
        }
    }

    override fun getItemCount() = languages.size

    fun updateLanguages(newLanguages: List<Locale>) {
        languages = newLanguages
        notifyDataSetChanged()
    }
}
