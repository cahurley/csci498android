package csci498.helloandroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import java.util.Date;

public class HelloAndroid extends Activity implements View.OnClickListener
{
	Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        button = new Button(this);
        button.setOnClickListener(this);
        updateTime();
        
        setContentView(button);
    }

    public void onClick(View view)
    {
    	updateTime();
    }
    
    private void updateTime()
    {
    	button.setText(new Date().toString());
    }
}
