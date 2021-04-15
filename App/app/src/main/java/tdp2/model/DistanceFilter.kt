package tdp2.model

import android.location.Location

class DistanceFilter : GenericFilter<Int> {
    override fun filter(selectedValue: Int, banks: List<BankATM>, currentLocation: Location): List<BankATM> {
        return banks.filter { bank ->

            val bankLocation = bank.location
            val distance = calculateRadio(currentLocation, bankLocation)

            distance <= selectedValue
        }
    }

    private fun calculateRadio(currentLocation: Location, bankLocation: Location): Float {
        return currentLocation.distanceTo(bankLocation)
    }

}