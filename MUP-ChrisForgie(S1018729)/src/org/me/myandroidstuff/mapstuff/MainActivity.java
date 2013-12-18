package org.me.myandroidstuff.mapstuff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener, OnMarkerClickListener
{
	//Variables for the activity
	private GoogleMap googleMap; //Google map element
	private LatLng ll_Glasgow; //Default Glasgow Location
	
	//Set up a Venue object for each Glasgow commonwealth venue
	//Then store them in an ArrayList so we can loop through and access each one
	private Venue commonwealthVenue;
	private ArrayList<Venue> venueLocations;
	//Marker objects for each venue
	//Then store these in an ArrayList so we can check which one has been clicked
	private Marker venueMarker;
	private ArrayList<Marker> arrayMarkers;
	
	//Set up a PreviousHost object for each previous host city
	//Then store these in an ArrayList for easier access
	private PreviousHost pastHost;
	private ArrayList<PreviousHost> previousHosts;
	private Marker pastHostMarker; 
	private boolean b_pastHost; //True when a marker has been placed
	private int pastHostPos; //Position of past host in ArrayList (Used to display dialog box)
	
	//The textbox for the user to enter their location, the marker for it and the boolean to see if its enabled
	//i.e. only draw one marker for each new location (total)
	private EditText textLocation;
	private Marker locationMarker;
	private boolean b_locationPos; //True when a marker has been placed for the user entering their position
	private LatLng myEnteredPos; //Lat/Lng of the users position which they enter. Defined in onPostExecute
	private Polyline distanceLine; //A line between our entered location and Glasgow
	private boolean b_distanceLine; //Have we drawn a line? If so remove it when we enter a new location
	
	//Marker for the GPS position and boolean value to check if it's placed, if GPS position is changed
	private Marker gpsMarker;
	private boolean b_gpsMarker;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Retrieve the map element from main.xml
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		if(googleMap == null)
		{
			//Throw an error that the map has not been found
			Toast.makeText(getApplicationContext(), "The map element was not found.", Toast.LENGTH_SHORT).show();
		}
		else{
			//The map exists
			
			//Set up the map i.e. default location
			setUpMap();
			
			//Set up the buttons/spinners/edit text overlaid on the map
			setMapButtons();
			
			//Set up the 14 venues, their location, title and sports held
			setCommonwealthVenues();
			
			//Place the 14 venue markers on the map
			setVenueMarkers();
			
			//Set up the previous 10 host cities
			setPreviousCities();
		}
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//When the user clicks on a marker
	@SuppressWarnings("deprecation")
	@Override
	//When a marker has been clicked, open a dialog box
	public boolean onMarkerClick(Marker marker) {

		//Loop through to check if we have clicked any of the Glasgow 2014 venue markers
		for(int i=0; i<arrayMarkers.size(); i++){
			if(marker.equals(arrayMarkers.get(i)))
			{
				//Get the venue details for this marker
				Venue venueDetails = venueLocations.get(i);
				
				AlertDialog venueInfo = new AlertDialog.Builder(MainActivity.this).create();
		        //Set the title and information of the venue
				venueInfo.setTitle(venueDetails.getVenueTitle());
				venueInfo.setMessage("Address: " + venueDetails.getVenueAddress() + ".\n\nSports Held Here: " + venueDetails.getVenueSports() + ".");
				// Setting Icon to Dialog
				venueInfo.setIcon(getResources().getIdentifier(venueDetails.getVenueIcon(), "drawable", getPackageName()));
		        // Setting OK Button
				venueInfo.setButton("OK", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
		                }
		        });
		        // Showing Alert Message
		        venueInfo.show();
		        
				return true;
			}
		}
		
		//Check for the other marker we can click (past host city)
		if(marker.equals(pastHostMarker))
		{
			//Get the details for this host city
			PreviousHost cityDetails = previousHosts.get(pastHostPos);
			
			AlertDialog cityInfo = new AlertDialog.Builder(MainActivity.this).create();
	        //Set the title and information of the venue
			cityInfo.setTitle(cityDetails.getHostCity() + " " + cityDetails.getHostYear());
			cityInfo.setMessage("Country: " + cityDetails.getHostCountry() + "\nCity: " + cityDetails.getHostCity() + "\nYear: " + cityDetails.getHostYear() + "\nMedals won by Scotland: " + cityDetails.getScotlandMedals());
			// Setting Icon to Dialog
			cityInfo.setIcon(getResources().getIdentifier(cityDetails.getIcon(), "drawable", getPackageName()));
	        // Setting OK Button
			cityInfo.setButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
	                }
	        });
	        // Showing Alert Message
	        cityInfo.show();
	        
			return true;
		}
		
		return false;
	}
	
	//When the user clicks on a button
	@Override
	public void onClick(View v) {

		//Check which button has been pressed
		switch(v.getId()){
			case R.id.gpsPosition:
				LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
				boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);
				if (enabled)
				{
					Criteria criteria = new Criteria();
				    String bestProvider = service.getBestProvider(criteria, false);
				    Location location = service.getLastKnownLocation(bestProvider);
					if(location != null)
					{
						//If a marker already exists for GPS position, remove it and position a new one
						if(b_gpsMarker)
						{
							gpsMarker.remove();
						}
						//Create a marker for our GPS position
						MarkerOptions gpsMarkerDet = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("GPS Position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));;
						gpsMarker = googleMap.addMarker(gpsMarkerDet);
						googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));
						b_gpsMarker = true; //We've placed a marker for the GPS position
					}
					else
					{
						Toast.makeText(getApplicationContext(), "GPS Unavailable", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case R.id.locationText:
				textLocation.setText(null);
				break;
			case R.id.locationButton:
				//Remove any lines on the new map for a new location even blank
				//so I'll stick it up here
				if(b_distanceLine){
					distanceLine.remove();
					b_distanceLine = false;
				}
				// Getting the place entered
                String location = textLocation.getText().toString();
                if(location==null || location.equals("")){
                    Toast.makeText(getBaseContext(), "No Place is entered", Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = "https://maps.googleapis.com/maps/api/geocode/json?";
 
                try {
                    //Encode any special characters such as "space"
                    location = URLEncoder.encode(location, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
 
                String address = "address=" + location;
                String sensor = "sensor=false";
                //Form the url from where to get the information from
                url = url + address + "&" + sensor;
 
                //Instantiating a downloadTask
                DownloadTask downloadTask = new DownloadTask();
                //Start downloading information from the URL provided
                //This method is at the bottom of this class
                downloadTask.execute(url);
				break;
			case R.id.distanceHome:
				//We have placed a marker for our location
				if(b_locationPos){
					//Draw a line between our entered location and Glasgow
					distanceLine = googleMap.addPolyline(new PolylineOptions().add(ll_Glasgow, myEnteredPos).width(5).color(Color.RED));
					b_distanceLine = true; //Set line drawn to true
					
					//Work out distance between the two points
					float distancePoints = distBetween(ll_Glasgow.latitude, ll_Glasgow.longitude, myEnteredPos.latitude, myEnteredPos.longitude);
					//Display a toast showing distance
					Toast.makeText(getBaseContext(), "Distance between your entered location and Glasgow is: " + String.format("%.1f", distancePoints) + "km", Toast.LENGTH_LONG).show();
				}else{
					//We haven't placed a marker yet
					Toast.makeText(getBaseContext(), "Please enter a location.", Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}

	//The following two methods (onItemSelected and onNothingSelected)
	//are for when the user clicks on a spinner
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) 
	{
		Spinner clickedSpinner = (Spinner)parent;
		
		//The user is changing the map view
		if(clickedSpinner.getId() == R.id.mapView)
		{
			switch(pos)
			{
				case 0:
				//Map view: normal
				googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				break;
				case 1:
				//Map view: satellite
				googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				break;
				case 2:
				//Map view: terrain
				googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				break;
				case 3:
				//Map view: hybrid
				googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				break;
			}
		}
		
		//The user is changing to a previous host cities
		if(clickedSpinner.getId() == R.id.previousCities)
		{
			switch(pos)
			{
				case 0:
				//City: Glasgow
					//Set map position to Glasgow and animate camera
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(previousHosts.get(0).getPosition(), 15));
					googleMap.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null); 
					//NOT placing a marker for glasgow
				break;
				//The default case should work for all other cities
				//Glasgow is the only difference as it doesn't need a a marker
				default:
					//Move camera to city chosen
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(previousHosts.get(pos).getPosition(), 20));
					googleMap.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null); 
					//Create a marker for this past host
					MarkerOptions pastHostMrkDet = new MarkerOptions().position(previousHosts.get(pos).getPosition()).title(previousHosts.get(pos).getHostCity()).icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier(previousHosts.get(pos).getIcon(), "drawable", getPackageName())));;
					//If the marker already exists then remove it. Only show 1 marker for a previous host city at one time
					if(b_pastHost)
					{
						pastHostMarker.remove();
					}
					pastHostMarker = googleMap.addMarker(pastHostMrkDet);
					b_pastHost = true; //Set marker placed to true
					pastHostPos = pos;
					break;
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) 
	{
		//Do nothing
	}
	
	
	/* Class Methods Below */
	
	//Sets up the map with the default location Glasgow
	//and zooms in towards it
	private void setUpMap()
	{
		//Set up the default location of glasgow so the map can zoom to it (startup)
		ll_Glasgow = new LatLng(55.864237, -4.251806); //http://itouchmap.com/latlong.html (Search: Glasgow Central Station)
		//Set the map initial position to Glasgow
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll_Glasgow, 15));
		// Zoom in, animating the camera.
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(10f), 2000, null); 
		
		//Set GPS enabled but hide the official button so I can use my own button
		googleMap.getUiSettings().setMyLocationButtonEnabled(false);
		googleMap.setMyLocationEnabled(true);
		
		//Set on marker click listener
		googleMap.setOnMarkerClickListener(this);
	}
	
	
	//Finds the buttons/spinners from the layout file and sets up the OnClickListener for them
	private void setMapButtons()
	{
		//Find each spinner on the map
		Spinner mapView = (Spinner)findViewById(R.id.mapView);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.map_views));
	    mapView.setAdapter(dataAdapter);
		Spinner previousCities = (Spinner)findViewById(R.id.previousCities);
		dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.host_cities));
	    previousCities.setAdapter(dataAdapter);

		//Find each button on the map
		Button gpsLocation = (Button)findViewById(R.id.gpsPosition);
		Button findLocation = (Button)findViewById(R.id.locationButton);
		Button distanceHome = (Button)findViewById(R.id.distanceHome);
		
		//The edit text for the user to enter their location
		textLocation = (EditText)findViewById(R.id.locationText);
		
		//Set up on click listeners for the buttons/spinners
		mapView.setOnItemSelectedListener(this);
		previousCities.setOnItemSelectedListener(this);
		gpsLocation.setOnClickListener(this);
		textLocation.setOnClickListener(this);
		findLocation.setOnClickListener(this);
		distanceHome.setOnClickListener(this);
		
	}
	
	//Set up the locations and details for each venue at Glasgow 2014
	//Sets up each of the 14 venues for the commonwealth
	//Their locations, title, address and sports held
	private void setCommonwealthVenues()
	{
		//Create the ArrayList to store all of these locations
		venueLocations = new ArrayList<Venue>();
		
		/*
		 * NOTE: The longitude and latitude of most of the venues were obtained from the following website
		 * http://itouchmap.com/latlong.html
		 * The address of most of the venues were found using Google Maps
		 */
		
		/* Athletes Village Details:
		 * //http://itouchmap.com/latlong.html (Dragged pin to location to find latitude and longitude)
		 */
		commonwealthVenue = new Venue(new LatLng(55.838896, -4.18143), "Athletes Village", "London Road, Glasgow", "None. Home of athletes during the games", "ic_commonwealth");
		venueLocations.add(commonwealthVenue);
		
		/* Barry Buddon Shooting Centre Details:
		 * //http://itouchmap.com/latlong.html (Search: DD7 7RY)
		 * Postcode found from http://www.glasgow2014packages.com/?venue=barry-buddon-shooting-centre
		 */
		commonwealthVenue = new Venue(new LatLng(56.484432, -2.780588), "Barry Buddon Shooting Centre", "Buddon, Dundee", "Shooting", "ic_shooting");
		venueLocations.add(commonwealthVenue);
		commonwealthVenue = new Venue(new LatLng(55.797496, -4.221868), "Cathkin Braes", "Cathkin Road, Rutherglen", "Cycling: Mountain Bike", "ic_cycling");
		venueLocations.add(commonwealthVenue);
		commonwealthVenue = new Venue(new LatLng(55.849722, -4.205556), "Celtic Park", "Parkhead, Glasgow", "None. Opening Cermony", "ic_commonwealth");
		venueLocations.add(commonwealthVenue);
		commonwealthVenue = new Venue(new LatLng(55.846847, -4.207008), "Emirates Arena", "1000 London Rd, Glasgow", "Badminton and Cycling: Track", "ic_badminton");
		venueLocations.add(commonwealthVenue);
		commonwealthVenue = new Venue(new LatLng(55.84496, -4.236709), "Glasgow Hockey Centre", "Glasgow Green, Glasgow", "Hockey", "ic_hockey");
		venueLocations.add(commonwealthVenue);
		commonwealthVenue = new Venue(new LatLng(55.825703, -4.252386), "Hampden Park", "Letherby Dr, Glasgow", "Athletics", "ic_athletics");
		venueLocations.add(commonwealthVenue);
		commonwealthVenue = new Venue(new LatLng(55.853316, -4.308668), "Ibrox Stadium", "150 Edmiston Dr, Glasgow", "Rugby", "ic_rugby");
		venueLocations.add(commonwealthVenue);
		commonwealthVenue = new Venue(new LatLng(55.867324, -4.288245), "Kelvingrove Lawn Bowls Centre", "Kelvin Way, Glasgow", "Lawn Bowls", "ic_lawnbowls");
		venueLocations.add(commonwealthVenue);
		commonwealthVenue = new Venue(new LatLng(55.939202, -3.172731), "Royal Commonwealth Pool", "21 Dalkeith Rd, Edinburgh", "Aquatics: Diving", "ic_aquatics");
		venueLocations.add(commonwealthVenue);
		commonwealthVenue = new Venue(new LatLng(55.860914, -4.288986), "S.E.C.C Precinct", "Exhibition Way, Glasgow", "Netball, Gymnastics, Wrestling, Weightlifting, Boxing and Judo", "ic_netball");
		venueLocations.add(commonwealthVenue);
		commonwealthVenue = new Venue(new LatLng(55.881685, -4.3422), "Scotstoun Sports Campus", "72 Duncan Ave, Glasgow", "Squash and Table Tennis", "ic_squash");
		venueLocations.add(commonwealthVenue);
		commonwealthVenue = new Venue(new LatLng(55.785288, -4.014812), "Strathcylde Country Park", "366 Hamilton Rd, Motherwell", "Triathlon", "ic_triathlon");
		venueLocations.add(commonwealthVenue);
		commonwealthVenue = new Venue(new LatLng(55.845049, -4.176075), "Tollcross Swimming", "367 Wellshot Rd, Glasgow", "Aquatics: Swimming", "ic_aquatics");
		venueLocations.add(commonwealthVenue);
	}

	
	//Place the markers for each venue in Glasgow
	//Loops through each venue location and adds a marker to the map
	private void setVenueMarkers()
	{
		//Loop through each venue and add a marker to the map
		//Then add each marker to an ArrayList of markers
		arrayMarkers = new ArrayList<Marker>();
		for(int i=0; i<venueLocations.size(); i++)
		{
			Venue currentVenue = venueLocations.get(i);
			
			//Create a marker for this current venue
			MarkerOptions venueMarkerDet = new MarkerOptions().position(currentVenue.getMapPosition()).title(currentVenue.getVenueTitle()).icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier(currentVenue.getVenueIcon(), "drawable", getPackageName())));;
			venueMarker = googleMap.addMarker(venueMarkerDet);
			//Add this to the array list of markers
			arrayMarkers.add(venueMarker);
		}
	}

	
	private void setPreviousCities()
	{
		//Set up array list
		previousHosts = new ArrayList<PreviousHost>();
		
		/*
		 * NOTE: The longitude and latitude of each of these cities were found using
		 * http://itouchmap.com/latlong.html
		 * Details about medals won by Scotland were found on Wikipedia:
		 * http://en.wikipedia.org/wiki/Scotland_at_the_Commonwealth_Games
		 */
		
		//Add Glasgow in as the current host, although a marker won't be placed for Glasgow
		//This'll be for when the user is viewing a different country and picks Glasgow then the map
		//will zoom back to Glasgow.
		pastHost = new PreviousHost(new LatLng(55.864237, -4.251806), "Scotland", "Glasgow", 2014, 0, "icph_scotland");
		previousHosts.add(pastHost);
		pastHost = new PreviousHost(new LatLng(28.635308, 77.22496), "India", "Delhi", 2010, 26, "icph_india");
		previousHosts.add(pastHost);
		pastHost = new PreviousHost(new LatLng(-37.814107, 144.96328), "Australia", "Melbourne", 2006, 29, "icph_australia");
		previousHosts.add(pastHost);
		pastHost = new PreviousHost(new LatLng(53.479324, -2.248485), "England", "Manchester", 2002, 29, "icph_england");
		previousHosts.add(pastHost);
		pastHost = new PreviousHost(new LatLng(3.139003, 101.686855), "Malaysia", "Kuala Lumpur", 1998, 12, "icph_malaysia");
		previousHosts.add(pastHost);
		pastHost = new PreviousHost(new LatLng(48.428421, -123.365644), "Canada", "Victoria", 1994, 20, "icph_canada");
		previousHosts.add(pastHost);
		pastHost = new PreviousHost(new LatLng(-36.84846, 174.763332), "New Zealand", "Auckland", 1990, 22, "icph_newzealand");
		previousHosts.add(pastHost);
		pastHost = new PreviousHost(new LatLng(55.953252, -3.188267), "Scotland", "Edinburgh", 1986, 33, "icph_scotland");
		previousHosts.add(pastHost);
		pastHost = new PreviousHost(new LatLng(-27.471011, 153.023449), "Australia", "Brisbane", 1982, 26, "icph_australia");
		previousHosts.add(pastHost);
		pastHost = new PreviousHost(new LatLng(53.544389, -113.490927), "Canada", "Edmonton", 1978, 14, "icph_canada");
		previousHosts.add(pastHost);
		pastHost = new PreviousHost(new LatLng(-43.532054, 172.636225), "New Zealand", "Christchurch", 1974, 19, "icph_newzealand");
		previousHosts.add(pastHost);
	}

	//Method for working out the distance between two lat/lng positions
	//Obtained/Adapted from: http://stackoverflow.com/questions/19479667/how-can-i-calculate-a-current-distance-to-polyline-points-on-google-maps-v2-in-a
	public static float distBetween(double lat1, double lng1, double lat2, double lng2) {
	    double earthRadius = 3958.75;
	    double dLat = Math.toRadians(lat2 - lat1);
	    double dLng = Math.toRadians(lng2 - lng1);
	    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
	            + Math.cos(Math.toRadians(lat1))
	            * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
	            * Math.sin(dLng / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double dist = earthRadius * c;

	    float metreConversion = 1.609f;

	    return (float) (dist * metreConversion);
	}
	
	/* Geocoding Methods Below
	 * These methods take a url and retrieve the JSON code
	 * parse it then return the latitude and longitude of the 
	 * position entered, then place a pin on the map. 
	 * Obtained from: http://wptrafficanalyzer.in/blog/locating-user-input-address-in-google-maps-android-api-v2-with-geocoding-api/
	 * */
	
	/* These are a little edited by me but not much
	 * Just the placing of the marker and its style to make 
	 * it more suiting to the rest of the application.
	 */
	
	private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
 
            data = sb.toString();
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
 
        return data;
    }
    /** A class, to download Places from Geocoding webservice */
    private class DownloadTask extends AsyncTask<String, Integer, String>{
 
        String data = null;
 
        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){
 
            // Instantiating ParserTask which parses the json data from Geocoding webservice
            // in a non-ui thread
            ParserTask parserTask = new ParserTask();
 
            // Start parsing the places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }
 
    /** A class to parse the Geocoding Places in non-ui thread */
    class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
 
        JSONObject jObject;
 
        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {
 
            List<HashMap<String, String>> places = null;
            GeocodeJSONParser parser = new GeocodeJSONParser();
 
            try{
                jObject = new JSONObject(jsonData[0]);
 
                /** Getting the parsed data as a an ArrayList */
                places = parser.parse(jObject);
 
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }
 
        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){
 
            // Clears all the existing markers
        	//Don't do this as I would like to keep all venue markers
            //googleMap.clear();
 
            for(int i=0;i<list.size();i++){
 
                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();
 
                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);
 
                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));
 
                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));
                
                //Store these lat/lng co-ordinates so they can be accessed later
                myEnteredPos = new LatLng(Double.parseDouble(hmPlace.get("lat")), Double.parseDouble(hmPlace.get("lng")));
 
                // Getting name
                String name = hmPlace.get("formatted_address");
 
                LatLng latLng = new LatLng(lat, lng);
 
                // Setting the position for the marker
                markerOptions.position(latLng);
 
                // Setting the title for the marker
                markerOptions.title("My Location: " + name);
                
                //Make the marker icon green
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
 
                //If a marker is already placed for a location then remove it
                if(b_locationPos){
                	locationMarker.remove();
                }
                
                // Placing a marker on the touched position
                locationMarker = googleMap.addMarker(markerOptions);
                
                //Set marker placed to true
                b_locationPos = true;
 
	            //Move the camera to the marker entered
	            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 11));
                
            }
        }
    }
}
