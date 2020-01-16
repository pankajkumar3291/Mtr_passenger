package dr.mtr.passenger.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import dr.mtr.passenger.R;

public class DriverIsWaitingDialog extends Dialog {

    public DriverIsWaitingDialog(Context context, int theme_black_noTitleBar_fullscreen) {
        super(context, theme_black_noTitleBar_fullscreen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_ride_scheduled);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 3000);
    }

}
