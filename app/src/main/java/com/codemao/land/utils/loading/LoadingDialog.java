package com.codemao.land.utils.loading;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.codemao.land.R;

public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context, boolean cancel) {
        super(context,R.style.dialog_loading);
        setCanceledOnTouchOutside(cancel);
        getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.c_858e99));
        init();

        Window window = getWindow();
        if (null != window) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = 500;
            lp.height = 500;
            window.setAttributes(lp);
        }
    }


    private void init() {
        setContentView(R.layout.dialog_loading);
    }
}
