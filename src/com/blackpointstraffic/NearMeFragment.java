package com.blackpointstraffic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackpointstraffic.adapter.CustomizedInfoWindowAdapter;
import com.blackpointstraffic.helper.GeoUtil;
import com.blackpointstraffic.helper.JSONParser;
import com.blackpointstraffic.model.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearMeFragment extends SupportMapFragment implements
		LocationListener, OnMarkerClickListener, OnInfoWindowClickListener {

	private GoogleMap googleMap;
	private int cirleRadius = 5000;
	private int zoomLevel = 12;
	private Location currentLocation;

	public NearMeFragment() {
	}

	public int getCirleRadius() {
		return cirleRadius;
	}

	public void setCirleRadius(int cirleRadius) {
		this.cirleRadius = cirleRadius;
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = super.onCreateView(inflater, container,
				savedInstanceState);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// get preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		cirleRadius = Integer.parseInt(prefs.getString("prefDistance", "-1"));
		if (cirleRadius < 0) {
			cirleRadius = 5000;
		}
		zoomLevel = caculateZoomLevel(cirleRadius);

		googleMap = getMap();
		googleMap.setMyLocationEnabled(true);

		LocationManager locationManager = (LocationManager) getActivity()
				.getSystemService(FragmentActivity.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);

		// Getting Current Location
		currentLocation = locationManager.getLastKnownLocation(provider);
		locationManager.requestLocationUpdates(provider, 15000, 0, this);

		LatLng latLng = new LatLng(currentLocation.getLatitude(),
				currentLocation.getLongitude());

		// Showing the current location in Google Map
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));

		// add circle
		CircleOptions circleOptions = new CircleOptions()
				.center(new LatLng(currentLocation.getLatitude(),
						currentLocation.getLongitude())).fillColor(0x30ff0000)
				.strokeColor(0x60ff0000).strokeWidth(1).radius(cirleRadius);

		googleMap.addCircle(circleOptions);
		googleMap.setOnInfoWindowClickListener(this);

		// call async task
		new GetPOIs().execute();
	}

	private int caculateZoomLevel(int cirleRadius) {
		switch (cirleRadius) {
		case 500:
			return 15;
		case 1000:
			return 14;
		case 2000:
			return 13;
		case 5000:
			return 12;
		case 10000:
		case 15000:
			return 11;
		case 20000:
		case 25000:
		case 30000:
			return 10;
		case 35000:
		case 40000:
		case 45000:
		case 50000:
			return 9;
		default:
			return 12;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
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

	@Override
	public boolean onMarkerClick(Marker marker) {
		marker.showInfoWindow();
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Intent intent = new Intent(NearMeFragment.this.getActivity(),
				DetailsActivity.class);
		intent.putExtra(getActivity().getString(R.string.TAG_NAME),
				marker.getTitle());
		intent.putExtra(getActivity().getString(R.string.SNIPPET),
				marker.getSnippet());

		startActivity(intent);
	}

	public void showNotification(Map<String, String> data) {
		NotificationManager notificationManager = (NotificationManager) getActivity()
				.getSystemService(Context.NOTIFICATION_SERVICE);

		double distance = Double.parseDouble(data
				.get(getString(R.string.TAG_DISTANCE)));

		String snippet = data
				.get(getActivity().getString(R.string.TAG_ADDRESS))
				+ "~"
				+ data.get(getActivity().getString(R.string.TAG_CATEGORY))
				+ "~"
				+ data.get(getActivity().getString(R.string.TAG_RATING))
				+ "~"
				+ data.get(getActivity().getString(R.string.TAG_IMAGE))
				+ "~"
				+ data.get(getActivity().getString(R.string.TAG_CREATE_DATE))
				+ "~"
				+ data.get(getActivity().getString(R.string.TAG_DESCRIPTION))
				+ "~"
				+ data.get(getActivity().getString(R.string.TAG_DISTANCE));

		Intent intent = new Intent(getActivity(), DetailsActivity.class);
		intent.putExtra(getActivity().getString(R.string.TAG_NAME),
				data.get(getActivity().getString(R.string.TAG_NAME)));
		intent.putExtra(getActivity().getString(R.string.SNIPPET), snippet);

		PendingIntent pIntent = PendingIntent.getActivity(getActivity(), 0,
				intent, 0);

		String notificationContent = data.get(getString(R.string.TAG_NAME))
				+ " ("
				+ (distance >= 1000 ? (int) (distance / 1000) + "km)"
						: (int) (distance / 100) + "m)");

		Notification n = new Notification.Builder(getActivity())
				.setContentTitle(getString(R.string.notification_Title))
				.setContentText(notificationContent)
				.setStyle(
						new Notification.BigTextStyle()
								.bigText(notificationContent))
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
				.setAutoCancel(true).build();
		n.defaults |= Notification.DEFAULT_SOUND;
		notificationManager.notify(0, n);
	}

	private class GetPOIs extends AsyncTask<Void, Void, Void> {

		private JSONArray poiArr = null;
		private List<Map<String, String>> poiList;

		private String tag_id;
		private String tag_name;
		private String tag_address;
		private String tag_image;
		private String tag_geometry;
		private String tag_category;
		private String tag_rating;
		private String tag_date;
		private String tag_description;
		private String tag_distance;

		public GetPOIs() {
			poiList = new ArrayList<Map<String, String>>();

			tag_id = getActivity().getString(R.string.TAG_ID);
			tag_name = getActivity().getString(R.string.TAG_NAME);
			tag_address = getActivity().getString(R.string.TAG_ADDRESS);
			tag_image = getActivity().getString(R.string.TAG_IMAGE);
			tag_geometry = getActivity().getString(R.string.TAG_GEOMETRY);
			tag_category = getActivity().getString(R.string.TAG_CATEGORY);
			tag_rating = getActivity().getString(R.string.TAG_RATING);
			tag_date = getActivity().getString(R.string.TAG_CREATE_DATE);
			tag_description = getActivity().getString(R.string.TAG_DESCRIPTION);
			tag_distance = getActivity().getString(R.string.TAG_DISTANCE);
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				StringBuilder urlService = new StringBuilder();
				urlService.append(getActivity().getString(
						R.string.URL_GET_POI_IN_RADIUS));
				urlService.append("/" + currentLocation.getLongitude());
				urlService.append("/" + currentLocation.getLatitude());
				urlService.append("/" + cirleRadius);

				poiArr = JSONParser.getJsonFromUrl(urlService.toString());

				if (poiArr != null) {

					for (int i = 0; i < poiArr.length(); i++) {
						JSONObject jsonObject = poiArr.getJSONObject(i);

						Map<String, String> map = new HashMap<String, String>();
						map.put(tag_id, jsonObject.getString(tag_id));
						map.put(tag_name, jsonObject.getString(tag_name));
						map.put(tag_address, jsonObject.getString(tag_address));
						map.put(tag_image, jsonObject.getString(tag_image));
						map.put(tag_geometry,
								jsonObject.getString(tag_geometry));
						map.put(tag_category,
								jsonObject.getString(tag_category));
						map.put(tag_rating, jsonObject.getString(tag_rating));
						map.put(tag_date, jsonObject.getString(tag_date));
						map.put(tag_description,
								jsonObject.getString(tag_description));
						map.put(tag_distance,
								jsonObject.getString(tag_distance));

						poiList.add(map);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (poiList.size() > 0) {

				for (Map<String, String> data : poiList) {
					List<GeoLocation> geoList = GeoUtil.toLatLng(data
							.get(tag_geometry));
					LatLng latLng = new LatLng(geoList.get(0).getLat(), geoList
							.get(0).getLng());
					String title = data.get(tag_name);
					String snippet = data.get(tag_address) + "~"
							+ data.get(tag_category) + "~"
							+ data.get(tag_rating) + "~" + data.get(tag_image)
							+ "~" + data.get(tag_date) + "~"
							+ data.get(tag_description) + "~"
							+ data.get(tag_distance);

					// add marker
					MarkerOptions markerOptions = new MarkerOptions()
							.position(latLng)
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_RED))
							.title(title).snippet(snippet);

					googleMap.addMarker(markerOptions);
					googleMap
							.setInfoWindowAdapter(new CustomizedInfoWindowAdapter(
									getActivity()));
				}

				// vibrate
				Vibrator v = (Vibrator) getActivity().getSystemService(
						Context.VIBRATOR_SERVICE);
				long[] pattern = { 0, 500, 500, 500, 500, 500, 500, 500, 500,
						500, 500, 500, 500 };
				v.vibrate(pattern, -1);

				// show notification
				showNotification(poiList.get(0));
			}
		}
	}
}
