package cn.xmyoula.wifiscanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.widget.Toast;

public class Wifi_Result {
	public static final int WIFI_STATE_DISABING = 0;// 网卡关闭中
	public static final int WIFI_STATE_DISABLED = 1;// Wifi网卡不可用
	public static final int WIFI_STATE_ENABLING = 2;// 网卡正在打开
	public static final int WIFI_STATE_ENABLED = 3;// 网卡可用
	public static final int WIFI_STATE_UNKNOWN = 4;// 网卡未知状态
	public static final int MAX_FILE_LENGTH=1024*1024;//最大文件长度1M
	public String filename;
	
	public int WifiResult(WifiManager wm) {

		int i = 0;
		switch (wm.getWifiState()) {
		case WIFI_STATE_DISABING:
			return -1;
		case WIFI_STATE_DISABLED:
			return -5;
		case WIFI_STATE_ENABLING:
			return -2;
		case WIFI_STATE_ENABLED:
			// wifi网络已打开
			break;
		case WIFI_STATE_UNKNOWN:
			return -6;
		default:// 未知错误
			break;

		}
		if (wm.getWifiState() == 3) {
			WifiInfo info = wm.getConnectionInfo();
			int strength = info.getRssi();
			int speed = info.getLinkSpeed();
			String units = WifiInfo.LINK_SPEED_UNITS;
			String ssid = info.getSSID();
			List<ScanResult> results = wm.getScanResults(); // 加检测是否打开wifi
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
			String date = sdf.format(new java.util.Date());
			if(filename==null){
				filename=date+".txt";
				}
			String date1=date.replace('_', ':');//文件中的时间
			
			String otherwifi = "\n\nThe time is :" + date1 + "\n";
			for (ScanResult result : results) {
				otherwifi += result.SSID + ":" + result.BSSID + ":"
						+ result.level + "\n";
			}
			String status = Environment.getExternalStorageState();
			String sDir;
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				sDir = Environment.getExternalStorageDirectory().getPath()
						+ "/wifi_scanner";
			} else {
				sDir = Environment.getRootDirectory().getPath()
						+ "/data/wifi_scanner";
			}

			File dir = new File(sDir);
			if (!dir.exists()) {
				try {
					dir.mkdirs();// 按照指定的路径创建文件夹
				} catch (Exception e) {

				}
			}
			File file=new File(sDir,String.valueOf(filename));
			if (!file.exists()) {
				try {
					file.createNewFile();// 在指定的文件夹中创建文件

				} catch (Exception e) {
				}
			}
			else {
	        	if(file.length()>MAX_FILE_LENGTH) //获取文件大小,单位为bytes 
	        	filename=date+".txt";
	        	file=new File(sDir,String.valueOf(filename)); 
	        	 try { 
	                  file.createNewFile(); //在指定的文件夹中创建文件 
	            } catch (Exception e) { 
	            }
	        }
			try {
				FileOutputStream outputStream = new FileOutputStream(file,true);
				outputStream.write(otherwifi.getBytes());
				outputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return 1;
		} else {
			return 0;
		}

	}

}
