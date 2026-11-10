package com.ivy.exchangerates.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.exchangerates.data.RateUi
import java.util.Locale

private val POPULAR_CURRENCIES = listOf(
    "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "INR", "CNY", "BGN", "BRL", "BTC", "ETH"
)

@Composable
fun CurrencyConverterCard(
    baseCurrency: String,
    rates: List<RateUi>,
    modifier: Modifier = Modifier
) {
    var amountInput by remember { mutableStateOf("100") }
    var fromCurrency by remember { mutableStateOf(baseCurrency) }
    var toCurrency by remember {
        mutableStateOf(if (baseCurrency == "USD") "EUR" else "USD")
    }

    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val exchangeRate = calculateRate(fromCurrency, toCurrency, rates)
    val convertedAmount = amount * exchangeRate

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.r4)
            .background(UI.colors.medium)
            .border(1.dp, UI.colors.primary.copy(alpha = 0.3f), UI.shapes.r4)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "💱 Currency Converter",
                style = UI.typo.b1.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            // Swap button
            Box(
                modifier = Modifier
                    .clip(UI.shapes.rFull)
                    .background(UI.colors.primary.copy(alpha = 0.15f))
                    .clickable {
                        val temp = fromCurrency
                        fromCurrency = toCurrency
                        toCurrency = temp
                    }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⇄ Swap",
                    style = UI.typo.c.style(
                        color = UI.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Amount input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(UI.shapes.r2)
                .background(UI.colors.pure)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Amount:",
                style = UI.typo.b2.style(
                    color = UI.colors.gray,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(Modifier.width(8.dp))
            TextField(
                modifier = Modifier.weight(1f),
                value = amountInput,
                onValueChange = { amountInput = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = UI.colors.pureInverse,
                    unfocusedTextColor = UI.colors.pureInverse
                ),
                textStyle = UI.typo.b1.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        // From currency picker row
        Text(
            text = "From:",
            style = UI.typo.c.style(
                color = UI.colors.gray,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            POPULAR_CURRENCIES.forEach { code ->
                val selected = code == fromCurrency
                Box(
                    modifier = Modifier
                        .clip(UI.shapes.rFull)
                        .background(if (selected) UI.colors.primary else UI.colors.pure)
                        .clickable { fromCurrency = code }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = code,
                        style = UI.typo.c.style(
                            color = if (selected) Color.White else UI.colors.pureInverse,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // To currency picker row
        Text(
            text = "To:",
            style = UI.typo.c.style(
                color = UI.colors.gray,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            POPULAR_CURRENCIES.forEach { code ->
                val selected = code == toCurrency
                Box(
                    modifier = Modifier
                        .clip(UI.shapes.rFull)
                        .background(if (selected) UI.colors.primary else UI.colors.pure)
                        .clickable { toCurrency = code }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = code,
                        style = UI.typo.c.style(
                            color = if (selected) Color.White else UI.colors.pureInverse,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Result Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(UI.shapes.r3)
                .background(UI.colors.pure)
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${formatAmount(convertedAmount)} $toCurrency",
                style = UI.typo.h2.style(
                    color = UI.colors.primary,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "1 $fromCurrency = ${formatRate(exchangeRate)} $toCurrency",
                style = UI.typo.c.style(
                    color = UI.colors.gray,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

private fun calculateRate(from: String, to: String, rates: List<RateUi>): Double {
    if (from.equals(to, ignoreCase = true)) return 1.0

    // Direct match: from -> to
    val direct = rates.find {
        it.from.equals(from, ignoreCase = true) && it.to.equals(to, ignoreCase = true)
    }
    if (direct != null && direct.rate > 0) return direct.rate

    // Inverse match: to -> from
    val inverse = rates.find {
        it.from.equals(to, ignoreCase = true) && it.to.equals(from, ignoreCase = true)
    }
    if (inverse != null && inverse.rate > 0) return 1.0 / inverse.rate

    // Via a common base rate (e.g. from -> Base and to -> Base)
    val fromRates = rates.filter { it.from.equals(from, ignoreCase = true) || it.to.equals(from, ignoreCase = true) }
    for (fr in fromRates) {
        val baseCode = if (fr.from.equals(from, ignoreCase = true)) fr.to else fr.from
        val rateFromToBase = if (fr.from.equals(from, ignoreCase = true)) fr.rate else (1.0 / fr.rate)

        val toRate = rates.find {
            (it.from.equals(baseCode, ignoreCase = true) && it.to.equals(to, ignoreCase = true)) ||
                    (it.to.equals(baseCode, ignoreCase = true) && it.from.equals(to, ignoreCase = true))
        }
        if (toRate != null && toRate.rate > 0) {
            val rateBaseToTo = if (toRate.from.equals(baseCode, ignoreCase = true)) toRate.rate else (1.0 / toRate.rate)
            return rateFromToBase * rateBaseToTo
        }
    }

    return 1.0
}

private fun formatAmount(amount: Double): String {
    return if (amount >= 1000) {
        String.format(Locale.US, "%,.2f", amount)
    } else if (amount >= 1) {
        String.format(Locale.US, "%.2f", amount)
    } else {
        String.format(Locale.US, "%.4f", amount)
    }
}

private fun formatRate(rate: Double): String {
    return if (rate >= 1) {
        String.format(Locale.US, "%.4f", rate)
    } else {
        String.format(Locale.US, "%.6f", rate)
    }
}
