package ec.richardnarvaez.chatf.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.chat.Fragments.FragmentContact;
import ec.richardnarvaez.chatf.utils.FirebaseUtils;

public class ContactsActivity extends AppCompatActivity {

    Fragment fragment = null;
    Fragment firstfragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransition;

    //USUARIOS
    private String IdUsuarioActivo;
    private String IdUsuarioAchatear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = findViewById(R.id.toolBarContacts);
        setSupportActionBar(toolbar);
        Bundle bundle=getIntent().getExtras();
        IdUsuarioActivo = FirebaseUtils.getCurrentUserId();
        fragmentManager = getSupportFragmentManager();
        firstfragment = FragmentContact.newInstance();
        remplaceFragment(firstfragment);
    }
    @Override
    public void onStart(){
        super.onStart();
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
}
