package csci498.cahurley.lunchlist;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class DetailForm extends Activity 
{
	EditText name = null;
	EditText address = null;
	EditText notes = null;
	EditText feed = null;
	TextView location = null;
	RadioGroup types = null;
	RestaurantHelper helper = null;
	LocationManager locationManager = null;
	String restaurantId = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_form);
		
		setMemberVariables();
		
		restaurantId = getIntent().getStringExtra(LunchList.ID_EXTRA);
		if(restaurantId != null)
		{
			load();
		}
		
		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
	}
	
	@Override
	public void onSaveInstanceState(Bundle state)
	{
		super.onSaveInstanceState(state);
		
		state.putString("name", name.getText().toString());
		state.putString("address", address.getText().toString());
		state.putString("notes", notes.getText().toString());
		state.putInt("type", types.getCheckedRadioButtonId());
	}
	
    private void setMemberVariables()
    {
    	helper = new RestaurantHelper(this);
    	name = (EditText)findViewById(R.id.name_edit_text);
    	address = (EditText)findViewById(R.id.address_edit_text);
    	feed = (EditText)findViewById(R.id.feed);
    	notes = (EditText)findViewById(R.id.notes_edit_text);
    	location = (TextView)findViewById(R.id.location_text_view);
    	types = (RadioGroup)findViewById(R.id.types_of_restaurants_radio);    	
    }
    
	@Override
	public void onRestoreInstanceState(Bundle state)
	{
		super.onRestoreInstanceState(state);
		
		name.setText(state.getString("name"));
		address.setText(state.getString("address"));
		notes.setText(state.getString("notes"));
		types.check(state.getInt("type"));
	}
    
	@Override
	public void onPause()
	{
		save();
		locationManager.removeUpdates(onLocationChange);
		
		super.onPause();
	}
	
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    	
    	helper.close();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	new MenuInflater(this).inflate(R.menu.details_option, menu);
    	
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
    	if (restaurantId == null)
    	{
    		menu.findItem(R.id.location).setEnabled(false);
    		menu.findItem(R.id.map).setEnabled(false);
    	}
    	
    	return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	if (item.getItemId() == R.id.feed)
    	{
    		if (isNetworkAvailable())
    		{
    			Intent i = new Intent(this, FeedActivity.class);
    			
    			i.putExtra(FeedActivity.FEED_URL, feed.getText().toString());
    			startActivity(i);
    		}
    		else
    		{
    			Toast.makeText(this, "Sorry, the Internet is not available", Toast.LENGTH_LONG).show();
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
    		Intent i = new Intent(this, RestaurantMap.class);
    		startActivity(i);
    		
    		return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
    private boolean isNetworkAvailable()
    {
    	ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
    	NetworkInfo info = connectivityManager.getActiveNetworkInfo();
    	
    	return (info != null);
    }
    
    private void load()
    {
    	Cursor cursor = helper.getById(restaurantId);
    	
    	cursor.moveToFirst();
    	name.setText(helper.getName(cursor));
    	address.setText(helper.getAddress(cursor));
    	notes.setText(helper.getNotes(cursor));
    	feed.setText(helper.getFeed(cursor));
    	location.setText(String.format("%s, %s", String.valueOf(helper.getLatitude(cursor)), String.valueOf(helper.getLongitude(cursor))));
    	
    	String type = helper.getType(cursor);
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
				helper.insert(name.getText().toString(), address.getText().toString(), type, notes.getText().toString(), feed.getText().toString());
			}
			else
			{
				helper.update(restaurantId, name.getText().toString(), address.getText().toString(), type, notes.getText().toString(), feed.getText().toString());
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
			helper.updateLocation(restaurantId, fix.getLatitude(), fix.getLongitude());
			location.setText(String.format("%s, %s", String.valueOf(fix.getLatitude()), String.valueOf(fix.getLongitude())));
			locationManager.removeUpdates(onLocationChange);
			
			Toast.makeText(DetailForm.this, "Location Saved", Toast.LENGTH_LONG).show();
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
}
