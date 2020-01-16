package dr.mtr.passenger.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import dr.mtr.passenger.R;
import dr.mtr.passenger.dialogs.GlobalAlertDialog;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.LOCATION_SERVICE;

public interface RxUtils {

    default <T> io.reactivex.SingleTransformer<T, T> applySchedulersForSingle() {
        return new io.reactivex.SingleTransformer<T, T>() {

            @Override
            public SingleSource<T> apply(Single<T> upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    default void decompose(CompositeDisposable compositeDisposable) {
        if (compositeDisposable != null) {
            if (!compositeDisposable.isDisposed()) {
                compositeDisposable.dispose();
            }
        }
    }

    default boolean isGpsEnabled(Context context, Activity activity) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (locationManager != null)
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.i("About GPS", "GPS is Enabled in your device");
                    return true;
                } else {
                    showGpsLocationDialog(activity);
                    return false;
                }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    default void showGpsLocationDialog(Activity activity) {
        new GlobalAlertDialog(activity, true, false) {
            @Override
            public void onConfirmation() {
                super.onConfirmation();
                activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
            }
        }.show(R.string.turn_on_location);
    }


}
