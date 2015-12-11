package com.shx.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.shx.model.City;
import com.shx.model.County;
import com.shx.model.Province;

public class WeatherDB {
	/**
	 * ���ݿ���
	 */
	public static final String DB_NAME = "weather";
	/**
	 * ���ݿ�汾
	 */
	public static final int VERSION = 1;
	private static WeatherDB weatherDB;
	private SQLiteDatabase db;
	/**
	 * �����췽��˽�л�
	 * @param context  ������
	 */
	private WeatherDB(Context context){
		WeatherOpenHelper dbHelper = new WeatherOpenHelper(context, DB_NAME, null, VERSION);
		db=dbHelper.getWritableDatabase();
	}
	/**
	 * ��ȡWeatherDB��ʵ��
	 * @author �ۺ���
	 * @date 2015��12��7�� ����7:52:09
	 * @version v1.0
	 * @param context  ������
	 * @return WeatherDB��ʵ��
	 */
	public synchronized static WeatherDB getInstance(Context context){
		if (weatherDB==null) {
			weatherDB=new WeatherDB(context);
		}
		return weatherDB;
	}
	/**
	 * ��Provinceʵ���洢�����ݿ�
	 * @author �ۺ���
	 * @date 2015��12��7�� ����7:53:01
	 * @version v1.0
	 * @param province  ʡ��
	 */
	public void saveProvince(Province province){
		if (province!=null) {
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	/**
	 *�����ݿ��ȡȫ�����е�ʡ����Ϣ 
	 * @author �ۺ���
	 * @date 2015��12��7�� ����7:57:17
	 * @version v1.0
	 * @return List<Province>  ����ʡ����Ϣ����
	 */
	public List<Province> loadProvinces(){
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("_ID")));
				province.setProinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			} while (cursor.moveToNext());
		}
		return list;
	}
	/**
	 *��Cityʵ���洢�����ݿ� 
	 * @author �ۺ���
	 * @date 2015��12��7�� ����8:01:51
	 * @version v1.0
	 * @param city ����
	 */
	public void saveCity(City city){
		if (city!=null) {
			ContentValues values=new ContentValues();
			values.put("city_name",city.getCityName());
			values.put("city_code",city.getCityCode());
			values.put("province_id",city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	/**
	 *�����ݿ��ȡĳʡ�����еĳ�����Ϣ 
	 * @author �ۺ���
	 * @date 2015��12��7�� ����8:03:54
	 * @version v1.0
	 * @param provinceId  ʡ�ݱ��
	 * @return   List<City>  ���س�����Ϣ����
	 */
	public List<City> loadCities(int provinceId){
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("_ID")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;
	}
	/**
	 * ��Countyʵ���洢�����ݿ� 
	 * @author �ۺ���
	 * @date 2015��12��7�� ����8:12:22
	 * @version v1.0
	 * @param county  ��,��
	 */
	public void saveCounty(County county){
		if (county!=null) {
			ContentValues values=new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}
	/**
	 * �����ݿ��ȡĳ�����µ���������Ϣ
	 * @author �ۺ���
	 * @date 2015��12��7�� ����8:14:20
	 * @version v1.0
	 * @param cityId
	 * @return
	 */
	public List<County> loadCounties(int cityId){
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("_ID")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());
		}
		return list;
	}
}
