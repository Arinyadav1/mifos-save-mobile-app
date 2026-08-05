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

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import org.mifos.core.base.store.screen.DataFreshness
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.common.Constants
import org.mifos.core.common.formatDateFromLong
import org.mifos.core.common.getCurrentEpochMillis
import org.mifos.core.data.client.ClientRepository
import org.mifos.core.data.savings.SavingsRepository
import org.mifos.core.model.client.ClientTemplate
import org.mifos.core.model.savings.ChargeRequest
import org.mifos.core.model.savings.CreateSavingAccountRequest
import org.mifos.core.model.savings.SavingsAccountTemplate
import org.mifos.feature.saving.generated.resources.Res
import org.mifos.feature.saving.generated.resources.feature_saving_error_decimal_places
import org.mifos.feature.saving.generated.resources.feature_saving_error_invalid_amount
import org.mifos.feature.saving.generated.resources.feature_saving_error_invalid_interest_rate
import org.mifos.feature.saving.generated.resources.feature_saving_error_select_product

class CreateSavingViewModel(
    savedStateHandle: SavedStateHandle,
    private val savingsRepository: SavingsRepository,
    private val clientRepository: ClientRepository,
) : BaseViewModel<CreateSavingState, CreateSavingEvent, CreateSavingAction>(
    CreateSavingState(
        groupId = savedStateHandle.toRoute<CreateSavingRoute>().groupId,
    ),
) {

    init {
        loadTemplate()
    }

    private fun loadTemplate() {
        mutableStateFlow.update { it.copy(screenState = ScreenState.Loading) }
        viewModelScope.launch {
            combine(
                savingsRepository.getSavingsAccountTemplate(),
                clientRepository.getClientTemplate(),
            ) { savingsState, clientState ->
                combineStates(savingsState, clientState)
            }.collect { screenState ->
                when (screenState) {
                    is ScreenState.Content -> {
                        val mergedTemplate = mergeTemplates(screenState.data, state.template)
                        updateStateWithTemplate(mergedTemplate)
                    }
                    else -> {
                        mutableStateFlow.update {
                            it.copy(
                                screenState = when (screenState) {
                                    is ScreenState.Error -> ScreenState.Error(screenState.error)
                                    is ScreenState.NoNetwork -> ScreenState.NoNetwork()
                                    is ScreenState.Unauthenticated -> ScreenState.Unauthenticated
                                    is ScreenState.Empty -> ScreenState.Empty
                                    else -> ScreenState.Loading
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    private fun combineStates(
        savingsState: ScreenState<SavingsAccountTemplate>,
        clientState: ScreenState<ClientTemplate>,
    ): ScreenState<SavingsAccountTemplate> {
        return when {
            savingsState is ScreenState.Error -> ScreenState.Error(savingsState.error)
            clientState is ScreenState.Error -> ScreenState.Error(clientState.error)
            savingsState is ScreenState.NoNetwork ||
                clientState is ScreenState.NoNetwork -> ScreenState.NoNetwork()
            savingsState is ScreenState.Unauthenticated ||
                clientState is ScreenState.Unauthenticated -> ScreenState.Unauthenticated
            savingsState is ScreenState.Empty || clientState is ScreenState.Empty -> ScreenState.Empty
            savingsState is ScreenState.Loading || clientState is ScreenState.Loading -> ScreenState.Loading
            savingsState is ScreenState.Content && clientState is ScreenState.Content -> {
                val mergedTemplate = savingsState.data.copy(
                    productOptions = clientState.data.savingProductOptions,
                    fieldOfficerOptions = clientState.data.staffOptions,
                )
                ScreenState.Content(mergedTemplate, DataFreshness.FRESH)
            }
            else -> ScreenState.Loading
        }
    }

    private fun mergeTemplates(
        template: SavingsAccountTemplate,
        initialTemplate: SavingsAccountTemplate?,
    ): SavingsAccountTemplate {
        if (initialTemplate == null) return template
        return template.copy(
            productOptions = template.productOptions.ifEmpty {
                initialTemplate.productOptions
            },
            fieldOfficerOptions = template.fieldOfficerOptions.ifEmpty {
                initialTemplate.fieldOfficerOptions
            },
            currencyOptions = template.currencyOptions.ifEmpty {
                initialTemplate.currencyOptions
            },
            interestCompoundingPeriodTypeOptions = template
                .interestCompoundingPeriodTypeOptions.ifEmpty {
                    initialTemplate.interestCompoundingPeriodTypeOptions
                },
            interestPostingPeriodTypeOptions = template
                .interestPostingPeriodTypeOptions.ifEmpty {
                    initialTemplate.interestPostingPeriodTypeOptions
                },
            interestCalculationTypeOptions = template
                .interestCalculationTypeOptions.ifEmpty {
                    initialTemplate.interestCalculationTypeOptions
                },
            interestCalculationDaysInYearTypeOptions = template
                .interestCalculationDaysInYearTypeOptions.ifEmpty {
                    initialTemplate.interestCalculationDaysInYearTypeOptions
                },
            lockinPeriodFrequencyTypeOptions = template
                .lockinPeriodFrequencyTypeOptions.ifEmpty {
                    initialTemplate.lockinPeriodFrequencyTypeOptions
                },
            withdrawalFeeTypeOptions = template
                .withdrawalFeeTypeOptions.ifEmpty {
                    initialTemplate.withdrawalFeeTypeOptions
                },
            paymentTypeOptions = template.paymentTypeOptions.ifEmpty {
                initialTemplate.paymentTypeOptions
            },
            chargeOptions = template.chargeOptions.ifEmpty {
                initialTemplate.chargeOptions
            },
            accountingRuleOptions = template.accountingRuleOptions.ifEmpty {
                initialTemplate.accountingRuleOptions
            },
        )
    }

    private fun updateStateWithTemplate(mergedTemplate: SavingsAccountTemplate) {
        mutableStateFlow.update {
            it.copy(
                screenState = ScreenState.Content(Unit, DataFreshness.FRESH),
                template = mergedTemplate,
                currencyCode = mergedTemplate.currency?.code.orEmpty(),
                decimalPlaces = mergedTemplate.currency?.decimalPlaces?.toString().orEmpty(),
                nominalAnnualInterestRate = mergedTemplate
                    .nominalAnnualInterestRate?.toString().orEmpty(),
                minimumOpeningBalance = mergedTemplate.minRequiredOpeningBalance?.toString().orEmpty(),
                frequency = mergedTemplate.lockinPeriodFrequency?.toString().orEmpty(),
                freqTypeIndex = mergedTemplate.lockinPeriodFrequencyTypeOptions
                    .indexOfFirst { opt ->
                        opt.id == mergedTemplate.lockinPeriodFrequencyType?.id
                    },
                currencyIndex = mergedTemplate.currencyOptions
                    .indexOfFirst { opt -> opt.code == mergedTemplate.currency?.code },
                interestCompoundingPeriodIndex = mergedTemplate
                    .interestCompoundingPeriodTypeOptions.indexOfFirst { opt ->
                        opt.id == mergedTemplate.interestCompoundingPeriodType?.id
                    },
                interestPostingPeriodIndex = mergedTemplate
                    .interestPostingPeriodTypeOptions.indexOfFirst { opt ->
                        opt.id == mergedTemplate.interestPostingPeriodType?.id
                    },
                interestCalculationIndex = mergedTemplate
                    .interestCalculationTypeOptions.indexOfFirst { opt ->
                        opt.id == mergedTemplate.interestCalculationType?.id
                    },
                daysInYearIndex = mergedTemplate
                    .interestCalculationDaysInYearTypeOptions.indexOfFirst { opt ->
                        opt.id == mergedTemplate.interestCalculationDaysInYearType?.id
                    },
            )
        }
    }

    override fun handleAction(action: CreateSavingAction) {
        when {
            handleNavigationOrSubmission(action) -> Unit
            handleDetailsAction(action) -> Unit
            handleTermsAction(action) -> Unit
            else -> handleChargesAction(action)
        }
    }

    private fun handleNavigationOrSubmission(action: CreateSavingAction): Boolean {
        when (action) {
            CreateSavingAction.OnBackClick -> sendEvent(CreateSavingEvent.NavigateBack)
            CreateSavingAction.NextStep -> handleNextStep()
            CreateSavingAction.PreviousStep -> handlePreviousStep()
            CreateSavingAction.SubmitSavingsApplication -> submitSavingsApplication()
            CreateSavingAction.DismissSuccessDialog -> {
                mutableStateFlow.update { it.copy(showSuccessDialog = false) }
                sendEvent(CreateSavingEvent.Finish)
            }
            CreateSavingAction.Retry -> {
                mutableStateFlow.update { it.copy(submitState = SubmitState.Idle) }
                loadTemplate()
            }
            else -> return false
        }
        return true
    }

    private fun handleDetailsAction(action: CreateSavingAction): Boolean {
        when (action) {
            is CreateSavingAction.OnProductNameChange -> {
                mutableStateFlow.update {
                    it.copy(
                        savingsProductSelected = action.index,
                        savingProductError = null,
                    )
                }
            }
            is CreateSavingAction.OnFieldOfficerChange -> {
                mutableStateFlow.update { it.copy(fieldOfficerIndex = action.index) }
            }
            is CreateSavingAction.OnExternalIdChange -> {
                mutableStateFlow.update { it.copy(externalId = action.value) }
            }
            is CreateSavingAction.OnSubmittedDatePickerToggle -> {
                mutableStateFlow.update { it.copy(isSubmittedDatePickerVisible = action.visible) }
            }
            is CreateSavingAction.OnSubmittedDateSelected -> {
                handleSubmittedDateSelected(action.millis)
            }
            else -> return false
        }
        return true
    }

    private fun handleTermsAction(action: CreateSavingAction): Boolean {
        return handleTermsBasicAction(action) || handleTermsAdvanceAction(action)
    }

    private fun handleTermsBasicAction(action: CreateSavingAction): Boolean {
        when (action) {
            is CreateSavingAction.OnCurrencyChange -> {
                val code = state.template?.currencyOptions?.getOrNull(action.index)?.code.orEmpty()
                mutableStateFlow.update {
                    it.copy(
                        currencyIndex = action.index,
                        currencyCode = code,
                    )
                }
            }
            is CreateSavingAction.OnDecimalPlacesChange -> {
                mutableStateFlow.update {
                    it.copy(
                        decimalPlaces = action.value,
                        decimalPlacesError = null,
                    )
                }
            }
            is CreateSavingAction.OnNominalAnnualInterestRateChange -> {
                mutableStateFlow.update {
                    it.copy(
                        nominalAnnualInterestRate = action.value,
                        nominalAnnualInterestRateError = null,
                    )
                }
            }
            is CreateSavingAction.OnInterestCompoundingPeriodChange -> {
                mutableStateFlow.update { it.copy(interestCompoundingPeriodIndex = action.index) }
            }
            is CreateSavingAction.OnInterestPostingPeriodChange -> {
                mutableStateFlow.update { it.copy(interestPostingPeriodIndex = action.index) }
            }
            is CreateSavingAction.OnInterestCalculationChange -> {
                mutableStateFlow.update { it.copy(interestCalculationIndex = action.index) }
            }
            is CreateSavingAction.OnDaysInYearChange -> {
                mutableStateFlow.update { it.copy(daysInYearIndex = action.index) }
            }
            else -> return false
        }
        return true
    }

    private fun handleTermsAdvanceAction(action: CreateSavingAction): Boolean {
        when (action) {
            is CreateSavingAction.OnOverdraftAllowedChange -> {
                mutableStateFlow.update { it.copy(isCheckedOverdraftAllowed = action.allowed) }
            }
            is CreateSavingAction.OnMinimumOpeningBalanceChange -> {
                mutableStateFlow.update { it.copy(minimumOpeningBalance = action.value) }
            }
            is CreateSavingAction.OnMinimumBalanceChange -> {
                mutableStateFlow.update { it.copy(isCheckedMinimumBalance = action.allowed) }
            }
            is CreateSavingAction.OnMonthlyMinimumBalanceChange -> {
                mutableStateFlow.update { it.copy(monthlyMinimumBalance = action.value) }
            }
            is CreateSavingAction.OnFrequencyChange -> {
                mutableStateFlow.update { it.copy(frequency = action.value) }
            }
            is CreateSavingAction.OnFreqTypeChange -> {
                mutableStateFlow.update { it.copy(freqTypeIndex = action.index) }
            }
            else -> return false
        }
        return true
    }

    private fun handleChargesAction(action: CreateSavingAction): Boolean {
        when (action) {
            CreateSavingAction.OnAddChargeClick -> {
                mutableStateFlow.update {
                    it.copy(
                        isAddChargeDialogVisible = true,
                        editingChargeIndex = -1,
                        chooseChargeIndex = -1,
                        chargeAmount = "",
                        chargeDate = formatDateFromLong(getCurrentEpochMillis()),
                        selectedChargeDateMillis = getCurrentEpochMillis(),
                        chargeAmountError = null,
                    )
                }
            }
            is CreateSavingAction.OnEditChargeClick -> handleEditChargeDialog(action.index)
            is CreateSavingAction.OnDeleteChargeClick -> handleDeleteCharge(action.index)
            is CreateSavingAction.OnChooseChargeChange -> {
                val charge = state.template?.chargeOptions?.getOrNull(action.index)
                mutableStateFlow.update {
                    it.copy(
                        chooseChargeIndex = action.index,
                        chargeAmount = charge?.amount?.toString().orEmpty(),
                    )
                }
            }
            is CreateSavingAction.OnChargeAmountChange -> {
                mutableStateFlow.update {
                    it.copy(
                        chargeAmount = action.value,
                        chargeAmountError = null,
                    )
                }
            }
            is CreateSavingAction.OnChargeDateDatePickerToggle -> {
                mutableStateFlow.update { it.copy(isChargeDatePickerVisible = action.visible) }
            }
            is CreateSavingAction.OnChargeDateSelected -> handleChargeDateSelected(action.millis)
            CreateSavingAction.OnAddChargeToList -> handleAddOrEditChargeToList()
            CreateSavingAction.OnDismissDialog -> {
                mutableStateFlow.update { it.copy(isAddChargeDialogVisible = false) }
            }
            else -> return false
        }
        return true
    }

    private fun handleNextStep() {
        val current = state.currentStep
        if (current == 0) {
            val isSavingProductInvalid = state.savingsProductSelected == -1
            if (isSavingProductInvalid) {
                mutableStateFlow.update { it.copy(savingProductError = Res.string.feature_saving_error_select_product) }
            } else {
                mutableStateFlow.update { it.copy(savingProductError = null, currentStep = 1) }
            }
        } else if (current == 1) {
            val decimalPlaces = state.decimalPlaces.toIntOrNull()
            val isDecimalInvalid = state.decimalPlaces.isNotBlank() &&
                (decimalPlaces == null || decimalPlaces < 0 || decimalPlaces > 6)
            val nominalInterest = state.nominalAnnualInterestRate.toDoubleOrNull()
            val isNominalInterestInvalid = state.nominalAnnualInterestRate.isNotBlank() &&
                (nominalInterest == null || nominalInterest < 0)

            if (isDecimalInvalid || isNominalInterestInvalid) {
                mutableStateFlow.update {
                    it.copy(
                        decimalPlacesError = if (isDecimalInvalid) {
                            Res.string.feature_saving_error_decimal_places
                        } else {
                            null
                        },
                        nominalAnnualInterestRateError = if (isNominalInterestInvalid) {
                            Res.string.feature_saving_error_invalid_interest_rate
                        } else {
                            null
                        },
                    )
                }
            } else {
                mutableStateFlow.update {
                    it.copy(
                        decimalPlacesError = null,
                        nominalAnnualInterestRateError = null,
                        currentStep = 2,
                    )
                }
            }
        } else if (current < state.totalSteps - 1) {
            mutableStateFlow.update { it.copy(currentStep = current + 1) }
        }
    }

    private fun handlePreviousStep() {
        val current = state.currentStep
        if (current > 0) {
            mutableStateFlow.update { it.copy(currentStep = current - 1) }
        }
    }

    private fun handleSubmittedDateSelected(millis: Long?) {
        if (millis != null) {
            val formattedDate = formatDateFromLong(millis)
            mutableStateFlow.update {
                it.copy(
                    selectedSubmittedOnDateMillis = millis,
                    submittedOnDate = formattedDate,
                    isSubmittedDatePickerVisible = false,
                )
            }
        } else {
            mutableStateFlow.update { it.copy(isSubmittedDatePickerVisible = false) }
        }
    }

    private fun handleChargeDateSelected(millis: Long?) {
        if (millis != null) {
            val formattedDate = formatDateFromLong(millis)
            mutableStateFlow.update {
                it.copy(
                    selectedChargeDateMillis = millis,
                    chargeDate = formattedDate,
                    isChargeDatePickerVisible = false,
                )
            }
        } else {
            mutableStateFlow.update { it.copy(isChargeDatePickerVisible = false) }
        }
    }

    private fun handleEditChargeDialog(index: Int) {
        val selectedEditCharge = state.addedCharges[index]
        val chooseChargeIndex =
            state.template?.chargeOptions?.indexOfFirst { it.id == selectedEditCharge.id } ?: -1
        mutableStateFlow.update {
            it.copy(
                chargeAmount = selectedEditCharge.amount?.toString().orEmpty(),
                chargeDate = selectedEditCharge.date,
                chooseChargeIndex = chooseChargeIndex,
                isAddChargeDialogVisible = true,
                editingChargeIndex = index,
                chargeAmountError = null,
            )
        }
    }

    private fun handleDeleteCharge(index: Int) {
        val newCharges = state.addedCharges.toMutableList().apply {
            removeAt(index)
        }
        mutableStateFlow.update {
            it.copy(addedCharges = newCharges)
        }
    }

    private fun handleAddOrEditChargeToList() {
        val selectedIndex = state.chooseChargeIndex
        val selectedCharge = state.template?.chargeOptions?.getOrNull(selectedIndex)
        val amount = state.chargeAmount.toDoubleOrNull()

        if (selectedCharge == null) {
            return
        }

        if (amount == null || amount < 0) {
            mutableStateFlow.update { it.copy(chargeAmountError = Res.string.feature_saving_error_invalid_amount) }
            return
        }

        val newCharge = CreatedCharges(
            id = selectedCharge.id,
            name = selectedCharge.name,
            amount = amount,
            date = state.chargeDate,
            type = selectedCharge.chargeCalculationType?.value ?: "",
            collectedOn = selectedCharge.chargeTimeType?.value ?: "",
        )

        val currentList = state.addedCharges.toMutableList()
        val editIdx = state.editingChargeIndex
        if (editIdx != -1 && editIdx in currentList.indices) {
            currentList[editIdx] = newCharge
        } else {
            currentList.add(newCharge)
        }

        mutableStateFlow.update {
            it.copy(
                addedCharges = currentList,
                isAddChargeDialogVisible = false,
                editingChargeIndex = -1,
                chooseChargeIndex = -1,
                chargeAmount = "",
            )
        }
    }

    private fun submitSavingsApplication() {
        val prodId = state.template?.productOptions?.getOrNull(state.savingsProductSelected)?.id
        val officerId = state.template?.fieldOfficerOptions?.getOrNull(state.fieldOfficerIndex)?.id

        val request = CreateSavingAccountRequest(
            productId = prodId,
            groupId = state.groupId,
            fieldOfficerId = officerId,
            locale = Constants.LOCALE_EN,
            dateFormat = Constants.DATE_FORMAT_SHORT_MONTH,
            submittedOnDate = state.submittedOnDate,
            externalId = state.externalId.ifBlank { null },
            nominalAnnualInterestRate = state.nominalAnnualInterestRate,
            allowOverdraft = state.isCheckedOverdraftAllowed,
            enforceMinRequiredBalance = state.isCheckedMinimumBalance,
            minRequiredOpeningBalance = state.minimumOpeningBalance.ifBlank { null },
            minRequiredBalance = state.monthlyMinimumBalance.ifBlank { null },
            lockinPeriodFrequency = state.frequency.toIntOrNull(),
            lockinPeriodFrequencyType = state.template?.lockinPeriodFrequencyTypeOptions?.getOrNull(
                state.freqTypeIndex,
            )?.id?.toInt(),
            interestCompoundingPeriodType = state.template?.interestCompoundingPeriodTypeOptions?.getOrNull(
                state.interestCompoundingPeriodIndex,
            )?.id?.toInt(),
            interestCalculationType = state.template?.interestCalculationTypeOptions?.getOrNull(
                state.interestCalculationIndex,
            )?.id?.toInt(),
            interestCalculationDaysInYearType = state.template?.interestCalculationDaysInYearTypeOptions?.getOrNull(
                state.daysInYearIndex,
            )?.id?.toInt(),
            interestPostingPeriodType = state.template?.interestPostingPeriodTypeOptions?.getOrNull(
                state.interestPostingPeriodIndex,
            )?.id?.toInt(),
            charges = state.addedCharges.map { charges ->
                ChargeRequest(
                    chargeId = charges.id,
                    amount = charges.amount?.toString(),
                )
            },
        )

        mutableStateFlow.update { it.copy(submitState = SubmitState.Submitting()) }
        viewModelScope.launch {
            when (val result = savingsRepository.createSavingsAccount(request)) {
                is ScreenState.Content -> {
                    mutableStateFlow.update {
                        it.copy(
                            submitState = SubmitState.Submitted(Unit),
                            showSuccessDialog = true,
                        )
                    }
                }

                is ScreenState.Error -> {
                    mutableStateFlow.update {
                        it.copy(
                            submitState = SubmitState.Failed(result.error),
                            screenState = ScreenState.Error(result.error),
                        )
                    }
                }

                is ScreenState.NoNetwork -> {
                    mutableStateFlow.update {
                        it.copy(
                            submitState = SubmitState.Failed(),
                            screenState = ScreenState.NoNetwork(),
                        )
                    }
                }

                is ScreenState.Unauthenticated -> {
                    mutableStateFlow.update {
                        it.copy(
                            submitState = SubmitState.Failed(),
                            screenState = ScreenState.Unauthenticated,
                        )
                    }
                }

                is ScreenState.Empty -> {
                    mutableStateFlow.update {
                        it.copy(
                            submitState = SubmitState.Failed(),
                            screenState = ScreenState.Empty,
                        )
                    }
                }

                is ScreenState.Loading -> Unit
            }
        }
    }
}

data class CreateSavingState(
    val groupId: Long? = null,
    val currentStep: Int = 0,
    val totalSteps: Int = 4,
    val template: SavingsAccountTemplate? = null,
    val screenState: ScreenState<Unit> = ScreenState.Loading,
    val submitState: SubmitState<Unit> = SubmitState.Idle,
    val showSuccessDialog: Boolean = false,

    // Step 1: Details
    val savingsProductSelected: Int = -1,
    val fieldOfficerIndex: Int = -1,
    val externalId: String = "",
    val submittedOnDate: String = formatDateFromLong(getCurrentEpochMillis()),
    val selectedSubmittedOnDateMillis: Long? = getCurrentEpochMillis(),
    val isSubmittedDatePickerVisible: Boolean = false,
    val savingProductError: StringResource? = null,

    // Step 2: Terms
    val currencyIndex: Int = -1,
    val currencyCode: String = "",
    val decimalPlaces: String = "",
    val decimalPlacesError: StringResource? = null,
    val nominalAnnualInterestRate: String = "",
    val nominalAnnualInterestRateError: StringResource? = null,
    val interestCompoundingPeriodIndex: Int = -1,
    val interestPostingPeriodIndex: Int = -1,
    val interestCalculationIndex: Int = -1,
    val daysInYearIndex: Int = -1,
    val isCheckedOverdraftAllowed: Boolean = false,
    val minimumOpeningBalance: String = "",
    val isCheckedMinimumBalance: Boolean = false,
    val monthlyMinimumBalance: String = "",
    val frequency: String = "",
    val freqTypeIndex: Int = -1,

    // Step 3: Charges
    val chooseChargeIndex: Int = -1,
    val addedCharges: List<CreatedCharges> = emptyList(),
    val chargeDate: String = formatDateFromLong(getCurrentEpochMillis()),
    val selectedChargeDateMillis: Long? = getCurrentEpochMillis(),
    val isChargeDatePickerVisible: Boolean = false,
    val chargeAmount: String = "",
    val chargeAmountError: StringResource? = null,
    val isAddChargeDialogVisible: Boolean = false,
    val editingChargeIndex: Int = -1,
)

data class CreatedCharges(
    val id: Long? = -1,
    val name: String?,
    val date: String,
    val type: String?,
    val amount: Double? = 0.0,
    val collectedOn: String = "",
)

sealed interface CreateSavingEvent {
    data object NavigateBack : CreateSavingEvent
    data object Finish : CreateSavingEvent
}

sealed interface CreateSavingAction {
    data object OnBackClick : CreateSavingAction
    data object NextStep : CreateSavingAction
    data object PreviousStep : CreateSavingAction
    data class OnProductNameChange(val index: Int) : CreateSavingAction
    data class OnFieldOfficerChange(val index: Int) : CreateSavingAction
    data class OnExternalIdChange(val value: String) : CreateSavingAction
    data class OnSubmittedDatePickerToggle(val visible: Boolean) : CreateSavingAction
    data class OnSubmittedDateSelected(val millis: Long?) : CreateSavingAction

    data class OnCurrencyChange(val index: Int) : CreateSavingAction
    data class OnDecimalPlacesChange(val value: String) : CreateSavingAction
    data class OnNominalAnnualInterestRateChange(val value: String) : CreateSavingAction
    data class OnInterestCompoundingPeriodChange(val index: Int) : CreateSavingAction
    data class OnInterestPostingPeriodChange(val index: Int) : CreateSavingAction
    data class OnInterestCalculationChange(val index: Int) : CreateSavingAction
    data class OnDaysInYearChange(val index: Int) : CreateSavingAction
    data class OnOverdraftAllowedChange(val allowed: Boolean) : CreateSavingAction
    data class OnMinimumOpeningBalanceChange(val value: String) : CreateSavingAction
    data class OnMinimumBalanceChange(val allowed: Boolean) : CreateSavingAction
    data class OnMonthlyMinimumBalanceChange(val value: String) : CreateSavingAction
    data class OnFrequencyChange(val value: String) : CreateSavingAction
    data class OnFreqTypeChange(val index: Int) : CreateSavingAction

    data object OnAddChargeClick : CreateSavingAction
    data class OnEditChargeClick(val index: Int) : CreateSavingAction
    data class OnDeleteChargeClick(val index: Int) : CreateSavingAction
    data class OnChooseChargeChange(val index: Int) : CreateSavingAction
    data class OnChargeAmountChange(val value: String) : CreateSavingAction
    data class OnChargeDateDatePickerToggle(val visible: Boolean) : CreateSavingAction
    data class OnChargeDateSelected(val millis: Long?) : CreateSavingAction
    data object OnAddChargeToList : CreateSavingAction
    data object OnDismissDialog : CreateSavingAction

    data object SubmitSavingsApplication : CreateSavingAction
    data object DismissSuccessDialog : CreateSavingAction
    data object Retry : CreateSavingAction
}
