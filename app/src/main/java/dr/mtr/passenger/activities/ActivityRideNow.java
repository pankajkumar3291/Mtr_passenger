package dr.mtr.passenger.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttMessageDeliveryCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dr.amalbit.trail.MapAnimator;
import dr.mtr.passenger.R;
import dr.mtr.passenger.application.ApplicationHelper;
import dr.mtr.passenger.components.RippleView;
import dr.mtr.passenger.components.SessionSecuredPreferences;
import dr.mtr.passenger.dialogs.DriverIsWaitingDialog;
import dr.mtr.passenger.dialogs.GlobalProgressDialog;
import dr.mtr.passenger.enums.TripStatus;
import dr.mtr.passenger.eventbus.DriverLocationEvent;
import dr.mtr.passenger.fragments.RatingDriverFragment;
import dr.mtr.passenger.model.activeTrip.EOActiveTripData;
import dr.mtr.passenger.model.activeTrip.EOActiveTripRequest;
import dr.mtr.passenger.model.activeTrip.EOActiveTripResponse;
import dr.mtr.passenger.model.cancelTrip.EOCancelTripRequest;
import dr.mtr.passenger.model.cancelTrip.EOCancelTripResponse;
import dr.mtr.passenger.model.cancelTrip.TripCancellation;
import dr.mtr.passenger.model.direction.GoogleDirectionResponse;
import dr.mtr.passenger.model.direction.Route;
import dr.mtr.passenger.model.direction.RouteDecode;
import dr.mtr.passenger.model.direction.Step;
import dr.mtr.passenger.model.fare.EOFareRequest;
import dr.mtr.passenger.model.fare.EOFareResponse;
import dr.mtr.passenger.model.nearByDriver.EONearByDriverRequest;
import dr.mtr.passenger.model.newTrip.EONewTripRequest;
import dr.mtr.passenger.model.newTrip.EONewTripResponse;
import dr.mtr.passenger.model.rating.EORatingRequest;
import dr.mtr.passenger.mqtt.MQTTServiceClass;
import dr.mtr.passenger.networking.APIClient;
import dr.mtr.passenger.radar.MapRadar;
import dr.mtr.passenger.utils.GlobalUtil;
import dr.mtr.passenger.utils.LocalizationHelper;
import dr.mtr.passenger.utils.MapUtils;
import dr.mtr.passenger.utils.ObjectUtil;
import dr.mtr.passenger.utils.RxUtils;
import dr.mtr.passenger.utils.UIUtil;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.maps.model.JointType.ROUND;
import static dr.mtr.passenger.enums.TripStatus.RIDER_ENTERING_DETAILS_AND_NOT_PRESSED_DONE;
import static dr.mtr.passenger.utils.Constants.DISTANCE_GOOGLE_API_KEY;
import static dr.mtr.passenger.utils.Constants.DOMAIN_ID;
import static dr.mtr.passenger.utils.Constants.DOMAIN_ID_Value;
import static dr.mtr.passenger.utils.Constants.FARE_CALCULATION_API;
import static dr.mtr.passenger.utils.Constants.LOGIN_PREFERENCE;
import static dr.mtr.passenger.utils.Constants.PASSENGER_ALLOW_CREDIT;
import static dr.mtr.passenger.utils.Constants.PASSENGER_INFO_ID;
import static dr.mtr.passenger.utils.Constants.PASSENGER_MOBILE;
import static dr.mtr.passenger.utils.Constants.RESPONSE_OK;
import static dr.mtr.passenger.utils.Constants.RESPONSE_SUCCESS;

public class ActivityRideNow extends BaseActivity implements View.OnClickListener, MapUtils, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, RxUtils {

    private final int REQUEST_CODE = 101;

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String domainId;
    private String passengerId;
    private String phoneNumber;
    private String allowCredit;

    private TripStatus mCurrentTripStatus = RIDER_ENTERING_DETAILS_AND_NOT_PRESSED_DONE;
    private Toolbar toolbar;
    private ConstraintLayout constraintLayout;
    private ImageView iv_back, ivPaymentType, iv_contact_type, ivCarType;
    private CircleImageView ivDriverImage;
    private TextView tvCarType, autoCompleteFromLocation, autoCompleteToLocation, tvEstimatedDistance, tvEstimatedFareToConfirmTrip, tvEstimatedTime, tvTaxiFor, tvContactValue,
            tvPaymentType, tvWaitingTime, tvTaxiType, tvTaxiNumber1, tvTaxiNumber2, tvDriverName, tvDriverRating, tvEstimatedFareCurrentTrip;
    private CoordinatorLayout bookingLayout;
    private LinearLayout layoutConfirmBooking, layout_final_trip_details, layoutTaxiFor, layoutPaymentType, layoutCoupon, layoutCallDriver, layoutCancelRide, layoutMore;
    private Button btnConfirmBooking;
    private BottomSheet sheetForPaymentType;
    private BottomSheet sheetForContactType;
    private BottomSheet sheetForReasons;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;
    private LocationManager locationManager;
    private EONearByDriverRequest nearByDriverRequest;
    private String sourceAddress;
    private String destinationAddress;
    private double sourceLatitude;
    private double sourceLongitude;

    private double destinationLatitude;
    private double destinationLongitude;
    private boolean isRideLater;
    private String reserveTime;
    private EOActiveTripData activeTrip = new EOActiveTripData();
    private List<Polyline> polylineList = new ArrayList<>();
    private List<Marker> markerListOtherThanTaxis = new ArrayList<>();
    private String mapStyle;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BETWEEN_UPDATES = 10000;
    private boolean isGpsEnabled = false;
    private boolean isNetworkEnable = false;
    private boolean canGetLocation = true;
    private Location lastLocation;
    private RippleView rippleView;
    private MapRadar mapRadar;

