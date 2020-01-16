package dr.mtr.passenger.networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import dr.mtr.passenger.model.activeTrip.EOActiveTripRequest;
import dr.mtr.passenger.model.activeTrip.EOActiveTripResponse;
import dr.mtr.passenger.model.cancelTrip.EOCancelTripRequest;
import dr.mtr.passenger.model.cancelTrip.EOCancelTripResponse;
import dr.mtr.passenger.model.direction.GoogleDirectionResponse;
import dr.mtr.passenger.model.fare.EOFareRequest;
import dr.mtr.passenger.model.fare.EOFareResponse;
import dr.mtr.passenger.model.firebase.EOFireBaseResponse;
import dr.mtr.passenger.model.firebase.EOFireBaseTokenRequest;
import dr.mtr.passenger.model.googleDistanceMatrix.DistanceAndTimeResponse;
import dr.mtr.passenger.model.history.EOTripHistoryRequest;
import dr.mtr.passenger.model.history.EOTripHistoryResponse;
import dr.mtr.passenger.model.login.EOLoginRequest;
import dr.mtr.passenger.model.login.EOLoginResponse;
import dr.mtr.passenger.model.nearByDriver.EONearByDriverRequest;
import dr.mtr.passenger.model.nearByDriver.EONearByDriverResponse;
import dr.mtr.passenger.model.newTrip.EONewTripRequest;
import dr.mtr.passenger.model.newTrip.EONewTripResponse;
import dr.mtr.passenger.model.rating.EORatingRequest;
import dr.mtr.passenger.model.rating.EORatingResponse;
import dr.mtr.passenger.model.signup.EOSignUpRequest;
import dr.mtr.passenger.model.signup.EOSignUpResponse;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

import static dr.mtr.passenger.utils.Constants.BASE_URL;

public class APIClient {

    public static APIInterface getClient() {

//        try {
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init((KeyStore) null);
//            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
//            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
//                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
//            }
//            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
//            SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, new TrustManager[]{trustManager}, null);
//            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
//            e.printStackTrace();
//            System.out.println("SSL Socket exception : " + e);
//        }

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                /* .connectTimeout(1, TimeUnit.MINUTES)
                 .readTimeout(1, TimeUnit.MINUTES)
                 .writeTimeout(1, TimeUnit.MINUTES)*/
                .sslSocketFactory(getSSLSocketFactory())
                .addInterceptor(interceptor).build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL).client(client)
                //.addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(APIInterface.class);
    }

    private static SSLSocketFactory getSSLSocketFactory() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            return sslContext.getSocketFactory();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    public interface APIInterface {

        //TODO this api is used for google direction to draw path, get total distance,total time from route response
//        @GET
//        Call<GoogleDirectionResponse> getDirectionPath(@Url String url);

        //TODO this api is used for google direction to draw path, get total distance,total time from route response
        @GET
        Single<GoogleDirectionResponse> getDirectionPath(@Url String url);


        //TODO this api is used of google to pick time and distance
        @GET
        Call<DistanceAndTimeResponse> getDistanceAndTimeAPI(@Url String url,
                                                            @Query("units") String units,
                                                            @Query("mode") String mode,
                                                            @Query("origins") String origin,
                                                            @Query("destinations") String destination,
                                                            @Query("key") String key);

        @POST("login.php")
        @Headers("Content-type: application/json")
        Call<EOLoginResponse> customerLogin(@Body EOLoginRequest eoLoginRequest);

        @POST("register.php")
        @Headers("Content-type: application/json")
        Call<EOSignUpResponse> customerRegistration(@Body EOSignUpRequest eoSignUpRequest);

        @POST("updatetoken.php")
        @Headers("Content-type: application/json")
        Call<EOFireBaseResponse> updateToken(@Body EOFireBaseTokenRequest fireBaseTokenRequest);

        @POST("nearbydrivers.php")
        @Headers("Content-type: application/json")
        Call<EONearByDriverResponse> nearByDrivers(@Body EONearByDriverRequest nearByDriverRequest);

        @POST("newtrip.php")
        @Headers("Content-type: application/json")
        Call<EONewTripResponse> createNewTrip(@Body EONewTripRequest tripRequest);

        @POST("history.php")
        @Headers("Content-type: application/json")
        Call<EOTripHistoryResponse> getTripHistory(@Body EOTripHistoryRequest historyRequest);

        @POST("activetrip.php")
        @Headers("Content-type: application/json")
        Call<EOActiveTripResponse> getActiveTrip(@Body EOActiveTripRequest tripRequest);

        @POST("canceltrip.php")
        @Headers("Content-type: application/json")
        Call<EOCancelTripResponse> cancelOnGoingTrip(@Body EOCancelTripRequest cancelTripRequest);

        @POST("giverating.php")
        @Headers("Content-type: application/json")
        Call<EORatingResponse> giveRatingToDriver(@Body EORatingRequest ratingRequest);

        @Headers("Content-type: application/json")
        @POST
        Call<EOFareResponse> fareCalculationForRide(@Url String url, @Body EOFareRequest fareRequest);


    }

}
