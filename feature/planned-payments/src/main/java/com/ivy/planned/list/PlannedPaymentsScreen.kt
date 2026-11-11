package com.ivy.planned.list

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.model.TransactionType
import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.model.IntervalType
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.design.l0_system.Purple
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.legacy.IvyWalletPreview
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.PlannedPaymentRule
import com.ivy.legacy.utils.timeNowUTC
import com.ivy.navigation.EditPlannedScreen
import com.ivy.navigation.PlannedPaymentsScreen
import com.ivy.navigation.navigation
import com.ivy.navigation.screenScopedViewModel
import com.ivy.ui.R
import com.ivy.ui.rememberScrollPositionListState
import com.ivy.wallet.ui.theme.Green
import com.ivy.wallet.ui.theme.Orange
import kotlinx.collections.immutable.persistentListOf
import java.time.ZoneOffset
import java.util.UUID

@Composable
fun BoxWithConstraintsScope.PlannedPaymentsScreen(screen: PlannedPaymentsScreen) {
    val viewModel: PlannedPaymentsViewModel = screenScopedViewModel()
    val uiState = viewModel.uiState()

    UI(
        state = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun BoxWithConstraintsScope.UI(
    state: PlannedPaymentsScreenState,
    onEvent: (PlannedPaymentsScreenEvent) -> Unit = {}
) {
    val nav = navigation()

    if (state.viewMode == PlannedPaymentsViewMode.LIST) {
        PlannedPaymentsLazyColumn(
            Header = {
                Spacer(Modifier.height(32.dp))

                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = stringResource(R.string.planned_payments_inline),
                    style = UI.typo.h2.style(
                        fontWeight = FontWeight.ExtraBold,
                        color = UI.colors.pureInverse
                    )
                )

                Spacer(Modifier.height(16.dp))

                PlannedPaymentsViewSwitcher(
                    viewMode = state.viewMode,
                    onViewModeChanged = {
                        onEvent(PlannedPaymentsScreenEvent.OnViewModeChanged(it))
                    }
                )

                Spacer(Modifier.height(20.dp))
            },
            currency = state.currency,
            categories = state.categories,
            accounts = state.accounts,
            oneTime = state.oneTimePlannedPayment,
            oneTimeIncome = state.oneTimeIncome,
            oneTimeExpenses = state.oneTimeExpenses,
            recurring = state.recurringPlannedPayment,
            recurringIncome = state.recurringIncome,
            recurringExpenses = state.recurringExpenses,
            oneTimeExpanded = state.isOneTimePaymentsExpanded,
            recurringExpanded = state.isRecurringPaymentsExpanded,
            setOneTimeExpanded = {
                onEvent(PlannedPaymentsScreenEvent.OnOneTimePaymentsExpanded(it))
            },
            setRecurringExpanded = {
                onEvent(PlannedPaymentsScreenEvent.OnRecurringPaymentsExpanded(it))
            },
            listState = rememberScrollPositionListState(key = "plannedPayments")
        )
    } else {
        val calendarListState = rememberScrollPositionListState(key = "plannedPaymentsCalendar")
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = calendarListState
        ) {
            item {
                Spacer(Modifier.height(32.dp))

                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = stringResource(R.string.planned_payments_inline),
                    style = UI.typo.h2.style(
                        fontWeight = FontWeight.ExtraBold,
                        color = UI.colors.pureInverse
                    )
                )

                Spacer(Modifier.height(16.dp))

                PlannedPaymentsViewSwitcher(
                    viewMode = state.viewMode,
                    onViewModeChanged = {
                        onEvent(PlannedPaymentsScreenEvent.OnViewModeChanged(it))
                    }
                )

                Spacer(Modifier.height(20.dp))

                MonthNavigationHeader(
                    currentMonth = state.selectedMonth,
                    onPreviousMonth = {
                        onEvent(PlannedPaymentsScreenEvent.OnMonthChanged(state.selectedMonth.minusMonths(1)))
                    },
                    onNextMonth = {
                        onEvent(PlannedPaymentsScreenEvent.OnMonthChanged(state.selectedMonth.plusMonths(1)))
                    }
                )

                Spacer(Modifier.height(16.dp))

                MonthlyPlannedSummaryCard(
                    expectedIncome = state.monthlyExpectedIncome,
                    expectedExpenses = state.monthlyExpectedExpenses,
                    currency = state.currency
                )

                Spacer(Modifier.height(16.dp))

                CalendarGrid(
                    month = state.selectedMonth,
                    selectedDate = state.selectedDate,
                    datesWithBills = state.datesWithBills,
                    onDateSelected = {
                        onEvent(PlannedPaymentsScreenEvent.OnDateSelected(it))
                    }
                )

                Spacer(Modifier.height(24.dp))

                val formattedDate = "${state.selectedDate.month.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault())} ${state.selectedDate.dayOfMonth}"
                Text(
                    modifier = Modifier.padding(start = 24.dp),
                    text = "Bills Due on $formattedDate",
                    style = UI.typo.h3.style(
                        fontWeight = FontWeight.Bold,
                        color = UI.colors.pureInverse
                    )
                )

                Spacer(Modifier.height(8.dp))
            }

            if (state.billsForSelectedDate.isEmpty()) {
                item {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        text = "No bills scheduled for this date",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = UI.typo.b2.style(
                            color = UI.colors.gray
                        )
                    )
                }
            } else {
                items(
                    count = state.billsForSelectedDate.size,
                    key = { state.billsForSelectedDate[it].id }
                ) { index ->
                    PlannedPaymentCard(
                        baseCurrency = state.currency,
                        categories = state.categories,
                        accounts = state.accounts,
                        plannedPayment = state.billsForSelectedDate[index],
                        onClick = {
                            nav.navigateTo(
                                EditPlannedScreen(
                                    type = it.type,
                                    plannedPaymentRuleId = it.id
                                )
                            )
                        }
                    )
                }
            }

            item {
                Spacer(Modifier.height(96.dp))
            }
        }
    }

    PlannedPaymentsBottomBar(
        onClose = {
            nav.back()
        },
        onAdd = {
            nav.navigateTo(
                EditPlannedScreen(
                    type = TransactionType.EXPENSE,
                    plannedPaymentRuleId = null
                )
            )
        }
    )
}

