package ec.richardnarvaez.chatf.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.chat.Constants.Constants;
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
        setUserStatus();
    }


    private void Firebase() {

        mAuthListener = firebaseAuth -> {
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            new Handler().postDelayed(() -> {
                if (user != null) {

                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "getInstanceId failed", task.getException());
                                        return;
                                    }

                                    // Get new Instance ID token
                                    String token = task.getResult().getToken();
                                    FirebaseUtils.getCurrentUserAuthorRef().child("token_msg").setValue(token);
                                }
                            });

                    FirebaseUtils.getCurrentUserRef().child("author").child("user_name").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull final DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                Intent splashIntent = new Intent(SplashActivity.this, LoginActivity.class);
                                splashIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(splashIntent);
                            } else {
                                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(mainIntent);
                            }

                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                        }
                    });
                } else {
                    Intent intentLogin = new Intent(SplashActivity.this, LoginActivity.class);
                    intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentLogin);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            }, 1000 * 3);
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


    public void setUserStatus() {

        if (FirebaseUtils.getCurrentUserId() != null) {

            final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);

                    Toast.makeText(SplashActivity.this, "" + connected, Toast.LENGTH_SHORT).show();
                    if (connected) {
                        DatabaseReference userLastOnlineRef = FirebaseUtils.getPeopleRef()
                                .child(FirebaseUtils.getCurrentUserId())
                                .child(Constants.AUTHOR_DATABASE);
                        Toast.makeText(SplashActivity.this, "Se conecto", Toast.LENGTH_SHORT).show();
                        userLastOnlineRef.child("is_connected").setValue(true);
                        userLastOnlineRef.child("is_connected").onDisconnect().setValue(false);
                        userLastOnlineRef.child("last_connection").onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.w(TAG, "Listener was cancelled at .info/connected");
                }
            });
        }
    }

}

