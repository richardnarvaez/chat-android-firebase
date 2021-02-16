package ec.richardnarvaez.chatf.activities;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.chat.Fragments.FragmentChatRoom;
// HEAD

import ec.richardnarvaez.chatf.chat.Constants.Constants;
import ec.richardnarvaez.chatf.Utils.FirebaseUtils;
//ed574b80d4953298f9d3c12ab8be11f71966c1f9

import android.os.Bundle;

public class ChatRoomActivity extends AppCompatActivity {

    Fragment fragment = null;
    FragmentChatRoom firstfragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransition;
    private String IdUserActive;
    private String IdFriendKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Bundle bundle=getIntent().getExtras();

        if(bundle!=null){
            IdFriendKey =bundle.getString(Constants.FRIEND_KEY);
        }else{
            finish();
        }

        IdUserActive = FirebaseUtils.getCurrentUserId();
        fragmentManager = getSupportFragmentManager();
        firstfragment = FragmentChatRoom.newInstance(IdUserActive, "", "", IdFriendKey);
        remplaceFragment(firstfragment);
    }
    private void remplaceFragment(Fragment newFragment) {
        if (fragment != null) {
            fragmentTransition = fragmentManager.beginTransaction();
            fragmentTransition.hide(fragment).commit();
        }
        try {
            fragment = newFragment;
            fragmentTransition = fragmentManager.beginTransaction();
            if (fragment.isAdded()) {
                fragmentTransition.show(fragment);
            } else {
             fragmentTransition.add(R.id.replaceFrameLayout, fragment);
                if (fragmentManager.getBackStackEntryCount() != 0) {
                    fragmentTransition.addToBackStack(null);
                }
            }
            fragmentTransition.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
