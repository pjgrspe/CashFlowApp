package ph.edu.auf.gorospe.patrickjason.cashflow.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ph.edu.auf.gorospe.patrickjason.cashflow.data.AccountCard
import ph.edu.auf.gorospe.patrickjason.cashflow.data.UserRepository
import androidx.lifecycle.asLiveData


class AccountViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _accountDetails = MutableLiveData<AccountCard>()
    val accountDetails: LiveData<AccountCard> get() = _accountDetails

    fun getAccountById(accountId: String) {
        viewModelScope.launch {
            try {
                val account = userRepository.getAccountById(accountId)
                _accountDetails.value = account
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateAccount(updatedAccount: AccountCard) {
        viewModelScope.launch {
            try {
                userRepository.updateAccount(updatedAccount)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteAccount(accountId: String) {
        viewModelScope.launch {
            try {
                userRepository.deleteAccount(accountId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
