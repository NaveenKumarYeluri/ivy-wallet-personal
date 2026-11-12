package com.ivy.reports

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ivy.base.model.TransactionType
import com.ivy.data.repository.SavingsGoal
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.ui.R
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.Orange
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.components.ItemIconM
import com.ivy.wallet.ui.theme.components.IvyIcon
import kotlinx.collections.immutable.ImmutableList
import java.util.Locale

@Composable
fun ReportPeriodSelector(
    periodType: ReportPeriodType,
    periodLabel: String,
    canGoForward: Boolean,
    onSelectPeriodType: (ReportPeriodType) -> Unit,
    onPreviousPeriod: () -> Unit,
    onNextPeriod: () -> Unit,
    onOpenPicker: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        // Segmented pill switcher
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(UI.colors.medium)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ReportPeriodType.entries.forEach { type ->
                val isSelected = type == periodType
                val label = when (type) {
                    ReportPeriodType.MONTHLY -> "Monthly"
                    ReportPeriodType.YEARLY -> "Yearly"
                    ReportPeriodType.ALL_TIME -> "All Time"
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) UI.colors.pure else Color.Transparent)
                        .clickable { onSelectPeriodType(type) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = UI.typo.b2.style(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) UI.colors.pureInverse else UI.colors.gray
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Navigation arrows & current period label
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(UI.colors.medium)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (periodType != ReportPeriodType.ALL_TIME) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(UI.colors.pure)
                        .clickable { onPreviousPeriod() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "‹",
                        style = UI.typo.b1.style(
                            fontWeight = FontWeight.Bold,
                            color = UI.colors.pureInverse
                        )
                    )
                }
            } else {
                Spacer(Modifier.size(36.dp))
            }

            if (periodType != ReportPeriodType.ALL_TIME) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onOpenPicker() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = periodLabel,
                        style = UI.typo.b1.style(
                            fontWeight = FontWeight.Bold,
                            color = UI.colors.pureInverse
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.width(6.dp))
                    IvyIcon(
                        modifier = Modifier.size(16.dp),
                        icon = R.drawable.ic_calendar,
                        tint = UI.colors.primary,
                        contentDescription = "Pick date"
                    )
                }
            } else {
                Text(
                    text = periodLabel,
                    style = UI.typo.b1.style(
                        fontWeight = FontWeight.Bold,
                        color = UI.colors.pureInverse
                    ),
                    textAlign = TextAlign.Center
                )
            }

            if (periodType != ReportPeriodType.ALL_TIME) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(UI.colors.pure)
                        .alpha(if (canGoForward) 1f else 0.35f)
                        .clickable(enabled = canGoForward) { onNextPeriod() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "›",
                        style = UI.typo.b1.style(
                            fontWeight = FontWeight.Bold,
                            color = UI.colors.pureInverse
                        )
                    )
                }
            } else {
                Spacer(Modifier.size(36.dp))
            }
        }
    }
}

@Composable
fun ActiveFilterCard(
    filter: ReportFilter,
    onEditFilter: () -> Unit,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(UI.colors.medium)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(UI.colors.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                IvyIcon(
                    icon = R.drawable.ic_filter_xs,
                    tint = UI.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Custom Filter Active",
                    style = UI.typo.b1.style(
                        fontWeight = FontWeight.Bold,
                        color = UI.colors.pureInverse
                    )
                )
                Spacer(Modifier.height(2.dp))
                val periodDesc = filter.period?.let { p ->
                    val m = p.month
                    val lastN = p.lastNRange
                    val fromTo = p.fromToRange
                    when {
                        m != null -> "${m.name} ${p.year ?: ""}".trim()
                        lastN != null -> "Last ${lastN.forDisplay()}"
                        fromTo != null -> if (fromTo.from == null) "All Time" else "Custom Date Range"
                        else -> "Custom Period"
                    }
                } ?: "Filtered Transactions"
                Text(
                    text = periodDesc,
                    style = UI.typo.c.style(
                        color = UI.colors.gray,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // Edit button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(UI.colors.pure)
                    .clickable { onEditFilter() }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.edit),
                    style = UI.typo.b2.style(
                        fontWeight = FontWeight.Bold,
                        color = UI.colors.primary
                    )
                )
            }

            Spacer(Modifier.width(8.dp))

            // Clear button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(UI.colors.pure)
                    .clickable { onClearFilter() }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.clean_filter),
                    style = UI.typo.b2.style(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                )
            }
        }
    }
}

