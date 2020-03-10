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

import static android.content.Context.LOCATION_SERVICE;

public class MapController implements  LocationProvider.OnLocationReceivedListener{

    private GoogleMap mMap;
    private Marker me;

    private MapsActivity activity;
    private LocationProvider gpsProvider;
    private LocationProvider networkProvider;

    private double minAccuracy = 1000;


    public MapController(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @SuppressLint("MissingPermission")
    public void init(){
        //Ejemplo de poner un polígono sobre el mapa
        Polygon edicioA = mMap.addPolygon(new PolygonOptions().add(new LatLng(3.342031,-76.530598))
                .add(new LatLng(3.342036,-76.530072))
                .add(new LatLng(3.342266,-76.530056))
                .add(new LatLng(3.342288,-76.530598))
                .fillColor(Color.argb(10,255,0,0))
                .strokeColor(Color.argb(10,255,0,0)));

        LocationManager manager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);

        //UBICAR MARCADOR EN POSICION INCIAL
        setInitialPos( manager.getLastKnownLocation(LocationManager.GPS_PROVIDER) );

        //PROVIDER DE NETWORK
        this.networkProvider = new LocationProvider();
        networkProvider.setListener(this);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500,2, networkProvider);

        //PROVIDER DE GPS
        this.gpsProvider = new LocationProvider();
        gpsProvider.setListener(this);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500,2, gpsProvider);
    }

    //Metodo para ubicar el marcador en la posición inicial
    public void setInitialPos(Location lastKnownLocation){
        LatLng latLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        me = mMap.addMarker(new MarkerOptions().position(latLng));
    }

    //En este metodo recibimos los locations de ambos providers
    @Override
    public void OnLocationReceived(Location location) {
        if(location.getAccuracy() <= minAccuracy){
            minAccuracy = location.getAccuracy();
            activity.getOutput().setText("Accuracy: "+location.getAccuracy());
            me.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me.getPosition(), 18));
        }
    }

    public void setActivity(MapsActivity activity) {
        this.activity = activity;
    }

}
