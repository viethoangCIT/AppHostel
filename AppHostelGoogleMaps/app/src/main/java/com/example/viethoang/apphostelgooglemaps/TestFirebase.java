package com.example.viethoang.apphostelgooglemaps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TestFirebase extends AppCompatActivity {

    private ArrayList<Place> placeArrayList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_firebase);


        //lấy dữ lieu data
        Query all = FirebaseDatabase.getInstance().getReference().child("Place");

        all.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                placeArrayList = new ArrayList<>();

                for(DataSnapshot item : dataSnapshot.getChildren())
                {
                    Place place = item.getValue(Place.class);
                    placeArrayList.add(place);
                }

                if(placeArrayList!=null)
                {
                    Toast.makeText(TestFirebase.this, "mang k null", Toast.LENGTH_SHORT).show();
                    for(int i = 0; i < placeArrayList.size(); i++)
                    {
                        String lnglat = placeArrayList.get(i).getLnglat().toString();
                        Toast.makeText(TestFirebase.this, "Place : "+lnglat, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TestFirebase.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
