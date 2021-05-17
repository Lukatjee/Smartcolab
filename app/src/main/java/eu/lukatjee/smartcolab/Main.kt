package eu.lukatjee.smartcolab

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import java.util.*


class Main : AppCompatActivity(), View.OnClickListener {

    private val mAuth = FirebaseAuth.getInstance();
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?){

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val forgotPin = findViewById<TextView>(R.id.forgotPin); forgotPin.setOnClickListener(this)
        val loginButton = findViewById<Button>(R.id.loginButton); loginButton.setOnClickListener(this)

    }

    public override fun onStart() {

        super.onStart()

        val currentUser = mAuth.currentUser

        if (currentUser != null) {

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val emailChanges = db.collection("emailChanges")

            emailChanges.document(uid).get()

                .addOnCompleteListener { task ->

                    val document: DocumentSnapshot? = task.result
                    if (document!!.exists()) {

                        val lastChangeEmailTime = document.getDate("timestamp")

                    }

                }

        }

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

                val lastLogin = hashMapOf(

                    "timestamp" to Timestamp.now()

                )

                db.collection("lastLogin").document(FirebaseInstallations.getInstance().id.toString()).set(lastLogin)

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
