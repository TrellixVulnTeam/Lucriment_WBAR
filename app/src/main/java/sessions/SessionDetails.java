package sessions;

import android.content.Intent;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import misc.BottomNavHelper;
import students.Favourites;
import com.lucriment.lucriment.R;

import java.io.IOException;
import java.util.List;

import messaging.ViewMessagesActivity;
import students.UserInfo;
import tutors.MyProfileActivity;
import students.TutorListActivity;

public class SessionDetails extends AppCompatActivity implements OnMapReadyCallback {

    private TimeInterval ti;
    private TextView classLabel, nameLabel, name, timeInterval, location, nameTypeLabel;
    private String nameString;
    private String className;
    private String locationName;
    private String subject;
    private MapView map;
    private GoogleMap gMap;
    private String userType;
    private UserInfo userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);
        name = (TextView) findViewById(R.id.name3);
        location = (TextView) findViewById(R.id.location);
        classLabel = (TextView) findViewById(R.id.classtitle);
        timeInterval = (TextView) findViewById(R.id.time);
        nameTypeLabel = (TextView) findViewById(R.id.nameTypeLabel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // ActionBar actionBar = getActionBar();
       // actionBar.setDisplayHomeAsUpEnabled(true);
//        map = (MapView) findViewById(R.id.mapFragment);
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        if(getIntent().hasExtra("time")){
            ti = getIntent().getParcelableExtra("time");
        }
        if(getIntent().hasExtra("name")){
            nameString = getIntent().getStringExtra("name");
        }
        if(getIntent().hasExtra("location")){
            locationName = getIntent().getStringExtra("location");
        }
        if(getIntent().hasExtra("subject")){
            subject = getIntent().getStringExtra("subject");
        }

        if(userType.equals("Student")){
            nameTypeLabel.setText("Tutor");
        }
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.search) {
                    Intent y = new Intent(SessionDetails.this, TutorListActivity.class);
                    y.putExtra("userType", userType);
                    y.putExtra("userInfo",userInfo);
                    startActivity(y);
                }
                if (itemId == R.id.profile) {
                    Intent y = new Intent(SessionDetails.this, MyProfileActivity.class);
                    y.putExtra("userType", userType);
                    y.putExtra("userInfo",userInfo);
                    startActivity(y);
                }

                if (itemId == R.id.sessions) {
                    Intent y = new Intent(SessionDetails.this, SessionsActivity.class);
                    y.putExtra("userType", userType);
                    y.putExtra("userInfo",userInfo);
                    startActivity(y);
                    //this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                }
                if (itemId == R.id.inbox) {
                    Intent y = new Intent(SessionDetails.this, ViewMessagesActivity.class);
                    y.putExtra("userType", userType);
                    y.putExtra("userInfo",userInfo);
                    startActivity(y);
                }
                if(itemId == R.id.favourites){
                    Intent y = new Intent(SessionDetails.this, Favourites.class);
                    y.putExtra("userType", userType);
                    y.putExtra("userInfo",userInfo);
                    startActivity(y);

                }


                finish();
                return false;
            }
        });
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
        }
        menu.findItem(R.id.sessions).setChecked(true);



        classLabel.setText(subject);
        name.setText(nameString);
       timeInterval.setText(ti.returnFormattedDate());
        location.setText(locationName);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent y = new Intent(SessionDetails.this, SessionsActivity.class);
        y.putExtra("userType", userType);
        y.putExtra("userInfo",userInfo);
        startActivity(y);
        finish();
        return true;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Geocoder gc = new Geocoder(this);
        try {
            List<android.location.Address> list = gc.getFromLocationName(locationName,1);
            android.location.Address add = list.get(0);
            double lat = add.getLatitude();
            double lng = add.getLongitude();
            LatLng sydney = new LatLng(lat, lng);
            googleMap.addMarker(new MarkerOptions().position(sydney)
                    .title(locationName));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            googleMap.setMaxZoomPreference(23);
            googleMap.setMinZoomPreference(15);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
