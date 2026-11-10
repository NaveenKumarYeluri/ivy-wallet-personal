package com.ivy.settings.modal

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivy.base.legacy.Theme
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IconScale
import com.ivy.design.l1_buildingBlocks.IvyIconScaled
import com.ivy.ui.R
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalTitle
import java.util.UUID

@Composable
fun BoxWithConstraintsScope.ChooseThemeModal(
    id: UUID = UUID.randomUUID(),
    visible: Boolean,
    currentTheme: Theme,
    dismiss: () -> Unit,
    onThemeSelected: (Theme) -> Unit
) {
    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {}
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(text = stringResource(R.string.choose_theme))

        Spacer(Modifier.height(24.dp))

        ThemeOptionRow(
            icon = R.drawable.home_more_menu_auto_mode,
            title = stringResource(R.string.auto_mode),
            description = stringResource(R.string.theme_system_description),
            isSelected = currentTheme == Theme.AUTO,
            onClick = {
                onThemeSelected(Theme.AUTO)
                dismiss()
            }
        )

        Spacer(Modifier.height(12.dp))

        ThemeOptionRow(
            icon = R.drawable.home_more_menu_light_mode,
            title = stringResource(R.string.light_mode),
            description = stringResource(R.string.theme_light_description),
            isSelected = currentTheme == Theme.LIGHT,
            onClick = {
                onThemeSelected(Theme.LIGHT)
                dismiss()
            }
        )

        Spacer(Modifier.height(12.dp))

        ThemeOptionRow(
            icon = R.drawable.home_more_menu_dark_mode,
            title = stringResource(R.string.dark_mode),
            description = stringResource(R.string.theme_dark_description),
            isSelected = currentTheme == Theme.DARK,
            onClick = {
                onThemeSelected(Theme.DARK)
                dismiss()
            }
        )

        Spacer(Modifier.height(12.dp))

        ThemeOptionRow(
            icon = R.drawable.home_more_menu_amoled_dark_mode,
            title = stringResource(R.string.amoled_mode),
            description = stringResource(R.string.theme_amoled_description),
            isSelected = currentTheme == Theme.AMOLED_DARK,
            onClick = {
                onThemeSelected(Theme.AMOLED_DARK)
                dismiss()
            }
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ThemeOptionRow(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = UI.shapes.r3
    val borderColor = if (isSelected) UI.colors.primary else Color.Transparent
    val backgroundColor = if (isSelected) UI.colors.primary.copy(alpha = 0.12f) else UI.colors.medium

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundColor, shape)
            .border(2.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IvyIconScaled(
            icon = icon,
            tint = if (isSelected) UI.colors.primary else UI.colors.pureInverse,
            iconScale = IconScale.M,
            padding = 2.dp
        )

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = UI.typo.b2.style(
                    color = if (isSelected) UI.colors.primary else UI.colors.pureInverse,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = description,
                style = UI.typo.c.style(
                    color = UI.colors.gray,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        if (isSelected) {
            Spacer(Modifier.width(12.dp))

            IvyIconScaled(
                icon = R.drawable.ic_done,
                tint = UI.colors.primary,
                iconScale = IconScale.S,
                padding = 0.dp
            )
        }
    }
}
