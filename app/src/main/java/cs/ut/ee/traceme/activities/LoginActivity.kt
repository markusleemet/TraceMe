package cs.ut.ee.traceme.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import cs.ut.ee.traceme.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val registerAccountActivity = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        text_view_not_a_member.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivityForResult(registerIntent, 123)
        }
    }


    fun buttonLoginPressed(view: View){
        val traceIntent = Intent(this, TraceActivity::class.java)
        startActivity(traceIntent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == registerAccountActivity) {
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
    }


}
