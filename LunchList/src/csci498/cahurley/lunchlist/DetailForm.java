package csci498.cahurley.lunchlist;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class DetailForm extends Activity 
{
	EditText name = null;
	EditText address = null;
	EditText notes = null;
	EditText feed = null;
	RadioGroup types = null;
	RestaurantHelper helper = null;
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
    	types = (RadioGroup)findViewById(R.id.types_of_restaurants_radio);
    	notes = (EditText)findViewById(R.id.notes_edit_text);
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
    	
    	return (super.onOptionsItemSelected(item));
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
}
