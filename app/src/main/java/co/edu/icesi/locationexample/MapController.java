package co.edu.icesi.locationexample;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.HashMap;

import static android.content.Context.LOCATION_SERVICE;

public class MapController implements  LocationProvider.OnLocationReceivedListener{

    private static final int DEFAULT_ZOOM = 15;
    private static final int MIN_TIME = 500;
    private static final int MIN_DISTANCE = 2;

    private GoogleMap mMap;
    private Marker myLocation;

    private MapsActivity activity;
    private LocationProvider gpsProvider;
    private LocationProvider networkProvider;
    private HashMap< Double, Marker> markers;


    private double minAccuracy = 1000;


    public MapController(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public GoogleMap getmMap() {
        return mMap;
    }

    public HashMap<Double, Marker> getMarkers() {
        return markers;
    }

    public Marker getMyLocation() {
        return myLocation;
    }

    @SuppressLint("MissingPermission")
    public void init(){

        markers = new HashMap<>();

        mMap.setOnMapLongClickListener(this.activity);
        mMap.setOnMarkerClickListener(this.activity);
        LocationManager manager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);

        //UBICAR MARCADOR EN POSICION INCIAL
        if(manager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null)
            setInitialPos( manager.getLastKnownLocation(LocationManager.GPS_PROVIDER) );
        else
            setInitialPos( manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) );

        //PROVIDER DE NETWORK
        this.networkProvider = new LocationProvider();
        networkProvider.setListener(this);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME,MIN_DISTANCE, networkProvider);

        //PROVIDER DE GPS
        this.gpsProvider = new LocationProvider();
        gpsProvider.setListener(this);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME,MIN_DISTANCE, gpsProvider);

    }

    //Metodo para ubicar el marcador en la posición inicial
    public void setInitialPos(Location lastKnownLocation){
        LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        myLocation = mMap.addMarker(new MarkerOptions().position(latLng));
        myLocation.setIcon((BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
    }

    //En este metodo recibimos los locations de ambos providers
    @Override
    public void OnLocationReceived(Location location) {
        if(location.getAccuracy() <= minAccuracy){
            minAccuracy = location.getAccuracy();
            LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
            myLocation.setPosition(pos);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation.getPosition(), DEFAULT_ZOOM));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,DEFAULT_ZOOM));
            updateInfo();
            if(markers.containsKey(pos)){
                activity.getOutput().append("\n Usted esta cerca de: " + markers.get(pos).getTitle().toString());
            }
        }
    }

    public void setActivity(MapsActivity activity) {
        this.activity = activity;
    }

    public static double calculateDistance(double lat1, double lat2, double lon1, double lon2) {
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }

    public Marker getMinimunMarker(){
        double minDis = Double.MAX_VALUE;
        for(double dis : markers.keySet()){
            if(dis < minDis)
                minDis = dis;
        }
        return markers.get(minDis);
    }

    public void updateInfo(){
        if(getMinimunMarker() != null) {
            activity.getOutput().setText("El lugar mas cercano es: " + getMinimunMarker().getTitle() + "\n"
                    + "A una distancia de: " + calculateDistance(getMinimunMarker().getPosition().latitude,
                    myLocation.getPosition().latitude,
                    getMinimunMarker().getPosition().longitude,
                    myLocation.getPosition().longitude));
        }
    }

}
