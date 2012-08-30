package csci498.cahurley.lunchlist;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity 
{
	Restaurant restaurant = new Restaurant();
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button saveButton = (Button)findViewById(R.id.save_button);
        saveButton.setOnClickListener(onSave);
        
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
			EditText name = (EditText)findViewById(R.id.name_edit_text);
			EditText address = (EditText)findViewById(R.id.address_edit_text);
			
			restaurant.setName(name.getText().toString());
			restaurant.setAddress(address.getText().toString());
			
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
		}
	};
	
}
