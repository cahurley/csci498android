package csci498.cahurley.lunchlist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

public class ListViewsFactory implements RemoteViewsFactory
{
	private final String SELECT_ID_NAME_FROM_RESTAURANTS = "SELECT _ID, name FROM restaurants";
	
	private Context context = null;
	private RestaurantHelper helper = null;
	private Cursor restaurantCursor = null;
	
	public ListViewsFactory(Context context, Intent intent)
	{
		this.context = context;
	}
	
	public void onCreate()
	{
		helper = new RestaurantHelper(context);
		restaurantCursor = helper.getReadableDatabase().rawQuery(SELECT_ID_NAME_FROM_RESTAURANTS, null);
	}
	
	public void onDestroy()
	{
		restaurantCursor.close();
		helper.close();
	}

	public int getCount() 
	{
		return restaurantCursor.getCount();
	}

	public long getItemId(int position) 
	{
		restaurantCursor.moveToPosition(position);
		return restaurantCursor.getInt(0);
	}

	public RemoteViews getLoadingView() 
	{
		return null;
	}

	public RemoteViews getViewAt(int position) 
	{
		RemoteViews row = new RemoteViews(context.getPackageName(), android.R.layout.simple_list_item_1);
		
		restaurantCursor.moveToPosition(position);
		row.setTextViewText(android.R.id.text1, restaurantCursor.getString(1));
		
		Bundle extras = new Bundle();
		extras.putString(LunchList.ID_EXTRA, String.valueOf(restaurantCursor.getInt(0)));
		
		Intent i = new Intent();
		i.putExtras(extras);
		
		row.setOnClickFillInIntent(android.R.id.text1, i);
		
		return row;
	}

	public int getViewTypeCount() 
	{
		return 1;
	}

	public boolean hasStableIds() 
	{
		return true;
	}

	public void onDataSetChanged() 
	{
		// no-op
	}
}
