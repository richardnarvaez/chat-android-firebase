package ec.richardnarvaez.chatf.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import androidx.viewpager.widget.ViewPager;

import java.util.HashMap;
import java.util.Map;

import ec.richardnarvaez.chatf.R;
import ec.richardnarvaez.chatf.chat.adapters.TabsPagesAdapter;
import ec.richardnarvaez.chatf.chat.Constants.Constants;
import ec.richardnarvaez.chatf.Utils.FirebaseUtils;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String IdUsuarioActivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        IdUsuarioActivo = FirebaseUtils.getCurrentUserId();
        setUserStatus(Constants.USER_CONNECTED);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = findViewById(R.id.tabs);

        TabsPagesAdapter tabsPagerAdapter = new TabsPagesAdapter(this, getSupportFragmentManager());
        ViewPager vistaP = findViewById(R.id.ContentScrol);
        vistaP.setAdapter(tabsPagerAdapter);
        tabLayout.setupWithViewPager(vistaP);


        FloatingActionButton contactsButton = findViewById(R.id.contactsButton);
        contactsButton.setOnClickListener((View view)->{
            Intent contactsIntent = new Intent(MainActivity.this,ContactsActivity.class);
            startActivity(contactsIntent);
        });
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStop() {

        super.onStop();
    }
    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        setUserStatus(Constants.USER_DISCONNECTED);
    }

    public void setUserStatus(Boolean status){
        DatabaseReference hopperRef = FirebaseUtils.getPeopleRef().child(IdUsuarioActivo).child(Constants.AUTHOR_DATABASE);
        Map<String, Object> hopperUpdates = new HashMap<>();
        hopperUpdates.put("is_connected", status);
        hopperRef.updateChildren(hopperUpdates);
    }
}
