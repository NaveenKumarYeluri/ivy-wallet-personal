package com.ivy.planned.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.base.model.TransactionType
import com.ivy.data.model.Category
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.PlannedPaymentRule
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.formatDouble
import kotlinx.collections.immutable.ImmutableList
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun PlannedPaymentsViewSwitcher(
    viewMode: PlannedPaymentsViewMode,
    onViewModeChanged: (PlannedPaymentsViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(UI.colors.medium)
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        val listSelected = viewMode == PlannedPaymentsViewMode.LIST
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (listSelected) UI.colors.primary else Color.Transparent)
                .clickable { onViewModeChanged(PlannedPaymentsViewMode.LIST) }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "List View",
                style = UI.typo.b2.style(
                    fontWeight = if (listSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (listSelected) UI.colors.pureInverse else UI.colors.pureInverse.copy(alpha = 0.6f)
                )
            )
        }

        val calendarSelected = viewMode == PlannedPaymentsViewMode.CALENDAR
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (calendarSelected) UI.colors.primary else Color.Transparent)
                .clickable { onViewModeChanged(PlannedPaymentsViewMode.CALENDAR) }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Calendar View",
                style = UI.typo.b2.style(
                    fontWeight = if (calendarSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (calendarSelected) UI.colors.pureInverse else UI.colors.pureInverse.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
fun MonthNavigationHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(UI.colors.medium)
                .clickable { onPreviousMonth() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.Bold,
                    color = UI.colors.pureInverse
                )
            )
        }

        val monthName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        Text(
            text = "$monthName ${currentMonth.year}",
            style = UI.typo.h3.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse
            )
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(UI.colors.medium)
                .clickable { onNextMonth() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "›",
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.Bold,
                    color = UI.colors.pureInverse
                )
            )
        }
    }
}

@Composable
fun MonthlyPlannedSummaryCard(
    expectedIncome: Double,
    expectedExpenses: Double,
    currency: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(UI.colors.medium)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Expected Income",
                style = UI.typo.c.style(
                    color = UI.colors.gray
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "+$currency${formatDouble(expectedIncome)}",
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.Bold,
                    color = Green
                )
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Expected Bills",
                style = UI.typo.c.style(
                    color = UI.colors.gray
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "-$currency${formatDouble(expectedExpenses)}",
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.Bold,
                    color = Red
                )
            )
        }
    }
}

@Composable
fun CalendarGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    datesWithBills: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(UI.colors.medium)
            .padding(12.dp)
    ) {
        // Day of week headers
        val daysOfWeek = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            for (dow in daysOfWeek) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = dow.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(2),
                    textAlign = TextAlign.Center,
                    style = UI.typo.c.style(
                        fontWeight = FontWeight.Bold,
                        color = UI.colors.gray
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Days Grid
        val firstDayOfMonth = month.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value // 1 (Mon) to 7 (Sun)
        val daysInMonth = month.lengthOfMonth()
        val today = LocalDate.now()

        var currentDay = 1
        val totalCells = ((firstDayOfWeek - 1) + daysInMonth + 6) / 7 * 7

        for (row in 0 until (totalCells / 7)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayNum = cellIndex - (firstDayOfWeek - 1) + 1

                    if (dayNum in 1..daysInMonth) {
                        val date = month.atDay(dayNum)
                        val isSelected = date == selectedDate
                        val isToday = date == today
                        val hasBill = datesWithBills.contains(date)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    when {
                                        isSelected -> UI.colors.primary
                                        isToday -> UI.colors.pure.copy(alpha = 0.6f)
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = dayNum.toString(),
                                    style = UI.typo.b2.style(
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) UI.colors.pureInverse else UI.colors.pureInverse
                                    )
                                )

                                if (hasBill) {
                                    Spacer(Modifier.height(3.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) UI.colors.pureInverse else UI.colors.primary
                                            )
                                    )
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
