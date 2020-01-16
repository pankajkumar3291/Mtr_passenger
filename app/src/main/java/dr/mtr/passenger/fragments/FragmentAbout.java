package dr.mtr.passenger.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import dr.mtr.passenger.R;
import dr.mtr.passenger.activities.ActivityPrivacyPolicy;

public class FragmentAbout extends Fragment {

    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_about, container, false);

        this.view.findViewById(R.id.tv_privacy_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), ActivityPrivacyPolicy.class));
            }
        });

        return this.view;
    }

}
