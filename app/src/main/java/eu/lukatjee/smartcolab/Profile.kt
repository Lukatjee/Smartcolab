package eu.lukatjee.smartcolab

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import io.paperdb.Paper


class Profile : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val saveEditProfile = findViewById<Button>(R.id.editProfileBtn)
        saveEditProfile.setOnClickListener(this)

        val logoutButton = findViewById<Button>(R.id.logoutBtn)
        logoutButton.setOnClickListener(this)

        val profilePictureVw = findViewById<ImageView>(R.id.profilePictureVw)
        profilePictureVw.setOnClickListener(this)

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
    private val storageRef = FirebaseStorage.getInstance().reference

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

                val profileChangeRqst = UserProfileChangeRequest.Builder().setDisplayName(displaynameIpt).build()

                FirebaseAuth.getInstance().currentUser!!.updateProfile(profileChangeRqst).addOnCompleteListener(this){ task ->

                    if (task.isSuccessful) {

                        displaynameSv()

                    }

                }

                FirebaseAuth.getInstance().currentUser!!.updateEmail(emailIpt).addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        emailSv()

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

            R.id.profilePictureVw -> {

                var someActivityResultLauncher = registerForActivityResult(

                    StartActivityForResult(),
                    ActivityResultCallback { result ->

                        if (result.resultCode == Activity.RESULT_OK) {

                            val data = result.data

                        }
                    })

                val openGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                someActivityResultLauncher.launch(openGalleryIntent)

                val imageUrl = FirebaseAuth.getInstance().currentUser!!.photoUrl
                uploadImageToFirebase(imageUrl)

            }

        }

    }

    private fun uploadImageToFirebase(imageUrl : Uri?) {

        val fileRef = storageRef.child("profile.png")

        fileRef.putFile(imageUrl!!).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                Toast.makeText(this, "Successfully uploaded the image", Toast.LENGTH_LONG).show()

            } else {

                println("Oh no")

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

        val photoUrl = FirebaseAuth.getInstance().currentUser!!.photoUrl

    }

}
