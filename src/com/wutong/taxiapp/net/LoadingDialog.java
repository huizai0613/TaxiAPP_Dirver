
package com.wutong.taxiapp.net;

import android.content.Context;
import android.widget.ImageView;

import com.iss.app.AbsDialog;
import com.wutong.taxiapp.util.AnimUtils;
import com.wutong.taxiapp_driver.R;

public class LoadingDialog extends AbsDialog {

    private ImageView imageView_heart;

    public LoadingDialog(Context context) {
        super(context, R.style.dialog_normal);
        setContentView(R.layout.dialog_loading);
        setProperty(1,1);
    }


    @Override
    protected void initView() {
        imageView_heart = (ImageView) findViewById(R.id.imageView_heart);
        AnimUtils.applyScaleAnim(imageView_heart);
        
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void setListener() {
        // TODO Auto-generated method stub
        
    }

}
