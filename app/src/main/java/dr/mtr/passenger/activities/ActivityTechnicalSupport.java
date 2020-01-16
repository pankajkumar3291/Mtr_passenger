package dr.mtr.passenger.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import dr.mtr.passenger.R;
import dr.mtr.passenger.utils.LocalizationHelper;

public class ActivityTechnicalSupport extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalizationHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technical_support);

        this.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO play sound on back pressed
                MediaPlayer mediaPlayer = MediaPlayer.create(ActivityTechnicalSupport.this, R.raw.select);
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

                ActivityTechnicalSupport.this.finish();
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
