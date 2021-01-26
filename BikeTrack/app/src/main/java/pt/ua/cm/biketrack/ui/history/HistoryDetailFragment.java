package pt.ua.cm.biketrack.ui.history;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import pt.ua.cm.biketrack.R;
import pt.ua.cm.biketrack.models.TrackInfo;


public class HistoryDetailFragment extends Fragment implements OnMapReadyCallback, RoutingListener {

    private static final String TRACKING_LOCATION_KEY = "pt.ua.cm.biketrack.TRACKING_LOCATION_KEY";
    private HistoryViewModel mHistoryViewModel;

    //Google Maps Components
    private GoogleMap mMap;

    //Location Components
    private Boolean mTrackingLocation;
    private static final int DEFAULT_ZOOM = 16;


    //UI Components
    private FloatingActionButton share_button;
    private TextView speed;
    private TextView distance;
    private TextView time;
    private int distanceInMeters = 0;
    private List<Polyline> polylines;
    private TrackInfo info;

    private String qr_Data;

    private static final String QUERY_ID = "query_id";

    private int queryID;

    public HistoryDetailFragment() {
        // Required empty public constructor
    }

    public static HistoryDetailFragment newInstance(int id) {
        HistoryDetailFragment fragment = new HistoryDetailFragment();
        Bundle args = new Bundle();
        args.putInt(QUERY_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            queryID = getArguments().getInt(QUERY_ID);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mTrackingLocation = savedInstanceState.getBoolean(TRACKING_LOCATION_KEY);
        }

        View root = inflater.inflate(R.layout.fragment_history_detail, container, false);

        //Initialize polylines array
        polylines = new ArrayList<>();

        //Get text views ID's
        speed = root.findViewById(R.id.speed_detail);
        distance = root. findViewById(R.id.distance_detail);
        time = root.findViewById(R.id.chronometer_detail);

        share_button = root.findViewById(R.id.fab_save_qr);


        //Get id from Navigation Bundle
        int id = HistoryDetailFragmentArgs.fromBundle(getArguments()).getQueryId();

        //Initialize ViewModel
        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        mHistoryViewModel.getTrack(id).observe(getViewLifecycleOwner(), trackInfo -> {
            info = new TrackInfo(trackInfo.getAvgSpeed(), trackInfo.getDistance(),
                    trackInfo.getInitialLatitude(), trackInfo.getInitialLongitude(),
                    trackInfo.getFinalLatitude(), trackInfo.getFinalLongitude(), trackInfo.getTime());

            //Set UI data
            speed.setText(String.format("%s Km/h", String.valueOf(info.getAvgSpeed())));
            distance.setText(String.format("%s m", String.valueOf(info.getDistance())));
            time.setText(info.getTime());

            qr_Data = String.format("%s,%s,%s,%s,%s,%s,%s", info.getAvgSpeed(),
                    info.getDistance(), info.getInitialLatitude(), info.getInitialLongitude(),
                    info.getFinalLatitude(), info.getFinalLongitude(), info.getTime());

            //Initialize Google Maps
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map_detail);
            assert mapFragment != null;
            mapFragment.getMapAsync(this);
        });

        share_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
               HistoryDetailFragmentDirections.ActionNavigationDetailsToNavigationQr action = HistoryDetailFragmentDirections.actionNavigationDetailsToNavigationQr(qr_Data);
               action.setQrData(qr_Data);
               Navigation.findNavController(v).navigate(action);
            }
        });


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

        LatLng start = new LatLng(info.getInitialLatitude(), info.getInitialLongitude());
        LatLng finish = new LatLng(info.getFinalLatitude(), info.getFinalLongitude());
        mMap.addMarker(new MarkerOptions().position(start)
                .title("Start"));
        mMap.addMarker(new MarkerOptions().position(finish)
                .title("Finish"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(info.getInitialLatitude(),
                        info.getInitialLongitude()), DEFAULT_ZOOM));

        getRouteToCurrentPosition(info.getInitialLatitude(), info.getInitialLongitude(), info.getFinalLatitude(), info.getFinalLongitude());

    }

    private void getRouteToCurrentPosition(double initialLat, double initialLng, double finalLat, double finalLng) {
        Routing routing = new Routing.Builder()
                .key(getResources().getString(R.string.google_maps_key))
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(initialLat, initialLng), new LatLng(finalLat, finalLng))
                .build();
        routing.execute();
    }

    private void erasePolylines(){
        for(Polyline poly : polylines){
            poly.remove();
        }
        polylines.clear();
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
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(TRACKING_LOCATION_KEY, mTrackingLocation);
        super.onSaveInstanceState(outState);
    }
}
