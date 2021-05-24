package eu.lukatjee.smartcolab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import io.paperdb.Paper
import java.util.*

class Login : AppCompatActivity(), View.OnClickListener {

    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        Paper.init(this)

        val passwordResetBtn = findViewById<TextView>(R.id.passwordResetBtn)
        passwordResetBtn.setOnClickListener(this)

        val signInBtn = findViewById<Button>(R.id.signInBtn)
        signInBtn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.signInBtn -> userLogin()
            R.id.passwordResetBtn -> {

            val emailEt = findViewById<EditText>(R.id.emailEt)
            val emailIpt = emailEt.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(emailIpt).matches()) {

                emailEt.error = "Invalid e-mail address"
                emailEt.requestFocus()

                return

            }

            auth.sendPasswordResetEmail(emailIpt).addOnCompleteListener { task ->

                if (!task.isSuccessful) {

                    Toast.makeText(this, "This email does not belong to an existing user", Toast.LENGTH_LONG).show()
                    return@addOnCompleteListener

                }

                Toast.makeText(this, "Successfully sent a password reset email", Toast.LENGTH_LONG).show()

            }

        }}

    }

    private fun userLogin() {

        val emailEt = findViewById<EditText>(R.id.emailEt)
        val emailIpt = emailEt.text.toString().trim().toLowerCase(Locale.ROOT)

        val passwordEt = findViewById<EditText>(R.id.passwordEt)
        val passwordIpt = passwordEt.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(emailIpt).matches()) {

            emailEt.error = "Invalid e-mail address"
            emailEt.requestFocus()

            return

        }

        if (passwordIpt.isEmpty()) {

            passwordEt.error = "Password is required"
            passwordEt.requestFocus()

            return

        }

        auth.signInWithEmailAndPassword(emailIpt, passwordIpt).addOnCompleteListener(this) { task ->

            if (task.isSuccessful) {

                intent = Intent(this, Profile::class.java)
                intent.putExtra("FROM_ACTIVITY", "LOGIN")
                startActivity(intent)

                val userData = hashMapOf(emailIpt to passwordIpt)
                Paper.book().write("userData", userData)

            } else {

                Toast.makeText(this, "Failed to log in, please check the entered credentials", Toast.LENGTH_LONG).show()

            }

        }

    }

}