package csci498.cahurley.lunchlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
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

public class LunchFragment extends ListFragment
{
	public final static String ID_EXTRA = "csci498.cahurley.lunchlist._ID";
	
	Cursor restaurants = null;
	RestaurantAdapter adapter = null;
	RestaurantHelper helper = null;
	SharedPreferences preferences = null;
	OnRestaurantListener listener = null;
	
	@Override
	public void onCreate(Bundle state)
	{
		super.onCreate(state);
		
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		helper = new RestaurantHelper(getActivity());
		preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		initList();
        preferences.registerOnSharedPreferenceChangeListener(preferenceListener);
	}
    
    @Override
    public void onPause()
    {
    	helper.close();
    	super.onPause();
    }
	
    @Override
    public void onListItemClick(ListView list, View view, int position, long id)
    {
    	if (listener != null)
    	{
    		listener.onRestaurantSelected(id);
    	}
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
    	inflater.inflate(R.menu.option, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	if(item.getItemId() == R.id.add)
    	{
    		startActivity(new Intent(getActivity(), DetailForm.class));
    		
    		return true;
    	}
    	else if(item.getItemId() == R.id.preferences)
    	{
    		startActivity(new Intent(getActivity(), EditPreferences.class));
    		
    		return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
	public void setOnRestaurantListener(OnRestaurantListener listener)
	{
		this.listener = listener;
	}
    
    private void initList()
    {
    	if(restaurants != null)
    	{
    		restaurants.close();
    	}
    	
    	restaurants = helper.getAll(preferences.getString("sort_order", "name"));
    	adapter = new RestaurantAdapter(restaurants);
    	setListAdapter(adapter);
    }
	
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener()
    {	
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) 
		{
			if(key.equals("sort_order"))
			{
				initList();
			}
		}
	};
    
	class RestaurantAdapter extends CursorAdapter
	{
		@SuppressWarnings("deprecation")
		public RestaurantAdapter(Cursor c) 
		{
			super(getActivity(), c);
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
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View row = inflater.inflate(R.layout.restaurant_row, parent, false);
			
			RestaurantHolder holder = new RestaurantHolder(row);
			
			row.setTag(holder);
			
			return row;
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
	
	public interface OnRestaurantListener
	{		
		void onRestaurantSelected(long id);
	}
	
}
