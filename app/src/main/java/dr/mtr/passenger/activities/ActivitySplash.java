package dr.mtr.passenger.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import dr.mtr.passenger.R;
import dr.mtr.passenger.application.ApplicationHelper;
import dr.mtr.passenger.components.SessionSecuredPreferences;
import dr.mtr.passenger.mqtt.MQTTServiceClass;
import dr.mtr.passenger.utils.LocalizationHelper;
import dr.mtr.passenger.utils.MapUtils;
import dr.mtr.passenger.utils.ObjectUtil;

import static dr.mtr.passenger.utils.Constants.ENGLISH_LANGUAGE_ID;
import static dr.mtr.passenger.utils.Constants.IS_LOGGED_IN;
import static dr.mtr.passenger.utils.Constants.LANGUAGE_PREFERENCE;
import static dr.mtr.passenger.utils.Constants.LOGIN_PREFERENCE;
import static dr.mtr.passenger.utils.Constants.SELECTED_LANGUAGE;
import static dr.mtr.passenger.utils.Constants.SPANISH_LANGUAGE_ID;

public class ActivitySplash extends BaseActivity implements View.OnClickListener, MapUtils {

    private TextView tv_sign_up, tv_log_in;
    private LinearLayout layoutLoginSignup;
    private SessionSecuredPreferences loginPreferences;
    private SessionSecuredPreferences languagePreferences;
    private boolean isLoggedIn;
    private String[] all_permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalizationHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        this.initView();
        this.setOnClickListener();

        //TODO first check in language preference which language contains
        if (languagePreferences.contains(SELECTED_LANGUAGE)) {
            if (languagePreferences.getString(SELECTED_LANGUAGE, "").equalsIgnoreCase("English")) {
                LocalizationHelper.setLocale(ActivitySplash.this, ENGLISH_LANGUAGE_ID);
            } else {
                LocalizationHelper.setLocale(ActivitySplash.this, SPANISH_LANGUAGE_ID);
            }
        }

        //TODO first time to check IS_LOGGED_IN exist or not
        if (isLoggedIn) {
            this.layoutLoginSignup.setVisibility(View.GONE);
            openMainActivity();
        } else {
            this.layoutLoginSignup.setVisibility(View.VISIBLE);
            if (hasPermissions(this, all_permissions)) {
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkAndRequestPermissions();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(ApplicationHelper.application(), MQTTServiceClass.class);
        if (!isServiceRunning(MQTTServiceClass.class, this)) {
            ActivitySplash.this.startService(intent);
        }
    }

    private void initView() {
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.isLoggedIn = this.loginPreferences.getBoolean(IS_LOGGED_IN, false);
        this.layoutLoginSignup = this.findViewById(R.id.layoutLoginSignup);
        this.tv_sign_up = this.findViewById(R.id.tv_sign_up);
        this.tv_log_in = this.findViewById(R.id.tv_log_in);
    }

    private void setOnClickListener() {
        this.tv_log_in.setOnClickListener(this);
        this.tv_sign_up.setOnClickListener(this);
    }

    private void openMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent dashboardIntent = new Intent(ActivitySplash.this, MainActivity.class);
                dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(dashboardIntent);
                ActivitySplash.this.finish();
            }
        }, 2000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_log_in:
                this.startActivity(new Intent(this, ActivityLogin.class));
                break;
            case R.id.tv_sign_up:
                this.startActivity(new Intent(this, ActivitySignUp.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String language = languagePreferences.getString(SELECTED_LANGUAGE, "");
        if (ObjectUtil.isNonEmptyStr(language)) {
            this.tv_log_in.setText(language.equals("English") ? getString(R.string.login) : "Iniciar Sesión");
            this.tv_sign_up.setText(language.equals("English") ? getString(R.string.sign_up) : "REGÍSTRATE");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
