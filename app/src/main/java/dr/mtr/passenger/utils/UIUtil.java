package dr.mtr.passenger.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import dr.mtr.passenger.R;
import dr.mtr.passenger.application.ApplicationHelper;
import dr.mtr.passenger.dialogs.NetworkDialog;


public class UIUtil {

    public static void showToast(String msg) {
        showToast(ApplicationHelper.application().getContext(), msg);
    }

    public static void showToast(Context context, String msg) {
        final Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 1500);
    }

    private static Window getWindow() {
        AppCompatActivity activity = (AppCompatActivity) ApplicationHelper.application().getContext();
        if (activity == null)
            return null;
        return activity.getWindow();
    }

    public static void hideKeyBoard(Context context, View v) {
        if (v == null) {
            return;
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void hideKeyboard(Activity activity) {
        hideKeyBoard(activity, activity.getCurrentFocus());
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null && activity.getCurrentFocus() != null) {
            hideKeyBoard(activity, activity.getCurrentFocus());
        } else {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

    public static void hideKeyBoardFromDialog(Context context, Dialog view) {
        hideKeyBoard(context, view.getCurrentFocus());
    }

    //TODO this dialog will show when internet connection not available
    public static void showNetworkDialog(Context context) {
        NetworkDialog networkDialog = new NetworkDialog(context);
        networkDialog.show();
    }

    public static Animation animBlink() {
        return AnimationUtils.loadAnimation(ApplicationHelper.application().getContext(), R.anim.blink_anim);
    }

    public static float dimenInDip(int size, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size, context.getResources().getDisplayMetrics());
    }

    public static void cancelAnimation(TextView view) {
        if (view == null || view.getAnimation() == null) {
            return;
        }
        view.getAnimation().cancel();
        view.clearAnimation();
        view.setAnimation(null);
    }

    public static void startAnimation(TextView view) {
        if (view == null || view.getAnimation() != null) {
            return;
        }
        view.startAnimation(animBlink());
    }

    public static Drawable getDrawable(@DrawableRes int drawableResId) {
        return ContextCompat.getDrawable(ApplicationHelper.application().getBaseContext(), drawableResId);
    }

    //    public static Drawable getDrawable(String drawableResName) {
    //        return getDrawable(FNResources.drawable.get(drawableResName));
    //    }

    public static int getColor(@ColorRes int colorId) {
        return getColor(ApplicationHelper.application().getBaseContext(), colorId);
    }

    public static int getColor(Context context, @ColorRes int colorId) {
        try {
            return ContextCompat.getColor(context, colorId);
        } catch (Exception e) {
            return colorId;
        }
    }

    /**
     * Sets Background {@link Drawable} to any given view. Checks for the SDK
     * version and calls the appropriate method. This method is made to handle
     * the deprecation issue of setBackgroudDrawable method.
     *
     * @param view
     * @param drawable
     */
    public static void setBackground(Drawable drawable, View view) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setBackgroundOld(view, drawable);
        } else {
            setBackgroundNew(view, drawable);
        }
    }

    public static void setBackground(View v) {
        setBackground(null, v);
    }

    public static void setBackground(@DrawableRes int resId, View v) {
        setBackground(getDrawable(resId), v);
    }

    @SuppressWarnings("deprecation")
    private static void setBackgroundOld(View view, Drawable drawable) {
        view.setBackgroundDrawable(drawable);
    }

    @SuppressLint("NewApi")
    private static void setBackgroundNew(View view, Drawable drawable) {
        view.setBackground(drawable);
    }

    public static void setBackground(View v, @ColorRes int bgColor, @ColorRes int strokeColor, int strokeWidth, float[] radii, GlobalEnum shape) {
        GradientDrawable drawable = getShape(getColor(bgColor), getColor(strokeColor), strokeWidth, radii, shape);
        setBackground(drawable, v);
    }

    public static void setBackgroundRect(View v, @ColorRes int bgColor, @ColorRes int strokeColor, int strokeWidth, float radius) {
        setBackground(v, bgColor, strokeColor, strokeWidth, new float[]{radius}, GlobalEnum.RECT_SHAPE);
    }

    public static void setBackgroundRect(View v, @ColorRes int bgColor, float radius) {
        GradientDrawable drawable = getRectShape(getColor(bgColor), radius);
        setBackground(drawable, v);
    }

    public static void setBackgroundRound(View v, @ColorRes int bgColor, @ColorRes int strokeColor, int strokeWidth, float[] radii) {
        setBackground(v, bgColor, strokeColor, strokeWidth, radii, GlobalEnum.ROUND_CORNER);
    }

    public static void setBackgroundRound(View v, @ColorRes int bgColor, float[] radii) {
        GradientDrawable drawable = getRoundCorner(getColor(bgColor), radii);
        setBackground(drawable, v);
    }

    public static void setBackgroundOval(View v, @ColorRes int bgColor, @ColorRes int strokeColor, int strokeWidth) {
        GradientDrawable drawable = getOvalShape(getColor(bgColor), getColor(strokeColor), strokeWidth);
        setBackground(drawable, v);
    }

    public static void setBackgroundOval(View v, @ColorInt int bgColor, @ColorInt int strokeColor) {
        GradientDrawable drawable = getOvalShape(bgColor, strokeColor, 0);
        setBackground(drawable, v);
    }

    public static void setBackgroundOval(View v, @ColorRes int bgColor) {
        setBackgroundOval(v, bgColor, bgColor, 0);
    }

    public static void setBackgroundRing(View v, @ColorRes int bgColor, @ColorRes int strokeColor, int strokeWidth) {
        GradientDrawable drawable = getRingShape(getColor(bgColor), getColor(strokeColor), strokeWidth);
        setBackground(drawable, v);
    }

    public static void setBackgroundRing(View v, @ColorInt int bgColor, @ColorInt int strokeColor) {
        GradientDrawable drawable = getRingShape(bgColor, strokeColor, 0);
        setBackground(drawable, v);
    }

    public static void setBackgroundRing(View v, @ColorRes int bgColor) {
        setBackgroundRing(v, bgColor, bgColor, 0);
    }

    public static GradientDrawable getShape(@ColorInt int bgColor, @ColorInt int strokeColor, int strokeWidth, float[] radii, GlobalEnum shape) {
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{});
        gradientDrawable.setStroke(strokeWidth, strokeColor);
        gradientDrawable.setColor(bgColor);
        switch (shape) {
            case RECT_SHAPE:
                gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                float radius = radii[0];
                gradientDrawable.setCornerRadius(radius);
                break;
            case ROUND_CORNER:
                gradientDrawable.setCornerRadii(radii);
                break;
            case OVAL_SHAPE:
                gradientDrawable.setShape(GradientDrawable.OVAL);
                break;
            case RING_SHAPE:
                gradientDrawable.setShape(GradientDrawable.RING);
                break;
        }
        return gradientDrawable;
    }

    public static GradientDrawable getRectShape(@ColorInt int bgColor, @ColorInt int strokeColor, int strokeWidth, float radius) {
        return getShape(bgColor, strokeColor, strokeWidth, new float[]{radius}, GlobalEnum.RECT_SHAPE);
    }

    public static GradientDrawable getRectShape(@ColorInt int bgColor, float radius) {
        return getShape(bgColor, Color.TRANSPARENT, 2, new float[]{radius}, GlobalEnum.RECT_SHAPE);
    }

    public static GradientDrawable getRoundCorner(@ColorInt int bgColor, @ColorInt int strokeColor, int strokeWidth, float[] radii) {
        return getShape(bgColor, strokeColor, strokeWidth, radii, GlobalEnum.ROUND_CORNER);
    }

    public static GradientDrawable getRoundCorner(@ColorInt int bgColor, float[] radii) {
        return getShape(bgColor, Color.TRANSPARENT, 0, radii, GlobalEnum.ROUND_CORNER);
    }

    public static GradientDrawable getOvalShape(@ColorInt int bgColor, @ColorInt int strokeColor, int strokeWidth) {
        return getShape(bgColor, strokeColor, strokeWidth, new float[]{0}, GlobalEnum.OVAL_SHAPE);
    }

    public static GradientDrawable getOvalShape(@ColorInt int bgColor) {
        return getShape(bgColor, Color.TRANSPARENT, 0, new float[]{0}, GlobalEnum.OVAL_SHAPE);
    }

    public static GradientDrawable getRingShape(@ColorInt int bgColor, @ColorInt int strokeColor, int strokeWidth) {
        return getShape(bgColor, strokeColor, strokeWidth, new float[]{0}, GlobalEnum.RING_SHAPE);
    }

    public static GradientDrawable getRingShape(@ColorInt int bgColor) {
        return getShape(bgColor, Color.TRANSPARENT, 0, new float[]{0}, GlobalEnum.RING_SHAPE);
    }

    public static int getHeight(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getConfiguration().screenWidthDp;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getConfiguration().screenHeightDp;
    }

    public static void enableTouch() {
        if (getWindow() == null) {
            return;
        }
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void disableTouch() {
        if (getWindow() == null) {
            return;
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void showKeyBoard(Activity context, View view) {
        if (view == null) {
            return;
        }
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static float getDimension(@DimenRes int dimenId) {
        return ApplicationHelper.application().getResources().getDimension(dimenId);
    }

    public static int getDimensionInt(@DimenRes int dimenId) {
        return (int) getDimension(dimenId);
    }

}
