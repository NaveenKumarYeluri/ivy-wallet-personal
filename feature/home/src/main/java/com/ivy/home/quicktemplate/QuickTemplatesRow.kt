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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.base.model.TransactionType
import com.ivy.data.model.Category
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.legacy.ui.theme.modal.ModalNameInput
import com.ivy.ui.R
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.White
import com.ivy.wallet.ui.theme.components.IvyIcon
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalPositiveButton
import com.ivy.wallet.ui.theme.modal.ModalTitle
import java.util.Locale
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier
                        .clip(UI.shapes.rFull)
                        .clickable { onAddTemplateClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = "+ Add",
                    style = UI.typo.c.style(
                        color = UI.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.width(4.dp))
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
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Prominent "+ Custom" chip at the start
            Row(
                modifier = Modifier
                    .clip(UI.shapes.rFull)
                    .background(UI.colors.primary.copy(alpha = 0.12f))
                    .border(1.dp, UI.colors.primary, UI.shapes.rFull)
                    .clickable { onAddTemplateClick() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "➕",
                    fontSize = 12.sp
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Custom",
                    style = UI.typo.b2.style(
                        color = UI.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            templates.forEach { template ->
                QuickTemplateChip(
                    template = template,
                    currency = currency,
                    onClick = { onTemplateClick(template) }
                )
            }

            // Also keep "+ New" at the end for easy access when scrolled right
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
        String.format(Locale.US, "%.2f", this)
    }
}

private val PRESET_EMOJIS = listOf(
    "☕", "🍔", "🛒", "⛽", "🚇", "🎬", "💊", "🏋️", "🎁", "🍕", "🚕", "✈️", "📚", "⚡",
    "💰", "💼", "💵", "🏠", "🎮", "📱"
)

@Composable
fun BoxWithConstraintsScope.CreateOrEditTemplateModal(
    visible: Boolean,
    editingTemplate: QuickTemplate? = null,
    categories: List<Category> = emptyList(),
    accounts: List<Account> = emptyList(),
    dismiss: () -> Unit,
    onSaveTemplate: (QuickTemplate) -> Unit
) {
    var name by remember(visible, editingTemplate) {
        mutableStateOf(TextFieldValue(editingTemplate?.name ?: ""))
    }
    var amountText by remember(visible, editingTemplate) {
        mutableStateOf(
            TextFieldValue(
                editingTemplate?.amount?.let {
                    if (it % 1.0 == 0.0) it.toInt().toString() else String.format(Locale.US, "%.2f", it)
                } ?: ""
            )
        )
    }
    var selectedEmoji by remember(visible, editingTemplate) {
        mutableStateOf(editingTemplate?.emoji ?: "☕")
    }
    var selectedType by remember(visible, editingTemplate) {
        mutableStateOf(editingTemplate?.type ?: TransactionType.EXPENSE)
    }
    var selectedCategoryId by remember(visible, editingTemplate) {
        mutableStateOf<UUID?>(editingTemplate?.categoryId)
    }
    var selectedCategoryName by remember(visible, editingTemplate) {
        mutableStateOf<String?>(editingTemplate?.categoryName)
    }
    var selectedAccountId by remember(visible, editingTemplate) {
        mutableStateOf<UUID?>(editingTemplate?.accountId)
    }
    var selectedAccountName by remember(visible, editingTemplate) {
        mutableStateOf<String?>(editingTemplate?.accountName)
    }

    val isEditing = editingTemplate != null

    IvyModal(
        id = UUID.randomUUID(),
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalPositiveButton(
                text = if (isEditing) "Update" else "Save",
                iconStart = R.drawable.ic_check
            ) {
                if (name.text.isNotBlank()) {
                    val amount = amountText.text.toDoubleOrNull()
                    onSaveTemplate(
                        QuickTemplate(
                            id = editingTemplate?.id ?: UUID.randomUUID().toString(),
                            name = name.text.trim(),
                            amount = amount,
                            type = selectedType,
                            emoji = selectedEmoji,
                            categoryId = selectedCategoryId,
                            categoryName = selectedCategoryName,
                            accountId = selectedAccountId,
                            accountName = selectedAccountName
                        )
                    )
                    dismiss()
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(32.dp))

            ModalTitle(text = if (isEditing) "Edit Template" else "New Quick Template")

            Spacer(Modifier.height(20.dp))

            // Name input
            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = "Template Name",
                style = UI.typo.b2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.height(8.dp))

            ModalNameInput(
                hint = "e.g., Morning Coffee",
                autoFocusKeyboard = false,
                textFieldValue = name,
                setTextFieldValue = { name = it }
            )

            Spacer(Modifier.height(16.dp))

            // Amount input (optional)
            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = "Amount (optional)",
                style = UI.typo.b2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.height(8.dp))

            ModalNameInput(
                hint = "0.00",
                autoFocusKeyboard = false,
                textFieldValue = amountText,
                setTextFieldValue = { amountText = it }
            )

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

            Spacer(Modifier.height(16.dp))

            // Category selector (optional)
            if (categories.isNotEmpty()) {
                Text(
                    modifier = Modifier.padding(horizontal = 32.dp),
                    text = "Category (optional)",
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
                    val isNoneSelected = selectedCategoryId == null
                    Box(
                        modifier = Modifier
                            .clip(UI.shapes.rFull)
                            .background(if (isNoneSelected) UI.colors.primary else UI.colors.medium)
                            .clickable {
                                selectedCategoryId = null
                                selectedCategoryName = null
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "None",
                            style = UI.typo.c.style(
                                color = if (isNoneSelected) White else UI.colors.pureInverse,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    categories.forEach { cat ->
                        val isSelected = selectedCategoryId == cat.id.value
                        Box(
                            modifier = Modifier
                                .clip(UI.shapes.rFull)
                                .background(if (isSelected) UI.colors.primary else UI.colors.medium)
                            .clickable {
                                selectedCategoryId = cat.id.value
                                selectedCategoryName = cat.name.value
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat.name.value,
                            style = UI.typo.c.style(
                                color = if (isSelected) White else UI.colors.pureInverse,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        // Account selector (optional)
        if (accounts.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(horizontal = 32.dp),
                text = "Account (optional)",
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
                val isDefaultSelected = selectedAccountId == null
                Box(
                    modifier = Modifier
                        .clip(UI.shapes.rFull)
                        .background(if (isDefaultSelected) UI.colors.primary else UI.colors.medium)
                        .clickable {
                            selectedAccountId = null
                            selectedAccountName = null
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Default",
                        style = UI.typo.c.style(
                            color = if (isDefaultSelected) White else UI.colors.pureInverse,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                accounts.forEach { acc ->
                    val isSelected = selectedAccountId == acc.id
                    Box(
                        modifier = Modifier
                            .clip(UI.shapes.rFull)
                            .background(if (isSelected) UI.colors.primary else UI.colors.medium)
                            .clickable {
                                selectedAccountId = acc.id
                                selectedAccountName = acc.name
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = acc.name,
                            style = UI.typo.c.style(
                                color = if (isSelected) White else UI.colors.pureInverse,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        // Emoji Picker
        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
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

        Spacer(Modifier.height(32.dp))
    }
}
}

// Backward compatibility helper
@Composable
fun BoxWithConstraintsScope.CreateTemplateModal(
    visible: Boolean,
    dismiss: () -> Unit,
    onSaveTemplate: (QuickTemplate) -> Unit
) {
    CreateOrEditTemplateModal(
        visible = visible,
        editingTemplate = null,
        categories = emptyList(),
        accounts = emptyList(),
        dismiss = dismiss,
        onSaveTemplate = onSaveTemplate
    )
}

@Composable
fun BoxWithConstraintsScope.ManageTemplatesModal(
    visible: Boolean,
    templates: List<QuickTemplate>,
    currency: String,
    dismiss: () -> Unit,
    onEditTemplate: (QuickTemplate) -> Unit = {},
    onDeleteTemplate: (String) -> Unit,
    onAddNewTemplate: () -> Unit = {},
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(32.dp))

            ModalTitle(text = "Manage Templates")

            Spacer(Modifier.height(16.dp))

            // Add Custom Template Button inside Manage modal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 4.dp)
                    .clip(UI.shapes.r3)
                    .background(UI.colors.primary.copy(alpha = 0.12f))
                    .clickable { onAddNewTemplate() }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IvyIcon(
                    icon = R.drawable.ic_plus,
                    tint = UI.colors.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Add Custom Template",
                    style = UI.typo.b2.style(
                        color = UI.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

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
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = template.emoji,
                            fontSize = 20.sp
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
                            val details = buildList {
                                add(if (template.type == TransactionType.INCOME) "Income" else "Expense")
                                if (template.amount != null && template.amount > 0) {
                                    add("${template.amount.toIntOrDouble()} $currency")
                                }
                                if (!template.categoryName.isNullOrBlank()) {
                                    add(template.categoryName)
                                }
                                if (!template.accountName.isNullOrBlank()) {
                                    add(template.accountName)
                                }
                            }.joinToString(" • ")
                            Text(
                                text = details,
                                style = UI.typo.c.style(
                                    color = UI.colors.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IvyIcon(
                            modifier = Modifier
                                .clip(UI.shapes.rFull)
                                .clickable { onEditTemplate(template) }
                                .padding(8.dp),
                            icon = R.drawable.ic_edit,
                            tint = UI.colors.primary
                        )
                        Spacer(Modifier.width(4.dp))
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
}
