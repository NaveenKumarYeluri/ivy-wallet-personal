package com.ivy.planned.list

import com.ivy.data.model.Category
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.PlannedPaymentRule
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate
import java.time.YearMonth
import javax.annotation.concurrent.Immutable

enum class PlannedPaymentsViewMode {
    LIST,
    CALENDAR
}

@Immutable
data class PlannedPaymentsScreenState(
    val currency: String,
    val categories: ImmutableList<Category>,
    val accounts: ImmutableList<Account>,
    val oneTimePlannedPayment: ImmutableList<PlannedPaymentRule>,
    val oneTimeIncome: Double,
    val oneTimeExpenses: Double,
    val recurringPlannedPayment: ImmutableList<PlannedPaymentRule>,
    val recurringIncome: Double,
    val recurringExpenses: Double,
    val isOneTimePaymentsExpanded: Boolean,
    val isRecurringPaymentsExpanded: Boolean,
    val viewMode: PlannedPaymentsViewMode = PlannedPaymentsViewMode.LIST,
    val selectedMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val datesWithBills: Set<LocalDate> = emptySet(),
    val billsForSelectedDate: ImmutableList<PlannedPaymentRule> = persistentListOf(),
    val monthlyExpectedIncome: Double = 0.0,
    val monthlyExpectedExpenses: Double = 0.0
)
