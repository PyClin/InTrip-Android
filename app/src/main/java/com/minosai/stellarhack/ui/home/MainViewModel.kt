package com.minosai.stellarhack.ui.home

import androidx.lifecycle.*
import com.minosai.stellarhack.ticket.TicketRepo
import com.minosai.stellarhack.ticket.model.Ticket
import com.minosai.stellarhack.ui.home.data.WalletRepo
import com.minosai.stellarhack.utils.events.Event
import com.minosai.stellarhack.utils.events.EventManager
import com.minosai.stellarhack.utils.events.NewTicketEvent
import com.minosai.stellarhack.utils.model.Resource
import com.minosai.stellarhack.utils.preference.AppPreferences
import com.minosai.stellarhack.utils.toLiveData
import kotlinx.coroutines.launch

class MainViewModel(
    private val ticketRepo: TicketRepo,
    private val walletRepo: WalletRepo,
    private val appPreferences: AppPreferences,
    private val eventManager: EventManager
) : ViewModel() {

    private val _ticketList = MediatorLiveData<Resource<List<Ticket>>>()
    val ticketList = _ticketList.toLiveData()

    private val _walletBalance =
        MutableLiveData<Resource<Float>>(Resource.Success(appPreferences.walletBalance))
    val walletBalance = _walletBalance.toLiveData()

    private val _syncState = MutableLiveData<Resource<Unit>>()
    val syncState = _syncState.toLiveData()

    val username: String?
        get() = appPreferences.username

    val userType: String?
        get() = appPreferences.userType

    private val newTicketObserver = Observer<Event> {
//        refreshWalletBalance()
        _walletBalance.postValue(Resource.Success(appPreferences.walletBalance))
    }

    init {
        if (shouldLogin().not()) {
            getTicketList()
            refreshWalletBalance()
        }
        eventManager.addObserver(NewTicketEvent, newTicketObserver)
    }

    override fun onCleared() {
        eventManager.removeObserver(NewTicketEvent, newTicketObserver)
    }

    fun getTicketList() = viewModelScope.launch {
        _ticketList.postValue(Resource.Loading())
        _ticketList.addSource(ticketRepo.getAllTickets()) { list ->
            _ticketList.postValue(
                Resource.Success(
                    list.sortedWith(Comparator { ticket1, ticket2 ->
                        ticket1.getDate()?.compareTo(ticket2.getDate()) ?: 0
                    }).asReversed()
                )
            )
        }
    }

    fun refreshWalletBalance() = viewModelScope.launch {
        _walletBalance.postValue(Resource.Loading())
        walletRepo.getBalance()
        _walletBalance.postValue(Resource.Success(appPreferences.walletBalance))
    }

    fun deposit(amount: Float) = viewModelScope.launch {
        _walletBalance.postValue(Resource.Loading())
        walletRepo.deposit(amount)
        _walletBalance.postValue(Resource.Success(appPreferences.walletBalance))
//        appPreferences.apply {
//            walletBalance += amount
//            _walletBalance.postValue(Resource.Success(walletBalance))
//        }
    }

    fun shouldLogin() = appPreferences.accessToken.isNullOrBlank()

    fun claim() = viewModelScope.launch {
        _walletBalance.postValue(Resource.Loading())
        when (ticketRepo.claim()) {
            is Resource.Success -> refreshWalletBalance()
            is Resource.Loading -> {
                /* no-op */
            }
            is Resource.Error -> {
                _walletBalance.postValue(Resource.Error("An error occurred while claiming"))
            }
        }
    }

    fun sync() = viewModelScope.launch {
        _syncState.postValue(Resource.Loading())
        _syncState.postValue(ticketRepo.syncTickets())
    }
}