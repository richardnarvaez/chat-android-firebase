package ec.richardnarvaez.chatf.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.utils.FirebaseUtils;
import ec.richardnarvaez.chatf.utils.GlideUtils;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    FirebaseAuth mAuth;
    String TAG = this.getClass().getSimpleName();
    ProgressDialog progress;
    RadioButton rb_customer, rb_business;

    private DatabaseReference mDatabase;
    FirebaseDatabase database;
    FirebaseUser user;

    private CallbackManager callbackManager;
    FirebaseAuth.AuthStateListener fireBaseAuthListener;

    LoginButton facebook;
    Button google;
    int GOOGLE_CODE = 777;
    GoogleApiClient googleApiClient;

    boolean ema;
    private String GOOGLE = "google";
    private String FACEBOOK = "facebook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Utils.printHashKey(LoginActivity.this);

        ImageView backImage = findViewById(R.id.backImage);
        progress = new ProgressDialog(this);
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show();
        GlideUtils.loadImageWAlert(progress, "https://images.unsplash.com/photo-1515578706925-0dc1a7bfc8cb?ixlib=rb-0.3.5&s=4417d7814ece7de7a084a0a60872fa33&auto=format&fit=crop&w=882&q=80", backImage, LoginActivity.this);

        mAuth = FirebaseAuth.getInstance();
        fireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    FirebaseUtils.newUser();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "No estas logueado :(", Toast.LENGTH_SHORT).show();
                }
            }
        };

        final LinearLayout rsocial = (LinearLayout) findViewById(R.id.bt_social);
        final LinearLayout remail = (LinearLayout) findViewById(R.id.et_email);


        Button btEmail = findViewById(R.id.login_email);
        btEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ema) {
                    ema = false;
                } else {
                    ema = true;
                }
                remail.setVisibility(ema ? View.VISIBLE : View.GONE);
                rsocial.setVisibility(ema ? View.GONE : View.VISIBLE);
            }
        });


        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        database = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Buttons//
        facebook = findViewById(R.id.login_facebook);
        loginFacebook();
        Button loginFacebook = findViewById(R.id.login_facebook_per);
        loginFacebook.setOnClickListener(v -> facebook.performClick());

        google = findViewById(R.id.login_google);
        google.setOnClickListener(v -> {
            Intent i = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(i, GOOGLE_CODE);
        });


        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
    }

    private void loginFacebook() {
        facebook.setReadPermissions("email", "public_profile");
        //Use page admin.
        //facebook.setReadPermissions("pages", "manage_pages");
        facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Sucess Facebook Login");
                final AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                credentialAuth(FACEBOOK, credential);
            }

            @Override
            public void onCancel() {
                if (progress != null) {
                    progress.dismiss();
                }
                Log.d(TAG, "Facebook: Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Facebook: Error");
            }
        });
    }

    void signin(String email, String password) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        credentialAuth("Email", credential);
    }


    private void credentialAuth(final String type, final AuthCredential credential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(LoginActivity.this, task -> {

            if (!task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Ocurrio un error al iniciar sesión con " + type, Toast.LENGTH_SHORT).show();
                if (type.equals(FACEBOOK)) {
                    new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, graphResponse -> LoginManager.getInstance().logOut()).executeAsync();
                } else if (type.equals(GOOGLE)) {
                    FirebaseUtils.logOut(LoginActivity.this, googleApiClient);
                }
            }

            if (progress != null) {
                progress.dismiss();
            }

        });
    }

    private void LogInFilter() {
        FirebaseUser user = mAuth.getCurrentUser();
        Log.e("USER", "User: " + user.getUid());
        if (user != null) {
            String uid = user.getUid();
            mDatabase.child("users").child(uid).child("type")
                    .child("business").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean type = dataSnapshot.getValue(Boolean.class);
                            Log.e(TAG, "Type: " + type);
                            if (type) {
                                finish();
                            } else {
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });

            Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

        }
    }

    void createUser(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            progress.dismiss();
                        } else {
                            Log.e(TAG, "Creando usuario");
                            AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                            credentialAuth("Nefty", credential);
                        }
                    }
                });
    }

    private void createNewUser(Task<AuthResult> task, final String name, final String email, final String pass) {

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            Log.e(TAG, "User" + user.getDisplayName());
            Log.e(TAG, "Email" + user.getEmail());
            Log.e(TAG, "photoUrl" + user.getPhotoUrl());

            final String uid = user.getUid();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse("https://lh3.googleusercontent.com/-efl5pzCHx5k/AAAAAAAAAAI/AAAAAAAAAAA/Gl9qS3-a0tc/photo.jpg"))
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User profile updated.");
                                if (rb_business.isChecked()) {
                                    mDatabase.child("users").child(uid).child("type").child("business").setValue(true);
                                    Log.e(TAG, "User Business");
                                } else if (rb_customer.isChecked()) {
                                    mDatabase.child("users").child(uid).child("type").child("business").setValue(false);
                                    Log.e(TAG, "User Customer");
                                }

                                signin(email, pass);
                            }
                        }
                    });
            progress.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(fireBaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(fireBaseAuthListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            progress.show();
            resultGoogle(result);
        }
    }

    private void resultGoogle(GoogleSignInResult result) {
        if (!result.isSuccess()) {
            progress.dismiss();
            Toast.makeText(LoginActivity.this, "No success, retry", Toast.LENGTH_SHORT).show();
        } else {
            AuthCredential credential = GoogleAuthProvider.getCredential(result.getSignInAccount().getIdToken(), null);
            credentialAuth(GOOGLE, credential);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //
    }
}

