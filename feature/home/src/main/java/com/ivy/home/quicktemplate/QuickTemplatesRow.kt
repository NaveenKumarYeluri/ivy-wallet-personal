package com.ivy.home.quicktemplate

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.base.model.TransactionType
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.legacy.ui.theme.modal.IvyModalComponents
import com.ivy.legacy.legacy.ui.theme.modal.ModalNameInput
import com.ivy.legacy.legacy.ui.theme.modal.ModalPositiveButton
import com.ivy.navigation.EditTransactionScreen
import com.ivy.navigation.navigation
import com.ivy.ui.R
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.components.IvyNameTextField
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalTitle
import java.util.UUID

@Composable
fun QuickTemplatesRow(
    templates: List<QuickTemplate>,
    currency: String,
    onTemplateClick: (QuickTemplate) -> Unit,
    onAddTemplateClick: () -> Unit,
    onManageTemplatesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "⚡",
                    fontSize = 14.sp
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Quick Log",
                    style = UI.typo.b2.style(
                        color = UI.colors.pureInverse,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Text(
                modifier = Modifier
                    .clip(UI.shapes.rFull)
                    .clickable { onManageTemplatesClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                text = "Manage",
                style = UI.typo.c.style(
                    color = UI.colors.primary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            templates.forEach { template ->
                QuickTemplateChip(
                    template = template,
                    currency = currency,
                    onClick = { onTemplateClick(template) }
                )
            }

            // Add Template Chip
            Row(
                modifier = Modifier
                    .clip(UI.shapes.rFull)
                    .background(UI.colors.medium.copy(alpha = 0.5f))
                    .border(1.dp, UI.colors.primary.copy(alpha = 0.5f), UI.shapes.rFull)
                    .clickable { onAddTemplateClick() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "+",
                    style = UI.typo.b2.style(
                        color = UI.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "New",
                    style = UI.typo.c.style(
                        color = UI.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun QuickTemplateChip(
    template: QuickTemplate,
    currency: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(UI.shapes.rFull)
            .background(UI.colors.medium)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = template.emoji,
            fontSize = 14.sp
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = template.name,
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.SemiBold
            )
        )
        if (template.amount != null && template.amount > 0) {
            Spacer(Modifier.width(6.dp))
            Text(
                text = "${template.amount.toIntOrDouble()} $currency",
                style = UI.typo.c.style(
                    color = UI.colors.primary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

private fun Double.toIntOrDouble(): String {
    return if (this % 1.0 == 0.0) {
        this.toInt().toString()
    } else {
        String.format("%.2f", this)
    }
}

private val PRESET_EMOJIS = listOf(
    "☕", "🍔", "🛒", "⛽", "🚇", "🎬", "💊", "🏋️", "🎁", "🍕", "🚕", "✈️", "📚", "⚡"
)

@Composable
fun BoxWithConstraintsScope.CreateTemplateModal(
    visible: Boolean,
    dismiss: () -> Unit,
    onSaveTemplate: (QuickTemplate) -> Unit
) {
    var name by remember(visible) { mutableStateOf(TextFieldValue("")) }
    var amountText by remember(visible) { mutableStateOf(TextFieldValue("")) }
    var selectedEmoji by remember(visible) { mutableStateOf("☕") }
    var selectedType by remember(visible) { mutableStateOf(TransactionType.EXPENSE) }
    val focusManager = LocalFocusManager.current

    IvyModal(
        id = UUID.randomUUID(),
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalPositiveButton(
                text = "Save",
                iconStart = R.drawable.ic_check
            ) {
                if (name.text.isNotBlank()) {
                    val amount = amountText.text.toDoubleOrNull()
                    onSaveTemplate(
                        QuickTemplate(
                            name = name.text.trim(),
                            amount = amount,
                            type = selectedType,
                            emoji = selectedEmoji
                        )
                    )
                    dismiss()
                }
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(text = "New Quick Template")

        Spacer(Modifier.height(20.dp))

        // Name input
        ModalNameInput(
            title = "Template Name",
            hint = "e.g., Morning Coffee",
            value = name,
            onValueChange = { name = it }
        )

        Spacer(Modifier.height(16.dp))

        // Amount input (optional)
        ModalNameInput(
            title = "Amount (optional)",
            hint = "0.00",
            value = amountText,
            onValueChange = { amountText = it }
        )

        Spacer(Modifier.height(16.dp))

        // Emoji Picker
        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = "Select Icon",
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PRESET_EMOJIS.forEach { emoji ->
                val isSelected = emoji == selectedEmoji
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(UI.shapes.rFull)
                        .background(if (isSelected) UI.colors.primary else UI.colors.medium)
                        .clickable { selectedEmoji = emoji },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = emoji,
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Type toggle: Expense / Income
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(UI.shapes.rFull)
                    .background(if (selectedType == TransactionType.EXPENSE) UI.colors.primary else UI.colors.medium)
                    .clickable { selectedType = TransactionType.EXPENSE }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Expense",
                    style = UI.typo.b2.style(
                        color = if (selectedType == TransactionType.EXPENSE) White else UI.colors.pureInverse,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(UI.shapes.rFull)
                    .background(if (selectedType == TransactionType.INCOME) UI.colors.primary else UI.colors.medium)
                    .clickable { selectedType = TransactionType.INCOME }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Income",
                    style = UI.typo.b2.style(
                        color = if (selectedType == TransactionType.INCOME) White else UI.colors.pureInverse,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun BoxWithConstraintsScope.ManageTemplatesModal(
    visible: Boolean,
    templates: List<QuickTemplate>,
    currency: String,
    dismiss: () -> Unit,
    onDeleteTemplate: (String) -> Unit,
    onResetDefaults: () -> Unit
) {
    IvyModal(
        id = UUID.randomUUID(),
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalPositiveButton(
                text = "Done",
                iconStart = R.drawable.ic_check
            ) {
                dismiss()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(text = "Manage Templates")

        Spacer(Modifier.height(16.dp))

        templates.forEach { template ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 6.dp)
                    .clip(UI.shapes.r3)
                    .background(UI.colors.medium)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = template.emoji,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = template.name,
                            style = UI.typo.b2.style(
                                color = UI.colors.pureInverse,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (template.amount != null && template.amount > 0) {
                            Text(
                                text = "${template.amount.toIntOrDouble()} $currency",
                                style = UI.typo.c.style(
                                    color = UI.colors.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }

                IvyIcon(
                    modifier = Modifier
                        .clip(UI.shapes.rFull)
                        .clickable { onDeleteTemplate(template.id) }
                        .padding(8.dp),
                    icon = R.drawable.ic_delete,
                    tint = Red
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Reset to Defaults Button
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(UI.shapes.rFull)
                .clickable { onResetDefaults() }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            text = "Reset to Defaults",
            style = UI.typo.c.style(
                color = UI.colors.primary,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(24.dp))
    }
}
