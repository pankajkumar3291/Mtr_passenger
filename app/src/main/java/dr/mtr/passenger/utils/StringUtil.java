package dr.mtr.passenger.utils;


import dr.mtr.passenger.application.ApplicationHelper;

/**
 * Created by android on 2/2/19.
 */

public class StringUtil {

    public static String getStringForID(int id) {
        return ApplicationHelper.application().getResources().getString(id);
    }

    public static String getStringForID(int id, Object... formatArgs) {
        return ApplicationHelper.application().getResources().getString(id, formatArgs);
    }

    public static int getIdForName(String name) {
        return ApplicationHelper.application().getResourceId(name, "string");
    }

    public static int getIdForName(String name, String packageName) {
        return ApplicationHelper.application().getResourceId(name, "string", packageName);
    }

}
