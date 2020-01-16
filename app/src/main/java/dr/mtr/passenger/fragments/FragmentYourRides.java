package dr.mtr.passenger.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dr.mtr.passenger.R;
import dr.mtr.passenger.adapters.RideHistoryAdapter;
import dr.mtr.passenger.application.ApplicationHelper;
import dr.mtr.passenger.components.SessionSecuredPreferences;
import dr.mtr.passenger.dialogs.GlobalProgressDialog;
import dr.mtr.passenger.model.history.EOTripHistoryData;
import dr.mtr.passenger.model.history.EOTripHistoryRequest;
import dr.mtr.passenger.model.history.EOTripHistoryResponse;
import dr.mtr.passenger.networking.APIClient;
import dr.mtr.passenger.utils.GlobalUtil;
import dr.mtr.passenger.utils.ObjectUtil;
import dr.mtr.passenger.utils.UIUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static dr.mtr.passenger.utils.Constants.LOGIN_PREFERENCE;
import static dr.mtr.passenger.utils.Constants.PASSENGER_INFO_ID;
import static dr.mtr.passenger.utils.Constants.RESPONSE_SUCCESS;

public class FragmentYourRides extends Fragment {

    private View view;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String passengerId;
    private RecyclerView ridesHistoryRecyclerView;
    private TextView tv_no_data;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_your_rides, container, false);

        this.initView();
        this.getTripHistoryData();

        return this.view;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.passengerId = loginPreferences.getString(PASSENGER_INFO_ID, "");

        this.ridesHistoryRecyclerView = view.findViewById(R.id.ridesHistoryRecyclerView);
        this.tv_no_data = view.findViewById(R.id.tv_no_data);
    }

    private void getTripHistoryData() {
        if (!GlobalUtil.isNetworkAvailable(getActivity())) {
            UIUtil.showNetworkDialog(getActivity());
            return;
        }

        progress.showProgressBar();
        EOTripHistoryRequest tripHistoryRequest = new EOTripHistoryRequest();
        if (ObjectUtil.isNonEmptyStr(this.passengerId))
            tripHistoryRequest.setPid(this.passengerId);

        apiInterface.getTripHistory(tripHistoryRequest).enqueue(new Callback<EOTripHistoryResponse>() {
            @Override
            public void onResponse(Call<EOTripHistoryResponse> call, Response<EOTripHistoryResponse> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    EOTripHistoryResponse tripHistoryResponse = response.body();
                    if (!ObjectUtil.isEmpty(tripHistoryResponse)) {
                        if (tripHistoryResponse.getStatus().equalsIgnoreCase(RESPONSE_SUCCESS)) {
                            if (!ObjectUtil.isEmpty(tripHistoryResponse.getData())) {
                                tv_no_data.setVisibility(View.GONE);
                                ridesHistoryRecyclerView.setVisibility(View.VISIBLE);
                                ridesHistoryRecyclerView.setHasFixedSize(true);
                                ridesHistoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                ridesHistoryRecyclerView.setAdapter(new RideHistoryAdapter(getActivity(), (ArrayList<EOTripHistoryData>) tripHistoryResponse.getData()));
                            } else {
                                tv_no_data.setVisibility(View.VISIBLE);
                                ridesHistoryRecyclerView.setVisibility(View.GONE);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Response not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EOTripHistoryResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    Toast.makeText(getActivity(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
