package csci498.cahurley.lunchlist;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

public class LunchList extends TabActivity
{
	Cursor restaurants = null;
	RestaurantAdapter adapter = null;
	EditText name = null;
	EditText address = null;
	RadioGroup types = null;
	EditText notes = null;
	RestaurantHelper helper = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        helper = new RestaurantHelper(this);
        
        setMemberVariables();
        
        configureSaveButton();
        configureList();
        configureTabs();
    }
    
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    	
    	helper.close();
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
        

        restaurants = helper.getAll();
        startManagingCursor(restaurants);
        adapter = new RestaurantAdapter(restaurants);
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
			String type = getRestaurantType();
			
			helper.insert(name.getText().toString(), address.getText().toString(),
					type, notes.getText().toString());
			restaurants.requery();
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
			Intent i = new Intent(LunchList.this, DetailForm.class);
			startActivity(i);
		}
	};
	
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
		
		void populateFrom(Cursor cursor, RestaurantHelper helper)
		{
			name.setText(helper.getName(cursor));
			address.setText(helper.getAddress(cursor));
			
			if(helper.getType(cursor).equals("sit_down"))
			{
				icon.setImageResource(R.drawable.sit_down_restaurant_icon);
			}
			else if(helper.getType(cursor).equals("take_out"))
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
