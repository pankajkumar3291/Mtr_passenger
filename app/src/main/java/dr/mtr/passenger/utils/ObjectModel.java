package dr.mtr.passenger.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ObjectModel implements Parcelable, Cloneable, ObjectValidation {

    private static final String TAG = ObjectModel.class.getSimpleName();
    private static Comparator<Field> compareMemberByName = new CompareMemberByName();
    public static final Creator<ObjectModel> CREATOR = new Creator<ObjectModel>() {
        public ObjectModel createFromParcel(Parcel in) {
            Class<?> parceledClass;
            try {
                parceledClass = Class.forName(in.readString());
                ObjectModel model = (ObjectModel) parceledClass.newInstance();
                restoreFields(model, in);
                return model;
            } catch (Exception e) {
                Log.e("Error in creator : " + TAG, e.getMessage());
            }
            return null;
        }

        public ObjectModel[] newArray(int size) {
            return new ObjectModel[size];
        }
    };

    public ObjectModel(Parcel in) {
        String className = in.readString();
        try {
            restoreFields(this, in);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Log.e("Error in parcel : " + TAG, e.getMessage());
        }
    }

    protected ObjectModel() {
        super();
    }

    protected static void restoreFields(ObjectModel model, Parcel in) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = model.getClass().getFields();
        Arrays.sort(fields, compareMemberByName);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                field.set(model, in.readInt());
            } else if (field.getType().equals(double.class) || field.getType().equals(Double.class)) {
                field.set(model, in.readDouble());
            } else if (field.getType().equals(float.class) || field.getType().equals(Float.class)) {
                field.set(model, in.readFloat());
            } else if (field.getType().equals(long.class) || field.getType().equals(Long.class)) {
                field.set(model, in.readLong());
            } else if (field.getType().equals(String.class)) {
                field.set(model, in.readString());
            } else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                field.set(model, in.readByte() == 1);
            } else if (field.getType().equals(Date.class)) {
                Date date = new Date(in.readLong());
                field.set(model, date);
            } else if (ArrayList.class.isAssignableFrom(field.getType())) {
                in.readTypedList((ArrayList) field.get(model), ObjectModel.CREATOR);
            } else if (List.class.isAssignableFrom(field.getType())) {
                in.readTypedList((List) field.get(model), ObjectModel.CREATOR);
            } else if (Hashtable.class.isAssignableFrom(field.getType())) {
                field.set(model, in.readSerializable());
            } else if (HashMap.class.isAssignableFrom(field.getType())) {
                field.set(model, in.readHashMap(ObjectModel.class.getClassLoader()));
            } else if (Map.class.isAssignableFrom(field.getType())) {
                field.set(model, in.readHashMap(ObjectModel.class.getClassLoader()));
            } else if (ObjectModel.class.isAssignableFrom(field.getType())) {
                field.set(model, in.readParcelable(field.getType().getClassLoader()));
            } else {
                Log.e("ObjectModel", model.getClass().getSimpleName() + " - Could not read field from parcel: " + field.getName() + " (" + field.getType().toString() + ")");
            }
        }
    }

    protected static void storeFields(ObjectModel model, Parcel out, int flags) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = model.getClass().getFields();
        Arrays.sort(fields, compareMemberByName);

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                out.writeInt(model.intValueForKey(field.getName()));
            } else if (field.getType().equals(double.class) || field.getType().equals(Double.class)) {
                out.writeDouble(model.doubleValueForKey(field.getName()));
            } else if (field.getType().equals(float.class) || field.getType().equals(Float.class)) {
                out.writeFloat(model.floatValueForKey(field.getName()));
            } else if (field.getType().equals(long.class) || field.getType().equals(Long.class)) {
                out.writeLong(model.longValueForKey(field.getName()));
            } else if (field.getType().equals(String.class)) {
                out.writeString((String) field.get(model));
            } else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                out.writeByte(model.boolValueForKey(field.getName()) ? (byte) 1 : (byte) 0);
            } else if (field.getType().equals(Date.class)) {
                Date date = (Date) field.get(model);
                if (date != null) {
                    out.writeLong(date.getTime());
                } else {
                    out.writeLong(0);
                }
            } else if (ArrayList.class.isAssignableFrom(field.getType())) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Class<?> classList = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                if (Serializable.class.isAssignableFrom(classList)) {
                    out.writeSerializable((ArrayList) field.get(model));
                } else {
                    out.writeTypedList((ArrayList) field.get(model));
                }
            } else if (List.class.isAssignableFrom(field.getType())) {
                ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                Class<?> classList = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                if (Serializable.class.isAssignableFrom(classList)) {
                    out.writeSerializable((ArrayList) field.get(model));
                } else {
                    out.writeTypedList((ArrayList) field.get(model));
                }
            } else if (Hashtable.class.isAssignableFrom(field.getType())) {
                out.writeSerializable((Hashtable) field.get(model));
            } else if (HashMap.class.isAssignableFrom(field.getType())) {
                out.writeMap((HashMap) field.get(model));
            } else if (Map.class.isAssignableFrom(field.getType())) {
                out.writeMap((Map) field.get(model));
            } else if (ObjectModel.class.isAssignableFrom(field.getType())) {
                out.writeParcelable((ObjectModel) field.get(model), flags);
            } else {
                Log.e("ObjectModel ", model.getClass().getSimpleName() + " - Could not write field to parcel: " + field.getName() + " (" + field.getType().toString() + ")");
            }
        }
    }

    private static String getSignature(Method meth) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Class entity : meth.getParameterTypes()) {
            sb.append(getSignature(entity));
        }
        sb.append(")");
        sb.append(getSignature(meth.getReturnType()));
        return sb.toString();
    }

    private static String getSignature(Constructor cons) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Class entity : cons.getParameterTypes()) {
            sb.append(getSignature(entity));
        }
        sb.append(")V");
        return sb.toString();
    }

    private static String getSignature(Class clazz) {
        String type = null;
        if (clazz.isArray()) {
            Class cl = clazz;
            int dimensions = 0;
            while (cl.isArray()) {
                dimensions++;
                cl = cl.getComponentType();
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dimensions; i++) {
                sb.append("[");
            }
            sb.append(getSignature(cl));
            type = sb.toString();
        } else if (clazz.isPrimitive()) {
            if (clazz == Integer.TYPE) {
                type = "I";
            } else if (clazz == Byte.TYPE) {
                type = "B";
            } else if (clazz == Long.TYPE) {
                type = "J";
            } else if (clazz == Float.TYPE) {
                type = "F";
            } else if (clazz == Double.TYPE) {
                type = "D";
            } else if (clazz == Short.TYPE) {
                type = "S";
            } else if (clazz == Character.TYPE) {
                type = "C";
            } else if (clazz == Boolean.TYPE) {
                type = "Z";
            } else if (clazz == Void.TYPE) {
                type = "V";
            }
        } else {
            type = "L" + clazz.getName().replace('.', '/') + ";";
        }
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getClass().getName());
        try {
            storeFields(this, dest, flags);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Log.e("Error in WriteToParcel " + TAG, e.getMessage());
        }
    }

    public void init() {

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean boolValueForKey(String key) {
        return ObjectUtil.boolValue(this.valueForKey(key));
    }

    public long longValueForKey(String key) {
        return ObjectUtil.longValue(this.valueForKey(key));
    }

    public int intValueForKey(String key) {
        return ObjectUtil.intValue(this.valueForKey(key));
    }

    public double doubleValueForKey(String key) {
        return ObjectUtil.floatValue(this.valueForKey(key));
    }

    public float floatValueForKey(String key) {
        return ObjectUtil.floatValue(this.valueForKey(key));
    }

    public Object valueForKey(String key) {
        return ObjectUtil.valueForKey(key, this);
    }

    @Override
    public boolean isEmpty(Object o) {
        return ObjectUtil.isEmpty(o);
    }

    @Override
    public boolean isEmptyStr(String text) {
        return ObjectUtil.isEmptyStr(text);
    }

    @Override
    public boolean isNonEmptyStr(String text) {
        return ObjectUtil.isNonEmptyStr(text);
    }

    @Override
    public boolean isEmptyView(View v) {
        return ObjectUtil.isEmptyView(v);
    }

    @Override
    public String getTextFromView(View v) {
        return ObjectUtil.getTextFromView(v);
    }

    private static class CompareMemberByName implements Comparator {

        public int compare(Object o1, Object o2) {
            String s1 = ((Member) o1).getName();
            String s2 = ((Member) o2).getName();

            if (o1 instanceof Method) {
                s1 += getSignature((Method) o1);
                s2 += getSignature((Method) o2);
            } else if (o1 instanceof Constructor) {
                s1 += getSignature((Constructor) o1);
                s2 += getSignature((Constructor) o2);
            }
            return s1.compareTo(s2);
        }
    }

}
