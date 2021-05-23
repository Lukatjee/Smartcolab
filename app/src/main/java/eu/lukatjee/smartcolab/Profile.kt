package eu.lukatjee.smartcolab

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class Profile : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val saveEditProfile = findViewById<Button>(R.id.edit_save_profile_button)
        saveEditProfile.setOnClickListener(this)

        val logoutButton = findViewById<Button>(R.id.button2)
        logoutButton.setOnClickListener(this)

        getData()

    }

    private var isPressed = false

    private fun doubleTapBack() {

        if (isPressed) {

            this.finishAffinity()

        } else {

            Toast.makeText(this, "Press return key again to exit the app.", Toast.LENGTH_LONG).show()
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

            R.id.edit_save_profile_button -> {
                val saveEditProfile = findViewById<Button>(R.id.edit_save_profile_button)

                val userNameView = findViewById<EditText>(R.id.displayname_view)
                val emailView = findViewById<EditText>(R.id.email_view)

                if (!isEditing) {

                    isEditing = true

                    saveEditProfile.text = getString(R.string.save)
                    saveEditProfile.invalidate()

                    userNameView.isEnabled = true
                    emailView.isEnabled = true

                    return

                }

                val currentUserDisplayNameInput = userNameView.text.toString().trim()
                val currentUserEmailInput = emailView.text.toString().trim()

                if (currentUserDisplayNameInput.isEmpty()) {

                    emailView.error = "Invalid username"
                    emailView.requestFocus()

                    return

                }

                if (!(Patterns.EMAIL_ADDRESS.matcher(currentUserEmailInput).matches())) {

                    emailView.error = "Invalid e-mail address"
                    emailView.requestFocus()

                    return

                }

                val profileUpdate = UserProfileChangeRequest.Builder().setDisplayName(currentUserDisplayNameInput).build()

                FirebaseAuth.getInstance().currentUser!!.updateProfile(profileUpdate).addOnCompleteListener(this){ task ->

                    if (task.isSuccessful) {

                        saveProfileDisplayname()

                    } else {

                        println("Oopsie daisy, I couldn't change your displayname")

                    }

                }

                FirebaseAuth.getInstance().currentUser!!.updateEmail(currentUserEmailInput).addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        saveProfileEmail()

                    } else {

                        println("Oopsie daisy, I couldn't change the email address")

                    }

                }

                saveProfile()

            }

            R.id.button2 -> {

                FirebaseAuth.getInstance().signOut()

                intent = Intent(this, Landing::class.java)
                intent.putExtra("FROM_ACTIVITY", "NONE")

                startActivity(intent)

            }

        }

    }

    private fun saveProfile() {

        val saveEditProfile = findViewById<Button>(R.id.edit_save_profile_button)

        saveEditProfile.text = getString(R.string.edit)
        saveEditProfile.invalidate()

        isEditing = false

    }

    private fun saveProfileDisplayname() {

        val userNameView = findViewById<EditText>(R.id.displayname_view)
        userNameView.isEnabled = false

    }

    private fun saveProfileEmail() {

        val emailView = findViewById<EditText>(R.id.email_view)
        emailView.isEnabled = false

    }

    private fun getData() {

        val displayNameVw = findViewById<TextView>(R.id.displayname_view)
        val emailAddressVw = findViewById<TextView>(R.id.email_view)

        displayNameVw.text = FirebaseAuth.getInstance().currentUser!!.displayName
        displayNameVw.invalidate()

        emailAddressVw.text = FirebaseAuth.getInstance().currentUser!!.email
        emailAddressVw.invalidate()

    }

}
