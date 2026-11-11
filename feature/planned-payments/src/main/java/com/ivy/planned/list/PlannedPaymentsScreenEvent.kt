package com.ivy.planned.list

import java.time.LocalDate
import java.time.YearMonth

sealed interface PlannedPaymentsScreenEvent {
    data class OnOneTimePaymentsExpanded(val isExpanded: Boolean) : PlannedPaymentsScreenEvent
    data class OnRecurringPaymentsExpanded(val isExpanded: Boolean) : PlannedPaymentsScreenEvent
    data class OnViewModeChanged(val viewMode: PlannedPaymentsViewMode) : PlannedPaymentsScreenEvent
    data class OnMonthChanged(val month: YearMonth) : PlannedPaymentsScreenEvent
    data class OnDateSelected(val date: LocalDate) : PlannedPaymentsScreenEvent
}
