package com.ivy.reports

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.LegacyTransaction
import com.ivy.base.legacy.TransactionHistoryItem
import com.ivy.base.legacy.stringRes
import com.ivy.base.model.TransactionType
import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import com.ivy.data.model.Category
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Tag
import com.ivy.data.model.Transaction
import com.ivy.data.model.TransactionId
import com.ivy.data.model.Transfer
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.repository.CategoryRepository
import com.ivy.data.repository.TagRepository
import com.ivy.data.repository.TransactionRepository
import com.ivy.data.repository.mapper.TransactionMapper
import com.ivy.data.temp.migration.getTransactionType
import com.ivy.data.temp.migration.getValue
import com.ivy.domain.RootScreen
import com.ivy.domain.features.Features
import com.ivy.domain.usecase.csv.ExportCsvUseCase
import com.ivy.frp.filterSuspend
import com.ivy.legacy.IvyWalletCtx
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.temp.toLegacy
import com.ivy.legacy.utils.getISOFormattedDateTime
import com.ivy.legacy.utils.scopedIOThread
import com.ivy.legacy.utils.timeNowUTC
import com.ivy.legacy.utils.toLowerCaseLocal
import com.ivy.legacy.utils.uiThread
import com.ivy.ui.ComposeViewModel
import com.ivy.ui.R
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.budget.BudgetsAct
import com.ivy.wallet.domain.action.exchange.ExchangeAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.transaction.CalcTrnsIncomeExpenseAct
import com.ivy.wallet.domain.action.transaction.TrnsWithDateDivsAct
import com.ivy.wallet.domain.deprecated.logic.PlannedPaymentsLogic
import com.ivy.wallet.domain.pure.data.IncomeExpenseTransferPair
import com.ivy.wallet.domain.pure.exchange.ExchangeData
import com.ivy.wallet.domain.pure.transaction.trnCurrency
import com.ivy.wallet.domain.pure.util.orZero
import com.ivy.wallet.ui.theme.Gray
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@Stable
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val plannedPaymentsLogic: PlannedPaymentsLogic,
    private val transactionRepository: TransactionRepository,
    private val ivyContext: IvyWalletCtx,
    private val exchangeAct: ExchangeAct,
    private val accountsAct: AccountsAct,
    private val categoryRepository: CategoryRepository,
    private val trnsWithDateDivsAct: TrnsWithDateDivsAct,
    private val calcTrnsIncomeExpenseAct: CalcTrnsIncomeExpenseAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val transactionMapper: TransactionMapper,
    private val tagRepository: TagRepository,
    private val exportCsvUseCase: ExportCsvUseCase,
    private val timeProvider: TimeProvider,
    private val timeConverter: TimeConverter,
    private val features: Features,
    private val budgetsAct: BudgetsAct
) : ComposeViewModel<ReportScreenState, ReportScreenEvent>() {
    private val unSpecifiedCategory =
        Category(
            name = NotBlankTrimmedString.unsafe(stringRes(R.string.unspecified)),
            color = ColorInt(Gray.toArgb()),
            icon = null,
            id = CategoryId(UUID.randomUUID()),
            orderNum = 0.0,
        )
    private var baseCurrency by mutableStateOf("")
    private var categories by mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private var historyIncomeExpense by mutableStateOf(IncomeExpenseTransferPair.zero())
    private var filter by mutableStateOf<ReportFilter?>(null)
    private var balance by mutableDoubleStateOf(0.0)
    private var income by mutableDoubleStateOf(0.0)
    private var expenses by mutableDoubleStateOf(0.0)
    private var upcomingIncome by mutableDoubleStateOf(0.0)
    private var upcomingExpenses by mutableDoubleStateOf(0.0)
    private var overdueIncome by mutableDoubleStateOf(0.0)
    private var overdueExpenses by mutableDoubleStateOf(0.0)
    private var history by mutableStateOf<ImmutableList<TransactionHistoryItem>>(persistentListOf())
    private var upcomingTransactions by
    mutableStateOf<ImmutableList<LegacyTransaction>>(persistentListOf())
    private var overdueTransactions by
    mutableStateOf<ImmutableList<LegacyTransaction>>(persistentListOf())
    private var accounts by mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private var upcomingExpanded by mutableStateOf(false)
    private var overdueExpanded by mutableStateOf(false)
    private var loading by mutableStateOf(false)
    private var accountIdFilters by mutableStateOf<ImmutableList<UUID>>(persistentListOf())
    private var transactions by mutableStateOf<ImmutableList<LegacyTransaction>>(persistentListOf())
    private var filterOverlayVisible by mutableStateOf(false)
    private var showTransfersAsIncExpCheckbox by mutableStateOf(false)
    private var treatTransfersAsIncExp by mutableStateOf(false)
    private var allTags by mutableStateOf<ImmutableList<Tag>>(persistentListOf())
    private var topSpendingCategories by mutableStateOf<ImmutableList<CategorySpendingItem>>(persistentListOf())
    private var topIncomeCategories by mutableStateOf<ImmutableList<CategorySpendingItem>>(persistentListOf())
    private var savingsRate by mutableFloatStateOf(0f)

    // Default Reports fields
    private var periodType by mutableStateOf(ReportPeriodType.MONTHLY)
    private var selectedYearMonth by mutableStateOf(YearMonth.now())
    private var selectedYear by mutableIntStateOf(Year.now().value)
    private var periodLabel by mutableStateOf("")
    private var canGoForward by mutableStateOf(false)
    private var intervalGraphItems by mutableStateOf<ImmutableList<IntervalGraphItem>>(persistentListOf())
    private var pieChartMode by mutableStateOf(TransactionType.EXPENSE)
    private var categoryViewMode by mutableStateOf(TransactionType.EXPENSE)
    private var dailyBurnRate by mutableDoubleStateOf(0.0)
    private var projectedPeriodExpense by mutableDoubleStateOf(0.0)
    private var projectedPeriodSavings by mutableDoubleStateOf(0.0)
    private var financialRunwayMonths by mutableDoubleStateOf(0.0)
    private var predictionStatus by mutableStateOf("On Track")
    private var balanceTrendPoints by mutableStateOf<ImmutableList<BalanceTrendPoint>>(persistentListOf())
    private var startingBalance by mutableDoubleStateOf(0.0)
    private var endingBalance by mutableDoubleStateOf(0.0)
    private var peakBalance by mutableDoubleStateOf(0.0)
    private var lowestBalance by mutableDoubleStateOf(0.0)
    private var netChange by mutableDoubleStateOf(0.0)
    private var dailyHeatmapDays by mutableStateOf<ImmutableList<DailySpendingHeatmapDay>>(persistentListOf())
    private var selectedHeatmapDay by mutableStateOf<DailySpendingHeatmapDay?>(null)
    private var categoryBudgets by mutableStateOf<ImmutableList<ReportBudgetItem>>(persistentListOf())
    private var totalBudgetLimit by mutableDoubleStateOf(0.0)
    private var totalBudgetSpent by mutableDoubleStateOf(0.0)
    private var totalBudgetRemaining by mutableDoubleStateOf(0.0)

    private var tagSearchJob: Job? = null
    private val tagSearchDebounceTimeInMills: Long = 500

    @Composable
    fun getShouldShowAccountSpecificColorInTransactions(): Boolean {
        return features.showAccountColorsInTransactions.asEnabledState()
    }

    @Composable
    override fun uiState(): ReportScreenState {
        LaunchedEffect(Unit) {
            start()
        }

        val currentCategoryList = if (categoryViewMode == TransactionType.EXPENSE) topSpendingCategories else topIncomeCategories
        val currentPieSlices = if (pieChartMode == TransactionType.EXPENSE) topSpendingCategories else topIncomeCategories

        return ReportScreenState(
            categories = categories,
            accounts = accounts,
            accountIdFilters = accountIdFilters,
            balance = balance,
            baseCurrency = baseCurrency,
            expenses = expenses,
            filter = filter,
            filterOverlayVisible = filterOverlayVisible,
            history = history,
            income = income,
            loading = loading,
            overdueExpanded = overdueExpanded,
            overdueExpenses = overdueExpenses,
            overdueIncome = overdueIncome,
            overdueTransactions = overdueTransactions,
            showTransfersAsIncExpCheckbox = showTransfersAsIncExpCheckbox,
            transactions = transactions,
            treatTransfersAsIncExp = treatTransfersAsIncExp,
            upcomingExpanded = upcomingExpanded,
            upcomingExpenses = upcomingExpenses,
            upcomingIncome = upcomingIncome,
            upcomingTransactions = upcomingTransactions,
            allTags = allTags,
            showAccountColorsInTransactions = getShouldShowAccountSpecificColorInTransactions(),
            topSpendingCategories = currentCategoryList,
            topIncomeCategories = topIncomeCategories,
            savingsRate = savingsRate,
            periodType = periodType,
            periodLabel = periodLabel,
            canGoForward = canGoForward,
            intervalGraphItems = intervalGraphItems,
            pieChartMode = pieChartMode,
            pieChartSlices = currentPieSlices,
            categoryViewMode = categoryViewMode,
            dailyBurnRate = dailyBurnRate,
            projectedPeriodExpense = projectedPeriodExpense,
            projectedPeriodSavings = projectedPeriodSavings,
            financialRunwayMonths = financialRunwayMonths,
            predictionStatus = predictionStatus,
            balanceTrendPoints = balanceTrendPoints,
            startingBalance = startingBalance,
            endingBalance = endingBalance,
            peakBalance = peakBalance,
            lowestBalance = lowestBalance,
            netChange = netChange,
            dailyHeatmapDays = dailyHeatmapDays,
            selectedHeatmapDay = selectedHeatmapDay,
            categoryBudgets = categoryBudgets,
            totalBudgetLimit = totalBudgetLimit,
            totalBudgetSpent = totalBudgetSpent,
            totalBudgetRemaining = totalBudgetRemaining
        )
    }

    override fun onEvent(event: ReportScreenEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            when (event) {
                is ReportScreenEvent.OnFilter -> setFilter(event.filter)
                is ReportScreenEvent.OnExport -> export(event.context)
                is ReportScreenEvent.OnShareSummary -> shareSummary(event.context)
                is ReportScreenEvent.OnPayOrGet -> payOrGet(event.transaction)
                is ReportScreenEvent.SkipTransaction -> skipTransaction(event.transaction)
                is ReportScreenEvent.SkipTransactions -> skipTransactions(event.transactions)
                is ReportScreenEvent.OnPayOrGetLegacy -> payOrGetLegacy(event.transaction)
                is ReportScreenEvent.SkipTransactionLegacy -> skipTransactionLegacy(event.transaction)
                is ReportScreenEvent.SkipTransactionsLegacy -> skipTransactionsLegacy(event.transactions)
                is ReportScreenEvent.OnOverdueExpanded -> setOverdueExpandedValue(event.overdueExpanded)
                is ReportScreenEvent.OnUpcomingExpanded -> setUpcomingExpandedValue(event.upcomingExpanded)
                is ReportScreenEvent.OnFilterOverlayVisible -> setFilterOverlayVisibleValue(event.filterOverlayVisible)
                is ReportScreenEvent.OnTreatTransfersAsIncomeExpense -> onTreatTransfersAsIncomeExpense(
                    event.transfersAsIncomeExpense
                )

                is ReportScreenEvent.OnTagSearch -> onTagSearch(event.data)
                is ReportScreenEvent.OnSelectPeriodType -> selectPeriodType(event.periodType)
                ReportScreenEvent.OnPreviousPeriod -> previousPeriod()
                ReportScreenEvent.OnNextPeriod -> nextPeriod()
                is ReportScreenEvent.OnTogglePieChartMode -> { pieChartMode = event.type }
                is ReportScreenEvent.OnToggleCategoryViewMode -> { categoryViewMode = event.type }
                is ReportScreenEvent.OnSelectHeatmapDay -> {
                    selectedHeatmapDay = if (selectedHeatmapDay?.dayOfMonth == event.day?.dayOfMonth) null else event.day
                }
            }
        }
    }

    private suspend fun onTagSearch(query: String) {
        withContext(Dispatchers.IO) {
            tagSearchJob?.cancelAndJoin()
            delay(tagSearchDebounceTimeInMills) // Debounce effect
            tagSearchJob = launch(Dispatchers.IO) {
                NotBlankTrimmedString.from(query.toLowerCaseLocal())
                    .fold(
                        ifRight = {
                            allTags =
                                tagRepository.findByText(text = it.value).toImmutableList()
                        },
                        ifLeft = {
                            allTags = tagRepository.findAll().toImmutableList()
                        }
                    )
            }
        }
    }

    private fun start() {
        viewModelScope.launch(Dispatchers.IO) {
            baseCurrency = baseCurrencyAct(Unit)
            accounts = accountsAct(Unit)
            categories =
                (listOf(unSpecifiedCategory) + categoryRepository.findAll()).toImmutableList()
            allTags = tagRepository.findAll().toImmutableList()
            if (filter == null) {
                recomputeDefaultReports()
            }
        }
    }

    private fun selectPeriodType(type: ReportPeriodType) {
        periodType = type
        viewModelScope.launch(Dispatchers.IO) {
            recomputeDefaultReports()
        }
    }

    private fun previousPeriod() {
        when (periodType) {
            ReportPeriodType.MONTHLY -> selectedYearMonth = selectedYearMonth.minusMonths(1)
            ReportPeriodType.YEARLY -> selectedYear -= 1
            ReportPeriodType.ALL_TIME -> {}
        }
        viewModelScope.launch(Dispatchers.IO) {
            recomputeDefaultReports()
        }
    }

    private fun nextPeriod() {
        when (periodType) {
            ReportPeriodType.MONTHLY -> if (canGoForward) selectedYearMonth = selectedYearMonth.plusMonths(1)
            ReportPeriodType.YEARLY -> if (canGoForward) selectedYear += 1
            ReportPeriodType.ALL_TIME -> {}
        }
        viewModelScope.launch(Dispatchers.IO) {
            recomputeDefaultReports()
        }
    }

    private suspend fun recomputeDefaultReports() {
        withContext(Dispatchers.IO) {
            loading = true
            val allTransactions = transactionRepository.findAll()
            val allAccounts = accountsAct(Unit)
            accounts = allAccounts.toImmutableList()
            val allCats = (listOf(unSpecifiedCategory) + categoryRepository.findAll()).toImmutableList()
            categories = allCats
            val categoryMap = allCats.associateBy { it.id.value }

            val now = LocalDate.now()
            val (periodLabelStr, canForward, periodTrns) = when (periodType) {
                ReportPeriodType.MONTHLY -> {
                    val label = "${selectedYearMonth.month.getDisplayName(TextStyle.FULL, Locale.US)} ${selectedYearMonth.year}"
                    val canFwd = selectedYearMonth.isBefore(YearMonth.now())
                    val startInstant = selectedYearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                    val endInstant = selectedYearMonth.atEndOfMonth().atTime(23, 59, 59, 999_999_999).atZone(ZoneId.systemDefault()).toInstant()
                    val trns = allTransactions.filter { it.time in startInstant..endInstant }
                    Triple(label, canFwd, trns)
                }
                ReportPeriodType.YEARLY -> {
                    val label = "$selectedYear"
                    val canFwd = selectedYear < Year.now().value
                    val startInstant = LocalDate.of(selectedYear, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                    val endInstant = LocalDate.of(selectedYear, 12, 31).atTime(23, 59, 59, 999_999_999).atZone(ZoneId.systemDefault()).toInstant()
                    val trns = allTransactions.filter { it.time in startInstant..endInstant }
                    Triple(label, canFwd, trns)
                }
                ReportPeriodType.ALL_TIME -> {
                    val label = "All Recorded Time"
                    Triple(label, false, allTransactions)
                }
            }

            periodLabel = periodLabelStr
            canGoForward = canForward

            val periodIncomes = periodTrns.filterIsInstance<Income>()
            val periodExpenses = periodTrns.filterIsInstance<Expense>()

            val inc = periodIncomes.sumOf { it.value.amount.value }
            val exp = periodExpenses.sumOf { it.value.amount.value }

            income = inc
            expenses = exp
            balance = inc - exp
            savingsRate = if (inc > 0) (((inc - exp) / inc) * 100).toFloat().coerceIn(-100f, 100f) else 0f

            // Category breakdown for expenses
            val totalExp = if (exp > 0) exp else 1.0
            topSpendingCategories = periodExpenses
                .groupBy { it.category?.value }
                .map { (catId, trns) ->
                    val cat = catId?.let { categoryMap[it] } ?: unSpecifiedCategory
                    val sum = trns.sumOf { it.value.amount.value }
                    val pct = ((sum / totalExp) * 100).toFloat().coerceIn(0f, 100f)
                    CategorySpendingItem(
                        categoryName = cat.name.value,
                        categoryColor = cat.color.value,
                        categoryIcon = cat.icon?.id,
                        totalAmount = sum,
                        percentage = pct,
                        transactionCount = trns.size
                    )
                }
                .sortedByDescending { it.totalAmount }
                .toImmutableList()

            // Category breakdown for income
            val totalInc = if (inc > 0) inc else 1.0
            topIncomeCategories = periodIncomes
                .groupBy { it.category?.value }
                .map { (catId, trns) ->
                    val cat = catId?.let { categoryMap[it] } ?: unSpecifiedCategory
                    val sum = trns.sumOf { it.value.amount.value }
                    val pct = ((sum / totalInc) * 100).toFloat().coerceIn(0f, 100f)
                    CategorySpendingItem(
                        categoryName = cat.name.value,
                        categoryColor = cat.color.value,
                        categoryIcon = cat.icon?.id,
                        totalAmount = sum,
                        percentage = pct,
                        transactionCount = trns.size
                    )
                }
                .sortedByDescending { it.totalAmount }
                .toImmutableList()

            // Interval Graph Items
            val graphItems = mutableListOf<IntervalGraphItem>()
            when (periodType) {
                ReportPeriodType.MONTHLY -> {
                    val daysInMonth = selectedYearMonth.lengthOfMonth()
                    val intervals = listOf(
                        "1-7" to (1..7),
                        "8-14" to (8..14),
                        "15-21" to (15..21),
                        "22-28" to (22..28),
                        "29+" to (29..daysInMonth)
                    )
                    intervals.forEach { (lbl, range) ->
                        val subIncomes = periodIncomes.filter {
                            val day = it.time.atZone(ZoneId.systemDefault()).dayOfMonth
                            day in range
                        }.sumOf { it.value.amount.value }
                        val subExpenses = periodExpenses.filter {
                            val day = it.time.atZone(ZoneId.systemDefault()).dayOfMonth
                            day in range
                        }.sumOf { it.value.amount.value }
                        graphItems.add(IntervalGraphItem(label = lbl, income = subIncomes, expense = subExpenses))
                    }
                }
                ReportPeriodType.YEARLY -> {
                    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                    for (m in 1..12) {
                        val subIncomes = periodIncomes.filter {
                            it.time.atZone(ZoneId.systemDefault()).monthValue == m
                        }.sumOf { it.value.amount.value }
                        val subExpenses = periodExpenses.filter {
                            it.time.atZone(ZoneId.systemDefault()).monthValue == m
                        }.sumOf { it.value.amount.value }
                        graphItems.add(IntervalGraphItem(label = monthNames[m - 1], income = subIncomes, expense = subExpenses))
                    }
                }
                ReportPeriodType.ALL_TIME -> {
                    val years = periodTrns.map { it.time.atZone(ZoneId.systemDefault()).year }.distinct().sorted()
                    val displayYears = if (years.isEmpty()) listOf(Year.now().value) else years.takeLast(5)
                    displayYears.forEach { yr ->
                        val subIncomes = periodIncomes.filter {
                            it.time.atZone(ZoneId.systemDefault()).year == yr
                        }.sumOf { it.value.amount.value }
                        val subExpenses = periodExpenses.filter {
                            it.time.atZone(ZoneId.systemDefault()).year == yr
                        }.sumOf { it.value.amount.value }
                        graphItems.add(IntervalGraphItem(label = yr.toString(), income = subIncomes, expense = subExpenses))
                    }
                }
            }
            intervalGraphItems = graphItems.toImmutableList()

            // Daily Spending Activity Heatmap
            if (periodType == ReportPeriodType.MONTHLY) {
                val daysInMonth = selectedYearMonth.lengthOfMonth()
                val dayExpensesMap = periodExpenses.groupBy {
                    it.time.atZone(ZoneId.systemDefault()).dayOfMonth
                }
                val dayIncomesMap = periodIncomes.groupBy {
                    it.time.atZone(ZoneId.systemDefault()).dayOfMonth
                }

                val maxDailyExp = (1..daysInMonth).maxOfOrNull { d ->
                    dayExpensesMap[d]?.sumOf { it.value.amount.value } ?: 0.0
                }?.coerceAtLeast(1.0) ?: 1.0

                val dFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM", Locale.US)
                dailyHeatmapDays = (1..daysInMonth).map { d ->
                    val dayDate = selectedYearMonth.atDay(d)
                    val dExp = dayExpensesMap[d]?.sumOf { it.value.amount.value } ?: 0.0
                    val dInc = dayIncomesMap[d]?.sumOf { it.value.amount.value } ?: 0.0
                    val count = (dayExpensesMap[d]?.size ?: 0) + (dayIncomesMap[d]?.size ?: 0)
                    val intensity = if (dExp > 0) (dExp / maxDailyExp).toFloat().coerceIn(0.15f, 1f) else 0f
                    DailySpendingHeatmapDay(
                        dayOfMonth = d,
                        dayOfWeek = dayDate.dayOfWeek.value,
                        dateString = dayDate.format(dFormatter),
                        totalExpense = dExp,
                        totalIncome = dInc,
                        transactionCount = count,
                        intensity = intensity
                    )
                }.toImmutableList()

                selectedHeatmapDay = selectedHeatmapDay?.let { sel ->
                    dailyHeatmapDays.find { it.dayOfMonth == sel.dayOfMonth }
                }
            } else {
                dailyHeatmapDays = persistentListOf()
                selectedHeatmapDay = null
            }

            // Category Budgets Calculation
            val allBudgets = budgetsAct(Unit)
            if (allBudgets.isNotEmpty()) {
                val budgetItems = allBudgets.map { b ->
                    val catIds = b.parseCategoryIds()
                    val accIds = b.parseAccountIds()

                    val matchedExpenses = periodExpenses.filter { trn ->
                        val trnCatId = trn.category?.value
                        val trnAccId = trn.account.value
                        val matchesCat = catIds.isEmpty() || (trnCatId != null && catIds.contains(trnCatId))
                        val matchesAcc = accIds.isEmpty() || accIds.contains(trnAccId)
                        matchesCat && matchesAcc
                    }

                    val spent = matchedExpenses.sumOf { it.value.amount.value }
                    val limit = b.amount.coerceAtLeast(0.01)
                    val pct = ((spent / limit) * 100).toFloat().coerceIn(0f, 999f)
                    val remaining = b.amount - spent
                    val primaryCat = catIds.firstOrNull()?.let { categoryMap[it] }

                    ReportBudgetItem(
                        id = b.id,
                        name = b.name,
                        limitAmount = b.amount,
                        spentAmount = spent,
                        percentage = pct,
                        remainingAmount = remaining,
                        isExceeded = spent > b.amount,
                        primaryCategoryName = primaryCat?.name?.value,
                        primaryCategoryColor = primaryCat?.color?.value,
                        primaryCategoryIcon = primaryCat?.icon?.id
                    )
                }

                categoryBudgets = budgetItems.toImmutableList()
                totalBudgetLimit = budgetItems.sumOf { it.limitAmount }
                totalBudgetSpent = budgetItems.sumOf { it.spentAmount }
                totalBudgetRemaining = totalBudgetLimit - totalBudgetSpent
            } else {
                categoryBudgets = persistentListOf()
                totalBudgetLimit = 0.0
                totalBudgetSpent = 0.0
                totalBudgetRemaining = 0.0
            }

            // Future Prediction & Runway
            val (daysElapsed, daysRemaining) = when (periodType) {
                ReportPeriodType.MONTHLY -> {
                    val daysInMonth = selectedYearMonth.lengthOfMonth()
                    if (selectedYearMonth == YearMonth.now()) {
                        val elapsed = now.dayOfMonth.coerceAtLeast(1)
                        elapsed to (daysInMonth - elapsed).coerceAtLeast(0)
                    } else if (selectedYearMonth.isBefore(YearMonth.now())) {
                        daysInMonth to 0
                    } else {
                        1 to (daysInMonth - 1)
                    }
                }
                ReportPeriodType.YEARLY -> {
                    val daysInYear = Year.of(selectedYear).length()
                    if (selectedYear == Year.now().value) {
                        val elapsed = now.dayOfYear.coerceAtLeast(1)
                        elapsed to (daysInYear - elapsed).coerceAtLeast(0)
                    } else if (selectedYear < Year.now().value) {
                        daysInYear to 0
                    } else {
                        1 to (daysInYear - 1)
                    }
                }
                ReportPeriodType.ALL_TIME -> {
                    val totalDays = 365
                    totalDays to 0
                }
            }

            val burn = if (daysElapsed > 0) exp / daysElapsed else 0.0
            dailyBurnRate = burn
            projectedPeriodExpense = exp + (burn * daysRemaining)
            projectedPeriodSavings = inc - projectedPeriodExpense

            val totalAccBalance = if (balance > 0) balance else inc
            val monthlyBurn = burn * 30.4
            financialRunwayMonths = when {
                monthlyBurn > 0.01 && totalAccBalance > 0 -> totalAccBalance / monthlyBurn
                totalAccBalance <= 0 -> 0.0
                else -> 99.0
            }

            predictionStatus = when {
                inc > 0 && projectedPeriodExpense <= inc * 0.7 -> "Excellent Savings"
                inc > 0 && projectedPeriodExpense <= inc -> "On Track"
                projectedPeriodExpense > inc && inc > 0 -> "Caution: Over Budget"
                else -> "Active Pace"
            }

            // Balance Trend
            val sortedTrns = periodTrns.sortedBy { it.time }
            val trendPts = mutableListOf<BalanceTrendPoint>()
            var runningBalance = 0.0
            trendPts.add(BalanceTrendPoint(label = "Start", balance = 0.0))

            if (sortedTrns.isNotEmpty()) {
                val groupedByDay = sortedTrns.groupBy {
                    val ldt = it.time.atZone(ZoneId.systemDefault())
                    "${ldt.monthValue}/${ldt.dayOfMonth}"
                }
                groupedByDay.forEach { (dayLabel, trns) ->
                    val dayInc = trns.filterIsInstance<Income>().sumOf { it.value.amount.value }
                    val dayExp = trns.filterIsInstance<Expense>().sumOf { it.value.amount.value }
                    runningBalance += (dayInc - dayExp)
                    trendPts.add(BalanceTrendPoint(label = dayLabel, balance = runningBalance))
                }
            } else {
                trendPts.add(BalanceTrendPoint(label = "End", balance = 0.0))
            }

            balanceTrendPoints = trendPts.toImmutableList()
            startingBalance = 0.0
            endingBalance = runningBalance
            netChange = runningBalance
            peakBalance = trendPts.maxOfOrNull { it.balance } ?: 0.0
            lowestBalance = trendPts.minOfOrNull { it.balance } ?: 0.0

            transactions = periodTrns.map { it.toLegacy(transactionMapper) }.toImmutableList()
            loading = false
        }
    }

    private suspend fun setFilter(reportFilter: ReportFilter?) {
        scopedIOThread { scope ->
            if (reportFilter == null) {
                setReportValues(
                    income = 0.00,
                    expense = 0.00,
                    upcomingIncomeExpenseTransferPair = IncomeExpenseTransferPair.zero(),
                    overDueIncomeExpenseTransferPair = IncomeExpenseTransferPair.zero(),
                    history = persistentListOf(),
                    upcomingTransactions = persistentListOf(),
                    overdueTransactions = persistentListOf(),
                    accounts = accountsAct(Unit),
                    reportFilter = filter,
                    accountIdFilters = persistentListOf(),
                    transactions = persistentListOf(),
                    balanceValue = 0.00
                )
                topSpendingCategories = persistentListOf()
                savingsRate = 0f
                return@scopedIOThread
            }

            if (!reportFilter.validate()) return@scopedIOThread
            val tempAccounts = reportFilter.accounts
            val baseCurrency = baseCurrency
            loading = true

            val transactionsList = filterTransactions(
                baseCurrency = baseCurrency,
                accounts = tempAccounts,
                filter = reportFilter
            )

            val tempHistory = transactionsList
                .sortedByDescending { it.time }

            val historyWithDateDividers = scope.async {
                trnsWithDateDivsAct(
                    TrnsWithDateDivsAct.Input(
                        baseCurrency = baseCurrency,
                        transactions = tempHistory
                    )
                )
            }

            historyIncomeExpense = calcTrnsIncomeExpenseAct(
                CalcTrnsIncomeExpenseAct.Input(
                    transactions = tempHistory,
                    accounts = tempAccounts,
                    baseCurrency = baseCurrency
                )
            )

            val tempIncome = historyIncomeExpense.income.toDouble() +
                    if (treatTransfersAsIncExp) historyIncomeExpense.transferIncome.toDouble() else 0.0

            val tempExpenses = historyIncomeExpense.expense.toDouble() +
                    if (treatTransfersAsIncExp) historyIncomeExpense.transferExpense.toDouble() else 0.0

            val tempBalance = calculateBalance(historyIncomeExpense).toDouble()

            val accountFilterIdList = scope.async { reportFilter.accounts.map { it.id } }

            val timeNowUTC = timeNowUTC()

            // Upcoming
            val upcomingTransactionsList = transactionsList
                .filter {
                    !it.settled && it.time.atZone(ZoneId.systemDefault()).toLocalDateTime()
                        .isAfter(timeNowUTC)
                }
                .sortedBy { it.time }
                .toImmutableList()

            val upcomingIncomeExpense = calcTrnsIncomeExpenseAct(
                CalcTrnsIncomeExpenseAct.Input(
                    transactions = upcomingTransactionsList,
                    accounts = tempAccounts,
                    baseCurrency = baseCurrency
                )
            )
            // Overdue
            val overdue = transactionsList.filter {
                !it.settled && it.time.atZone(ZoneId.systemDefault()).toLocalDateTime()
                    .isBefore(timeNowUTC)
            }.sortedByDescending {
                it.time
            }.toImmutableList()
            val overdueIncomeExpense = calcTrnsIncomeExpenseAct(
                CalcTrnsIncomeExpenseAct.Input(
                    transactions = overdue,
                    accounts = tempAccounts,
                    baseCurrency = baseCurrency
                )
            )

            setReportValues(
                income = tempIncome,
                expense = tempExpenses,
                upcomingIncomeExpenseTransferPair = upcomingIncomeExpense,
                overDueIncomeExpenseTransferPair = overdueIncomeExpense,
                history = historyWithDateDividers.await().toImmutableList(),
                upcomingTransactions = upcomingTransactionsList.map {
                    it.toLegacy(transactionMapper)
                }.toImmutableList(),
                overdueTransactions = overdue.map {
                    it.toLegacy(transactionMapper)
                }.toImmutableList(),
                accounts = tempAccounts.toImmutableList(),
                reportFilter = reportFilter,
                accountIdFilters = accountFilterIdList.await().toImmutableList(),
                transactions = transactionsList.map {
                    it.toLegacy(transactionMapper)
                }.toImmutableList(),
                balanceValue = tempBalance
            )

            val expenseTrns = transactionsList.filter {
                with(transactionMapper) { it.getTransactionType() == TransactionType.EXPENSE }
            }
            val totalExpenseVal = if (tempExpenses > 0.0) tempExpenses else 1.0
            val categoryMap = categories.associateBy { it.id.value }

            topSpendingCategories = expenseTrns
                .groupBy { it.category?.value }
                .map { (catId, trns) ->
                    val cat = catId?.let { categoryMap[it] } ?: unSpecifiedCategory
                    val sum = trns.sumOf { (it as? Expense)?.value?.amount?.value ?: 0.0 }
                    val pct = ((sum / totalExpenseVal) * 100).toFloat().coerceIn(0f, 100f)
                    CategorySpendingItem(
                        categoryName = cat.name.value,
                        categoryColor = cat.color.value,
                        categoryIcon = cat.icon?.id,
                        totalAmount = sum,
                        percentage = pct,
                        transactionCount = trns.size
                    )
                }
                .sortedByDescending { it.totalAmount }
                .toImmutableList()

            savingsRate = if (tempIncome > 0.0) {
                (((tempIncome - tempExpenses) / tempIncome) * 100).toFloat().coerceIn(-100f, 100f)
            } else {
                0f
            }

            loading = false
        }
    }

    private fun setReportValues(
        income: Double,
        expense: Double,
        upcomingIncomeExpenseTransferPair: IncomeExpenseTransferPair,
        overDueIncomeExpenseTransferPair: IncomeExpenseTransferPair,
        history: ImmutableList<TransactionHistoryItem>,
        upcomingTransactions: ImmutableList<LegacyTransaction>,
        overdueTransactions: ImmutableList<LegacyTransaction>,
        accounts: ImmutableList<Account>,
        reportFilter: ReportFilter? = null,
        accountIdFilters: ImmutableList<UUID>,
        transactions: ImmutableList<LegacyTransaction>,
        balanceValue: Double
    ) {
        this.income = income
        this.expenses = expense
        this.upcomingExpenses = upcomingIncomeExpenseTransferPair.expense.toDouble()
        this.upcomingIncome = upcomingIncomeExpenseTransferPair.income.toDouble()
        this.overdueIncome = overDueIncomeExpenseTransferPair.income.toDouble()
        this.overdueExpenses = overDueIncomeExpenseTransferPair.expense.toDouble()
        this.history = history
        this.upcomingTransactions = upcomingTransactions
        this.overdueTransactions = overdueTransactions
        this.accounts = accounts
        this.filter = reportFilter
        this.accountIdFilters = accountIdFilters
        this.transactions = transactions
        this.balance = balanceValue
        this.showTransfersAsIncExpCheckbox =
            reportFilter?.trnTypes?.contains(TransactionType.TRANSFER) ?: false
    }

    private suspend fun filterTransactions(
        baseCurrency: String,
        accounts: List<Account>,
        filter: ReportFilter,
    ): ImmutableList<Transaction> {
        val filterAccountIds = filter.accounts.map { it.id }
        val filterCategoryIds =
            filter.categories.map { if (it.id.value == unSpecifiedCategory.id.value) null else it.id }
        val filterRange =
            filter.period?.toRange(ivyContext.startDayOfMonth, timeConverter, timeProvider)

        val transactions = if (filter.includedTags.isNotEmpty()) {
            tagRepository.findByAllAssociatedIdForTagId(filter.includedTags)
                .asSequence()
                .flatMap { it.value }
                .map { TransactionId(it.associatedId.value) }
                .distinct()
                .toList()
                .let {
                    transactionRepository.findByIds(it)
                }
        } else {
            transactionRepository.findAll()
        }

        val excludeableByTagTransactionsIds = if (filter.excludedTags.isNotEmpty()) {
            tagRepository.findByAllAssociatedIdForTagId(filter.excludedTags)
                .asSequence()
                .flatMap { it.value }
                .distinct()
                .map { TransactionId(it.associatedId.value) }
                .toList()
                .let {
                    transactionRepository.findByIds(it)
                }.map {
                    it.id
                }
        } else {
            emptyList()
        }

        return transactions
            .filter { !excludeableByTagTransactionsIds.contains(it.id) }
            .filter {
                with(transactionMapper) {
                    filter.trnTypes.contains(it.getTransactionType())
                }
            }
            .filter {
                // Filter by Time Period

                filterRange ?: return@filter false

                filterRange.includes(it.time)
            }
            .filter { trn ->
                // Filter by Accounts
                when (trn) {
                    is Transfer -> {
                        filterAccountIds.contains(trn.fromAccount.value) || // Transfers Out
                                (filterAccountIds.contains(trn.toAccount.value)) // Transfers In
                    }

                    is Expense -> {
                        filterAccountIds.contains(trn.account.value)
                    }

                    is Income -> {
                        filterAccountIds.contains(trn.account.value)
                    }
                }
            }
            .filter { trn ->
                // Filter by Categories

                filterCategoryIds.contains(trn.category) || with(transactionMapper) {
                    (trn.getTransactionType() == TransactionType.TRANSFER)
                }
            }
            .filterSuspend {
                // Filter by Amount
                // !NOTE: Amount must be converted to baseCurrency amount

                val trnAmountBaseCurrency = exchangeAct(
                    ExchangeAct.Input(
                        data = ExchangeData(
                            baseCurrency = baseCurrency,
                            fromCurrency = trnCurrency(it, accounts, baseCurrency),
                        ),
                        amount = it.getValue()
                    )
                ).orZero().toDouble()

                (filter.minAmount == null || trnAmountBaseCurrency >= filter.minAmount) &&
                        (filter.maxAmount == null || trnAmountBaseCurrency <= filter.maxAmount)
            }
            .filter {
                // Filter by Included Keywords

                val includeKeywords = filter.includeKeywords
                if (includeKeywords.isEmpty()) return@filter true

                it.title?.let { title ->
                    includeKeywords.forEach { keyword ->
                        if (title.value.containsLowercase(keyword)) {
                            return@filter true
                        }
                    }
                }

                it.description?.let { description ->
                    includeKeywords.forEach { keyword ->
                        if (description.value.containsLowercase(keyword)) {
                            return@filter true
                        }
                    }
                }

                false
            }
            .filter {
                // Filter by Excluded Keywords

                val excludedKeywords = filter.excludeKeywords
                if (excludedKeywords.isEmpty()) return@filter true

                it.title?.let { title ->
                    excludedKeywords.forEach { keyword ->
                        if (title.value.containsLowercase(keyword)) {
                            return@filter false
                        }
                    }
                }
                it.description?.let { description ->
                    excludedKeywords.forEach { keyword ->
                        if (description.value.containsLowercase(keyword)) {
                            return@filter false
                        }
                    }
                }
                true
            }.toImmutableList()
    }

    private fun String.containsLowercase(anotherString: String): Boolean {
        return this.toLowerCaseLocal().contains(anotherString.toLowerCaseLocal())
    }

    private fun calculateBalance(incomeExpenseTransferPair: IncomeExpenseTransferPair): BigDecimal {
        return incomeExpenseTransferPair.income + incomeExpenseTransferPair.transferIncome - incomeExpenseTransferPair.expense - incomeExpenseTransferPair.transferExpense
    }

    private suspend fun export(context: Context) {
        val filter = filter ?: return
        if (!filter.validate()) return

        ivyContext.createNewFile(
            "IvyWalletReport-${
                timeNowUTC().getISOFormattedDateTime()
            }.csv"
        ) { fileUri ->
            viewModelScope.launch {
                loading = true

                exportCsvUseCase.exportToFile(
                    outputFile = fileUri,
                    exportScope = {
                        filterTransactions(
                            baseCurrency = baseCurrency,
                            accounts = accounts,
                            filter = filter
                        )
                    }
                )

                (context as RootScreen).shareCSVFile(
                    fileUri = fileUri
                )

                loading = false
            }
        }
    }

    private fun setUpcomingExpandedValue(expanded: Boolean) {
        upcomingExpanded = expanded
    }

    private fun setOverdueExpandedValue(expanded: Boolean) {
        overdueExpanded = expanded
    }

    private suspend fun payOrGet(transaction: Transaction) {
        uiThread {
            plannedPaymentsLogic.payOrGet(
                transaction = transaction
            ) {
                start()
                setFilter(filter)
            }
        }
    }

    @Deprecated("Uses legacy Transaction")
    private suspend fun payOrGetLegacy(transaction: com.ivy.base.legacy.Transaction) {
        uiThread {
            plannedPaymentsLogic.payOrGetLegacy(transaction = transaction) {
                start()
                setFilter(filter)
            }
        }
    }

    private fun setFilterOverlayVisibleValue(visible: Boolean) {
        filterOverlayVisible = visible
    }

    private fun onTreatTransfersAsIncomeExpense(transfersAsIncExp: Boolean) {
        income = historyIncomeExpense.income.toDouble() +
                if (transfersAsIncExp) historyIncomeExpense.transferIncome.toDouble() else 0.0
        expenses = historyIncomeExpense.expense.toDouble() +
                if (transfersAsIncExp) historyIncomeExpense.transferExpense.toDouble() else 0.0
        treatTransfersAsIncExp = transfersAsIncExp
    }

    private suspend fun skipTransaction(transaction: Transaction) {
        uiThread {
            plannedPaymentsLogic.payOrGet(
                transaction = transaction,
                skipTransaction = true
            ) {
                start()
                setFilter(filter)
            }
        }
    }

    @Deprecated("Uses legacy Transaction")
    private suspend fun skipTransactionLegacy(transaction: com.ivy.base.legacy.Transaction) {
        uiThread {
            plannedPaymentsLogic.payOrGetLegacy(
                transaction = transaction,
                skipTransaction = true
            ) {
                start()
                setFilter(filter)
            }
        }
    }

    private suspend fun skipTransactions(transactions: List<Transaction>) {
        uiThread {
            plannedPaymentsLogic.payOrGet(
                transactions = transactions,
                skipTransaction = true
            ) {
                start()
                setFilter(filter)
            }
        }
    }

    @Deprecated("Uses legacy Transaction")
    private suspend fun skipTransactionsLegacy(transactions: List<com.ivy.base.legacy.Transaction>) {
        uiThread {
            plannedPaymentsLogic.payOrGetLegacy(
                transactions = transactions,
                skipTransaction = true
            ) {
                start()
                setFilter(filter)
            }
        }
    }

    private fun shareSummary(context: Context) {
        val periodText = filter?.period?.toString() ?: periodLabel.ifEmpty { "Current Period" }
        val currency = baseCurrency
        val net = income - expenses
        val netSign = if (net >= 0) "+" else ""

        val sb = StringBuilder()
        sb.appendLine("═════════════════════════════════════")
        sb.appendLine("   IVY WALLET - FINANCIAL REPORT")
        sb.appendLine("═════════════════════════════════════")
        sb.appendLine("Period: $periodText")
        sb.appendLine("Currency: $currency")
        sb.appendLine()
        sb.appendLine("▶ FINANCIAL SUMMARY")
        sb.appendLine("• Total Income:   $currency${String.format(java.util.Locale.US, "%.2f", income)}")
        sb.appendLine("• Total Expenses: $currency${String.format(java.util.Locale.US, "%.2f", expenses)}")
        sb.appendLine("• Net Cash Flow:  $netSign$currency${String.format(java.util.Locale.US, "%.2f", net)}")
        sb.appendLine("• Savings Rate:   ${String.format(java.util.Locale.US, "%.1f%%", savingsRate)}")
        sb.appendLine()

        if (topSpendingCategories.isNotEmpty()) {
            sb.appendLine("▶ TOP SPENDING CATEGORIES")
            topSpendingCategories.forEachIndexed { index, cat ->
                sb.appendLine(
                    "${index + 1}. ${cat.categoryName}: $currency${String.format(java.util.Locale.US, "%.2f", cat.totalAmount)} (${String.format(java.util.Locale.US, "%.1f%%", cat.percentage)}) - ${cat.transactionCount} txn${if (cat.transactionCount != 1) "s" else ""}"
                )
            }
            sb.appendLine()
        }

        sb.appendLine("═════════════════════════════════════")
        sb.appendLine("Generated with Ivy Wallet Personal (100% Offline & Private)")

        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(android.content.Intent.EXTRA_SUBJECT, "Financial Report - Ivy Wallet")
            putExtra(android.content.Intent.EXTRA_TEXT, sb.toString())
        }
        context.startActivity(android.content.Intent.createChooser(intent, "Share Financial Report"))
    }
}