package io.ishtiaque06.www.mealtracker;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private SharedViewModel mSharedViewModel;
    private NavController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
        mController = Navigation.findNavController(this, R.id.fragment);
        checkUserLogin(mSharedViewModel.getAuth());
    }

    @Override
    public void onStart() {
        super.onStart();
        checkUserLogin(mSharedViewModel.getAuth());
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUserLogin(mSharedViewModel.getAuth());
    }

    public void checkUserLogin(FirebaseAuth auth) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            mController.navigate(R.id.homePageFragment);
        }
        else {
            Log.d(TAG, "doesn't work.");
            mController.navigate(R.id.loginFragment);
        }
    }

    @Override
    public void onBackPressed() {
        if (mController.getCurrentDestination().getId() == R.id.loginFragment) {
            moveTaskToBack(true);
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Here!");
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        int RC_SIGN_IN = 23;
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                mSharedViewModel.googleSignIn(account).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Google Sign in succeeded");
                            mController.navigate(R.id.homePageFragment);
                        }
                        else {
                            Log.d(TAG, "Google failed", task.getException());
                            Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
        else {
            Log.d(TAG, "Code is:" + requestCode);
        }
    }
}
