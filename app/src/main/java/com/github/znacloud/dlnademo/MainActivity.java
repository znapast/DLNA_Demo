package com.github.znacloud.dlnademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xunlei.downloadprovider.dlna.OnDLNADialogListener;
import com.xunlei.downloadprovider.dlna.ShowDLNADialog;

public class MainActivity extends AppCompatActivity {

    private ShowDLNADialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         mDialog = new ShowDLNADialog(this, new OnDLNADialogListener() {
            @Override
            public void onDialogDismiss(boolean isDismiss, MediaPlayerPlayCMD playCMD) {

            }

            @Override
            public void onListChange(boolean isAdd) {

            }
        });

        findViewById(android.R.id.content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.showDialog(OnDLNADialogListener.MediaPlayerPlayCMD.Play_None, "TestName", "http;//test/url", 0);
            }
        });


    }

    @Override
    public void onBackPressed() {
        if(mDialog.isDialogShowing()){
            mDialog.hideDialog();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if(mDialog != null){
            mDialog.release();
        }
        super.onDestroy();
    }
}
