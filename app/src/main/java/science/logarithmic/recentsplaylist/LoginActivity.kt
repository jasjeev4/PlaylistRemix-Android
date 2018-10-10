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
import android.provider.Settings.System.getString
import android.util.Log
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.multimoon.colorful.CAppCompatActivity

import kotlinx.android.synthetic.main.activity_login.*

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

//const val EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE"

class LoginActivity : CAppCompatActivity(), LoaderCallbacks<Cursor> {

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

        // Check if result comes from the correct activity
        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, data)

//            Snackbar.make(login_activity_view, response.type.toString(), Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
            if(response.type == AuthenticationResponse.Type.TOKEN) {
                val token = response.accessToken;
                val code = response.code;
                getRecents(token);
            }
            else {
                showProgress(false)
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

    private fun getRecents(token: String) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://primary-server-168620.appspot.com/?token=$token"

        Log.e("Making this request: ", url);

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    // Display the first 500 characters of the response string.
                    Log.e("The json: ", response)
                    showProgress(false)
                    showResult(response)
                },
                Response.ErrorListener { error ->
                    showProgress(false)
                    error.printStackTrace()
                    showError(error.toString())
                })

//        stringRequest.retryPolicy = DefaultRetryPolicy(
//                1200,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        // Add the request to the RequestQueue.
        queue.add(stringRequest)

    }

    private fun getRedirectUri(): Uri {
        return Uri.Builder()
                .scheme(getString(R.string.com_spotify_sdk_redirect_scheme))
                .authority(getString(R.string.com_spotify_sdk_redirect_host))
                .build()
    }

    private fun showResult(result: String?) {
        val intent = Intent(this, ScrollingActivity::class.java).apply {
            putExtra("result", result)
        }
        startActivity(intent)
    }

    private fun showError(error: CharSequence) {
        showProgress(false)
        Snackbar.make(login_activity_view, "We're having some trouble pulling data right now.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }
}
