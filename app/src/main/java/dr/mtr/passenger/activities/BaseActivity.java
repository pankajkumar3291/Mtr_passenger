package dr.mtr.passenger.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.franmontiel.localechanger.utils.ActivityRecreationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dr.mtr.passenger.R;
import dr.mtr.passenger.application.ApplicationHelper;
import dr.mtr.passenger.application.GlobalApplication;
import dr.mtr.passenger.components.SessionSecuredPreferences;
import dr.mtr.passenger.dialogs.GlobalAlertDialog;
import dr.mtr.passenger.gps.GpsLocationProvider;
import dr.mtr.passenger.gps.GpsLocationReceiver;
import dr.mtr.passenger.utils.LocalizationHelper;
import dr.mtr.passenger.utils.RxUtils;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static dr.mtr.passenger.utils.Constants.IS_LOGGED_IN;
import static dr.mtr.passenger.utils.Constants.LOGIN_PREFERENCE;

public abstract class BaseActivity extends AppCompatActivity implements RxUtils {

    private boolean isGPSStatusDialogShowing;
    private GpsLocationReceiver gpsLocationReceiver;
    private CompositeDisposable compositeDisposable;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 6;
    private Dialog dialog;
    private SessionSecuredPreferences loginPreferences;
    private String[] all_permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalizationHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.gpsLocationReceiver = new GpsLocationReceiver();
        this.compositeDisposable = new CompositeDisposable();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);

        //TODO this method is used to show the location dialog when gps is not available
        if (!isGpsEnabled(BaseActivity.this, BaseActivity.this)) {
            //TODO check here is user logged in app then clear sharedpreference and moved to splash screen
            if (loginPreferences.contains(IS_LOGGED_IN)) {
                loginPreferences.edit().clear().apply();
                Intent loginIntent = new Intent(BaseActivity.this, ActivitySplash.class);
                BaseActivity.this.startActivity(loginIntent);
                BaseActivity.this.finish();
            } else {
                return;
            }
        }

        ((GlobalApplication) getApplicationContext()).getBus().toObservableGpsProvider().subscribe(new Observer<GpsLocationProvider>() {
            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(GpsLocationProvider gpsLocationProvider) {
                if (!gpsLocationProvider.isGpsLocationProviderEnabled()) {
                    if (!isGPSStatusDialogShowing) {
                        isGPSStatusDialogShowing = true;
                        createDialogForGps();
                    }
                } else {
                    isGPSStatusDialogShowing = false;
                }
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onComplete() {

            }
        });

    }

    private void createDialogForGps() {
        new GlobalAlertDialog(this, false, true) {
            @Override
            public void onWarningDismiss() {
                super.onWarningDismiss();

                //TODO when user is logout out from app then clear shared preferences
                if (loginPreferences.contains(IS_LOGGED_IN)) {
                    loginPreferences.edit().clear().apply();
                    Intent loginIntent = new Intent(BaseActivity.this, ActivitySplash.class);
                    BaseActivity.this.startActivity(loginIntent);
                    BaseActivity.this.finish();
                }
            }
        }.show(R.string.turn_on_location);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(gpsLocationReceiver, new IntentFilter("android.location.PROVIDERS_CHANGED"));
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        decompose(compositeDisposable);
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            if (gpsLocationReceiver != null) {
                unregisterReceiver(gpsLocationReceiver);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityRecreationHelper.onResume(this);
    }

    @Override
    protected void onRestart() {
        if (hasPermissions(this, all_permissions)) {
            if (dialog != null) {
                dialog.dismiss();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkAndRequestPermissions();
            }
        }
        super.onRestart();
    }

    public boolean checkAndRequestPermissions() {
        int coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int readContact = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        int phonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (readContact != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }

        if (phonePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with all permissions
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for all permissions
                    if (perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                            showDialogOK("Camera and Storage Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    Intent intent = new Intent();
                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", BaseActivity.this.getPackageName(), null);
                                                    intent.setData(uri);
                                                    BaseActivity.this.startActivity(intent);
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            dialog = new Dialog(BaseActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.dialog_permission);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            Button dialogBtn_cancel = dialog.findViewById(R.id.button23);
                            dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                    System.exit(0);
                                }
                            });
                            Button dialogBtn_okay = dialog.findViewById(R.id.button22);
                            dialogBtn_okay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", BaseActivity.this.getPackageName(), null);
                                    intent.setData(uri);
                                    BaseActivity.this.startActivity(intent);
                                }
                            });
                            dialog.show();
                        }
                    }
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


}
