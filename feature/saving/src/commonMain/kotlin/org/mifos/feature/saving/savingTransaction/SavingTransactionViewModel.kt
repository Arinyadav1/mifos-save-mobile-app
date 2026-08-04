/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.savingTransaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
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
import org.mifos.core.data.savings.SavingsRepository
import org.mifos.core.model.savings.PaymentTypeOption
import org.mifos.feature.saving.generated.resources.Res
import org.mifos.feature.saving.generated.resources.feature_saving_amount_required
import org.mifos.feature.saving.generated.resources.feature_saving_payment_type_required

class SavingTransactionViewModel(
    savedStateHandle: SavedStateHandle,
    private val savingsRepository: SavingsRepository,
) : BaseViewModel<SavingTransactionState, SavingTransactionEvent, SavingTransactionAction>(
    SavingTransactionState(
        accountId = savedStateHandle.toRoute<SavingTransactionRoute>().accountId,
        isWithdrawal = savedStateHandle.toRoute<SavingTransactionRoute>().isWithdrawal,
    ),
) {
    private val route = savedStateHandle.toRoute<SavingTransactionRoute>()

    init {
        loadTemplate()
    }

    private fun loadTemplate() {
        mutableStateFlow.update { it.copy(screenState = ScreenState.Loading) }
        viewModelScope.launch {
            savingsRepository.getSavingsTransactionTemplate(route.accountId).collect { result ->
                mutableStateFlow.update { state ->
                    when (result) {
                        is ScreenState.Content -> {
                            val template = result.data
                            val options = template.paymentTypeOptions
                            val dateText = template.date?.let { localDate ->
                                val day = localDate.dayOfMonth.toString().padStart(2, '0')
                                val month = localDate.monthNumber.toString().padStart(2, '0')
                                val year = localDate.year
                                "$day/$month/$year"
                            } ?: state.dateText
                            state.copy(
                                screenState = ScreenState.Content(Unit, DataFreshness.FRESH),
                                paymentTypeOptions = options,
                                dateText = dateText,
                            )
                        }
                        is ScreenState.Error -> state.copy(screenState = ScreenState.Error(result.error))
                        is ScreenState.NoNetwork -> state.copy(screenState = ScreenState.NoNetwork())
                        is ScreenState.Unauthenticated -> state.copy(screenState = ScreenState.Unauthenticated)
                        is ScreenState.Empty -> state.copy(screenState = ScreenState.Empty)
                        is ScreenState.Loading -> state.copy(screenState = ScreenState.Loading)
                    }
                }
            }
        }
    }

    override fun handleAction(action: SavingTransactionAction) {
        when (action) {
            SavingTransactionAction.OnBackClick -> sendEvent(SavingTransactionEvent.NavigateBack)
            is SavingTransactionAction.OnDateSelected -> handleDateSelected(action.millis)
            SavingTransactionAction.SubmitTransaction -> submitTransaction()
            SavingTransactionAction.DismissSuccessDialog -> dismissSuccessDialog()
            SavingTransactionAction.Retry -> retry()
            SavingTransactionAction.DismissErrorState -> dismissErrorState()
            else -> handleFormAction(action)
        }
    }

    private fun handleFormAction(action: SavingTransactionAction) {
        when (action) {
            is SavingTransactionAction.OnDatePickerToggle -> {
                mutableStateFlow.update { it.copy(isDatePickerVisible = action.visible) }
            }
            is SavingTransactionAction.OnAmountChanged -> {
                mutableStateFlow.update { it.copy(amount = action.amount, amountErrorRes = null) }
            }
            is SavingTransactionAction.OnPaymentTypeSelected -> {
                mutableStateFlow.update {
                    it.copy(selectedPaymentType = action.option, paymentTypeErrorRes = null)
                }
            }
            is SavingTransactionAction.OnAccountNumberChanged -> {
                mutableStateFlow.update { it.copy(accountNumber = action.value) }
            }
            is SavingTransactionAction.OnCheckNumberChanged -> {
                mutableStateFlow.update { it.copy(checkNumber = action.value) }
            }
            is SavingTransactionAction.OnRoutingCodeChanged -> {
                mutableStateFlow.update { it.copy(routingCode = action.value) }
            }
            is SavingTransactionAction.OnReceiptNumberChanged -> {
                mutableStateFlow.update { it.copy(receiptNumber = action.value) }
            }
            is SavingTransactionAction.OnBankNumberChanged -> {
                mutableStateFlow.update { it.copy(bankNumber = action.value) }
            }
            else -> Unit
        }
    }

    private fun dismissSuccessDialog() {
        mutableStateFlow.update { it.copy(showSuccessDialog = false) }
        sendEvent(SavingTransactionEvent.NavigateBackWithUpdateData(route.accountId))
    }

    private fun retry() {
        mutableStateFlow.update {
            it.copy(
                screenState = ScreenState.Content(Unit, DataFreshness.FRESH),
                submitState = SubmitState.Idle,
            )
        }
    }

    private fun dismissErrorState() {
        mutableStateFlow.update { it.copy(submitState = SubmitState.Idle) }
    }

    private fun handleDateSelected(millis: Long?) {
        if (millis != null) {
            val formattedDate = formatDateFromLong(millis)
            mutableStateFlow.update {
                it.copy(
                    selectedDateMillis = millis,
                    dateText = formattedDate,
                    isDatePickerVisible = false,
                )
            }
        } else {
            mutableStateFlow.update { it.copy(isDatePickerVisible = false) }
        }
    }

    private fun submitTransaction() {
        val dateText = state.dateText
        val amount = state.amount
        val selectedPaymentType = state.selectedPaymentType

        if (dateText.isBlank() || amount.isBlank() || selectedPaymentType == null) {
            mutableStateFlow.update {
                it.copy(
                    amountErrorRes = if (amount.isBlank()) {
                        Res.string.feature_saving_amount_required
                    } else {
                        null
                    },
                    paymentTypeErrorRes = if (selectedPaymentType == null) {
                        Res.string.feature_saving_payment_type_required
                    } else {
                        null
                    },
                )
            }
            return
        }

        mutableStateFlow.update { it.copy(submitState = SubmitState.Submitting()) }
        viewModelScope.launch {
            val result = if (state.isWithdrawal) {
                savingsRepository.withdrawTransaction(
                    accountId = route.accountId,
                    locale = Constants.LOCALE_EN,
                    dateFormat = Constants.DATE_FORMAT_SHORT_MONTH,
                    transactionDate = dateText,
                    transactionAmount = amount,
                    paymentTypeId = selectedPaymentType.id.toString(),
                    accountNumber = state.accountNumber.takeIf { it.isNotBlank() },
                    checkNumber = state.checkNumber.takeIf { it.isNotBlank() },
                    routingCode = state.routingCode.takeIf { it.isNotBlank() },
                    receiptNumber = state.receiptNumber.takeIf { it.isNotBlank() },
                    bankNumber = state.bankNumber.takeIf { it.isNotBlank() },
                )
            } else {
                savingsRepository.depositTransaction(
                    accountId = route.accountId,
                    locale = Constants.LOCALE_EN,
                    dateFormat = Constants.DATE_FORMAT_SHORT_MONTH,
                    transactionDate = dateText,
                    transactionAmount = amount,
                    paymentTypeId = selectedPaymentType.id.toString(),
                    accountNumber = state.accountNumber.takeIf { it.isNotBlank() },
                    checkNumber = state.checkNumber.takeIf { it.isNotBlank() },
                    routingCode = state.routingCode.takeIf { it.isNotBlank() },
                    receiptNumber = state.receiptNumber.takeIf { it.isNotBlank() },
                    bankNumber = state.bankNumber.takeIf { it.isNotBlank() },
                )
            }
            when (result) {
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
                            screenState = ScreenState.Error(result.error),
                            submitState = SubmitState.Failed(),
                        )
                    }
                }
                is ScreenState.NoNetwork -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ScreenState.NoNetwork(),
                            submitState = SubmitState.Failed(),
                        )
                    }
                }
                is ScreenState.Unauthenticated -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ScreenState.Unauthenticated,
                            submitState = SubmitState.Failed(),
                        )
                    }
                }
                is ScreenState.Empty -> {
                    mutableStateFlow.update {
                        it.copy(
                            screenState = ScreenState.Empty,
                            submitState = SubmitState.Failed(),
                        )
                    }
                }
                is ScreenState.Loading -> Unit
            }
        }
    }
}

