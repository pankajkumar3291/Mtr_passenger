package dr.mtr.passenger.utils;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class MoverCarMarker implements MapUtils {

    private Marker marker;

    public MoverCarMarker(Marker marker) {
        this.marker = marker;
    }

    /**
     * Take the emissions from the Rx Relay as a pair of LatLng and starts the animation of
     * car on map by taking the 2 pair of LatLng's.
     *
     * @param latLngs List of LatLng emitted by Rx Relay with size two.
     */
    public void animateCarOnMap(final List<LatLng> latLngs) {
        marker.setPosition(latLngs.get(0));
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float v = valueAnimator.getAnimatedFraction();
                double lng = v * latLngs.get(1).longitude + (1 - v) * latLngs.get(0).longitude;
                double lat = v * latLngs.get(1).latitude + (1 - v) * latLngs.get(0).latitude;
                LatLng newPos = new LatLng(lat, lng);
                marker.setPosition(newPos);
                marker.setAnchor(0.5f, 0.5f);
                marker.setRotation(getBearing(latLngs.get(0), newPos));
            }
        });
        if (getDistanceInMetersNative(latLngs.get(0).latitude, latLngs.get(0).longitude, latLngs.get(1).latitude, latLngs.get(1).longitude) > 5)
            valueAnimator.start();
    }

    /**
     * Bearing between two LatLng pair
     * @param begin First LatLng Pair
     * @param end   Second LatLng Pair
     * @return The bearing or the angle at which the marker should rotate for going to {@code end} LAtLng.
     */
    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

}
