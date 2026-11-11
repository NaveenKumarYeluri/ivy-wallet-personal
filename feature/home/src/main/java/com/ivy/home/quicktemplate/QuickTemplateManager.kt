package com.ivy.home.quicktemplate

import com.ivy.base.legacy.SharedPrefs
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuickTemplateManager @Inject constructor(
    private val sharedPrefs: SharedPrefs
) {
    companion object {
        private const val PREF_TEMPLATES = "quick_transaction_templates_v1"
    }

    fun getTemplates(): List<QuickTemplate> {
        val jsonString = sharedPrefs.getString(PREF_TEMPLATES, null)
        if (jsonString.isNullOrBlank()) {
            return QuickTemplate.DEFAULT_TEMPLATES
        }
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<QuickTemplate>()
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                list.add(QuickTemplate.fromJson(item))
            }
            if (list.isEmpty()) QuickTemplate.DEFAULT_TEMPLATES else list
        } catch (e: Exception) {
            QuickTemplate.DEFAULT_TEMPLATES
        }
    }

    fun saveTemplates(templates: List<QuickTemplate>) {
        val jsonArray = JSONArray()
        for (template in templates) {
            jsonArray.put(template.toJson())
        }
        sharedPrefs.putString(PREF_TEMPLATES, jsonArray.toString())
    }

    fun addTemplate(template: QuickTemplate) {
        val current = getTemplates().toMutableList()
        current.add(template)
        saveTemplates(current)
    }

    fun updateTemplate(template: QuickTemplate) {
        val current = getTemplates().toMutableList()
        val index = current.indexOfFirst { it.id == template.id }
        if (index != -1) {
            current[index] = template
            saveTemplates(current)
        } else {
            addTemplate(template)
        }
    }

    fun deleteTemplate(id: String) {
        val current = getTemplates().filterNot { it.id == id }
        saveTemplates(current)
    }

    fun resetToDefaults() {
        saveTemplates(QuickTemplate.DEFAULT_TEMPLATES)
    }
}