@Composable
fun IncomeExpenseComparisonGraph(
    intervalItems: ImmutableList<IntervalGraphItem>,
    currency: String,
    modifier: Modifier = Modifier
) {
    if (intervalItems.isEmpty()) return

    val maxVal = intervalItems.maxOfOrNull { maxOf(it.income, it.expense) }?.coerceAtLeast(1.0) ?: 1.0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(UI.colors.medium)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Income & Expense Graph",
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.Bold,
                    color = UI.colors.pureInverse
                )
            )

            // Legend
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(Green))
                    Spacer(Modifier.width(4.dp))
                    Text("Income", style = UI.typo.c.style(color = UI.colors.gray, fontWeight = FontWeight.SemiBold))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(Red))
                    Spacer(Modifier.width(4.dp))
                    Text("Expense", style = UI.typo.c.style(color = UI.colors.gray, fontWeight = FontWeight.SemiBold))
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Bar Chart area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            intervalItems.forEach { item ->
                val incomeFraction = (item.income / maxVal).toFloat().coerceIn(0f, 1f)
                val expenseFraction = (item.expense / maxVal).toFloat().coerceIn(0f, 1f)

                val animIncome by animateFloatAsState(targetValue = incomeFraction, animationSpec = tween(600), label = "animInc")
                val animExpense by animateFloatAsState(targetValue = expenseFraction, animationSpec = tween(600), label = "animExp")

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Income Bar
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .fillMaxHeight(fraction = animIncome.coerceAtLeast(0.04f))
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(Green)
                        )
                        Spacer(Modifier.width(4.dp))
                        // Expense Bar
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .fillMaxHeight(fraction = animExpense.coerceAtLeast(0.04f))
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(Red)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = item.label,
                        style = UI.typo.c.style(
                            fontWeight = FontWeight.Medium,
                            color = UI.colors.gray
                        ),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryPieChartSection(
    slices: ImmutableList<CategorySpendingItem>,
    totalAmount: Double,
    currency: String,
    currentMode: TransactionType,
    onToggleMode: (TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(UI.colors.medium)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pie Chart Distribution",
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.Bold,
                    color = UI.colors.pureInverse
                )
            )

            // Switcher
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(UI.colors.pure)
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (currentMode == TransactionType.EXPENSE) Red.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { onToggleMode(TransactionType.EXPENSE) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Expense",
                        style = UI.typo.c.style(
                            fontWeight = FontWeight.Bold,
                            color = if (currentMode == TransactionType.EXPENSE) Red else UI.colors.gray
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (currentMode == TransactionType.INCOME) Green.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { onToggleMode(TransactionType.INCOME) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Income",
                        style = UI.typo.c.style(
                            fontWeight = FontWeight.Bold,
                            color = if (currentMode == TransactionType.INCOME) Green else UI.colors.gray
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        if (slices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No ${if (currentMode == TransactionType.EXPENSE) "expenses" else "income"} for this period",
                    style = UI.typo.b2.style(color = UI.colors.gray)
                )
            }
        } else {
            // Interactive Donut Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(160.dp)) {
                    val strokeWidth = 28.dp.toPx()
                    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                    val arcOffset = Offset(strokeWidth / 2f, strokeWidth / 2f)

                    var startAngle = -90f
                    val total = if (totalAmount > 0) totalAmount else 1.0

                    slices.forEach { slice ->
                        val sweep = ((slice.totalAmount / total) * 360f).toFloat()
                        if (sweep > 0.5f) {
                            drawArc(
                                color = Color(slice.categoryColor),
                                startAngle = startAngle,
                                sweepAngle = sweep - 1.5f,
                                useCenter = false,
                                topLeft = arcOffset,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        startAngle += sweep
                    }
                }

                // Center Text
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (currentMode == TransactionType.EXPENSE) "Total Spent" else "Total Earned",
                        style = UI.typo.c.style(color = UI.colors.gray, fontWeight = FontWeight.Medium)
                    )
                    Text(
                        text = "$currency${String.format(Locale.US, "%.0f", totalAmount)}",
                        style = UI.typo.b1.style(
                            fontWeight = FontWeight.Black,
                            color = UI.colors.pureInverse
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Slice Legend Items
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                slices.take(5).forEach { slice ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(slice.categoryColor))
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = slice.categoryName,
                                style = UI.typo.b2.style(
                                    fontWeight = FontWeight.SemiBold,
                                    color = UI.colors.pureInverse
                                )
                            )
                        }

                        Text(
                            text = "$currency${String.format(Locale.US, "%.2f", slice.totalAmount)} (${String.format(Locale.US, "%.1f", slice.percentage)}%)",
                            style = UI.typo.c.style(
                                fontWeight = FontWeight.Bold,
                                color = UI.colors.pureInverse
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryWiseBreakdownSection(
    categories: ImmutableList<CategorySpendingItem>,
    currency: String,
    currentMode: TransactionType,
    onToggleMode: (TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Category Breakdown",
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.Bold,
                    color = UI.colors.pureInverse
                )
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(UI.colors.medium)
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (currentMode == TransactionType.EXPENSE) UI.colors.pure else Color.Transparent)
                        .clickable { onToggleMode(TransactionType.EXPENSE) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Expenses",
                        style = UI.typo.c.style(
                            fontWeight = FontWeight.Bold,
                            color = if (currentMode == TransactionType.EXPENSE) Red else UI.colors.gray
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (currentMode == TransactionType.INCOME) UI.colors.pure else Color.Transparent)
                        .clickable { onToggleMode(TransactionType.INCOME) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Income",
                        style = UI.typo.c.style(
                            fontWeight = FontWeight.Bold,
                            color = if (currentMode == TransactionType.INCOME) Green else UI.colors.gray
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(UI.colors.medium)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No category data available for this period",
                    style = UI.typo.c.style(color = UI.colors.gray)
                )
            }
        } else {
            categories.forEach { item ->
                CategorySpendingRow(
                    item = item,
                    currency = currency
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FuturePredictionCard(
    dailyBurnRate: Double,
    projectedPeriodExpense: Double,
    projectedPeriodSavings: Double,
    financialRunwayMonths: Double,
    predictionStatus: String,
    currency: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(UI.colors.medium)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Future Prediction & Runway",
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.Bold,
                    color = UI.colors.pureInverse
                )
            )

            val statusColor = if (predictionStatus.contains("Caution")) Red else Green
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = predictionStatus,
                    style = UI.typo.c.style(
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 2x2 grid of projections
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Metric 1: Daily Burn Rate
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(UI.colors.pure)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Daily Burn Rate",
                    style = UI.typo.c.style(color = UI.colors.gray, fontWeight = FontWeight.Medium)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$currency${String.format(Locale.US, "%.2f", dailyBurnRate)}/day",
                    style = UI.typo.b2.style(fontWeight = FontWeight.Black, color = UI.colors.pureInverse)
                )
            }

            // Metric 2: Projected Total Spend
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(UI.colors.pure)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Projected Spend",
                    style = UI.typo.c.style(color = UI.colors.gray, fontWeight = FontWeight.Medium)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$currency${String.format(Locale.US, "%.2f", projectedPeriodExpense)}",
                    style = UI.typo.b2.style(fontWeight = FontWeight.Black, color = Red)
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Metric 3: Projected Net
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(UI.colors.pure)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Projected Surplus",
                    style = UI.typo.c.style(color = UI.colors.gray, fontWeight = FontWeight.Medium)
                )
                Spacer(Modifier.height(4.dp))
                val isPositive = projectedPeriodSavings >= 0
                val sign = if (isPositive) "+" else ""
                Text(
                    text = "$sign$currency${String.format(Locale.US, "%.2f", projectedPeriodSavings)}",
                    style = UI.typo.b2.style(fontWeight = FontWeight.Black, color = if (isPositive) Green else Red)
                )
            }

            // Metric 4: Financial Runway
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(UI.colors.pure)
                    .padding(12.dp)
            ) {
                Text(
                    text = "Financial Runway",
                    style = UI.typo.c.style(color = UI.colors.gray, fontWeight = FontWeight.Medium)
                )
                Spacer(Modifier.height(4.dp))
                val runwayStr = if (financialRunwayMonths > 60.0) ">5 years" else "${String.format(Locale.US, "%.1f", financialRunwayMonths)} months"
                Text(
                    text = runwayStr,
                    style = UI.typo.b2.style(fontWeight = FontWeight.Black, color = UI.colors.pureInverse)
                )
            }
        }
    }
}

