package dr.mtr.passenger.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.franmontiel.localechanger.utils.ActivityRecreationHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dr.mtr.passenger.R;
import dr.mtr.passenger.application.ApplicationHelper;
import dr.mtr.passenger.components.SessionSecuredPreferences;
import dr.mtr.passenger.dialogs.GlobalAlertDialog;
import dr.mtr.passenger.dialogs.GlobalProgressDialog;
import dr.mtr.passenger.eventbus.MapTypeEvent;
import dr.mtr.passenger.fragments.FragmentAbout;
import dr.mtr.passenger.fragments.FragmentDashboard;
import dr.mtr.passenger.fragments.FragmentSupport;
import dr.mtr.passenger.fragments.FragmentYourRides;
import dr.mtr.passenger.model.firebase.EOFireBaseResponse;
import dr.mtr.passenger.model.firebase.EOFireBaseTokenRequest;
import dr.mtr.passenger.mqtt.MQTTServiceClass;
import dr.mtr.passenger.networking.APIClient;
import dr.mtr.passenger.utils.GlobalUtil;
import dr.mtr.passenger.utils.LocalizationHelper;
import dr.mtr.passenger.utils.MapUtils;
import dr.mtr.passenger.utils.ObjectUtil;
import dr.mtr.passenger.utils.StringUtil;
import dr.mtr.passenger.utils.UIUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static dr.mtr.passenger.utils.Constants.ENGLISH_LANGUAGE_ID;
import static dr.mtr.passenger.utils.Constants.IS_LOGGED_IN;
import static dr.mtr.passenger.utils.Constants.LANGUAGE_PREFERENCE;
import static dr.mtr.passenger.utils.Constants.LOGIN_PREFERENCE;
import static dr.mtr.passenger.utils.Constants.PASSENGER_FIRST_NAME;
import static dr.mtr.passenger.utils.Constants.PASSENGER_INFO_ID;
import static dr.mtr.passenger.utils.Constants.PASSENGER_LAST_NAME;
import static dr.mtr.passenger.utils.Constants.PASSENGER_MOBILE;
import static dr.mtr.passenger.utils.Constants.PASSENGER_PHOTO;
import static dr.mtr.passenger.utils.Constants.RESPONSE_SUCCESS;
import static dr.mtr.passenger.utils.Constants.SELECTED_LANGUAGE;
import static dr.mtr.passenger.utils.Constants.SELECTED_LANG_ID;
import static dr.mtr.passenger.utils.Constants.SPANISH_LANGUAGE_ID;

