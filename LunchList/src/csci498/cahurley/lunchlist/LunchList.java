package csci498.cahurley.lunchlist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.TabActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class LunchList extends TabActivity
{
	List<Restaurant> restaurants = new ArrayList<Restaurant>();
	RestaurantAdapter adapter = null;
	EditText name = null;
	EditText address = null;
	RadioGroup types = null;
	EditText notes = null;
	Restaurant currentRestaurant = null;
	int progress;
	AtomicBoolean isActive = new AtomicBoolean(true);
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_main);
        
        setMemberVariables();
        
        if(savedInstanceState != null)
        {
        	name.setText(savedInstanceState.getString("name_text_field"));
        	address.setText(savedInstanceState.getString("address_text_field"));
        	notes.setText(savedInstanceState.getString("notes_text_field"));
        	
        	types.check(savedInstanceState.getInt("restaurant_type"));
        }
        
        configureSaveButton();
        configureList();
        configureTabs();
    }
    
    private void doSomeLongWork(final int increment)
    {
    	runOnUiThread(new Runnable()
    	{
    		public void run()
    		{
    			progress += increment;
    			setProgress(progress);
    		}
    	});
    	
    	SystemClock.sleep(250);
    }
    
    private Runnable longTask = new Runnable()
    {
    	public void run()
    	{
    		for(int i = progress; i < 10000 && isActive.get(); i+=200)
    		{
    			doSomeLongWork(200);
    		}
    		
    		if(isActive.get())
    		{
	    		runOnUiThread(new Runnable()
	    		{
	    			public void run()
	    			{
	    				setProgressBarVisibility(false);
	    			}
	    		});
    		}
    	}
    };
    
    @Override
    public void onPause()
    {
    	super.onPause();
    	
    	isActive.set(false);
    }
    
    @Override
	protected void onSaveInstanceState(Bundle outState) 
    {
		super.onSaveInstanceState(outState);
		
		outState.putString("name_text_field", name.getText().toString());
		outState.putString("address_text_field", address.getText().toString());
		outState.putString("notes_text_field", notes.getText().toString());
		
		outState.putInt("restaurant_type", types.getCheckedRadioButtonId());
	}

	@Override
    public void onResume()
    {
    	super.onResume();
    	
    	isActive.set(true);
    	
    	if(progress > 0)
    	{
    		startWork();
    	}
    }
    
    private void startWork()
    {
    	setProgressBarVisibility(true);
    	new Thread(longTask).start();
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
    	if(item.getItemId() == R.id.toast)
    	{
    		String message = "No restaurant selected";
    		
    		if(currentRestaurant != null)
    		{
    			message = currentRestaurant.getNotes();
    		}
    		
    		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    		
    		return(true);
    	}
    	else if(item.getItemId() == R.id.run)
    	{
    		startWork();
    		
    		return(true);
    	}
    	
    	return(super.onOptionsItemSelected(item));
    }
    
    private void setMemberVariables()
    {
    	name = (EditText)findViewById(R.id.name_edit_text);
    	address = (EditText)findViewById(R.id.address_edit_text);
    	types = (RadioGroup)findViewById(R.id.types_of_restaurants_radio);
    	notes = (EditText)findViewById(R.id.notes_edit_text);
    }
    
    private void configureSaveButton()
    {
        Button saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(onSave);
    }
    
    private void configureList()
    {
        ListView list = (ListView)findViewById(R.id.restaurants);
        adapter = new RestaurantAdapter();
        list.setAdapter(adapter);
        
        list.setOnItemClickListener(onListClick);
    }
    
    private void configureTabs()
    {
    	TabHost.TabSpec spec = getTabHost().newTabSpec("tag1");
    	
    	spec.setContent(R.id.restaurants);
    	spec.setIndicator("List", getResources().getDrawable(R.drawable.list_tab_icon));
    	getTabHost().addTab(spec);
    	
    	spec = getTabHost().newTabSpec("tag2");
    	spec.setContent(R.id.details);
    	spec.setIndicator("Details", getResources().getDrawable(R.drawable.restaurant_tab_icon));
    	getTabHost().addTab(spec);
    	
    	getTabHost().setCurrentTab(0);
    }
    
    private View.OnClickListener onSave = new View.OnClickListener()
    {	
		public void onClick(View v) 
		{
			currentRestaurant = new Restaurant();
			setRestaurantValues(currentRestaurant, name, address, notes);
			
			adapter.add(currentRestaurant);
		}
		
		void setRestaurantValues(Restaurant restaurant, EditText name, EditText address, EditText notes)
		{
			restaurant.setName(name.getText().toString());
			restaurant.setAddress(address.getText().toString());			
			restaurant.setType(getRestaurantType());
			restaurant.setNotes(notes.getText().toString());
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
		
	};
	
	private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			currentRestaurant = restaurants.get(position);
			
			name.setText(currentRestaurant.getName());
			address.setText(currentRestaurant.getAddress());
			notes.setText(currentRestaurant.getNotes());
			
			if(currentRestaurant.getType().equals("sit_down"))
			{
				types.check(R.id.sit_down);
			}
			else if(currentRestaurant.getType().equals("take_out"))
			{
				types.check(R.id.take_out);
			}
			else
			{
				types.check(R.id.delivery);
			}
			
			getTabHost().setCurrentTab(1);
		}
	};
	
	class RestaurantAdapter extends ArrayAdapter<Restaurant>
	{
		public RestaurantAdapter() 
		{
			super(LunchList.this, R.layout.restaurant_row, restaurants);
		}
		
		public View getView(int position, View convertView, ViewGroup parent)
		{
			View row = convertView;
			RestaurantHolder holder = null;
			
			if(row == null)
			{
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.restaurant_row, parent, false);
				
				holder = new RestaurantHolder(row);
				row.setTag(holder);
			}
			else
			{
				holder = (RestaurantHolder)row.getTag();
			}
			
			holder.populateFrom(restaurants.get(position));
			return(row);
		}
		
	}
	
	class RestaurantHolder
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
		
		void populateFrom(Restaurant r)
		{
			name.setText(r.getName());
			address.setText(r.getAddress());
			
			if(r.getType().equals("sit_down"))
			{
				icon.setImageResource(R.drawable.sit_down_restaurant_icon);
			}
			else if(r.getType().equals("take_out"))
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
