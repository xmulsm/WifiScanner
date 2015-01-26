package cn.xmyoula.wifiscanner;

import java.util.ArrayList;
import cn.xmyoula.library.RippleBackground;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ImageView foundDevice;
	private boolean isStart = false;
	private MyThread mythread;
	private MyHandler myhander;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final RippleBackground rippleBackground = (RippleBackground) findViewById(R.id.content);
		myhander = new MyHandler();
		mythread = new MyThread();// ʵ����
		ImageView button = (ImageView) findViewById(R.id.centerImage);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (!isStart) {
					rippleBackground.startRippleAnimation();
					Toast.makeText(getApplicationContext(),
					"WifiScanner is running!", Toast.LENGTH_SHORT).show();
					isStart = true;
					mythread.stop = false;
					new Thread(mythread).start(); // �����߳�
				} else {
					Toast.makeText(getApplicationContext(),
					"WifiScanner stops!", Toast.LENGTH_SHORT).show();
					rippleBackground.stopRippleAnimation();
					mythread.stop = true;
					isStart = false;

				}
			}
		});
	}

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void foundDevice() {
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setDuration(400);
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		ArrayList<Animator> animatorList = new ArrayList<Animator>();
		ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(foundDevice,
				"ScaleX", 0f, 1.2f, 1f);
		animatorList.add(scaleXAnimator);
		ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(foundDevice,
				"ScaleY", 0f, 1.2f, 1f);
		animatorList.add(scaleYAnimator);
		animatorSet.playTogether(animatorList);
		foundDevice.setVisibility(View.VISIBLE);
		animatorSet.start();
	}

	class MyHandler extends Handler {
		int file_name_count = 1;

		public void handleMessage(Message msg) {
			file_name_count++;
			if (msg.what == -2) {
				Toast.makeText(MainActivity.this, "����Wifi�����״̬",
						Toast.LENGTH_SHORT).show();
			} else if (msg.what == -5) {
				Toast.makeText(MainActivity.this, "WIFI�����ѹر�",
						Toast.LENGTH_SHORT).show();
				// ����һ���Ի��� �Ƿ�����WIFI����
				AlertDialog.Builder builder = new Builder(MainActivity.this);
				builder.setMessage("ȷ����������");
				builder.setTitle("��ʾ");
				builder.setPositiveButton("ȷ��",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();
								// TODO Auto-generated method stub
								Intent intent = new Intent(
										"android.settings.WIFI_SETTINGS");
								startActivity(intent);

							}
						});
				builder.setNegativeButton("ȡ��",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								//isStart = false;
								// TODO Auto-generated method stub

							}
						});
				builder.create().show();
			} else if (msg.what == -1) {
				Toast.makeText(MainActivity.this, "Wifi����������",
						Toast.LENGTH_SHORT).show();
			} else {
			}

		}
	}

	//public int pre_result=1;
	public class MyThread implements Runnable {
		volatile boolean stop = false;
		Wifi_Result wifi_s = new Wifi_Result();
		String wserviceName = Context.WIFI_SERVICE;
		WifiManager wm = (WifiManager) getSystemService(wserviceName); //

		public void run() {

			while (!stop) {

				try {
					int result = wifi_s.WifiResult(wm);// ��ʱִ���������
					Message message = new Message();
					message.what=result;
					myhander.sendMessage(message);// ��������
					Thread.currentThread();
					Thread.sleep(5000);// �߳�ÿ��5sִ��һ��
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}

			}
		}

	}
}