public class MainActivity extends BaseActivity implements View.OnClickListener, MapUtils {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView tv_page_title, tv_user_name, tv_user_phone, tv_book_your_ride, tv_your_rides, tv_about, tv_support, tv_rate, tv_qr_code, tv_scanner, tv_share, tv_logout;
    private ImageView iv_hand_mobile, iv_location;
    private CircleImageView iv_circular_user_icon;
    boolean isClickedDoubleTap = false;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private SessionSecuredPreferences languagePreferences;
    private String passengerInfoId, firstName, lastName, phoneNumber, profileImage;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalizationHelper.onAttach(newBase));
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(ApplicationHelper.application(), MQTTServiceClass.class);
        if (!isServiceRunning(MQTTServiceClass.class, this)) {
            startService(intent);
        } /*else {
            stopService(intent);
        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        this.initView();
        this.setOnClickListener();
        //this.updateTokenOnServer();
        this.dataToView();

//        //TODO start a service from here
//        startService(new Intent(this, MQTTServiceClass.class));

        //TODO add dashboard fragment by default when open the app
        if (savedInstanceState == null) {
            addDashboardFragment(new FragmentDashboard());
        }
    }


    public Toolbar getToolbar() {
        return toolbar;
    }

    public TextView getPageTitle() {
        return tv_page_title;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.passengerInfoId = loginPreferences.getString(PASSENGER_INFO_ID, "");
        this.firstName = loginPreferences.getString(PASSENGER_FIRST_NAME, "");
        this.lastName = loginPreferences.getString(PASSENGER_LAST_NAME, "");
        this.phoneNumber = loginPreferences.getString(PASSENGER_MOBILE, "");
        this.profileImage = loginPreferences.getString(PASSENGER_PHOTO, "");

        this.toolbar = this.findViewById(R.id.toolbar);
        this.drawerLayout = this.findViewById(R.id.drawerLayout);
        this.tv_page_title = this.findViewById(R.id.tv_page_title);
        this.iv_hand_mobile = this.findViewById(R.id.iv_hand_mobile);
        this.iv_location = this.findViewById(R.id.iv_location);
        this.iv_circular_user_icon = this.findViewById(R.id.iv_circular_user_icon);
        this.tv_user_name = this.findViewById(R.id.textView6);
        this.tv_user_phone = this.findViewById(R.id.textView7);
        this.tv_book_your_ride = this.findViewById(R.id.textView5);
        this.tv_your_rides = this.findViewById(R.id.textView8);
        this.tv_about = this.findViewById(R.id.textView10);
        this.tv_support = this.findViewById(R.id.textView11);
        this.tv_rate = this.findViewById(R.id.textView12);
        this.tv_qr_code = this.findViewById(R.id.textView13);
        this.tv_scanner = this.findViewById(R.id.textView21);
        this.tv_share = this.findViewById(R.id.textView16);
        this.tv_logout = this.findViewById(R.id.textView17);
    }

    private void setOnClickListener() {
        this.tv_your_rides.setOnClickListener(this);
        this.tv_about.setOnClickListener(this);
        this.tv_support.setOnClickListener(this);
        this.tv_rate.setOnClickListener(this);
        this.tv_qr_code.setOnClickListener(this);
        this.tv_scanner.setOnClickListener(this);
        this.tv_share.setOnClickListener(this);
        this.tv_logout.setOnClickListener(this);
        this.iv_hand_mobile.setOnClickListener(this);
        this.iv_location.setOnClickListener(this);
        this.tv_book_your_ride.setOnClickListener(this);

        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.passengerInfoId = loginPreferences.getString(PASSENGER_INFO_ID, "");
        this.firstName = loginPreferences.getString(PASSENGER_FIRST_NAME, "");
        this.lastName = loginPreferences.getString(PASSENGER_LAST_NAME, "");
        this.phoneNumber = loginPreferences.getString(PASSENGER_MOBILE, "");
        this.profileImage = loginPreferences.getString(PASSENGER_PHOTO, "");

        this.toolbar = this.findViewById(R.id.toolbar);
        this.drawerLayout = this.findViewById(R.id.drawerLayout);
        this.tv_page_title = this.findViewById(R.id.tv_page_title);
        this.iv_hand_mobile = this.findViewById(R.id.iv_hand_mobile);
        this.iv_location = this.findViewById(R.id.iv_location);
        this.iv_circular_user_icon = this.findViewById(R.id.iv_circular_user_icon);
        this.tv_user_name = this.findViewById(R.id.textView6);
        this.tv_user_phone = this.findViewById(R.id.textView7);
        this.tv_book_your_ride = this.findViewById(R.id.textView5);
        this.tv_your_rides = this.findViewById(R.id.textView8);
        this.tv_about = this.findViewById(R.id.textView10);
        this.tv_support = this.findViewById(R.id.textView11);
        this.tv_rate = this.findViewById(R.id.textView12);
        this.tv_qr_code = this.findViewById(R.id.textView13);
        this.tv_scanner = this.findViewById(R.id.textView21);
        this.tv_share = this.findViewById(R.id.textView16);
        this.tv_logout = this.findViewById(R.id.textView17);

        this.tv_your_rides.setOnClickListener(this);
        this.tv_about.setOnClickListener(this);
        this.tv_support.setOnClickListener(this);
        this.tv_rate.setOnClickListener(this);
        this.tv_qr_code.setOnClickListener(this);
        this.tv_scanner.setOnClickListener(this);
        this.tv_share.setOnClickListener(this);
        this.tv_logout.setOnClickListener(this);
        this.iv_hand_mobile.setOnClickListener(this);
        this.iv_location.setOnClickListener(this);
        this.tv_book_your_ride.setOnClickListener(this);

        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        super.onSaveInstanceState(outState);
    }

    private void dataToView() {
        if (!ObjectUtil.isEmptyStr(firstName) && !ObjectUtil.isEmptyStr(lastName))
            this.tv_user_name.setText(firstName.concat(" ").concat(lastName));
        if (!ObjectUtil.isEmptyStr(phoneNumber))
            this.tv_user_phone.setText(phoneNumber);
        if (!ObjectUtil.isEmptyStr(profileImage))
            loadProfileImage(profileImage, this.iv_circular_user_icon);
    }

    private void loadProfileImage(String imagePath, ImageView imageView) {
        Picasso.get()
                .load(imagePath)
                .error(R.drawable.ic_profile)
                .fit()
                .into(imageView);
    }

    private void updateTokenOnServer() {
        if (!GlobalUtil.isNetworkAvailable(MainActivity.this)) {
            UIUtil.showNetworkDialog(MainActivity.this);
            return;
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
            String newToken = instanceIdResult.getToken();

            EOFireBaseTokenRequest eoFireBaseTokenRequest = new EOFireBaseTokenRequest();
            eoFireBaseTokenRequest.setToken(newToken);
            if (!ObjectUtil.isEmptyStr(this.passengerInfoId))
                eoFireBaseTokenRequest.setUserid(this.passengerInfoId);

            //progress.showProgressBar();
            apiInterface.updateToken(eoFireBaseTokenRequest).enqueue(new Callback<EOFireBaseResponse>() {
                @Override
                public void onResponse(Call<EOFireBaseResponse> call, Response<EOFireBaseResponse> response) {
                    //progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOFireBaseResponse loginResponse = response.body();
                        if (!ObjectUtil.isEmpty(loginResponse)) {
                            if (loginResponse.getStatus().equalsIgnoreCase(RESPONSE_SUCCESS)) {
                                Toast.makeText(MainActivity.this, "" + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "" + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOFireBaseResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        //progress.hideProgressBar();
                        Toast.makeText(MainActivity.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        });
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textView5:
                if (!checkDashboardFragment()) {
                    iv_location.setVisibility(View.VISIBLE);
                    iv_hand_mobile.setVisibility(View.VISIBLE);
                    this.tv_page_title.setText(R.string.dashboard_title);
                    this.replaceFragment(new FragmentDashboard(), "dashboard");
                    this.drawerLayout.closeDrawer(Gravity.START);
                } else {
                    this.drawerLayout.closeDrawer(Gravity.START);
                }
                break;
            case R.id.textView8:
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("yourRides");
                if (fragment != null && fragment.isVisible()) {
                    this.drawerLayout.closeDrawer(Gravity.START);
                } else {
                    iv_location.setVisibility(View.GONE);
                    iv_hand_mobile.setVisibility(View.GONE);
                    this.tv_page_title.setText(R.string.your_rides);
                    this.replaceFragment(new FragmentYourRides(), "yourRides");
                    this.drawerLayout.closeDrawer(Gravity.START);
                }
                break;
            case R.id.textView10:
                iv_location.setVisibility(View.GONE);
                iv_hand_mobile.setVisibility(View.GONE);
                this.tv_page_title.setText(R.string.about);
                this.replaceFragment(new FragmentAbout(), "about");
                this.drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.textView11:
                iv_location.setVisibility(View.GONE);
                iv_hand_mobile.setVisibility(View.GONE);
                this.tv_page_title.setText(R.string.support);
                this.replaceFragment(new FragmentSupport(), "support");
                this.drawerLayout.closeDrawer(Gravity.START);
                break;
            case R.id.textView12:
                this.showRateDialogForRate(MainActivity.this);
                break;
            case R.id.textView16:
                this.shareApp();
                break;
            case R.id.textView17:
                this.logoutFromApp();
                break;
            case R.id.iv_hand_mobile:
                this.selectLanguageDialog();
                break;
            case R.id.iv_location:
                this.selectCustomMap();
                break;
        }
    }

    public void showRateDialogForRate(Context context) {
        new GlobalAlertDialog(this, true, false) {
            @SuppressLint("WrongConstant")
            @Override
            public void onConfirmation() {
                super.onConfirmation();
                //TODO when user is rate the app on play store
                Uri uri = Uri.parse("market://details?id=" + ApplicationHelper.application().packageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    MainActivity.this.startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    MainActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + ApplicationHelper.application().packageName())));
                }
            }
        }.show(R.string.rate_app_on_playstore);
    }

    private void shareApp() {
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Insert Subject here");
        String app_url = "https://play.google.com/store/apps/details?id=" + ApplicationHelper.application().packageName();
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, app_url);
        startActivity(Intent.createChooser(shareIntent, "Share Via"));
    }

    private void logoutFromApp() {
        new GlobalAlertDialog(this, true, false) {
            @SuppressLint("WrongConstant")
            @Override
            public void onConfirmation() {
                super.onConfirmation();

                //TODO when user is logout out from app then clear shared preferences
                if (loginPreferences.contains(IS_LOGGED_IN)) {
                    loginPreferences.edit().clear().apply();
                    if (drawerLayout.isDrawerOpen(Gravity.END)) {
                        drawerLayout.closeDrawer(Gravity.END);
                    }
                    Intent loginIntent = new Intent(MainActivity.this, ActivitySplash.class);
                    MainActivity.this.startActivity(loginIntent);
                    MainActivity.this.finish();
                }

            }
        }.show(R.string.are_you_sure_you_want_to_logout);
    }

    private void addDashboardFragment(FragmentDashboard fragmentDashboard) {
        this.replaceFragment(fragmentDashboard, "dashboard");
    }

    public void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private boolean checkDashboardFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("dashboard");
        if (fragment != null && fragment.isVisible())
            return true;
        else
            return false;
    }

    @Override
    public void onBackPressed() {
        if (isClickedDoubleTap) {
            super.onBackPressed();
            return;
        }

        if (checkDashboardFragment()) {
            this.isClickedDoubleTap = true;
            Snackbar snackbar1 = Snackbar.make(findViewById(R.id.fragmentContainer), "Please click BACK again to exit!", Snackbar.LENGTH_SHORT);
            snackbar1.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isClickedDoubleTap = false;
                }
            }, 2000);
        } else {
            iv_location.setVisibility(View.VISIBLE);
            iv_hand_mobile.setVisibility(View.VISIBLE);
            this.tv_page_title.setText(R.string.dashboard_title);
            replaceFragment(new FragmentDashboard(), "dashboard");
        }
    }

    private void selectLanguageDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_select_language);

        ConstraintLayout englishLayout = dialog.findViewById(R.id.constraintLayout2);
        ConstraintLayout spanishLayout = dialog.findViewById(R.id.constraintLayout3);

        englishLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LocalizationHelper.getLanguage(MainActivity.this).equals(ENGLISH_LANGUAGE_ID)) {
                    dialog.dismiss();
                } else {
                    languagePreferences.edit().putString(SELECTED_LANG_ID, ENGLISH_LANGUAGE_ID).apply();
                    languagePreferences.edit().putString(SELECTED_LANGUAGE, "English").apply();
                    LocalizationHelper.setLocale(MainActivity.this, ENGLISH_LANGUAGE_ID);
                    ActivityRecreationHelper.recreate(MainActivity.this, true);
                    dialog.dismiss();
                }
            }
        });

        spanishLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LocalizationHelper.getLanguage(MainActivity.this).equals(SPANISH_LANGUAGE_ID)) {
                    dialog.dismiss();
                } else {
                    languagePreferences.edit().putString(SELECTED_LANG_ID, SPANISH_LANGUAGE_ID).apply();
                    languagePreferences.edit().putString(SELECTED_LANGUAGE, "Spanish").apply();
                    LocalizationHelper.setLocale(MainActivity.this, SPANISH_LANGUAGE_ID);
                    ActivityRecreationHelper.recreate(MainActivity.this, true);
                    dialog.dismiss();
                }
            }
        });

        dialog.findViewById(R.id.constraintLayout4).findViewById(R.id.constraintLayout4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityRecreationHelper.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ActivityRecreationHelper.onDestroy(this);
        super.onDestroy();
    }

    private Dialog mapDialog;

    private void selectCustomMap() {
        mapDialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        mapDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(mapDialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        mapDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mapDialog.setCancelable(false);
        mapDialog.setContentView(R.layout.dialog_select_map);

        ConstraintLayout cancelLayout = mapDialog.findViewById(R.id.constraintLayout7);
        ConstraintLayout defaultLayout = mapDialog.findViewById(R.id.constraintLayout2);
        ConstraintLayout darkLayout = mapDialog.findViewById(R.id.constraintLayout3);
        ConstraintLayout paperLayout = mapDialog.findViewById(R.id.constraintLayout4);
        ConstraintLayout customLayout = mapDialog.findViewById(R.id.constraintLayout5);
        ConstraintLayout retroLayout = mapDialog.findViewById(R.id.constraintLayout6);

        cancelLayout.setOnClickListener(customMapClickListener);
        defaultLayout.setOnClickListener(customMapClickListener);
        darkLayout.setOnClickListener(customMapClickListener);
        paperLayout.setOnClickListener(customMapClickListener);
        customLayout.setOnClickListener(customMapClickListener);
        retroLayout.setOnClickListener(customMapClickListener);

        mapDialog.show();
    }

    View.OnClickListener customMapClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.constraintLayout7:
                    mapDialog.dismiss();
                    break;
                case R.id.constraintLayout2:
                    EventBus.getDefault().post(new MapTypeEvent(StringUtil.getStringForID(R.string.default_str)));
                    mapDialog.dismiss();
                    break;
                case R.id.constraintLayout3:
                    EventBus.getDefault().post(new MapTypeEvent(StringUtil.getStringForID(R.string.dark)));
                    mapDialog.dismiss();
                    break;
                case R.id.constraintLayout4:
                    EventBus.getDefault().post(new MapTypeEvent(StringUtil.getStringForID(R.string.paper)));
                    mapDialog.dismiss();
                    break;
                case R.id.constraintLayout5:
                    EventBus.getDefault().post(new MapTypeEvent(StringUtil.getStringForID(R.string.custom)));
                    mapDialog.dismiss();
                    break;
                case R.id.constraintLayout6:
                    EventBus.getDefault().post(new MapTypeEvent(StringUtil.getStringForID(R.string.retro)));
                    mapDialog.dismiss();
                    break;
            }
        }
    };

}
