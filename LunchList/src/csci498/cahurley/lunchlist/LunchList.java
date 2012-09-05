package csci498.cahurley.lunchlist;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class LunchList extends Activity implements TextWatcher
{
	List<String> restaurantAddresses = new ArrayList<String>();
	List<Restaurant> restaurantList = new ArrayList<Restaurant>();
	RestaurantAdapter adapter = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(onSave);
        
        ListView list = (ListView)findViewById(R.id.restaurants);
        adapter = new RestaurantAdapter();
        list.setAdapter(adapter);
        
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
			address.addTextChangedListener((TextWatcher)LunchList.this);
			address.setAdapter(new ArrayAdapter<String>(LunchList.this, android.R.layout.simple_dropdown_item_1line, restaurantAddresses));
			
			restaurant.setName(name.getText().toString());
			restaurant.setAddress(address.getText().toString());
			restaurantAddresses.add(address.getText().toString());
			
			RadioGroup types = (RadioGroup)findViewById(R.id.types_of_restaurants_radio);
			switch(types.getCheckedRadioButtonId())
			{
				case R.id.sit_down:
					restaurant.setType("sit_down");
					break;
				case R.id.take_out:
					restaurant.setType("take_out");
					break;
				case R.id.delivery:
					restaurant.setType("delivery");
					break;
			}
			
			adapter.add(restaurant);
		}
	};

	public void afterTextChanged(Editable arg0) { }

	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

	public void onTextChanged(CharSequence s, int start, int before, int count) { }
	
	class RestaurantAdapter extends ArrayAdapter<Restaurant>
	{
		public RestaurantAdapter() 
		{
			super(LunchList.this, android.R.layout.simple_list_item_1, restaurantList);
		}
	}
	
}
