package com.ivy.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.ui.R
import com.ivy.wallet.ui.theme.components.CloseButton
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.findContrastTextColor
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalSet
import com.ivy.wallet.ui.theme.modal.ModalTitle
import java.time.YearMonth
import java.util.UUID

@Composable
fun BoxWithConstraintsScope.ReportMonthYearPickerModal(
    visible: Boolean,
    initialYearMonth: YearMonth,
    dismiss: () -> Unit,
    onSelectYearMonth: (YearMonth) -> Unit
) {
    var selectedYear by remember(initialYearMonth, visible) {
        mutableIntStateOf(initialYearMonth.year)
    }
    var selectedMonth by remember(initialYearMonth, visible) {
        mutableIntStateOf(initialYearMonth.monthValue)
    }
    var isYearSelectionMode by remember(visible) {
        mutableStateOf(false)
    }

    IvyModal(
        id = UUID.randomUUID(),
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSet {
                onSelectYearMonth(YearMonth.of(selectedYear, selectedMonth))
                dismiss()
            }
        },
        SecondaryActions = {
            CloseButton {
                dismiss()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(
            text = if (isYearSelectionMode) "Select Year" else "Select Month"
        )

        Spacer(Modifier.height(20.dp))

        // Year Switcher Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(UI.colors.medium)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(UI.colors.pure)
                    .clickable {
                        if (isYearSelectionMode) {
                            selectedYear -= 10
                        } else {
                            selectedYear -= 1
                        }
                    },
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

            // Clickable year pill that toggles Year Grid mode
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isYearSelectionMode) UI.colors.pure else UI.colors.medium)
                    .clickable { isYearSelectionMode = !isYearSelectionMode }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isYearSelectionMode) {
                        val startDecade = (selectedYear / 10) * 10
                        "$startDecade - ${startDecade + 9}"
                    } else {
                        "$selectedYear"
                    },
                    style = UI.typo.b1.style(
                        fontWeight = FontWeight.Bold,
                        color = if (isYearSelectionMode) UI.colors.primary else UI.colors.pureInverse
                    )
                )
                Spacer(Modifier.width(6.dp))
                IvyIcon(
                    modifier = Modifier.size(14.dp),
                    icon = if (isYearSelectionMode) R.drawable.ic_check else R.drawable.ic_calendar,
                    tint = if (isYearSelectionMode) UI.colors.primary else UI.colors.gray,
                    contentDescription = "Toggle year view"
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(UI.colors.pure)
                    .clickable {
                        if (isYearSelectionMode) {
                            selectedYear += 10
                        } else {
                            selectedYear += 1
                        }
                    },
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
        }

        Spacer(Modifier.height(20.dp))

        if (isYearSelectionMode) {
            // 3-column Year Grid around selectedYear
            val startDecade = (selectedYear / 10) * 10
            val years = (startDecade..startDecade + 11).toList()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (row in 0 until 4) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (col in 0 until 3) {
                            val idx = row * 3 + col
                            if (idx < years.size) {
                                val y = years[idx]
                                val isSelected = y == selectedYear
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) UI.colors.primary else UI.colors.medium)
                                        .clickable {
                                            selectedYear = y
                                            isYearSelectionMode = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$y",
                                        style = UI.typo.b2.style(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) findContrastTextColor(UI.colors.primary) else UI.colors.pureInverse
                                        )
                                    )
                                }
                            } else {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        } else {
            // 3-column Month Grid (Jan - Dec)
            val monthNames = listOf(
                1 to "Jan", 2 to "Feb", 3 to "Mar",
                4 to "Apr", 5 to "May", 6 to "Jun",
                7 to "Jul", 8 to "Aug", 9 to "Sep",
                10 to "Oct", 11 to "Nov", 12 to "Dec"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (row in 0 until 4) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (col in 0 until 3) {
                            val (monthNum, monthName) = monthNames[row * 3 + col]
                            val isCurrentSelection = monthNum == selectedMonth

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isCurrentSelection) UI.colors.primary else UI.colors.medium
                                    )
                                    .clickable {
                                        selectedMonth = monthNum
                                        onSelectYearMonth(YearMonth.of(selectedYear, monthNum))
                                        dismiss()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = monthName,
                                    style = UI.typo.b2.style(
                                        fontWeight = if (isCurrentSelection) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCurrentSelection) findContrastTextColor(UI.colors.primary) else UI.colors.pureInverse
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(36.dp))
    }
}

@Composable
fun BoxWithConstraintsScope.ReportYearPickerModal(
    visible: Boolean,
    initialYear: Int,
    dismiss: () -> Unit,
    onSelectYear: (Int) -> Unit
) {
    var selectedYear by remember(initialYear, visible) {
        mutableIntStateOf(initialYear)
    }
    var decadeStart by remember(initialYear, visible) {
        mutableIntStateOf((initialYear / 10) * 10)
    }

    IvyModal(
        id = UUID.randomUUID(),
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSet {
                onSelectYear(selectedYear)
                dismiss()
            }
        },
        SecondaryActions = {
            CloseButton {
                dismiss()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(
            text = "Select Year"
        )

        Spacer(Modifier.height(20.dp))

        // Decade Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(UI.colors.medium)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(UI.colors.pure)
                    .clickable {
                        decadeStart -= 10
                    },
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

            Text(
                text = "$decadeStart - ${decadeStart + 9}",
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.Bold,
                    color = UI.colors.pureInverse
                ),
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(UI.colors.pure)
                    .clickable {
                        decadeStart += 10
                    },
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
        }

        Spacer(Modifier.height(20.dp))

        // 3-column Year Grid for decadeStart .. decadeStart + 11
        val years = (decadeStart..decadeStart + 11).toList()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (row in 0 until 4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (col in 0 until 3) {
                        val idx = row * 3 + col
                        if (idx < years.size) {
                            val y = years[idx]
                            val isSelected = y == selectedYear
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) UI.colors.primary else UI.colors.medium)
                                    .clickable {
                                        selectedYear = y
                                        onSelectYear(y)
                                        dismiss()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$y",
                                    style = UI.typo.b2.style(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) findContrastTextColor(UI.colors.primary) else UI.colors.pureInverse
                                    )
                                )
                            }
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(36.dp))
    }
}
