package dr.mtr.passenger.fragments;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import dr.mtr.passenger.R;
import dr.mtr.passenger.activities.ActivityRideNow;
import dr.mtr.passenger.application.ApplicationHelper;
import dr.mtr.passenger.components.SessionSecuredPreferences;
import dr.mtr.passenger.dialogs.GlobalProgressDialog;
import dr.mtr.passenger.enums.TripStatus;
import dr.mtr.passenger.eventbus.DriverLocationEvent;
import dr.mtr.passenger.eventbus.MapTypeEvent;
import dr.mtr.passenger.model.activeTrip.EOActiveTripRequest;
import dr.mtr.passenger.model.activeTrip.EOActiveTripResponse;
import dr.mtr.passenger.model.nearByDriver.EONearByDriverData;
import dr.mtr.passenger.model.nearByDriver.EONearByDriverRequest;
import dr.mtr.passenger.model.nearByDriver.EONearByDriverResponse;
import dr.mtr.passenger.mqtt.MQTTServiceClass;
import dr.mtr.passenger.networking.APIClient;
import dr.mtr.passenger.utils.GlobalUtil;
import dr.mtr.passenger.utils.LatLngInterpolator;
import dr.mtr.passenger.utils.MapUtils;
import dr.mtr.passenger.utils.ObjectUtil;
import dr.mtr.passenger.utils.OnClickViewPagerItem;
import dr.mtr.passenger.vehicles.TaxiTypeAdapter;
import dr.mtr.passenger.vehicles.VehicleItem;
import dr.mtr.passenger.vehicles.VehicleViewModel;
import dr.mtr.passenger.widgets.EqualSpacingItemDecoration;
import dr.mtr.passenger.widgets.discretescrollview.DiscreteScrollView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread;
import static dr.mtr.passenger.enums.TripStatus.RIDER_CONFIRMED_TRIP_SEARCHING_NEARBY_TAXI;
import static dr.mtr.passenger.enums.TripStatus.RIDER_ENTERING_DETAILS_AND_NOT_PRESSED_DONE;
import static dr.mtr.passenger.utils.Constants.DOMAIN_ID;
import static dr.mtr.passenger.utils.Constants.LOGIN_PREFERENCE;
import static dr.mtr.passenger.utils.Constants.PASSENGER_INFO_ID;
import static dr.mtr.passenger.utils.Constants.PASSENGER_MOBILE;
import static dr.mtr.passenger.utils.Constants.RESPONSE_SUCCESS;
import static dr.mtr.passenger.utils.Providers.provideVehicleDrawable;

public class FragmentDashboard extends Fragment implements OnClickViewPagerItem, OnMapReadyCallback, MapUtils, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, LocationListener, DiscreteScrollView.OnItemChangedListener<TaxiTypeAdapter.ViewHolder>, DiscreteScrollView.ScrollStateChangeListener<TaxiTypeAdapter.ViewHolder> {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String domainId;
    private String passengerId;
    private String phoneNumber;

    private View view;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private LocationManager locationManager;
    private Location lastLocation;
    private double longitude;
    private double latitude;

    private double pickerSourceLatitude;
    private double pickerSourceLongitude;
    private double pickerDestLatitude;
    private double pickerDestLongitude;

    private ImageView imageViewCentermarker;
    private TextView autoCompleteFromLocation, autoCompleteToLocation;
    private LinearLayout layoutPickUp, layoutDropOff, layoutPickUpAndDropOff, service_unavailable, layoutRideNow;
    private FloatingActionButton fabCurrentLocation;
    private CardView cvPickUp, cvDropOff;
    private CoordinatorLayout bookingLayout, coordinatorLayout;
    private Button btnRideLater, btnRideNow;
    private RecyclerView pager_container;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;

