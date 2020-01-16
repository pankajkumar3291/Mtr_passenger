package dr.mtr.passenger.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import dr.mtr.passenger.R;
import dr.mtr.passenger.application.ApplicationHelper;
import dr.mtr.passenger.components.SessionSecuredPreferences;
import dr.mtr.passenger.dialogs.GlobalProgressDialog;
import dr.mtr.passenger.model.login.EOLoginRequest;
import dr.mtr.passenger.model.login.EOLoginResponse;
import dr.mtr.passenger.networking.APIClient;
import dr.mtr.passenger.utils.GlobalUtil;
import dr.mtr.passenger.utils.LocalizationHelper;
import dr.mtr.passenger.utils.ObjectUtil;
import dr.mtr.passenger.utils.RxUtils;
import dr.mtr.passenger.utils.UIUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static dr.mtr.passenger.utils.Constants.DOMAIN_ID;
import static dr.mtr.passenger.utils.Constants.DOMAIN_ID_Value;
import static dr.mtr.passenger.utils.Constants.IS_LOGGED_IN;
import static dr.mtr.passenger.utils.Constants.LOGIN_PREFERENCE;
import static dr.mtr.passenger.utils.Constants.PASSENGER_ALLOW_CREDIT;
import static dr.mtr.passenger.utils.Constants.PASSENGER_FIRST_NAME;
import static dr.mtr.passenger.utils.Constants.PASSENGER_INFO_ID;
import static dr.mtr.passenger.utils.Constants.PASSENGER_LAST_NAME;
import static dr.mtr.passenger.utils.Constants.PASSENGER_MOBILE;
import static dr.mtr.passenger.utils.Constants.PASSENGER_PHOTO;
import static dr.mtr.passenger.utils.Constants.RESPONSE_SUCCESS;

public class ActivityLogin extends BaseActivity implements View.OnClickListener, RxUtils {

    private ImageView iv_back;
    private TextInputEditText et_email_id, et_password;
    private TextView tv_forget_password;
    private Button btnSignin;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String[] all_permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalizationHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.initView();
        this.setOnClickListner();

        if (hasPermissions(this, all_permissions)) {

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkAndRequestPermissions();
            }
        }
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.iv_back = this.findViewById(R.id.iv_back);
        this.et_email_id = this.findViewById(R.id.et_email_id);
        this.et_password = this.findViewById(R.id.et_password);
        this.tv_forget_password = this.findViewById(R.id.textView);
        this.btnSignin = this.findViewById(R.id.btnSignin);
    }

    private void setOnClickListner() {
        this.iv_back.setOnClickListener(this);
        this.btnSignin.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.btnSignin:
                if (isValidLogin()) {
                    loginCustomer();
                }
                break;
        }
    }

    private void loginCustomer() {

        if (!GlobalUtil.isNetworkAvailable(ActivityLogin.this)) {
            UIUtil.showNetworkDialog(ActivityLogin.this);
            return;
        }

        EOLoginRequest loginRequest = new EOLoginRequest();
        loginRequest.setEmail(ObjectUtil.getTextFromView(et_email_id));
        loginRequest.setPassword(ObjectUtil.getTextFromView(et_password));

        progress.showProgressBar();
        apiInterface.customerLogin(loginRequest).enqueue(new Callback<EOLoginResponse>() {
            @Override
            public void onResponse(Call<EOLoginResponse> call, Response<EOLoginResponse> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    EOLoginResponse loginResponse = response.body();
                    if (!ObjectUtil.isEmpty(loginResponse)) {
                        if (loginResponse.getStatus().equalsIgnoreCase(RESPONSE_SUCCESS)) {

                            //TODO save isLogged flag and passenger info id and passenger name into shared preference
                            loginPreferences.edit().putInt(DOMAIN_ID, DOMAIN_ID_Value).apply();
                            loginPreferences.edit().putBoolean(IS_LOGGED_IN, true).apply();
                            loginPreferences.edit().putString(PASSENGER_INFO_ID, loginResponse.getData().getPassanger_info_id()).apply();
                            loginPreferences.edit().putString(PASSENGER_FIRST_NAME, loginResponse.getData().getPassanger_name()).apply();
                            loginPreferences.edit().putString(PASSENGER_LAST_NAME, loginResponse.getData().getPassenger_lname()).apply();
                            loginPreferences.edit().putString(PASSENGER_MOBILE, loginResponse.getData().getMobile_number()).apply();
                            loginPreferences.edit().putString(PASSENGER_PHOTO, loginResponse.getData().getProfile_image()).apply();
                            loginPreferences.edit().putString(PASSENGER_ALLOW_CREDIT, loginResponse.getData().getAllowCredit()).apply();

                            Intent permissionIntent = new Intent(ActivityLogin.this, MainActivity.class);
                            permissionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(permissionIntent);
                            ActivityLogin.this.finish();

                        } else {
                            Toast.makeText(ActivityLogin.this, "" + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EOLoginResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(ActivityLogin.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isValidLogin() {
        String errorMsg = null;

        String emailId = ObjectUtil.getTextFromView(et_email_id);
        String password = ObjectUtil.getTextFromView(et_password);

        if (ObjectUtil.isEmptyStr(emailId) || ObjectUtil.isEmptyStr(password)) {
            errorMsg = this.getString(R.string.all_fields_required);
        } else if (!GlobalUtil.isValidEmail(emailId)) {
            errorMsg = this.getString(R.string.valid_email);
        } else if (!isGpsEnabled(ActivityLogin.this, ActivityLogin.this)) {
            return false;
        }

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
