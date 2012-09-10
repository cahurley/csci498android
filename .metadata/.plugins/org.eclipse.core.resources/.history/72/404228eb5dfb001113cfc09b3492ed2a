package csci498.cahurley.lunchlist;

import java.util.Calendar;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DatePickerFragment extends DialogFragment 
{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			// use current date as default date in picker
			final Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DATE);
			
			// create new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day)
		{
			
		}
}
