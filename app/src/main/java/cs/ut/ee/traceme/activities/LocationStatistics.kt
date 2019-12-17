package cs.ut.ee.traceme.activities

import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.github.kittinunf.fuel.Fuel
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.google.gson.Gson
import cs.ut.ee.traceme.R
import kotlinx.android.synthetic.main.activity_location_statistcs.*
import kotlinx.android.synthetic.main.activity_statistics.*

class LocationStatistics : AppCompatActivity() {
    private val statURL = "http://3.134.85.176:8000/api/stat"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getBoolean("theme", false)) setTheme(R.style.AppThemeDark) else setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_statistcs)

        token = intent!!.getStringExtra("token")!!
        Fuel.get(statURL).header("Authorization", "Token $token").also { Log.i("lüliti", "$it") }.response { request, response, result ->
            when (response.statusCode) {
                200 -> {
                    //get data
                    val statistics = Gson().fromJson<Stats>(response.body().toByteArray().toString(Charsets.UTF_8), Stats::class.java)
                    Log.i("lüliti", "$statistics")
                    drawChart(statistics)
                }
                500 -> {
                    //TOAST to show user information
                    val toast = Toast.makeText(this, "Something went wrong! Try again later.", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }
                else -> {
                    Log.i("lüliti", "other status code: ${response.statusCode}")
                }
            }
        }
    }

    private fun drawChart(statistics: Stats){
        val north_tartu = statistics.north_tartu.split("%")[0].toFloat()
        val east_tartu = statistics.east_tartu.split("%")[0].toFloat()
        val south_tartu = statistics.south_tartu.split("%")[0].toFloat()
        val west_tartu = statistics.west_tartu.split("%")[0].toFloat()
        Log.i("lüliti", "north: $north_tartu")
        Log.i("lüliti", "east: $east_tartu")
        Log.i("lüliti", "south: $south_tartu")
        Log.i("lüliti", "west: $west_tartu")

        val northTartuData = PieEntry(north_tartu, "North Tartu")
        val eastTartuData = PieEntry(east_tartu, "East Tartu")
        val southTartuData = PieEntry(south_tartu, "South Tartu")
        val westTartuData = PieEntry(west_tartu, "West Tartu")


        val entries = arrayListOf<PieEntry>(northTartuData, eastTartuData, southTartuData, westTartuData)
        val dataSet = PieDataSet(entries, "")
        dataSet.setColors(arrayListOf(Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW))
        val pieData = PieData(dataSet)
        location_bar_chart.data = pieData
        location_bar_chart.description.isEnabled = false
        location_bar_chart.legend.isEnabled = true
        location_bar_chart.setDrawEntryLabels(false)
        location_bar_chart.legend.setDrawInside(true)
        location_bar_chart.holeRadius = 45f
        location_bar_chart.legend.orientation = Legend.LegendOrientation.HORIZONTAL
        location_bar_chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        location_bar_chart.invalidate()

    }

    private data class Stats(val car: String, val on_foot: String, val bus: String, val subway: String,
                             val north_tartu: String, val east_tartu: String, val south_tartu: String,
                             val west_tartu: String){}
}
