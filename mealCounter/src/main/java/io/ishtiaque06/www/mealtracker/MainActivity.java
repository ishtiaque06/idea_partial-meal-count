package io.ishtiaque06.www.mealtracker;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.FirebaseDatabase;

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
        Log.d(TAG, "onCreate");
        mSharedViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean firebasePersistence = sharedPref.getBoolean("firebasePersistence", false);
        if (!firebasePersistence) {
            mSharedViewModel.getDatabase().setPersistenceEnabled(true);
            mSharedViewModel.setUserDatabase();
            editor.putBoolean("firebasePersistence", true);
            editor.apply();
        }
        mSharedViewModel.setUserDatabase();
        mController = Navigation.findNavController(this, R.id.fragment);
        checkUserLogin(mSharedViewModel.getAuth());
    }

    public void checkUserLogin(FirebaseAuth auth) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            mSharedViewModel.checkDatabaseUser(currentUser);
        }
        else {
            mController.navigate(R.id.loginFragment);
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (mController.getCurrentDestination().getId() == R.id.loginFragment) {
            moveTaskToBack(true);
            finish();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        int RC_SIGN_IN = 23;
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "Google Sign In attempt happening.");
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                mSharedViewModel.googleSignIn(account)
                        .addOnCompleteListener(
                        new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Google Sign in succeeded");
                                    Log.d(TAG, String.valueOf(mSharedViewModel.getAuth().getCurrentUser() instanceof FirebaseUser));
                                    mSharedViewModel.setUserDatabase();
                                    mSharedViewModel.setUser(mSharedViewModel.getAuth().getCurrentUser());
                                    mSharedViewModel.checkDatabaseUser(mSharedViewModel.getUser());
                                    mController.navigate(R.id.homePageFragment);
                                } else {
                                    Log.d(TAG, "Google failed", task.getException());
                                    Toast.makeText(getApplicationContext(), "Failed! Make sure you have " +
                                                    "internet and a Google Account in this device.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed with Google:", e);
                Toast.makeText(getApplicationContext(), "Failed Google Auth! " +
                                "Make sure you have " +
                                "Internet and a Google Account in this device.",
                        Toast.LENGTH_LONG).show();
            }
        }
        else {
            Log.d(TAG, "Code is wrong! Got code: " + requestCode);
        }
    }
}
