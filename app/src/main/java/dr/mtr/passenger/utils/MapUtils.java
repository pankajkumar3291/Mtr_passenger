package dr.mtr.passenger.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dr.amalbit.trail.MapAnimator;
import dr.mtr.passenger.R;
import dr.mtr.passenger.model.direction.Route;
import dr.mtr.passenger.model.direction.RouteDecode;
import dr.mtr.passenger.model.direction.Step;

public interface MapUtils {

    default void showMarkers(LatLng location, float distance, List<Marker> markers) {
        for (Marker marker : markers) {
            if (SphericalUtil.computeDistanceBetween(marker.getPosition(), location) <= distance) {
                if (!marker.isVisible()) {
                    ObjectAnimator.ofFloat(marker, "alpha", 0f, 1f).setDuration(500).start();
                }
                marker.setVisible(true);
            } else {
                if (marker.isVisible()) {
                    Animator animator = ObjectAnimator.ofFloat(marker, "alpha", 1f, 0f).setDuration(500);
                    animator.addListener(new Animator.AnimatorListener(){
                        @Override
                        public void onAnimationEnd(Animator animator){
                            marker.setVisible(false);
                        }

                        @Override
                        public void onAnimationStart(Animator animator){
                        }

                        @Override
                        public void onAnimationCancel(Animator animator){
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator){
                        }
                    });
                    animator.setDuration(500).start();
                }
            }
        }
    }

    default boolean showMarkers(LatLng from, LatLng to, float distance){
        return SphericalUtil.computeDistanceBetween(from, to) <= distance;
    }

    default LatLngBounds toBounds(LatLng center, double radiusInMeters){
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    default BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        Objects.requireNonNull(background).setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        Objects.requireNonNull(vectorDrawable).draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    default Bitmap createBitmapFromShape(@ApplicationContext Context context, @DrawableRes int drawable) {
        int px = context.getResources().getDimensionPixelSize(R.dimen.bikeshare_small_marker_size);
        Bitmap bitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        Drawable shape = ContextCompat.getDrawable(context, drawable);
        Objects.requireNonNull(shape).setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        shape.draw(c);
        return bitmap;
    }

    /**
     * Zooms a Route (given a List of LalLng) at the greatest possible zoom level.
     *
     * @param googleMap:      instance of GoogleMap
     * @param lstLatLngRoute: list of LatLng forming Route
     */
    static void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {
        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);
        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();
        googleMap.setPadding(0, 200, 0, 300);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }

    default double getDistanceInMetersNative(double lat1, double lon1, double lat2, double lon2) {
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(lat1);
        startPoint.setLongitude(lon1);
        Location endPoint = new Location("locationA");
        endPoint.setLatitude(lat2);
        endPoint.setLongitude(lon2);
        return startPoint.distanceTo(endPoint);
    }

    default String addressLineToShortAddress(Address address) {
        String addressTemp = address.getAddressLine(0);
        String[] arr = addressTemp.split(",");
        List<String> list = new ArrayList<>(Arrays.asList(arr));
        switch (list.size()) {
            case 3:
                list.remove(list.size() - 1);
                break;
            case 4:
                list.remove(list.size() - 1);
                list.remove(list.size() - 1);
                break;
            case 5:
                list.remove(list.size() - 1);
                list.remove(list.size() - 1);
                break;
            case 6:
                list.remove(list.size() - 1);
                list.remove(list.size() - 1);
                break;
        }
        return android.text.TextUtils.join(",", list);
    }

