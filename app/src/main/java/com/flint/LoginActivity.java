package com.flint;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    String url = "http://84.200.84.218:3001/checkUserName";

    SignInButton gplus;
    GoogleApiClient mGoogleApiClient;
    AutoCompleteTextView user_name, phone_number;
    String name, email, id, profile_pic;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gplus = (SignInButton) findViewById(R.id.gplus_login);
        user_name = (AutoCompleteTextView) findViewById(R.id.userName);
        phone_number = (AutoCompleteTextView) findViewById(R.id.phoneNumber);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();
        mGoogleApiClient.connect();

        gplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_name.getText().toString().equals("")) {
                    user_name.setError("Please enter your name");
                    user_name.requestFocus();
                    return;

                } else if (phone_number.getText().toString().length() < 10 || phone_number.getText().toString().length() > 10) {
                    phone_number.setError("Please enter your phone number");
                    phone_number.requestFocus();
                    return;
                } else {

                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, 200);

                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("TAG", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            try {
                if (acct.getDisplayName() != null)
                    name = acct.getDisplayName();
                else
                    name = "not available";

                email = acct.getEmail();
                id = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                if (personPhoto != null)
                    profile_pic = personPhoto.toString();
                else
                    profile_pic = "NA";
                Log.d("Testing values", name + " " + email + " " + id + " " + profile_pic);
            } catch (NullPointerException e) {
                Log.d("Data missing", "Some data is missing");
            }

            ArrayList<ParamClass> parameteres = new ArrayList<>();


            ParamClass param = new ParamClass();
            param.paramName = "username";
            param.paramValue = user_name.getText().toString();


            parameteres.add(param);

            param.paramName = "emailid";
            param.paramValue = email;

            parameteres.add(param);


            String available = "";
            try {
                available = new CheckUser(url, parameteres).execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (available.equals("available")) {
                preferences = getSharedPreferences("SignIn", Context.MODE_PRIVATE);
                editor = preferences.edit();
                editor.putInt("LOGGED IN",1);
                editor.apply();
                Toast.makeText(LoginActivity.this, "Welcome to FLint!", Toast.LENGTH_SHORT).show();
            } else if (available.equals("taken")) {
                user_name.setError("Username already taken");
                user_name.requestFocus();
            } else {
                new AlertDialog.Builder(LoginActivity.this).setTitle("User already exists!")
                        .setMessage("This user already exists with the user name " + available + "\nContinue using Flint with the username " + available + "?")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                preferences = getSharedPreferences("SignIn", Context.MODE_PRIVATE);
                                editor = preferences.edit();
                                editor.putInt("LOGGED IN",1);
                                editor.apply();
                                Toast.makeText(LoginActivity.this, "Welcome back to FLint!", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }

        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(LoginActivity.this, 105);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
}