@Preview
@Composable
private fun Preview() {
    IvyWalletPreview {
        val account = Account(name = "Cash", Green.toArgb())
        val food = Category(
            name = NotBlankTrimmedString.unsafe("Food"),
            color = ColorInt(Purple.toArgb()),
            icon = null,
            id = CategoryId(UUID.randomUUID()),
            orderNum = 0.0,
        )
        val shisha = Category(
            name = NotBlankTrimmedString.unsafe("Shisha"),
            color = ColorInt(Orange.toArgb()),
            icon = null,
            id = CategoryId(UUID.randomUUID()),
            orderNum = 0.0,
        )

        UI(
            PlannedPaymentsScreenState(
                currency = "BGN",
                accounts = persistentListOf(account),
                categories = persistentListOf(food, shisha),
                oneTimePlannedPayment = persistentListOf(
                    PlannedPaymentRule(
                        accountId = account.id,
                        title = "Lidl pazar",
                        categoryId = food.id.value,
                        amount = 250.75,
                        startDate = timeNowUTC().plusDays(5).toInstant(ZoneOffset.UTC),
                        oneTime = true,
                        intervalType = null,
                        intervalN = null,
                        type = TransactionType.EXPENSE
                    )
                ),
                oneTimeExpenses = 250.75,
                oneTimeIncome = 0.0,
                recurringPlannedPayment = persistentListOf(
                    PlannedPaymentRule(
                        accountId = account.id,
                        title = "Tabu",
                        categoryId = shisha.id.value,
                        amount = 1025.5,
                        startDate = timeNowUTC().plusDays(5).toInstant(ZoneOffset.UTC),
                        oneTime = false,
                        intervalType = IntervalType.MONTH,
                        intervalN = 1,
                        type = TransactionType.EXPENSE
                    )
                ),
                recurringExpenses = 1025.5,
                recurringIncome = 0.0,
                isOneTimePaymentsExpanded = true,
                isRecurringPaymentsExpanded = true
            )
        )
    }
}
