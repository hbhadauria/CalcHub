package com.hsb.calchub.ui.navigation

import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.hsb.calchub.ui.screens.MainScreen
import com.hsb.calchub.ui.screens.apy.ApyCalculatorScreen
import com.hsb.calchub.ui.screens.brokerage.BrokerageCalculatorScreen
import com.hsb.calchub.ui.screens.cagr.CagrCalculatorScreen
import com.hsb.calchub.ui.screens.car_loan_emi.CarLoanEmiCalculatorScreen
import com.hsb.calchub.ui.screens.compound_interest.CompoundInterestCalculatorScreen
import com.hsb.calchub.ui.screens.emi.EmiCalculatorScreen
import com.hsb.calchub.ui.screens.epf.EpfCalculatorScreen
import com.hsb.calchub.ui.screens.fd.FdCalculatorScreen
import com.hsb.calchub.ui.screens.flat_vs_reducing.FlatVsReducingCalculatorScreen
import com.hsb.calchub.ui.screens.gratuity.GratuityCalculatorScreen
import com.hsb.calchub.ui.screens.gst.GstCalculatorScreen
import com.hsb.calchub.ui.screens.home_loan_emi.HomeLoanEmiCalculatorScreen
import com.hsb.calchub.ui.screens.hra.HraCalculatorScreen
import com.hsb.calchub.ui.screens.income_tax.IncomeTaxCalculatorScreen
import com.hsb.calchub.ui.screens.inflation.InflationCalculatorScreen
import com.hsb.calchub.ui.screens.lumpsum.LumpsumCalculatorScreen
import com.hsb.calchub.ui.screens.margin.MarginCalculatorScreen
import com.hsb.calchub.ui.screens.mf_returns.MfReturnsCalculatorScreen
import com.hsb.calchub.ui.screens.nps.NpsCalculatorScreen
import com.hsb.calchub.ui.screens.nsc.NscCalculatorScreen
import com.hsb.calchub.ui.screens.post_office_mis.PostOfficeMisCalculatorScreen
import com.hsb.calchub.ui.screens.ppf.PpfCalculatorScreen
import com.hsb.calchub.ui.screens.rd.RdCalculatorScreen
import com.hsb.calchub.ui.screens.retirement.RetirementCalculatorScreen
import com.hsb.calchub.ui.screens.salary.SalaryCalculatorScreen
import com.hsb.calchub.ui.screens.scss.ScssCalculatorScreen
import com.hsb.calchub.ui.screens.simple_interest.SimpleInterestCalculatorScreen
import com.hsb.calchub.ui.screens.sip.SipCalculatorScreen
import com.hsb.calchub.ui.screens.ssy.SsyCalculatorScreen
import com.hsb.calchub.ui.screens.step_up_sip.StepUpSipCalculatorScreen
import com.hsb.calchub.ui.screens.stock_average.StockAverageCalculatorScreen
import com.hsb.calchub.ui.screens.swp.SwpCalculatorScreen
import com.hsb.calchub.ui.screens.tds.TdsCalculatorScreen
import com.hsb.calchub.ui.screens.xirr.XirrCalculatorScreen
import com.hsb.calchub.ui.theme.LocalCalcHubTokens
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

/**
 * Type-safe Navigation 3 key hierarchy backed by [SnapshotStateList] and [rememberSaveable].
 */
@Immutable
sealed interface CalcNavKey {
    @Immutable
    data object MainHub : CalcNavKey

    @Immutable
    data class CalculatorDetail(val route: String) : CalcNavKey
}

