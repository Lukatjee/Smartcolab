package eu.lukatjee.smartcolab

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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

        val changeDisplaynameButton = findViewById<TextView>(R.id.changeDisplaynameButton)
        changeDisplaynameButton.setOnClickListener(this)

        val changeEmailButton = findViewById<TextView>(R.id.changeEmailButton)
        changeEmailButton.setOnClickListener(this)

        val logoutButton = findViewById<ImageButton>(R.id.logoutButton)
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

    override fun onClick(v: View?) {

        val changeDisplaynameEditText = findViewById<EditText>(R.id.editUsernameField)
        val changeEmailEditText = findViewById<EditText>(R.id.editEmailField)

        when (v?.id) {

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

                    val currentlyLoggedInUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
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

                changeEmailEditText.setOnKeyListener { _, keyCode, _ ->

                    val currentlyLoggedInUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
                    val currentUserInput = changeEmailEditText.text.toString().trim()

                    if (keyCode == KeyEvent.KEYCODE_ENTER) {

                        if (!(Patterns.EMAIL_ADDRESS.matcher(currentUserInput).matches())) {

                            changeEmailEditText.error = "Invalid e-mail address"
                            changeEmailEditText.requestFocus()

                            return@setOnKeyListener false

                        }

                        currentlyLoggedInUser!!.updateEmail(currentUserInput).addOnCompleteListener {

                            changeEmailEditText.visibility = EditText.GONE
                            changeEmailEditText.text.clear(); getData()

                            val changedData = hashMapOf(

                                "timestamp" to Timestamp.now()

                            )

                            db.collection("emailChanges").document(currentlyLoggedInUser.uid).set(changedData)

                        }

                        return@setOnKeyListener true

                    }

                    return@setOnKeyListener false

                }

            }

            R.id.logoutButton -> {

                FirebaseAuth.getInstance().signOut()

                intent = Intent(this, Landing::class.java)
                startActivity(intent)

            }

        }

    }

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