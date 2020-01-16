package dr.mtr.passenger.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import dr.mtr.passenger.R;

public class ActivityPermissions extends BaseActivity {

    private TextView tv_allow_permission;
    private String[] all_permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        this.initView();
    }

    private void initView() {
        this.tv_allow_permission = this.findViewById(R.id.tv_allow_permission);

        if (hasPermissions(this, all_permissions)) {
            Intent dashboardIntent = new Intent(ActivityPermissions.this, MainActivity.class);
            dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(dashboardIntent);
            ActivityPermissions.this.finish();
        } else {
            
            this.tv_allow_permission.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkAndRequestPermissions();
                    }
                }
            });

        }

//        boolean isPermissionGranted = checkAndRequestPermissions();
//        if (isPermissionGranted) {
//            Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show();
//            Intent dashboardIntent = new Intent(ActivityPermissions.this, MainActivity.class);
//            dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(dashboardIntent);
//            ActivityPermissions.this.finish();
//        }

    }

//    @Override
//    public void onClick(View view) {
//        if (view.getId() == R.id.tv_allow_permission) {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                checkAndRequestPermissions();
//
////                boolean isPermissionGranted = checkAndRequestPermissions();
////                if (isPermissionGranted) {
////                    Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show();
////                    Intent dashboardIntent = new Intent(ActivityPermissions.this, MainActivity.class);
////                    dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                    startActivity(dashboardIntent);
////                    ActivityPermissions.this.finish();
////                } else {
////                    Toast.makeText(this, "not granted", Toast.LENGTH_SHORT).show();
////                    checkAndRequestPermissions();
////                }
//
//            }
//
//        }
//    }


}
