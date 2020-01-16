package dr.mtr.passenger.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import dr.mtr.passenger.R;
import dr.mtr.passenger.application.ApplicationHelper;
import dr.mtr.passenger.components.SessionSecuredPreferences;
import dr.mtr.passenger.dialogs.GlobalProgressDialog;
import dr.mtr.passenger.model.signup.EOSignUpRequest;
import dr.mtr.passenger.model.signup.EOSignUpResponse;
import dr.mtr.passenger.networking.APIClient;
import dr.mtr.passenger.utils.GlobalUtil;
import dr.mtr.passenger.utils.LocalizationHelper;
import dr.mtr.passenger.utils.ObjectUtil;
import dr.mtr.passenger.utils.RxUtils;
import dr.mtr.passenger.utils.UIUtil;
import dr.mtr.passenger.widgets.UsPhoneNumberFormatter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static dr.mtr.passenger.utils.Constants.DOMAIN_ID_Value;
import static dr.mtr.passenger.utils.Constants.IS_LOGGED_IN;
import static dr.mtr.passenger.utils.Constants.LOGIN_PREFERENCE;
import static dr.mtr.passenger.utils.Constants.PASSENGER_ALLOW_CREDIT;
import static dr.mtr.passenger.utils.Constants.PASSENGER_FIRST_NAME;
import static dr.mtr.passenger.utils.Constants.PASSENGER_INFO_ID;
import static dr.mtr.passenger.utils.Constants.PASSENGER_LAST_NAME;
import static dr.mtr.passenger.utils.Constants.PASSENGER_MOBILE;
import static dr.mtr.passenger.utils.Constants.RESPONSE_SUCCESS;

public class ActivitySignUp extends BaseActivity implements View.OnClickListener, RxUtils {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private ImageView iv_back;
    private TextInputEditText et_first_name, et_last_name, et_email_id, et_password, et_phone_number;
    private Button btnSignUp;
    private String[] all_permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS};

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalizationHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.initView();
        this.setOnClickListener();

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
        this.et_first_name = this.findViewById(R.id.et_first_name);
        this.et_last_name = this.findViewById(R.id.et_last_name);
        this.et_email_id = this.findViewById(R.id.et_email_id);
        this.et_password = this.findViewById(R.id.et_password);
        this.et_phone_number = this.findViewById(R.id.et_phone_number);
        this.btnSignUp = this.findViewById(R.id.btnSignUp);

        this.et_phone_number.addTextChangedListener(new UsPhoneNumberFormatter(et_phone_number, /*"+# (###) ###-####"*/"(###) ###-####"));
    }

    private void setOnClickListener() {
        this.iv_back.setOnClickListener(this);
        this.btnSignUp.setOnClickListener(this);
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
            case R.id.btnSignUp:
                if (isValidRegistration()) {
                    this.registerNewUser();
                }
                break;
        }
    }

    private void registerNewUser() {

        if (!GlobalUtil.isNetworkAvailable(ActivitySignUp.this)) {
            UIUtil.showNetworkDialog(ActivitySignUp.this);
            return;
        }

        progress.showProgressBar();
        EOSignUpRequest signUpRequest = new EOSignUpRequest();
        signUpRequest.setFirstname(ObjectUtil.getTextFromView(et_first_name));
        signUpRequest.setLastname(ObjectUtil.getTextFromView(et_last_name));
        signUpRequest.setEmail(ObjectUtil.getTextFromView(et_email_id));
        signUpRequest.setMobile(ObjectUtil.getTextFromView(et_phone_number));
        signUpRequest.setPassword(ObjectUtil.getTextFromView(et_password));
        signUpRequest.setDomainid(String.valueOf(DOMAIN_ID_Value));

        apiInterface.customerRegistration(signUpRequest).enqueue(new Callback<EOSignUpResponse>() {
            @Override
            public void onResponse(Call<EOSignUpResponse> call, Response<EOSignUpResponse> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    EOSignUpResponse signUpResponse = response.body();
                    if (!ObjectUtil.isEmpty(signUpResponse)) {
                        if (signUpResponse.getStatus().equalsIgnoreCase(RESPONSE_SUCCESS)) {

                            //TODO save isLogged flag and passenger info id into shared preference
                            loginPreferences.edit().putBoolean(IS_LOGGED_IN, true).apply();
                            loginPreferences.edit().putString(PASSENGER_INFO_ID, signUpResponse.getId()).apply();
                            loginPreferences.edit().putString(PASSENGER_FIRST_NAME, ObjectUtil.getTextFromView(et_first_name)).apply();
                            loginPreferences.edit().putString(PASSENGER_LAST_NAME, ObjectUtil.getTextFromView(et_last_name)).apply();
                            loginPreferences.edit().putString(PASSENGER_MOBILE, ObjectUtil.getTextFromView(et_phone_number)).apply();
                            loginPreferences.edit().putString(PASSENGER_ALLOW_CREDIT, signUpResponse.getData().getAllowCredit()).apply();

                            Intent permissionIntent = new Intent(ActivitySignUp.this, MainActivity.class);
                            permissionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(permissionIntent);
                            ActivitySignUp.this.finish();

                        } else {
                            Toast.makeText(ActivitySignUp.this, "" + signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EOSignUpResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(ActivitySignUp.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isValidRegistration() {
        String errorMsg = null;

        String firstName = ObjectUtil.getTextFromView(et_first_name);
        String lastName = ObjectUtil.getTextFromView(et_last_name);
        String emailId = ObjectUtil.getTextFromView(et_email_id);
        String password = ObjectUtil.getTextFromView(et_password);
        String phoneNumber = ObjectUtil.getTextFromView(et_phone_number);

        if (ObjectUtil.isEmptyStr(firstName) || ObjectUtil.isEmptyStr(lastName) || ObjectUtil.isEmptyStr(emailId) || ObjectUtil.isEmptyStr(password) || ObjectUtil.isEmptyStr(phoneNumber)) {
            errorMsg = this.getString(R.string.all_fields_required);
        } else if (!GlobalUtil.isValidEmail(emailId)) {
            errorMsg = this.getString(R.string.valid_email);
        } else if (!isGpsEnabled(ActivitySignUp.this, ActivitySignUp.this)) {
            return false;
        }

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