    default void didTapButton(View view) {
        final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.7, 20);
        myAnim.setInterpolator(interpolator);
        view.startAnimation(myAnim);
    }

    class MyBounceInterpolator implements android.view.animation.Interpolator {
        private double mAmplitude = 1;
        private double mFrequency = 10;

        MyBounceInterpolator(double amplitude, double frequency) {
            mAmplitude = amplitude;
            mFrequency = frequency;
        }

        public float getInterpolation(float time) {
            return (float) (-1 * Math.pow(Math.E, -time / mAmplitude) * Math.cos(mFrequency * time) + 1);
        }
    }

    default void setRouteAfterRideNowIfRouteFound(Context context, List<Route> routelistForPolyLine, GoogleMap map, List<Polyline> polylineList, List<Marker> markerListOtherThanTaxis) {
        MarkerOptions markerOptionsStart = new MarkerOptions().icon(bitmapDescriptorFromVector(context, R.drawable.picloc1));
        MarkerOptions markerOptionsEnd = new MarkerOptions().icon(bitmapDescriptorFromVector(context, R.drawable.droploc1));
        ArrayList<LatLng> routelist = new ArrayList<>();
        if (routelistForPolyLine.size() > 0) {
            ArrayList<LatLng> decodelist;
            Route routeA = routelistForPolyLine.get(0);
            if (routeA.getLegs().size() > 0) {
                List<Step> steps = routeA.getLegs().get(0).getSteps();
                Step step;
                dr.mtr.passenger.model.direction.Location location;
                String polyline;
                for (int i = 0; i < steps.size(); i++) {
                    step = steps.get(i);
                    location = step.getStartLocation();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                    polyline = step.getPolyline().getPoints();
                    decodelist = RouteDecode.decodePoly(polyline);
                    routelist.addAll(decodelist);
                    location = step.getEndLocation();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                }
            }
        }
        if (routelist.size() > 0) {
            PolylineOptions rectLine = new PolylineOptions().width(8).color(ContextCompat.getColor(context, R.color.colorPrimary));
            for (int i = 0; i < routelist.size(); i++){
                rectLine.add(routelist.get(i));
            }
            markerOptionsStart.position(routelist.get(0));
            markerOptionsEnd.position(routelist.get(routelist.size() - 1));
            markerListOtherThanTaxis.add(map.addMarker(markerOptionsStart));
            markerListOtherThanTaxis.add(map.addMarker(markerOptionsEnd));
            if (routelist.isEmpty()) return;
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (LatLng latLngPoint : routelist)
                boundsBuilder.include(latLngPoint);
            int routePadding = 50;
            LatLngBounds latLngBounds = boundsBuilder.build();
            map.setPadding(routePadding, routePadding, routePadding, routePadding);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
            MapAnimator.getInstance().animateRoute(map, routelist, polylineList);
        }
    }

    default void setRouteFromDriverToPickUp(Context context, List<Route> routelistForPolyLine, GoogleMap map, List<Polyline> polylineList, List<Marker> markerListOtherThanTaxis) {
        MarkerOptions markerOptionsStart = new MarkerOptions().icon(bitmapDescriptorFromVector(context, R.drawable.uber_car));
        MarkerOptions markerOptionsEnd = new MarkerOptions().icon(bitmapDescriptorFromVector(context, R.drawable.picloc1));
        ArrayList<LatLng> routelist = new ArrayList<>();
        if (routelistForPolyLine.size() > 0) {
            ArrayList<LatLng> decodelist;
            Route routeA = routelistForPolyLine.get(0);
            if (routeA.getLegs().size() > 0) {
                List<Step> steps = routeA.getLegs().get(0).getSteps();
                Step step;
                dr.mtr.passenger.model.direction.Location location;
                String polyline;
                for (int i = 0; i < steps.size(); i++) {
                    step = steps.get(i);
                    location = step.getStartLocation();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                    polyline = step.getPolyline().getPoints();
                    decodelist = RouteDecode.decodePoly(polyline);
                    routelist.addAll(decodelist);
                    location = step.getEndLocation();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                }
            }
        }
        if (routelist.size() > 0) {
            PolylineOptions rectLine = new PolylineOptions().width(8).color(ContextCompat.getColor(context, R.color.colorPrimary));
            for (int i = 0; i < routelist.size(); i++) {
                rectLine.add(routelist.get(i));
            }
            // Adding route on the map
            MapAnimator.getInstance().animateRoute(map, routelist, polylineList);
            markerOptionsStart.position(routelist.get(0));
            markerOptionsEnd.position(routelist.get(routelist.size() - 1));
            markerListOtherThanTaxis.add(map.addMarker(markerOptionsStart));
            markerListOtherThanTaxis.add(map.addMarker(markerOptionsEnd));

            if (routelist.isEmpty()) return;
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (LatLng latLngPoint : routelist)
                boundsBuilder.include(latLngPoint);
            int routePadding = 50;
            LatLngBounds latLngBounds = boundsBuilder.build();
            map.setPadding(routePadding, routePadding, routePadding, routePadding);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
        }
    }

    default void setRouteFromPickUpToDropOff(Context context, List<Route> routelistForPolyLine, GoogleMap map, List<Polyline> polylineList, List<Marker> markerListOtherThanTaxis) {
        MarkerOptions markerOptionsStart = new MarkerOptions().icon(bitmapDescriptorFromVector(context, R.drawable.uber_car));
        MarkerOptions markerOptionsEnd = new MarkerOptions().icon(bitmapDescriptorFromVector(context, R.drawable.droploc1));
        ArrayList<LatLng> routelist = new ArrayList<>();
        if (routelistForPolyLine.size() > 0) {
            ArrayList<LatLng> decodelist;
            Route routeA = routelistForPolyLine.get(0);
            if (routeA.getLegs().size() > 0) {
                List<Step> steps = routeA.getLegs().get(0).getSteps();
                Step step;
                dr.mtr.passenger.model.direction.Location location;
                String polyline;
                for (int i = 0; i < steps.size(); i++) {
                    step = steps.get(i);
                    location = step.getStartLocation();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                    polyline = step.getPolyline().getPoints();
                    decodelist = RouteDecode.decodePoly(polyline);
                    routelist.addAll(decodelist);
                    location = step.getEndLocation();
                    routelist.add(new LatLng(location.getLat(), location.getLng()));
                }
            }
        }
        if (routelist.size() > 0) {
            PolylineOptions rectLine = new PolylineOptions().width(8).color(ContextCompat.getColor(context, R.color.colorPrimary));
            for (int i = 0; i < routelist.size(); i++) {
                rectLine.add(routelist.get(i));
            }
            // Adding route on the map
            MapAnimator.getInstance().animateRoute(map, routelist, polylineList);
            markerOptionsStart.position(routelist.get(0));
            markerOptionsEnd.position(routelist.get(routelist.size() - 1));
            markerListOtherThanTaxis.add(map.addMarker(markerOptionsStart));
            markerListOtherThanTaxis.add(map.addMarker(markerOptionsEnd));
            if (routelist.isEmpty()) return;
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            for (LatLng latLngPoint : routelist)
                boundsBuilder.include(latLngPoint);
            int routePadding = 50;
            LatLngBounds latLngBounds = boundsBuilder.build();
            map.setPadding(routePadding, routePadding, routePadding, routePadding);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
        }

    }

    default boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : Objects.requireNonNull(manager).getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
