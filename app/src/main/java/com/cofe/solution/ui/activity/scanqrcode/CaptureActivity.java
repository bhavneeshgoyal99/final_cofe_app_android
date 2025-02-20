/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cofe.solution.ui.activity.scanqrcode;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.xm.activity.base.XMBasePresenter;

import java.io.IOException;
import java.lang.reflect.Field;

import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseActivity;

import io.reactivex.annotations.Nullable;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
@RuntimePermissions
public final class CaptureActivity extends DemoBaseActivity implements SurfaceHolder.Callback, View.OnClickListener {

	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private InactivityTimer inactivityTimer;
	private SurfaceView scanPreview = null;
	private RelativeLayout scanContainer;
	private RelativeLayout scanCropView;
	private ImageView scanLine;
	private ImageView mFlash;
	private ImageView imgGallary;
	private ImageView imgMannual;

	private Rect mCropRect = null;

	private static final int PICK_IMAGE = 1;


	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	private boolean isHasSurface = false;

	@Override
	public XMBasePresenter getPresenter() {
		return null;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_qr_scan);

		scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
		scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
		scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
		scanLine = (ImageView) findViewById(R.id.capture_scan_line);
		mFlash = (ImageView) findViewById(R.id.capture_flash);
		imgGallary = (ImageView) findViewById(R.id.imgGallary);
		imgMannual = (ImageView) findViewById(R.id.imgMannual);
		mFlash.setOnClickListener(this);
		imgGallary.setOnClickListener(this);
		imgMannual.setOnClickListener(this);

		inactivityTimer = new InactivityTimer(CaptureActivity.this);
		TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,
				0.9f);
		animation.setDuration(4000);
		animation.setRepeatCount(-1);
		animation.setRepeatMode(Animation.RESTART);
		scanLine.startAnimation(animation);

		CaptureActivityPermissionsDispatcher.initDataWithPermissionCheck(this);
	}

	/**
	 * 获取摄像头权限
	 * Get camera permissions
	 */
	@NeedsPermission({Manifest.permission.CAMERA})
	protected void initData() {

	}


	@Override
	protected void onResume() {
		super.onResume();

		cameraManager = new CameraManager(getApplication());

		handler = null;

		if (isHasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(scanPreview.getHolder());
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			scanPreview.getHolder().addCallback(CaptureActivity.this);
		}

		inactivityTimer.onResume();
	}

	@Override
	protected void onPause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}

		if (inactivityTimer != null) {
			inactivityTimer.onPause();
		}

		if (cameraManager != null) {
			cameraManager.closeDriver();
		}

		if (!isHasSurface) {
			scanPreview.getHolder().removeCallback(this);
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (inactivityTimer != null) {
			inactivityTimer.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!isHasSurface) {
			isHasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isHasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 *
	 * @param rawResult The contents of the barcode.
	 * @param bundle    The extras
	 */
	public void handleDecode(final Result rawResult, Bundle bundle) {
		inactivityTimer.onActivity();
		Intent intent = new Intent();
		intent.putExtra("result", rawResult.getText());
		setResult(RESULT_OK, intent);
		finish();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			// Creating the handler starts the preview, which can also throw a
			// RuntimeException.
			if (handler == null) {
				handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
			}

			initCrop();
		} catch (IOException ioe) {
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		// camera error
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(R.string.open_camera_error);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}

		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			//handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
	}

	public Rect getCropRect() {
		return mCropRect;
	}

	/**
	 * 初始化截取的矩形区域
	 * Initialize the truncated rectangular region
	 */

	private void initCrop() {
		int cameraWidth = cameraManager.getCameraResolution().y;
		int cameraHeight = cameraManager.getCameraResolution().x;

		/** 获取布局中扫描框的位置信息 */
		int[] location = new int[2];
		scanCropView.getLocationInWindow(location);

		int cropLeft = location[0];
		int cropTop = location[1] - getStatusBarHeight();

		int cropWidth = scanCropView.getWidth();
		int cropHeight = scanCropView.getHeight();

		/** 获取布局容器的宽高 */
		int containerWidth = scanContainer.getWidth();
		int containerHeight = scanContainer.getHeight();

		/** 计算最终截取的矩形的左上角顶点x坐标 */
		int x = cropLeft * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的左上角顶点y坐标 */
		int y = cropTop * cameraHeight / containerHeight;

		/** 计算最终截取的矩形的宽度 */
		int width = cropWidth * cameraWidth / containerWidth;
		/** 计算最终截取的矩形的高度 */
		int height = cropHeight * cameraHeight / containerHeight;

		/** 生成最终的截取的矩形 */
		mCropRect = new Rect(x, y, width + x, height + y);
	}

	private int getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			return getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	private boolean flag;

	protected void openLight() {
		if (flag == true) {
			flag = false;
			// 关闪光灯
			cameraManager.offLight();
			// mFlash.setBackgroundResource(R.mipmap.flash_default);
		} else {
			cameraManager.openLight();
			if (cameraManager.hasFlash) {
				// mFlash.setBackgroundResource(R.mipmap.flash_open);
				flag = true;
			} else {
				Toast.makeText(CaptureActivity.this, getString(R.string.no_flash), Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.capture_flash:
				openLight();
				break;

			case R.id.imgGallary:
				openGallery();
				break;
			case R.id.imgMannual:
				showMannualQrDialog();
				break;


			default:
				break;
		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		CaptureActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
	}

	// simran open gallery to detect scanner
	private void openGallery() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		intent.setType("image/*");
		startActivityForResult(intent, PICK_IMAGE);
	}

	// simran gallery scanner manage
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
			Uri imageUri = data.getData();
			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
				decodeBitmap(bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// simran decode method
	private void decodeBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth(), height = bitmap.getHeight();
		int[] intArray = new int[width * height];
		bitmap.getPixels(intArray, 0, width, 0, 0, width, height);

		LuminanceSource source = new RGBLuminanceSource(width, height, intArray);
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

		try {
			MultiFormatReader reader = new MultiFormatReader();
			Result result = reader.decode(binaryBitmap);

			Intent intent = new Intent();
			intent.putExtra("result", result.getText());
			setResult(RESULT_OK, intent);
			Toast.makeText(this, getString(R.string.scanned) + result.getText(), Toast.LENGTH_LONG).show();
			finish();
		} catch (NotFoundException e) {
			finish();
			e.printStackTrace();
			//Toast.makeText(this, "No barcode found", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			finish();
			e.printStackTrace();
			 Toast.makeText(this, getString(R.string.no_barcode_found), Toast.LENGTH_SHORT).show();
		}
	}


	// simran dialog for manualy qr
	private void showMannualQrDialog() {
		// Create and configure dialog
		Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_qr_mannual);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		// Get views from dialog layout

		EditText etDeviceId = dialog.findViewById(R.id.etDeviceId);
		Button btnCancel = dialog.findViewById(R.id.btnCancel);
		Button btnOk = dialog.findViewById(R.id.btnOk);

		// Set button click listener
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				Intent intent = new Intent();
				intent.putExtra("useredit_sr ", etDeviceId.getText().toString());
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		// Show the dialog
		dialog.show();
	}


}