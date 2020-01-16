package dr.mtr.passenger.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import dr.mtr.passenger.R;
import dr.mtr.passenger.utils.LocalizationHelper;

public class ActivityCustomerCare extends BaseActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalizationHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_care);

        this.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO play sound on back pressed
                MediaPlayer mediaPlayer = MediaPlayer.create(ActivityCustomerCare.this, R.raw.select);
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

                ActivityCustomerCare.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
