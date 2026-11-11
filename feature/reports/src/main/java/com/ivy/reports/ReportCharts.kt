package com.ivy.reports

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.components.ItemIconMDefaultIcon
import com.ivy.wallet.ui.theme.formatDouble
import kotlinx.collections.immutable.ImmutableList
import java.util.Locale

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
                text = "$netSign$currency${formatDouble(net)}",
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
                    text = "Income: $currency${formatDouble(income)} (${String.format(Locale.US, "%.1f", incomePercent)}%)",
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
                    text = "Expenses: $currency${formatDouble(expenses)} (${String.format(Locale.US, "%.1f", expensePercent)}%)",
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
            style = UI.typo.h3.style(
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
                    ItemIconMDefaultIcon(
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
                    text = "$currency${formatDouble(item.totalAmount)}",
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
