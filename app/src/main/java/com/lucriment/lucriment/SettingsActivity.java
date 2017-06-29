package com.lucriment.lucriment;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    private Button profileButton, logoutButton;
    private String userType;
    private UserInfo userInfo;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //GET INTENTS
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        firebaseAuth = FirebaseAuth.getInstance();

        //INITIALIZE BOTTOM NAVIGATION VIEW
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
        }
        menu.findItem(getNavigationMenuItemId()).setChecked(true);

        //INITIALIZE BUTTONS
        profileButton = (Button) findViewById(R.id.profileButton);
        logoutButton = (Button) findViewById(R.id.logout);


        // SET LISTENERS
        profileButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

    }

    @Override
    int getContentViewId() {
        return R.layout.activity_settings;
    }

    @Override
    int getNavigationMenuItemId() {
        return R.id.profile;
    }

    @Override
    String getUserType() {
        return userType;
    }

    @Override
    UserInfo getUserInformation() {
        return userInfo;
    }

    @Override
    public void onClick(View v) {
        if(v == profileButton){
            Intent i = new Intent(SettingsActivity.this, MyProfileActivity.class);
            i.putExtra("userType", userType);
            i.putExtra("userInfo",userInfo);
            startActivity(i);

        }
        if(v == logoutButton){
            firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
