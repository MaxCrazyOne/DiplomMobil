package com.example.duplom;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtil {

    private static final String CAMERA_API_URL = "https://642590f69e0a30d92b35b3d6.mockapi.io/camera";

    public static List<CameraPoint> getCameraLocations() {

        List<CameraPoint> cameraLocations = new ArrayList<>();

        try {
            URL cameraEndpoint = new URL(CAMERA_API_URL + "/cameras");
            HttpsURLConnection myConnection = (HttpsURLConnection) cameraEndpoint.openConnection();
            if (myConnection.getResponseCode() == 200) {
                InputStream responseBody = myConnection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
                JsonReader jsonReader = new JsonReader(responseBodyReader);
                jsonReader.beginArray();
                while (jsonReader.hasNext()){
                    cameraLocations.add(readCameraPoint(jsonReader));
                }
                jsonReader.endArray();
                jsonReader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return cameraLocations;
    }

    private static CameraPoint readCameraPoint(JsonReader reader) throws IOException {
        int id = 0;
        String address = null;
        double longitude = 0;
        double latitude = 0;
        int parkingCount = 0;

        reader.beginObject();
        while (reader.hasNext()) {
            String key = reader.nextName();
            switch (key) {
                case "id":
                    id = reader.nextInt();
                    break;
                case "address":
                    address = reader.nextString();
                    break;
                case "latitude":
                    latitude = reader.nextDouble();
                    break;
                case "longitude":
                    longitude = reader.nextDouble();
                    break;
                case "parkingCount":
                    parkingCount = reader.nextInt();
                    break;
            }

        }
        reader.endObject();
        CameraPoint point = new CameraPoint(id, address, latitude, longitude, parkingCount);
        return point;

    }

}
