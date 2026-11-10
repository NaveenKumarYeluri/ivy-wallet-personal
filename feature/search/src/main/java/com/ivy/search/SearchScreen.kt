package com.ivy.search

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.legacy.Theme
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.utils.IvyComponentPreview
import com.ivy.legacy.data.AppBaseData
import com.ivy.legacy.ui.SearchInput
import com.ivy.legacy.ui.component.transaction.transactions
import com.ivy.legacy.utils.densityScope
import com.ivy.legacy.utils.keyboardOnlyWindowInsets
import com.ivy.legacy.utils.keyboardVisibleState
import com.ivy.legacy.utils.selectEndTextFieldValue
import com.ivy.navigation.IvyPreview
import com.ivy.navigation.SearchScreen
import com.ivy.navigation.screenScopedViewModel
import com.ivy.ui.R
import com.ivy.wallet.ui.theme.findContrastTextColor
import com.ivy.wallet.ui.theme.modal.DURATION_MODAL_ANIM
import kotlinx.collections.immutable.persistentListOf

@Composable
fun SearchScreen(screen: SearchScreen) {
    val viewModel: SearchViewModel = screenScopedViewModel()
    val uiState = viewModel.uiState()

    SearchUi(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun SearchUi(
    uiState: SearchState,
    onEvent: (SearchEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Spacer(Modifier.height(24.dp))

        val listState = rememberLazyListState()

        var searchQueryTextFieldValue by remember {
            mutableStateOf(selectEndTextFieldValue(uiState.searchQuery))
        }

        SearchInput(
            searchQueryTextFieldValue = searchQueryTextFieldValue,
            hint = stringResource(R.string.search_transactions),
            showClearIcon = searchQueryTextFieldValue.text.isNotEmpty(),
            onSetSearchQueryTextField = {
                searchQueryTextFieldValue = it
                onEvent(SearchEvent.Search(it.text))
            }
        )

        if (uiState.tags.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.tags, key = { it.id.value }) { tag ->
                    val isSelected = uiState.selectedTagId == tag.id
                    val tagBgColor = if (isSelected) Color(tag.color.value) else UI.colors.medium
                    val tagTextColor = if (isSelected) {
                        findContrastTextColor(tagBgColor)
                    } else {
                        UI.colors.pureInverse
                    }

                    Row(
                        modifier = Modifier
                            .clip(UI.shapes.rFull)
                            .background(tagBgColor, UI.shapes.rFull)
                            .clickable {
                                onEvent(SearchEvent.SelectTag(tag.id))
                            }
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "#${tag.name.value}",
                            style = UI.typo.nC.style(
                                color = tagTextColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }

        LaunchedEffect(uiState.transactions) {
            // scroll to top when transactions are changed
            listState.animateScrollToItem(index = 0, scrollOffset = 0)
        }

        Spacer(Modifier.height(16.dp))
        val emptyStateTitle = stringResource(R.string.no_transactions)
        val selectedTag = remember(uiState.tags, uiState.selectedTagId) {
            uiState.tags.firstOrNull { it.id == uiState.selectedTagId }
        }
        val currentQueryText = searchQueryTextFieldValue.text.ifEmpty {
            selectedTag?.let { "#${it.name.value}" } ?: ""
        }
        val emptyStateText = stringResource(
            R.string.no_transactions_for_query,
            currentQueryText
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState

        ) {
            transactions(
                baseData = AppBaseData(
                    baseCurrency = uiState.baseCurrency,
                    accounts = uiState.accounts,
                    categories = uiState.categories
                ),
                upcoming = null,
                setUpcomingExpanded = { },
                overdue = null,
                setOverdueExpanded = { },
                history = uiState.transactions,
                onPayOrGet = { },
                emptyStateTitle = emptyStateTitle,
                emptyStateText = emptyStateText,
                dateDividerMarginTop = 16.dp,
                shouldShowAccountSpecificColorInTransactions = uiState.shouldShowAccountSpecificColorInTransactions
            )

            item {
                val keyboardVisible by keyboardVisibleState()
                val keyboardShownInsetDp by animateDpAsState(
                    targetValue = densityScope {
                        if (keyboardVisible) keyboardOnlyWindowInsets().bottom.toDp() else 0.dp
                    },
                    animationSpec = tween(DURATION_MODAL_ANIM)
                )

                Spacer(Modifier.height(keyboardShownInsetDp))
                // add keyboard height margin at bototm so the list can scroll to bottom
            }
        }
    }
}

@Preview
@Composable
private fun Preview(isDark: Boolean = false) {
    IvyPreview(isDark) {
        SearchUi(
            uiState = SearchState(
                searchQuery = "",
                transactions = persistentListOf(),
                baseCurrency = "",
                accounts = persistentListOf(),
                categories = persistentListOf(),
                shouldShowAccountSpecificColorInTransactions = false
            ),
            onEvent = {}
        )
    }
}

/** For screenshot testing */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchUiTest(isDark: Boolean) {
    val theme = if (isDark) Theme.DARK else Theme.LIGHT
    IvyComponentPreview(theme = theme) {
        Preview(isDark)
    }
}