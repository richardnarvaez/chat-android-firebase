package ec.richardnarvaez.chatf.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ec.richardnarvaez.chatf.MainActivity;
import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.utils.FirebaseUtils;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    private String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        Firebase();
    }


    private void Firebase() {

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (user != null) {
                            FirebaseUtils.getCurrentUserRef().child("author").child("user_name").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull final DataSnapshot snapshot) {

                                    if (!snapshot.exists()) {

                                        startActivity(new Intent(SplashActivity.this, LoginActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    } else {
                                        startActivity(new Intent(SplashActivity.this, MainActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    }

                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                }
                            });
                            //overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        } else {
                            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        }
                    }
                }, 1000 * 3);

                /*FirebaseUser user = firebaseAuth.getCurrentUser();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                if (user != null) {
                    String uid = user.getUid();
                    mDatabase.child("users").child(uid).child("type")
                            .child("business").addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //User user = dataSnapshot.getValue(User.class);
                                    type = dataSnapshot.getValue(Boolean.class);
                                    Log.e(TAG, "Type: " + type);
                                    if (type) {
                                        startActivity(new Intent(SplashActivity.this, CustomerActivity.class));
                                        finish();
                                    } else {
                                        startActivity(new Intent(SplashActivity.this, CustomerActivity.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                }
                            });

                    Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                    Log.e(TAG, "onAuthStateChanged:signed_out");
                }*/
            }
        };

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

}

