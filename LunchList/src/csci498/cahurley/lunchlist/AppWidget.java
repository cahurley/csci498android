package csci498.cahurley.lunchlist;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

public class AppWidget extends AppWidgetProvider 
{
	@Override
	public void onUpdate(Context context, AppWidgetManager manager, int[] appWidgetIds)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			onHCUpdate(context, manager, appWidgetIds);
		}
		else
		{
			context.startService(new Intent(context, WidgetService.class));
		}
	}
	
	public void onHCUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		for (int i = 0; i < appWidgetIds.length; i++)
		{
			Intent svcIntent = new Intent(context, ListWidgetService.class);
			svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			svcIntent.setData(Uri.parse(svcIntent.toURI()));
			
			RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget);
			widget.setRemoteAdapter(appWidgetIds[i], R.id.restaurants, svcIntent);
			
			Intent clickIntent = new Intent(context, DetailForm.class);
			PendingIntent clickPendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			widget.setPendingIntentTemplate(R.id.restaurants, clickPendingIntent);
			
			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
		}
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
}
