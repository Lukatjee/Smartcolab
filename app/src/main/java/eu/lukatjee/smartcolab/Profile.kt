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

        getData()

        val editUsernameBtn = findViewById<TextView>(R.id.changeDpBtn); editUsernameBtn.setOnClickListener(this)
        val editEmailBtn = findViewById<TextView>(R.id.changeEmailBtn); editEmailBtn.setOnClickListener(this)

    }

    override fun onBackPressed() {

        super.onBackPressed()
        FirebaseAuth.getInstance().signOut()

    }

    override fun onClick(v: View?) {

        val editDpFld = findViewById<EditText>(R.id.editUsernameField)
        val editEmFld = findViewById<EditText>(R.id.editEmailField)

        when (v?.id) {

            R.id.changeDpBtn -> {

                when (editDpFld.visibility) {

                    EditText.GONE -> { editDpFld.visibility = EditText.VISIBLE; when (editEmFld.visibility) { EditText.VISIBLE -> { editEmFld.visibility = EditText.GONE; editEmFld.text.clear() }}}
                    else -> editDpFld.visibility = EditText.GONE

                }

                editDpFld.setOnKeyListener { _, keyCode, event ->

                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser; val userIpt = editDpFld.text.toString().trim()

                        if (userIpt.isNotEmpty()) {

                            val profileUpdate = UserProfileChangeRequest.Builder().setDisplayName(userIpt).build()
                            user!!.updateProfile(profileUpdate).addOnCompleteListener {

                                editDpFld.visibility = EditText.GONE
                                editDpFld.text.clear()

                                val changedData = hashMapOf(

                                    "timestamp" to Timestamp.now()

                                )

                                getData()
                                db.collection("usernameChanges").document(user.uid).set(changedData)

                            }

                            return@setOnKeyListener true

                        } else {

                            editDpFld.error = "Invalid username"
                            editDpFld.requestFocus()

                            return@setOnKeyListener false

                        }

                    }

                    false

                }

            }

            R.id.changeEmailBtn -> {

                when (editEmFld.visibility) {

                    EditText.GONE -> { editEmFld.visibility = EditText.VISIBLE; when (editDpFld.visibility) { EditText.VISIBLE -> { editDpFld.visibility = EditText.GONE; editDpFld.text.clear() }}}
                    else -> editEmFld.visibility = EditText.GONE

                }

                editEmFld.setOnKeyListener { _, keyCode, event ->

                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

                        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                        val userIpt = editEmFld.text.toString().trim()

                        if (Patterns.EMAIL_ADDRESS.matcher(userIpt).matches()) {

                            user!!.updateEmail(userIpt).addOnCompleteListener {

                                editEmFld.visibility = EditText.GONE
                                editEmFld.text.clear()

                                val changedData = hashMapOf(

                                    "timestamp" to Timestamp.now()

                                )

                                getData()
                                db.collection("emailChanges").document(user.uid).set(changedData)

                            }

                            return@setOnKeyListener true

                        } else {

                            editEmFld.error = "Invalid e-mail address"
                            editEmFld.requestFocus()

                            return@setOnKeyListener false

                        }

                    }

                    false

                }

            }

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