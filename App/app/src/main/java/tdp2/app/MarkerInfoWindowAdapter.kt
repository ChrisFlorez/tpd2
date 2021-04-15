package tdp2.app

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.GoogleMap

class MarkerInfoWindowAdapter(context: Context) : GoogleMap.InfoWindowAdapter {
    private val context: Context = context.applicationContext

    override fun getInfoWindow(arg0: Marker): View? {
        return null
    }

    @SuppressLint("SetTextI18n")
    override fun getInfoContents(arg0: Marker): View {

        val infoToShow = arg0.snippet.split("|")

        val inflater: LayoutInflater =
            context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val v = inflater.inflate(R.layout.map_marker_info_window, null)

        if (arg0.title != "UbicacionActual") {

            val address = v.findViewById(R.id.address) as TextView
            val bank = v.findViewById(R.id.bank) as TextView
            val net = v.findViewById(R.id.net) as TextView
            val terminals = v.findViewById(R.id.terminals) as TextView

            address.text = "Dirección: " + infoToShow[0]
            bank.text = "Banco: " + infoToShow[1]
            net.text = "Red: " + infoToShow[2]
            terminals.text = "Cantidad de terminales: " + infoToShow[3]
        } else {
            val address = v.findViewById(R.id.address) as TextView
            address.text = infoToShow[0]
        }

        return v

    }
}