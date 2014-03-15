package com.blackpointstraffic.adapter;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackpointstraffic.R;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class CustomizedInfoWindowAdapter implements InfoWindowAdapter {

	private FragmentActivity activity;
	private ImageLoader imageLoader;

	public CustomizedInfoWindowAdapter() {
	}

	public CustomizedInfoWindowAdapter(FragmentActivity activity) {
		this.activity = activity;

		DisplayImageOptions dio = new DisplayImageOptions.Builder()
				.showImageOnFail(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.cacheInMemory()
				.cacheOnDisc().build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				activity.getApplicationContext())
				.defaultDisplayImageOptions(dio).build();

		imageLoader = ImageLoader.getInstance();
		if (!imageLoader.isInited()) {
			imageLoader.init(config);
		}
	}

	@Override
	public View getInfoContents(Marker marker) {
		View view = activity.getLayoutInflater().inflate(
				R.layout.info_window_layout, null);
		String snippet = marker.getSnippet();
		String[] data = snippet.split("~");

		TextView txtName = (TextView) view.findViewById(R.id.name);
		TextView txtAddress = (TextView) view.findViewById(R.id.address);
		TextView txtCategory = (TextView) view
				.findViewById(R.id.categoryAndRating);
		ImageView imageView = (ImageView) view.findViewById(R.id.image);

		txtName.setText(marker.getTitle());
		txtAddress.setText(data[0]);
		txtCategory.setText(data[1] + " - " + data[2]);
		imageLoader.displayImage(data[3], imageView);
		
		return view;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		return null;
	}

}
