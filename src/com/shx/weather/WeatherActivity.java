package com.shx.weather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shx.service.AutoUpdateService;
import com.shx.util.HttpCallbackListener;
import com.shx.util.HttpUtil;
import com.shx.util.Utility;
/**
 * 显示天气信息
 * @author 邵海雄
 * @date 2015年12月7日 下午10:33:39
 * @version v1.0
 */
public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;
	/**
	 *用于显示城市名 
	 */
	private TextView cityNameText;
	/**
	 * 用于显示发布时间
	 */
	private TextView publishText;
	/**
	 * 用于显示天气描述信息
	 */
	private TextView weatherDespText;
	/**
	 * 用于显示气温1
	 */
	private TextView temp1Text;
	/**
	 * 用于显示气温2
	 */
	private TextView temp2Text;
	/**
	 * 用于显示当前日期
	 */
	private TextView currentDateText;
	/**
	 * 切换城市按钮
	 */
	private Button switchCity;
	/**
	 * 更新天气按钮
	 */
	private Button refreshWeather;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_weather);
		initViews();
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			//有县级代号时就去查询天气
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else {
			//没有县级代号时就直接显示本地天气
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}
	/**
	 *从SharedPreferences文件中读取存储的天气信息,并显示到界面上. 
	 * @author 邵海雄
	 * @date 2015年12月8日 下午5:58:20
	 * @version v1.0
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1",""));
		temp2Text.setText(prefs.getString("temp2",""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("今天"+prefs.getString("publish_time", "")+"发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		//启动自动更新天气服务
		Intent intent = new Intent(this,AutoUpdateService.class);
		startService(intent);
	}
	/**
	 *查询县级代号所对应的天气代号 
	 * @author 邵海雄
	 * @date 2015年12月8日 下午5:43:48
	 * @version v1.0
	 * @param countyCode  县级代号
	 */
	private void queryWeatherCode(String countyCode) {
		String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		Log.d("WeatherActivity",address);
		queryFromServer(address,"countyCode");
	}
	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 * @author 邵海雄
	 * @date 2015年12月8日 下午5:47:19
	 * @version v1.0
	 * @param address  地址
	 * @param type  类型
	 */
	private void queryFromServer(String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						//从服务器返回的数据中解析出天气代号
						String[] array =response.split("\\|");
						if (array!=null&&array.length==2) {
							String weatherCode =array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if ("weatherCode".equals(type)) {
					//处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new  Runnable() {
					public void run() {
						publishText.setText("同步失败");
					}
				});
			}
		});
	}
	/**
	 *初始化各控件 
	 * @author 邵海雄
	 * @date 2015年12月7日 下午10:39:12
	 * @version v1.0
	 */
	private void initViews() {
		weatherInfoLayout =(LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText =(TextView) findViewById(R.id.city_name);
		publishText=(TextView) findViewById(R.id.publish_text);
		weatherDespText =(TextView) findViewById(R.id.weather_desp);
		temp1Text =(TextView) findViewById(R.id.temp1);
		temp2Text=(TextView) findViewById(R.id.temp2);
		currentDateText=(TextView) findViewById(R.id.current_date);
		switchCity =(Button) findViewById(R.id.switch_city);
		refreshWeather=(Button) findViewById(R.id.refresh_weather);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city://切换城市
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather://刷新天气
			publishText.setText("同步中...");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode =prefs.getString("weather_code","");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		}
	}
	/**
	 *查询天气代号所对应的天气 
	 * @author 邵海雄
	 * @date 2015年12月8日 下午5:45:42
	 * @version v1.0
	 * @param weatherCode  天气代号
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		Log.d("WeatherActivity",address);
		queryFromServer(address, "weatherCode");
	}
}
