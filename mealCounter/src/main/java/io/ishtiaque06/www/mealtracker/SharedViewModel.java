package io.ishtiaque06.www.mealtracker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SharedViewModel extends ViewModel {
    final String TAG = "SharedViewModel";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mUserDatabase;
    private boolean mPersistenceSettings = false;

    public FirebaseDatabase getDatabase() {
        return mDatabase;
    }

    public FirebaseUser getUser() {
        return mUser;
    }

    public void setUser(FirebaseUser user) {
        mUser = user;
    }

    public DatabaseReference setUserDatabase() {
        return mUserDatabase = mDatabase.getReference().child("users");
    }

    private MutableLiveData<Object> mCurrentMealCount = new MutableLiveData<>();

    public MutableLiveData<Object> getCurrentMealCount() {
        return mCurrentMealCount;
    }

    private ValueEventListener mealChangeListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                mCurrentMealCount.setValue(dataSnapshot.getValue());
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public ValueEventListener getMealChangeListener() {
        return mealChangeListener;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    Task googleSignIn(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        return mAuth.signInWithCredential(credential);
    }

    void checkDatabaseUser(final FirebaseUser user) {
        Log.d(TAG, "here!");
        mUserDatabase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    mUserDatabase.child(user.getUid()).child("name").setValue(user.getDisplayName());
                    mUserDatabase.child(user.getUid()).child("email").setValue(user.getEmail());
                    mUserDatabase.child(user.getUid()).child("mealCount").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Can't create new user table: ", databaseError.toException());
            }
        });
    }

    public void swipeMeal() {
        Long current = (Long) getCurrentMealCount().getValue();
        mUserDatabase.child(mUser.getUid()).child("mealCount").setValue(--current);
    }

    public DatabaseReference getMealCountfromDatabase() {
        return mUserDatabase.child(mUser.getUid()).child("mealCount");
    }

    public void updateMealCount(Long newCount) {
        if (newCount > 0) {
            getMealCountfromDatabase().setValue(newCount);
        }
    }
}

