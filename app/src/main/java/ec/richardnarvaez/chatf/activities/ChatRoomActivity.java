package ec.richardnarvaez.chatf.activities;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.chat.FragmentChatRoom;
// HEAD
import ec.richardnarvaez.chatf.chat.constantes.Constantes;
import ec.richardnarvaez.chatf.chat.models.Author;

import ec.richardnarvaez.chatf.utils.FirebaseUtils;
//ed574b80d4953298f9d3c12ab8be11f71966c1f9

import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class ChatRoomActivity extends AppCompatActivity {

    Fragment fragment = null;
    FragmentChatRoom firstfragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransition;
// HEAD
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    //NavigationView navigationBottom;

    private String IdUsuarioActivo;
    private String IdUsuarioAchatear;

// ed574b80d4953298f9d3c12ab8be11f71966c1f9

    private String KeyReceptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Bundle bundle=getIntent().getExtras();

        //receptor del key para emparejar
// HEAD
        if(bundle!=null){
            KeyReceptor=bundle.getString("keyreceptor");
        }else{
            finish();
        }




        firstfragment = FragmentChatRoom.newInstance("Sala", "", "", "jskd823");

        if(bundle!=null){
            IdUsuarioAchatear=bundle.getString("keyreceptor");
        }else{
            finish();
        }
        IdUsuarioActivo = FirebaseUtils.getCurrentUserId();
        fragmentManager = getSupportFragmentManager();

        firstfragment = FragmentChatRoom.newInstance(IdUsuarioActivo, "", "", IdUsuarioAchatear);
//ed574b80d4953298f9d3c12ab8be11f71966c1f9

        remplaceFragment(firstfragment);

    }
    private void remplaceFragment(Fragment newFragment) {

        if (fragment != null) {
            fragmentTransition = fragmentManager.beginTransaction();
            fragmentTransition.hide(fragment).commit();
        }

        try {
            fragment = newFragment;

            /*if (fragment instanceof FragmentChatRoom) {
                navigationBottom.setVisibility(View.VISIBLE);
            } else {
                navigationBottom.setVisibility(View.GONE);
            }*/

            //Effect
            /*Fade enterFade = new Fade();
            enterFade.setStartDelay(MOVE_DEFAULT_TIME + FADE_DEFAULT_TIME);
            enterFade.setDuration(FADE_DEFAULT_TIME);
            fragment.setEnterTransition(enterFade);*/

            // Insert the fragment
            fragmentTransition = fragmentManager.beginTransaction();

            if (fragment.isAdded()) {
                fragmentTransition.show(fragment);
            } else {
                /*R.id.fragment_container*/
             fragmentTransition.add(R.id.remplaceframelayout, fragment);
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
