package cs.ut.ee.traceme.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import cs.ut.ee.traceme.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private val emailRegex: Regex = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    private val nameRegex: Regex = Regex("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*\$")
    private val phoneRegex: Regex = Regex("^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*\$")

    override fun onCreate(savedInstanceState: Bundle?) {
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

        //lets check inserted name
        if (!nameRegex.containsMatchIn(name)) {
            //TOAST to show user information (email is not correct)
            val toast = Toast.makeText(this, "Inserted name is not correct", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 10)
            toast.show()
            return
        }

        //lets check that correct email is inserted
        if (!emailRegex.containsMatchIn(email)) {
            //TOAST to show user information (email is not correct)
            val toast = Toast.makeText(this, "Inserted email is not correct", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 10)
            toast.show()
            return
        }

        //lets check that correct email is inserted
        if (!phoneRegex.containsMatchIn(phone)) {
            //TOAST to show user information (email is not correct)
            val toast = Toast.makeText(this, "Inserted phone number is not correct", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 10)
            toast.show()
            return
        }

        //lets check that passwords match
        if (password != confirmPassword) {
            //TOAST to show user information (passwords to not match)
            val toast = Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.TOP, 0, 0)
            toast.show()
            return
        }

        finish()
    }
}