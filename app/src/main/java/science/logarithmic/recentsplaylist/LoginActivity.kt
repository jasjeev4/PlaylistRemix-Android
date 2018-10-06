package science.logarithmic.recentsplaylist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList
import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.widget.Toast
import io.multimoon.colorful.CAppCompatActivity

import kotlinx.android.synthetic.main.activity_login.*

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import science.logarithmic.recentsplaylist.R.layout.activity_login

/**
 * A login screen that offers login via email/password.
 *
 */

const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"

class LoginActivity : CAppCompatActivity(), LoaderCallbacks<Cursor> {
    private val mOkHttpClient = OkHttpClient()
    private var mAccessToken: String? = null
    private var mAccessCode: String? = null
    private val mCall: Call? = null

    val CLIENT_ID = "bac34290c1f0480f9a21b4aab5e2c544"
    val AUTH_TOKEN_REQUEST_CODE = 16
    val AUTH_CODE_REQUEST_CODE = 17

    private var currentViewId: Int? = null

    fun setCurrentViewById(id: Int) {
        currentViewId = id
    }

    fun getCurrentViewById(): Int? {
        return currentViewId
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Cursor> {
        TODO(reason = "not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentViewById(R.layout.activity_login)
        setContentView(R.layout.activity_login)
        // Login button


        email_sign_in_button.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View) {
                spotifyLogin()
            }
        })
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            email_sign_in_button.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    fun toast(message: CharSequence) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()



    fun spotifyLogin(){
        showProgress(true);
        val REDIRECT_URI = getRedirectUri().toString()

        val builder = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)

        builder.setScopes(arrayOf("user-read-private", "user-read-birthdate", "user-read-email", "app-remote-control", "playlist-read-private", "playlist-modify-private", "playlist-read-collaborative", "playlist-modify-public", "user-library-read", "user-library-modify", "user-top-read", "user-read-recently-played"))
        val request = builder.build()

        AuthenticationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        showProgress(false)
        // Check if result comes from the correct activity
        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, data)

            Snackbar.make(login_activity_view, response.type.toString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            if(response.type == AuthenticationResponse.Type.TOKEN) {
                var token = response.accessToken

                Snackbar.make(login_activity_view, token, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }
            else {
                Snackbar.make(login_activity_view, "Error logging in with Spotify", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }
        } // Handle successful response
            // Handle error response
            // Most likely auth flow was cancelled
            // Handle other cases
        else {
            Snackbar.make(login_activity_view, "Not request code", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    private fun getRedirectUri(): Uri {
        return Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build()
    }
}
