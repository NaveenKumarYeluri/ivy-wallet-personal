package com.ivy.reports

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.base.legacy.Theme
import com.ivy.base.legacy.stringRes
import com.ivy.base.model.TransactionType
import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.utils.thenIf
import com.ivy.legacy.IvyWalletPreview
import com.ivy.legacy.data.AppBaseData
import com.ivy.legacy.data.LegacyDueSection
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.ivyWalletCtx
import com.ivy.legacy.ui.component.IncomeExpensesCards
import com.ivy.legacy.ui.component.transaction.TransactionsDividerLine
import com.ivy.legacy.ui.component.transaction.transactions
import com.ivy.legacy.utils.clickableNoIndication
import com.ivy.legacy.utils.horizontalSwipeListener
import com.ivy.legacy.utils.rememberInteractionSource
import com.ivy.legacy.utils.rememberSwipeListenerState
import com.ivy.navigation.BudgetScreen
import com.ivy.navigation.PieChartStatisticScreen
import com.ivy.navigation.ReportScreen
import com.ivy.navigation.navigation
import com.ivy.ui.R
import com.ivy.ui.rememberScrollPositionListState
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import com.ivy.wallet.ui.theme.Gray
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.GreenDark
import com.ivy.wallet.ui.theme.GreenLight
import com.ivy.wallet.ui.theme.IvyDark
import com.ivy.wallet.ui.theme.Orange
import com.ivy.wallet.ui.theme.Purple1Dark
import com.ivy.wallet.ui.theme.Red3Light
import com.ivy.wallet.ui.theme.components.BackButtonType
import com.ivy.wallet.ui.theme.components.BalanceRow
import com.ivy.wallet.ui.theme.components.CircleButtonFilled
import com.ivy.wallet.ui.theme.components.IvyCheckboxWithText
import com.ivy.wallet.ui.theme.components.IvyOutlinedButton
import com.ivy.wallet.ui.theme.components.IvyToolbar
import com.ivy.wallet.ui.theme.pureBlur
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import java.math.BigDecimal
import java.util.UUID

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ReportScreen(
    screen: ReportScreen
) {
    val viewModel: ReportViewModel = viewModel()
    val state = viewModel.uiState()

    ReportScreenContent(
        state = state,
        onEventHandler = viewModel::onEvent,
        isTab = false
    )
}

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ReportScreenContent(
    state: ReportScreenState = ReportScreenState(),
    onEventHandler: (ReportScreenEvent) -> Unit = {},
    isTab: Boolean = false
) {
    val legacyTransactions = state.transactions
    val nav = navigation()
    val context = LocalContext.current
    val ivyCtx = ivyWalletCtx()

    val listState = rememberScrollPositionListState(key = if (isTab) "reports_tab" else "reports_screen")
    val swipeListenerState = rememberSwipeListenerState()

    if (state.loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1000f)
                .background(pureBlur())
                .clickableNoIndication(rememberInteractionSource()) {
                    // consume clicks
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.generating_report),
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = Orange
                )
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .thenIf(isTab) {
                horizontalSwipeListener(
                    sensitivity = 200,
                    state = swipeListenerState,
                    onSwipeLeft = {
                        // Rightmost tab
                    },
                    onSwipeRight = {
                        ivyCtx.selectMainTab(com.ivy.legacy.data.model.MainTab.ACCOUNTS)
                    }
                )
            },
        state = listState
    ) {
        stickyHeader {
            Toolbar(
                isTab = isTab,
                onExport = {
                    onEventHandler.invoke(ReportScreenEvent.OnExport(context = context))
                },
                onShareSummary = {
                    onEventHandler.invoke(ReportScreenEvent.OnShareSummary(context = context))
                },
                onFilter = {
                    onEventHandler.invoke(
                        ReportScreenEvent.OnFilterOverlayVisible(
                            filterOverlayVisible = true
                        )
                    )
                }
            )
        }

        // 1. Period Switcher (Monthly, Yearly, All Time)
        item {
            ReportPeriodSelector(
                periodType = state.periodType,
                periodLabel = state.periodLabel.ifEmpty { "November 2026" },
                canGoForward = state.canGoForward,
                onSelectPeriodType = { onEventHandler.invoke(ReportScreenEvent.OnSelectPeriodType(it)) },
                onPreviousPeriod = { onEventHandler.invoke(ReportScreenEvent.OnPreviousPeriod) },
                onNextPeriod = { onEventHandler.invoke(ReportScreenEvent.OnNextPeriod) }
            )
            Spacer(Modifier.height(16.dp))
        }

        // 2. Executive Summary
        item {
            Text(
                modifier = Modifier.padding(start = 32.dp),
                text = "Financial Summary",
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(Modifier.height(8.dp))

            BalanceRow(
                modifier = Modifier.padding(start = 32.dp),
                textColor = UI.colors.pureInverse,
                currency = state.baseCurrency,
                balance = state.balance,
                balanceAmountPrefix = when {
                    state.balance > 0 -> "+"
                    else -> null
                }
            )

            Spacer(Modifier.height(16.dp))

            IncomeExpensesCards(
                history = state.history,
                currency = state.baseCurrency,
                income = state.income,
                expenses = state.expenses,
                hasAddButtons = false,
                itemColor = UI.colors.pure,
                incomeHeaderCardClicked = {
                    onEventHandler.invoke(ReportScreenEvent.OnTogglePieChartMode(TransactionType.INCOME))
                },
                expenseHeaderCardClicked = {
                    onEventHandler.invoke(ReportScreenEvent.OnTogglePieChartMode(TransactionType.EXPENSE))
                }
            )

            if (state.showTransfersAsIncExpCheckbox) {
                IvyCheckboxWithText(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.transfers_as_income_expense),
                    checked = state.treatTransfersAsIncExp
                ) {
                    onEventHandler.invoke(
                        ReportScreenEvent.OnTreatTransfersAsIncomeExpense(
                            transfersAsIncomeExpense = it
                        )
                    )
                }
            } else {
                Spacer(Modifier.height(20.dp))
            }
        }

        // 3. Cash Flow Ratio
        item {
            CashFlowBarChart(
                income = state.income,
                expenses = state.expenses,
                currency = state.baseCurrency
            )
            Spacer(Modifier.height(12.dp))
        }

        // 4. Savings Efficiency
        item {
            SavingsRateCard(
                savingsRate = state.savingsRate,
                income = state.income,
                expenses = state.expenses,
                currency = state.baseCurrency
            )
            Spacer(Modifier.height(16.dp))
        }

        // 5. Daily Spending Activity Heatmap (Monthly view)
        if (state.dailyHeatmapDays.isNotEmpty()) {
            item {
                DailySpendingHeatmapSection(
                    days = state.dailyHeatmapDays,
                    selectedDay = state.selectedHeatmapDay,
                    currency = state.baseCurrency,
                    onSelectDay = {
                        onEventHandler.invoke(ReportScreenEvent.OnSelectHeatmapDay(it))
                    }
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        // 6. Income & Expense Comparison Graph
        if (state.intervalGraphItems.isNotEmpty()) {
            item {
                IncomeExpenseComparisonGraph(
                    intervalItems = state.intervalGraphItems,
                    currency = state.baseCurrency
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        // 7. Category Budgets & Spending Limits
        item {
            CategoryBudgetsSection(
                budgets = state.categoryBudgets,
                totalLimit = state.totalBudgetLimit,
                totalSpent = state.totalBudgetSpent,
                totalRemaining = state.totalBudgetRemaining,
                currency = state.baseCurrency,
                onManageBudgets = {
                    nav.navigateTo(BudgetScreen)
                }
            )
            Spacer(Modifier.height(16.dp))
        }

        // 8. Pie Chart Distribution
        item {
            val totalForPie = if (state.pieChartMode == TransactionType.EXPENSE) state.expenses else state.income
            CategoryPieChartSection(
                slices = state.pieChartSlices,
                totalAmount = totalForPie,
                currency = state.baseCurrency,
                currentMode = state.pieChartMode,
                onToggleMode = { onEventHandler.invoke(ReportScreenEvent.OnTogglePieChartMode(it)) }
            )
            Spacer(Modifier.height(16.dp))
        }

        // 7. Future Prediction & Runway
        item {
            FuturePredictionCard(
                dailyBurnRate = state.dailyBurnRate,
                projectedPeriodExpense = state.projectedPeriodExpense,
                projectedPeriodSavings = state.projectedPeriodSavings,
                financialRunwayMonths = state.financialRunwayMonths,
                predictionStatus = state.predictionStatus,
                currency = state.baseCurrency
            )
            Spacer(Modifier.height(16.dp))
        }

        // 8. Balance Trend Sparkline / Area Graph
        if (state.balanceTrendPoints.isNotEmpty()) {
            item {
                BalanceTrendSection(
                    points = state.balanceTrendPoints,
                    startingBalance = state.startingBalance,
                    endingBalance = state.endingBalance,
                    netChange = state.netChange,
                    peakBalance = state.peakBalance,
                    lowestBalance = state.lowestBalance,
                    currency = state.baseCurrency
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        // 9. Category Breakdown List
        item {
            CategoryWiseBreakdownSection(
                categories = state.topSpendingCategories,
                currency = state.baseCurrency,
                currentMode = state.categoryViewMode,
                onToggleMode = { onEventHandler.invoke(ReportScreenEvent.OnToggleCategoryViewMode(it)) }
            )
            Spacer(Modifier.height(16.dp))
        }

        // 10. If custom filter active, show transaction history
        if (state.filter != null) {
            transactions(
                baseData = AppBaseData(
                    baseCurrency = state.baseCurrency,
                    categories = state.categories,
                    accounts = state.accounts
                ),
                upcoming = LegacyDueSection(
                    trns = state.upcomingTransactions,
                    stats = IncomeExpensePair(
                        income = state.upcomingIncome.toBigDecimal(),
                        expense = state.upcomingExpenses.toBigDecimal()
                    ),
                    expanded = state.upcomingExpanded
                ),
                setUpcomingExpanded = {
                    onEventHandler.invoke(ReportScreenEvent.OnUpcomingExpanded(upcomingExpanded = it))
                },
                overdue = LegacyDueSection(
                    trns = state.overdueTransactions,
                    stats = IncomeExpensePair(
                        income = state.overdueIncome.toBigDecimal(),
                        expense = state.overdueExpenses.toBigDecimal()
                    ),
                    expanded = state.overdueExpanded
                ),
                setOverdueExpanded = {
                    onEventHandler.invoke(ReportScreenEvent.OnOverdueExpanded(overdueExpanded = it))
                },
                history = state.history,
                lastItemSpacer = 48.dp,
                onPayOrGet = {
                    onEventHandler.invoke(ReportScreenEvent.OnPayOrGetLegacy(transaction = it))
                },
                emptyStateTitle = stringRes(R.string.no_transactions),
                emptyStateText = stringRes(R.string.no_transactions_for_your_filter),
                shouldShowAccountSpecificColorInTransactions = state.showAccountColorsInTransactions,
                onSkipTransaction = {
                    onEventHandler.invoke(ReportScreenEvent.SkipTransactionLegacy(transaction = it))
                },
                onSkipAllTransactions = {
                    onEventHandler.invoke(ReportScreenEvent.SkipTransactionsLegacy(transactions = it))
                }
            )
        }

        // Bottom padding for navigation bar
        if (isTab) {
            item {
                Spacer(Modifier.height(150.dp))
            }
        }
    }

    FilterOverlay(
        visible = state.filterOverlayVisible,
        baseCurrency = state.baseCurrency,
        accounts = state.accounts,
        categories = state.categories,
        filter = state.filter,
        allTags = state.allTags,
        onClose = {
            onEventHandler.invoke(
                ReportScreenEvent.OnFilterOverlayVisible(
                    filterOverlayVisible = false
                )
            )
        },
        onSetFilter = {
            onEventHandler.invoke(ReportScreenEvent.OnFilter(filter = it))
        },
        onTagSearch = {
            onEventHandler.invoke(ReportScreenEvent.OnTagSearch(data = it))
        }
    )
}

@Composable
private fun Toolbar(
    isTab: Boolean = false,
    onExport: () -> Unit,
    onShareSummary: () -> Unit,
    onFilter: () -> Unit
) {
    val nav = navigation()
    if (isTab) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.reports),
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = UI.colors.pureInverse
                )
            )

            Spacer(Modifier.weight(1f))

            CircleButtonFilled(
                icon = R.drawable.ic_share
            ) {
                onShareSummary()
            }

            Spacer(Modifier.width(10.dp))

            IvyOutlinedButton(
                text = stringResource(R.string.export),
                iconTint = UI.colors.primary,
                borderColor = UI.colors.primary,
                textColor = UI.colors.primary,
                solidBackground = true,
                padding = 8.dp,
                iconStart = R.drawable.ic_export_csv
            ) {
                onExport()
            }

            Spacer(Modifier.width(10.dp))

            CircleButtonFilled(
                icon = R.drawable.ic_filter_xs
            ) {
                onFilter()
            }
        }
    } else {
        IvyToolbar(
            backButtonType = BackButtonType.CLOSE,
            onBack = {
                nav.back()
            }
        ) {
            Spacer(Modifier.weight(1f))

            CircleButtonFilled(
                icon = R.drawable.ic_share
            ) {
                onShareSummary()
            }

            Spacer(Modifier.width(12.dp))

            IvyOutlinedButton(
                text = stringResource(R.string.export),
                iconTint = UI.colors.primary,
                borderColor = UI.colors.primary,
                textColor = UI.colors.primary,
                solidBackground = true,
                padding = 8.dp,
                iconStart = R.drawable.ic_export_csv
            ) {
                onExport()
            }

            Spacer(Modifier.width(12.dp))

            CircleButtonFilled(
                icon = R.drawable.ic_filter_xs
            ) {
                onFilter()
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview(theme: Theme = Theme.LIGHT) {
    IvyWalletPreview(theme) {
        val acc1 = Account("Cash", color = Green.toArgb())
        val acc2 = Account("DSK", color = GreenDark.toArgb())
        val cat1 = Category(
            name = NotBlankTrimmedString.unsafe("Science"),
            color = ColorInt(Purple1Dark.toArgb()),
            icon = IconAsset.unsafe("atom"),
            id = CategoryId(UUID.randomUUID()),
            orderNum = 0.0,
        )
        val state = ReportScreenState(
            baseCurrency = "BGN",
            balance = -6405.66,
            income = 2000.0,
            expenses = 8405.66,
            upcomingIncome = 4800.23,
            upcomingExpenses = 0.0,
            overdueIncome = 2335.12,
            overdueExpenses = 0.0,
            history = persistentListOf(),
            upcomingTransactions = persistentListOf(),
            overdueTransactions = persistentListOf(),
            upcomingExpanded = true,
            overdueExpanded = true,
            filter = ReportFilter.emptyFilter("BGN"),
            loading = false,
            accounts = persistentListOf(
                acc1,
                acc2,
                Account("phyre", color = GreenLight.toArgb(), icon = "cash"),
                Account("Revolut", color = IvyDark.toArgb()),
            ),
            categories = persistentListOf(
                cat1,
                Category(
                    name = NotBlankTrimmedString.unsafe("Pet"),
                    color = ColorInt(Red3Light.toArgb()),
                    icon = IconAsset.unsafe("pet"),
                    id = CategoryId(UUID.randomUUID()),
                    orderNum = 0.0,
                ),
                Category(
                    name = NotBlankTrimmedString.unsafe("Home"),
                    color = ColorInt(Green.toArgb()),
                    icon = null,
                    id = CategoryId(UUID.randomUUID()),
                    orderNum = 0.0,
                ),
            ),
        )

        ReportScreenContent(state = state)
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun Preview_NO_FILTER(theme: Theme = Theme.LIGHT) {
    IvyWalletPreview(theme) {
        val acc1 = Account("Cash", color = Green.toArgb())
        val acc2 = Account("DSK", color = GreenDark.toArgb())
        val cat1 = Category(
            name = NotBlankTrimmedString.unsafe("Science"),
            color = ColorInt(Purple1Dark.toArgb()),
            icon = IconAsset.unsafe("atom"),
            id = CategoryId(UUID.randomUUID()),
            orderNum = 0.0,
        )
        val state = ReportScreenState(
            baseCurrency = "BGN",
            balance = 0.0,
            income = 0.0,
            expenses = 0.0,
            upcomingIncome = 0.0,
            upcomingExpenses = 0.0,
            overdueIncome = 0.0,
            overdueExpenses = 0.0,
            history = persistentListOf(),
            upcomingTransactions = persistentListOf(),
            overdueTransactions = persistentListOf(),
            upcomingExpanded = true,
            overdueExpanded = true,
            filter = null,
            loading = false,
            accounts = persistentListOf(
                acc1,
                acc2,
                Account("phyre", color = GreenLight.toArgb(), icon = "cash"),
                Account("Revolut", color = IvyDark.toArgb()),
            ),
            categories = persistentListOf(
                cat1,
                Category(
                    name = NotBlankTrimmedString.unsafe("Pet"),
                    color = ColorInt(Red3Light.toArgb()),
                    icon = IconAsset.unsafe("pet"),
                    id = CategoryId(UUID.randomUUID()),
                    orderNum = 0.0,
                ),
                Category(
                    name = NotBlankTrimmedString.unsafe("Home"),
                    color = ColorInt(Green.toArgb()),
                    icon = null,
                    id = CategoryId(UUID.randomUUID()),
                    orderNum = 0.0,
                ),
            ),
        )

        ReportScreenContent(state = state)
    }
}

/** For screenshot testing */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportUiTest(isDark: Boolean) {
    val theme = if (isDark) Theme.DARK else Theme.LIGHT
    Preview(theme)
}

/** For screenshot testing */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportNoFilterUiTest(isDark: Boolean) {
    val theme = if (isDark) Theme.DARK else Theme.LIGHT
    Preview_NO_FILTER(theme)
}
