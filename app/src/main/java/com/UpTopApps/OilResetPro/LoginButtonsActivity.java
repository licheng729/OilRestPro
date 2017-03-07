package com.UpTopApps.OilResetPro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;


public class LoginButtonsActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Button fbButton, gpButton, orpEmailButton;
    private SignInButton signInButton;
    private static final String TAG = "LoginButtonsActivity";
    private static final int RC_SIGN_IN = 9001;
    private String email, firstname, lastname, socialLoginId, socialType;
    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    LoginButtonsActivity context = LoginButtonsActivity.this;
    RequestQueue queue;
    String url = "http://oilresetproapi.sandboxserver.co.za/add-new-user";
    String url2 = "http://oilresetproapi.sandboxserver.co.za/authenticate-user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_buttons);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        queue = Volley.newRequestQueue(this);

        sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        fbButton = (Button) findViewById(R.id.fb);
        gpButton = (Button) findViewById(R.id.gp);
        orpEmailButton = (Button) findViewById(R.id.orp);
        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));
        callbackManager = CallbackManager.Factory.create();

        fbButton.setOnClickListener(this);
        gpButton.setOnClickListener(this);
        orpEmailButton.setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final String userId = loginResult.getAccessToken().getUserId();
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                try {

                                    email = object.getString("email");
                                    firstname = object.getString("first_name");
                                    lastname = object.getString("last_name");
                                    String msg = "Welcome "+firstname+" "+lastname+" you are awesome!!!";
                                  //  Toast.makeText(LoginButtonsActivity.this,msg,Toast.LENGTH_LONG).show();
                                   // startActivity(new Intent(LoginButtonsActivity.this, HomeActivity.class));

                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("name", firstname);
                                    params.put("surname", lastname);
                                    params.put("email", email);
                                    params.put("password", null);
                                    params.put("push_token", "push_token");
                                    params.put("platform", "ANDROID");
                                    params.put("social_type","FACEBOOK");
                                    params.put("social_token",userId);

                                    JSONObject parameters = new JSONObject(params);
                                    showProgressDialog();
                                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                                            (Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

                                                @Override
                                                public void onResponse(JSONObject response) {
                                                    // mTxtDisplay.setText("Response: " + response.toString());
                                                    hideProgressDialog();
                                                    String res = null;
                                                    try {
                                                        Log.d("Response: ",  response.toString());
                                                        res = response.getString("response");
                                                        if(Integer.parseInt(res) > 0){
                                                            editor.putBoolean(Constants.IS_LOGGED_IN, true);
                                                            editor.putString(Constants.AUTH_TOKEN, response.getString("auth_token"));
                                                            editor.putString(Constants.FIRST_NAME, firstname);
                                                            editor.putString(Constants.LAST_NAME, lastname);
                                                            editor.putString(Constants.USER_EMAIL, email);
                                                            editor.putBoolean(Constants.HAS_PAID, false);
                                                            editor.commit();
                                                           startActivity(new Intent(LoginButtonsActivity.this, HomeActivity.class));
                                                        }else {
                                                           Toast.makeText(LoginButtonsActivity.this,res,Toast.LENGTH_LONG).show();
                                                        }
                                                    }catch (JSONException ex){

                                                    }catch (NumberFormatException ex2){
                                                        if(res.equalsIgnoreCase("EMAIL ALREADY EXISTS")){
                                                            doLogin();
                                                        }else {
                                                            Toast.makeText(LoginButtonsActivity.this,res,Toast.LENGTH_LONG).show();
                                                        }

                                                    }

                                                }
                                            }, new Response.ErrorListener() {

                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    // TODO Auto-generated method stub
                                                    hideProgressDialog();

                                                }
                                            }
                                            );
                                    queue.add(jsObjRequest);
                                }catch (JSONException ex){

                                }

                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void goToEmailLogin(){
        Intent intent = new Intent(LoginButtonsActivity.this, EmailLoginActivity.class);
        startActivity(intent);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fb:
                loginButton.performClick();
                break;
            case R.id.gp:
                signInWithGoogle();
                break;
            case R.id.orp:
                goToEmailLogin();
                break;
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
          //  mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
          //  updateUI(true);

            String personName = acct.getDisplayName();
            firstname = acct.getGivenName();
            lastname = acct.getFamilyName();
            email = acct.getEmail();
            socialLoginId = acct.getId();
           // String msg = "Welcome "+firstname+" "+lastname+" you are awesome!!!";
           // Toast.makeText(LoginButtonsActivity.this,msg,Toast.LENGTH_LONG).show();
          //  startActivity(new Intent(LoginButtonsActivity.this, HomeActivity.class));

            Map<String, String> params = new HashMap<String, String>();
            params.put("name", firstname);
            params.put("surname", lastname);
            params.put("email", email);
            params.put("password", null);
            params.put("push_token", "push_token");
            params.put("platform", "ANDROID");
            params.put("social_type","FACEBOOK");
            params.put("social_token",socialLoginId);

            JSONObject parameters = new JSONObject(params);
            showProgressDialog();
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            // mTxtDisplay.setText("Response: " + response.toString());
                            hideProgressDialog();
                            String res = null;
                            try {
                                Log.d("Response: ",  response.toString());
                                res = response.getString("response");
                                if(Integer.parseInt(res) > 0){
                                    editor.putBoolean(Constants.IS_LOGGED_IN, true);
                                    editor.putString(Constants.AUTH_TOKEN, response.getString("auth_token"));
                                    editor.putString(Constants.FIRST_NAME, firstname);
                                    editor.putString(Constants.LAST_NAME, lastname);
                                    editor.putString(Constants.USER_EMAIL, email);
                                    editor.putBoolean(Constants.HAS_PAID, false);
                                    editor.commit();
                                   startActivity(new Intent(LoginButtonsActivity.this, HomeActivity.class));
                                }else {
                                     Toast.makeText(LoginButtonsActivity.this,res,Toast.LENGTH_LONG).show();
                                }
                            }catch (JSONException ex){

                            }catch (NumberFormatException ex2){
                                if(res.equalsIgnoreCase("EMAIL ALREADY EXISTS")){
                                    doLogin();
                                }else {
                                    Toast.makeText(LoginButtonsActivity.this,res,Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            hideProgressDialog();

                        }
                    }
                    );
            queue.add(jsObjRequest);

        } else {
            // Signed out, show unauthenticated UI.
           // updateUI(false);
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void doLogin(){
        Map<String, String> params2 = new HashMap<String, String>();
        params2.put("push_token", "push_token");
        params2.put("platform", "ANDROID");
        params2.put("social_login","fb_gp");
        params2.put("email", email);

        final String url = url2+"?push_token=push_token&platform=ANDROID&social_login=gb_gp&email="+email;

        showProgressDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // mTxtDisplay.setText("Response: " + response.toString());
                        hideProgressDialog();
                        String res = null;
                        try {
                            Log.d("Response: ",  response.toString());
                            res = response.getString("response");
//                            Log.d("Response: ",  res);
                            if(res.equals("success")){
                                editor.putBoolean(Constants.IS_LOGGED_IN, true);
                                editor.putString(Constants.AUTH_TOKEN, response.getString("auth_token"));
                                editor.putString(Constants.FIRST_NAME, firstname);
                                editor.putString(Constants.LAST_NAME, lastname);
                                editor.putString(Constants.USER_EMAIL, email);
                                if(response.getString("payment_status").equals("has_paid")){
                                    editor.putBoolean(Constants.HAS_PAID, true);
                                }else if(response.getString("payment_status").equals("has_not_paid")){
                                    editor.putBoolean(Constants.HAS_PAID, false);
                                }

                                editor.commit();
                               startActivity(new Intent(LoginButtonsActivity.this, HomeActivity.class));
                            }else {
                                 Toast.makeText(LoginButtonsActivity.this,res,Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException ex){

                        }catch (NumberFormatException ex2){
                                Toast.makeText(LoginButtonsActivity.this,res,Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        hideProgressDialog();

                    }
                }
                );
        queue.add(jsObjRequest);
    }
}