/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.createSaving

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.mifos.core.base.designsystem.component.KptDoubleButton
import org.mifos.core.base.designsystem.component.KptSuccessDialog
import org.mifos.core.base.designsystem.theme.KptTheme
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.effects.EventsEffect
import org.mifos.core.base.ui.screen.DefaultLoadingContent
import org.mifos.core.base.ui.screen.ScreenStateLoading
import org.mifos.core.base.ui.submit.MutationScreenContent
import org.mifos.core.designsystem.component.KptCheckboxToggle
import org.mifos.core.designsystem.component.KptDatePickerDialog
import org.mifos.core.designsystem.component.KptHeader
import org.mifos.core.designsystem.component.KptHeaderBackButton
import org.mifos.core.designsystem.component.KptHeaderTitle
import org.mifos.core.designsystem.component.KptKeyValueCard
import org.mifos.core.designsystem.component.KptKeyValueRow
import org.mifos.core.designsystem.icon.AppIcons
import org.mifos.core.ui.input.KptDropdownTextField
import org.mifos.core.ui.input.KptTextField
import org.mifos.core.ui.scaffold.KptScaffold
import org.mifos.feature.saving.generated.resources.Res
import org.mifos.feature.saving.generated.resources.feature_saving_add
import org.mifos.feature.saving.generated.resources.feature_saving_add_charge
import org.mifos.feature.saving.generated.resources.feature_saving_allow_overdraft
import org.mifos.feature.saving.generated.resources.feature_saving_back
import org.mifos.feature.saving.generated.resources.feature_saving_cancel
import org.mifos.feature.saving.generated.resources.feature_saving_charge_amount
import org.mifos.feature.saving.generated.resources.feature_saving_charge_amount_hint
import org.mifos.feature.saving.generated.resources.feature_saving_charge_date
import org.mifos.feature.saving.generated.resources.feature_saving_charge_name
import org.mifos.feature.saving.generated.resources.feature_saving_create_savings_title
import org.mifos.feature.saving.generated.resources.feature_saving_create_success_message
import org.mifos.feature.saving.generated.resources.feature_saving_create_success_title
import org.mifos.feature.saving.generated.resources.feature_saving_currency
import org.mifos.feature.saving.generated.resources.feature_saving_days_in_year_label
import org.mifos.feature.saving.generated.resources.feature_saving_decimal_places
import org.mifos.feature.saving.generated.resources.feature_saving_decimal_places_hint
import org.mifos.feature.saving.generated.resources.feature_saving_edit_charge
import org.mifos.feature.saving.generated.resources.feature_saving_external_id
import org.mifos.feature.saving.generated.resources.feature_saving_external_id_hint
import org.mifos.feature.saving.generated.resources.feature_saving_field_officer
import org.mifos.feature.saving.generated.resources.feature_saving_interest_calculation_label
import org.mifos.feature.saving.generated.resources.feature_saving_interest_compounding_period
import org.mifos.feature.saving.generated.resources.feature_saving_interest_posting_period
import org.mifos.feature.saving.generated.resources.feature_saving_lock_in_freq
import org.mifos.feature.saving.generated.resources.feature_saving_lock_in_freq_type
import org.mifos.feature.saving.generated.resources.feature_saving_mifos_save
import org.mifos.feature.saving.generated.resources.feature_saving_min_opening_balance
import org.mifos.feature.saving.generated.resources.feature_saving_min_opening_balance_hint
import org.mifos.feature.saving.generated.resources.feature_saving_next
import org.mifos.feature.saving.generated.resources.feature_saving_no_charges
import org.mifos.feature.saving.generated.resources.feature_saving_nominal_interest_rate
import org.mifos.feature.saving.generated.resources.feature_saving_nominal_interest_rate_hint
import org.mifos.feature.saving.generated.resources.feature_saving_ok
import org.mifos.feature.saving.generated.resources.feature_saving_savings_product_label
import org.mifos.feature.saving.generated.resources.feature_saving_select_charge
import org.mifos.feature.saving.generated.resources.feature_saving_select_currency
import org.mifos.feature.saving.generated.resources.feature_saving_select_days_in_year
import org.mifos.feature.saving.generated.resources.feature_saving_select_field_officer
import org.mifos.feature.saving.generated.resources.feature_saving_select_interest_calculation
import org.mifos.feature.saving.generated.resources.feature_saving_select_interest_compounding_period
import org.mifos.feature.saving.generated.resources.feature_saving_select_interest_posting_period
import org.mifos.feature.saving.generated.resources.feature_saving_select_lock_in_freq_type
import org.mifos.feature.saving.generated.resources.feature_saving_select_savings_product
import org.mifos.feature.saving.generated.resources.feature_saving_step_charges
import org.mifos.feature.saving.generated.resources.feature_saving_step_details
import org.mifos.feature.saving.generated.resources.feature_saving_step_preview
import org.mifos.feature.saving.generated.resources.feature_saving_step_terms
import org.mifos.feature.saving.generated.resources.feature_saving_submit_savings_application
import org.mifos.feature.saving.generated.resources.feature_saving_submitted_on_date
import org.mifos.feature.saving.generated.resources.feature_saving_submitted_on_date_hint

