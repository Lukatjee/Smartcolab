package eu.lukatjee.smartcolab

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class Landing : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?){

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)
        intent.putExtra("FROM_ACTIVITY", "NONE")

        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener(this)

        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener(this)

    }

    public override fun onStart() {

        super.onStart()

        if (FirebaseAuth.getInstance().currentUser != null) {

            intent = Intent(this, Profile::class.java)
            intent.putExtra("FROM_ACTIVITY", "NONE")
            startActivity(intent)

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
