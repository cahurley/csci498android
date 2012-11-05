package csci498.cahurley.lunchlist;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;

public class WidgetService extends IntentService 
{
	private final String QUERY_FOR_NUMBER_OF_RESTAURANTS = "SELECT COUNT(*) FROM restaurants";
	private final String QUERY_FOR_RANDOM_RESTAURANT = "SELECT _ID, name FROM restaurants LIMIT 1 OFFSET ?";
	
	public WidgetService()
	{
		super("WidgetService");
	}
	
	@Override
	public void onHandleIntent(Intent intent)
	{
		ComponentName componentName = new ComponentName(this, AppWidget.class);
		RemoteViews updateViews = new RemoteViews("csci498.cahurley.lunchlist", R.layout.widget);
		RestaurantHelper helper = new RestaurantHelper(this);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		
		try
		{
			Cursor cursor = helper.getReadableDatabase().rawQuery(QUERY_FOR_NUMBER_OF_RESTAURANTS, null);
			cursor.moveToFirst();
			
			int count = cursor.getInt(0);
			
			cursor.close();
			
			if (count > 0)
			{
				int offset = (int)(count * Math.random());
				String args[] = {String.valueOf(offset)};
				
				cursor = helper.getReadableDatabase().rawQuery(QUERY_FOR_RANDOM_RESTAURANT, args);
				cursor.moveToFirst();
				updateViews.setTextViewText(R.id.name, cursor.getString(1));
				
				Intent i = new Intent(this, DetailForm.class);
				i.putExtra(LunchList.ID_EXTRA, cursor.getString(0));
				
				PendingIntent pendingI = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
				
				updateViews.setOnClickPendingIntent(R.id.name, pendingI);
				
				cursor.close();
			}
			else
			{
				updateViews.setTextViewText(R.id.title, this.getString(R.string.empty));
			}
		}
		finally
		{
			helper.close();
		}
		
		Intent i = new Intent(this, WidgetService.class);
		PendingIntent pendingI = PendingIntent.getService(this, 0, i, 0);
		
		updateViews.setOnClickPendingIntent(R.id.next, pendingI);
		manager.updateAppWidget(componentName, updateViews);
	}
}
