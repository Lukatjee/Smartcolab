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
        val changeDisplaynameButton = findViewById<TextView>(R.id.changeDpBtn)
        changeDisplaynameButton.setOnClickListener(this)

        // Initialize the button to change the user's email address
        val changeEmailButton = findViewById<TextView>(R.id.changeEmailBtn)
        changeEmailButton.setOnClickListener(this)

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

            R.id.changeDpBtn -> {

                if (changeDisplaynameEditText.visibility == EditText.GONE) {

                    changeDisplaynameEditText.visibility = EditText.VISIBLE

                } else {

                    changeDisplaynameEditText.visibility = EditText.GONE

                }

                if (changeEmailEditText.visibility == EditText.VISIBLE) {

                    changeEmailEditText.visibility = EditText.GONE; changeEmailEditText.text.clear()

                }

                changeDisplaynameEditText.setOnKeyListener { _, keyCode, event ->

                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser;
                        val userIpt = changeDisplaynameEditText.text.toString().trim()

                        if (userIpt.isEmpty()) {

                            changeDisplaynameEditText.error = "Invalid username"; changeDisplaynameEditText.requestFocus()

                        }

                        val profileUpdate = UserProfileChangeRequest.Builder().setDisplayName(userIpt).build()
                        user!!.updateProfile(profileUpdate).addOnCompleteListener {

                            changeDisplaynameEditText.visibility = EditText.GONE
                            changeDisplaynameEditText.text.clear()

                            val changedData = hashMapOf(

                                "timestamp" to Timestamp.now()

                            )

                            db.collection("usernameChanges").document(user.uid).set(changedData)

                        }

                    }

                }

            }
        }

                    R.id.changeEmailBtn -> {

                        if (changeDisplaynameEditText.visibility == EditText.GONE) {

                            changeDisplaynameEditText.visibility = EditText.VISIBLE

                        } else {

                            changeDisplaynameEditText.visibility = EditText.GONE

                        }

                        if (changeEmailEditText.visibility == EditText.VISIBLE) {

                            changeEmailEditText.visibility = EditText.GONE; changeEmailEditText.text.clear()

                        }

                        changeEmailEditText.setOnKeyListener { _, keyCode, event ->

                            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                                val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                                val userIpt = changeEmailEditText.text.toString().trim()

                                if (Patterns.EMAIL_ADDRESS.matcher(userIpt).matches()) {

                                    user!!.updateEmail(userIpt).addOnCompleteListener {

                                        changeEmailEditText.visibility = EditText.GONE
                                        changeEmailEditText.text.clear()

                                        val changedData = hashMapOf(

                                            "timestamp" to Timestamp.now()

                                        )

                                        getData()
                                        db.collection("emailChanges").document(user.uid)
                                            .set(changedData)

                                    }

                                    return@setOnKeyListener true

                                } else {

                                    changeEmailEditText.error = "Invalid e-mail address"
                                    changeEmailEditText.requestFocus()

                                    return@setOnKeyListener false

                                }

                            }

                            false

                        }

                }

    private fun getData() {

        val user = FirebaseAuth.getInstance().currentUser

        var name : String? = "N/A"
        var email : String?

        user.let {

            if (user!!.displayName!!.isNotEmpty()) { name = user.displayName }
            email = user.email

        }

        val displayNameVw = findViewById<TextView>(R.id.displayNameVw)
        displayNameVw.text = name; displayNameVw.invalidate()

        val emailAddressVw = findViewById<TextView>(R.id.emailVw)
        emailAddressVw.text = email; emailAddressVw.invalidate()

    }

}