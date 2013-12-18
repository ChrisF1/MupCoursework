package org.me.myandroidstuff.mapstuff;

import com.google.android.gms.maps.model.LatLng;

/* This class will store previous host cities as objects
 * Each previous host city object will include:
 * Country, host city, year and number of medals won by Scotland
 * It'll also store the position (longitude and latitude of city)
 * The position will be used to place the marker
 */
public class PreviousHost {

	//Variables for this class
	private String hostCountry, hostCity, icon;
	private int year, scotlandMedals;
	private LatLng position;
	
	
	//Create get/set methods for the class
	//Return host country
	public String getHostCountry()
	{
		return hostCountry;
	}
	//Return host city
	public String getHostCity()
	{
		return hostCity;
	}
	//Return host icon
	public String getIcon()
	{
		return icon;
	}
	//Return year which event was held
	public int getHostYear()
	{
		return year;
	}
	//Return number of medals won by Scotland
	public int getScotlandMedals()
	{
		return scotlandMedals;
	}
	//Return position of city
	public LatLng getPosition()
	{
		return position;
	}
	
	
	//Default constructor for this class
	public PreviousHost()
	{
		hostCountry = null;
		hostCity = null;
		year = 0;
		scotlandMedals = 0;
		position = null;
		icon = null;
	}
	
	//Full constructor for this class
	public PreviousHost(LatLng ph_Position, String ph_Country, String ph_City, int ph_Year, int ph_ScotMedals, String ph_Icon)
	{
		position = ph_Position;
		hostCountry = ph_Country;
		hostCity = ph_City;
		year = ph_Year;
		scotlandMedals = ph_ScotMedals;
		icon = ph_Icon;
	}
}
