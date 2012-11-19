package csci498.cahurley.lunchlist;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmActivity extends Activity implements OnPreparedListener
{
	MediaPlayer player = new MediaPlayer();
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String sound = preferences.getString("alarm_ringtone", null);
		if (sound != null)
		{
			player.setAudioStreamType(AudioManager.STREAM_ALARM);
			
			try
			{
				player.setDataSource(sound);
				player.setOnPreparedListener(this);
				player.prepareAsync();
			}
			catch (Exception ex)
			{
				Log.e("LunchList", "Exception in playing ringtone", ex);
			}
		}
	}
	
	@Override
	public void onPause()
	{
		if (player.isPlaying())
		{
			player.stop();
		}
		
		super.onPause();
	}
	
	public void onPrepared(MediaPlayer player)
	{
		player.start();
	}
	
}
