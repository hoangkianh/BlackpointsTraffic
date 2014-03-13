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

	public static double caculateDistance(GeoLocation geo1, GeoLocation geo2) {

		double radLat1 = Math.toRadians(geo1.getLat());
		double radLng1 = Math.toRadians(geo1.getLng());
		double radLat2 = Math.toRadians(geo2.getLat());
		double radLng2 = Math.toRadians(geo2.getLng());

		double distance = Math.acos(Math.sin(radLat1) * Math.sin(radLat2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.cos(radLng2 - radLng1)) * 6371000;
		return distance;
	}
}
