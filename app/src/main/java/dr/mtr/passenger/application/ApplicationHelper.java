package dr.mtr.passenger.application;

import com.google.gson.internal.Primitives;

public class ApplicationHelper {

    private static GlobalApplication applicationObj;


    public static GlobalApplication application() {
        return applicationObj;
    }

    public static <T> T application(Class<T> entity) {
        return Primitives.wrap(entity).cast(application());
    }

    public static void setApplicationObj(GlobalApplication applicationObj) {
        ApplicationHelper.applicationObj = applicationObj;
    }
}
