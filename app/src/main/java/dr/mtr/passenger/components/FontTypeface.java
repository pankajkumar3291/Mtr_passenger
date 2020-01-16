package dr.mtr.passenger.components;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by android on 6/2/19.
 */

public class FontTypeface {

    private static FontTypeface setTypeface = null;

    private Typeface englishTypeface;
    private Typeface arabicTypeface;

    /**
     * <h2>AppTypeface</h2>
     *
     * @param context: calling activity reference
     * @return: Single instance of this class
     */
    public static FontTypeface getInstance(Context context) {
        if (setTypeface == null) {
            setTypeface = new FontTypeface(context.getApplicationContext());
        }
        return setTypeface;
    }

    private FontTypeface(Context context) {
        initTypefaces(context);
    }

    /**
     * <h2>initTypefaces</h2>
     * <p>
     * method to initializes the typefaces of the app
     * </p>
     *
     * @param context Context of the activity from where it is called
     */
    private void initTypefaces(Context context) {
        this.englishTypeface = Typeface.createFromAsset(context.getAssets(), "gill_sans_mt.ttf");
        this.arabicTypeface = Typeface.createFromAsset(context.getAssets(), "ge_ss_two_Light_arabic.otf");
    }

    //this method will called when user want to select english font
    public Typeface getEnglishTypeface() {
        return englishTypeface;
    }

    //this method is called when user want to select arabic font
    public Typeface getArabicTypeface() {
        return arabicTypeface;
    }


}
