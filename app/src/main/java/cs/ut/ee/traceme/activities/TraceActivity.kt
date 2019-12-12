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
    private val backgroundLocationPermissionConstant = 3442
    private val fineLocationPermissionConstant = 3444
    private val fineAndBackgroundLocationPermissionConstant = 3443

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trace)
        checkForPermissions()
    }


    private fun startLocationService(){
        Intent(this, LocationService::class.java).also { intent ->
            startService(intent)
        }
    }

    private fun killLocationService(){
        Intent(this, LocationService::class.java).also { intent ->
            stopService(intent)
        }
    }

    private fun addListenerToSwitch(){
        switch_location.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                switch_location.text = resources.getString(R.string.location_sharing_is_on)
                startLocationService()
            }else{
                switch_location.text = resources.getString(R.string.location_sharing_is_off)
                killLocationService()
            }
        }
    }

    private fun checkForPermissionWhenSwitch(){
        switch_location.isChecked = false
        switch_location.setOnCheckedChangeListener { buttonView, isChecked ->
            checkForPermissions()
        }
    }


    /**
    private fun checkForPermissions(){
        var allPermissionsAreGranted = false
        //Check if necessary permissions are granted
        val permissionAccessFineLocationApproved = ActivityCompat
            .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        val backgroundLocationPermissionApproved = ActivityCompat
            .checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if (permissionAccessFineLocationApproved) {
            if (backgroundLocationPermissionApproved) {
                addListenerToSwitch()
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    backgroundLocationPermissionConstant)
            }
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                fineAndBackgroundLocationPermissionConstant
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            backgroundLocationPermissionConstant -> {
                Log.i("lüliti", "backgroud location was: ${grantResults[0] == PackageManager.PERMISSION_GRANTED}")
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addListenerToSwitch()
                }else{
                    checkForPermissionWhenSwitch()
                    //TOAST to show user information
                    val toast = Toast.makeText(this, "Please grant permission to share your location", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }
            }
            fineAndBackgroundLocationPermissionConstant -> {
                Log.i("lüliti", "fine location was: ${grantResults[0] == PackageManager.PERMISSION_GRANTED}")
                Log.i("lüliti", "background location was: ${grantResults[1] == PackageManager.PERMISSION_GRANTED}")
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    addListenerToSwitch()
                }else{
                    checkForPermissionWhenSwitch()
                    //TOAST to show user information
                    val toast = Toast.makeText(this, "Please grant permission to share your location", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }
            }
        }
    }**/

    private fun checkForPermissions(){
        //Check if necessary permissions are granted
        val permissionAccessFineLocationApproved = ActivityCompat
            .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if (permissionAccessFineLocationApproved) {
            addListenerToSwitch()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                fineAndBackgroundLocationPermissionConstant
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            fineLocationPermissionConstant -> {
                Log.i("lüliti", "finelocation was: ${grantResults[0] == PackageManager.PERMISSION_GRANTED}")
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addListenerToSwitch()
                } else {
                    checkForPermissionWhenSwitch()
                    //TOAST to show user information
                    val toast = Toast.makeText(
                        this,
                        "Please grant permission to share your location",
                        Toast.LENGTH_LONG
                    )
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }
            }
        }
    }

    override fun onDestroy() {
        killLocationService()
        super.onDestroy()
    }
}
