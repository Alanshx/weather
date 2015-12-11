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
	 *�����ʹ�����������ص�ʡ������  
	 * @author �ۺ���
	 * @date 2015��12��7�� ����8:33:57
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
					//���������������ݴ洢��Province��
					db.saveProvince(province);
				}
			}
			return true;
		}
		return false;
	}
	/**
	 *�����ʹ�����������ص��м�����  
	 * @author �ۺ���
	 * @date 2015��12��7�� ����8:33:57
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
					//���������������ݴ洢��City��
					db.saveCity(city);
				}
			}
			return true;
		}
		return false;
	}
	/**
	 *�����ʹ�����������ص��ؼ�����  
	 * @author �ۺ���
	 * @date 2015��12��7�� ����8:33:57
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
					//���������������ݴ洢��City��
					db.saveCounty(county);
				}
			}
			return true;
		}
		return false;
	}
	/**
	 *�������������ص�JSON����,���������������ݴ洢������ 
	 * @author �ۺ���
	 * @date 2015��12��7�� ����10:18:36
	 * @version v1.0
	 * @param context  ������
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
	 * �����������ص�����������Ϣ�洢��SharePreferences�ļ���
	 * @author �ۺ���
	 * @date 2015��12��7�� ����10:24:16
	 * @version v1.0
	 * @param context  ������
	 * @param cityName  ��������
	 * @param weatherCode  ��������
	 * @param temp1  ���϶�1
	 * @param temp2  ���϶�2
	 * @param weatherDesp   ����������Ϣ
	 * @param publishTime   ����ʱ��
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
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
