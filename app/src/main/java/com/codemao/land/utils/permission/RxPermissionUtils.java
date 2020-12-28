package com.codemao.land.utils.permission;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.codemao.land.R;
import com.codemao.land.utils.ui.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;


/**
 * 权限申请
 */

public class RxPermissionUtils {
    private static final String TAG = "RxPermissionUtils";
    private static Context mContext;
    private static RxPermissions mRxPermissions;

    public static RxPermissionUtils getInstance(Context context) {
        mContext = context;
        mRxPermissions = new RxPermissions((Activity) context);
        return new RxPermissionUtils();
    }


    public void getPowerStatus(PowerClickCallBack powerClickCallBack, String... permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            mRxPermissions.request(permissions)
                    .subscribe(isPowerOn -> {
                        Log.d(TAG,"==========isPowerOn======"+isPowerOn);
                        if (isPowerOn) {
                            powerClickCallBack.onClick();
                        } else {
                            ToastUtils.showCustom(mContext, mContext.getString(R.string.exception_power_on_tip));
                        }
                    });
        } else {
            boolean isCameraCanStatus = false;
            for (String str : permissions) {
                if (TextUtils.equals(str, Manifest.permission.CAMERA)) {
                    isCameraCanStatus = true;
                    break;
                }
            }

            if (isCameraCanStatus) {
                if (isCameraCanUse()) {
                    powerClickCallBack.onClick();
                } else {
                    ToastUtils.showCustom(mContext, mContext.getString(R.string.exception_power_on_tip));
                }
            } else {
                powerClickCallBack.onClick();
            }
        }
    }


    public interface PowerClickCallBack {
        void onClick();
    }


    /**
     * 判断权限集合
     * permissions 权限数组
     * return true-表示没有改权限  false-表示权限已开启
     */
    public boolean lacksPermissions(Context mContexts, String... mPermissions) {
        for (String permission : mPermissions) {
            if (lacksPermission(mContexts, permission)) {
                return true;
            }
        }
        return false;

    }

    private boolean lacksPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    /**
     * 测试当前摄像头能否被使用
     */
    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            canUse = false;
        } finally {
            if (mCamera != null && canUse) {
                mCamera.release();
                mCamera = null;
            } else {
                canUse = false;
            }
        }
        return canUse;
    }

}
