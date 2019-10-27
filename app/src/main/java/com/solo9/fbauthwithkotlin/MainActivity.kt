package com.solo9.fbauthwithkotlin
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.*
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import java.util.*

class MainActivity : AppCompatActivity() {
    var TAG = "MainActivity"
    private var callbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callbackManager = CallbackManager.Factory.create()
        login_button.setReadPermissions(Arrays.asList<String>("email", "public_profile"))
        checkLoginStatus()
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {

            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {

            }
        })


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    internal var tokenTracker: AccessTokenTracker = object : AccessTokenTracker() {
        override fun onCurrentAccessTokenChanged(
            oldAccessToken: AccessToken?,
            currentAccessToken: AccessToken?
        ) {
            if (currentAccessToken == null) {
                profile_name.setText("")
                profile_email.setText("")
                profile_pic.setImageResource(0)
                Toast.makeText(this@MainActivity, "User Logged out", Toast.LENGTH_LONG).show()
            } else
                loadUserProfile(currentAccessToken)
        }
    }


    private fun loadUserProfile(newAccessToken: AccessToken?) {

        val request = GraphRequest.newMeRequest(
            newAccessToken
        ) { `object`, response ->
            try {
                val first_name = `object`.getString("first_name")
                val last_name = `object`.getString("last_name")
                val email = `object`.getString("email")
                val id = `object`.getString("id")
                val image_url = "https://graph.facebook.com/$id/picture?type=normal"
                Log.e(TAG, "---------facebook Data------>$first_name,$last_name,$email$id")
                profile_email.setText(email)
                profile_name.setText("$first_name $last_name")
                val requestOptions = RequestOptions()
                requestOptions.dontAnimate()
                Glide.with(this@MainActivity).load(image_url).into(profile_pic)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "first_name,last_name,email,id")
        request.parameters = parameters
        request.executeAsync()

    }

    private fun checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() != null) {
            loadUserProfile(AccessToken.getCurrentAccessToken())
        }
    }
}