@Composable
fun BalanceTrendSection(
    points: ImmutableList<BalanceTrendPoint>,
    startingBalance: Double,
    endingBalance: Double,
    netChange: Double,
    peakBalance: Double,
    lowestBalance: Double,
    currency: String,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(UI.colors.medium)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Balance Trend",
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.Bold,
                    color = UI.colors.pureInverse
                )
            )

            val isPos = netChange >= 0
            val sign = if (isPos) "+" else ""
            Text(
                text = "$sign$currency${String.format(Locale.US, "%.2f", netChange)}",
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isPos) Green else Red
                )
            )
        }

        Spacer(Modifier.height(14.dp))

        // High/Low stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Peak: $currency${String.format(Locale.US, "%.2f", peakBalance)}",
                style = UI.typo.c.style(color = UI.colors.gray, fontWeight = FontWeight.Medium)
            )
            Text(
                text = "Low: $currency${String.format(Locale.US, "%.2f", lowestBalance)}",
                style = UI.typo.c.style(color = UI.colors.gray, fontWeight = FontWeight.Medium)
            )
        }

        Spacer(Modifier.height(14.dp))

        // Sparkline Area Chart
        val minVal = points.minOf { it.balance }
        val maxVal = points.maxOf { it.balance }
        val range = (maxVal - minVal).coerceAtLeast(1.0)
        val lineColor = if (netChange >= 0) Green else Red

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            val stepX = size.width / (points.size - 1).coerceAtLeast(1)
            val path = Path()
            val fillPath = Path()

            points.forEachIndexed { index, pt ->
                val x = index * stepX
                val normalizedY = ((pt.balance - minVal) / range).toFloat()
                val y = size.height - (normalizedY * (size.height - 24.dp.toPx())) - 12.dp.toPx()

                if (index == 0) {
                    path.moveTo(x, y)
                    fillPath.moveTo(x, size.height)
                    fillPath.lineTo(x, y)
                } else {
                    path.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }

                if (index == points.size - 1) {
                    fillPath.lineTo(x, size.height)
                    fillPath.close()
                }
            }

            // Draw area gradient fill
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    listOf(lineColor.copy(alpha = 0.25f), Color.Transparent)
                )
            )

            // Draw trend line
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw end dot
            val lastX = (points.size - 1) * stepX
            val lastY = size.height - (((points.last().balance - minVal) / range).toFloat() * (size.height - 24.dp.toPx())) - 12.dp.toPx()
            drawCircle(color = lineColor, radius = 5.dp.toPx(), center = Offset(lastX, lastY))
        }
    }
}

