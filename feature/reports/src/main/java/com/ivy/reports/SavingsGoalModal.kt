package com.ivy.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ivy.data.repository.SavingsGoal
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.theme.components.IvyTitleTextField
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalAddSave
import com.ivy.wallet.ui.theme.modal.ModalDelete
import com.ivy.wallet.ui.theme.modal.ModalSave
import java.util.UUID

private val GOAL_PRESET_COLORS = listOf(
    Color(0xFF388E3C), // Green
    Color(0xFF1976D2), // Blue
    Color(0xFF7B1FA2), // Purple
    Color(0xFFF57C00), // Orange
    Color(0xFFC2185B), // Pink
    Color(0xFF00796B)  // Teal
)

@Composable
fun BoxWithConstraintsScope.SavingsGoalModal(
    visible: Boolean,
    goal: SavingsGoal?,
    currency: String,
    dismiss: () -> Unit,
    onSave: (SavingsGoal) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {
    var name by remember(goal, visible) { mutableStateOf(goal?.name ?: "") }
    var targetAmountStr by remember(goal, visible) { mutableStateOf(goal?.targetAmount?.let { if (it > 0) it.toLong().toString() else "" } ?: "") }
    var currentAmountStr by remember(goal, visible) { mutableStateOf(goal?.currentAmount?.let { if (it > 0) it.toLong().toString() else "" } ?: "") }
    var targetDate by remember(goal, visible) { mutableStateOf(goal?.targetDate ?: "") }
    var selectedColor by remember(goal, visible) { mutableIntStateOf(goal?.color ?: GOAL_PRESET_COLORS[0].toArgb()) }
    var errorMessage by remember(goal, visible) { mutableStateOf<String?>(null) }

    val isEdit = goal != null

    IvyModal(
        id = UUID.randomUUID(),
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSave {
                val targetAmount = targetAmountStr.toDoubleOrNull() ?: 0.0
                val currentAmount = currentAmountStr.toDoubleOrNull() ?: 0.0

                if (name.isBlank()) {
                    errorMessage = "Please enter a goal name"
                } else if (targetAmount <= 0.0) {
                    errorMessage = "Target amount must be greater than 0"
                } else {
                    errorMessage = null
                    val newGoal = SavingsGoal(
                        id = goal?.id ?: UUID.randomUUID().toString(),
                        name = name.trim(),
                        targetAmount = targetAmount,
                        currentAmount = currentAmount,
                        targetDate = targetDate.trim().ifEmpty { null },
                        color = selectedColor
                    )
                    onSave(newGoal)
                    dismiss()
                }
            }
        },
        SecondaryActions = {
            if (isEdit && onDelete != null && goal != null) {
                ModalDelete {
                    onDelete(goal.id)
                    dismiss()
                }
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = if (isEdit) "Edit Savings Goal" else "New Savings Goal",
            style = UI.typo.b1.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.height(4.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "Track money set aside for emergencies, vacations, or targets",
            style = UI.typo.c.style(
                color = UI.colors.gray
            )
        )

        Spacer(Modifier.height(20.dp))

        Column(modifier = Modifier.padding(horizontal = 32.dp)) {
            IvyTitleTextField(
                value = TextFieldValue(name),
                hint = "Goal name (e.g. Emergency Fund, New Laptop)"
            ) {
                name = it.text
            }

            Spacer(Modifier.height(16.dp))

            IvyTitleTextField(
                value = TextFieldValue(targetAmountStr),
                hint = "Target amount ($currency)"
            ) {
                targetAmountStr = it.text.filter { ch -> ch.isDigit() || ch == '.' }
            }

            Spacer(Modifier.height(16.dp))

            IvyTitleTextField(
                value = TextFieldValue(currentAmountStr),
                hint = "Current saved amount ($currency) [Optional]"
            ) {
                currentAmountStr = it.text.filter { ch -> ch.isDigit() || ch == '.' }
            }

            Spacer(Modifier.height(16.dp))

            IvyTitleTextField(
                value = TextFieldValue(targetDate),
                hint = "Target date (e.g. Dec 2027) [Optional]"
            ) {
                targetDate = it.text
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Choose Tag Color",
                style = UI.typo.c.style(
                    fontWeight = FontWeight.SemiBold,
                    color = UI.colors.pureInverse
                )
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GOAL_PRESET_COLORS.forEach { color ->
                    val isSelected = selectedColor == color.toArgb()
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (isSelected) {
                                    Modifier.border(3.dp, UI.colors.pureInverse, CircleShape)
                                } else {
                                    Modifier
                                }
                            )
                            .clickable { selectedColor = color.toArgb() }
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = errorMessage!!,
                    style = UI.typo.c.style(
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun BoxWithConstraintsScope.QuickDepositModal(
    visible: Boolean,
    goal: SavingsGoal?,
    isDeposit: Boolean,
    currency: String,
    dismiss: () -> Unit,
    onConfirm: (goalId: String, amount: Double) -> Unit
) {
    if (goal == null) return

    var amountStr by remember(goal, visible) { mutableStateOf("") }
    var errorMessage by remember(goal, visible) { mutableStateOf<String?>(null) }

    val actionName = if (isDeposit) "Deposit to" else "Withdraw from"

    IvyModal(
        id = UUID.randomUUID(),
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSave {
                val amt = amountStr.toDoubleOrNull() ?: 0.0
                if (amt <= 0.0) {
                    errorMessage = "Please enter an amount greater than 0"
                } else if (!isDeposit && amt > goal.currentAmount) {
                    errorMessage = "Cannot withdraw more than current saved amount"
                } else {
                    errorMessage = null
                    onConfirm(goal.id, amt)
                    dismiss()
                }
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "$actionName ${goal.name}",
            style = UI.typo.b1.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.height(4.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "Currently saved: $currency${String.format(java.util.Locale.US, "%.2f", goal.currentAmount)}",
            style = UI.typo.c.style(
                color = UI.colors.gray
            )
        )

        Spacer(Modifier.height(20.dp))

        Column(modifier = Modifier.padding(horizontal = 32.dp)) {
            IvyTitleTextField(
                value = TextFieldValue(amountStr),
                hint = "Amount ($currency)"
            ) {
                amountStr = it.text.filter { ch -> ch.isDigit() || ch == '.' }
            }

            Spacer(Modifier.height(14.dp))

            // Quick Amount Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(500, 1000, 2000, 5000).forEach { quickVal ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(UI.colors.medium)
                            .clickable {
                                val current = amountStr.toDoubleOrNull() ?: 0.0
                                amountStr = (current + quickVal).toInt().toString()
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "+$quickVal",
                            style = UI.typo.c.style(
                                fontWeight = FontWeight.Bold,
                                color = UI.colors.primary
                            )
                        )
                    }
                }
            }

            if (errorMessage != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = errorMessage!!,
                    style = UI.typo.c.style(
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
