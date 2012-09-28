package csci498.cahurley.lunchlist;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class DetailForm extends Activity 
{
	EditText name = null;
	EditText address = null;
	EditText notes = null;
	RadioGroup types = null;
	RestaurantHelper helper = null;
	String restaurantId = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_form);
		
		setMemberVariables();
		
		configureSaveButton();
		
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
	
	@Override
	public void onRestoreInstanceState(Bundle state)
	{
		super.onRestoreInstanceState(state);
		
		name.setText(state.getString("name"));
		address.setText(state.getString("address"));
		notes.setText(state.getString("notes"));
		types.check(state.getInt("type"));
	}
	
    private void setMemberVariables()
    {
    	helper = new RestaurantHelper(this);
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
    
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    	
    	helper.close();
    }
    
    private void load()
    {
    	Cursor cursor = helper.getById(restaurantId);
    	
    	cursor.moveToFirst();
    	name.setText(helper.getName(cursor));
    	address.setText(helper.getAddress(cursor));
    	notes.setText(helper.getNotes(cursor));
    	
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
    
    private View.OnClickListener onSave = new View.OnClickListener()
    {	
		public void onClick(View v) 
		{
			String type = getRestaurantType();
			
			if(restaurantId == null)
			{
				helper.insert(name.getText().toString(), address.getText().toString(), type, notes.getText().toString());
			}
			else
			{
				helper.update(restaurantId, name.getText().toString(), address.getText().toString(), type, notes.getText().toString());
			}
			
			finish();
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
}