@Composable
fun CashFlowBarChart(
    income: Double,
    expenses: Double,
    currency: String,
    modifier: Modifier = Modifier
) {
    val total = (income + expenses).coerceAtLeast(0.01)
    val incomePercent = ((income / total) * 100).coerceIn(0.0, 100.0).toFloat()
    val expensePercent = ((expenses / total) * 100).coerceIn(0.0, 100.0).toFloat()

    val animatedIncomeRatio by animateFloatAsState(
        targetValue = (incomePercent / 100f).coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "incomeRatio"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(UI.colors.medium)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cash Flow Ratio",
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.Bold,
                    color = UI.colors.pureInverse
                )
            )

            val net = income - expenses
            val netColor = if (net >= 0) Green else Red
            val netSign = if (net >= 0) "+" else ""
            Text(
                text = "$netSign$currency${String.format(Locale.US, "%.2f", net)}",
                style = UI.typo.b2.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = netColor
                )
            )
        }

        Spacer(Modifier.height(14.dp))

        // Proportional Dual Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Red.copy(alpha = 0.85f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedIncomeRatio)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Green)
            )
        }

        Spacer(Modifier.height(12.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Green)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Income: $currency${String.format(Locale.US, "%.2f", income)} (${String.format(Locale.US, "%.1f", incomePercent)}%)",
                    style = UI.typo.c.style(
                        fontWeight = FontWeight.Medium,
                        color = UI.colors.pureInverse
                    )
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Red)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Expenses: $currency${String.format(Locale.US, "%.2f", expenses)} (${String.format(Locale.US, "%.1f", expensePercent)}%)",
                    style = UI.typo.c.style(
                        fontWeight = FontWeight.Medium,
                        color = UI.colors.pureInverse
                    )
                )
            }
        }
    }
}

