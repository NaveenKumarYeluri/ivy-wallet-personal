package com.ivy.planned.list

import com.ivy.data.model.IntervalType
import com.ivy.legacy.datamodel.PlannedPaymentRule
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object RecurringBillCalculator {
    fun doesRuleFallOnDate(rule: PlannedPaymentRule, date: LocalDate): Boolean {
        val start = rule.startDate?.atZone(ZoneId.systemDefault())?.toLocalDate() ?: return false
        if (date.isBefore(start)) return false

        if (rule.oneTime) {
            return date == start
        }

        val n = (rule.intervalN ?: 1).coerceAtLeast(1)
        val intervalType = rule.intervalType ?: IntervalType.MONTH

        return when (intervalType) {
            IntervalType.DAY -> {
                val daysDiff = ChronoUnit.DAYS.between(start, date)
                daysDiff >= 0 && daysDiff % n == 0L
            }
            IntervalType.WEEK -> {
                val weeksDiff = ChronoUnit.WEEKS.between(start, date)
                date.dayOfWeek == start.dayOfWeek && weeksDiff >= 0 && weeksDiff % n == 0L
            }
            IntervalType.MONTH -> {
                val monthsDiff = ChronoUnit.MONTHS.between(start.withDayOfMonth(1), date.withDayOfMonth(1))
                if (monthsDiff >= 0 && monthsDiff % n == 0L) {
                    val targetDay = start.dayOfMonth.coerceAtMost(date.lengthOfMonth())
                    date.dayOfMonth == targetDay
                } else false
            }
            IntervalType.YEAR -> {
                val yearsDiff = ChronoUnit.YEARS.between(start.withDayOfYear(1), date.withDayOfYear(1))
                if (yearsDiff >= 0 && yearsDiff % n == 0L) {
                    date.month == start.month && date.dayOfMonth == start.dayOfMonth.coerceAtMost(date.lengthOfMonth())
                } else false
            }
        }
    }

    fun getBillsForDate(rules: List<PlannedPaymentRule>, date: LocalDate): List<PlannedPaymentRule> {
        return rules.filter { doesRuleFallOnDate(it, date) }
    }

    fun getDatesWithBillsInMonth(rules: List<PlannedPaymentRule>, month: YearMonth): Set<LocalDate> {
        val dates = mutableSetOf<LocalDate>()
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            if (rules.any { doesRuleFallOnDate(it, date) }) {
                dates.add(date)
            }
        }
        return dates
    }
}
