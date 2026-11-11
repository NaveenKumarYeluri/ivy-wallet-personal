package com.ivy.data.repository

import android.content.Context
import com.ivy.base.threading.DispatchersProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class SavingsGoal(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val targetDate: String? = null,
    val color: Int = -16744193,
    val note: String? = null
) {
    val percentage: Float
        get() = if (targetAmount > 0) ((currentAmount / targetAmount) * 100).toFloat().coerceIn(0f, 999f) else 0f

    val remainingAmount: Double
        get() = (targetAmount - currentAmount).coerceAtLeast(0.0)

    val isCompleted: Boolean
        get() = currentAmount >= targetAmount
}

@Singleton
class SavingsGoalRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchersProvider: DispatchersProvider
) {
    private val prefs by lazy {
        context.getSharedPreferences("ivy_savings_goals", Context.MODE_PRIVATE)
    }

    private val json by lazy {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    companion object {
        private const val KEY_GOALS = "savings_goals_list"
    }

    suspend fun getAll(): ImmutableList<SavingsGoal> = withContext(dispatchersProvider.io) {
        val raw = prefs.getString(KEY_GOALS, null) ?: return@withContext emptyList<SavingsGoal>().toImmutableList()
        try {
            json.decodeFromString<List<SavingsGoal>>(raw).toImmutableList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList<SavingsGoal>().toImmutableList()
        }
    }

    suspend fun save(goal: SavingsGoal) = withContext(dispatchersProvider.io) {
        val current = getAll().toMutableList()
        val index = current.indexOfFirst { it.id == goal.id }
        if (index >= 0) {
            current[index] = goal
        } else {
            current.add(goal)
        }
        persist(current)
    }

    suspend fun deposit(goalId: String, amount: Double) = withContext(dispatchersProvider.io) {
        val current = getAll().toMutableList()
        val index = current.indexOfFirst { it.id == goalId }
        if (index >= 0) {
            val goal = current[index]
            val updated = goal.copy(currentAmount = (goal.currentAmount + amount).coerceAtLeast(0.0))
            current[index] = updated
            persist(current)
        }
    }

    suspend fun withdraw(goalId: String, amount: Double) = withContext(dispatchersProvider.io) {
        val current = getAll().toMutableList()
        val index = current.indexOfFirst { it.id == goalId }
        if (index >= 0) {
            val goal = current[index]
            val updated = goal.copy(currentAmount = (goal.currentAmount - amount).coerceAtLeast(0.0))
            current[index] = updated
            persist(current)
        }
    }

    suspend fun delete(goalId: String) = withContext(dispatchersProvider.io) {
        val current = getAll().toMutableList()
        current.removeAll { it.id == goalId }
        persist(current)
    }

    private fun persist(list: List<SavingsGoal>) {
        try {
            val raw = json.encodeToString(list)
            prefs.edit().putString(KEY_GOALS, raw).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
