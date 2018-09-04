package com.adflash.uploadfiles;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UploadService extends Service
{
	private final static String TAG = "upload_test";

	private static TimerTask timerTask;

	private static Timer timer;
	private Context mContext;

	private static ExecutorService sExecutor;

	static
	{
		sExecutor = Executors.newCachedThreadPool();
	}
	
	public static ExecutorService getExecutor()
    {
        return UploadService.sExecutor;
    }

	@Override
	public void onCreate()
	{
		super.onCreate();
		Log.i(TAG, "onCreate");
		mContext = getApplicationContext();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.i(TAG, "onStartCommand");
		mContext = getApplicationContext();
		try
		{
			if (timerTask == null)
			{
				timerTask = new TimerTask()
				{
					@Override
					public void run()
					{
						try
						{
							UploadFile uploadFile = new UploadFile(mContext);

							UploadService.getExecutor().submit(uploadFile);
							
						} catch (Exception e)
						{
							Log.i(TAG, "timerTask e:" + e.getMessage());
						}
					}
				};
			}

			if (timer == null)
			{
				timer = new Timer(true);
				
				//30*60*1000L
				timer.schedule(timerTask, 0L,  30*60*1000L);
			}
		} catch (Exception e)
		{
			Log.i(TAG, "e:" + e.getMessage());
			try
			{
				if (timer != null)
				{
					timer.cancel();
					timer = null;
				}
				if (timerTask != null)
				{
					timerTask.cancel();
					timerTask = null;
				}
			} catch (Exception e2)
			{
				Log.i(TAG, "e2:" + e2.getMessage());
			}
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		try
		{
			if (timer != null)
			{
				timer.cancel();
				timer = null;
			}
			if (timerTask != null)
			{
				timerTask.cancel();
				timerTask = null;
			}
		} catch (Exception e3)
		{
			Log.i(TAG, "e3:" + e3.getMessage());
		}
	}

}