data class SavingTransactionState(
    val accountId: Long,
    val isWithdrawal: Boolean = false,
    val selectedDateMillis: Long? = null,
    val dateText: String = formatDateFromLong(getCurrentEpochMillis()),
    val amount: String = "",
    val amountErrorRes: StringResource? = null,
    val paymentTypeOptions: List<PaymentTypeOption> = emptyList(),
    val selectedPaymentType: PaymentTypeOption? = null,
    val paymentTypeErrorRes: StringResource? = null,
    val accountNumber: String = "",
    val checkNumber: String = "",
    val routingCode: String = "",
    val receiptNumber: String = "",
    val bankNumber: String = "",
    val isDatePickerVisible: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val screenState: ScreenState<Unit> = ScreenState.Content(Unit, DataFreshness.FRESH),
    val submitState: SubmitState<Unit> = SubmitState.Idle,
)

sealed interface SavingTransactionEvent {
    data class NavigateBackWithUpdateData(val accountId: Long) : SavingTransactionEvent
    data object NavigateBack : SavingTransactionEvent
}

sealed interface SavingTransactionAction {
    data object OnBackClick : SavingTransactionAction
    data class OnDateSelected(val millis: Long?) : SavingTransactionAction
    data class OnDatePickerToggle(val visible: Boolean) : SavingTransactionAction
    data class OnAmountChanged(val amount: String) : SavingTransactionAction
    data class OnPaymentTypeSelected(val option: PaymentTypeOption) : SavingTransactionAction
    data class OnAccountNumberChanged(val value: String) : SavingTransactionAction
    data class OnCheckNumberChanged(val value: String) : SavingTransactionAction
    data class OnRoutingCodeChanged(val value: String) : SavingTransactionAction
    data class OnReceiptNumberChanged(val value: String) : SavingTransactionAction
    data class OnBankNumberChanged(val value: String) : SavingTransactionAction
    data object SubmitTransaction : SavingTransactionAction
    data object DismissSuccessDialog : SavingTransactionAction
    data object Retry : SavingTransactionAction
    data object DismissErrorState : SavingTransactionAction
}
