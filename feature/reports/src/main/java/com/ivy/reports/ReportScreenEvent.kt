package com.ivy.reports

import android.content.Context
import com.ivy.base.model.TransactionType
import com.ivy.data.model.Transaction

sealed class ReportScreenEvent {
    data class OnFilter(val filter: ReportFilter?) : ReportScreenEvent()
    data class OnExport(val context: Context) : ReportScreenEvent()
    data class OnShareSummary(val context: Context) : ReportScreenEvent()
    data class OnPayOrGet(val transaction: Transaction) : ReportScreenEvent()
    data class SkipTransaction(val transaction: Transaction) : ReportScreenEvent()
    data class SkipTransactions(val transactions: List<Transaction>) : ReportScreenEvent()
    data class OnUpcomingExpanded(val upcomingExpanded: Boolean) : ReportScreenEvent()
    data class OnOverdueExpanded(val overdueExpanded: Boolean) : ReportScreenEvent()
    data class OnFilterOverlayVisible(val filterOverlayVisible: Boolean) : ReportScreenEvent()
    data class OnTagSearch(val data: String) : ReportScreenEvent()
    data class OnTreatTransfersAsIncomeExpense(val transfersAsIncomeExpense: Boolean) :
        ReportScreenEvent()
    data class OnPayOrGetLegacy(val transaction: com.ivy.base.legacy.Transaction) :
        ReportScreenEvent()
    data class SkipTransactionLegacy(val transaction: com.ivy.base.legacy.Transaction) :
        ReportScreenEvent()
    data class SkipTransactionsLegacy(val transactions: List<com.ivy.base.legacy.Transaction>) :
        ReportScreenEvent()

    // Default Reports events
    data class OnSelectPeriodType(val periodType: ReportPeriodType) : ReportScreenEvent()
    data object OnPreviousPeriod : ReportScreenEvent()
    data object OnNextPeriod : ReportScreenEvent()
    data class OnTogglePieChartMode(val type: TransactionType) : ReportScreenEvent()
    data class OnToggleCategoryViewMode(val type: TransactionType) : ReportScreenEvent()
}
