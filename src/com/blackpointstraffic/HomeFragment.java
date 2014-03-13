package com.blackpointstraffic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.blackpointstraffic.helper.GeoUtil;
import com.blackpointstraffic.helper.JsonParser;
import com.blackpointstraffic.model.GeoLocation;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {

	private ProgressDialog dialog;

	private JSONArray poiArr = null;
	private List<Map<String, String>> poiList;

	public HomeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_home, container,
				false);

		poiList = new ArrayList<Map<String, String>>();

		// call async task
		new GetPOIs().execute();

		return rootView;
	}

	private class GetPOIs extends AsyncTask<Void, Void, Void> {

		private String tag_id;
		private String tag_name;
		private String tag_geometry;

		public GetPOIs() {
			tag_id = getActivity().getString(R.string.TAG_ID);
			tag_name = getActivity().getString(R.string.TAG_NAME);
			tag_geometry = getActivity().getString(R.string.TAG_GEOMETRY);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = new ProgressDialog(getActivity());
			dialog.setMessage("Loading...");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try {
				poiArr = JsonParser.getJsonFromUrl(getActivity().getString(
						R.string.URL_GET_ALL_POI));

				for (int i = 0; i < poiArr.length(); i++) {
					JSONObject jsonObject = poiArr.getJSONObject(i);

					Map<String, String> map = new HashMap<String, String>();
					map.put(tag_id, jsonObject.getString(tag_id));
					map.put(tag_name, jsonObject.getString(tag_name));
					map.put(tag_geometry, jsonObject.getString(tag_geometry));

					poiList.add(map);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			// dismiss the progress dialog
			if (dialog.isShowing()) {
				dialog.dismiss();
			}

			String kq = "";
			for (Map<String, String> map : poiList) {
				kq += map.get(tag_id) + ":" + map.get(tag_name);
				List<GeoLocation> geoList = GeoUtil.toLatLng(map.get(tag_geometry));
				kq += "-" + "(" + geoList.get(0).getLat() + ", "
						+ geoList.get(0).getLng() + ")\n";
			}

			TextView textView = (TextView) getActivity().findViewById(R.id.txt);
			textView.setText(kq);
		}
	}
}
