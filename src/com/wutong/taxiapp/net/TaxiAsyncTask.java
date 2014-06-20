
package com.wutong.taxiapp.net;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

public abstract class TaxiAsyncTask<Params, Progress, Result> extends
        AsyncTask<Params, Progress, Result> {
	protected TaxiLib mTaxiLib;
    private LoadingDialog ld;
    private boolean isShow=true;
    private Context mActivity;
    protected String exception;

    
    
    public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

	public TaxiAsyncTask(Context activity) {
        this(activity, true);
    }
    
    public TaxiAsyncTask(Context activity, final DialogInterface.OnCancelListener l) {
        this(activity, l, true);
    }

    public TaxiAsyncTask(Context activity, final boolean cancelable) {
        this(activity, null, cancelable);
    }

    public TaxiAsyncTask(Context activity, final DialogInterface.OnCancelListener l,
            final boolean cancelable) {
        super();
        this.mActivity=activity;
        ld = new LoadingDialog(activity);
        ld.setCancelable(cancelable);
        ld.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (cancelable) {
                    TaxiAsyncTask.this.cancel(true);
                }
                if (l != null) {
                    l.onCancel(dialog);
                }
            }
        });
    }

    @Override
    protected Result doInBackground(Params... params) {
//    	mTaxiLib = TaxiLib.getInstance(mActivity);
        return null;
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if(ld.isShowing()){
        	ld.dismiss();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(isShow){
        	ld.show();
        }
    }

}
