package com.blackpointstraffic.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blackpointstraffic.model.GeoLocation;

public class GeoUtil {
	public static List<GeoLocation> toLatLng(String geoString) {
		List<GeoLocation> geoList = new ArrayList<GeoLocation>();
		List<Double> coordinatesList = new ArrayList<Double>();

		Matcher m = Pattern.compile("\\([^\\(\\)]+\\)").matcher(geoString);

		while (m.find()) {
			Matcher m2 = Pattern.compile("-?\\d+\\.?\\d*").matcher(m.group());

			while (m2.find()) {
				coordinatesList.add(Double.parseDouble(m2.group()));
			}
		}

		int i;
		for (i = 0; i < coordinatesList.size(); i += 2) {
			GeoLocation gl = new GeoLocation();
			gl.setLng(coordinatesList.get(i));
			gl.setLat(coordinatesList.get(i + 1));

			geoList.add(gl);
		}
		return geoList;
	}
}
