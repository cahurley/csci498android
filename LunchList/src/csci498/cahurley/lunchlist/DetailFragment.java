package csci498.cahurley.lunchlist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class DetailFragment extends Fragment 
{
	private static final String ARG_REST_ID = "csci498.cahurley.lunchlist.ARG_REST_ID";
	
	EditText name = null;
	EditText address = null;
	EditText notes = null;
	EditText feed = null;
	TextView location = null;
	RadioGroup types = null;
	RestaurantHelper helper = null;
	LocationManager locationManager = null;
	double latitude = 0.0d;
	double longitude = 0.0d;
	String restaurantId = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.detail_form, container, false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
    	name = (EditText)getView().findViewById(R.id.name_edit_text);
    	address = (EditText)getView().findViewById(R.id.address_edit_text);
    	feed = (EditText)getView().findViewById(R.id.feed);
    	notes = (EditText)getView().findViewById(R.id.notes_edit_text);
    	location = (TextView)getView().findViewById(R.id.location_text_view);
    	types = (RadioGroup)getView().findViewById(R.id.types_of_restaurants_radio); 
    	
    	Bundle args = getArguments();
    	
    	if (args != null)
    	{
    		loadRestaurant(args.getString(ARG_REST_ID));
    	}
	}
    
	@Override
	public void onPause()
	{
		save();
		getHelper().close();
		locationManager.removeUpdates(onLocationChange);
		
		super.onPause();
	}
	
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
    	inflater.inflate(R.menu.details_option, menu);
    }
    
    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
    	if (restaurantId == null)
    	{
    		menu.findItem(R.id.location).setEnabled(false);
    		menu.findItem(R.id.map).setEnabled(false);
    	}
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	if (item.getItemId() == R.id.feed)
    	{
    		if (isNetworkAvailable())
    		{
    			Intent i = new Intent(getActivity(), FeedActivity.class);
    			
    			i.putExtra(FeedActivity.FEED_URL, feed.getText().toString());
    			startActivity(i);
    		}
    		else
    		{
    			Toast.makeText(getActivity(), "Sorry, the Internet is not available", Toast.LENGTH_LONG).show();
    		}
    		
    		return true;
    	}
    	else if (item.getItemId() == R.id.location)
    	{
    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, onLocationChange);
    		
    		return true;
    	}
    	else if (item.getItemId() == R.id.map)
    	{
    		Intent i = new Intent(getActivity(), RestaurantMap.class);
    		
    		i.putExtra(RestaurantMap.EXTRA_LATITUDE, latitude);
    		i.putExtra(RestaurantMap.EXTRA_LONGITUDE, longitude);
    		i.putExtra(RestaurantMap.EXTRA_NAME, name.getText().toString());
    		
    		startActivity(i);
    		
    		return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
	private RestaurantHelper getHelper()
	{
		if (helper == null)
		{
			helper = new RestaurantHelper(getActivity());
		}
		
		return helper;
	}
	
    private boolean isNetworkAvailable()
    {
    	ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo info = connectivityManager.getActiveNetworkInfo();
    	
    	return (info != null);
    }
    
    public void loadRestaurant(String restaurantId)
    {
    	this.restaurantId = restaurantId;
    	
    	if (restaurantId != null)
    	{
    		load();
    	}
    }
    
    private void load()
    {
    	Cursor cursor = getHelper().getById(restaurantId);
    	
    	cursor.moveToFirst();
    	name.setText(getHelper().getName(cursor));
    	address.setText(getHelper().getAddress(cursor));
    	notes.setText(getHelper().getNotes(cursor));
    	feed.setText(getHelper().getFeed(cursor));
    	location.setText(String.format("%s, %s", String.valueOf(getHelper().getLatitude(cursor)), String.valueOf(getHelper().getLongitude(cursor))));
    	
    	latitude = getHelper().getLatitude(cursor);
    	longitude = getHelper().getLongitude(cursor);
    	
    	String type = getHelper().getType(cursor);
    	if(type.equals("sit_down"))
    	{
    		types.check(R.id.sit_down);
    	}
    	else if(type.equals("take_out"))
    	{
    		types.check(R.id.take_out);
    	}
    	else
    	{
    		types.check(R.id.delivery);
    	}
    	
    	cursor.close();
    }
    
    private void save()
    {	
    	if (name.getText().toString().length() > 0)
    	{
    		String type = getRestaurantType();
    		
			if(restaurantId == null)
			{
				getHelper().insert(name.getText().toString(), address.getText().toString(), type, notes.getText().toString(), feed.getText().toString());
			}
			else
			{
				getHelper().update(restaurantId, name.getText().toString(), address.getText().toString(), type, notes.getText().toString(), feed.getText().toString());
			}
    		
    	}
	}
    
	String getRestaurantType()
	{
		switch(types.getCheckedRadioButtonId())
		{
			case R.id.sit_down:
				return("sit_down");
			case R.id.take_out:
				return("take_out");
			case R.id.delivery:
				return("delivery");
			default:
				return("sit_down");
		}
	}
	
	LocationListener onLocationChange = new LocationListener()
	{
		public void onLocationChanged(Location fix)
		{
			getHelper().updateLocation(restaurantId, fix.getLatitude(), fix.getLongitude());
			location.setText(String.format("%s, %s", String.valueOf(fix.getLatitude()), String.valueOf(fix.getLongitude())));
			locationManager.removeUpdates(onLocationChange);
			
			Toast.makeText(getActivity(), "Location Saved", Toast.LENGTH_LONG).show();
		}
		
		public void onProviderDisabled(String provider)
		{
			
		}
		
		public void onProviderEnabled(String provider)
		{
			
		}
		
		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			
		}
	};
	
	public static DetailFragment newInstance(long id)
	{
		DetailFragment result = new DetailFragment();
		Bundle args = new Bundle();
		
		args.putString(ARG_REST_ID, String.valueOf(id));
		result.setArguments(args);
		
		return result;
	}
	
}
