package com.shx.service;

import com.shx.receiver.AutoUpdateReceiver;
import com.shx.util.HttpCallbackListener;
import com.shx.util.HttpUtil;
import com.shx.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * ��̨�Զ���������
 * 
 * @author �ۺ���
 * @date 2015��12��8�� ����7:27:58
 * @version v1.0
 */
public class AutoUpdateService extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				updateWeather();
			}
		}).start();
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		//anHour������ٸ�Сʱ����һ������
		int anHour = 3*60*60*1000;//����3Сʱ�ĺ�����
		long triggerAtTime=SystemClock.elapsedRealtime()+anHour;
		Intent i = new Intent(this,AutoUpdateReceiver.class);
		PendingIntent pi=PendingIntent.getBroadcast(this,0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}
	/**
	 *����������Ϣ 
	 * @author �ۺ���
	 * @date 2015��12��8�� ����7:36:33
	 * @version v1.0
	 */
	protected void updateWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weather_code", "");
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		Log.d("AutoUpdateService",address);
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				Utility.handleWeatherResponse(AutoUpdateService.this, response);
			}
			
			@Override
			public void onError(Exception e) {
				e.printStackTrace();
			}
		});
	}
}
