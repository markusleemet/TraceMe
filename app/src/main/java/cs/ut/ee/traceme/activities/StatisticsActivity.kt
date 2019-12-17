package cs.ut.ee.traceme.activities

import android.content.SharedPreferences
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.github.kittinunf.fuel.Fuel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import cs.ut.ee.traceme.R
import kotlinx.android.synthetic.main.activity_statistics.*


class StatisticsActivity : AppCompatActivity() {
    private val statURL = "http://3.134.85.176:8000/api/stat"
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getBoolean("theme", false)) setTheme(R.style.AppThemeDark) else setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        //get data from API
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
        //add data to chart
        val mean_on_foot: Float = statistics.on_foot.split("%")[0].toFloat()
        val mean_car: Float = statistics.car.split("%")[0].toFloat()
        val mean_bus: Float = statistics.bus.split("%")[0].toFloat()
        val mean_subway: Float = statistics.subway.split("%")[0].toFloat()
        Log.i("lüliti", "$mean_on_foot")
        Log.i("lüliti", "$mean_car")
        Log.i("lüliti", "$mean_bus")
        Log.i("lüliti", "$mean_subway")
        val pieEntryOnFoot = PieEntry(mean_on_foot, "on foot")
        val pieEntryCar = PieEntry(mean_car, "car")
        val pieEntryBus = PieEntry(mean_bus, "bus")
        val pieEntrySubway = PieEntry(mean_subway, "subway")
        val entries = arrayListOf<PieEntry>(pieEntryBus, pieEntryCar, pieEntryOnFoot, pieEntrySubway)
        val dataSet = PieDataSet(entries, "")
        dataSet.setColors(arrayListOf(Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW))
        val pieData = PieData(dataSet)
        mean_of_transport_piechart.data = pieData
        mean_of_transport_piechart.description.isEnabled = false
        mean_of_transport_piechart.legend.isEnabled = false

        mean_of_transport_piechart.holeRadius = 45f
        mean_of_transport_piechart.setHoleColor(Color.TRANSPARENT)
        mean_of_transport_piechart.invalidate()
    }

    private data class Stats(val car: String, val on_foot: String, val bus: String, val subway: String,
    val north_tartu: String, val east_Tartu: String, val south_tartu: String, val west_tartu: String){}
}