@Composable
fun CreateSavingScreen(
    onBackClick: () -> Unit,
    onBackWithUpdateData: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateSavingViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            CreateSavingEvent.NavigateBack -> onBackClick()
            CreateSavingEvent.Finish -> onBackWithUpdateData()
        }
    }

    if (state.showSuccessDialog) {
        KptSuccessDialog(
            title = stringResource(Res.string.feature_saving_create_success_title),
            message = stringResource(Res.string.feature_saving_create_success_message),
            buttonText = stringResource(Res.string.feature_saving_ok),
            onConfirm = { viewModel.trySendAction(CreateSavingAction.DismissSuccessDialog) },
        )
    }

    if (state.isSubmittedDatePickerVisible) {
        KptDatePickerDialog(
            onDateSelected = { millis ->
                viewModel.trySendAction(CreateSavingAction.OnSubmittedDateSelected(millis))
            },
            onDismiss = {
                viewModel.trySendAction(CreateSavingAction.OnSubmittedDatePickerToggle(false))
            },
            initialSelectedDateMillis = state.selectedSubmittedOnDateMillis,
        )
    }

    if (state.isChargeDatePickerVisible) {
        KptDatePickerDialog(
            onDateSelected = { millis ->
                viewModel.trySendAction(CreateSavingAction.OnChargeDateSelected(millis))
            },
            onDismiss = {
                viewModel.trySendAction(CreateSavingAction.OnChargeDateDatePickerToggle(false))
            },
            initialSelectedDateMillis = state.selectedChargeDateMillis,
        )
    }

    if (state.isAddChargeDialogVisible) {
        AddChargeDialog(
            state = state,
            onAction = viewModel::trySendAction,
        )
    }

    CreateSavingScreenContent(
        state = state,
        onAction = viewModel::trySendAction,
        modifier = modifier,
    )
}

@Composable
internal fun CreateSavingScreenContent(
    state: CreateSavingState,
    onAction: (CreateSavingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    KptScaffold(
        modifier = modifier,
        containerColor = KptTheme.colorScheme.primary,
        topBar = {
            KptHeader(
                navigationIcon = {
                    KptHeaderBackButton(onClick = { onAction(CreateSavingAction.OnBackClick) })
                },
                title = {
                    KptHeaderTitle(
                        title = stringResource(Res.string.feature_saving_create_savings_title),
                        subtitle = stringResource(Res.string.feature_saving_mifos_save),
                    )
                },
            )
        },
        bottomBar = {
            if (state.submitState is SubmitState.Idle || state.submitState is SubmitState.Submitting) {
                val leftText = if (state.currentStep == 0) {
                    stringResource(Res.string.feature_saving_cancel)
                } else {
                    stringResource(Res.string.feature_saving_back)
                }

                val rightText = if (state.currentStep == state.totalSteps - 1) {
                    stringResource(Res.string.feature_saving_submit_savings_application)
                } else {
                    stringResource(Res.string.feature_saving_next)
                }

                KptDoubleButton(
                    leftButtonText = leftText,
                    rightButtonText = rightText,
                    onLeftButtonClick = {
                        if (state.currentStep == 0) {
                            onAction(CreateSavingAction.OnBackClick)
                        } else {
                            onAction(CreateSavingAction.PreviousStep)
                        }
                    },
                    onRightButtonClick = {
                        if (state.currentStep == state.totalSteps - 1) {
                            onAction(CreateSavingAction.SubmitSavingsApplication)
                        } else {
                            onAction(CreateSavingAction.NextStep)
                        }
                    },
                    enabledRight = state.submitState !is SubmitState.Submitting,
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = KptTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topStart = KptTheme.spacing.lg,
                        topEnd = KptTheme.spacing.lg,
                    ),
                ),
        ) {
            MutationScreenContent(
                screenState = state.screenState,
                submitState = state.submitState,
                onRetry = { onAction(CreateSavingAction.Retry) },
                onSubmitted = {},
                loading = {
                    DefaultLoadingContent(
                        config = ScreenStateLoading.Spinner,
                    )
                },
                modifier = Modifier.fillMaxSize(),
            ) { _, _ ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = KptTheme.spacing.md, vertical = KptTheme.spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
                ) {
                    // Custom Stepper
                    StepProgressBar(
                        currentStep = state.currentStep,
                        steps = listOf(
                            stringResource(Res.string.feature_saving_step_details),
                            stringResource(Res.string.feature_saving_step_terms),
                            stringResource(Res.string.feature_saving_step_charges),
                            stringResource(Res.string.feature_saving_step_preview),
                        ),
                    )

                    // Step Layouts
                    when (state.currentStep) {
                        0 -> StepDetailsContent(state, onAction)
                        1 -> StepTermsContent(state, onAction)
                        2 -> StepChargesContent(state, onAction)
                        3 -> StepPreviewContent(state)
                    }
                }
            }
        }
    }
}

