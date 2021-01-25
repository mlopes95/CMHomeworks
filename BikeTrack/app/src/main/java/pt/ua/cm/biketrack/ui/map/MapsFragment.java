package pt.ua.cm.biketrack.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;

import pt.ua.cm.biketrack.R;
import pt.ua.cm.biketrack.models.TrackInfo;
import pt.ua.cm.biketrack.ui.history.HistoryViewModel;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;


public class MapsFragment extends Fragment implements OnMapReadyCallback, RoutingListener {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TRACKING_LOCATION_KEY = "pt.ua.cm.biketrack.TRACKING_LOCATION_KEY";
    private MapViewModel mapViewModel;
    private HistoryViewModel mHistoryViewModel;

    //Google Maps Components
    private GoogleMap mMap;

    //Location Components
    private Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastKnownLocation;
    private Location mInitialPosition;
    private PlacesClient mPlaceDetectionClient;
    private Boolean mTrackingLocation;
    private Boolean mLocationPermissionGranted;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private LocationCallback mLocationCallback;

    //UI Components
    private FloatingActionButton play_button;
    private FloatingActionButton stop_button;
    private FloatingActionButton save_button;
    private TextView speed;
    private TextView distance;
    private String spd;
    private String dist;
    private int distanceInMeters = 0;
    private Chronometer chrono;
    private long pauseOffset;
    private boolean isRunning;
    private List<Polyline> polylines;
    private List<Integer> avgSpeed;
    private int avg;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mTrackingLocation = savedInstanceState.getBoolean(TRACKING_LOCATION_KEY);
        }

        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        //Buttons and other stuff
        speed = root.findViewById(R.id.speed);
        distance = root.findViewById(R.id.distance);
        play_button = root.findViewById(R.id.fab_play);
        stop_button = root.findViewById(R.id.fab_stop);
        save_button = root.findViewById(R.id.fab_save);
        chrono = root.findViewById(R.id.chronometer);
        chrono.setBase(SystemClock.elapsedRealtime());
        mTrackingLocation = false;
        mLocationPermissionGranted = false;
        polylines = new ArrayList<>();
        avgSpeed = new ArrayList<>();


        // Construct a GeoDataClient.
        Places.initialize(this.requireContext(), getString(R.string.google_maps_key));

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.createClient(this.requireContext());

        //Initialize ViewModel
        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        // Initialize the FusedLocationClient.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(
                this.requireActivity());

        //Initialize Google Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        play_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int id;
                if (!mTrackingLocation) {
                    id = getResources().getIdentifier("pt.ua.cm.biketrack:drawable/ic_baseline_pause_24", null, null);
                    play_button.setImageResource(id);
                    enableMyLocation();
                    startTrackingLocation();
                    if (!isRunning) {
                        chrono.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                        chrono.start();
                        isRunning = true;
                    }
                } else {
                    id = getResources().getIdentifier("pt.ua.cm.biketrack:drawable/ic_baseline_play_arrow_24", null, null);
                    play_button.setImageResource(id);
                    pauseTrackingLocation();
                    if (isRunning) {
                        chrono.stop();
                        pauseOffset = SystemClock.elapsedRealtime() - chrono.getBase();
                        isRunning = false;
                    }
                }
            }
        });

        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chrono.stop();
                pauseOffset = SystemClock.elapsedRealtime() - chrono.getBase();
                isRunning = false;
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
                stopTrackingLocation();
                erasePolylines();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mTrackingLocation){
                    saveToBD();
                    erasePolylines();
                    resetValues();
                } else {
                    Toast.makeText( getContext(), "Please stop tracking before attempting to save", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(mLastKnownLocation == null){
                    mLastKnownLocation = locationResult.getLastLocation();
                    mInitialPosition = mLastKnownLocation;
                }
                if (mTrackingLocation) {
                    mLastLocation = locationResult.getLastLocation();

                    //Speed TxtView Update
                    spd = (int) mLastLocation.getSpeed() + " Km/h";
                    speed.setText(spd);

                    //Update avgSpeeds
                    avgSpeed.add((int) mLastLocation.getSpeed());

                    //Distance TxtView Update
                    distanceInMeters += locationResult.getLastLocation().distanceTo(mLastKnownLocation);
                    dist = (int) distanceInMeters + " m";
                    distance.setText(dist);
                    mLastKnownLocation = mLastLocation;

                    //Update Maps Camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mLastLocation.getLatitude(),
                                    mLastLocation.getLongitude()), DEFAULT_ZOOM));

                    //Draw polylines
                    getRouteToCurrentPosition(mInitialPosition, mLastLocation);
                }
            }
        };
        return root;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        enableMyLocation();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }


    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.requireActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    private void startTrackingLocation() {
        if (ActivityCompat.checkSelfPermission(this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.requireActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mTrackingLocation = true;
            mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null /* Looper */);
        }
    }

    private void pauseTrackingLocation() {
        if (mTrackingLocation) {
            mTrackingLocation = false;
        }
    }

    private void stopTrackingLocation(){
            if(mTrackingLocation){
                mTrackingLocation = false;
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startTrackingLocation();
                    mLocationPermissionGranted = true;
                } else {
                    Toast.makeText(this.getContext(),
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                Log.e("enable", "Button enabled");
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
                Log.e("disable", "disabled");
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this.requireActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mInitialPosition = mLastKnownLocation;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this.requireActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }


    private void getRouteToCurrentPosition(Location initialPosition, Location lastLocation) {
        Routing routing = new Routing.Builder()
                .key(getResources().getString(R.string.google_maps_key))
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(initialPosition.getLatitude(), initialPosition.getLongitude()), new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
                .build();
        routing.execute();
    }

    private void erasePolylines(){
        for(Polyline poly : polylines){
            poly.remove();
        }
        polylines.clear();
    }

    private void saveToBD(){
        for(int value : avgSpeed){
            avg += value;
        }
        avg = avg/avgSpeed.size();
        TrackInfo trackInfo = new TrackInfo(avg, distanceInMeters, mInitialPosition.getLatitude(),
                mInitialPosition.getLongitude(), mLastLocation.getLatitude(),
                mLastLocation.getLongitude(), chrono.getText().toString());
        mHistoryViewModel.insert(trackInfo);
    }

    private void resetValues(){
        distanceInMeters = 0;
        spd = "0 Km/h";
        avgSpeed.clear();
        speed.setText(spd);
        dist = (int) distanceInMeters + " m";
        distance.setText(dist);
        chrono.stop();
        chrono.setBase(SystemClock.elapsedRealtime());
        pauseOffset= 0;
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
        if(e != null) {
            Toast.makeText(this.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this.getContext(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            erasePolylines();
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(ContextCompat.getColor(this.getContext(), R.color.design_default_color_secondary));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            //Toast.makeText(this.getContext() ,"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled() {
        erasePolylines();
    }

    @Override
    public void onPause() {
        if (mTrackingLocation) {
            stopTrackingLocation();
            mTrackingLocation = true;
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mTrackingLocation) {
            startTrackingLocation();
        }
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(TRACKING_LOCATION_KEY, mTrackingLocation);
        super.onSaveInstanceState(outState);
    }
}
