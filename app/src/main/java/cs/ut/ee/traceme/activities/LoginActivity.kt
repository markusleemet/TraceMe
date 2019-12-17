package cs.ut.ee.traceme.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.Gson
import cs.ut.ee.traceme.R
import cs.ut.ee.traceme.services.LocationService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val registerAccountActivity = 123
    private val traceActivity = 1234
    private lateinit var sharedPreferences: SharedPreferences
    private val loginURL = "http://3.134.85.176:8000/api/login"


    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getBoolean("theme", false)) setTheme(R.style.AppThemeDark) else setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        text_view_not_a_member.setOnClickListener {
            buttonRegisterPressed()
        }
    }

    private fun checkForInternet(): Boolean{
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.activeNetworkInfo?.isConnected != true) {
            return false
        }
        return true
    }

    private fun buttonRegisterPressed(){
        //check for internet
        if (!checkForInternet()) {
            //TOAST to show user information
            val toast = Toast.makeText(this, "You are not connected to internet", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
            return
        }
        val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivityForResult(registerIntent, 123)
    }


    fun buttonLoginPressed(view: View){
        //check for internet
        if (!checkForInternet()) {
            //TOAST to show user information
            val toast = Toast.makeText(this, "You are not connected to internet", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
            return
        }

        //get values from inputs
        val email = text_input_edit_text_login_email.text.toString()
        val password = text_input_edit_text_login_password.text.toString()
        val requestBodyAsString = Gson().toJson(Login(email, password))

        Fuel.post(loginURL).jsonBody(requestBodyAsString).response { request, response, result ->
            when (response.statusCode) {
                200 -> {
                    val userToken = Gson().fromJson<Token>(response.body().toByteArray().toString(Charsets.UTF_8), Token::class.java).Token
                    val traceIntent = Intent(this, TraceActivity::class.java)
                    traceIntent.putExtra("token", userToken)
                    startActivityForResult(traceIntent, traceActivity)
                }
                400 -> {
                    //TOAST to show user information
                    val toast = Toast.makeText(this, "Wrong email address or password", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            registerAccountActivity -> {
                if (resultCode == Activity.RESULT_OK){
                    //TOAST to show user information
                    val toast = Toast.makeText(this, "Account was created!", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP, 0, 10)
                    toast.show()
                }else{
                    //TOAST to show user information
                    val toast = Toast.makeText(this, "Account wasn't created!", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }
            }
            traceActivity -> {
                if (resultCode == Activity.RESULT_OK) {
                    recreate()
                    Log.i("lüliti", "You are logged out!")
                }else{
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        killLocationService()
    }


    private fun killLocationService(){
        sharedPreferences.edit().putBoolean("locationSharing", false).apply()
        Intent(this, LocationService::class.java).also { intent ->
            stopService(intent)
        }
    }

    data class Login(val email: String, val password: String){}
    data class Token(val Token: String){}
}
