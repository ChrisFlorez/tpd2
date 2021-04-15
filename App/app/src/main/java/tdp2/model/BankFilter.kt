package tdp2.model

import android.location.Location

class BankFilter: GenericFilter<String> {
    override fun filter(selectedValue: String, banks: List<BankATM>, currentLocation: Location): List<BankATM> {
        return banks.filter { bank ->
            bank.banco.toUpperCase() == selectedValue
        }
    }
}