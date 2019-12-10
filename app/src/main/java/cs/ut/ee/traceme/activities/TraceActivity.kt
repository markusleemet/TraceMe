package cs.ut.ee.traceme.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cs.ut.ee.traceme.R
import cs.ut.ee.traceme.services.LocationService
import kotlinx.android.synthetic.main.activity_trace.*

class TraceActivity : AppCompatActivity() {
    private val fineLocationPermissionConstant = 3442

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trace)

        switch_location.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    startLocationService()
                }else{
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        fineLocationPermissionConstant)
                }
            }else{
                Intent(this, LocationService::class.java).also { intent ->
                    stopService(intent)
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            fineLocationPermissionConstant -> {
                Log.i("lüliti", "onRequestPermissionResult: result was ${grantResults[0] == PackageManager.PERMISSION_GRANTED}")
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService()
                }else{
                    switch_location.isChecked = false
                    //TOAST to show user information
                    val toast = Toast.makeText(this, "Please grant permission to share your location", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }
            }
            else -> {
                return
            }
        }
    }

    private fun startLocationService(){
        Intent(this, LocationService::class.java).also { intent ->
            startService(intent)
        }
    }
}
