package eu.lukatjee.smartcolab

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import io.paperdb.Paper

class Profile : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val saveEditProfile = findViewById<Button>(R.id.editProfileBtn)
        saveEditProfile.setOnClickListener(this)

        val logoutButton = findViewById<Button>(R.id.logoutBtn)
        logoutButton.setOnClickListener(this)

        getData()

    }

    private var isPressed = false

    private fun doubleTapBack() {

        if (isPressed) {

            this.finishAffinity()

        } else {

            Toast.makeText(this, "Press the return key again to exit the app", Toast.LENGTH_LONG).show()
            isPressed = true

        }

    }

    override fun onBackPressed() {

        when (intent.extras!!.getString("FROM_ACTIVITY")) {

            "NONE" -> doubleTapBack()
            "LOGIN" -> doubleTapBack()

        }

    }

    private var isEditing = false

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.editProfileBtn -> {

                val editProfileBtn = findViewById<Button>(R.id.editProfileBtn)

                val displaynameEt = findViewById<EditText>(R.id.displaynameEt)
                val emailEt = findViewById<EditText>(R.id.emailEtProfile)

                if (!isEditing) {

                    isEditing = true

                    editProfileBtn.text = getString(R.string.save)
                    editProfileBtn.invalidate()

                    displaynameEt.isEnabled = true
                    emailEt.isEnabled = true

                    return

                }

                val displaynameIpt = displaynameEt.text.toString().trim()
                val emailIpt = emailEt.text.toString().trim()

                if (displaynameIpt.isEmpty()) {

                    emailEt.error = "Invalid username"
                    emailEt.requestFocus()

                    return

                }

                if (!(Patterns.EMAIL_ADDRESS.matcher(emailIpt).matches())) {

                    emailEt.error = "Invalid e-mail address"
                    emailEt.requestFocus()

                    return

                }

                val proflieChangeRqst = UserProfileChangeRequest.Builder().setDisplayName(displaynameIpt).build()

                FirebaseAuth.getInstance().currentUser!!.updateProfile(proflieChangeRqst).addOnCompleteListener(this){ task ->

                    if (task.isSuccessful) {

                        displaynameSv()

                    } else {

                        println("Oopsie daisy, I couldn't change your displayname")

                    }

                }

                FirebaseAuth.getInstance().currentUser!!.updateEmail(emailIpt).addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        emailSv()

                    } else {

                        println("Oopsie daisy, I couldn't change the email address")

                    }

                }

                fullProfileSv()

            }

            R.id.logoutBtn -> {

                FirebaseAuth.getInstance().signOut()

                intent = Intent(this, Landing::class.java)
                intent.putExtra("FROM_ACTIVITY", "NONE")

                startActivity(intent)
                Paper.book().destroy()

            }

        }

    }

    private fun fullProfileSv() {

        val editProfileBtn = findViewById<Button>(R.id.editProfileBtn)

        editProfileBtn.text = getString(R.string.edit)
        editProfileBtn.invalidate()

        isEditing = false

    }

    private fun displaynameSv() {

        val displaynameEt = findViewById<EditText>(R.id.displaynameEt)
        displaynameEt.isEnabled = false

    }

    private fun emailSv() {

        val emailEt = findViewById<EditText>(R.id.emailEtProfile)
        emailEt.isEnabled = false

    }

    private fun getData() {

        val displaynameEt = findViewById<TextView>(R.id.displaynameEt)
        val emailEt = findViewById<TextView>(R.id.emailEtProfile)

        displaynameEt.text = FirebaseAuth.getInstance().currentUser!!.displayName
        displaynameEt.invalidate()

        emailEt.text = FirebaseAuth.getInstance().currentUser!!.email
        emailEt.invalidate()

    }

}
