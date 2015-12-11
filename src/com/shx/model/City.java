package com.shx.model;

/**
 * 城市
 * 
 * @author 邵海雄
 * @date 2015年12月7日 下午7:44:24
 * @version v1.0
 */
public class City {
	private int id;
	private String cityName;
	private String cityCode;
	private int provinceId;

	public int getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(int provinceId) {
		this.provinceId = provinceId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

}
