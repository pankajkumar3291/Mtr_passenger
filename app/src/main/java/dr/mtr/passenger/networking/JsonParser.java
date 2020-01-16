package dr.mtr.passenger.networking;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Map;

import dr.mtr.passenger.utils.ObjectUtil;

public class JsonParser {

    private static JsonParser instance;
    private Gson gson;

    private JsonParser() {
        this.init();
    }

    public static synchronized JsonParser getInstance() {
        if (instance == null) {
            instance = new JsonParser();
        }
        return instance;
    }

    private void init() {
        this.gson = new GsonBuilder().create();
    }

    public String toJson(Object obj) {
        return this.gson.toJson(obj);
    }

    public <T> T getObject(Class<T> entity, Object response) {
        if (ObjectUtil.isEmpty(response)) {
            return null;
        }
        try {
            String jsonString = response instanceof Map ? this.toJson(response) : JSonHelper.toJSON(response).toString();
            return ObjectUtil.isNonEmptyStr(jsonString) ? this.gson.fromJson(jsonString, entity) : null;
        } catch (Exception e) {
            Log.e("Object Value : " + JsonParser.class.getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    public <T> ArrayList<T> getList(Class<T> entity, Object response) {
        if (ObjectUtil.isEmpty(response)) {
            return new ArrayList<>();
        }
        ArrayList<T> returnList = new ArrayList<>();
        try {
            String jsonString = response instanceof Iterable ? this.toJson(response) : JSonHelper.toJSON(response).toString();
            ArrayList<Object> list = this.gson.fromJson(jsonString, new TypeToken<ArrayList<T>>() {
            }.getType());
            for (Object obj : list) {
                returnList.add(this.getObject(entity, obj));
            }
        } catch (Exception e) {
            Log.e("List Value : " + JsonParser.class.getSimpleName(), e.getMessage(), e);
        }
        return returnList;
    }

}
