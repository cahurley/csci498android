package csci498.cahurley.lunchlist;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * DatePicker code from http://stackoverflow.com/questions/9861483/datepickerdialog
 */
public class LunchList extends TabActivity
{
	List<Restaurant> restaurants = new ArrayList<Restaurant>();
	RestaurantAdapter adapter = null;
	EditText name = null;
	EditText address = null;
	RadioGroup types = null;
	Restaurant currentRestaurant = null;
	
	private TextView dateDisplay;
	private Button pickDate;
	private int year, month, day;
	
	static final int DATE_DIALOG_ID = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setMemberVariables();
        
        configureSaveButton();
        configureList();
        configureTabs();
        configureDatePicker();
    }
    
    private void setMemberVariables()
    {
    	name = (EditText)findViewById(R.id.name_edit_text);
    	address = (EditText)findViewById(R.id.address_edit_text);
    	types = (RadioGroup)findViewById(R.id.types_of_restaurants_radio);
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
    
    private void configureDatePicker()
    {
    	dateDisplay = (TextView)findViewById(R.id.date_text);
    	pickDate = (Button)findViewById(R.id.date_picker_button);
    	
    	pickDate.setOnClickListener(new View.OnClickListener() 
    	{
			public void onClick(View v) 
			{
				showDialog(DATE_DIALOG_ID);
			}
		});
    	
    	Calendar c;
    	if(currentRestaurant == null)
    	{
    		c = Calendar.getInstance();
    	}
    	else
    	{
    		c = currentRestaurant.getDate();
    	}
    	
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		updateDisplay(dateDisplay);
    }
    
    private void updateDisplay(TextView dateDisplay)
    {
    	dateDisplay.setText(new StringBuilder().append(month + 1).append("-")
    							.append(day).append("-")
    							.append(year));
    }
    
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
    {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) 
		{
			year = year;
			month = monthOfYear;
			day = dayOfMonth;
			updateDisplay(dateDisplay);
		}
	};
	
	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch(id)
		{
			case DATE_DIALOG_ID:
				return new DatePickerDialog(this, dateSetListener, year, month, day);
		}
		return null;
	}
    
    private View.OnClickListener onSave = new View.OnClickListener()
    {	
		public void onClick(View v) 
		{
			currentRestaurant = new Restaurant();
			setRestaurantValues(currentRestaurant, name, address);
			
			adapter.add(currentRestaurant);
		}
		
		void setRestaurantValues(Restaurant restaurant, EditText name, EditText address)
		{
			restaurant.setName(name.getText().toString());
			restaurant.setAddress(address.getText().toString());			
			restaurant.setType(getRestaurantType());
			restaurant.setDate(getLastVisitedDate());
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
		
		Calendar getLastVisitedDate()
		{
			Calendar lastVisited = Calendar.getInstance();
			lastVisited.set(Calendar.YEAR, year);
			lastVisited.set(Calendar.MONTH, month);
			lastVisited.set(Calendar.DAY_OF_MONTH, day);
			
			return lastVisited;
		}
		
	};
	
	private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			currentRestaurant = restaurants.get(position);
			
			name.setText(currentRestaurant.getName());
			address.setText(currentRestaurant.getAddress());
			updateDisplay(dateDisplay);
			
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
		private TextView date = null;
		private ImageView icon = null;
		
		public RestaurantHolder(View row) 
		{
			name = (TextView)row.findViewById(R.id.title);
			address = (TextView)row.findViewById(R.id.address);
			date = (TextView)row.findViewById(R.id.date_last_visited);
			icon = (ImageView)row.findViewById(R.id.icon);
		}
		
		void populateFrom(Restaurant r)
		{
			name.setText(r.getName());
			address.setText(r.getAddress());

			updateDisplay(date);
			
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
