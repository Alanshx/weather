package com.shx.receiver;

import com.shx.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *ʵ�ֺ�̨��ʱ�������� 
 * @author �ۺ���
 * @date 2015��12��8�� ����7:39:44
 * @version v1.0
 */
public class AutoUpdateReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context,AutoUpdateService.class);
		context.startService(i);
	}
}
