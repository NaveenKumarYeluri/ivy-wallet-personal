package com.ivy.home.quicktemplate

import com.ivy.base.model.TransactionType
import org.json.JSONObject
import java.util.UUID

data class QuickTemplate(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val amount: Double? = null,
    val type: TransactionType = TransactionType.EXPENSE,
    val categoryId: UUID? = null,
    val categoryName: String? = null,
    val emoji: String = "⚡"
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("name", name)
            if (amount != null) put("amount", amount)
            put("type", type.name)
            if (categoryId != null) put("categoryId", categoryId.toString())
            if (categoryName != null) put("categoryName", categoryName)
            put("emoji", emoji)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): QuickTemplate {
            return QuickTemplate(
                id = json.optString("id", UUID.randomUUID().toString()),
                name = json.optString("name", ""),
                amount = if (json.has("amount") && !json.isNull("amount")) json.optDouble("amount") else null,
                type = try {
                    TransactionType.valueOf(json.optString("type", TransactionType.EXPENSE.name))
                } catch (e: Exception) {
                    TransactionType.EXPENSE
                },
                categoryId = json.optString("categoryId").takeIf { it.isNotBlank() }?.let {
                    try { UUID.fromString(it) } catch (e: Exception) { null }
                },
                categoryName = json.optString("categoryName").takeIf { it.isNotBlank() },
                emoji = json.optString("emoji", "⚡")
            )
        }

        val DEFAULT_TEMPLATES = listOf(
            QuickTemplate(
                id = "default_coffee",
                name = "Coffee",
                amount = 3.50,
                type = TransactionType.EXPENSE,
                emoji = "☕"
            ),
            QuickTemplate(
                id = "default_lunch",
                name = "Lunch",
                amount = 12.00,
                type = TransactionType.EXPENSE,
                emoji = "🍔"
            ),
            QuickTemplate(
                id = "default_groceries",
                name = "Groceries",
                amount = 50.00,
                type = TransactionType.EXPENSE,
                emoji = "🛒"
            ),
            QuickTemplate(
                id = "default_fuel",
                name = "Fuel",
                amount = 40.00,
                type = TransactionType.EXPENSE,
                emoji = "⛽"
            ),
            QuickTemplate(
                id = "default_transit",
                name = "Transit",
                amount = 2.50,
                type = TransactionType.EXPENSE,
                emoji = "🚇"
            )
        )
    }
}
