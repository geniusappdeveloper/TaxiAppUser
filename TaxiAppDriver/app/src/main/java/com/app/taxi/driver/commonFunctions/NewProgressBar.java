package com.app.taxi.driver.commonFunctions;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.app.taxi.driver.R;

/**
 * Created by Admin on 2/5/2018.
 */

public class NewProgressBar {
    Context con;
    Dialog dialog;
    public NewProgressBar(Context con  )
    {
        this.con  =con;
    }
    public void show ()
    {

        dialog = new Dialog(con);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.progressbar_layout);

        dialog.show();


    }

    public void dismiss()
    {

        dialog.dismiss();
    }
}
