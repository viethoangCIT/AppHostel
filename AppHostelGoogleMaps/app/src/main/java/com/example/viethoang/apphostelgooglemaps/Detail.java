package com.example.viethoang.apphostelgooglemaps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            String id_place = bundle.getString("id_place");
            Toast.makeText(this, "id_place: " + id_place, Toast.LENGTH_SHORT).show();
        }
    }
}
