package ec.richardnarvaez.chatf.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.activities.LoginActivity;
import ec.richardnarvaez.chatf.activities.MainActivity;
import ec.richardnarvaez.chatf.chat.Constants.Constants;
import ec.richardnarvaez.chatf.chat.models.Author;


/**
 * Created by RICHARD on 07/04/2017.
 */

public class FirebaseUtils {

    private static String postsPath;
    private static String TAG = "FirebaseUtil";

    public static DatabaseReference getBaseRef() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static StorageReference getBaseStorageRef() {
        return FirebaseStorage.getInstance().getReference();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    public static boolean isLogin() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    //todos los datos del usuario logueado
    public static Author getAuthorMain() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return null;
        return new Author(user.getEmail(), user.getDisplayName(), user.getPhotoUrl().toString(), true, 0, user.getUid(), user.getDisplayName(), false, true);
//        return new Author(user.getDisplayName(), user.getPhotoUrl().toString(), user.getEmail(), user.getUid(), "", "");
    }

    public static DatabaseReference getAuthorAccount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return null;

        return FirebaseUtils.getPeopleRef()
                .child(user.getUid());

    }

    public static DatabaseReference getCurrentUserRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return getBaseRef().child("users").child(getCurrentUserId());
        }
        return null;
    }

    public static DatabaseReference getCurrentUserAuthorRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return getBaseRef().child("users").child(getCurrentUserId()).child("author");
        }
        return null;
    }

    public static StorageReference getCurrentUserStorageRef() {
        String uid = getCurrentUserId();
        if (uid != null) {
            return getBaseStorageRef().child("users").child(getCurrentUserId());
        }
        return null;
    }

    public static DatabaseReference getOtherPeopleRef(String uid) {
        return getBaseRef().child("users").child(uid);
    }

    public static DatabaseReference getPeopleRef() {
        return getBaseRef().child("users");
    }

    public static DatabaseReference getFeedRef() {
        return getBaseRef().child("feed");
    }

    public static DatabaseReference getPostsRef() {
        return getBaseRef().child("posts");
    }

    public static DatabaseReference getLikesRef() {
        return getBaseRef().child("likes");
    }

    public static DatabaseReference getProductSaleRef() {
        return getBaseRef().child("product_sale");
    }

    public static DatabaseReference getFollowersRef() {
        return getBaseRef().child("followers");
    }

    public static DatabaseReference getCommentsRef() {
        return getBaseRef().child("chats");
    }

    public static String getPeoplePath() {
        return "users/";
    }

    public static String getPostsPath() {
        return "posts/";
    }

    public static void isfollow(final Context context, final String mUserId) {

        final String currentUserId = getCurrentUserId();

        assert currentUserId != null;
        getPeopleRef().child(currentUserId).child("following").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> updatedUserData = new HashMap<>();

                if (dataSnapshot.exists()) {
                    updatedUserData.put("users/" + currentUserId + "/following/" + mUserId, null);
                    updatedUserData.put("followers/" + mUserId + "/" + currentUserId, null);
                    FirebaseUtils.getFeedRef().child(FirebaseUtils.getCurrentUserId()).setValue(null);
                } else {
                    updatedUserData.put("followers/" + mUserId + "/" + currentUserId, true);
                    updatedUserData.put("users/" + currentUserId + "/following/" + mUserId, true);
                }

                FirebaseUtils.getBaseRef().updateChildren(updatedUserData, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            Toast.makeText(context, R.string.follow_user_error, Toast.LENGTH_LONG).show();
                            Log.d(TAG, context.getString(R.string.follow_user_error) + "\n" +
                                    firebaseError.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }

    public static void follow(final Context context, final String mUserId) {

        final String currentUserId = getCurrentUserId();

        if (getCurrentUserId() == null) {
            Toast.makeText(context, "You need to sign in to follow someone.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        assert currentUserId != null;
        getPeopleRef().child(currentUserId).child("following").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> updatedUserData = new HashMap<>();

                if (dataSnapshot.exists()) {
                    updatedUserData.put("users/" + currentUserId + "/following/" + mUserId, null);
                    updatedUserData.put("followers/" + mUserId + "/" + currentUserId, null);
                    FirebaseUtils.getFeedRef().child(FirebaseUtils.getCurrentUserId()).setValue(null);
                } else {
                    updatedUserData.put("followers/" + mUserId + "/" + currentUserId, true);
                    updatedUserData.put("users/" + currentUserId + "/following/" + mUserId, true);
                }

                FirebaseUtils.getBaseRef().updateChildren(updatedUserData, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            Toast.makeText(context, R.string.follow_user_error, Toast.LENGTH_LONG).show();
                            Log.d(TAG, context.getString(R.string.follow_user_error) + "\n" +
                                    firebaseError.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

    }

    public static void toggleLikes(final String postKey) {
        final String userKey = getCurrentUserId();
        final DatabaseReference postLikesRef = getLikesRef();
        postLikesRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long likes = dataSnapshot.getChildrenCount();
                if (dataSnapshot.child(userKey).exists()) {
                    postLikesRef.child(postKey).child(userKey).removeValue();
                    FirebaseUtils.getPostsRef().child(postKey).child("likes").setValue(likes - 1);
                } else {
                    postLikesRef.child(postKey).child(userKey).setValue(ServerValue.TIMESTAMP);
                    FirebaseUtils.getPostsRef().child(postKey).child("likes").setValue(likes + 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError firebaseError) {

            }
        });
    }

    public static void toggleSave(final String postKey) {
        final DatabaseReference postLikesRef = FirebaseUtils.getCurrentUserRef();
        postLikesRef.child("save").child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    postLikesRef.child("save").child(postKey).removeValue();
                } else {
                    postLikesRef.child("save").child(postKey).setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    //crea nuevo usuario con cambios
    public static void newUser(String onlyname, String userName, boolean checked) {

        Author autor = FirebaseUtils.getAuthorMain();
        DatabaseReference mDatabase = FirebaseUtils.getCurrentUserRef().child("author");

        mDatabase.child("name").setValue(onlyname);
        assert autor != null;
        mDatabase.child("profile_picture").setValue(autor.getProfile_picture());
        mDatabase.child("email").setValue(autor.getEmail());
        mDatabase.child("type").setValue(0);
        mDatabase.child("verified").setValue(false);
        mDatabase.child("user_name").setValue(userName.replace(" ", ""));
        mDatabase.child("sex").setValue(checked);
        mDatabase.child("uid").setValue(autor.getUid());
        mDatabase.child("following").child(autor.getUid()).setValue(System.currentTimeMillis());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(onlyname)
                //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated.");
                            // dialog.hide();
                        }
                    }
                });

    }

    //nuevo usuario por defecto
    public static void newUser() {

        Author autor = FirebaseUtils.getAuthorMain();
        DatabaseReference usersRef = FirebaseUtils.getCurrentUserRef();

        if (autor != null) {
            HashMap<String, Object> val = new HashMap<>();
            val.put("name", autor.getName());
            val.put("profile_picture", autor.getProfile_picture());
            val.put("email", autor.getEmail());
            val.put("type", 0);
            val.put("verified", false);
            val.put("user_name", "user_name");
            val.put("sex", true);
            val.put("uid", autor.getUid());

            assert usersRef != null;
            usersRef.child("author").setValue(val);
        } else {
            Log.e("TAG", "Iniciando sesion...");
        }

    }

    //salir de sesión
    public static void logOut(final Activity ctx, GoogleApiClient mGoogleApiClient) {

        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        ctx.finish();
                        ctx.startActivity(new Intent(ctx, LoginActivity.class));
                    }
                });

    }


    public static void setLiveState(boolean state) {
        DatabaseReference userLastOnlineRef = getPeopleRef()
                .child(getCurrentUserId())
                .child(Constants.AUTHOR_DATABASE);
        userLastOnlineRef.child("is_connected").setValue(state);
        if (!state) {
            userLastOnlineRef.child("last_connection").setValue(ServerValue.TIMESTAMP);
        }
    }
}
