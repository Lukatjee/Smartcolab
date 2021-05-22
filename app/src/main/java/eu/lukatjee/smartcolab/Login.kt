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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations

class Login : AppCompatActivity(), View.OnClickListener {

    private var db = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val forgotPin = findViewById<TextView>(R.id.forgotPin); forgotPin.setOnClickListener(this)
        val loginButton = findViewById<Button>(R.id.loginButton); loginButton.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.loginButton -> userLogin(); R.id.forgotPin -> {

            val usernameInput = findViewById<EditText>(R.id.fieldUsername)
            val usernameData = usernameInput.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(usernameData).matches()) {

                usernameInput.error = "Invalid e-mail address"
                usernameInput.requestFocus()

                return

            }

            auth.sendPasswordResetEmail(usernameData).addOnCompleteListener { task ->

                if (!task.isSuccessful) {

                    Toast.makeText(this, "This email address is not in our database", Toast.LENGTH_LONG).show()
                    return@addOnCompleteListener

                }

                Toast.makeText(this, "Email has been sent", Toast.LENGTH_LONG).show()

            }

        }}

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

        auth.signInWithEmailAndPassword(usernameData, passwordData).addOnCompleteListener(this) { task ->

            if (task.isSuccessful) {

                intent = Intent(this, Profile::class.java)
                intent.putExtra("FROM_ACTIVITY", "LOGIN")
                startActivity(intent)

                val lastLogin = hashMapOf("timestamp" to Timestamp.now())
                db.collection("lastLogin").document(FirebaseInstallations.getInstance().id.toString()).set(lastLogin)

            } else {

                Toast.makeText(this, "Failed to log in, please check your credentials!", Toast.LENGTH_LONG).show()

            }

        }

    }

}