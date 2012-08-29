package csci498.cahurley.lunchlist;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    }
    
    private View.OnClickListener onSave = new View.OnClickListener()
    {	
		public void onClick(View v) 
		{
			EditText name = (EditText)findViewById(R.id.name_edit_text);
			EditText address = (EditText)findViewById(R.id.address_edit_text);
			
			restaurant.setName(name.getText().toString());
			restaurant.setAddress(address.getText().toString());
		}
	};
	
}
