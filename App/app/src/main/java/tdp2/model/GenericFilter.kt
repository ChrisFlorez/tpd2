package tdp2.model

import android.location.Location

interface GenericFilter<T> {
    fun filter(selectedValue: T, banks: List<BankATM>, currentLocation: Location): List<BankATM>
}