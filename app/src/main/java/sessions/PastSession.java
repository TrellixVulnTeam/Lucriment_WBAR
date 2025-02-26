package sessions;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucriment.lucriment.BaseActivity;
import com.lucriment.lucriment.R;
import students.Rating;

import java.util.ArrayList;

import misc.BottomNavHelper;
import students.UserInfo;

public class PastSession extends BaseActivity implements View.OnClickListener {

    private TimeInterval ti;
    private TextView subjectWithField, sessionLengthField, dateField, locationField,reviewText;
    private String nameString;
    private String className;
    private String locationName;
    private String subjectWith, subject;
    private MapView map;
    private Review studentReview,tutorReview;
    private Button reviewButton;
    private RatingBar ratingBar, reviewBar;
    private EditText reviewField;
    private GoogleMap gMap;
    private boolean leavingReview = false;
    private String SessionID;
    private DatabaseReference databaseReference, databaseReference2, databaseReference3;
    ArrayList<SessionRequest> allSessions = new ArrayList<>();
    private SessionRequest thisSession;
    private String userType;
    private Rating currentRating;
    private ArrayList<Review> reviews = new ArrayList<>();
    private UserInfo userInfo;
    private ImageView imageView;
    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_session);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(false);
        }
        menu.findItem(getNavigationMenuItemId()).setChecked(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //INITIALIZE WIDGETS
        subjectWithField = (TextView) findViewById(R.id.subjectWithField);
        sessionLengthField = (TextView) findViewById(R.id.sessionLengthField);
        dateField = (TextView) findViewById(R.id.dateField);
        locationField = (TextView) findViewById(R.id.locationField);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        reviewField = (EditText) findViewById(R.id.reviewField);
        reviewButton = (Button) findViewById(R.id.reviewButton);
        reviewBar = (RatingBar) findViewById(R.id.reviewScore);
        reviewText = (TextView) findViewById(R.id.reviewText);
        imageView = (ImageView) findViewById(R.id.tutorView);
        //GET INTENTS
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        if(getIntent().hasExtra("sessionID")){
            SessionID = getIntent().getStringExtra("sessionID");
        }
        if(getIntent().hasExtra("studentReview")){
            studentReview = getIntent().getParcelableExtra("studentReview");
        }
        if(getIntent().hasExtra("tutorReview")){
            tutorReview = getIntent().getParcelableExtra("tutorReview");
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
        subjectWith = subject + " with " + nameString;

        subjectWithField.setText(subjectWith);
        dateField.setText(ti.returnFormattedDate());
        locationField.setText(locationName);
        sessionLengthField.setText(String.valueOf(ti.returnTimeInHours())+"hrs");
        if(userType.equals("Tutor")) {
            if (studentReview == null) {

                reviewButton.setVisibility(View.VISIBLE);
            }else{
                if(tutorReview!=null){
                reviewBar.isIndicator();
                reviewBar.setVisibility(View.VISIBLE);
                reviewBar.setRating((float) tutorReview.getRating());
                reviewText.setVisibility(View.VISIBLE);
                reviewText.setText(tutorReview.getText());}}
        }else{
            if(tutorReview==null){
                reviewButton.setVisibility(View.VISIBLE);
            }else{reviewBar.isIndicator();
                reviewBar.setVisibility(View.VISIBLE);
                reviewBar.setRating((float) tutorReview.getRating());
                reviewText.setVisibility(View.VISIBLE);
                reviewText.setText(tutorReview.getText());}
        }
        reviewButton.setOnClickListener(this);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("sessions").child(SessionID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allSessions.clear();
                for(DataSnapshot sessions:dataSnapshot.getChildren()){
                    allSessions.add(sessions.getValue(SessionRequest.class));

                }
                processSessions();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent y = new Intent(PastSession.this, SessionsActivity.class);
        y.putExtra("userType", userType);
        y.putExtra("userInfo",userInfo);
        startActivity(y);
        finish();
        return true;
    }

    @Override
    protected int getContentViewId() {
       return R.layout.activity_past_session;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.sessions;
    }

    @Override
    protected String getUserType() {
        return userType;
    }

    @Override
    protected UserInfo getUserInformation() {
        return userInfo;
    }

    //GET CURRENT SESSION
    private void processSessions(){
        for(SessionRequest s:allSessions){

            if(s.getTime().getFrom()==(ti.getFrom())){
               thisSession = s;
            }
        }
        //IF CURRENT USER IS A TUTOR, GET THE STUDENTS DATA SNAP, OTHERWISE GET TUTOR DATASNAP
        if(userType.equals("Tutor")) {
          databaseReference2 = FirebaseDatabase.getInstance().getReference().child("users").child(thisSession.getStudentId());
            databaseReference3 = FirebaseDatabase.getInstance().getReference().child("users").child(thisSession.getStudentId());


        }else{
          databaseReference2 = FirebaseDatabase.getInstance().getReference().child("users").child(thisSession.getTutorId());
            databaseReference3 = FirebaseDatabase.getInstance().getReference().child("tutors").child(thisSession.getTutorId());
        }
            databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                     currentRating = dataSnapshot.child("rating").getValue(Rating.class);
                    imagePath = dataSnapshot.child("profileImage").getValue(String.class);
                    Glide.with(getApplicationContext())
                            .load(imagePath)
                            .into(imageView);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        DataSnapshot reviewSnapshot = dataSnapshot.child("reviews");
                        for(DataSnapshot r:reviewSnapshot.getChildren()){
                            Review curRev = r.getValue(Review.class);
                            reviews.add(curRev);
                        }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if(v==reviewButton){
            if(leavingReview){
                leavingReview = false;
            }else{
                leavingReview = true;
            }
            if(leavingReview){
                ratingBar.setVisibility(View.VISIBLE);
                reviewField.setVisibility(View.VISIBLE);
            }
            else{
                java.util.Calendar cc = java.util.Calendar.getInstance();
                if(userType.equals("Tutor")) {
                    double rating = Double.valueOf(ratingBar.getRating());
                    Review review = new Review(thisSession.getTutorName(), rating, reviewField.getText().toString(), cc.getTimeInMillis(),userInfo.getId());
                    thisSession.setStudentReview(review);
                    reviews.add(review);
                }else{
                    double rating = Double.valueOf(ratingBar.getRating());
                    Review review = new Review(thisSession.getStudentName(), rating, reviewField.getText().toString(), cc.getTimeInMillis(),userInfo.getId());
                    thisSession.setTutorReview(review);
                    reviews.add(review);
                }
                if(currentRating == null){
                    currentRating = new Rating(ratingBar.getRating(),1);
                }else{
                    currentRating.setNumberOfReviews(currentRating.getNumberOfReviews()+1);
                    currentRating.setTotalScore(currentRating.getTotalScore()+ratingBar.getRating());
                }
                databaseReference3.child("reviews").setValue(reviews);
                databaseReference2.child("rating").setValue(currentRating);
                databaseReference3.child("rating").setValue(currentRating);
                databaseReference.setValue(allSessions);
                ratingBar.setVisibility(View.INVISIBLE);
                reviewField.setVisibility(View.INVISIBLE);
                reviewButton.setVisibility(View.INVISIBLE);
            }

        }
    }
}
