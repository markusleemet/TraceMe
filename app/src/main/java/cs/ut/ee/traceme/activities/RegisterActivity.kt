package cs.ut.ee.traceme.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.Gson
import cs.ut.ee.traceme.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private val emailRegex: Regex = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    private val nameRegex: Regex = Regex("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*\$")
    private val phoneRegex: Regex = Regex("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*\$")
    private lateinit var sharedPreferences: SharedPreferences
    private val registerURL = "http://3.134.85.176:8000/api/register"

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPreferences.getBoolean("theme", false)) setTheme(R.style.AppThemeDark) else setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }



    fun buttonRegisterPressed(view: View){
        //first, lets get inputs
        val name = text_input_edit_text_register_name.text.toString()
        val email = text_input_edit_text_register_email.text.toString()
        val phone = text_input_edit_text_register_phone.text.toString()
        val password = text_input_edit_text_register_password.text.toString()
        val confirmPassword = text_input_edit_text_register_confirm_password.text.toString()

        if (!insertedInformationIsCorrect(name, email, phone, password, confirmPassword)) {
            return
        }else{
            registerAccount(name, email, phone, password)
        }
    }


    private fun insertedInformationIsCorrect(name: String, email: String, phone: String, password: String, confirmPassword: String): Boolean{
        //lets check inserted name
        if (!nameRegex.containsMatchIn(name)) {
            //TOAST to show user information (email is not correct)
            val toast = Toast.makeText(this, "Inserted name is not correct", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 10)
            toast.show()
            return false
        }

        //lets check that correct email is inserted
        if (!emailRegex.containsMatchIn(email)) {
            //TOAST to show user information (email is not correct)
            val toast = Toast.makeText(this, "Inserted email is not correct", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 10)
            toast.show()
            return false
        }

        //lets check that correct email is inserted
        if (!phoneRegex.containsMatchIn(phone)) {
            //TOAST to show user information (email is not correct)
            val toast = Toast.makeText(this, "Inserted phone number is not correct", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 10)
            toast.show()
            return false
        }

        //lets check that passwords match
        if (password != confirmPassword) {
            //TOAST to show user information (passwords to not match)
            val toast = Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
            return false
        }
        return true
    }

    private fun registerAccount(name: String, email: String, phone: String, password: String){
        val requestBody = Gson().toJson(Register(email, phone, name, password))
        Fuel.post(registerURL).jsonBody(requestBody).response { request, response, result ->
            Log.i("lüliti", "request -> $request")
            Log.i("lüliti", "response -> $response")

            when (response.statusCode) {
                201 -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                400 -> {
                    //TOAST to show user information
                    val toast = Toast.makeText(this, "Something went wrong! Try again later.", Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }
            }
        }
    }

    private data class Register(val email: String, val phone_number: String, val name: String, val password: String)
}
