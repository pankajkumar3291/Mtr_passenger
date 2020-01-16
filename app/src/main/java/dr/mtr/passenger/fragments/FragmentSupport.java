package dr.mtr.passenger.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import dr.mtr.passenger.R;
import dr.mtr.passenger.activities.ActivityCustomerCare;
import dr.mtr.passenger.activities.ActivityTechnicalSupport;

public class FragmentSupport extends Fragment implements View.OnClickListener {

    private View view;
    private TextView tv_technical_support, tv_customer_care;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_support, container, false);

        this.initView();
        this.setOnClickListener();

        return this.view;
    }

    private void initView() {
        this.tv_technical_support = view.findViewById(R.id.tv_technical_support);
        this.tv_customer_care = view.findViewById(R.id.tv_customer_care);
    }

    private void setOnClickListener() {
        this.tv_technical_support.setOnClickListener(this);
        this.tv_customer_care.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_technical_support:
                this.getActivity().startActivity(new Intent(getActivity(), ActivityTechnicalSupport.class));
                break;
            case R.id.tv_customer_care:
                this.getActivity().startActivity(new Intent(getActivity(), ActivityCustomerCare.class));
                break;
        }
    }

}
