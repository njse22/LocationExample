package co.edu.icesi.locationexample;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

public class LocationController implements LocationListener {


    public static final int GPS = 0;
    public static final int NETWORK = 1;
    private GoogleMap mMap;
    private Marker me;
    private Polygon edicioA;

    public LocationController(GoogleMap googleMap) {
        mMap = googleMap;

        edicioA = mMap.addPolygon(new PolygonOptions().add(new LatLng(3.342031,-76.530598))
                .add(new LatLng(3.342036,-76.530072))
                .add(new LatLng(3.342266,-76.530056))
                .add(new LatLng(3.342288,-76.530598))
                .fillColor(Color.argb(10,255,0,0))
                .strokeColor(Color.argb(10,255,0,0)));
    }

    public void setInitialPos(Location lastKnownLocation){
        LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        me = mMap.addMarker(new MarkerOptions().position(latLng));
    }

    @Override
    public void onLocationChanged(Location location) {
        me.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me.getPosition(), 18));
        listener.OnLocationReceived(location);
        if(
                PolyUtil.containsLocation(location.getLatitude(), location.getLongitude(), edicioA.getPoints(), true)
        ){
            listener.OnEdificioReached();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    OnLocationReceivedListener listener;

    public void setListener(OnLocationReceivedListener listener){
        this.listener = listener;
    }

    public interface OnLocationReceivedListener{
        void OnLocationReceived(Location location);
        void OnEdificioReached();
    }
}
