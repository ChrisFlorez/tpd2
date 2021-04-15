package tdp2.app

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_maps.*

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    @SuppressLint("SetTextI18n")
    private fun showMessage(isConnected: Boolean) {

        if (!isConnected) {

            if (distanceSpinner.selectedItem != null) {

                val toast = Toast.makeText(
                    applicationContext,
                    "Se perdió la conectividad a Internet", Toast.LENGTH_LONG
                )

                toast.show()
            } else {

                val toast = Toast.makeText(
                    applicationContext,
                    "No hay conexión a Internet. Vuelva a intentarlo más tarde", Toast.LENGTH_LONG
                )

                toast.show()
                finishAffinity()

            }
        }
    }

    override fun onResume() {
        super.onResume()

        ConnectivityReceiver.connectivityReceiverListener = this
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }
}