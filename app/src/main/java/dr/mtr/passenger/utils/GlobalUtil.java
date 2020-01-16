package dr.mtr.passenger.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dr.mtr.passenger.R;
import dr.mtr.passenger.application.ApplicationHelper;

public class GlobalUtil {

    private static final String TAG = GlobalUtil.class.getSimpleName();

    public static boolean isAlpha(String name) {
        return ObjectUtil.isNonEmptyStr(name) && name.matches("[a-zA-Z]+");
    }

    /**
     * @param emailId
     * @return true if emailID is in correct format
     */
    public static boolean isValidEmail(String emailId) {
        return ObjectUtil.isNonEmptyStr(emailId) && Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
    }

    /**
     * This is used to check the given URL is valid or not.
     *
     * @param url
     * @return true if url is valid, false otherwise.
     */
    public static boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    public static String encodeObjectTobase64(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Bitmap) {
            return encodeTobase64((Bitmap) obj);
        } else if (obj instanceof File) {
            return encodeFileTobase64((File) obj);
        }
        return obj.toString();
    }

    public static String encodeTobase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static String encodeFileTobase64(File file) {
        try {
            return Base64.encodeToString(FileUtils.readFileToByteArray(file), Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static String decodeStringFromBase64(String input) {
        byte[] decodedByte = Base64.decode(input, Base64.DEFAULT);
        return new String(decodedByte);
    }


    public static int getDipFromPixel(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Network Not Available !!!");
        }
        return false;
    }

    //TODO this method is used in phone number field to separate as a hyphen/strip
    public static String stripSeparators(String phoneNumber) {
        String returnStr = "";
        if (ObjectUtil.isEmptyStr(phoneNumber)) {
            return returnStr;
        }
        for (Character c : phoneNumber.toCharArray()) {
            if (Character.isDigit(c) || c.toString().equals("+")) {
                returnStr += c;
            }
        }
        return returnStr;
    }

    public static String appNameWithoutSpace(Context context) {
        return appName(context).replaceAll(" ", "");
    }

    public static String appName(Context context) {
        return context.getString(R.string.app_name);
    }

    public static String to2Decimal(double value, double divider) {
        DecimalFormat df = new DecimalFormat("####0.00");
        return df.format(value / divider);
    }

    public static String to2Decimal(double value) {
        return to2Decimal(value, 100.0);
    }

    public static String formatPhoneNumber(String phoneNumber) {
        if (ObjectUtil.isEmptyStr(phoneNumber)) {
            return phoneNumber;
        }
        String formattedNumber = "";
        int startPosition = 0;
        int endPosition = 0;
        for (Character c : ApplicationHelper.application().phoneFormat().toCharArray()) {
            if (startPosition >= phoneNumber.length()) {
                break;
            }
            if (Character.isLetter(c)) {
                endPosition = startPosition + 1;
                formattedNumber += phoneNumber.substring(startPosition, endPosition);
                startPosition++;
                continue;
            }
            formattedNumber += phoneNumber.substring(startPosition, endPosition) + c;
        }
        if (phoneNumber.length() > endPosition) {
            formattedNumber += phoneNumber.substring(endPosition, phoneNumber.length());
        }
        return formattedNumber;
    }


    public static Long stringToNumber(String numberStr) {
        try {
            return Long.parseLong(ObjectUtil.isNonEmptyStr(numberStr) ? numberStr : "0");
        } catch (Exception e1) {
            return 0L;
        }
    }

    public static Float stringToFloat(String numberStr) {
        try {
            return Float.parseFloat(ObjectUtil.isNonEmptyStr(numberStr) ? numberStr : "0");
        } catch (Exception e1) {
            return 0f;
        }
    }

    public static Double stringToDouble(String numberStr) {
        try {
            return Double.parseDouble(ObjectUtil.isNonEmptyStr(numberStr) ? numberStr : "0");
        } catch (Exception e1) {
            return 0.0;
        }
    }

    public static void addCalendarEvent(Activity activity, Timestamp startTime, Timestamp endTime, String title) {
        long beginTimeMs = new Timestamp(startTime.getTime()).getTime();
        long endTimeMs = new Timestamp(endTime.getTime()).getTime();
        if (Build.VERSION.SDK_INT >= 17) {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData(CalendarContract.Events.CONTENT_URI);
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTimeMs);
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTimeMs);
            intent.putExtra(CalendarContract.Events.TITLE, title);
            activity.startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", beginTimeMs);
            intent.putExtra("endTime", endTimeMs);
            intent.putExtra("title", title);
            activity.startActivity(intent);
        }
    }

    public static void doCall(Context context, long phoneNumber) {
        doCall(context, String.valueOf(phoneNumber));
    }

    @SuppressWarnings("MissingPermission")
    public static void doCall(Context context, String phoneNumber) {
        if (ObjectUtil.isNonEmptyStr(phoneNumber)) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            context.startActivity(intent);
        }
    }

    public static String capitalize(String s) {
        if (ObjectUtil.isEmptyStr(s)) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    //TODO to check device is tablet or not
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String formatCurrency(String amountStr, boolean convertToDollars) {
        return formatCurrency(amountStr, convertToDollars, false);
    }

    public static String formatCurrency(String amountStr, boolean convertToDollars, boolean returnZero) {
        BigDecimal parsed = new BigDecimal(0);
        try {
            parsed = new BigDecimal(amountStr).setScale(2, BigDecimal.ROUND_FLOOR);
            if (convertToDollars) {
                parsed = parsed.divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (returnZero || parsed.floatValue() > 0) ? currencyFormat().format(parsed) : "";
    }


    public static NumberFormat currencyFormat() {
        Locale locale = new Locale(Locale.getDefault().getLanguage());
        Currency currencyInstance = Currency.getInstance(locale);
        NumberFormat currenyFormat = NumberFormat.getCurrencyInstance(locale);
        currenyFormat.setCurrency(currencyInstance);
        return currenyFormat;
    }

    public static Number currencyToNumber(String amountStr) {
        try {
            return currencyFormat().parse(amountStr);
        } catch (ParseException e) {
            try {
                return Float.parseFloat(ObjectUtil.isNonEmptyStr(amountStr) ? amountStr : "0");
            } catch (Exception e1) {
            }
        }
        return 0;
    }

}
