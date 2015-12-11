package com.shx.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.shx.db.WeatherDB;
import com.shx.model.City;
import com.shx.model.County;
import com.shx.model.Province;

public class Utility {
	/**
	 *解析和处理服务器返回的省级数据  
	 * @author 邵海雄
	 * @date 2015年12月7日 下午8:33:57
	 * @version v1.0
	 */
	public synchronized static boolean handleProvincesResponse(WeatherDB db,String response){
		if (!TextUtils.isEmpty(response)) {
			String [] allProvince = response.split(",");
			if (allProvince!=null&&allProvince.length>0) {
				for (String p : allProvince) {
					String [] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProinceName(array[1]);
					//将解析出来的数据存储到Province表
					db.saveProvince(province);
				}
			}
			return true;
		}
		return false;
	}
	/**
	 *解析和处理服务器返回的市级数据  
	 * @author 邵海雄
	 * @date 2015年12月7日 下午8:33:57
	 * @version v1.0
	 */
	public synchronized static boolean handleCitiesResponse(WeatherDB db,String response,int provinceId){
		if (!TextUtils.isEmpty(response)) {
			String [] allCities = response.split(",");
			if (allCities!=null&&allCities.length>0) {
				for (String c : allCities) {
					String [] array = c.split("\\|");
					City city = new  City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//将解析出来的数据存储到City表
					db.saveCity(city);
				}
			}
			return true;
		}
		return false;
	}
	/**
	 *解析和处理服务器返回的县级数据  
	 * @author 邵海雄
	 * @date 2015年12月7日 下午8:33:57
	 * @version v1.0
	 */
	public synchronized static boolean handleCountiesResponse(WeatherDB db,String response,int cityId){
		if (!TextUtils.isEmpty(response)) {
			String [] allCounties = response.split(",");
			if (allCounties!=null&&allCounties.length>0) {
				for (String c : allCounties) {
					String [] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					//将解析出来的数据存储到City表
					db.saveCounty(county);
				}
			}
			return true;
		}
		return false;
	}
	/**
	 *解析服务器返回的JSON数据,并将解析出的数据存储到本地 
	 * @author 邵海雄
	 * @date 2015年12月7日 下午10:18:36
	 * @version v1.0
	 * @param context  上下文
	 * @param response
	 */
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 将服务器返回的所有天气信息存储到SharePreferences文件中
	 * @author 邵海雄
	 * @date 2015年12月7日 下午10:24:16
	 * @version v1.0
	 * @param context  上下文
	 * @param cityName  城市名称
	 * @param weatherCode  天气代号
	 * @param temp1  摄氏度1
	 * @param temp2  摄氏度2
	 * @param weatherDesp   天气描述信息
	 * @param publishTime   发布时间
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code",weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
