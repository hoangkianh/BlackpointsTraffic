package com.blackpointstraffic;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends FragmentActivity {
	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		DisplayImageOptions dio = new DisplayImageOptions.Builder()
				.showImageOnFail(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher).cacheInMemory()
				.cacheOnDisc().build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).defaultDisplayImageOptions(dio)
				.build();

		imageLoader = ImageLoader.getInstance();
		if (!imageLoader.isInited()) {
			imageLoader.init(config);
		}

		Intent intent = getIntent();
		String name = intent.getStringExtra(getString(R.string.TAG_NAME));
		String[] data = intent.getStringExtra(getString(R.string.SNIPPET))
				.split("~");

		TextView txtName = (TextView) findViewById(R.id.name);
		TextView txtAddress = (TextView) findViewById(R.id.address);
		TextView txtDate = (TextView) findViewById(R.id.date);
		TextView txtCategory = (TextView) findViewById(R.id.category);
		TextView txtRating = (TextView) findViewById(R.id.rating);
		TextView txtDescription = (TextView) findViewById(R.id.description);
		TextView txtDistance = (TextView) findViewById(R.id.distance);

		ImageView imageView = (ImageView) findViewById(R.id.image);

		txtName.setText(name);
		txtAddress.setText(data[0]);
		txtCategory.setText(data[1]);
		txtRating.setText(data[2]);
		txtDate.setText(data[4]);
		txtDescription.setText(data[5]);

		double distance = Double.parseDouble(data[6]);

		txtDistance.setText((distance >= 1000 ? (int) (distance / 1000) + "km"
				: (int) (distance / 100) + "m"));
		imageLoader.displayImage(data[3], imageView);

		setTitle(name);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			super.onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
