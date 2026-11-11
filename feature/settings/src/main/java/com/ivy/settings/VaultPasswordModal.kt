package com.ivy.settings

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.ui.theme.Red
import com.ivy.wallet.ui.theme.components.IvyTitleTextField
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalSave
import java.util.UUID

@Composable
fun BoxWithConstraintsScope.VaultPasswordModal(
    visible: Boolean,
    dismiss: () -> Unit,
    onPasswordConfirmed: (String) -> Unit
) {
    var password by remember(visible) { mutableStateOf("") }
    var confirmPassword by remember(visible) { mutableStateOf("") }
    var errorMessage by remember(visible) { mutableStateOf<String?>(null) }

    IvyModal(
        id = UUID.randomUUID(),
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSave {
                if (password.length < 4) {
                    errorMessage = "Password must be at least 4 characters"
                } else if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                } else {
                    errorMessage = null
                    onPasswordConfirmed(password)
                    dismiss()
                }
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            modifier = Modifier.padding(start = 32.dp),
            text = "Set Vault Password",
            style = UI.typo.b1.style(
                fontWeight = FontWeight.ExtraBold,
                color = UI.colors.pureInverse
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.padding(horizontal = 32.dp),
            text = "Your data will be encrypted with AES-256-GCM. Make sure to remember this password, as it cannot be recovered without it.",
            style = UI.typo.c.style(
                color = UI.colors.gray
            )
        )

        Spacer(Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 32.dp)) {
            IvyTitleTextField(
                value = androidx.compose.ui.text.input.TextFieldValue(password),
                hint = "Enter vault password"
            ) {
                password = it.text
            }

            Spacer(Modifier.height(16.dp))

            IvyTitleTextField(
                value = androidx.compose.ui.text.input.TextFieldValue(confirmPassword),
                hint = "Confirm vault password"
            ) {
                confirmPassword = it.text
            }

            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMessage ?: "",
                    style = UI.typo.c.style(
                        color = Red,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
