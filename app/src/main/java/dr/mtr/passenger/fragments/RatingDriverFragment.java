package dr.mtr.passenger.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import dr.mtr.passenger.R;
import dr.mtr.passenger.dialogs.GlobalProgressDialog;
import dr.mtr.passenger.model.rating.EORatingRequest;
import dr.mtr.passenger.model.rating.EORatingResponse;
import dr.mtr.passenger.networking.APIClient;
import dr.mtr.passenger.utils.GlobalUtil;
import dr.mtr.passenger.utils.ObjectUtil;
import dr.mtr.passenger.utils.UIUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static dr.mtr.passenger.utils.Constants.RESPONSE_SUCCESS;

public class RatingDriverFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private View view;
    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private CircleImageView ivDriverImage;
    private TextView tvDriverName;
    private RatingBar ratingBar;
    private EditText etReview;
    private Button btnCancel, btnSubmit;

    private EORatingRequest ratingRequest;

    public static RatingDriverFragment newInstance(EORatingRequest taxiRequest) {
        RatingDriverFragment fragment = new RatingDriverFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("RIDE_DATA", taxiRequest);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("RIDE_DATA"))
            ratingRequest = getArguments().getParcelable("RIDE_DATA");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.layout_driver_rating, container, false);

        this.initView();
        this.setOnClickListener();

        return this.view;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.ivDriverImage = view.findViewById(R.id.ivDriverImage);
        this.tvDriverName = view.findViewById(R.id.tvDriverName);
        this.ratingBar = view.findViewById(R.id.ratingBar);
        this.etReview = view.findViewById(R.id.etReview);
        this.btnCancel = view.findViewById(R.id.btnCancel);
        this.btnSubmit = view.findViewById(R.id.btnSubmit);
        this.setCancelable(false);
    }

    private void setOnClickListener() {
        this.btnCancel.setOnClickListener(this);
        this.btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvDriverName.setText(ratingRequest.getDriverName());
        Picasso.get().load(ratingRequest.getDriverPhoto()).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).into(ivDriverImage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                //TODO when ride is cancel then play flush sound
                MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.flush);
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

                this.dismiss();
                getActivity().finish();
                break;
            case R.id.btnSubmit:
                this.submitReviewOnServer();
                break;
        }
    }

    private void submitReviewOnServer() {
        if (ObjectUtil.isEmptyStr(ObjectUtil.getTextFromView(etReview))) {
            Toast.makeText(getActivity(), "Please enter comment.", Toast.LENGTH_SHORT).show();
        } else if (ratingBar.getRating() == 0.0) {
            Toast.makeText(getActivity(), "Please select number of star.", Toast.LENGTH_SHORT).show();
        } else {

            if (!GlobalUtil.isNetworkAvailable(getActivity())) {
                UIUtil.showNetworkDialog(getActivity());
                return;
            }

            this.progress.showProgressBar();
            ratingRequest.setComment(ObjectUtil.getTextFromView(etReview));
            ratingRequest.setRating(ratingBar.getRating());
            apiInterface.giveRatingToDriver(ratingRequest).enqueue(new Callback<EORatingResponse>() {
                @Override
                public void onResponse(Call<EORatingResponse> call, Response<EORatingResponse> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EORatingResponse ratingResponse = response.body();
                        if (!ObjectUtil.isEmpty(ratingResponse)) {
                            if (ratingResponse.getStatus().equalsIgnoreCase(RESPONSE_SUCCESS)) {

                                //TODO when ride is cancel then play flush sound
                                MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.flush);
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

                                dismiss();
                                Toast.makeText(getActivity(), ratingResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            } else {
                                Toast.makeText(getActivity(), ratingResponse.getStatus(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EORatingResponse> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


}
