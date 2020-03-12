package co.edu.icesi.locationexample;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

public class LocationProvider implements LocationListener{

    protected OnLocationReceivedListener listener;

    @Override
    public void onLocationChanged(Location location) {
        listener.OnLocationReceived(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    public void setListener(OnLocationReceivedListener listener){
        this.listener = listener;
    }

    public interface OnLocationReceivedListener{
        void OnLocationReceived(Location location);
    }
}