    private boolean isPickedUpLocationSelected = true;
    private boolean isCurrentLocationButtonPressed = false;
    private boolean isComeFromRideNowScreen = false;
    private boolean cameraApiMoveStarted;
    private boolean isCameraGestured = false;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private boolean isPickerLocationSelected = false;
    private TripStatus mCurrentTripStatus = RIDER_ENTERING_DETAILS_AND_NOT_PRESSED_DONE;
    private boolean isFirstTimeMoveCamera = false;
    private EONearByDriverRequest nearByDriverRequest;
    private List<VehicleItem> vehicleItem1List;
    private TaxiTypeAdapter taxiTypeAdapter;
    private HashMap<String, Marker> markerHashMap = new HashMap<>();
    //private List<Marker> markerListTaxis = new ArrayList<>();
    private ArrayList<EONearByDriverData> driverDataList;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 15;
    private static final long MIN_TIME_BETWEEN_UPDATES = 0;
    private boolean isGpsEnabled = false;
    private boolean isNetworkEnable = false;
    private boolean canGetLocation = true;
    private String mapStyleType;
    private MediaPlayer mediaPlayer;
    private int mMarkerCount = 0;
    private Marker currentLocationMarker;
    private Location currLocation;
    private Boolean firstTimeFlag = true;
    private FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        this.initView();
        this.setOnClickListener();

