package com.shx.weather;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shx.db.WeatherDB;
import com.shx.model.City;
import com.shx.model.County;
import com.shx.model.Province;
import com.shx.util.HttpCallbackListener;
import com.shx.util.HttpUtil;
import com.shx.util.Utility;
/**
 * ѡ�����
 * @author �ۺ���
 * @date 2015��12��7�� ����9:56:03
 * @version v1.0
 */
public class ChooseAreaActivity extends Activity {
	private static final int LEVEL_PROVINCE = 0;
	private static final int LEVEL_CITY = 1;
	private static final int LEVEL_COUNTY = 2;
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private WeatherDB db;
	private List<String> dataList = new ArrayList<String>();
	/**
	 * �Ƿ��WeatherActivity����ת����
	 */
	private boolean isFromWaatherActivity;
	/**
	 * ʡ�б�
	 */
	private List<Province> provinceList;
	/**
	 * ���б�
	 */
	private List<City> cityList;
	/**
	 *���б� 
	 */
	private List<County> countyList;
	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	/**
	 * ѡ�еĳ���
	 */
	private City selectedCity;
	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFromWaatherActivity=getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//�Ѿ�ѡ���˳����Ҳ��Ǵ�WeatherActivity��ת����,�Ż�ֱ����ת��WeatherActivity
		if (prefs.getBoolean("city_selected",false)&&!isFromWaatherActivity) {
			Intent intent = new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_area);
		initViews();
		initListView();
	}
	/**
	 *��ʼ��ListView 
	 * @author �ۺ���
	 * @date 2015��12��7�� ����9:49:32
	 * @version v1.0
	 */
	@SuppressWarnings("static-access")
	private void initListView() {
		adapter =new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		db = db.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel==LEVEL_PROVINCE) {
					selectedProvince =provinceList.get(position);
					queryCities();
				}else if(currentLevel==LEVEL_CITY){
					selectedCity=cityList.get(position);
					queryCounties();
				}else if (currentLevel==LEVEL_COUNTY) {
					String countyCode = countyList.get(position).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("county_code",countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		//����ʡ������
		queryProvinces();
	}
	/**
	 *��ѯȫ�����е�ʡ,���ȴ����ݿ��ѯ,���û�в�ѯ����ȥ�������ϲ�ѯ 
	 * @author �ۺ���
	 * @date 2015��12��7�� ����9:21:08
	 * @version v1.0
	 */
	private void queryProvinces() {
		provinceList =  db.loadProvinces();
		if (provinceList.size()>0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel=LEVEL_PROVINCE;
		}else {
			queryFromServer(null,"province");
		}
	}
	/**
	 *���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ���ص����� 
	 * @author �ۺ���
	 * @date 2015��12��7�� ����9:28:34
	 * @version v1.0
	 */
	private void queryFromServer(String	 code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else {
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		Log.d("ChooseAreaActivity",address);
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(db, response);
				}else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(db, response, selectedProvince.getId());
				}else if ("county".equals(type)){
					result = Utility.handleCountiesResponse(db, response, selectedCity.getId());
				}
				if (result) {
					//ͨ��runOnUiThread()�����ص����̴߳�����
					runOnUiThread(new  Runnable() {
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				//ͨ��runOnUiThread�����ص����̴߳����߼�
				runOnUiThread(new  Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,"����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	/**
	 *��ʾ���ȶԻ��� 
	 * @author �ۺ���
	 * @date 2015��12��7�� ����9:44:14
	 * @version v1.0
	 */
	private void showProgressDialog() {
		if (progressDialog==null) {
			progressDialog= new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/**
	 * �رս��ȶԻ���
	 * @author �ۺ���
	 * @date 2015��12��7�� ����9:45:42
	 * @version v1.0
	 */
	private void closeProgressDialog() {
		if (progressDialog!=null) {
			progressDialog.dismiss();
		}
	}
	/**
	 * ��ѯѡ���������е���,���ȴ����ݿ��ѯ,���û�в�ѯ����ȥ�������ϲ�ѯ
	 * @author �ۺ���
	 * @date 2015��12��7�� ����9:27:49
	 * @version v1.0
	 */
	protected void queryCounties() {
		countyList =db.loadCounties(selectedCity.getId());
		if (countyList.size()>0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}
	}
	/**
	 *��ѯѡ��ʡ�����е���,���ȴ����ݿ��ѯ,���û�в�ѯ����ȥ�������ϲ�ѯ  
	 * @author �ۺ���
	 * @date 2015��12��7�� ����9:24:28
	 * @version v1.0
	 */
	private void queryCities() {
		cityList = db.loadCities(selectedProvince.getId());
		if (cityList.size()>0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProinceName());
			currentLevel=LEVEL_CITY;
		}else {
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	/**
	 *��ʼ��������� 
	 * @author �ۺ���
	 * @date 2015��12��7�� ����9:24:57
	 * @version v1.0
	 */
	private void initViews() {
		listView=(ListView) findViewById(R.id.list_view);
		titleText =(TextView) findViewById(R.id.title_text);
	}
	/**
	 * ����Back����,���ݵ�ǰ�ļ������ж�,��ʱӦ�÷������б�,ʡ�б�,����ֱ���˳�
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel==LEVEL_COUNTY) {
			queryCities();
		}else if(currentLevel==LEVEL_CITY) {
			queryProvinces();
		}else {
			if (isFromWaatherActivity) {
				Intent intent = new Intent(this,WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}
