package dr.mtr.passenger.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dr.mtr.passenger.R;
import dr.mtr.passenger.model.history.EOTripHistoryData;
import dr.mtr.passenger.utils.ObjectUtil;
import dr.mtr.passenger.vehicles.VehicleItem;
import dr.mtr.passenger.vehicles.VehicleViewModel;

import static dr.mtr.passenger.utils.Constants.STATUS_CANCELLED;
import static dr.mtr.passenger.utils.Constants.STATUS_COMPLETED;
import static dr.mtr.passenger.utils.Constants.STATUS_RESERVATION;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.RideHistoryViewHolder> {

    private Context context;
    private ArrayList<EOTripHistoryData> tripHistoryArrayList;
    private List<VehicleItem> vehicleListItem;

    public RideHistoryAdapter(Context context, ArrayList<EOTripHistoryData> tripHistoryArrayList) {
        this.context = context;
        this.tripHistoryArrayList = tripHistoryArrayList;
        this.vehicleListItem = VehicleViewModel.get().getVehicles();
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RideHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_rides_history, viewGroup, false);
        return new RideHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideHistoryViewHolder viewHolder, int position) {
        EOTripHistoryData tripHistoryData = this.tripHistoryArrayList.get(position);

        viewHolder.tvPickUpAddress.setText(tripHistoryData.getPickupaddress());
        viewHolder.tvDropOffAddress.setText(tripHistoryData.getDropoffaddress());

        switch (tripHistoryData.getStatus()) {
            case STATUS_COMPLETED:
                viewHolder.tripStatusImage.setVisibility(View.GONE);
                viewHolder.tvFarePaid.setVisibility(View.VISIBLE);
                viewHolder.tvFarePaid.setText("$ ".concat(tripHistoryData.getFare()));
                break;
            case STATUS_CANCELLED:
                viewHolder.tripStatusImage.setVisibility(View.VISIBLE);
                viewHolder.tripStatusImage.setImageResource(R.drawable.ic_cancel_ride);
                viewHolder.tvFarePaid.setVisibility(View.GONE);
                break;
            case STATUS_RESERVATION:
                viewHolder.tripStatusImage.setVisibility(View.VISIBLE);
                viewHolder.tripStatusImage.setImageResource(R.drawable.ic_reservation_ride);
                viewHolder.tvFarePaid.setVisibility(View.GONE);
                break;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm aaa", Locale.getDefault());
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm aaa", Locale.getDefault());
        try {
            viewHolder.tvTripTime.setText(simpleDateFormat1.format(Objects.requireNonNull(simpleDateFormat.parse(tripHistoryData.getPickuptime()))));
        } catch (ParseException e) {
            e.printStackTrace();
            viewHolder.tvTripTime.setText(tripHistoryData.getPickuptime());
        }
        viewHolder.tvVehicleType.setText(tripHistoryData.getRequestedcar().concat(" | ").concat(tripHistoryData.getPlateno()));

        switch (tripHistoryData.getRequestedcar()) {
            case "Any":
                viewHolder.iv_car_type.setImageDrawable(context.getResources().getDrawable(R.drawable.all_car_history));
                break;
            case "Sedan":
                viewHolder.iv_car_type.setImageDrawable(context.getResources().getDrawable(R.drawable.car1));
                break;
            case "SUV":
                viewHolder.iv_car_type.setImageDrawable(context.getResources().getDrawable(R.drawable.car2));
                break;
            case "Minivan":
                viewHolder.iv_car_type.setImageDrawable(context.getResources().getDrawable(R.drawable.car3));
                break;
            /*case "Moto":
                viewHolder.iv_car_type.setImageDrawable(context.getResources().getDrawable(R.drawable.bike));
                break;*/
            default:
                viewHolder.iv_car_type.setImageDrawable(context.getResources().getDrawable(R.drawable.all_car));
        }

        Picasso.get()
                .load(tripHistoryData.getDriverimg())
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .into(viewHolder.ivDriverImage);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(this.tripHistoryArrayList.size()) ? 0 : this.tripHistoryArrayList.size();
    }

    class RideHistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTripTime, tvVehicleType, tvFarePaid, tvPickUpAddress, tvDropOffAddress;
        private CircleImageView ivDriverImage;
        private ImageView tripStatusImage, iv_car_type;

        private RideHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTripTime = itemView.findViewById(R.id.tvTripTime);
            tvVehicleType = itemView.findViewById(R.id.tvVehicleType);
            tvFarePaid = itemView.findViewById(R.id.tvFarePaid);
            tvPickUpAddress = itemView.findViewById(R.id.tvPickUpAddress);
            tvDropOffAddress = itemView.findViewById(R.id.tvDropOffAddress);
            ivDriverImage = itemView.findViewById(R.id.ivDriverImage);
            tripStatusImage = itemView.findViewById(R.id.imageView9);
            iv_car_type = itemView.findViewById(R.id.iv_car_type);
        }
    }

}
