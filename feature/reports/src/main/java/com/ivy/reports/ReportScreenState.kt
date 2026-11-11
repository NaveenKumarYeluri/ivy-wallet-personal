package com.ivy.reports

import com.ivy.base.legacy.TransactionHistoryItem
import com.ivy.base.model.TransactionType
import com.ivy.data.model.Category
import com.ivy.data.model.Tag
import com.ivy.legacy.datamodel.Account
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.util.UUID

enum class ReportPeriodType {
    MONTHLY, YEARLY, ALL_TIME
}

@Suppress("DataClassDefaultValues")
data class IntervalGraphItem(
    val label: String,
    val income: Double,
    val expense: Double
)

@Suppress("DataClassDefaultValues")
data class BalanceTrendPoint(
    val label: String,
    val balance: Double
)

@Suppress("DataClassDefaultValues")
data class ReportBudgetItem(
    val id: UUID,
    val name: String,
    val limitAmount: Double,
    val spentAmount: Double,
    val percentage: Float,
    val remainingAmount: Double,
    val isExceeded: Boolean,
    val primaryCategoryName: String?,
    val primaryCategoryColor: Int?,
    val primaryCategoryIcon: String?
)

@Suppress("DataClassDefaultValues")
data class CategorySpendingItem(
    val categoryName: String,
    val categoryColor: Int,
    val categoryIcon: String?,
    val totalAmount: Double,
    val percentage: Float,
    val transactionCount: Int
)

@Suppress("DataClassDefaultValues")
data class DailySpendingHeatmapDay(
    val dayOfMonth: Int,
    val dayOfWeek: Int, // 1 = Monday ... 7 = Sunday
    val dateString: String,
    val totalExpense: Double,
    val totalIncome: Double,
    val transactionCount: Int,
    val intensity: Float // 0f (no spending) to 1f (peak spending)
)

@Suppress("DataClassDefaultValues")
data class ReportScreenState(
    val baseCurrency: String = "",
    val balance: Double = 0.0,
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val upcomingIncome: Double = 0.0,
    val upcomingExpenses: Double = 0.0,
    val overdueIncome: Double = 0.0,
    val overdueExpenses: Double = 0.0,
    val history: ImmutableList<TransactionHistoryItem> = persistentListOf(),
    val upcomingTransactions: ImmutableList<com.ivy.base.legacy.Transaction> = persistentListOf(),
    val overdueTransactions: ImmutableList<com.ivy.base.legacy.Transaction> = persistentListOf(),
    val categories: ImmutableList<Category> = persistentListOf(),
    val accounts: ImmutableList<Account> = persistentListOf(),
    val upcomingExpanded: Boolean = false,
    val overdueExpanded: Boolean = false,
    val filter: ReportFilter? = null,
    val loading: Boolean = false,
    val accountIdFilters: ImmutableList<UUID> = persistentListOf(),
    val transactions: ImmutableList<com.ivy.base.legacy.Transaction> = persistentListOf(),
    val filterOverlayVisible: Boolean = false,
    val showTransfersAsIncExpCheckbox: Boolean = false,
    val treatTransfersAsIncExp: Boolean = false,
    val allTags: ImmutableList<Tag> = persistentListOf(),
    val showAccountColorsInTransactions: Boolean = false,
    val topSpendingCategories: ImmutableList<CategorySpendingItem> = persistentListOf(),
    val topIncomeCategories: ImmutableList<CategorySpendingItem> = persistentListOf(),
    val savingsRate: Float = 0f,
    // Default Reports Properties
    val periodType: ReportPeriodType = ReportPeriodType.MONTHLY,
    val periodLabel: String = "",
    val canGoForward: Boolean = false,
    val intervalGraphItems: ImmutableList<IntervalGraphItem> = persistentListOf(),
    val pieChartMode: TransactionType = TransactionType.EXPENSE,
    val pieChartSlices: ImmutableList<CategorySpendingItem> = persistentListOf(),
    val categoryViewMode: TransactionType = TransactionType.EXPENSE,
    // Future predictions
    val dailyBurnRate: Double = 0.0,
    val projectedPeriodExpense: Double = 0.0,
    val projectedPeriodSavings: Double = 0.0,
    val financialRunwayMonths: Double = 0.0,
    val predictionStatus: String = "On Track",
    // Balance trend
    val balanceTrendPoints: ImmutableList<BalanceTrendPoint> = persistentListOf(),
    val startingBalance: Double = 0.0,
    val endingBalance: Double = 0.0,
    val peakBalance: Double = 0.0,
    val lowestBalance: Double = 0.0,
    val netChange: Double = 0.0,
    // Daily spending heatmap
    val dailyHeatmapDays: ImmutableList<DailySpendingHeatmapDay> = persistentListOf(),
    val selectedHeatmapDay: DailySpendingHeatmapDay? = null,
    // Category budgets
    val categoryBudgets: ImmutableList<ReportBudgetItem> = persistentListOf(),
    val totalBudgetLimit: Double = 0.0,
    val totalBudgetSpent: Double = 0.0,
    val totalBudgetRemaining: Double = 0.0
)
