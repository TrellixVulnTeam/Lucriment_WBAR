package tutors;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucriment.lucriment.R;
import sessions.SubjectListViewAdapter;
import students.UserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SearchForSubjects extends AppCompatActivity {
    private UserInfo userInfo;
    private TutorInfo tutorInfo;
    private String userType;
    private ListView listView;
    private SubjectListViewAdapter subjectListAdapter;
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<String> subjects = new ArrayList<>();
    private ArrayList<String> all = new ArrayList<>();
    private DatabaseReference databaseReference, mySubjectRef, teachersRef;
    private ArrayList<String> currentSubjects = new ArrayList<>();
    private boolean editing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_for_subjects_layout);
        // GET INTENTS
        if(getIntent().hasExtra("userInfo")) {
            userInfo = getIntent().getParcelableExtra("userInfo");
        }
        if(getIntent().hasExtra("userType")){
            userType = getIntent().getStringExtra("userType");
        }
        if(getIntent().hasExtra("tutorInfo")){
            tutorInfo = getIntent().getParcelableExtra("tutorInfo");
        }

        if(tutorInfo.getSubjects()!=null) {
            String subArray[] = tutorInfo.returnSubjectString().split(",");
            Collections.addAll(currentSubjects, subArray);
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.search_subjects_actionbar);

        View view =getSupportActionBar().getCustomView();

        listView = (ListView) findViewById(R.id.subjectLV);
        subjectListAdapter = new SubjectListViewAdapter(this,categories,subjects,all,currentSubjects);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        listView.setAdapter(subjectListAdapter);
        subjectListAdapter.notifyDataSetChanged();
        teachersRef = FirebaseDatabase.getInstance().getReference().child("tutors").child(userInfo.getId());


        mySubjectRef = FirebaseDatabase.getInstance().getReference().child("tutors").child(tutorInfo.getId()).child("subjects");
        mySubjectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentSubjects.clear();
                for(DataSnapshot subs: dataSnapshot.getChildren()){
                    currentSubjects.add(subs.getValue(String.class));
                }
                subjectListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //SET DATABASE REFERENCE
        databaseReference = FirebaseDatabase.getInstance().getReference().child("subjects");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot category: dataSnapshot.getChildren()){
                    categories.add(category.getKey());
                    all.add(category.getKey());
                    for(DataSnapshot subject:category.getChildren()){
                        subjects.add(subject.getValue(String.class));
                        all.add(subject.getValue(String.class));
                    }
                }
                subjectListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        TextView labelText = (TextView) view.findViewById(R.id.textView15);
        SearchView searchView = (SearchView) findViewById(R.id.subjectSearch7);
        /*
        categories.add("Math");
        categories.add("Science");
        subjects.add("Calculus 1");
        subjects.add("Linear Algebra 1");
        subjects.add("Calculus 2");
        subjects.add("Calculus 3");
        subjects.add("Calculus 4");
        subjects.add("Calculus 5");
        all.add("Science");
        all.add("Math");
        all.add("Calculus 1");
        all.add("Calculus 2");
        all.add("Calculus 3");
        all.add("Calculus 4");
        all.add("Calculus 5");
        all.add("Linear Algebra 1"); */


        //SET LIST CLICK LISTENER
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentSubjects.contains(all.get(position))){
                    if(currentSubjects.size()==1){
                        Toast.makeText(SearchForSubjects.this,"You must teach at least one subject",Toast.LENGTH_LONG).show();
                        return;
                    }
                    currentSubjects.remove(all.get(position));
                    teachersRef.child(all.get(position)).removeValue();
                }else{
                    currentSubjects.add(all.get(position));
                    HashMap<String, Object> teachesMap = new HashMap<String, Object>();
                    teachesMap.put(all.get(position), true);
                    teachersRef.updateChildren(teachesMap);
                }
                mySubjectRef.setValue(currentSubjects);

            }
        });



        //SET SEARCH BAR LISTENER
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                all.clear();
                for(String s:subjects){
                    String sL = s.toLowerCase();
                    String newL = newText.toLowerCase();
                    if(sL.contains(newL)){
                        all.add(s);
                    }
                }
                subjectListAdapter.notifyDataSetChanged();

                return false;
            }
        });

        //SET ACTIONBAR LISTENERS
        TextView back = (TextView) view.findViewById(R.id.action_bar_back);
        TextView edit = (TextView) view.findViewById(R.id.edit_about_action_bar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SearchForSubjects.this, EditTutorProfile.class);

                i.putExtra("userInfo", userInfo);
                i.putExtra("userType",userType);
                i.putExtra("tutorInfo",tutorInfo);

                startActivity(i);
            }
        });
       edit.setText("");
    }


}