@Composable
fun StepProgressBar(
    currentStep: Int,
    steps: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = KptTheme.spacing.xs),
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(KptTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            steps.forEachIndexed { index, _ ->
                val isActive = index == currentStep
                val isCompleted = index < currentStep
                val barColor = when {
                    isCompleted -> KptTheme.colorScheme.primary
                    isActive -> KptTheme.colorScheme.primary
                    else -> KptTheme.colorScheme.surfaceVariant
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (isActive) 6.dp else 4.dp)
                        .clip(RoundedCornerShape(KptTheme.spacing.xs))
                        .background(barColor),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Step ${currentStep + 1} of ${steps.size}",
                style = KptTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = KptTheme.colorScheme.outline,
            )
            Text(
                text = steps.getOrNull(currentStep).orEmpty(),
                style = KptTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = KptTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun StepDetailsContent(
    state: CreateSavingState,
    onAction: (CreateSavingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val template = state.template ?: return

    val products = remember(template.productOptions) {
        template.productOptions.map { it.name.orEmpty() }
    }

    val officers = remember(template.fieldOfficerOptions) {
        template.fieldOfficerOptions.map { it.displayName.orEmpty() }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        modifier = modifier.fillMaxWidth(),
    ) {
        KptDropdownTextField(
            value = template.productOptions.getOrNull(state.savingsProductSelected)?.name.orEmpty(),
            onOptionSelected = { index, _ -> onAction(CreateSavingAction.OnProductNameChange(index)) },
            options = products,
            label = stringResource(Res.string.feature_saving_savings_product_label),
            placeholder = stringResource(Res.string.feature_saving_select_savings_product),
            errorText = state.savingProductError?.let { stringResource(it) },
            modifier = Modifier.fillMaxWidth(),
        )

        KptDropdownTextField(
            value = template.fieldOfficerOptions.getOrNull(state.fieldOfficerIndex)?.displayName.orEmpty(),
            onOptionSelected = { index, _ -> onAction(CreateSavingAction.OnFieldOfficerChange(index)) },
            options = officers,
            label = stringResource(Res.string.feature_saving_field_officer),
            placeholder = stringResource(Res.string.feature_saving_select_field_officer),
            modifier = Modifier.fillMaxWidth(),
        )

        KptTextField(
            value = state.externalId,
            onValueChange = { onAction(CreateSavingAction.OnExternalIdChange(it)) },
            label = stringResource(Res.string.feature_saving_external_id),
            placeholder = stringResource(Res.string.feature_saving_external_id_hint),
            modifier = Modifier.fillMaxWidth(),
        )

        KptTextField(
            value = state.submittedOnDate,
            onValueChange = {},
            readOnly = true,
            label = stringResource(Res.string.feature_saving_submitted_on_date),
            placeholder = stringResource(Res.string.feature_saving_submitted_on_date_hint),
            onCalenderClick = { onAction(CreateSavingAction.OnSubmittedDatePickerToggle(true)) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun StepTermsContent(
    state: CreateSavingState,
    onAction: (CreateSavingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val template = state.template ?: return

    val currencies = remember(template.currencyOptions) {
        template.currencyOptions.map { it.displayLabel.orEmpty() }
    }

    val compoundingPeriods = remember(template.interestCompoundingPeriodTypeOptions) {
        template.interestCompoundingPeriodTypeOptions.map { it.value.orEmpty() }
    }

    val postingPeriods = remember(template.interestPostingPeriodTypeOptions) {
        template.interestPostingPeriodTypeOptions.map { it.value.orEmpty() }
    }

    val calculationTypes = remember(template.interestCalculationTypeOptions) {
        template.interestCalculationTypeOptions.map { it.value.orEmpty() }
    }

    val daysInYears = remember(template.interestCalculationDaysInYearTypeOptions) {
        template.interestCalculationDaysInYearTypeOptions.map { it.value.orEmpty() }
    }

    val freqTypes = remember(template.lockinPeriodFrequencyTypeOptions) {
        template.lockinPeriodFrequencyTypeOptions.map { it.value.orEmpty() }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        modifier = modifier.fillMaxWidth(),
    ) {
        KptDropdownTextField(
            value = template.currencyOptions.getOrNull(state.currencyIndex)?.displayLabel.orEmpty()
                .ifBlank { state.currencyCode },
            onOptionSelected = { index, _ -> onAction(CreateSavingAction.OnCurrencyChange(index)) },
            options = currencies,
            label = stringResource(Res.string.feature_saving_currency),
            placeholder = stringResource(Res.string.feature_saving_select_currency),
            modifier = Modifier.fillMaxWidth(),
        )

        KptTextField(
            value = state.decimalPlaces,
            onValueChange = { onAction(CreateSavingAction.OnDecimalPlacesChange(it)) },
            label = stringResource(Res.string.feature_saving_decimal_places),
            placeholder = stringResource(Res.string.feature_saving_decimal_places_hint),
            errorText = state.decimalPlacesError?.let { stringResource(it) },
            modifier = Modifier.fillMaxWidth(),
        )

        KptTextField(
            value = state.nominalAnnualInterestRate,
            onValueChange = { onAction(CreateSavingAction.OnNominalAnnualInterestRateChange(it)) },
            label = stringResource(Res.string.feature_saving_nominal_interest_rate),
            placeholder = stringResource(Res.string.feature_saving_nominal_interest_rate_hint),
            errorText = state.nominalAnnualInterestRateError?.let { stringResource(it) },
            modifier = Modifier.fillMaxWidth(),
        )

        KptDropdownTextField(
            value = template.interestCompoundingPeriodTypeOptions
                .getOrNull(state.interestCompoundingPeriodIndex)?.value.orEmpty(),
            onOptionSelected = { index, _ -> onAction(CreateSavingAction.OnInterestCompoundingPeriodChange(index)) },
            options = compoundingPeriods,
            label = stringResource(Res.string.feature_saving_interest_compounding_period),
            placeholder = stringResource(Res.string.feature_saving_select_interest_compounding_period),
            modifier = Modifier.fillMaxWidth(),
        )

        KptDropdownTextField(
            value = template.interestPostingPeriodTypeOptions
                .getOrNull(state.interestPostingPeriodIndex)?.value.orEmpty(),
            onOptionSelected = { index, _ -> onAction(CreateSavingAction.OnInterestPostingPeriodChange(index)) },
            options = postingPeriods,
            label = stringResource(Res.string.feature_saving_interest_posting_period),
            placeholder = stringResource(Res.string.feature_saving_select_interest_posting_period),
            modifier = Modifier.fillMaxWidth(),
        )

        KptDropdownTextField(
            value = template.interestCalculationTypeOptions.getOrNull(state.interestCalculationIndex)?.value.orEmpty(),
            onOptionSelected = { index, _ -> onAction(CreateSavingAction.OnInterestCalculationChange(index)) },
            options = calculationTypes,
            label = stringResource(Res.string.feature_saving_interest_calculation_label),
            placeholder = stringResource(Res.string.feature_saving_select_interest_calculation),
            modifier = Modifier.fillMaxWidth(),
        )

        KptDropdownTextField(
            value = template.interestCalculationDaysInYearTypeOptions.getOrNull(state.daysInYearIndex)?.value.orEmpty(),
            onOptionSelected = { index, _ -> onAction(CreateSavingAction.OnDaysInYearChange(index)) },
            options = daysInYears,
            label = stringResource(Res.string.feature_saving_days_in_year_label),
            placeholder = stringResource(Res.string.feature_saving_select_days_in_year),
            modifier = Modifier.fillMaxWidth(),
        )

        KptCheckboxToggle(
            checked = state.isCheckedOverdraftAllowed,
            onCheckedChange = { onAction(CreateSavingAction.OnOverdraftAllowedChange(it)) },
            text = stringResource(Res.string.feature_saving_allow_overdraft),
            modifier = Modifier.fillMaxWidth(),
        )

        KptTextField(
            value = state.minimumOpeningBalance,
            onValueChange = { onAction(CreateSavingAction.OnMinimumOpeningBalanceChange(it)) },
            label = stringResource(Res.string.feature_saving_min_opening_balance),
            placeholder = stringResource(Res.string.feature_saving_min_opening_balance_hint),
            modifier = Modifier.fillMaxWidth(),
        )

        KptTextField(
            value = state.frequency,
            onValueChange = { onAction(CreateSavingAction.OnFrequencyChange(it)) },
            label = stringResource(Res.string.feature_saving_lock_in_freq),
            placeholder = "e.g. 6",
            modifier = Modifier.fillMaxWidth(),
        )

        KptDropdownTextField(
            value = template.lockinPeriodFrequencyTypeOptions.getOrNull(state.freqTypeIndex)?.value.orEmpty(),
            onOptionSelected = { index, _ -> onAction(CreateSavingAction.OnFreqTypeChange(index)) },
            options = freqTypes,
            label = stringResource(Res.string.feature_saving_lock_in_freq_type),
            placeholder = stringResource(Res.string.feature_saving_select_lock_in_freq_type),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun StepChargesContent(
    state: CreateSavingState,
    onAction: (CreateSavingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Charges Listing",
                style = KptTheme.typography.titleMedium,
                color = KptTheme.colorScheme.onSurface,
            )

            TextButton(onClick = { onAction(CreateSavingAction.OnAddChargeClick) }) {
                Icon(
                    imageVector = AppIcons.AddDefault,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = "Add Charge",
                    style = KptTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = KptTheme.spacing.xs),
                )
            }
        }

        if (state.addedCharges.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = KptTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                ),
            ) {
                Text(
                    text = stringResource(Res.string.feature_saving_no_charges),
                    style = KptTheme.typography.bodyMedium,
                    color = KptTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(KptTheme.spacing.lg),
                )
            }
        } else {
            state.addedCharges.forEachIndexed { index, charge ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = KptTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = KptTheme.colorScheme.surfaceContainerHigh),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(KptTheme.spacing.md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = charge.name.orEmpty(),
                                style = KptTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = KptTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "Amount: ${charge.amount} | Due: ${charge.date}",
                                style = KptTheme.typography.bodySmall,
                                color = KptTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Row {
                            IconButton(onClick = { onAction(CreateSavingAction.OnEditChargeClick(index)) }) {
                                Icon(
                                    imageVector = AppIcons.Edit,
                                    contentDescription = "Edit Charge",
                                    tint = KptTheme.colorScheme.primary,
                                )
                            }
                            IconButton(onClick = { onAction(CreateSavingAction.OnDeleteChargeClick(index)) }) {
                                Icon(
                                    imageVector = AppIcons.Delete,
                                    contentDescription = "Delete Charge",
                                    tint = KptTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StepPreviewContent(
    state: CreateSavingState,
    modifier: Modifier = Modifier,
) {
    val template = state.template ?: return

    val prodName = template.productOptions.getOrNull(state.savingsProductSelected)?.name.orEmpty()

    val compounding = template.interestCompoundingPeriodTypeOptions
        .getOrNull(state.interestCompoundingPeriodIndex)?.value.orEmpty()
    val posting = template.interestPostingPeriodTypeOptions
        .getOrNull(state.interestPostingPeriodIndex)?.value.orEmpty()
    val calc = template.interestCalculationTypeOptions
        .getOrNull(state.interestCalculationIndex)?.value.orEmpty()
    val days = template.interestCalculationDaysInYearTypeOptions
        .getOrNull(state.daysInYearIndex)?.value.orEmpty()

    val detailsMap = remember(prodName, state.externalId, state.submittedOnDate) {
        buildMap {
            put("Product", prodName)
            if (state.externalId.isNotBlank()) put("External ID", state.externalId)
            put("Submitted Date", state.submittedOnDate)
        }
    }

    val termsMap = remember(
        state.currencyCode,
        state.decimalPlaces,
        state.nominalAnnualInterestRate,
        compounding,
        posting,
        calc,
        days,
        state.isCheckedOverdraftAllowed,
        state.minimumOpeningBalance,
    ) {
        buildMap {
            put("Currency", state.currencyCode)
            put("Decimal Places", state.decimalPlaces)
            put("Interest Rate", "${state.nominalAnnualInterestRate}%")
            put("Compounding Period", compounding)
            put("Posting Period", posting)
            put("Calculation Type", calc)
            put("Days In Year", days)
            put("Overdraft Allowed", if (state.isCheckedOverdraftAllowed) "Yes" else "No")
            if (state.minimumOpeningBalance.isNotBlank()) {
                put("Min Required Opening Balance", state.minimumOpeningBalance)
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.md),
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Details Overview",
            style = KptTheme.typography.titleMedium,
            color = KptTheme.colorScheme.primary,
            modifier = Modifier.padding(top = KptTheme.spacing.sm),
        )
        KptKeyValueCard(items = detailsMap)

        Text(
            text = "Terms Overview",
            style = KptTheme.typography.titleMedium,
            color = KptTheme.colorScheme.primary,
        )
        KptKeyValueCard(items = termsMap)

        if (state.addedCharges.isNotEmpty()) {
            Text(
                text = "Charges Summary",
                style = KptTheme.typography.titleMedium,
                color = KptTheme.colorScheme.primary,
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = KptTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = KptTheme.colorScheme.inverseOnSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column {
                    state.addedCharges.forEachIndexed { index, charge ->
                        KptKeyValueRow(
                            key = charge.name.orEmpty(),
                            value = "${charge.amount} (${charge.date})",
                            showDivider = index < state.addedCharges.lastIndex,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddChargeDialog(
    state: CreateSavingState,
    onAction: (CreateSavingAction) -> Unit,
) {
    val template = state.template ?: return

    val title = if (state.editingChargeIndex == -1) {
        stringResource(Res.string.feature_saving_add_charge)
    } else {
        stringResource(Res.string.feature_saving_edit_charge)
    }

    AlertDialog(
        onDismissRequest = { onAction(CreateSavingAction.OnDismissDialog) },
        title = {
            Text(
                text = title,
                style = KptTheme.typography.titleLarge,
                color = KptTheme.colorScheme.onSurface,
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                KptDropdownTextField(
                    value = template.chargeOptions.getOrNull(state.chooseChargeIndex)?.name.orEmpty(),
                    onOptionSelected = { index, _ -> onAction(CreateSavingAction.OnChooseChargeChange(index)) },
                    options = template.chargeOptions.map { it.name.orEmpty() },
                    label = stringResource(Res.string.feature_saving_charge_name),
                    placeholder = stringResource(Res.string.feature_saving_select_charge),
                    modifier = Modifier.fillMaxWidth(),
                )

                KptTextField(
                    value = state.chargeAmount,
                    onValueChange = { onAction(CreateSavingAction.OnChargeAmountChange(it)) },
                    label = stringResource(Res.string.feature_saving_charge_amount),
                    placeholder = stringResource(Res.string.feature_saving_charge_amount_hint),
                    errorText = state.chargeAmountError?.let { stringResource(it) },
                    modifier = Modifier.fillMaxWidth(),
                )

                KptTextField(
                    value = state.chargeDate,
                    onValueChange = {},
                    readOnly = true,
                    label = stringResource(Res.string.feature_saving_charge_date),
                    onCalenderClick = { onAction(CreateSavingAction.OnChargeDateDatePickerToggle(true)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onAction(CreateSavingAction.OnAddChargeToList) }) {
                Text(
                    text = stringResource(Res.string.feature_saving_add),
                    style = KptTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = KptTheme.colorScheme.primary,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onAction(CreateSavingAction.OnDismissDialog) }) {
                Text(
                    text = stringResource(Res.string.feature_saving_cancel),
                    style = KptTheme.typography.labelLarge,
                    color = KptTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}