        return this.view;
    }

    private void initView() {

        this.progress = new GlobalProgressDialog(ApplicationHelper.application());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.domainId = String.valueOf(loginPreferences.getInt(DOMAIN_ID, 0));
        this.passengerId = loginPreferences.getString(PASSENGER_INFO_ID, "");
        this.phoneNumber = loginPreferences.getString(PASSENGER_MOBILE, "");

        this.imageViewCentermarker = view.findViewById(R.id.imageViewCentermarker);
        this.autoCompleteFromLocation = view.findViewById(R.id.autoCompleteFromLocation);
        this.autoCompleteToLocation = view.findViewById(R.id.autoCompleteToLocation);
        this.layoutPickUp = view.findViewById(R.id.layoutPickUp);
        this.layoutDropOff = view.findViewById(R.id.layoutDropOff);
        this.fabCurrentLocation = view.findViewById(R.id.fabCurrentLocation);
        this.cvPickUp = view.findViewById(R.id.cvPickUp);
        this.cvDropOff = view.findViewById(R.id.cvDropOff);

        this.bookingLayout = view.findViewById(R.id.bookingLayout);
        this.coordinatorLayout = view.findViewById(R.id.coordinatorLayout);
        this.layoutRideNow = view.findViewById(R.id.layoutRideNow);
        this.pager_container = view.findViewById(R.id.pager_container);
        this.btnRideLater = view.findViewById(R.id.btnRideLater);
        this.btnRideNow = view.findViewById(R.id.btnRideNow);
        this.layoutPickUpAndDropOff = view.findViewById(R.id.layoutPickUpAndDropOff);
        this.service_unavailable = view.findViewById(R.id.service_unavailable);

        //TODO load map by default, when open this fragment
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.googleMap);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        locationManager = (LocationManager) ApplicationHelper.application().getSystemService(Context.LOCATION_SERVICE);

        nearByDriverRequest = new EONearByDriverRequest();
        nearByDriverRequest.setDomainid(this.domainId);
        nearByDriverRequest.setPid(passengerId);
        nearByDriverRequest.setVtype(provideVehicleDrawable(0));

        vehicleItem1List = VehicleViewModel.get().getVehicles();

        pager_container.addItemDecoration(new EqualSpacingItemDecoration(16));
        taxiTypeAdapter = new TaxiTypeAdapter(vehicleItem1List, this);
        pager_container.setAdapter(taxiTypeAdapter);
        pager_container.smoothScrollToPosition(0);

        //TODO initialization for place picker page
        String apiKey = getString(R.string.google_maps_key);
        if (!Places.isInitialized()) {
            Places.initialize(ApplicationHelper.application(), apiKey);
        }

        this.buildGoogleApiClient();
        this.configureCameraIdle();
    }

    private void setOnClickListener() {
        this.fabCurrentLocation.setOnClickListener(this);
        this.autoCompleteFromLocation.setOnClickListener(this);
        this.cvPickUp.setOnClickListener(this);
        this.layoutPickUp.setOnClickListener(this);
        this.btnRideLater.setOnClickListener(this);
        this.btnRideNow.setOnClickListener(this);

        this.autoCompleteToLocation.setOnClickListener(this);
        this.cvDropOff.setOnClickListener(this);
        this.layoutDropOff.setOnClickListener(this);
    }

    //TODO get current location from here
    private void getCurrentLocation() {
        if (map != null)
            this.map.clear();

        if (ActivityCompat.checkSelfPermission(ApplicationHelper.application(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ApplicationHelper.application(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            longitude = lastLocation.getLongitude();
            latitude = lastLocation.getLatitude();

            if (!isFirstTimeMoveCamera) {
                isFirstTimeMoveCamera = true;
                pickerSourceLatitude = latitude;
                pickerSourceLongitude = longitude;
            } else {
                //TODO this is used to check when user click on fab button then lat, lang will set
                isFirstTimeMoveCamera = false;
                pickerSourceLatitude = latitude;
                pickerSourceLongitude = longitude;
            }

            String address = getAddressFromLatLong(latitude, longitude);
            if (!ObjectUtil.isEmptyStr(address))
                if (!isCurrentLocationButtonPressed)
                    this.autoCompleteFromLocation.setText(address);
                else
                    this.autoCompleteFromLocation.setText(getString(R.string.Fetching_Address));

            this.moveCameraFirstTime();
        }
    }

    //TODO move map first time to set current location
    private void moveCameraFirstTime() {
        LatLng latLng = new LatLng(latitude, longitude);
        this.map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16f).build();
        this.map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
        EventBus.getDefault().register(this);

        Intent intent = new Intent(ApplicationHelper.application(), MQTTServiceClass.class);
        if (!isServiceRunning(MQTTServiceClass.class, Objects.requireNonNull(getActivity()))) {
            getActivity().startService(intent);
        }
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(int position) {
        String vType = "Any";
        for (int i = 0; i < vehicleItem1List.size(); i++) {
            if (i == position) {
                vehicleItem1List.get(i).setSelected(true);
                vType = provideVehicleDrawable(position);
            } else {
                vehicleItem1List.get(i).setSelected(false);
            }
        }
        nearByDriverRequest.setVtype(vType);
        taxiTypeAdapter.notifyDataSetChanged();
    }

    private void openPlacePicker() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(ApplicationHelper.application());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private void didChangeCamera(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        this.map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
        this.map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //TODO get current location from here
        if (!isComeFromRideNowScreen) {
            getCurrentLocation();
        }

        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGpsEnabled && !isNetworkEnable) {
            //showSettingsAlert();
            getLastLocation();
        } else {
            if (ActivityCompat.checkSelfPermission(ApplicationHelper.application(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ApplicationHelper.application(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
                    //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                } else if (isGpsEnabled) {
                    // from GPS
                    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MapTypeEvent mapTypeEvent) {
        this.mapStyleType = mapTypeEvent.getMapTypeValue();
        if (ObjectUtil.isNonEmptyStr(mapStyleType) && !ObjectUtil.isEmpty(this.map)) {
            switch (mapStyleType) {
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
    }

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
            if (ActivityCompat.checkSelfPermission(ApplicationHelper.application(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ApplicationHelper.application(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();
        }
        this.map.setMyLocationEnabled(true);

        //TODO when map is moving then fetching address string is showing in To and From EditText
        map.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {

                if ((i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) || isCurrentLocationButtonPressed) {
                    isCurrentLocationButtonPressed = false;
                    cameraApiMoveStarted = false;
                    isCameraGestured = true;

                    if (mCurrentTripStatus == RIDER_CONFIRMED_TRIP_SEARCHING_NEARBY_TAXI) {
                    } else {
                        bookingLayout.animate().translationY(bookingLayout.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    }

                    //layoutPickUpAndDropOff.animate().translationY(-((MainActivity) getActivity()).getToolbar().getBottom()).setDuration(400).setInterpolator(new AccelerateInterpolator()).start();
                    //(((MainActivity) getActivity()).getToolbar()).animate().translationY(-((MainActivity) getActivity()).getToolbar().getBottom()).setDuration(350).setInterpolator(new AccelerateInterpolator()).start();

                    if (isPickedUpLocationSelected) {
                        autoCompleteFromLocation.setText(getString(R.string.Fetching_Address));
                    } else {
                        autoCompleteToLocation.setText(getString(R.string.Fetching_Address));
                    }
                }
                if (i == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
                    cameraApiMoveStarted = true;
                }
            }
        });

        this.map.setOnCameraIdleListener(onCameraIdleListener);
    }

    //TODO when map is move the new latitude and longitude is updating
    private void configureCameraIdle() {

        this.onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                latitude = map.getCameraPosition().target.latitude;
                longitude = map.getCameraPosition().target.longitude;

                nearByDriverRequest.setLat(String.valueOf(pickerSourceLatitude));
                nearByDriverRequest.setLng(String.valueOf(pickerSourceLongitude));

                //((MainActivity) getActivity()).getToolbar().animate().translationY(0).setDuration(400).setInterpolator(new DecelerateInterpolator()).start();
                //layoutPickUpAndDropOff.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

                bookingLayout.animate().translationY(0).setDuration(400).setInterpolator(new DecelerateInterpolator()).start();

                if (isCameraGestured) {
                    String changedAddress = getAddressFromLatLong(latitude, longitude);
                    if (isPickedUpLocationSelected) {
                        pickerSourceLatitude = map.getCameraPosition().target.latitude;
                        pickerSourceLongitude = map.getCameraPosition().target.longitude;
                        if (ObjectUtil.isEmptyStr(changedAddress)) {
                            autoCompleteFromLocation.setText(getString(R.string.Fetching_Address));
                        } else {
                            autoCompleteFromLocation.setText(changedAddress);
                        }
                    } else {
                        pickerDestLatitude = map.getCameraPosition().target.latitude;
                        pickerDestLongitude = map.getCameraPosition().target.longitude;
                        if (ObjectUtil.isEmptyStr(changedAddress)) {
                            autoCompleteToLocation.setText(getString(R.string.Fetching_Address));
                        } else {
                            autoCompleteToLocation.setText(changedAddress);
                        }
                    }
                    isCameraGestured = false;
                } else { //commented here becz when we change language then destination address not set from picker

                    if (isPickerLocationSelected) {
                        isPickerLocationSelected = false;
                    }
                }

                //TODO check from any active trip is going on or not if yes, then move to next screen and show the path
                checkOngoingTrip();

                //TODO from here call near by driverApi
                callNearByDriversApi();

            }
        };
    }

    private void callNearByDriversApi() {
        if (!ObjectUtil.isEmpty(nearByDriverRequest)) {
            if (driverDataList == null) {
                driverDataList = new ArrayList<>();
            } else {
                driverDataList.clear();
            }

            if (!GlobalUtil.isNetworkAvailable(getActivity())) {
                Snackbar.make(coordinatorLayout, getResources().getString(R.string.no_net_connection), Snackbar.LENGTH_LONG).show();
                return;
            }

            apiInterface.nearByDrivers(nearByDriverRequest).enqueue(new Callback<EONearByDriverResponse>() {
                @Override
                public void onResponse(Call<EONearByDriverResponse> call, Response<EONearByDriverResponse> response) {
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EONearByDriverResponse nearByDriverResponse = response.body();
                        if (!ObjectUtil.isEmpty(nearByDriverResponse)) {
                            if (nearByDriverResponse.getStatus().equalsIgnoreCase(RESPONSE_SUCCESS)) {

                                for (EONearByDriverData driver : nearByDriverResponse.getData()) {
                                    Marker marker = map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(driver.getLat()), Double.parseDouble(driver.getLng()))).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.uber_car)));
                                    markerHashMap.put(driver.getDriverid(), marker);
                                    //markerListTaxis.add(marker);
                                    //driver.setMarker(marker);
                                    driverDataList.add(driver);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EONearByDriverResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        Toast.makeText(ApplicationHelper.application(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //showMarkers(new LatLng(map.getCameraPosition().target.latitude, map.getCameraPosition().target.longitude), 2000, markerListTaxis);
    }

    private void checkOngoingTrip() {
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
                                if (!ObjectUtil.isEmpty(activeTripResponse.getData().get(0))) {
                                    Intent goToRideNowActivity = new Intent(ApplicationHelper.application(), ActivityRideNow.class);
                                    goToRideNowActivity.putExtra("onGoingTripResponse", activeTripResponse.getData().get(0));
                                    goToRideNowActivity.putExtra("isKillAppForcefully", true);
                                    startActivity(goToRideNowActivity);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOActiveTripResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        Toast.makeText(ApplicationHelper.application(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DriverLocationEvent driverCurrentLocation) {
        if (!ObjectUtil.isEmpty(driverCurrentLocation.getDriverCurrentLocation())) {

            System.out.println("markerHashMap : " + markerHashMap.size());
            LatLng driverCurrentLatLng = new LatLng(Double.parseDouble(driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getCurrentLatitude()), Double.parseDouble(driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getCurrentLongitude()));

            if (ObjectUtil.isEmpty(driverDataList)) {
                EONearByDriverData nearByDriverData = new EONearByDriverData();
                nearByDriverData.setDriverid(driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getDriverid());
                nearByDriverData.setLat(driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getCurrentLatitude());
                nearByDriverData.setLng(driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getCurrentLongitude());

                if (getActivity() != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //layoutRideNow.setVisibility(View.VISIBLE);
                            //service_unavailable.setVisibility(View.GONE);
                            if (map != null) {
                                Marker marker = map.addMarker(new MarkerOptions().position(driverCurrentLatLng).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.uber_car)));
                                markerHashMap.put(driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getDriverid(), marker);
                                //markerListTaxis.add(marker);
                                //nearByDriverData.setMarker(marker);
                            }
                        }
                    });
                }
                driverDataList.add(nearByDriverData);
                checkDriverRotationOnMap(driverDataList);
            } else {
                //EONearByDriverData eoNearByDriverData = driverDataList.get(driverDataList.indexOf(driverCurrentLocation.getJsonObject().getDriverid()));
                if (!ObjectUtil.isEmpty(driverDataList)) {
                    for (EONearByDriverData driverData : driverDataList) {
                        if (driverData.getDriverid().equals(driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getDriverid())) {
                            driverData.setLat(driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getCurrentLatitude());
                            driverData.setLng(driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getCurrentLongitude());
                            driverDataList.add(driverData);
                        } else {
                            EONearByDriverData nearByDriverData = new EONearByDriverData();
                            nearByDriverData.setDriverid(driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getDriverid());
                            nearByDriverData.setLat(driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getCurrentLatitude());
                            nearByDriverData.setLng(driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getCurrentLongitude());
                            driverDataList.add(nearByDriverData);
                            if (getActivity() != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (map != null) {
                                            Marker marker = map.addMarker(new MarkerOptions().position(driverCurrentLatLng).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.uber_car)));
                                            markerHashMap.put(driverCurrentLocation.getDriverCurrentLocation().getJsonObject().getDriverid(), marker);
                                        }
                                    }
                                });
                            }
                        }
                    }
                    checkDriverRotationOnMap(driverDataList);
                }

            }
        }
    }

    private void checkDriverRotationOnMap(ArrayList<EONearByDriverData> nearByDriverDataArrayList) {
        for (EONearByDriverData driverData : nearByDriverDataArrayList) {
            for (Map.Entry<String, Marker> entry : markerHashMap.entrySet()) {
                if (driverData.getDriverid().equals(entry.getKey())) {
                    rotateMarker(entry.getValue(), new LatLng(Double.valueOf(driverData.getLat()), Double.valueOf(driverData.getLng())));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getActivity()));

        if (map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.googleMap);
            Objects.requireNonNull(mapFragment).getMapAsync(this);
        }

        //TODO when we back from next screen then move camera on destination location
        if (autoCompleteToLocation.getAlpha() == 1.0f) {
            if (pickerDestLatitude != 0.0 && pickerDestLongitude != 0.0) {
                didChangeCamera(pickerDestLatitude, pickerDestLongitude);
            }
        }

        isClickedFromPicker = false;
        isClickedToPicker = false;
    }

    private boolean isClickedFromPicker;
    private boolean isClickedToPicker;

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabCurrentLocation:
                this.isCurrentLocationButtonPressed = true;
                this.getCurrentLocation();
                break;
            case R.id.autoCompleteFromLocation:
                fabCurrentLocation.setVisibility(View.VISIBLE);
                isCurrentLocationButtonPressed = false;

                if (autoCompleteFromLocation.getAlpha() == 1.0f && !isClickedFromPicker) {
                    isClickedFromPicker = true;
                    openPlacePicker();
                } else {
                    autoCompleteFromLocation.setAlpha(1.0f);
                    autoCompleteToLocation.setAlpha(0.5f);
                    autoCompleteFromLocation.requestFocus();
                }
                if (pickerSourceLatitude != 0.0 && pickerSourceLongitude != 0.0) {
                    didChangeCamera(pickerSourceLatitude, pickerSourceLongitude);
                } else {
                    moveCameraFirstTime();
                }
                imageViewCentermarker.setImageDrawable(ContextCompat.getDrawable(ApplicationHelper.application(), R.drawable.ic_marker_pickup));
                if (!isPickedUpLocationSelected)
                    didTapButton(imageViewCentermarker);

                isPickedUpLocationSelected = true;
                break;
            case R.id.autoCompleteToLocation:
                fabCurrentLocation.setVisibility(View.GONE);
                isCurrentLocationButtonPressed = false;

                if ((autoCompleteToLocation.getAlpha() == 1.0f && !isClickedToPicker) || autoCompleteToLocation.getText().equals(getResources().getString(R.string.drop_location)) && !isClickedToPicker) {
                    autoCompleteToLocation.setAlpha(1.0f);
                    autoCompleteFromLocation.setAlpha(0.5f);
                    autoCompleteToLocation.requestFocus();
                    isClickedToPicker = true;
                    openPlacePicker();
                } else {
                    autoCompleteToLocation.setAlpha(1.0f);
                    autoCompleteFromLocation.setAlpha(0.5f);
                    autoCompleteToLocation.requestFocus();
                }

                if (pickerDestLatitude != 0.0 && pickerDestLongitude != 0.0) {
                    didChangeCamera(pickerDestLatitude, pickerDestLongitude);
                }

                imageViewCentermarker.setImageDrawable(ContextCompat.getDrawable(ApplicationHelper.application(), R.drawable.ic_marker_dropoff));
                if (isPickedUpLocationSelected)
                    didTapButton(imageViewCentermarker);

                isPickedUpLocationSelected = false;
                break;
            case R.id.btnRideLater:

                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.button);
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

                if (autoCompleteToLocation.getText().equals(getResources().getString(R.string.drop_location)) && !isClickedToPicker) {
                    autoCompleteToLocation.setAlpha(1.0f);
                    autoCompleteFromLocation.setAlpha(0.5f);
                    autoCompleteToLocation.requestFocus();
                    isClickedToPicker = true;
                    isRideNowClicked = true;
                    fabCurrentLocation.setVisibility(View.GONE);
                    openPlacePicker();
                } else if (!autoCompleteToLocation.getText().equals(getResources().getString(R.string.drop_location)) && !isClickedToPicker) {
                    isClickedToPicker = true;
                    rideLateTrip();
                }

                imageViewCentermarker.setImageDrawable(ContextCompat.getDrawable(ApplicationHelper.application(), R.drawable.ic_marker_dropoff));
                isPickedUpLocationSelected = false;
                break;
            case R.id.btnRideNow:

                mediaPlayer = MediaPlayer.create(getActivity(), R.raw.button);
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

                if (autoCompleteToLocation.getText().equals(getResources().getString(R.string.drop_location)) && !isClickedToPicker) {
                    autoCompleteToLocation.setAlpha(1.0f);
                    autoCompleteFromLocation.setAlpha(0.5f);
                    autoCompleteToLocation.requestFocus();
                    isClickedToPicker = true;
                    isRideNowClicked = true;
                    fabCurrentLocation.setVisibility(View.GONE);
                    openPlacePicker();
                } else if (!autoCompleteToLocation.getText().equals(getResources().getString(R.string.drop_location)) && !isClickedToPicker) {
                    isClickedToPicker = true;
                    openRideNowScreen(false, "");
                }
                isPickedUpLocationSelected = false;
                imageViewCentermarker.setImageDrawable(ContextCompat.getDrawable(ApplicationHelper.application(), R.drawable.ic_marker_dropoff));
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                isComeFromRideNowScreen = true;
                isPickerLocationSelected = true;

                Place place = Autocomplete.getPlaceFromIntent(data);

                if (isRideNowClicked) {
                    String destinationLocation = getAddressFromLatLong(Objects.requireNonNull(place.getLatLng()).latitude, place.getLatLng().longitude);
                    autoCompleteToLocation.setText(destinationLocation);
                    pickerDestLatitude = place.getLatLng().latitude;
                    pickerDestLongitude = place.getLatLng().longitude;
                    didChangeCamera(pickerDestLatitude, pickerDestLongitude);
                    autoCompleteToLocation.setAlpha(1.0f);
                    autoCompleteFromLocation.setAlpha(0.5f);
                    autoCompleteToLocation.requestFocus();
                    imageViewCentermarker.setImageDrawable(ContextCompat.getDrawable(ApplicationHelper.application(), R.drawable.ic_marker_dropoff));
                    isRideNowClicked = false;
                } else {
                    if (isPickedUpLocationSelected) {
                        String sourceLocation = getAddressFromLatLong(Objects.requireNonNull(place.getLatLng()).latitude, place.getLatLng().longitude);
                        autoCompleteFromLocation.setText(sourceLocation);
                        pickerSourceLatitude = place.getLatLng().latitude;
                        pickerSourceLongitude = place.getLatLng().longitude;
                        didChangeCamera(pickerSourceLatitude, pickerSourceLongitude);
                        autoCompleteFromLocation.setAlpha(1.0f);
                        autoCompleteToLocation.setAlpha(0.5f);
                        autoCompleteFromLocation.requestFocus();
                    } else {
                        String destinationLocation = getAddressFromLatLong(Objects.requireNonNull(place.getLatLng()).latitude, place.getLatLng().longitude);
                        autoCompleteToLocation.setText(destinationLocation);
                        pickerDestLatitude = place.getLatLng().latitude;
                        pickerDestLongitude = place.getLatLng().longitude;
                        didChangeCamera(pickerDestLatitude, pickerDestLongitude);
                        autoCompleteToLocation.setAlpha(1.0f);
                        autoCompleteFromLocation.setAlpha(0.5f);
                        autoCompleteToLocation.requestFocus();
                    }
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
            } else if (resultCode == RESULT_CANCELED) {
                if (ObjectUtil.isEmpty(autoCompleteToLocation.getText().toString())) {
                    didTapButton(imageViewCentermarker);
                    //Toast.makeText(ApplicationHelper.application(), "Drop location required", Toast.LENGTH_SHORT).show();
                    autoCompleteToLocation.setText("Drop location required");
                    autoCompleteToLocation.setAlpha(1.0f);
                    autoCompleteFromLocation.setAlpha(0.5f);
                    autoCompleteToLocation.requestFocus();
                }
            }

        }
    }

    private void rideLateTrip() {
        new SingleDateAndTimePickerDialog.Builder(getActivity())
                .bottomSheet()
                .curved()
                .todayText(getString(R.string.picker_today))
                .title(getString(R.string.Select_Date_And_Time))
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
                        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                        String strDate = simpleDateFormat.format(date);
                        String strTime = simpleTimeFormat.format(date);
                        String currentDate = simpleDateFormat.format(new Date());
                        String currentStrTime = simpleTimeFormat.format(new Date());
                        try {
                            Date currentDat = simpleDateFormat.parse(currentDate);
                            Date currentTime = simpleTimeFormat.parse(currentStrTime);
                            Date userDate = simpleDateFormat.parse(strDate);
                            Date userTime = simpleTimeFormat.parse(strTime);

                            if (Objects.requireNonNull(currentDat).after(userDate)) {
                                Toast.makeText(getContext(), "You can not select previous date", Toast.LENGTH_SHORT).show();
                                isClickedToPicker = false;
                            } else if ((currentDat.equals(userDate) && Objects.requireNonNull(currentTime).after(userTime))) {
                                Toast.makeText(getContext(), "You can not select previous time", Toast.LENGTH_SHORT).show();
                                isClickedToPicker = false;
                            } else if ((currentDat.equals(userDate) && Objects.requireNonNull(currentTime).equals(userTime))) {
                                Toast.makeText(getContext(), "You can not select current time", Toast.LENGTH_SHORT).show();
                                isClickedToPicker = false;
                            } else {
                                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("MM-dd-yyyy HH:mm ", Locale.getDefault());
                                String reservedTime = simpleDateFormat1.format(date);
                                //TODO open ride now screen to hit api for confirm booking
                                openRideNowScreen(true, reservedTime);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .display();
    }

    //TODO this flag is used to check when we clicked on ride now button directly without fill the destination address
    private boolean isRideNowClicked = false;

    private void openRideNowScreen(boolean isRideLater, String reserveTime) {
        Intent rideNowIntent = new Intent(ApplicationHelper.application(), ActivityRideNow.class);
        if (ObjectUtil.isNonEmptyStr(autoCompleteFromLocation.getText().toString()) && ObjectUtil.isNonEmptyStr(autoCompleteToLocation.getText().toString())) {
            rideNowIntent.putExtra("sourceAddress", autoCompleteFromLocation.getText().toString());
            rideNowIntent.putExtra("destinationAddress", autoCompleteToLocation.getText().toString());

            rideNowIntent.putExtra("sourceLatitude", pickerSourceLatitude);
            rideNowIntent.putExtra("sourceLongitude", pickerSourceLongitude);
            rideNowIntent.putExtra("destinationLatitude", pickerDestLatitude);
            rideNowIntent.putExtra("destinationLongitude", pickerDestLongitude);
            rideNowIntent.putExtra("vehicleType", nearByDriverRequest);
            //rideNowIntent.putExtra("driverId", markerHashMap);
            if (ObjectUtil.isNonEmptyStr(this.mapStyleType))
                rideNowIntent.putExtra("mapStyle", this.mapStyleType);

            if (isRideLater) {
                rideNowIntent.putExtra("isRideLater", isRideLater);
                rideNowIntent.putExtra("reserveTime", reserveTime);
            }
        }
        startActivity(rideNowIntent);
    }

    protected synchronized void buildGoogleApiClient() {
        this.googleApiClient = new GoogleApiClient.Builder(ApplicationHelper.application())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        this.googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location updatedLocation) {

   }

    private void rotateMarker(Marker marker, LatLng destination) {
        if (marker != null) {
            LatLng startPosition = marker.getPosition();
            LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, destination);
                        marker.setPosition(newPosition);
                        marker.setAnchor(0.5f, 0.5f);
                        marker.setRotation((float) getBearing(startPosition, destination));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            valueAnimator.start();
        }
    }

    //Method for finding bearing between two points
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (map != null)
            map.clear();

//        if (mqttService != null) {
//            mqttService.disconnectMqtt();
//        }

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

    @Override
    public void onCurrentItemChanged(@Nullable TaxiTypeAdapter.ViewHolder viewHolder, int adapterPosition) {

    }

    @Override
    public void onScrollStart(@NonNull TaxiTypeAdapter.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScrollEnd(@NonNull TaxiTypeAdapter.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable TaxiTypeAdapter.ViewHolder currentHolder, @Nullable TaxiTypeAdapter.ViewHolder newCurrent) {

    }

}
