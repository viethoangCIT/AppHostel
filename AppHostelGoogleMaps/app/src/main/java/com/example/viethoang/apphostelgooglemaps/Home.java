package com.example.viethoang.apphostelgooglemaps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
//        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");

        mMap = googleMap;

//        Toast.makeText(this, "Is Marker ready", Toast.LENGTH_SHORT).show();

        if (mlocationPermissionsGranted) {
            getDevicelocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            init();
        }
    }

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSTION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));


    //widgets
    private AutoCompleteTextView edtSearchText;
    private ImageView imgGps;

    //vars
    private Boolean mlocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;

    private ArrayList<Place> placeArrayList = null;
    private Marker myMarker;
    private ImageView imgvDetail;

    private void anhXa()
    {
        edtSearchText = (AutoCompleteTextView) findViewById(R.id.edt_input_search);
        imgGps = (ImageView) findViewById(R.id.img_ic_gps);
        imgvDetail = (ImageView) findViewById(R.id.imgvDetail);
        imgvDetail.setVisibility(View.INVISIBLE);
    }

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        anhXa();

        //BEGIN: MAPS API
        getLocationPermission();
        Query all = FirebaseDatabase.getInstance().getReference("Place");
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
//                    Toast.makeText(MapActivity.this, "mang k null", Toast.LENGTH_SHORT).show();
                    for(int i = 0; i < placeArrayList.size(); i++)
                    {
                        String lnglat = placeArrayList.get(i).getLnglat().toString().trim();
                        String title = placeArrayList.get(i).getTitle().toString().trim();
                        String id_place = placeArrayList.get(i).getId_place().toString().trim();
                        //begin: cat chuoi sau do chuyen thanh double
                        String[] sLnglat = lnglat.split(",");
                        String sLnglat_01 = sLnglat[0].toString().trim();
                        String sLnglat_02 = sLnglat[1].toString().trim();


                        double lat = Double.parseDouble(sLnglat_01);
                        double lng = Double.parseDouble(sLnglat_02);
                        //end: cat chuoi sau do chuyen thanh double

                        drawMarker(new LatLng(lat,lng),title, id_place);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Home.this, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
            }
        });

        //END: MAPS API

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id=item.getItemId();

        switch (id){

            case R.id.nav_home:
                break;
            case R.id.nav_addHostel:
                Intent i= new Intent(Home.this,AddHostel.class);
                startActivity(i);
                break;
            // this is done, now let us go and intialise the home page.
            // after this lets start copying the above.
            // FOLLOW MEEEEE>>>
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void drawMarker(LatLng point, String stitle, final String id_place)
    {

        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker

        markerOptions.position(point)
                .title(stitle)
                .snippet(id_place)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));
        // Adding marker on the Google Map
        //mMap.addMarker(markerOptions);

        myMarker = mMap.addMarker(markerOptions);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //marker.hideInfoWindow();
                imgvDetail.setVisibility(View.VISIBLE);

                imgvDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Home.this, Detail.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("id_place",marker.getSnippet().toString());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });



                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                imgvDetail.setVisibility(View.INVISIBLE);
            }
        });

    }






    private void init(){
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);

        edtSearchText.setAdapter(mPlaceAutocompleteAdapter);

        edtSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    //excute our method for searching
                    getLocate();

                }
                return false;
            }
        });

        imgGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDevicelocation();
            }
        });

        hideSoftKeyboard();
    }

    private void getLocate(){
        Log.d(TAG, "getLocate: geoLocating");

        String searchString = edtSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(Home.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e){
            Log.d(TAG, "getLocate: IOException "+ e.getMessage());
        }

        if(list.size() >0){
            Address address = list.get(0);

            Log.d(TAG, "getLocate: found a location "+ address.toString());
            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        }
    }

    private void getDevicelocation(){
        Log.d(TAG, "getDevicelocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try{
            if(mlocationPermissionsGranted){
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful() && task.getResult () != null){
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(Home.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.d(TAG, "getDevicelocation: "+ e.getMessage());
        }
    }


    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG, "moveCamera: moving the camera to : lat: "+latLng.latitude + ", lng: "+ latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(Home.this));

//        if(!title.equals("My Location")){
//            MarkerOptions options = new MarkerOptions()
//                    .position(latLng)
//                    .title(title);
//            mMap.addMarker(options);
//        }

        hideSoftKeyboard();

    }

    private void initMap(){
        Log.d(TAG, "initMap: initalizing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(Home.this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permission");

        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mlocationPermissionsGranted = true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(this,
                        permission,
                        LOCATION_PERMISSTION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this,
                    permission,
                    LOCATION_PERMISSTION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");

        mlocationPermissionsGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSTION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                            mlocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mlocationPermissionsGranted = true;
                    //initalize our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
     * *********************** google place API autocomplete suggestions
     */
}
