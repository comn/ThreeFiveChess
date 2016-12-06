package cq.game.fivechess.net;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;


public class NetWorkUtil {
	private static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");// 4.0模拟器屏蔽掉该权限
	private static boolean isWIFI;
	private static boolean isMOBILE;

	/**
	 * 检查网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetWork(Context context) {
		// ConnectivityManager--系统服务
		// ①判断WIFI链接吗
		isWIFI = isWIFIConnectivity(context);
		// ②判断MOBILE链接吗
		isMOBILE = isMOBILEConnectivity(context);

		// 如果没有网络
		if (!isMOBILE && !isWIFI) {
			return false;
		}

		// ③如果MOBILE处于链接状态：WAP NET
		// 如果WAP——代理信息
		// 获取到当前正在处理链接的方式——读取链接的配置参数
		// 如果读取到proxy+port---非空wap
		if (isMOBILE) {
			readAPN(context);// 操作联系人
		}

		return true;
	}
	
	public static String getIP(){
		if (isWIFI) {
			
		}else if (isMOBILE) {
			
		}
		
		return null;
	}

	/**
	 * 读取到proxy+port
	 * 
	 * @param context
	 */
	private static void readAPN(Context context) {
		ContentResolver resolver = context.getContentResolver();

		Cursor cursor = resolver.query(PREFERRED_APN_URI, null, null, null, null);// 获取到当前正在处于链接（单选）

		if (cursor != null && cursor.moveToFirst()) {
			// proxy+port

			ConnectConstants.PROXY_IP = cursor.getString(cursor.getColumnIndex("proxy"));
			ConnectConstants.PROXY_PORT = cursor.getInt(cursor.getColumnIndex("port"));

		}

	}

	/**
	 * 判断MOBILE链接
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isMOBILEConnectivity(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// MOBILE链接的描述信息
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}

	/**
	 * 判断WIFI链接状态
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isWIFIConnectivity(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// WIFI链接的描述信息
		NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null) {
			return networkInfo.isConnected();
		}
		return false;
	}
	
	
}
