package tdp2.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import tdp2.model.BankATM
import tdp2.model.BankFilter
import tdp2.model.DistanceFilter
import tdp2.model.NetFilter


class MapsActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?): Boolean = false

    private lateinit var datamanager: DataManager
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private val zoomValues = hashMapOf(
        100 to 17.8f,
        200 to 16.8f,
        500 to 15.5f,
        1000 to 14.5f
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    // TODO mover esto a otro lado, depende de implementacion final, podemos usar jackson
    private fun fetchData(): List<BankATM> {
        val bankATMS = datamanager.getBankATMS()
        return bankATMS
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        checkGPSEnabled()
        requestMapPermissions()
    }

    private fun checkGPSEnabled() {
        val lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch(e: Exception) {

        }
        if (!gps_enabled) {
            Toast.makeText(getApplicationContext(),"Encienda el GPS para poder continuar.",Toast.LENGTH_LONG).show()
            Thread.sleep(1000)
            finishAffinity()
        }

    }

    private fun requestMapPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        setUpMap()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"No es posible localizar al usuario en el mapa.",Toast.LENGTH_SHORT).show()
            finishAffinity()

        } else {
            // restart para que ubique el contenido del mapa
            var restart_intent = Intent(intent)
            finish()
            startActivity(restart_intent)
        }
    }


    @SuppressLint("DefaultLocale")
    private fun setUpMap() {

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)

                //addCircle()

                //val bankLatLng = LatLng(-34.6382766900504, -58.3694599562864)

                //placeMarkerOnMap(bankLatLng)

                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        currentLatLng,
                        zoomValues.get(500)!!
                    )
                )
            }
        }
        // GET & setup information from server
        datamanager = DataManager(this)
    }


    // When the BankATMs are loaded this is called
    fun onDataManagerFinish() {
        val bankATMS = fetchData()
        val bankOptions = bankATMS.map { bank -> bank.banco.toUpperCase() }.distinct()
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it }))
        val netOptions = bankATMS.map { bank -> bank.red }.distinct()

        selectNet(netOptions, bankATMS)
        selectDistance(bankATMS)
        selectBank(bankOptions, bankATMS)
    }

    private fun addCircle(radio: Double = 500.0) {
        map.addCircle(
            CircleOptions()
                .center(LatLng(lastLocation.latitude, lastLocation.longitude))
                .radius(radio)
                .strokeColor(Color.RED)
        )
    }

    private fun placeMarkerOnMap(location: LatLng) {
        // 1
        val markerOptions = MarkerOptions().position(location).title("UbicacionActual").snippet("Tu ubicación actual")
        // 2
        map.addMarker(markerOptions)
    }

    fun selectDistance(bankATMS: List<BankATM>) {
        val distanceOptions = listOf(100, 200, 500, 1000)
        distanceSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, distanceOptions)
        distanceSpinner.setSelection(2)
        distanceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                netSpinner.setSelection(0)
                bankSpinner.setSelection(0)
                val distanceSelected = distanceOptions[p2]
                //addCircle(distanceSelected.toDouble())
                val currentLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        currentLatLng,
                        zoomValues.get(distanceSelected)!!
                    )
                )
                map.clear()
                placeMarkerOnMap(currentLatLng)
                showInMap(filterByDistance(distanceSelected, bankATMS))
            }
        }
    }

    fun selectNet(netOptions: List<String>, bankATMS: List<BankATM>) {
        val netList = listOf("Elegir red") + netOptions
        netSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, netList)

        netSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            @SuppressLint("DefaultLocale")
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                val banksByNet = listOf("Elegir banco") + bankATMS.filter { bank -> bank.red.toUpperCase() == netList[p2] }.map { bank -> bank.banco.toUpperCase()}.distinct()
                    .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it }))

                if (p2 != 0) {
                    bankSpinner.adapter = ArrayAdapter(
                        applicationContext,
                        android.R.layout.simple_spinner_dropdown_item,
                        banksByNet
                    )
                }

                val selectedBank = bankSpinner.selectedItem.toString()
                val selectedDistance = distanceSpinner.selectedItem.toString().toInt()

                val filteredByBank = filterByBank(selectedBank, bankATMS)
                val banksToFilter = if (selectedBank != bankSpinner.adapter.getItem(0)) filteredByBank else bankATMS

                val filteredByBankAndDistance = filterByDistance(selectedDistance, banksToFilter)
                val filteredBanks = if (p2 != 0) filterByNet(netList[p2], filteredByBankAndDistance) else filteredByBankAndDistance

                if (filteredBanks.isEmpty() && selectedBank != bankSpinner.adapter.getItem(0)) {
                    if (selectedDistance != distanceSpinner.adapter.getItem(3)) {
                        showNoBanksFoundMessage()
                    } else if (selectedDistance == distanceSpinner.adapter.getItem(3)) {
                        showNoBanksFoundMessageForBiggestRadio()
                    }

                }
                map.clear()
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
                showInMap(filteredBanks)
            }
        }
    }

    fun selectBank(bankOptions: List<String>, bankATMS: List<BankATM>) {
        val bankList = listOf("Elegir banco") + bankOptions
        bankSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, bankList)

        bankSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedDistance = distanceSpinner.selectedItem.toString().toInt()
                val selectedNet = netSpinner.selectedItem.toString()

                val filteredByBank = filterByBank(bankSpinner.selectedItem.toString(), bankATMS)
                val banksToFilter = if (p2 != 0) filteredByBank else bankATMS

                val filteredByBankAndDistance = filterByDistance(selectedDistance, banksToFilter)
                val filteredBanks = if (selectedNet != netSpinner.adapter.getItem(0)) filterByNet(selectedNet, filteredByBankAndDistance) else filteredByBankAndDistance

                if (filteredBanks.isEmpty() && bankList[p2] != bankSpinner.adapter.getItem(0)) {
                    if (selectedDistance != distanceSpinner.adapter.getItem(3)) {
                        showNoBanksFoundMessage()
                    } else if (selectedDistance == distanceSpinner.adapter.getItem(3)) {
                        showNoBanksFoundMessageForBiggestRadio()
                    }

                }
                map.clear()
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
                showInMap(filteredBanks)
            }
        }
    }

    fun filterByDistance(selectedValue: Int, bankATMS: List<BankATM>): List<BankATM> {
        val distanceFilter = DistanceFilter()

        return distanceFilter.filter(selectedValue, bankATMS, lastLocation)
    }

    fun filterByNet(selectedValue: String, bankATMS: List<BankATM>): List<BankATM> {
        val netFilter = NetFilter()

        return netFilter.filter(selectedValue, bankATMS, lastLocation)
    }

    fun filterByBank(selectedValue: String, bankATMS: List<BankATM>): List<BankATM> {
        val bankFilter = BankFilter()

        return bankFilter.filter(selectedValue, bankATMS, lastLocation)
    }

    fun showInMap(nearBanks: List<BankATM>) {
        nearBanks.forEach {
            val marker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(it.lat, it.long))
                    .snippet(it.ubicacion + "|" + it.banco + "|" + it.red + "|" + it.terminales)
                    .icon(
                        BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory.decodeResource(
                                resources,
                                R.mipmap.ic_bank
                            )
                        )
                    )
            )
            showInfo(marker)
        }
    }

    fun showInfo(marker: Marker) {

        val markerInfoWindowAdapter = MarkerInfoWindowAdapter(applicationContext)
        map.setInfoWindowAdapter(markerInfoWindowAdapter)

        map.setOnMapClickListener(GoogleMap.OnMapClickListener {
            marker.hideInfoWindow()
        })

        map.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener {
            marker.showInfoWindow()
            return@OnMarkerClickListener false
        })
    }

    fun showNoBanksFoundMessage() {
        val toast = Toast.makeText(
            applicationContext,
            "No hay resultados para el radio seleccionado, intente con un radio más grande", Toast.LENGTH_SHORT
        )

        toast.show()
    }

    fun showNoBanksFoundMessageForBiggestRadio() {
        val toast = Toast.makeText(
            applicationContext,
            "No hay resultados para la búsqueda, muévase por la ciudad antes de volver a buscar", Toast.LENGTH_SHORT
        )

        toast.show()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

}
