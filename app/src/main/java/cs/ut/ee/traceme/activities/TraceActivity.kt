package cs.ut.ee.traceme.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cs.ut.ee.traceme.R
import cs.ut.ee.traceme.services.LocationService
import kotlinx.android.synthetic.main.activity_trace.*

class TraceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trace)

        switch_location.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Intent(this, LocationService::class.java).also { intent ->
                    startService(intent)
                }
            }else{
                Intent(this, LocationService::class.java).also { intent ->
                    stopService(intent)
                }
            }
        }
    }
}
