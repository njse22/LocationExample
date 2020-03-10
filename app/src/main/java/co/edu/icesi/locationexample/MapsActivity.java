package co.edu.icesi.locationexample;

import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationController.OnLocationReceivedListener {

    private LocationController locationController;
    private TextView output;
    private boolean edificio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        output = findViewById(R.id.output);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationController = new LocationController(googleMap);
        locationController.setListener(this);
        locationController.setInitialPos( manager.getLastKnownLocation(LocationManager.GPS_PROVIDER) );
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, locationController);
    }

    @Override
    public void OnLocationReceived(Location location) {
        output.setText((edificio?"Usted esta en el A\n":"")+"Acu: "+location.getAccuracy());
    }

    @Override
    public void OnEdificioReached() {
        edificio = true;
    }
}
