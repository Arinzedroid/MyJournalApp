package tech.arinzedroid.myjournal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isUserAlreadySignedIn();
    }

    private void isUserAlreadySignedIn(){
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account == null){
            //User not signedIn. Start signIn process
            startActivity(new Intent(this,LogInActivity.class));
        }else{
            //user already signedIn. Allow access to user
            startActivity(new Intent(this,MainActivity.class));
        }
        finish();
    }
}
