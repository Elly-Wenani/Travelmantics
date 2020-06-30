package com.example.travelmantics;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class FirebaseUtil {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil mFirebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    public static ArrayList<TravelDeal> mDeals;
    private static final int RC_SIGN_IN = 123;
    private static Activity caller;

    private FirebaseUtil() {
    }

    public static void openFbReference(String ref, final Activity callerActivity) {
        if (mFirebaseUtil == null) {
            mFirebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();

            mFirebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;

            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null){
                        FirebaseUtil.singIn();
                    }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back", Toast.LENGTH_SHORT).show();
                }
            };
        }
        mDeals = new ArrayList<TravelDeal>();
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    public static void singIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme_NoActionBar)
                        .setLogo(R.drawable.logo)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public static void detachListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
}
