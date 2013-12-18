package org.me.myandroidstuff.mapstuff;

import com.google.android.gms.maps.model.LatLng;

/* Class for creating a new venue
	This class will store the lat/long position of the venue,
	the venue title, the address and the sports held at this venue,
	along with an image to denote the venue on the map */

public class Venue {

	//Variables for each venue
	private LatLng mapPosition;
	private String title, address, sportsHeld, icon;
	
	//Getter/Setter Methods
	//Get map position
	public LatLng getMapPosition()
	{
		return mapPosition;
	}
	
	//Get venue title
	public String getVenueTitle()
	{
		return title;
	}
	
	//Get venue address
	public String getVenueAddress()
	{
		return address;
	}
	
	//Get/Set the sports held at this venue
	public String getVenueSports()
	{
		return sportsHeld;
	}
	
	//Get the icon representing the venue
	public String getVenueIcon()
	{
		return icon;
	}
	
	
	//Default constructor
	public Venue()
	{
		mapPosition = null;
		title = null;
		address = null;
		sportsHeld = null;
		icon = null;
	}
	
	//Constructor for full venue details
	public Venue(LatLng mapPos, String venueTitle, String venueAddress, String venueSports, String venueIcon)
	{
		mapPosition = mapPos;
		title = venueTitle;
		address = venueAddress;
		sportsHeld = venueSports;
		icon = venueIcon;
	}
	
	
}
