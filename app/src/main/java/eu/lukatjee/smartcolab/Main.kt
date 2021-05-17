package eu.lukatjee.smartcolab

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class Main : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?){

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val forgotPin = findViewById<TextView>(R.id.forgotPin); forgotPin.setOnClickListener(this)
        val loginButton = findViewById<Button>(R.id.loginButton); loginButton.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.loginButton -> userLogin()
            R.id.forgotPin -> {

                val usernameInput = findViewById<EditText>(R.id.fieldUsername); val usernameData = usernameInput.text.toString().trim()

                if (!Patterns.EMAIL_ADDRESS.matcher(usernameData).matches()) {

                    usernameInput.error = "Invalid e-mail address"
                    usernameInput.requestFocus()

                    return

                }

                FirebaseAuth.getInstance().sendPasswordResetEmail(usernameData).addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        Toast.makeText(this, "Email has been sent!", Toast.LENGTH_LONG).show()

                    } else {

                        Toast.makeText(this, "This email address is not in our database!", Toast.LENGTH_LONG).show()

                    }

                }

            }

        }

    }

    private fun userLogin() {

        val usernameInput = findViewById<EditText>(R.id.fieldUsername); val usernameData = usernameInput.text.toString().trim()
        val passwordInput = findViewById<EditText>(R.id.fieldPassword); val passwordData = passwordInput.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(usernameData).matches()) {

            usernameInput.error = "Invalid e-mail address"
            usernameInput.requestFocus()

            return

        }

        if (passwordData.isEmpty()) {

            passwordInput.error = "Password is required"
            passwordInput.requestFocus()

            return

        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(usernameData, passwordData).addOnCompleteListener(this) { task ->

            if (task.isSuccessful) {

                intent = Intent(this, Profile::class.java)
                startActivity(intent)

                val user = FirebaseAuth.getInstance().currentUser

            } else {

                Toast.makeText(

                    this,
                    "Failed to log in, please check your credentials!",
                    Toast.LENGTH_LONG

                ).show()

            }

        }

    }

}
