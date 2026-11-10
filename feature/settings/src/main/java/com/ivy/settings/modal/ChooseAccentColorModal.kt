package com.ivy.settings.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.base.legacy.ThemeAccent
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IconScale
import com.ivy.design.l1_buildingBlocks.IvyIconScaled
import com.ivy.ui.R
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalTitle
import java.util.UUID

@Composable
fun BoxWithConstraintsScope.ChooseAccentColorModal(
    id: UUID = UUID.randomUUID(),
    visible: Boolean,
    currentAccent: ThemeAccent,
    dismiss: () -> Unit,
    onAccentSelected: (ThemeAccent) -> Unit
) {
    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {}
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(text = stringResource(R.string.choose_accent_color))

        Spacer(Modifier.height(24.dp))

        ThemeAccent.entries.forEach { accent ->
            AccentColorRow(
                accent = accent,
                isSelected = currentAccent == accent,
                onClick = {
                    onAccentSelected(accent)
                    dismiss()
                }
            )

            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AccentColorRow(
    accent: ThemeAccent,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = UI.shapes.r3
    val accentColor = Color(accent.colorHex)
    val borderColor = if (isSelected) accentColor else Color.Transparent
    val backgroundColor = if (isSelected) accentColor.copy(alpha = 0.12f) else UI.colors.medium

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor, shape)
            .border(2.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(accentColor, CircleShape)
                .border(2.dp, UI.colors.pure, CircleShape)
        )

        Spacer(Modifier.width(16.dp))

        Text(
            modifier = Modifier.weight(1f),
            text = accent.title,
            style = UI.typo.b2.style(
                color = if (isSelected) accentColor else UI.colors.pureInverse,
                fontWeight = FontWeight.Bold
            )
        )

        if (isSelected) {
            Spacer(Modifier.width(12.dp))

            IvyIconScaled(
                icon = R.drawable.ic_done,
                tint = accentColor,
                iconScale = IconScale.S,
                padding = 0.dp
            )
        }
    }
}