@Composable
fun SavingsRateCard(
    savingsRate: Float,
    income: Double,
    expenses: Double,
    currency: String,
    modifier: Modifier = Modifier
) {
    val net = income - expenses
    val isPositive = net >= 0
    val rateText = String.format(Locale.US, "%.1f%%", savingsRate.coerceIn(-100f, 100f))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(UI.colors.medium)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Savings Efficiency",
                    style = UI.typo.b1.style(
                        fontWeight = FontWeight.Bold,
                        color = UI.colors.pureInverse
                    )
                )
                Text(
                    text = if (isPositive) "Retained from total income" else "Overspent beyond income",
                    style = UI.typo.c.style(
                        color = UI.colors.gray
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isPositive) Green.copy(alpha = 0.15f) else Red.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = rateText,
                    style = UI.typo.b1.style(
                        fontWeight = FontWeight.Black,
                        color = if (isPositive) Green else Red
                    )
                )
            }
        }
    }
}

@Composable
fun TopSpendingCategoriesSection(
    categories: ImmutableList<CategorySpendingItem>,
    currency: String,
    modifier: Modifier = Modifier
) {
    if (categories.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "Category Spending Breakdown",
            style = UI.typo.b1.style(
                fontWeight = FontWeight.Bold,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.height(12.dp))

        categories.take(6).forEach { item ->
            CategorySpendingRow(
                item = item,
                currency = currency
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CategorySpendingRow(
    item: CategorySpendingItem,
    currency: String
) {
    val categoryColor = Color(item.categoryColor)
    val animatedPercent by animateFloatAsState(
        targetValue = (item.percentage / 100f).coerceIn(0f, 1f),
        animationSpec = tween(500),
        label = "categoryBar"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(UI.colors.medium)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    ItemIconM(
                        iconName = item.categoryIcon,
                        tint = categoryColor
                    )
                }

                Spacer(Modifier.width(10.dp))

                Column {
                    Text(
                        text = item.categoryName,
                        style = UI.typo.b2.style(
                            fontWeight = FontWeight.SemiBold,
                            color = UI.colors.pureInverse
                        )
                    )
                    Text(
                        text = "${item.transactionCount} transaction${if (item.transactionCount != 1) "s" else ""}",
                        style = UI.typo.c.style(
                            color = UI.colors.gray
                        )
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$currency${String.format(Locale.US, "%.2f", item.totalAmount)}",
                    style = UI.typo.b2.style(
                        fontWeight = FontWeight.Bold,
                        color = UI.colors.pureInverse
                    )
                )
                Text(
                    text = String.format(Locale.US, "%.1f%%", item.percentage),
                    style = UI.typo.c.style(
                        fontWeight = FontWeight.SemiBold,
                        color = categoryColor
                    )
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        // Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(UI.colors.pure.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedPercent)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(categoryColor)
            )
        }
    }
}

@Composable
fun DailySpendingHeatmapSection(
    days: ImmutableList<DailySpendingHeatmapDay>,
    selectedDay: DailySpendingHeatmapDay?,
    currency: String,
    onSelectDay: (DailySpendingHeatmapDay) -> Unit,
    modifier: Modifier = Modifier
) {
    if (days.isEmpty()) return

    val zeroExpenseDays = days.count { it.totalExpense == 0.0 }
    val firstDayOffset = ((days.firstOrNull()?.dayOfWeek ?: 1) - 1).coerceAtLeast(0)
    val paddedList: List<DailySpendingHeatmapDay?> = List(firstDayOffset) { null } + days
    val remainder = paddedList.size % 7
    val fullPaddedList = if (remainder != 0) paddedList + List(7 - remainder) { null } else paddedList
    val weeks = fullPaddedList.chunked(7)
    val weekdayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(UI.colors.medium)
            .padding(16.dp)
    ) {
        // Title & Zero-Spend Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Daily Spending Activity",
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.Bold,
                    color = UI.colors.pureInverse
                )
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Green.copy(alpha = 0.18f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🎯 $zeroExpenseDays Zero-Spend Days",
                    style = UI.typo.c.style(
                        fontWeight = FontWeight.Bold,
                        color = Green
                    )
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Calendar heat map • Tap a day for breakdown",
            style = UI.typo.c.style(
                color = UI.colors.gray
            )
        )

        Spacer(Modifier.height(14.dp))

        // Weekday headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekdayLabels.forEach { label ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = UI.typo.c.style(
                            fontWeight = FontWeight.Bold,
                            color = UI.colors.gray
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Calendar Grid
        weeks.forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { cell ->
                    if (cell == null) {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val isSelected = selectedDay?.dayOfMonth == cell.dayOfMonth
                        val cellBg = when {
                            cell.totalExpense == 0.0 && cell.totalIncome > 0.0 -> Green.copy(alpha = 0.35f)
                            cell.totalExpense == 0.0 -> UI.colors.pure.copy(alpha = 0.5f)
                            cell.intensity < 0.35f -> Red.copy(alpha = 0.28f)
                            cell.intensity < 0.70f -> Red.copy(alpha = 0.58f)
                            else -> Red.copy(alpha = 0.88f)
                        }

                        val textColor = when {
                            cell.intensity >= 0.70f -> Color.White
                            cell.totalExpense == 0.0 && cell.totalIncome > 0.0 -> Green
                            cell.totalExpense == 0.0 -> UI.colors.gray
                            else -> UI.colors.pureInverse
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(cellBg)
                                .then(
                                    if (isSelected) {
                                        Modifier.border(2.dp, UI.colors.pureInverse, RoundedCornerShape(8.dp))
                                    } else {
                                        Modifier.border(0.5.dp, UI.colors.pure.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    }
                                )
                                .clickable { onSelectDay(cell) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = cell.dayOfMonth.toString(),
                                    style = UI.typo.c.style(
                                        fontWeight = if (cell.intensity > 0.3f || isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = textColor
                                    )
                                )

                                if (cell.totalIncome > 0.0 && cell.totalExpense == 0.0) {
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(Green)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Day Details Strip
        if (selectedDay != null) {
            Spacer(Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(UI.colors.pure)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedDay.dateString,
                        style = UI.typo.b2.style(
                            fontWeight = FontWeight.Bold,
                            color = UI.colors.pureInverse
                        )
                    )

                    Text(
                        text = "${selectedDay.transactionCount} transaction${if (selectedDay.transactionCount != 1) "s" else ""}",
                        style = UI.typo.c.style(
                            color = UI.colors.gray
                        )
                    )
                }

                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (selectedDay.totalExpense > 0.0) {
                        Text(
                            text = "Expense: -$currency${String.format(Locale.US, "%.2f", selectedDay.totalExpense)}",
                            style = UI.typo.b2.style(
                                fontWeight = FontWeight.Bold,
                                color = Red
                            )
                        )
                    } else {
                        Text(
                            text = "No expenses logged",
                            style = UI.typo.b2.style(
                                fontWeight = FontWeight.SemiBold,
                                color = Green
                            )
                        )
                    }

                    if (selectedDay.totalIncome > 0.0) {
                        Text(
                            text = "Income: +$currency${String.format(Locale.US, "%.2f", selectedDay.totalIncome)}",
                            style = UI.typo.b2.style(
                                fontWeight = FontWeight.Bold,
                                color = Green
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeatmapLegendItem(color = UI.colors.pure.copy(alpha = 0.5f), label = "Zero")
            HeatmapLegendItem(color = Green.copy(alpha = 0.35f), label = "Income")
            HeatmapLegendItem(color = Red.copy(alpha = 0.28f), label = "Low")
            HeatmapLegendItem(color = Red.copy(alpha = 0.58f), label = "Medium")
            HeatmapLegendItem(color = Red.copy(alpha = 0.88f), label = "High")
        }
    }
}

@Composable
private fun HeatmapLegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Text(
            text = label,
            style = UI.typo.c.style(
                color = UI.colors.gray,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun CategoryBudgetsSection(
    budgets: ImmutableList<ReportBudgetItem>,
    totalLimit: Double,
    totalSpent: Double,
    totalRemaining: Double,
    currency: String,
    onManageBudgets: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(UI.colors.medium)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Category Budgets & Limits",
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.Bold,
                    color = UI.colors.pureInverse
                )
            )

            if (budgets.isNotEmpty()) {
                val badgeBg = if (totalRemaining >= 0) Green.copy(alpha = 0.18f) else Red.copy(alpha = 0.18f)
                val badgeColor = if (totalRemaining >= 0) Green else Red
                val badgeText = if (totalRemaining >= 0) {
                    "$currency${String.format(Locale.US, "%.0f", totalRemaining)} left"
                } else {
                    "- $currency${String.format(Locale.US, "%.0f", -totalRemaining)} over"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = UI.typo.c.style(
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Track your monthly spending limits and allowances",
            style = UI.typo.c.style(
                color = UI.colors.gray
            )
        )

        Spacer(Modifier.height(14.dp))

        if (budgets.isEmpty()) {
            // Empty State Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(UI.colors.pure)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No category budgets set yet",
                        style = UI.typo.b2.style(
                            fontWeight = FontWeight.SemiBold,
                            color = UI.colors.pureInverse
                        )
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Set monthly spending limits for Dining, Shopping, Fuel, etc. to prevent overspending.",
                        style = UI.typo.c.style(
                            color = UI.colors.gray,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(UI.colors.primary)
                            .clickable { onManageBudgets() }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "+ Set Category Budgets",
                            style = UI.typo.b2.style(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        } else {
            // Overall Total Progress (if > 1 budget)
            if (budgets.size > 1 && totalLimit > 0) {
                val totalPct = ((totalSpent / totalLimit) * 100).toFloat().coerceIn(0f, 999f)
                val totalBarColor = when {
                    totalPct < 70f -> Green
                    totalPct <= 90f -> Orange
                    else -> Red
                }
                val animatedTotalFraction by animateFloatAsState(
                    targetValue = (totalSpent / totalLimit).toFloat().coerceIn(0f, 1f),
                    animationSpec = tween(600),
                    label = "totalBudgetFraction"
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(UI.colors.pure)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Budget Health",
                            style = UI.typo.b2.style(
                                fontWeight = FontWeight.Bold,
                                color = UI.colors.pureInverse
                            )
                        )
                        Text(
                            text = "$currency${String.format(Locale.US, "%.0f", totalSpent)} / $currency${String.format(Locale.US, "%.0f", totalLimit)} (${String.format(Locale.US, "%.0f%%", totalPct)})",
                            style = UI.typo.c.style(
                                fontWeight = FontWeight.Bold,
                                color = totalBarColor
                            )
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(UI.colors.medium)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = animatedTotalFraction)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(totalBarColor)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
            }

            // List of Category Budgets
            budgets.forEachIndexed { index, budget ->
                val barColor = when {
                    budget.percentage < 70f -> Green
                    budget.percentage <= 90f -> Orange
                    else -> Red
                }
                val animatedFraction by animateFloatAsState(
                    targetValue = (budget.spentAmount / budget.limitAmount.coerceAtLeast(0.01)).toFloat().coerceIn(0f, 1f),
                    animationSpec = tween(600),
                    label = "budgetFraction_$index"
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            val catColor = budget.primaryCategoryColor?.let { Color(it) } ?: barColor
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(catColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                ItemIconM(
                                    iconName = budget.primaryCategoryIcon,
                                    tint = catColor
                                )
                            }

                            Spacer(Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = budget.name,
                                    style = UI.typo.b2.style(
                                        fontWeight = FontWeight.SemiBold,
                                        color = UI.colors.pureInverse
                                    )
                                )

                                val remainingText = if (budget.remainingAmount >= 0) {
                                    "$currency${String.format(Locale.US, "%.0f", budget.remainingAmount)} left"
                                } else {
                                    "$currency${String.format(Locale.US, "%.0f", -budget.remainingAmount)} over budget!"
                                }
                                Text(
                                    text = remainingText,
                                    style = UI.typo.c.style(
                                        fontWeight = FontWeight.Bold,
                                        color = barColor
                                    )
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$currency${String.format(Locale.US, "%.0f", budget.spentAmount)} / $currency${String.format(Locale.US, "%.0f", budget.limitAmount)}",
                                style = UI.typo.b2.style(
                                    fontWeight = FontWeight.Bold,
                                    color = UI.colors.pureInverse
                                )
                            )
                            Text(
                                text = String.format(Locale.US, "%.0f%%", budget.percentage),
                                style = UI.typo.c.style(
                                    fontWeight = FontWeight.SemiBold,
                                    color = barColor
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(UI.colors.pure.copy(alpha = 0.5f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = animatedFraction)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(barColor)
                        )
                    }

                    if (index < budgets.lastIndex) {
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Manage Budgets Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(UI.colors.pure)
                    .clickable { onManageBudgets() }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Manage All Budgets →",
                    style = UI.typo.b2.style(
                        fontWeight = FontWeight.Bold,
                        color = UI.colors.primary
                    )
                )
            }
        }
    }
}

@Composable
fun SavingsGoalsSection(
    goals: ImmutableList<SavingsGoal>,
    currency: String,
    onAddGoal: () -> Unit,
    onEditGoal: (SavingsGoal) -> Unit,
    onDeposit: (SavingsGoal) -> Unit,
    onWithdraw: (SavingsGoal) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(UI.colors.medium)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Savings Goals & Targets",
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.Bold,
                    color = UI.colors.pureInverse
                )
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(UI.colors.primary.copy(alpha = 0.15f))
                    .clickable { onAddGoal() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "+ New Goal",
                    style = UI.typo.c.style(
                        fontWeight = FontWeight.Bold,
                        color = UI.colors.primary
                    )
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Dedicated piggy banks for travel, emergencies & purchases",
            style = UI.typo.c.style(
                color = UI.colors.gray
            )
        )

        Spacer(Modifier.height(14.dp))

        if (goals.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(UI.colors.pure)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No savings goals created yet",
                        style = UI.typo.b2.style(
                            fontWeight = FontWeight.SemiBold,
                            color = UI.colors.pureInverse
                        )
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Build up an Emergency Fund or save towards a big target with progress tracking.",
                        style = UI.typo.c.style(
                            color = UI.colors.gray,
                            textAlign = TextAlign.Center
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(UI.colors.primary)
                            .clickable { onAddGoal() }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "+ Create First Goal",
                            style = UI.typo.b2.style(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        } else {
            val totalSaved = goals.sumOf { it.currentAmount }
            val totalTarget = goals.sumOf { it.targetAmount }.coerceAtLeast(0.01)
            val overallPct = ((totalSaved / totalTarget) * 100).toFloat().coerceIn(0f, 999f)

            // Aggregate Summary Card
            if (goals.size > 1) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(UI.colors.pure)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total Savings Progress",
                            style = UI.typo.b2.style(
                                fontWeight = FontWeight.Bold,
                                color = UI.colors.pureInverse
                            )
                        )
                        Text(
                            text = "$currency${String.format(Locale.US, "%.0f", totalSaved)} / $currency${String.format(Locale.US, "%.0f", totalTarget)} (${String.format(Locale.US, "%.0f%%", overallPct)})",
                            style = UI.typo.c.style(
                                fontWeight = FontWeight.Bold,
                                color = UI.colors.primary
                            )
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(UI.colors.medium)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = (totalSaved / totalTarget).toFloat().coerceIn(0f, 1f))
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(UI.colors.primary)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
            }

            // Goals List
            goals.forEachIndexed { index, goal ->
                val goalColor = Color(goal.color)
                val animatedFraction by animateFloatAsState(
                    targetValue = (goal.currentAmount / goal.targetAmount.coerceAtLeast(0.01)).toFloat().coerceIn(0f, 1f),
                    animationSpec = tween(600),
                    label = "goalFraction_$index"
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(UI.colors.pure)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(goalColor)
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                text = goal.name,
                                style = UI.typo.b2.style(
                                    fontWeight = FontWeight.Bold,
                                    color = UI.colors.pureInverse
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onEditGoal(goal) }
                                .padding(4.dp)
                        ) {
                            Text(
                                text = "Edit",
                                style = UI.typo.c.style(
                                    fontWeight = FontWeight.SemiBold,
                                    color = UI.colors.gray
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$currency${String.format(Locale.US, "%.0f", goal.currentAmount)} / $currency${String.format(Locale.US, "%.0f", goal.targetAmount)}",
                            style = UI.typo.b2.style(
                                fontWeight = FontWeight.Bold,
                                color = UI.colors.pureInverse
                            )
                        )

                        Text(
                            text = String.format(Locale.US, "%.0f%%", goal.percentage),
                            style = UI.typo.b2.style(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (goal.isCompleted) Green else goalColor
                            )
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(UI.colors.medium)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = animatedFraction)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (goal.isCompleted) Green else goalColor)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val subText = if (goal.isCompleted) {
                            "🎉 Target reached!"
                        } else {
                            "$currency${String.format(Locale.US, "%.0f", goal.remainingAmount)} left" +
                                    (goal.targetDate?.let { " • Target: $it" } ?: "")
                        }

                        Text(
                            text = subText,
                            style = UI.typo.c.style(
                                color = if (goal.isCompleted) Green else UI.colors.gray,
                                fontWeight = if (goal.isCompleted) FontWeight.Bold else FontWeight.Medium
                            )
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(UI.colors.medium)
                                    .clickable { onWithdraw(goal) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "- Withdraw",
                                    style = UI.typo.c.style(
                                        fontWeight = FontWeight.Bold,
                                        color = UI.colors.gray
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(goalColor.copy(alpha = 0.2f))
                                    .clickable { onDeposit(goal) }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "+ Deposit",
                                    style = UI.typo.c.style(
                                        fontWeight = FontWeight.Bold,
                                        color = goalColor
                                    )
                                )
                            }
                        }
                    }

                    if (index < goals.lastIndex) {
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}
