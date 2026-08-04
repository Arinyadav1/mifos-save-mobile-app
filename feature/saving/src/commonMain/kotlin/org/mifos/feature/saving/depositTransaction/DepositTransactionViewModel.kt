/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package org.mifos.feature.saving.depositTransaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.mifos.core.base.store.screen.DataFreshness
import org.mifos.core.base.store.screen.ScreenState
import org.mifos.core.base.store.submit.SubmitState
import org.mifos.core.base.ui.viewmodel.BaseViewModel
import org.mifos.core.common.Constants
import org.mifos.core.common.formatDateFromLong
import org.mifos.core.common.getCurrentEpochMillis
import org.mifos.core.data.savings.SavingsRepository
import org.mifos.core.model.savings.PaymentTypeOption

class DepositTransactionViewModel(
    savedStateHandle: SavedStateHandle,
    private val savingsRepository: SavingsRepository,
) : BaseViewModel<DepositTransactionState, DepositTransactionEvent, DepositTransactionAction>(
    DepositTransactionState(
        accountId = savedStateHandle.toRoute<DepositTransactionRoute>().accountId,
    ),
) {
    private val route = savedStateHandle.toRoute<DepositTransactionRoute>()

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

    override fun handleAction(action: DepositTransactionAction) {
        when (action) {
            DepositTransactionAction.OnBackClick -> sendEvent(DepositTransactionEvent.NavigateBack)
            is DepositTransactionAction.OnDateSelected -> handleDateSelected(action.millis)
            DepositTransactionAction.SubmitDeposit -> submitDeposit()
            DepositTransactionAction.DismissSuccessDialog -> dismissSuccessDialog()
            DepositTransactionAction.Retry -> retry()
            DepositTransactionAction.DismissErrorState -> dismissErrorState()
            else -> handleFormAction(action)
        }
    }

    private fun handleFormAction(action: DepositTransactionAction) {
        when (action) {
            is DepositTransactionAction.OnDatePickerToggle -> {
                mutableStateFlow.update { it.copy(isDatePickerVisible = action.visible) }
            }
            is DepositTransactionAction.OnAmountChanged -> {
                mutableStateFlow.update { it.copy(amount = action.amount, amountError = null) }
            }
            is DepositTransactionAction.OnPaymentTypeSelected -> {
                mutableStateFlow.update {
                    it.copy(selectedPaymentType = action.option, paymentTypeError = null)
                }
            }
            is DepositTransactionAction.OnAccountNumberChanged -> {
                mutableStateFlow.update { it.copy(accountNumber = action.value) }
            }
            is DepositTransactionAction.OnCheckNumberChanged -> {
                mutableStateFlow.update { it.copy(checkNumber = action.value) }
            }
            is DepositTransactionAction.OnRoutingCodeChanged -> {
                mutableStateFlow.update { it.copy(routingCode = action.value) }
            }
            is DepositTransactionAction.OnReceiptNumberChanged -> {
                mutableStateFlow.update { it.copy(receiptNumber = action.value) }
            }
            is DepositTransactionAction.OnBankNumberChanged -> {
                mutableStateFlow.update { it.copy(bankNumber = action.value) }
            }
            else -> Unit
        }
    }

    private fun dismissSuccessDialog() {
        mutableStateFlow.update { it.copy(showSuccessDialog = false) }
        sendEvent(DepositTransactionEvent.NavigateBackWithUpdateData(route.accountId))
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

    private fun submitDeposit() {
        val dateText = state.dateText
        val amount = state.amount
        val selectedPaymentType = state.selectedPaymentType

        if (dateText.isBlank() || amount.isBlank() || selectedPaymentType == null) {
            mutableStateFlow.update {
                it.copy(
                    amountError = if (amount.isBlank()) "Amount is required" else null,
                    paymentTypeError = if (selectedPaymentType == null) "Payment type is required" else null,
                )
            }
            return
        }

        mutableStateFlow.update { it.copy(submitState = SubmitState.Submitting()) }
        viewModelScope.launch {
            val result = savingsRepository.depositTransaction(
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

data class DepositTransactionState(
    val accountId: Long,
    val selectedDateMillis: Long? = null,
    val dateText: String = formatDateFromLong(getCurrentEpochMillis()),
    val amount: String = "",
    val amountError: String? = null,
    val paymentTypeOptions: List<PaymentTypeOption> = emptyList(),
    val selectedPaymentType: PaymentTypeOption? = null,
    val paymentTypeError: String? = null,
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

sealed interface DepositTransactionEvent {
    data class NavigateBackWithUpdateData(val accountId: Long) : DepositTransactionEvent
    data object NavigateBack : DepositTransactionEvent
}

sealed interface DepositTransactionAction {
    data object OnBackClick : DepositTransactionAction
    data class OnDateSelected(val millis: Long?) : DepositTransactionAction
    data class OnDatePickerToggle(val visible: Boolean) : DepositTransactionAction
    data class OnAmountChanged(val amount: String) : DepositTransactionAction
    data class OnPaymentTypeSelected(val option: PaymentTypeOption) : DepositTransactionAction
    data class OnAccountNumberChanged(val value: String) : DepositTransactionAction
    data class OnCheckNumberChanged(val value: String) : DepositTransactionAction
    data class OnRoutingCodeChanged(val value: String) : DepositTransactionAction
    data class OnReceiptNumberChanged(val value: String) : DepositTransactionAction
    data class OnBankNumberChanged(val value: String) : DepositTransactionAction
    data object SubmitDeposit : DepositTransactionAction
    data object DismissSuccessDialog : DepositTransactionAction
    data object Retry : DepositTransactionAction
    data object DismissErrorState : DepositTransactionAction
}
