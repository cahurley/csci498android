package csci498.cahurley.lunchlist;

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * ViewFlipper code from http://www.techrepublic.com/blog/app-builder/a-dog-limps-into-a-saloon-a-tutorial-on-androids-viewflipper-widget/634
 */
public class LunchList extends Activity
{
	List<Restaurant> restaurants = new ArrayList<Restaurant>();
	RestaurantAdapter adapter = null;
	EditText name = null;
	EditText address = null;
	RadioGroup types = null;
	EditText notes = null;
	Restaurant currentRestaurant = null;
	
	private ViewFlipper vf;
	private float lastX;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setMemberVariables();
        
        configureSaveButton();
        configureList();
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
    	
    	return(super.onOptionsItemSelected(item));
    }
    
    private void setMemberVariables()
    {
    	name = (EditText)findViewById(R.id.name_edit_text);
    	address = (EditText)findViewById(R.id.address_edit_text);
    	types = (RadioGroup)findViewById(R.id.types_of_restaurants_radio);
    	notes = (EditText)findViewById(R.id.notes_edit_text);
    	
    	vf = (ViewFlipper)findViewById(R.id.view_flipper);
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
		}
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent touchevent)
	{
		switch(touchevent.getAction())
		{
			case MotionEvent.ACTION_DOWN:
			{
				lastX = touchevent.getX();
				break;
			}
			case MotionEvent.ACTION_UP:
			{
				float currentX = touchevent.getX();
				if(lastX < currentX)
				{
					if(vf.getDisplayedChild() == 0)
					{
						break;
					}
					vf.setInAnimation(this, R.anim.in_from_left);
					vf.setOutAnimation(this, R.anim.out_to_right);
					vf.showNext();
				}
				if(lastX > currentX)
				{
					if(vf.getDisplayedChild() == 1)
					{
						break;
					}
					vf.setInAnimation(this, R.anim.in_from_right);
					vf.setInAnimation(this, R.anim.out_to_left);
					vf.showNext();
				}
				break;
			}
		}
		return false;
	}
	
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
