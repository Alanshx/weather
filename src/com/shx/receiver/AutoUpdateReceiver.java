package com.shx.receiver;

import com.shx.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *实现后台定时更新天气 
 * @author 邵海雄
 * @date 2015年12月8日 下午7:39:44
 * @version v1.0
 */
public class AutoUpdateReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context,AutoUpdateService.class);
		context.startService(i);
	}
}
