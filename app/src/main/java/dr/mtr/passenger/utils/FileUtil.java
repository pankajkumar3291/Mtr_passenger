package dr.mtr.passenger.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import dr.mtr.passenger.application.ApplicationHelper;
import dr.mtr.passenger.networking.JsonParser;

public class FileUtil {

    //TODO read content from file, pass file name
    public static String getFileContent(String fileName) {
        return streamToString(getFileFromAsset(fileName));
    }

    public static String streamToString(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferReader = new BufferedReader(inputStreamReader);
            StringBuilder fileContent = new StringBuilder();
            String str;
            while ((str = bufferReader.readLine()) != null) {
                fileContent.append(str.trim());
            }
            return fileContent.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static InputStream getFileFromAsset(String fileName) {
        try {
            if (ObjectUtil.isNonEmptyStr(fileName)) {
                return ApplicationHelper.application().getContext().getResources().getAssets().open(fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> ArrayList<T> objectListFromFile(String fileName, Class<T> entity) {
        String result = streamToString(getFileFromAsset(fileName));
        if (ObjectUtil.isNonEmptyStr(result)) {
            return JsonParser.getInstance().getList(entity, result);
        }
        return new ArrayList<>();
    }

    public static <T> T objectFromFile(String fileName, Class<T> entity) {
        String result = streamToString(getFileFromAsset(fileName));
        if (ObjectUtil.isNonEmptyStr(result)) {
            return JsonParser.getInstance().getObject(entity, result);
        }
        return null;
    }

    public static void writeFile(InputStream inputStream, File outputFile) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        writeToStream(inputStream, fileOutputStream, true);
    }

    public static void writeToStream(InputStream inputStream, OutputStream outputStream, boolean isCloseStreams) throws IOException {
        byte[] buffer = new byte[1048576]; // 1 MB Buffer
        int count;
        while ((count = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, count);
        }
        outputStream.flush();
        if (isCloseStreams) {
            outputStream.close();
            inputStream.close();
        }
    }

}
