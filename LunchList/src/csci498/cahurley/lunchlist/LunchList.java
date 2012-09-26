package csci498.cahurley.lunchlist;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LunchList extends ListActivity
{
	public final static String ID_EXTRA = "csci498.cahurley.lunchlist._ID";
	
	Cursor restaurants = null;
	RestaurantAdapter adapter = null;
	RestaurantHelper helper = null;
	SharedPreferences preferences = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        helper = new RestaurantHelper(this);
        restaurants = helper.getAll(preferences.getString("sort_order", "name"));
        startManagingCursor(restaurants);
        adapter = new RestaurantAdapter(restaurants);
        setListAdapter(adapter);
    }
    
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    	
    	helper.close();
    }
	
    @Override
    public void onListItemClick(ListView list, View view, int position, long id)
    {
    	Intent i = new Intent(LunchList.this, DetailForm.class);
    	
    	i.putExtra(ID_EXTRA, String.valueOf(id));
    	startActivity(i);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	new MenuInflater(this).inflate(R.menu.option, menu);
    	return(super.onCreateOptionsMenu(menu));
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	if(item.getItemId() == R.id.add)
    	{
    		startActivity(new Intent(LunchList.this, DetailForm.class));
    		
    		return(true);
    	}
    	else if(item.getItemId() == R.id.preferences)
    	{
    		startActivity(new Intent(LunchList.this, EditPreferences.class));
    		
    		return(true);
    	}
    	
    	return(super.onOptionsItemSelected(item));
    }
	
	class RestaurantAdapter extends CursorAdapter
	{
		@SuppressWarnings("deprecation")
		public RestaurantAdapter(Cursor c) 
		{
			super(LunchList.this, c);
		}
		
		@Override
		public void bindView(View row, Context context, Cursor cursor)
		{
			RestaurantHolder holder = (RestaurantHolder)row.getTag();
			
			holder.populateFrom(cursor, helper);
		}
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent)
		{
			LayoutInflater inflater = getLayoutInflater();
			View row = inflater.inflate(R.layout.restaurant_row, parent, false);
			
			RestaurantHolder holder = new RestaurantHolder(row);
			
			row.setTag(holder);
			
			return(row);
		}		
	}
	
	static class RestaurantHolder
	{
		private TextView name = null;
		private TextView address = null;
		private ImageView icon = null;
		
		public RestaurantHolder(View row) 
		{
			name = (TextView)row.findViewById(R.id.title);
			address = (TextView)row.findViewById(R.id.address);
			icon = (ImageView)row.findViewById(R.id.icon);
		}
		
		void populateFrom(Cursor cursor, RestaurantHelper helper)
		{
			String type = helper.getType(cursor);
			
			name.setText(helper.getName(cursor));
			address.setText(helper.getAddress(cursor));
			
			if(type.equals("sit_down"))
			{
				icon.setImageResource(R.drawable.sit_down_restaurant_icon);
			}
			else if(type.equals("take_out"))
			{
				icon.setImageResource(R.drawable.take_out_restaurant_icon);
			}
			else
			{
				icon.setImageResource(R.drawable.delivery_restaurant_icon);
			}
		}
	}
	
}