    private CountDownTimer countDownTimer;
    private String tripStatus;
    private boolean isKillAppForcefully; //this flag is used to check app is killed forcefully by the user
    private MediaPlayer mediaPlayer;
    public static AWSIotMqttManager mqttManager;
    public static AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus mqttClientStatus;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PolylineOptions /*polylineOptions,*/ blackPolylineOptions;
    private Polyline blackPolyline /*, greyPolyLine*/;
    private String paymentType = "";


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalizationHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_now);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!ObjectUtil.isEmpty(this.getIntent().getStringExtra("sourceAddress")) && !ObjectUtil.isEmpty(this.getIntent().getStringExtra("destinationAddress"))
                || !ObjectUtil.isEmpty(this.getIntent().getSerializableExtra("vehicleType")) && !ObjectUtil.isEmpty(this.getIntent().getBooleanExtra("isRideLater", false))
                || !ObjectUtil.isEmpty(this.getIntent().getStringExtra("reserveTime")) || !ObjectUtil.isEmpty(this.getIntent().getStringExtra("mapStyle"))) {

            this.sourceAddress = this.getIntent().getStringExtra("sourceAddress");
            this.destinationAddress = this.getIntent().getStringExtra("destinationAddress");

            this.sourceLatitude = this.getIntent().getDoubleExtra("sourceLatitude", 0.0);
            this.sourceLongitude = this.getIntent().getDoubleExtra("sourceLongitude", 0.0);
            this.destinationLatitude = this.getIntent().getDoubleExtra("destinationLatitude", 0.0);
            this.destinationLongitude = this.getIntent().getDoubleExtra("destinationLongitude", 0.0);
            this.nearByDriverRequest = (EONearByDriverRequest) this.getIntent().getSerializableExtra("vehicleType");

            this.isRideLater = this.getIntent().getBooleanExtra("isRideLater", false);
            this.reserveTime = this.getIntent().getStringExtra("reserveTime");
            this.mapStyle = this.getIntent().getStringExtra("mapStyle");

        }

        this.initView();
        this.setOnClickListener();
        this.dataToView();

        mqttManager = MQTTServiceClass.mqttManager;
        mqttClientStatus = MQTTServiceClass.mqttClientStatus;

        if (!ObjectUtil.isEmpty(this.getIntent().getBooleanExtra("isKillAppForcefully", false)) || !ObjectUtil.isEmpty(this.getIntent().getSerializableExtra("onGoingTripResponse"))) {
            this.isKillAppForcefully = this.getIntent().getBooleanExtra("isKillAppForcefully", false);
            this.activeTrip = (EOActiveTripData) this.getIntent().getSerializableExtra("onGoingTripResponse");

            if (this.isKillAppForcefully) {
                onGoingTrip(this.activeTrip);
            }
        }

    }

    private String returnStatusOfMqtt(String status) {
        String messageFromMQTT = "";
        switch (status) {
            case "2":  // assigned
                messageFromMQTT = "assigned";
                break;
            case "8": // waiting
                messageFromMQTT = "waiting";
                break;
            case "5": // picked
                messageFromMQTT = "picked";
                break;
        }
        return messageFromMQTT;
    }

    private void onGoingTrip(EOActiveTripData eoActiveTripData) {

        setSourceDestinationAddress(isKillAppForcefully); //this is used to set source and dest address when user kill app forcefully
        setDataToFinalScreen(eoActiveTripData);
        //TODO subscribe MQTT from here to fetch driver updated location, while movement driver
        if (ObjectUtil.isNonEmptyStr(eoActiveTripData.getDriverid()))
            subscribeMQTTForDriverLocation(eoActiveTripData.getDriverid());

        MapAnimator.getInstance().stopAndRemovePolyLine();
        if (map != null)
            map.clear();

        String tripStatus = returnStatusOfMqtt(eoActiveTripData.getStatus());

        switch (tripStatus) {
            case "assigned":
            case "waiting":
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layoutConfirmBooking.setVisibility(View.GONE);
                        layout_final_trip_details.setVisibility(View.VISIBLE);
                    }
                });
                //TODO Getting URL to the Google Directions API to draw path

                String urlToDrawPathFromDriverToPickup = getUrl(new LatLng(Double.valueOf(eoActiveTripData.getDriverCurrentLat()), Double.valueOf(eoActiveTripData.getDriverCurrentLng())),
                        new LatLng(Double.valueOf(eoActiveTripData.getPlat()), Double.valueOf(eoActiveTripData.getPlong())));
                drawPathFromDriverToPickup(urlToDrawPathFromDriverToPickup, eoActiveTripData.getDriverid(), tripStatus);
                break;

           /* case "waiting":
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layoutConfirmBooking.setVisibility(View.GONE);
                        layout_final_trip_details.setVisibility(View.VISIBLE);
                    }
                });

                //TODO Getting URL to the Google Directions API to draw path
                String urlToDrawPathFromDriverToPickup1 = getUrl(new LatLng(Double.valueOf(eoActiveTripData.getDriverCurrentLat()), Double.valueOf(eoActiveTripData.getDriverCurrentLng())),
                        new LatLng(Double.valueOf(eoActiveTripData.getPlat()), Double.valueOf(eoActiveTripData.getPlong())));
                drawPathFromDriverToPickup(urlToDrawPathFromDriverToPickup1, eoActiveTripData.getDriverid(), tripStatus);
                break;*/
            case "picked":

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layoutConfirmBooking.setVisibility(View.GONE);
                        layout_final_trip_details.setVisibility(View.VISIBLE);
                    }
                });

                String urlToDrawPathFromPickupToDropOff = getUrl(new LatLng(Double.valueOf(eoActiveTripData.getDriverCurrentLat()), Double.valueOf(eoActiveTripData.getDriverCurrentLng())),
                        new LatLng(Double.valueOf(eoActiveTripData.getDlat()), Double.valueOf(eoActiveTripData.getDlong())));
                drawPathFromPickupToDropOff(urlToDrawPathFromPickupToDropOff, eoActiveTripData.getDriverid());
                break;
        }
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(ActivityRideNow.this);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.domainId = String.valueOf(loginPreferences.getInt(DOMAIN_ID, 0));
        this.passengerId = loginPreferences.getString(PASSENGER_INFO_ID, "");
        this.phoneNumber = loginPreferences.getString(PASSENGER_MOBILE, "");
        this.allowCredit = loginPreferences.getString(PASSENGER_ALLOW_CREDIT, "");

        this.toolbar = this.findViewById(R.id.toolbar);
        this.iv_back = this.findViewById(R.id.iv_back);
        this.tvCarType = this.findViewById(R.id.tvCarType);
        this.autoCompleteFromLocation = this.findViewById(R.id.autoCompleteFromLocation);
        this.autoCompleteToLocation = this.findViewById(R.id.autoCompleteToLocation);
        this.bookingLayout = this.findViewById(R.id.bookingLayout);
        this.layoutConfirmBooking = this.findViewById(R.id.layoutConfirmBooking);
        this.layout_final_trip_details = this.findViewById(R.id.layout_final_trip_details);
        this.tvEstimatedDistance = this.findViewById(R.id.tvEstimatedDistance);
        this.tvEstimatedFareToConfirmTrip = this.findViewById(R.id.tvEstimatedFareToConfirmTrip);
        this.tvEstimatedTime = this.findViewById(R.id.tvEstimatedTime);
        this.layoutTaxiFor = this.findViewById(R.id.layoutTaxiFor);
        this.tvTaxiFor = this.findViewById(R.id.tvTaxiFor);
        this.layoutPaymentType = this.findViewById(R.id.layoutPaymentType);
        this.tvPaymentType = this.findViewById(R.id.tvPaymentType);
        this.layoutCoupon = this.findViewById(R.id.layoutCoupon);
        this.btnConfirmBooking = this.findViewById(R.id.btnConfirmBooking);
        this.ivPaymentType = this.findViewById(R.id.ivPaymentType);
        this.iv_contact_type = this.findViewById(R.id.iv_contact_type);
        this.tvContactValue = this.findViewById(R.id.tvContactValue);
        this.tvWaitingTime = this.findViewById(R.id.tvWaitingTime);
        this.tvTaxiType = this.findViewById(R.id.tvTaxiType);
        this.ivCarType = this.findViewById(R.id.ivCarType);

        this.tvTaxiNumber1 = this.findViewById(R.id.tvTaxiNumber1);
        this.tvTaxiNumber2 = this.findViewById(R.id.tvTaxiNumber2);
        this.tvDriverName = this.findViewById(R.id.tvDriverName);
        this.tvDriverRating = this.findViewById(R.id.tvDriverRating);
        this.ivDriverImage = this.findViewById(R.id.ivDriverImage);
        this.tvEstimatedFareCurrentTrip = this.findViewById(R.id.tvEstimatedFareCurrentTrip);
        this.layoutCallDriver = this.findViewById(R.id.layoutCallDriver);
        this.layoutCancelRide = this.findViewById(R.id.layoutCancelRide);
        this.layoutMore = this.findViewById(R.id.layoutMore);
        this.rippleView = this.findViewById(R.id.rippleView);
        this.constraintLayout = this.findViewById(R.id.constraintLayout);

        if (allowCredit.equalsIgnoreCase("0")) {
            tvPaymentType.setText(R.string.cash);
            this.layoutPaymentType.setOnClickListener(null);
        } else {
            this.layoutPaymentType.setOnClickListener(this);
        }

        //this.mqttService = new MQTTService(this, passengerId, ActivityRideNow.this);

        //TODO load map by default, when open this activity
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.googleMap);
        Objects.requireNonNull(mapFragment).getMapAsync(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        this.buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        this.googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void setOnClickListener() {
        this.iv_back.setOnClickListener(this);
        //this.layoutPaymentType.setOnClickListener(this);
        this.layoutTaxiFor.setOnClickListener(this);
        this.layoutCoupon.setOnClickListener(this);
        this.btnConfirmBooking.setOnClickListener(this);
        this.layoutCallDriver.setOnClickListener(this);
        this.layoutCancelRide.setOnClickListener(this);
        this.layoutMore.setOnClickListener(this);
    }

    private void dataToView() {
        if (ObjectUtil.isNonEmptyStr(sourceAddress))
            this.autoCompleteFromLocation.setText(sourceAddress);
        if (ObjectUtil.isNonEmptyStr(destinationAddress))
            this.autoCompleteToLocation.setText(destinationAddress);
        if (!ObjectUtil.isEmpty(nearByDriverRequest))
            this.tvCarType.setText(nearByDriverRequest.getVtype());
        //TODO from here by default show myself number
        tvTaxiFor.setText(R.string.my_self);
        if (ObjectUtil.isNonEmptyStr(phoneNumber))
            tvContactValue.setText(phoneNumber);
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();

        Intent intent = new Intent(ApplicationHelper.application(), MQTTServiceClass.class);
        if (!isServiceRunning(MQTTServiceClass.class, this)) {
            startService(intent);
        }
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
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();

        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (map != null)
            map.clear();

        if (countDownTimer != null)
            countDownTimer.cancel();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //TODO disable back button from here
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //Snackbar.make(constraintLayout, "You can not back, trip is running.", Snackbar.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                //TODO when user back press then play sound
                mediaPlayer = MediaPlayer.create(this, R.raw.select);
                if (mediaPlayer != null)
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // TODO Auto-generated method stub
                            mp.release();
                        }
                    });

                if (mediaPlayer != null)
                    mediaPlayer.start();

                if (countDownTimer != null)
                    countDownTimer.cancel();

                this.finish();
                break;
            case R.id.layoutPaymentType:
                this.paymentType();
                break;
            case R.id.layoutTaxiFor:
                this.contactType();
                break;
            case R.id.layoutCoupon:
                this.openCouponDialog();
                break;
            case R.id.btnConfirmBooking:
                mediaPlayer = MediaPlayer.create(this, R.raw.button);
                if (mediaPlayer != null)
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // TODO Auto-generated method stub
                            mp.release();
                        }
                    });

                if (mediaPlayer != null)
                    mediaPlayer.start();

                this.booNewTrip();
                break;
            case R.id.layoutCallDriver:
                if (!TextUtils.isEmpty(activeTrip.getDriverPhoneNumber())) {
                    Uri number = Uri.parse("tel:" + activeTrip.getDriverPhoneNumber());
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                } else {
                    Toast.makeText(ActivityRideNow.this, getString(R.string.Contact_Number_Not_Available), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layoutCancelRide:
                this.cancelOnGoingTrip();
                break;
            case R.id.layoutMore:
                this.openMoreDialog();
                break;
        }
    }

    private void booNewTrip() {
        if (!GlobalUtil.isNetworkAvailable(ActivityRideNow.this)) {
            UIUtil.showNetworkDialog(ActivityRideNow.this);
            return;
        }


        EONewTripRequest newTripRequest = new EONewTripRequest();
        newTripRequest.setVehicle(nearByDriverRequest.getVtype());
        newTripRequest.setDroplng(String.valueOf(destinationLongitude));
        newTripRequest.setDroplat(String.valueOf(destinationLatitude));
        newTripRequest.setDropaddress(destinationAddress);
        newTripRequest.setPicklng(String.valueOf(sourceLongitude));
        newTripRequest.setPicklat(String.valueOf(sourceLatitude));
        newTripRequest.setPickaddress(sourceAddress);
        newTripRequest.setUserid(passengerId);
        newTripRequest.setContact_number(phoneNumber);
        newTripRequest.setIsreserved(isRideLater ? "1" : "0");
        newTripRequest.setReservationtime(isRideLater ? reserveTime : "");
        newTripRequest.setDomainid(String.valueOf(DOMAIN_ID_Value));

        //TODO from here check payment type for confirm booking trip request
        if (allowCredit.equalsIgnoreCase("0"))
            newTripRequest.setPaymenttype("cash");
        else {
            if (!ObjectUtil.isNonEmptyStr(paymentType)) {
                Toast.makeText(this, getString(R.string.plese_select_pyment_type), Toast.LENGTH_SHORT).show();
                return;
            } else {
                newTripRequest.setPaymenttype(paymentType);
            }
        }

        progress.showProgressBar();
        if (isRideLater) {
            apiInterface.createNewTrip(newTripRequest).enqueue(new Callback<EONewTripResponse>() {
                @Override
                public void onResponse(Call<EONewTripResponse> call, Response<EONewTripResponse> response) {
                    progress.hideProgressBar();

                    if (!ObjectUtil.isEmpty(response.body())) {
                        EONewTripResponse tripResponse = response.body();
                        if (!ObjectUtil.isEmpty(tripResponse)) {
                            if (tripResponse.getStatus().equalsIgnoreCase(RESPONSE_SUCCESS)) {
                                //TODO from here show Driver waiting dialog and finish this screen and back to previous screen
                                DriverIsWaitingDialog dialog = new DriverIsWaitingDialog(ActivityRideNow.this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
                                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        //activeTrip.setDriverid("");
                                        //activeTrip.setTripid("");
                                        mCurrentTripStatus = RIDER_ENTERING_DETAILS_AND_NOT_PRESSED_DONE;
                                        ActivityRideNow.this.finish();
                                    }
                                });
                                dialog.show();
                            } else {
                                Toast.makeText(ActivityRideNow.this, "" + tripResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EONewTripResponse> call, Throwable t) {

                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityRideNow.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();

                        //TODO Getting URL to the Google Directions API , to change route in original path after 30 seconds
                        if (sourceLatitude != 0.0 && sourceLongitude != 0.0 && destinationLatitude != 0.0 && destinationLongitude != 0.0) {
                            if (sourceLatitude == destinationLatitude || sourceLongitude == destinationLongitude) {
                                showZeroMapRouteDialog();
                                isShowZeroMapDialog = true;
                            } else {
                                if (map != null)
                                    map.clear();
                                String urlToDrawPath = getUrl(new LatLng(sourceLatitude, sourceLongitude), new LatLng(destinationLatitude, destinationLongitude));
                                drawPathInRideNowAndRideLater(urlToDrawPath);
                            }
                        }
                    }
                }
            });
        } else {

            LatLng pickup = new LatLng(sourceLatitude, sourceLongitude);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(pickup, 16.0f));
            //animation will start after animating to pick up location
            if (mapRadar == null) {
                mapRadar = new MapRadar(map, pickup, this);
                //mapRadar.withClockWiseAnticlockwise(true);
                mapRadar.withDistance(400);
                mapRadar.withClockwiseAnticlockwiseDuration(1);
                //mapRadar.withOuterCircleFillColor(Color.parseColor("#12000000"));
                mapRadar.withOuterCircleStrokeColor(Color.parseColor("#0000AED9"));
                //mapRadar.withRadarColors(Color.parseColor("#00000000"), Color.parseColor("#ff000000"));  //starts from transparent to fuly black
                mapRadar.withRadarColors(Color.parseColor("#0000AED9"), Color.parseColor("#ff00AED9"));  //starts from transparent to fuly black
                //mapRadar.withOuterCircleStrokewidth(7);
                //mapRadar.withRadarSpeed(5);
                mapRadar.withOuterCircleTransparency(1.0f);
                mapRadar.withRadarTransparency(0.5f);
            } else {
                mapRadar.withLatLng(pickup);
            }


            apiInterface.createNewTrip(newTripRequest).enqueue(new Callback<EONewTripResponse>() {
                @Override
                public void onResponse(Call<EONewTripResponse> call, Response<EONewTripResponse> response) {
                    progress.hideProgressBar();

                    if (!ObjectUtil.isEmpty(response.body())) {
                        EONewTripResponse tripResponse = response.body();
                        if (!ObjectUtil.isEmpty(tripResponse)) {
                            if (tripResponse.getStatus().equalsIgnoreCase(RESPONSE_SUCCESS)) {
                                //activeTrip.setTripid(tripResponse.getTripid());

                                //rippleView.setVisibility(View.VISIBLE);
                                //rippleView.startRippleAnimation();
                                startRadarAnimation();
                                runOnUiThread(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      layoutConfirmBooking.setVisibility(View.GONE);
                                                  }
                                              }
                                );

                                checkDataFromMQttInCertainTimeInterval();

                                subscribeToMQtt(tripResponse.getTripid());

                            } else {
                                Toast.makeText(ActivityRideNow.this, "" + tripResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                }

                @Override
                public void onFailure(Call<EONewTripResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(ActivityRideNow.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();

                        //TODO Getting URL to the Google Directions API , to change route in original path after 30 seconds
                        if (sourceLatitude != 0.0 && sourceLongitude != 0.0 && destinationLatitude != 0.0 && destinationLongitude != 0.0) {
                            if (sourceLatitude == destinationLatitude || sourceLongitude == destinationLongitude) {
                                showZeroMapRouteDialog();
                                isShowZeroMapDialog = true;
                            } else {
                                if (map != null)
                                    map.clear();
                                String urlToDrawPath = getUrl(new LatLng(sourceLatitude, sourceLongitude), new LatLng(destinationLatitude, destinationLongitude));
                                drawPathInRideNowAndRideLater(urlToDrawPath);
                            }
                        }
                    }
                }
            });
        }


    }

    private void startRadarAnimation() {
        if (mapRadar != null) {
            mapRadar.startRadarAnimation();
        }
    }

    private void stopRadarAnimation() {
        if (mapRadar != null)
            if (mapRadar.isAnimationRunning()) {
                mapRadar.stopRadarAnimation();
            }
    }

    //TODO trip id subscribe to MQTT and get response messge from MQTT
    private void subscribeToMQtt(String activeTripId) {
        mqttManager = MQTTServiceClass.mqttManager;
        mqttClientStatus = MQTTServiceClass.mqttClientStatus;

        if (!ObjectUtil.isEmpty(mqttManager) && !ObjectUtil.isEmpty(mqttClientStatus)) {
            if (mqttClientStatus.equals(AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected)) {
                if (ObjectUtil.isNonEmptyStr(activeTripId)) {
                    mqttManager.subscribeToTopic(domainId + "/customerupdates/" + activeTripId, AWSIotMqttQos.QOS0, new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(String topic, byte[] data) {
                            try {
                                String message = new String(data, "UTF-8");
                                System.out.println("MQTT response : " + message);
                                //TODO from here, mqtt message status like : assigned, waiting, picked, dropped will decide the next process
                                if (ObjectUtil.isNonEmptyStr(message)) {
                                    if (countDownTimer != null)
                                        countDownTimer.cancel();
                                    currentStatusOfMqtt(message);
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } else {
                Log.d("onNext_MayBeConnected", "start_service");
                start_service();
            }
        }
    }

    public void start_service() {
        Intent intent = new Intent(ActivityRideNow.this, MQTTServiceClass.class);
        if (!isMyServiceRunning()) {
            startService(intent);
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : Objects.requireNonNull(manager).getRunningServices(Integer.MAX_VALUE)) {
            if ("dr.mtr.passenger.mqtt.MQTTServiceClass".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void checkDataFromMQttInCertainTimeInterval() {
        this.countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                System.out.println("timer : " + seconds);
                iv_back.setEnabled(false); //disable back button for 30s
            }

            @Override
            public void onFinish() {
                //layoutConfirmBooking.setVisibility(View.VISIBLE);
                iv_back.setEnabled(true); //again enable back button after 30s

//                rippleView.stopRippleAnimation();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //rippleView.stopRippleAnimation();
                        //rippleView.setVisibility(View.GONE);
                        layoutConfirmBooking.setVisibility(View.VISIBLE);
                        stopRadarAnimation();
                    }
                });

                Toast.makeText(ActivityRideNow.this, getResources().getString(R.string.drivers_are_another_route), Toast.LENGTH_SHORT).show();

                //TODO Getting URL to the Google Directions API , to change route in original path after 30 seconds
                if (sourceLatitude != 0.0 && sourceLongitude != 0.0 && destinationLatitude != 0.0 && destinationLongitude != 0.0) {
                    if (sourceLatitude == destinationLatitude || sourceLongitude == destinationLongitude) {
                        showZeroMapRouteDialog();
                        isShowZeroMapDialog = true;
                    } else {
                        if (map != null)
                            map.clear();
                        String urlToDrawPath = getUrl(new LatLng(sourceLatitude, sourceLongitude), new LatLng(destinationLatitude, destinationLongitude));
                        drawPathInRideNowAndRideLater(urlToDrawPath);
                    }
                }
            }
        }.start();
    }


    private void currentStatusOfMqtt(String messageFromMQTT) {

        //TODO stop radar if animating
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //rippleView.stopRippleAnimation();
                //rippleView.setVisibility(View.GONE);
                stopRadarAnimation();
            }
        });

        iv_back.setEnabled(false); //disable back button for all status

        switch (messageFromMQTT) {
            case "assigned":

                mediaPlayer = MediaPlayer.create(this, R.raw.cashreg);
                if (mediaPlayer != null)
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // TODO Auto-generated method stub
                            mp.release();
                        }
                    });
                if (mediaPlayer != null)
                    mediaPlayer.start();

                mCurrentTripStatus = TripStatus.NEARBY_TAXI_FOUND_DRIVER_COMING_TOWARDS_RIDER;
                this.getActiveTripApi("assigned");
                break;
            case "waiting":

                mediaPlayer = MediaPlayer.create(this, R.raw.beep);
                if (mediaPlayer != null)
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // TODO Auto-generated method stub
                            mp.release();
                        }
                    });
                if (mediaPlayer != null)
                    mediaPlayer.start();

                mCurrentTripStatus = TripStatus.DRIVER_ARRIVED_WAITING_FOR_CUSTOMER;
                this.getActiveTripApi("waiting");
                //tvWaitingTime.setText(getString(R.string.driver_is_waiting_for_you));
                break;
            case "picked":

                mediaPlayer = MediaPlayer.create(this, R.raw.beep);
                if (mediaPlayer != null)
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // TODO Auto-generated method stub
                            mp.release();
                        }
                    });

                if (mediaPlayer != null)
                    mediaPlayer.start();

                mCurrentTripStatus = TripStatus.RIDER_PICKED_UP_TRIP_STARTED;
                this.getActiveTripApi("picked");
                break;
            case "dropped":

                mediaPlayer = MediaPlayer.create(this, R.raw.beep);
                if (mediaPlayer != null)
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // TODO Auto-generated method stub
                            mp.release();
                        }
                    });

                if (mediaPlayer != null)
                    mediaPlayer.start();

                mCurrentTripStatus = TripStatus.TRIP_COMPLTED_DROPED_OFF;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layoutConfirmBooking.setVisibility(View.GONE);
                        bookingLayout.setVisibility(View.VISIBLE);
                    }
                });

                EORatingRequest giveRatingRequest = new EORatingRequest();
                if (!ObjectUtil.isEmpty(activeTrip)) {
                    giveRatingRequest.setTripid(Integer.parseInt(activeTrip.getTripid()));
                    giveRatingRequest.setDriverid(Integer.parseInt(activeTrip.getDriverid()));
                    giveRatingRequest.setPid(Integer.parseInt(passengerId));
                    giveRatingRequest.setDriverName(activeTrip.getDriverName());
                    giveRatingRequest.setDriverPhoto(activeTrip.getDriverImage());
                }
                RatingDriverFragment orderRideReviewFragment = RatingDriverFragment.newInstance(giveRatingRequest);
                orderRideReviewFragment.setCancelable(false);
                orderRideReviewFragment.show(getSupportFragmentManager(), RatingDriverFragment.class.getSimpleName());
                mCurrentTripStatus = RIDER_ENTERING_DETAILS_AND_NOT_PRESSED_DONE;
                break;
        }
    }

    private void getActiveTripApi(String status) {
        if (!GlobalUtil.isNetworkAvailable(ActivityRideNow.this)) {
            UIUtil.showNetworkDialog(ActivityRideNow.this);
            return;
        }

        EOActiveTripRequest activeTripRequest = new EOActiveTripRequest();
        activeTripRequest.setPid(passengerId);

        if (!ObjectUtil.isEmpty(activeTripRequest)) {
            apiInterface.getActiveTrip(activeTripRequest).enqueue(new Callback<EOActiveTripResponse>() {
                @Override
                public void onResponse(Call<EOActiveTripResponse> call, Response<EOActiveTripResponse> response) {
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOActiveTripResponse activeTripResponse = response.body();
                        if (!ObjectUtil.isEmpty(activeTripResponse)) {
                            if (activeTripResponse.getStatus().equalsIgnoreCase(RESPONSE_SUCCESS)) {
                                activeTrip = activeTripResponse.getData().get(0);

                                switch (status) {
                                    case "assigned":
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                layoutConfirmBooking.setVisibility(View.GONE);
                                                layout_final_trip_details.setVisibility(View.VISIBLE);
                                            }
                                        });

                                        setDataToFinalScreen(activeTripResponse.getData().get(0));
                                        //TODO Getting URL to the Google Directions API to draw path
                                        String urlToDrawPathFromDriverToPickup = getUrl(new LatLng(Double.valueOf(activeTripResponse.getData().get(0).getDriverCurrentLat()), Double.valueOf(activeTripResponse.getData().get(0).getDriverCurrentLng())),
                                                new LatLng(Double.valueOf(activeTripResponse.getData().get(0).getPlat()), Double.valueOf(activeTripResponse.getData().get(0).getPlong())));
                                        MapAnimator.getInstance().stopAndRemovePolyLine();
                                        if (map != null)
                                            map.clear();
                                        drawPathFromDriverToPickup(urlToDrawPathFromDriverToPickup, activeTripResponse.getData().get(0).getDriverid(), status);

                                        //TODO subscribe MQTT from here to fetch driver updated location, while movement driver
                                        if (ObjectUtil.isNonEmptyStr(activeTripResponse.getData().get(0).getDriverid()))
                                            subscribeMQTTForDriverLocation(activeTripResponse.getData().get(0).getDriverid());
                                        break;

                                    case "waiting":
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                layoutConfirmBooking.setVisibility(View.GONE);
                                                layout_final_trip_details.setVisibility(View.VISIBLE);
                                            }
                                        });
                                        //setSourceDestinationAddress(isKillAppForcefully); //this is used to set source and dest address when user kill app forcefully
                                        setDataToFinalScreen(activeTripResponse.getData().get(0));
                                        //TODO Getting URL to the Google Directions API to draw path
                                        String urlToDrawPathFromDriverToPickup1 = getUrl(new LatLng(Double.valueOf(activeTripResponse.getData().get(0).getDriverCurrentLat()), Double.valueOf(activeTripResponse.getData().get(0).getDriverCurrentLng())),
                                                new LatLng(Double.valueOf(activeTripResponse.getData().get(0).getPlat()), Double.valueOf(activeTripResponse.getData().get(0).getPlong())));
                                        MapAnimator.getInstance().stopAndRemovePolyLine();
                                        if (map != null)
                                            map.clear();
                                        drawPathFromDriverToPickup(urlToDrawPathFromDriverToPickup1, activeTripResponse.getData().get(0).getDriverid(), status);
                                        //TODO subscribe MQTT from here to fetch driver updated location, while movement driver
                                        //if (ObjectUtil.isNonEmptyStr(activeTripResponse.getData().get(0).getDriverid()))
                                        // subscribeMQTTForDriverLocation(activeTripResponse.getData().get(0).getDriverid());
                                        break;

                                    case "picked":

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                layoutConfirmBooking.setVisibility(View.GONE);
                                                layout_final_trip_details.setVisibility(View.VISIBLE);
                                            }
                                        });

                                        setDataToFinalScreen(activeTripResponse.getData().get(0));

                                        String urlToDrawPathFromPickupToDropOff = getUrl(new LatLng(Double.valueOf(activeTripResponse.getData().get(0).getDriverCurrentLat()), Double.valueOf(activeTripResponse.getData().get(0).getDriverCurrentLng())),
                                                new LatLng(Double.valueOf(activeTripResponse.getData().get(0).getDlat()), Double.valueOf(activeTripResponse.getData().get(0).getDlong())));
                                        MapAnimator.getInstance().stopAndRemovePolyLine();
                                        if (map != null)
                                            map.clear();
                                        drawPathFromPickupToDropOff(urlToDrawPathFromPickupToDropOff, activeTripResponse.getData().get(0).getDriverid());
                                        //TODO subscribe MQTT from here to fetch driver updated location, while movement driver
