package eu.lukatjee.smartcolab

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import io.paperdb.Paper


class Landing : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?){

        super.onCreate(savedInstanceState)

        Paper.init(this)

        setContentView(R.layout.activity_landing)
        intent.putExtra("FROM_ACTIVITY", "NONE")

        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener(this)

        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener(this)


    }

    public override fun onStart() {

        super.onStart()

        val userData = Paper.book().read("userData", hashMapOf<String, String>())

        if (userData.isNotEmpty()) {

            val userDataKey = userData.keys.first()
            val userDataPassword = userData[userDataKey] ?: return

            FirebaseAuth.getInstance().signInWithEmailAndPassword(userDataKey, userDataPassword).addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {

                        intent = Intent(this, Profile::class.java)
                        intent.putExtra("FROM_ACTIVITY", "NONE")

                        startActivity(intent)

                    } else {

                        Toast.makeText(this, "Saved credentials did not match an existing account", Toast.LENGTH_LONG).show()

                    }

                }

        }

    }

    override fun onBackPressed() {

        super.onBackPressed()

        when (intent.extras!!.getString("FROM_ACTIVITY")) {

            "NONE" -> this.finishAffinity()

        }

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.signInButton -> {

                intent = Intent(this, Login::class.java)
                intent.putExtra("FROM_ACTIVITY", "LANDING")
                startActivity(intent)

            }

        }

    }

}
