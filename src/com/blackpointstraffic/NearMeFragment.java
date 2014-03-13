package com.blackpointstraffic;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearMeFragment extends SupportMapFragment implements
		LocationListener {

	public NearMeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private GoogleMap map;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		map = getMap();
		map.setMyLocationEnabled(true);

		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(FragmentActivity.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);

		// Getting Current Location
		Location location = locationManager.getLastKnownLocation(provider);

		if (location != null) {
			onLocationChanged(location);
		}
		locationManager.requestLocationUpdates(provider, 20000, 0, this);

		MarkerOptions marker = new MarkerOptions().position(new LatLng(location
				.getLatitude(), location.getLongitude()));
		
		marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		map.addMarker(marker);
	}

	@Override
	public void onLocationChanged(Location location) {

		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());

		// Showing the current location in Google Map
		map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		// Zoom in the Google Map
		map.animateCamera(CameraUpdateFactory.zoomTo(15));
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
