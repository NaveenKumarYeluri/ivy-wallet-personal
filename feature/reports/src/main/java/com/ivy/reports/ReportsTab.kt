package com.ivy.reports

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ReportsTab() {
    val viewModel: ReportViewModel = viewModel()
    val state = viewModel.uiState()

    ReportScreenContent(
        state = state,
        onEventHandler = viewModel::onEvent,
        isTab = true
    )
}
