package misc;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucriment.lucriment.BaseActivity;
import com.lucriment.lucriment.R;
import students.Rating;

import students.CreationActivity;
import students.SelectedTutorActivity;
import students.UserInfo;
import tutors.TutorInfo;

public class ImageLayout extends BaseActivity {

    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private FirebaseRecyclerAdapter<TutorInfo, ImageLayoutViewHolder> mFirebaseAdapter;
    private UserInfo userInfo;
    private String userType;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private double tutorScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_layout);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = FirebaseDatabase.getInstance().getReference("tutors");
        recyclerView = (RecyclerView)findViewById(R.id.rView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ImageLayout.this));

        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        if(userInfo==null) {

            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DataSnapshot studentSnap = dataSnapshot.child("users");
                    DataSnapshot tutorSnap = dataSnapshot.child("tutors");
                    FirebaseUser thisUser = FirebaseAuth.getInstance().getCurrentUser();

                    if (studentSnap.hasChild(thisUser.getUid())) {
                        for (DataSnapshot userSnapShot : studentSnap.getChildren()) {
                            if (userSnapShot.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                                userInfo = userSnapShot.getValue(UserInfo.class);
                                userType = userInfo.getUserType();
                            }
                        }
                    } else if (tutorSnap.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        userType = userInfo.getUserType();
                    } else {
                        finish();
                        startActivity(new Intent(ImageLayout.this, CreationActivity.class));
                    }
                    // finish();
                    //   startActivity(new Intent(ProfileActivity.this, TutorListActivity.class));
                    // initializeButtons();

                    setUp();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {

            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
            BottomNavHelper.disableShiftMode(bottomNavigationView);
            bottomNavigationView.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         //   getTutors();
         //   registerTutorClicks();
            bottomNavigationView.setOnNavigationItemSelectedListener(this);
        }
      //  Toast.makeText(ImageLayout.this, "Wait ! Fetching List...", Toast.LENGTH_SHORT).show();

    }


    @Override
    protected void onStart() {
        super.onStart();

//Log.d("LOGGED", "IN onStart ");
        mFirebaseAdapter = new FirebaseRecyclerAdapter<TutorInfo, ImageLayoutViewHolder>
                (TutorInfo.class, R.layout.tutor_profile_layout, ImageLayoutViewHolder.class, myRef)
        {

            public void populateViewHolder(final ImageLayoutViewHolder viewHolder, final TutorInfo model, final int position) {
                viewHolder.Image_URL(model.getProfileImage());
                viewHolder.Image_Title(model.getFirstName());
                viewHolder.RateText(String.valueOf(model.getRate()));
                Rating rating = model.getRating();
                if (rating != null) {
                    double score = rating.getTotalScore()/rating.getNumberOfReviews();
                    viewHolder.RatingBar((float)score);
                }



//OnClick Item it will Delete data from Database
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        TutorInfo selectedTutor1 = model;
                        Rating tutorRating = selectedTutor1.getRating();
                        if(tutorRating!=null) {
                            tutorScore = tutorRating.getTotalScore() / tutorRating.getNumberOfReviews();

                        }
                        // selectedTutor1 = TutorListActivity.this.selectedTutor;
                        Intent i = new Intent(ImageLayout.this, SelectedTutorActivity.class);
                        i.putExtra("selectedTutor", selectedTutor1);
                        i.putExtra("tutorScore",tutorScore);
                        i.putExtra("userType", userType);
                        i.putExtra("userInfo",userInfo);
                        startActivity(i);

                        /*
                        AlertDialog.Builder builder = new AlertDialog.Builder(ImageLayout.this);
                        builder.setMessage("Do you want to Delete this data ?").setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int selectedItems = position;
                                        mFirebaseAdapter.getRef(selectedItems).removeValue();
                                        mFirebaseAdapter.notifyItemRemoved(selectedItems);
                                        recyclerView.invalidate();
                                        onStart();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.setTitle("Confirm");
                        dialog.show(); */
                    }
                });


            }
        };

        recyclerView.setAdapter(mFirebaseAdapter);
    }

    private void setUp(){
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected int getContentViewId() {
       return R.layout.activity_image_layout;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.search;
    }

    @Override
    protected String getUserType() {
        return userType;
    }

    @Override
    protected UserInfo getUserInformation() {
        return userInfo;
    }

    //View Holder For Recycler View
    public static class ImageLayoutViewHolder extends RecyclerView.ViewHolder {
        private final TextView tutorName;
        private final ImageView image_url;
        private final TextView rateText;
        private final TextView rating;



        public ImageLayoutViewHolder(final View itemView) {
            super(itemView);
            image_url = (ImageView) itemView.findViewById(R.id.ProfileImage);
            tutorName = (TextView) itemView.findViewById(R.id.browseDisplayName);

            rateText = (TextView) itemView.findViewById(R.id.browseRate);
            rating = (TextView) itemView.findViewById(R.id.rating);
        }


        private void RateText(String rate){
            rateText.setText("$"+rate+"/hr");
        }

        private void RatingBar(Float rating){
            this.rating.setText(rating+"");
        }


        private void Image_Title(String title) {
            tutorName.setText(title);
        }

        private void Image_URL(String title) {
// image_url.setImageResource(R.drawable.loading);
            Glide.with(itemView.getContext())
                    .load(title)


                    .thumbnail(0.1f)

                    .into(image_url);
        }
    }
}