//                                        if (ObjectUtil.isNonEmptyStr(activeTripResponse.getData().get(0).getDriverid()))
//                                            subscribeMQTTForDriverLocation(activeTripResponse.getData().get(0).getDriverid());
                                        break;
                                }


                            } else {
                                Toast.makeText(ActivityRideNow.this, "" + activeTripResponse.getStatus(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOActiveTripResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        Toast.makeText(ActivityRideNow.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }

    }

    private void setSourceDestinationAddress(boolean isKillAppForcefully) {
        if (isKillAppForcefully) {
            if (!ObjectUtil.isEmpty(activeTrip)) {
                autoCompleteFromLocation.setText(this.getAddressFromLatLong(Double.valueOf(activeTrip.getPlat()), Double.valueOf(activeTrip.getPlong())));
                autoCompleteToLocation.setText(this.getAddressFromLatLong(Double.valueOf(activeTrip.getDlat()), Double.valueOf(activeTrip.getDlong())));
            }
        }
    }

    //TODO from here call the google direction api via retrofit library  assigned/waiting state google response
    private void drawPathFromDriverToPickup(String url, String driverId, String tripStatus) {
        apiInterface.getDirectionPath(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<GoogleDirectionResponse>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(GoogleDirectionResponse googleDirectionResponse) {

                        if (!ObjectUtil.isEmpty(googleDirectionResponse)) {
                            if (googleDirectionResponse.getStatus().equalsIgnoreCase(RESPONSE_OK)) {

                                if (map != null)
                                    map.clear();
                                //setRouteFromDriverToPickUp(ActivityRideNow.this, googleDirectionResponse.getRoutes(), map, polylineList, markerListOtherThanTaxis);
                                drawPolyLineFromDriverToPickUp(googleDirectionResponse.getRoutes(), driverId);

                                if (tripStatus.equals("assigned")) {
                                    if (ObjectUtil.isNonEmptyStr(googleDirectionResponse.getRoutes().get(0).getLegs().get(0).getDuration().getText())) {
                                        tvWaitingTime.setText(MessageFormat.format(getString(R.string.Pick_Up_in), googleDirectionResponse.getRoutes().get(0).getLegs().get(0).getDuration().getText()));
                                    }
                                } else
                                    tvWaitingTime.setText(getString(R.string.driver_is_waiting_for_you));

                            } else {
                                //TODO in case ZERO_RESULTS show dialog
                                showZeroMapRouteDialog();
                                isShowZeroMapDialog = true;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (t.getMessage() != null) {
                            Toast.makeText(ActivityRideNow.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    //TODO from here call the google direction api via retrofit library picked state
    private void drawPathFromPickupToDropOff(String url, String driverId) {

        apiInterface.getDirectionPath(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<GoogleDirectionResponse>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(GoogleDirectionResponse googleDirectionResponse) {

                        if (!ObjectUtil.isEmpty(googleDirectionResponse)) {
                            if (googleDirectionResponse.getStatus().equalsIgnoreCase(RESPONSE_OK)) {

                                //setRouteFromPickUpToDropOff(ActivityRideNow.this, googleDirectionResponse.getRoutes(), map, polylineList, markerListOtherThanTaxis);
                                if (map != null)
                                    map.clear();
                                drawPolylineFromPickUpToDropOff(googleDirectionResponse.getRoutes(), driverId);

                                if (ObjectUtil.isNonEmptyStr(googleDirectionResponse.getRoutes().get(0).getLegs().get(0).getDuration().getText())) {
                                    tvWaitingTime.setText(MessageFormat.format(getString(R.string.Drop_off_in), googleDirectionResponse.getRoutes().get(0).getLegs().get(0).getDuration().getText()));
                                }

                            } else {
                                //TODO in case ZERO_RESULTS show dialog
                                showZeroMapRouteDialog();
                                isShowZeroMapDialog = true;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (t.getMessage() != null) {
                            Toast.makeText(ActivityRideNow.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private Marker startMarker;
    private ArrayList<DriverObject> allCordinatesList = new ArrayList<>();
    private ArrayList<LatLng> lastCordinatesMarker = new ArrayList<>();

    private void setRouteAfterRideNowIfRouteFound(List<Route> routelistForPolyLine) {
        MarkerOptions markerOptionsStart = new MarkerOptions().icon(bitmapDescriptorFromVector(this, R.drawable.picloc1));
        MarkerOptions markerOptionsEnd = new MarkerOptions().icon(bitmapDescriptorFromVector(this, R.drawable.droploc1));
        ArrayList<LatLng> routelist = new ArrayList<>();
        if (routelistForPolyLine.size() > 0) {
            ArrayList<LatLng> decodelist;
            Route routeA = routelistForPolyLine.get(0);
            if (routeA.getLegs().size() > 0) {
                List<Step> steps = routeA.getLegs().get(0).getSteps();
                Step step;
                dr.mtr.passenger.model.direction.Location location;
                String polyline;
                for (int i = 0; i < steps.size(); i++) {
                    step = steps.get(i);
                    location = step.getStartLocation();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                    polyline = step.getPolyline().getPoints();
                    decodelist = RouteDecode.decodePoly(polyline);
                    routelist.addAll(decodelist);
                    location = step.getEndLocation();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                }
            }
        }
        if (routelist.size() > 0) {
            PolylineOptions rectLine = new PolylineOptions().width(8).color(ContextCompat.getColor(this, R.color.colorPrimary));
            for (int i = 0; i < routelist.size(); i++) {
                rectLine.add(routelist.get(i));
            }
            markerOptionsStart.position(routelist.get(0));
            markerOptionsEnd.position(routelist.get(routelist.size() - 1));
            markerListOtherThanTaxis.add(map.addMarker(markerOptionsStart));
            markerListOtherThanTaxis.add(map.addMarker(markerOptionsEnd));
            if (routelist.isEmpty()) return;
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (LatLng latLngPoint : routelist)
                boundsBuilder.include(latLngPoint);
            int routePadding = 50;
            LatLngBounds latLngBounds = boundsBuilder.build();
            map.setPadding(routePadding, routePadding, routePadding, routePadding);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
            //MapAnimator.getInstance().animateRoute(map, routelist, polylineList);

            ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
            polylineAnimator.setDuration(1000);
            polylineAnimator.setInterpolator(new LinearInterpolator());
            polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    blackPolylineOptions = new PolylineOptions();
                    blackPolylineOptions.width(10);
                    blackPolylineOptions.color(Color.BLACK);
                    blackPolylineOptions.startCap(new SquareCap());
                    blackPolylineOptions.endCap(new SquareCap());
                    blackPolylineOptions.jointType(ROUND);
                    blackPolylineOptions.addAll(routelist); // add here to all LatLng list
                    if (blackPolyline != null)
                        blackPolyline.remove();
                    blackPolyline = map.addPolyline(blackPolylineOptions);

                }
            });
            polylineAnimator.start();

        }
    }

    private ArrayList<LatLng> routelist;

    private void drawPolyLineFromDriverToPickUp(List<Route> routelistForPolyLine, String driverId) {
        MarkerOptions markerOptionsStart = new MarkerOptions().icon(bitmapDescriptorFromVector(this, R.drawable.uber_car));
        MarkerOptions markerOptionsEnd = new MarkerOptions().icon(bitmapDescriptorFromVector(this, R.drawable.picloc1));

        if (routelist == null)
            routelist = new ArrayList<>();
        else
            routelist.clear();

        if (routelistForPolyLine.size() > 0) {
            ArrayList<LatLng> decodelist;
            Route routeA = routelistForPolyLine.get(0);
            if (routeA.getLegs().size() > 0) {
                List<Step> steps = routeA.getLegs().get(0).getSteps();
                Step step;
                dr.mtr.passenger.model.direction.Location location;
                String polyline;
                for (int i = 0; i < steps.size(); i++) {
                    step = steps.get(i);
                    location = step.getStartLocation();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                    polyline = step.getPolyline().getPoints();
                    decodelist = RouteDecode.decodePoly(polyline);
                    routelist.addAll(decodelist);
                    location = step.getEndLocation();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                }
            }
        }

        if (routelist.size() > 0) {
            PolylineOptions rectLine = new PolylineOptions().width(8).color(ContextCompat.getColor(this, R.color.colorPrimary));
            for (int i = 0; i < routelist.size(); i++) {
                rectLine.add(routelist.get(i));
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
                    polylineAnimator.setDuration(1000);
                    polylineAnimator.setInterpolator(new LinearInterpolator());
                    polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            blackPolylineOptions = new PolylineOptions();
                            blackPolylineOptions.width(10);
                            blackPolylineOptions.color(Color.BLACK);
                            blackPolylineOptions.startCap(new SquareCap());
                            blackPolylineOptions.endCap(new SquareCap());
                            blackPolylineOptions.jointType(ROUND);
                            blackPolylineOptions.addAll(routelist);

                            if (blackPolyline != null)
                                blackPolyline.remove();
                            blackPolyline = map.addPolyline(blackPolylineOptions);

                        }
                    });
                    polylineAnimator.start();

                    markerOptionsStart.position(routelist.get(0));
                    markerOptionsEnd.position(routelist.get(routelist.size() - 1));
                    startMarker = map.addMarker(markerOptionsStart.flat(true));
                }
            });
            Marker endMarker = map.addMarker(markerOptionsEnd);
            if (routelist.isEmpty()) return;
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (LatLng latLngPoint : routelist)
                boundsBuilder.include(latLngPoint);
            int routePadding = 50;
            LatLngBounds latLngBounds = boundsBuilder.build();
            map.setPadding(routePadding, routePadding, routePadding, routePadding);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
        }

        if (!ObjectUtil.isEmpty(allCordinatesList))
            allCordinatesList.clear();

        allCordinatesList.add(new DriverObject(routelist.get(0).latitude, routelist.get(0).longitude, driverId));

        if (!ObjectUtil.isEmpty(lastCordinatesMarker))
            lastCordinatesMarker.clear();
        lastCordinatesMarker.add(routelist.get(routelist.size() - 1));

    }

    //TODO draw path for drop off location
    private void drawPolylineFromPickUpToDropOff(List<Route> routelistForPolyLine, String driverId) {
        MarkerOptions markerOptionsStart = new MarkerOptions().icon(bitmapDescriptorFromVector(this, R.drawable.uber_car));
        MarkerOptions markerOptionsEnd = new MarkerOptions().icon(bitmapDescriptorFromVector(this, R.drawable.droploc1));

        if (routelist == null)
            routelist = new ArrayList<>();
        else
            routelist.clear();

        if (routelistForPolyLine.size() > 0) {
            ArrayList<LatLng> decodelist;
            Route routeA = routelistForPolyLine.get(0);
            if (routeA.getLegs().size() > 0) {
                List<Step> steps = routeA.getLegs().get(0).getSteps();
                Step step;
                dr.mtr.passenger.model.direction.Location location;
                String polyline;
                for (int i = 0; i < steps.size(); i++) {
                    step = steps.get(i);
                    location = step.getStartLocation();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                    polyline = step.getPolyline().getPoints();
                    decodelist = RouteDecode.decodePoly(polyline);
                    routelist.addAll(decodelist);
                    location = step.getEndLocation();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                }
            }
        }
        if (routelist.size() > 0) {
            PolylineOptions rectLine = new PolylineOptions().width(8).color(ContextCompat.getColor(this, R.color.colorPrimary));
            for (int i = 0; i < routelist.size(); i++) {
                rectLine.add(routelist.get(i));
            }

            ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
            polylineAnimator.setDuration(1000);
            polylineAnimator.setInterpolator(new LinearInterpolator());
            polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    blackPolylineOptions = new PolylineOptions();
                    blackPolylineOptions.width(10);
                    blackPolylineOptions.color(Color.BLACK);
                    blackPolylineOptions.startCap(new SquareCap());
                    blackPolylineOptions.endCap(new SquareCap());
                    blackPolylineOptions.jointType(ROUND);
                    blackPolylineOptions.addAll(routelist); // add here to all LatLng list
                    if (blackPolyline != null)
                        blackPolyline.remove();
                    blackPolyline = map.addPolyline(blackPolylineOptions);

                }
            });
            polylineAnimator.start();

            markerOptionsStart.position(routelist.get(0));
            markerOptionsEnd.position(routelist.get(routelist.size() - 1));
            startMarker = map.addMarker(markerOptionsStart.flat(true));
            Marker endMarker = map.addMarker(markerOptionsEnd);

            if (routelist.isEmpty()) return;
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (LatLng latLngPoint : routelist)
                boundsBuilder.include(latLngPoint);
            int routePadding = 50;
            LatLngBounds latLngBounds = boundsBuilder.build();
            map.setPadding(routePadding, routePadding, routePadding, routePadding);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
        }

        if (!ObjectUtil.isEmpty(allCordinatesList))
            allCordinatesList.clear();

        allCordinatesList.add(new DriverObject(routelist.get(0).latitude, routelist.get(0).longitude, driverId));
    }

    private void subscribeMQTTForDriverLocation(String driverId) {
        mqttManager = MQTTServiceClass.mqttManager;
        mqttClientStatus = MQTTServiceClass.mqttClientStatus;
        if (!ObjectUtil.isEmpty(mqttManager) && !ObjectUtil.isEmpty(mqttClientStatus)) {
            if (mqttClientStatus.equals(AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected)) {
                mqttManager.subscribeToTopic(domainId + "/driverslocation/" + driverId, AWSIotMqttQos.QOS0, new AWSIotMqttNewMessageCallback() {
                    @Override
                    public void onMessageArrived(String topic, byte[] data) {
                        try {
                            String message = new String(data, "UTF-8");
                            System.out.println("driver location MQTT : " + message);
                            //TODO from here parse the json response from JsonObject received from mqtt service
                            //EODriverCurrentLocation driverCurrentLocation = gson.fromJson(new String(data, "UTF-8"), EODriverCurrentLocation.class);
                            try {
                                JSONObject jsonObject = new JSONObject(message);
                                JSONObject jsonObject1 = jsonObject.getJSONObject("JsonObject");
                                String latitude = jsonObject1.getString("current_latitude");
                                String longitude = jsonObject1.getString("current_longitude");
                                String mqttDriverId = jsonObject1.getString("driverid");

                                if (!ObjectUtil.isEmpty(allCordinatesList)) {
                                    DriverObject driverObject = allCordinatesList.get(0);
                                    if (driverObject.driverId.equals(mqttDriverId)) {

                                        double oldLatitude = driverObject.lattitude;
                                        double oldLongitude = driverObject.longitude;

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                                                valueAnimator.setDuration(1000);
                                                valueAnimator.setInterpolator(new LinearInterpolator());
                                                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                    @Override
                                                    public void onAnimationUpdate(ValueAnimator valueAnimator) {

                                                        LatLng oldLatLng = new LatLng(oldLatitude, oldLongitude); //saved latlng
                                                        LatLng newLatLng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)); //mqtt latlng

                                                        //int isLastPoint;
                                                        for (int i = 0; i < routelist.size() - 1; i++) {
                                                            LatLng point1 = routelist.get(i);
                                                            LatLng point2 = routelist.get(i + 1);
                                                            List<LatLng> currentSegment = new ArrayList<>(2);
                                                            currentSegment.add(point1);
                                                            currentSegment.add(point2);
                                                            if (PolyUtil.isLocationOnPath(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude)), currentSegment, true, 50)) {

                                                                startMarker.setPosition(newLatLng);
                                                                startMarker.setAnchor(0.5f, 0.5f);
                                                                startMarker.setRotation((float) getBearing(oldLatLng, newLatLng));
                                                                //map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(newLatLng).zoom(16.0f).build()));

                                                                //isLastPoint = i;
                                                                routelist.remove(i); //remove the index which from routelist array which matched on routelist, LatLng comes from mqtt

                                                                if (blackPolyline != null) {
                                                                    blackPolyline.remove();
                                                                }

                                                                blackPolylineOptions = new PolylineOptions();
                                                                blackPolylineOptions.width(10);
                                                                blackPolylineOptions.color(Color.BLACK);
                                                                blackPolylineOptions.startCap(new SquareCap());
                                                                blackPolylineOptions.endCap(new SquareCap());
                                                                blackPolylineOptions.jointType(ROUND);
                                                                blackPolylineOptions.addAll(routelist);
                                                                blackPolyline = map.addPolyline(blackPolylineOptions);

                                                                driverObject.lattitude = newLatLng.latitude;
                                                                driverObject.longitude = newLatLng.longitude;
                                                                driverObject.driverId = mqttDriverId;

                                                                break;
                                                            }
                                                        }

                                                    }
                                                });
                                                valueAnimator.start();

//                                                startMarker.setPosition(newLatLng);
//                                                startMarker.setAnchor(0.5f, 0.5f);
//                                                startMarker.setRotation((float) getBearing(oldLatLng, newLatLng));
//                                                map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(newLatLng).zoom(16.0f).build()));

//                                                driverObject.lattitude = newLatLng.latitude;
//                                                driverObject.longitude = newLatLng.longitude;
//                                                driverObject.driverId = mqttDriverId;

                                            }
                                        });
                                    }

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                Log.d("onNext_MayBeConnected", "start_service");
                start_service();
            }
        }

    }

    private double getBearing(LatLng begin, LatLng end) {
        double PI = 3.14159;
        double lat1 = begin.latitude * PI / 180;
        double long1 = begin.longitude * PI / 180;
        double lat2 = end.latitude * PI / 180;
        double long2 = end.longitude * PI / 180;
        double dLon = (long2 - long1);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double brng = Math.atan2(y, x);
        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        return brng;
    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=true";
        String mode = "mode=driving";
        String transit_routing_preference = "transit_routing_preference=less_driving";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&" + transit_routing_preference + "&" + "key=" + DISTANCE_GOOGLE_API_KEY;
        return "https://maps.googleapis.com/maps/api/directions/json" + "?" + parameters;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DriverLocationEvent driverCurrentLocation) {
        if (!ObjectUtil.isEmpty(driverCurrentLocation.getDriverCurrentLocation())) {
            Toast.makeText(ActivityRideNow.this, "location event :" + driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getCurrentLongitude(), Toast.LENGTH_SHORT).show();

            System.out.println("location event :" + driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getCurrentLongitude());
        }
    }


    private boolean isShowZeroMapDialog; //this flag is used to check fare calculation if true then fare api will not hit

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);

        if (map.getUiSettings().isScrollGesturesEnabledDuringRotateOrZoom()) {
            map.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(false);
        }
        //TODO Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();
        }

        //TODO Getting URL to the Google Directions API
        if (sourceLatitude != 0.0 && sourceLongitude != 0.0 && destinationLatitude != 0.0 && destinationLongitude != 0.0) {
            if (sourceLatitude == destinationLatitude || sourceLongitude == destinationLongitude) {
                showZeroMapRouteDialog();
                isShowZeroMapDialog = true;
            } else {
                if (map != null)
                    map.clear();
                String urlToDrawPath = getUrl(new LatLng(sourceLatitude, sourceLongitude), new LatLng(destinationLatitude, destinationLongitude));
                this.drawPathInRideNowAndRideLater(urlToDrawPath);
            }

//            if (!isShowZeroMapDialog)
//                this.fareCalculationApi();
        }
    }

    private void fareCalculationApi() {
        EOFareRequest fareRequest = new EOFareRequest();
        fareRequest.setClat(String.valueOf(sourceLatitude));
        fareRequest.setClon(String.valueOf(sourceLongitude));
        fareRequest.setDlat(String.valueOf(destinationLatitude));
        fareRequest.setDlon(String.valueOf(destinationLongitude));
        fareRequest.setCustid(passengerId);

        if (!ObjectUtil.isEmpty(fareRequest)) {
            apiInterface.fareCalculationForRide(FARE_CALCULATION_API, fareRequest).enqueue(new Callback<EOFareResponse>() {
                @Override
                public void onResponse(Call<EOFareResponse> call, Response<EOFareResponse> response) {
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOFareResponse fareResponse = response.body();
                        if (!ObjectUtil.isEmpty(fareResponse)) {
                            if (fareResponse.getMessage().equalsIgnoreCase(RESPONSE_SUCCESS)) {
                                tvEstimatedFareToConfirmTrip.setText(MessageFormat.format("$ {0}", new DecimalFormat("##.##").format(fareResponse.getFare())));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOFareResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        Toast.makeText(ApplicationHelper.application(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //TODO from here call the google direction api via retrofit library
    private void drawPathInRideNowAndRideLater(String url) {

        apiInterface.getDirectionPath(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<GoogleDirectionResponse>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(GoogleDirectionResponse googleDirectionResponse) {

                        if (!ObjectUtil.isEmpty(googleDirectionResponse)) {
                            if (googleDirectionResponse.getStatus().equalsIgnoreCase(RESPONSE_OK)) {

                                MapAnimator.getInstance().stopAndRemovePolyLine();
                                //layoutConfirmBooking.setVisibility(View.VISIBLE);
                                //setRouteAfterRideNowIfRouteFound(ActivityRideNow.this, googleDirectionResponse.getRoutes(), map, polylineList, markerListOtherThanTaxis);
                                if (map != null)
                                    map.clear();
                                setRouteAfterRideNowIfRouteFound(googleDirectionResponse.getRoutes());
                                if (ObjectUtil.isNonEmptyStr(googleDirectionResponse.getRoutes().get(0).getLegs().get(0).getDistance().getText()) && ObjectUtil.isNonEmptyStr(googleDirectionResponse.getRoutes().get(0).getLegs().get(0).getDuration().getText())) {
                                    //TODO conversion from kilometer to miles
                                    String strKilometer = googleDirectionResponse.getRoutes().get(0).getLegs().get(0).getDistance().getText();
                                    String pattern = "([a-zA-Z,\\s])";
                                    String newPattern = strKilometer.replaceAll(pattern, "");
                                    double kilometer2 = Double.valueOf(newPattern);
                                    double miles = kilometer2 / 1.609;
                                    tvEstimatedDistance.setText(MessageFormat.format("{0}", new DecimalFormat("##.##").format(miles)) + " " + "miles");

                                    tvEstimatedTime.setText(googleDirectionResponse.getRoutes().get(0).getLegs().get(0).getDuration().getText());
//                                    //TODO price calculation from here
//                                    float[] totalDistance = new float[1];
//                                    Location.distanceBetween(sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude, totalDistance);
//                                    double totalPrice = 1 * totalDistance[0] / 1000;
//                                    tvEstimatedFareToConfirmTrip.setText(MessageFormat.format("$ {0}", new DecimalFormat("##.##").format(totalPrice)));

                                }

                            } else {
                                //TODO in case ZERO_RESULTS show dialog
                                showZeroMapRouteDialog();
                                isShowZeroMapDialog = true;
                            }

                            //TODO from here we are fetching fare from api
                            if (!isShowZeroMapDialog)
                                fareCalculationApi();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        if (t.getMessage() != null) {
                            Toast.makeText(ActivityRideNow.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void setDataToFinalScreen(EOActiveTripData activeTrip) {
        switch (activeTrip.getVehicleType()) {
            case "Any":
                ivCarType.setImageDrawable(getResources().getDrawable(R.drawable.all_car));
                break;
            case "Sedan":
                ivCarType.setImageDrawable(getResources().getDrawable(R.drawable.car1));
                break;
            case "SUV":
                ivCarType.setImageDrawable(getResources().getDrawable(R.drawable.car2));
                break;
            case "Minivan":
                ivCarType.setImageDrawable(getResources().getDrawable(R.drawable.car3));
                break;
          /*  case "Moto":
                ivCarType.setImageDrawable(getResources().getDrawable(R.drawable.bike));
                break;*/
            default:
                ivCarType.setImageDrawable(getResources().getDrawable(R.drawable.all_car));
                break;
        }
        tvTaxiType.setText(activeTrip.getVehicleType());
        tvDriverName.setText(activeTrip.getDriverName());
        tvDriverRating.setText(activeTrip.getDriverRating());
        //tvWaitingTime.setText(MessageFormat.format(getString(R.string.Pick_Up_in), "--"));
        //tvTaxiNumber1.setText(activeTrip.getTaxiPlateNumber());
        tvTaxiNumber2.setText(activeTrip.getTaxiPlateNumber());
        tvEstimatedFareCurrentTrip.setText(MessageFormat.format(getString(R.string.Estimated_Fare_to_be_paid), activeTrip.getEstimatedFare()));
        Picasso.get().load(activeTrip.getDriverImage()).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(ivDriverImage);
    }

    private void removeOtherMarkersThanTaxis(List<Marker> markerListOtherThanTaxis) {
        for (Marker marker : markerListOtherThanTaxis) {
            marker.remove();
        }
    }

    private void openCouponDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_coupon_code);

        float dialogRadius = UIUtil.getDimension(R.dimen._5sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.white, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        EditText etCouponCode = dialog.findViewById(R.id.editText20);

        dialog.findViewById(R.id.button25).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ObjectUtil.isNonEmptyStr(ObjectUtil.getTextFromView(etCouponCode))) {
                    Toast.makeText(ActivityRideNow.this, "Coupon code is not valid", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(ActivityRideNow.this, "Enter coupon code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.findViewById(R.id.button26).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void contactType() {
        sheetForPaymentType = new BottomSheet.Builder(this, R.style.BottomSheet_StyleDialog1).title(getString(R.string.contact_type)).sheet(R.menu.contact_list).listener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.self_contact:
                        iv_contact_type.setImageDrawable(ContextCompat.getDrawable(ApplicationHelper.application(), R.drawable.ic_user));
                        tvTaxiFor.setText(R.string.my_self);
                        if (ObjectUtil.isNonEmptyStr(phoneNumber))
                            tvContactValue.setText(phoneNumber);
                        break;
                    case R.id.add_new_contact:
                        iv_contact_type.setImageDrawable(ContextCompat.getDrawable(ApplicationHelper.application(), R.drawable.add_new_contact));
                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        startActivityForResult(intent, REQUEST_CODE);
                        break;
                }
                return false;
            }
        }).build();
        sheetForPaymentType.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(Objects.requireNonNull(contactData), null, null, null, null);
                if (Objects.requireNonNull(c).moveToFirst()) {
                    String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    String phoneNumber, personName;
                    if (Integer.valueOf(hasNumber) == 1) {
                        Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        while (Objects.requireNonNull(numbers).moveToNext()) {
                            phoneNumber = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            personName = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Nickname.DISPLAY_NAME));
                            tvTaxiFor.setText(personName);
                            tvContactValue.setText(phoneNumber);
                        }
                    }
                }
            }
        }
    }


    private void paymentType() {
        sheetForPaymentType = new BottomSheet.Builder(this, R.style.BottomSheet_StyleDialog1).title(getString(R.string.payment_type)).sheet(R.menu.list).listener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.by_cash:
                        ivPaymentType.setImageDrawable(ContextCompat.getDrawable(ApplicationHelper.application(), R.drawable.ic_by_cash));
                        tvPaymentType.setText(R.string.cash);
                        paymentType = "cash";
                        break;
                    case R.id.by_debit_card:
                        ivPaymentType.setImageDrawable(ContextCompat.getDrawable(ApplicationHelper.application(), R.drawable.ic_bycredit__card));
                        tvPaymentType.setText(R.string.company_credit);
                        paymentType = "companycredit";
                        break;

             /*     case R.id.by_Voucher:
                        ivPaymentType.setImageDrawable(ContextCompat.getDrawable(ApplicationHelper.application(), R.drawable.ic_by_voucher));
                        tvPaymentType.setText(R.string.Voucher);
                        break;
                    case R.id.by_credit_card:
                        ivPaymentType.setImageDrawable(ContextCompat.getDrawable(ApplicationHelper.application(), R.drawable.ic_bycredit__card));
                        tvPaymentType.setText(R.string.credit_card);
                        break;*/

                }
                return false;
            }
        }).build();
        sheetForPaymentType.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ObjectUtil.isNonEmptyStr(mapStyle) && !ObjectUtil.isEmpty(this.map)) {
            switch (mapStyle) {
                case "DEFAULT":
                    this.map.setMapStyle(MapStyleOptions.loadRawResourceStyle(ApplicationHelper.application(), R.raw.default_style));
                    break;
                case "DARK":
                    this.map.setMapStyle(MapStyleOptions.loadRawResourceStyle(ApplicationHelper.application(), R.raw.dark));
                    break;
                case "PAPER":
                    this.map.setMapStyle(MapStyleOptions.loadRawResourceStyle(ApplicationHelper.application(), R.raw.paper));
                    break;
                case "CUSTOM":
                    this.map.setMapStyle(MapStyleOptions.loadRawResourceStyle(ApplicationHelper.application(), R.raw.custom));
                    break;
                case "RETRO":
                    this.map.setMapStyle(MapStyleOptions.loadRawResourceStyle(ApplicationHelper.application(), R.raw.retro));
                    break;
            }
        }

        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsEnabled && !isNetworkEnable) {
            //showSettingsAlert();
            getLastLocation();
        } else {
            if (ActivityCompat.checkSelfPermission(ApplicationHelper.application(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ApplicationHelper.application(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
                getLocation();
            } else {
                canGetLocation = false;
            }
        }
    }

    private void getLocation() {
        try {
            if (canGetLocation) {
                if (isNetworkEnable) {
                    // from Network Provider
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                } else if (isGpsEnabled) {
                    // from GPS
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                } else {
                    lastLocation.setLatitude(0);
                    lastLocation.setLongitude(0);
                }
            } else {
                System.out.println("Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(Objects.requireNonNull(provider));
            System.out.println(location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override

    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location updatedLocation) {
        if (!ObjectUtil.isEmpty(updatedLocation)) {
            lastLocation = updatedLocation;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    private void showZeroMapRouteDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_route_not_found);
        float dialogRadius = UIUtil.getDimension(R.dimen._3sdp);
        UIUtil.setBackgroundRound(dialog.findViewById(R.id.mainLayout), R.color.white, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});
        dialog.findViewById(R.id.button25).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ActivityRideNow.this.finish();
            }
        });
        dialog.show();
    }

    private void cancelOnGoingTrip() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        Dialog dialog = builder.setTitle(getString(R.string.Cancel_Trip))
                .setMessage(getString(R.string.are_you_sure_you))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sheetForReasons = new BottomSheet.Builder(ActivityRideNow.this, R.style.BottomSheet_StyleDialog1).title(getString(R.string.reason_for_cancellation)).sheet(R.menu.noicon).listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String reason;
                                switch (which) {
                                    case R.id.reason1:
                                        reason = getString(R.string.reason1);
                                        break;
                                    case R.id.reason2:
                                        reason = getString(R.string.reason2);
                                        break;
                                    case R.id.reason3:
                                        reason = getString(R.string.reason3);
                                        break;
                                    case R.id.reason4:
                                        reason = getString(R.string.reason4);
                                        break;
                                    case R.id.reason5:
                                        reason = getString(R.string.reason5);
                                        break;
                                    default:
                                        reason = getString(R.string.reason_not_selected);
                                }

                                if (!ObjectUtil.isEmpty(activeTrip.getTripid())) {
                                    EOCancelTripRequest eoCancelTripRequest = new EOCancelTripRequest();
                                    eoCancelTripRequest.setReason(reason);
                                    eoCancelTripRequest.setTripid(Integer.parseInt(activeTrip.getTripid()));

                                    if (!GlobalUtil.isNetworkAvailable(ActivityRideNow.this)) {
                                        UIUtil.showNetworkDialog(ActivityRideNow.this);
                                        return;
                                    }

                                    apiInterface.cancelOnGoingTrip(eoCancelTripRequest).enqueue(new Callback<EOCancelTripResponse>() {
                                        @Override
                                        public void onResponse(Call<EOCancelTripResponse> call, Response<EOCancelTripResponse> response) {
                                            if (!ObjectUtil.isEmpty(response.body())) {
                                                EOCancelTripResponse cancelTripResponse = response.body();
                                                if (!ObjectUtil.isEmpty(cancelTripResponse)) {
                                                    if (cancelTripResponse.getStatus().equalsIgnoreCase(RESPONSE_SUCCESS)) {

                                                        //TODO when ride is cancel then play flush sound
                                                        mediaPlayer = MediaPlayer.create(ActivityRideNow.this, R.raw.flush);
                                                        if (mediaPlayer != null)
                                                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                                @Override
                                                                public void onCompletion(MediaPlayer mp) {
                                                                    // TODO Auto-generated method stub
                                                                    mp.release();
                                                                }
                                                            });

                                                        if (mediaPlayer != null)
                                                            mediaPlayer.start();

                                                        activeTrip.setTripid("");
                                                        activeTrip.setDriverid("");
                                                        Toast.makeText(ActivityRideNow.this, cancelTripResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                                        ActivityRideNow.this.finish();
                                                    } else {
                                                        Toast.makeText(ActivityRideNow.this, cancelTripResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                                        String topicToPublish = domainId + "/statusupdate/0/" + activeTrip.getTripid() + "/7";

                                                        TripCancellation tripCancellation = new TripCancellation();
                                                        tripCancellation.setIsvisible(1);
                                                        tripCancellation.setStatus(7);
                                                        tripCancellation.setTripid(Integer.parseInt(activeTrip.getTripid()));
                                                        tripCancellation.setVouchertype(0);

                                                        String toPublish = (new Gson().toJson(tripCancellation));
                                                        if (!ObjectUtil.isEmpty(mqttManager) && !ObjectUtil.isEmpty(mqttClientStatus)) {
                                                            if (mqttClientStatus.equals(AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected)) {

                                                                mqttManager.publishData(toPublish.getBytes(), topicToPublish, AWSIotMqttQos.QOS1, new AWSIotMqttMessageDeliveryCallback() {
                                                                    @Override
                                                                    public void statusChanged(MessageDeliveryStatus status, Object userData) {
                                                                        if (status == MessageDeliveryStatus.Success) {
                                                                            ActivityRideNow.this.runOnUiThread(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    activeTrip.setTripid("");
                                                                                    activeTrip.setDriverid("");
                                                                                }
                                                                            });
                                                                        } else {
                                                                            Toast.makeText(ActivityRideNow.this, "Trip Cancellation Failed", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                }, tripCancellation);
                                                            } else {
                                                                start_service();
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<EOCancelTripResponse> call, Throwable t) {
                                            if (t.getMessage() != null) {
                                                Toast.makeText(ActivityRideNow.this, "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                            }
                        }).build();
                        sheetForReasons.show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.drawable.my_button_bg);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void openMoreDialog() {
        final Dialog dialog = new Dialog(this, R.style.Theme_AppCompat_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.ScaleFromCenter;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_more);

        ConstraintLayout applyCouponLayout = dialog.findViewById(R.id.constraintLayout2);
        ConstraintLayout supportLayout = dialog.findViewById(R.id.constraintLayout3);

        applyCouponLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCouponDialog();
            }
        });

        supportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivityRideNow.this, "This feature is coming soon", Toast.LENGTH_SHORT).show();
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

    private String getAddressFromLatLong(double latitude, double longitude) {
        StringBuilder strAddress = new StringBuilder();
        Geocoder geocoder = new Geocoder(ApplicationHelper.application(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                strAddress.append(addresses.get(0).getAddressLine(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strAddress.toString();
    }


    private class DriverObject { //helper class to store driver object while movement car

        private double lattitude;
        private double longitude;
        private String driverId;

        DriverObject(double lattitude, double longitude, String driverId) {
            this.lattitude = lattitude;
            this.longitude = longitude;
            this.driverId = driverId;
        }

        @NonNull
        @Override
        public String toString() {
            return driverId;
        }
    }



}
