package csci498.cahurley.lunchlist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class LunchList extends Activity implements TextWatcher
{
	List<String> restaurantAddresses = new ArrayList<String>();
	List<Restaurant> model = new ArrayList<Restaurant>();
	RestaurantAdapter adapter = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        configureButton();
        configureListAdapter();
        setTextViewTypeFaces();
    }
    
    private void configureButton()
    {
        Button saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(onSave);
    }
    
    private void configureListAdapter()
    {
        ListView list = (ListView)findViewById(R.id.restaurants);
        adapter = new RestaurantAdapter();
        list.setAdapter(adapter);
    }
    
    private void setTextViewTypeFaces()
    {
        TextView nameText = (TextView)findViewById(R.id.name_text);
        TextView addressText = (TextView)findViewById(R.id.address_text);
        TextView typeOfRestaurantText = (TextView)findViewById(R.id.type_of_restaurant_text);
        
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/molten.ttf");
        nameText.setTypeface(font);
        addressText.setTypeface(font);
        typeOfRestaurantText.setTypeface(font);
    }
    
    private View.OnClickListener onSave = new View.OnClickListener()
    {	
		public void onClick(View v) 
		{
			Restaurant restaurant = new Restaurant();
			EditText name = (EditText)findViewById(R.id.name_edit_text);
			AutoCompleteTextView address = (AutoCompleteTextView)findViewById(R.id.address_auto_complete_text);
			
			configureAutoCompleteTextView(address);
			
			restaurant.setName(name.getText().toString());
			restaurant.setAddress(address.getText().toString());			
			restaurant.setType(getRestaurantType());
			
			restaurantAddresses.add(address.getText().toString());
			
			adapter.add(restaurant);
		}
	};
	
	private void configureAutoCompleteTextView(AutoCompleteTextView address)
	{
		address.addTextChangedListener((TextWatcher)LunchList.this);
		address.setAdapter(new ArrayAdapter<String>(LunchList.this, android.R.layout.simple_dropdown_item_1line, restaurantAddresses));
	}
	
	private String getRestaurantType()
	{
		RadioGroup types = (RadioGroup)findViewById(R.id.types_of_restaurants_radio);
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

	public void afterTextChanged(Editable arg0) { }

	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

	public void onTextChanged(CharSequence s, int start, int before, int count) { }
	
	class RestaurantAdapter extends ArrayAdapter<Restaurant>
	{
		public RestaurantAdapter() 
		{
			super(LunchList.this, R.layout.restaurant_row, model);
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
			
			holder.populateFrom(model.get(position));
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
			
			customizeRestaurantAddress(name);
		}
		
		void customizeRestaurantAddress(TextView name)
		{
			if(name.getText().length() > 10)
			{
				name.setTextAppearance(null, Typeface.ITALIC);
			}
			else
			{
				name.setTextAppearance(null, Typeface.BOLD);
			}
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