private val CalcBackStackSaver = listSaver<SnapshotStateList<CalcNavKey>, String>(
    save = { list ->
        list.map { key ->
            when (key) {
                is CalcNavKey.MainHub -> "main"
                is CalcNavKey.CalculatorDetail -> "calc:${key.route}"
            }
        }
    },
    restore = { saved ->
        val restored = saved.mapNotNull { encoded ->
            when {
                encoded == "main" -> CalcNavKey.MainHub
                encoded.startsWith("calc:") -> CalcNavKey.CalculatorDetail(encoded.removePrefix("calc:"))
                else -> null
            }
        }
        mutableStateListOf<CalcNavKey>().apply {
            if (restored.isEmpty()) add(CalcNavKey.MainHub) else addAll(restored)
        }
    }
)

@Composable
fun rememberCalcBackStack(
    initialKey: CalcNavKey = CalcNavKey.MainHub
): SnapshotStateList<CalcNavKey> = rememberSaveable(saver = CalcBackStackSaver) {
    mutableStateListOf(initialKey)
}

/**
 * Adaptive Navigation 3 display host:
 * - Compact widths (< 600.dp): single pane with [PredictiveBackHandler] gesture preview.
 * - Expanded widths (>= 600.dp): persistent List-Detail two-pane scene strategy.
 */
@Composable
fun AppNavigation() {
    val backStack = rememberCalcBackStack()
    var selectedMainTab by rememberSaveable { mutableIntStateOf(0) }
    val tokens = LocalCalcHubTokens.current

    fun openCalculator(route: String) {
        val currentTop = backStack.lastOrNull()
        if (currentTop is CalcNavKey.CalculatorDetail && currentTop.route == route) return
        if (currentTop is CalcNavKey.CalculatorDetail) {
            backStack[backStack.lastIndex] = CalcNavKey.CalculatorDetail(route)
        } else {
            backStack.add(CalcNavKey.CalculatorDetail(route))
        }
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(tokens.appBackground)
    ) {
        val isExpanded = maxWidth >= 600.dp

        if (isExpanded) {
            val activeRoute = (backStack.lastOrNull() as? CalcNavKey.CalculatorDetail)?.route ?: "sip"

            PredictiveBackHandler(enabled = backStack.size > 1) { progress: Flow<BackEventCompat> ->
                try {
                    progress.collect { }
                    navigateBack()
                } catch (_: CancellationException) {
                    // Gesture cancelled by user; keep detail selection intact.
                }
            }

            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .widthIn(min = 340.dp, max = 440.dp)
                        .weight(0.42f)
                        .fillMaxHeight()
                ) {
                    MainScreen(
                        onCalculatorClick = ::openCalculator,
                        selectedTab = selectedMainTab,
                        onTabSelected = { selectedMainTab = it },
                        isExpandedLayout = true,
                        activeDetailRoute = activeRoute
                    )
                }

                VerticalDivider(color = tokens.cardBorder)

                Box(
                    modifier = Modifier
                        .weight(0.58f)
                        .fillMaxHeight()
                ) {
                    CalculatorRouteContent(
                        route = activeRoute,
                        onBackClick = ::navigateBack
                    )
                }
            }
        } else {
            NavDisplay(
                backStack = backStack,
                onPop = ::navigateBack,
                mainContent = {
                    MainScreen(
                        onCalculatorClick = ::openCalculator,
                        selectedTab = selectedMainTab,
                        onTabSelected = { selectedMainTab = it },
                        isExpandedLayout = false,
                        activeDetailRoute = null
                    )
                },
                detailContent = { route ->
                    CalculatorRouteContent(
                        route = route,
                        onBackClick = ::navigateBack
                    )
                }
            )
        }
    }
}

