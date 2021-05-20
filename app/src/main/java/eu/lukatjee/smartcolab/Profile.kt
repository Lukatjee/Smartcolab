package eu.lukatjee.smartcolab

import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore


class Profile : AppCompatActivity(), View.OnClickListener {

    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize the button to change the user's displayname
        val changeDisplaynameButton = findViewById<TextView>(R.id.changeDisplaynameButton)
        changeDisplaynameButton.setOnClickListener(this)

        // Initialize the button to change the user's email address
        val changeEmailButton = findViewById<TextView>(R.id.changeEmailButton)
        changeEmailButton.setOnClickListener(this)

        getData()

    }

    override fun onBackPressed() {

        super.onBackPressed()

        // Temporary functionality, log out on pressing the back button
        FirebaseAuth.getInstance().signOut()

    }

    override fun onClick(v: View?) {

        val changeDisplaynameEditText = findViewById<EditText>(R.id.editUsernameField)
        val changeEmailEditText = findViewById<EditText>(R.id.editEmailField)

        when (v?.id) {

            // Handles changing the users displayname
            R.id.changeDisplaynameButton -> {

                if (changeDisplaynameEditText.visibility == EditText.GONE) {

                    changeDisplaynameEditText.visibility = EditText.VISIBLE

                } else {

                    changeDisplaynameEditText.visibility = EditText.GONE

                }

                if (changeEmailEditText.visibility == EditText.VISIBLE) {

                    changeEmailEditText.visibility = EditText.GONE
                    changeEmailEditText.text.clear()

                }

                changeDisplaynameEditText.setOnKeyListener { _, keyCode, event ->

                    val checkOne = keyCode == KeyEvent.KEYCODE_ENTER
                    val checkTwo = event.action == KeyEvent.ACTION_UP

                    val currentlyLoggedInUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser;
                    val currentUserInput = changeDisplaynameEditText.text.toString().trim()

                    val profileUpdate = UserProfileChangeRequest.Builder().setDisplayName(currentUserInput).build()

                    if (!checkOne && !checkTwo) { return@setOnKeyListener false }

                    if (currentUserInput.isEmpty()) {

                        changeDisplaynameEditText.error = "Invalid username"; changeDisplaynameEditText.requestFocus()
                        return@setOnKeyListener false

                    }

                    currentlyLoggedInUser!!.updateProfile(profileUpdate).addOnCompleteListener {

                        changeDisplaynameEditText.visibility = EditText.GONE
                        changeDisplaynameEditText.text.clear(); getData()

                    }

                    return@setOnKeyListener true

                }

            }

            // Handles changing the users email address
            R.id.changeEmailButton -> {

                if (changeEmailEditText.visibility == EditText.GONE) {

                    changeEmailEditText.visibility = EditText.VISIBLE

                } else {

                    changeEmailEditText.visibility = EditText.GONE

                }

                if (changeDisplaynameEditText.visibility == EditText.VISIBLE) {

                    changeDisplaynameEditText.visibility = EditText.GONE
                    changeDisplaynameEditText.text.clear()

                }

                changeEmailEditText.setOnKeyListener { _, keyCode, event ->

                    val checkOne = keyCode == KeyEvent.KEYCODE_ENTER
                    val checkTwo = event.action == KeyEvent.ACTION_UP

                    val currentlyLoggedInUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
                    val currentUserInput = changeEmailEditText.text.toString().trim()

                    if (!checkOne && !checkTwo) { return@setOnKeyListener false }

                    if (!Patterns.EMAIL_ADDRESS.matcher(currentUserInput).matches()) {

                        changeEmailEditText.error = "Invalid e-mail address"
                        changeEmailEditText.requestFocus()

                        return@setOnKeyListener false

                    }

                    currentlyLoggedInUser!!.updateEmail(currentUserInput).addOnCompleteListener {

                        changeEmailEditText.visibility = EditText.GONE
                        changeEmailEditText.text.clear()

                        val changedData = hashMapOf(

                            "timestamp" to Timestamp.now()

                        )

                        getData()
                        db.collection("emailChanges").document(currentlyLoggedInUser.uid).set(changedData)

                    }

                    return@setOnKeyListener true

                }

            }

        }

    }

    // Gather all the user data and display it on the screen
    private fun getData() {

        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserDisplayname : String? = "N/A"
        var currentUserEmail : String?

        val displayNameVw = findViewById<TextView>(R.id.displayNameVw)
        val emailAddressVw = findViewById<TextView>(R.id.emailVw)

        currentUser.let {

            if (currentUser!!.displayName!!.isNotEmpty()) { currentUserDisplayname = currentUser.displayName }
            currentUserEmail = currentUser.email

        }

        displayNameVw.text = currentUserDisplayname
        displayNameVw.invalidate()

        emailAddressVw.text = currentUserEmail
        emailAddressVw.invalidate()

    }

}