package com.flint;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    SignInButton gplus;
    GoogleApiClient mGoogleApiClient;
    AutoCompleteTextView user_name, phone_number;

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

                } else if (phone_number.getText().toString().equals("")) {
                    phone_number.setError("Please enter your email");
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
                String name = acct.getDisplayName();
                String email = acct.getEmail();
                String id = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                String profile_pic = personPhoto.toString();
                Log.d("Testing values", name + " " + email + " " + id + " " + profile_pic);
            } catch (NullPointerException e) {
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
