package cs.ut.ee.traceme.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import cs.ut.ee.traceme.R
import cs.ut.ee.traceme.services.LocationService
import kotlinx.android.synthetic.main.activity_trace.*

class TraceActivity : AppCompatActivity() {
    private val backgroundLocationPermissionConstant = 3442
    private val fineLocationPermissionConstant = 3444
    private val fineAndBackgroundLocationPermissionConstant = 3443
    private var doubleBackToExitPressedOnce = false
    private val settingsActivityConstant = 3444
    private val statisticsActivityConstant = 3445
    private lateinit var sharedPreferences: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getBoolean("theme", false)) setTheme(R.style.AppThemeDark) else setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trace)
        setSupportActionBar(my_toolbar)
        checkForPermissions()
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.on_foot -> {
                    sharedPreferences.edit().putInt("meanOfTransport", checkedId).apply()
                    Log.i("lüliti", "checked mean of transport: walking ($checkedId)")
                }
                R.id.car -> {
                    sharedPreferences.edit().putInt("meanOfTransport", checkedId).apply()
                    Log.i("lüliti", "checked mean of transport:  car ($checkedId)")
                }
                R.id.bus -> {
                    sharedPreferences.edit().putInt("meanOfTransport", checkedId).apply()
                    Log.i("lüliti", "checked mean of transport: bus ($checkedId)")
                }
                R.id.subway -> {
                    sharedPreferences.edit().putInt("meanOfTransport", checkedId).apply()
                    Log.i("lüliti", "checked mean of transport: subway ($checkedId)")
                }
            }
        }
        restoreActivity()
    }


    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit and end positioningd", Toast.LENGTH_SHORT).show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(settingsIntent, settingsActivityConstant)
                return true
            }
            R.id.logout -> {
                killLocationService()
                val finishIntent = Intent()
                setResult(Activity.RESULT_OK)
                finish()
                return  true
            }
            R.id.statistic -> {
                val statisticsIntent = Intent(this, StatisticsActivity::class.java)
                val token = intent.getStringExtra("token")
                statisticsIntent.putExtra("token", token)
                startActivityForResult(statisticsIntent, statisticsActivityConstant)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun startLocationService(){
        sharedPreferences.edit().putBoolean("locationSharing", true)
        Intent(this, LocationService::class.java).also { servicentent ->
            val token = intent.getStringExtra("token")
            servicentent.putExtra("token", token)
            startService(servicentent)
        }
    }

    internal fun killLocationService(){
        sharedPreferences.edit().putBoolean("locationSharing", false).apply()
        Intent(this, LocationService::class.java).also { intent ->
            stopService(intent)
        }
    }

    private fun restoreActivity(){
        val selectedMeanOfTransport = sharedPreferences.getInt("meanOfTransport", -1)
        if (selectedMeanOfTransport != -1) {
            radioGroup.check(selectedMeanOfTransport)
        }
        switch_location.isChecked = sharedPreferences.getBoolean("locationSharing", false)
    }

    private fun addListenerToSwitch(){
        switch_location.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Log.i("lüliti", "${radioGroup.checkedRadioButtonId}")
                if (radioGroup.checkedRadioButtonId != -1) {
                    switch_location.text = resources.getString(R.string.location_sharing_is_on)
                    startLocationService()
                }else{
                    switch_location.isChecked = false
                    //TOAST to show user information
                    val toast = Toast.makeText(this, "Please specify mean of transport first", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }
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


    private fun checkForPermissions(){
        //Check if necessary permissions are granted
        val permissionAccessFineLocationApproved = ActivityCompat
            .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        val backgroundLocationPermissionApproved = ActivityCompat
            .checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                PackageManager.PERMISSION_GRANTED


        if (android.os.Build.VERSION.SDK_INT < 29) {
            if (permissionAccessFineLocationApproved) {
                addListenerToSwitch()
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    fineAndBackgroundLocationPermissionConstant
                )
            }
        }else{
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
            backgroundLocationPermissionConstant -> {
                Log.i("lüliti", "backgroundlocation was: ${grantResults[0] == PackageManager.PERMISSION_GRANTED}")
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
            fineAndBackgroundLocationPermissionConstant -> {
                Log.i("lüliti", "fine groundlocation was: ${grantResults[0] == PackageManager.PERMISSION_GRANTED}")
                Log.i("lüliti", "backgroundgroundlocation was: ${grantResults[1] == PackageManager.PERMISSION_GRANTED}")
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED ) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            settingsActivityConstant -> {
                recreate()
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