@Composable
private fun NavDisplay(
    backStack: SnapshotStateList<CalcNavKey>,
    onPop: () -> Unit,
    mainContent: @Composable () -> Unit,
    detailContent: @Composable (String) -> Unit
) {
    var predictiveProgress by remember { mutableFloatStateOf(0f) }

    PredictiveBackHandler(enabled = backStack.size > 1) { progress: Flow<BackEventCompat> ->
        try {
            progress.collect { event ->
                predictiveProgress = event.progress.coerceIn(0f, 1f)
            }
            predictiveProgress = 0f
            onPop()
        } catch (_: CancellationException) {
            predictiveProgress = 0f
        }
    }

    val saveableStateHolder = rememberSaveableStateHolder()
    val currentKey = backStack.lastOrNull() ?: CalcNavKey.MainHub

    AnimatedContent(
        targetState = currentKey,
        transitionSpec = {
            if (targetState is CalcNavKey.CalculatorDetail) {
                (slideInHorizontally(tween(260)) { it / 4 } + fadeIn(tween(220))) togetherWith
                    (fadeOut(tween(180)))
            } else {
                fadeIn(tween(220)) togetherWith
                    (slideOutHorizontally(tween(240)) { it / 4 } + fadeOut(tween(180)))
            }
        },
        label = "nav3Display"
    ) { key ->
        when (key) {
            is CalcNavKey.MainHub -> saveableStateHolder.SaveableStateProvider("main_hub") {
                mainContent()
            }
            is CalcNavKey.CalculatorDetail -> {
                val corner = (predictiveProgress * 28f).dp
                val scale = 1f - predictiveProgress * 0.06f
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = predictiveProgress * 36f
                        }
                        .clip(RoundedCornerShape(corner))
                ) {
                    detailContent(key.route)
                }
            }
        }
    }
}

@Composable
private fun CalculatorRouteContent(
    route: String,
    onBackClick: () -> Unit
) {
    when (route) {
        "sip" -> SipCalculatorScreen(onBackClick)
        "emi" -> EmiCalculatorScreen(onBackClick)
        "fd" -> FdCalculatorScreen(onBackClick)
        "ppf" -> PpfCalculatorScreen(onBackClick)
        "lumpsum" -> LumpsumCalculatorScreen(onBackClick)
        "swp" -> SwpCalculatorScreen(onBackClick)
        "rd" -> RdCalculatorScreen(onBackClick)
        "nsc" -> NscCalculatorScreen(onBackClick)
        "epf" -> EpfCalculatorScreen(onBackClick)
        "ssy" -> SsyCalculatorScreen(onBackClick)
        "nps" -> NpsCalculatorScreen(onBackClick)
        "mf_returns" -> MfReturnsCalculatorScreen(onBackClick)
        "compound_interest" -> CompoundInterestCalculatorScreen(onBackClick)
        "simple_interest" -> SimpleInterestCalculatorScreen(onBackClick)
        "step_up_sip" -> StepUpSipCalculatorScreen(onBackClick)
        "retirement" -> RetirementCalculatorScreen(onBackClick)
        "income_tax" -> IncomeTaxCalculatorScreen(onBackClick)
        "hra" -> HraCalculatorScreen(onBackClick)
        "gratuity" -> GratuityCalculatorScreen(onBackClick)
        "apy" -> ApyCalculatorScreen(onBackClick)
        "cagr" -> CagrCalculatorScreen(onBackClick)
        "gst" -> GstCalculatorScreen(onBackClick)
        "tds" -> TdsCalculatorScreen(onBackClick)
        "salary" -> SalaryCalculatorScreen(onBackClick)
        "inflation" -> InflationCalculatorScreen(onBackClick)
        "post_office_mis" -> PostOfficeMisCalculatorScreen(onBackClick)
        "scss" -> ScssCalculatorScreen(onBackClick)
        "stock_average" -> StockAverageCalculatorScreen(onBackClick)
        "xirr" -> XirrCalculatorScreen(onBackClick)
        "brokerage" -> BrokerageCalculatorScreen(onBackClick)
        "margin" -> MarginCalculatorScreen(onBackClick)
        "flat_vs_reducing" -> FlatVsReducingCalculatorScreen(onBackClick)
        "home_loan_emi" -> HomeLoanEmiCalculatorScreen(onBackClick)
        "car_loan_emi" -> CarLoanEmiCalculatorScreen(onBackClick)
        else -> SipCalculatorScreen(onBackClick)
    }
}
