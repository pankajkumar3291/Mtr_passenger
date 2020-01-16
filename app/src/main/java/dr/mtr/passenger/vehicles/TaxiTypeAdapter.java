package dr.mtr.passenger.vehicles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dr.mtr.passenger.R;
import dr.mtr.passenger.application.ApplicationHelper;
import dr.mtr.passenger.utils.OnClickViewPagerItem;

public class TaxiTypeAdapter extends RecyclerView.Adapter<TaxiTypeAdapter.ViewHolder> {

    private RecyclerView parentRecycler;
    private Context context;
    private List<VehicleItem> data;
    private OnClickViewPagerItem homeController;

    public TaxiTypeAdapter(List<VehicleItem> data, OnClickViewPagerItem homeController) {
        this.data = data;
        this.homeController = homeController;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        parentRecycler = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_city_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int iconTint = ContextCompat.getColor(holder.itemView.getContext(), R.color.white);

        VehicleItem vehicleItem = data.get(position);
        holder.imageView.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), vehicleItem.getThree_car()));

        holder.textView.setText(vehicleItem.getAny());

        if (vehicleItem.isSelected()) {
            holder.cvMain.setBackground(ApplicationHelper.application().getResources().getDrawable(R.drawable.my_button_bg));
            holder.textView.setTextColor(ContextCompat.getColor(holder.textView.getContext(), R.color.white));
        } else {
            holder.cvMain.setBackgroundColor(ContextCompat.getColor(holder.cvMain.getContext(), R.color.white));
            holder.textView.setTextColor(ContextCompat.getColor(holder.textView.getContext(), R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView textView;
        private LinearLayout container;
        private LinearLayout cvMain;

        public ViewHolder(View itemView) {
            super(itemView);
            cvMain = itemView.findViewById(R.id.cvMain);
            imageView = itemView.findViewById(R.id.city_image);
            textView = itemView.findViewById(R.id.city_name);

            container = itemView.findViewById(R.id.container);
            itemView.findViewById(R.id.container).setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            homeController.onClick(getAdapterPosition());
            //parentRecycler.smoothScrollToPosition(getAdapterPosition());
        }
    }

}
