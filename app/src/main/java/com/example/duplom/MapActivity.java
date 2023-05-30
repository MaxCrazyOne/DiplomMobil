package com.example.duplom;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.GeoObjectCollection;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.GeoObjectTapListener;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.location.FilteringMode;
import com.yandex.mapkit.location.Location;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.CompositeIcon;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.RotationType;
import com.yandex.mapkit.map.VisibleRegionUtils;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.search.Properties;
import com.yandex.mapkit.search.Response;
import com.yandex.mapkit.search.SearchFactory;
import com.yandex.mapkit.search.SearchManager;
import com.yandex.mapkit.search.SearchManagerType;
import com.yandex.mapkit.search.SearchOptions;
import com.yandex.mapkit.search.Session;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MapActivity extends AppCompatActivity implements CameraListener, UserLocationObjectListener, Session.SearchListener {

    private static final double DESIRED_ACCURACY = 0;
    private static final long MINIMAL_TIME = 1000;
    private static final double MINIMAL_DISTANCE = 1;
    private static final boolean USE_IN_BACKGROUND = false;
    public static final float COMFORTABLE_ZOOM_LEVEL = 10f;
    private final String MAPKIT_API_KEY = "3e09e9bd-1096-4416-87ea-565b629eaaed";


    private MapView mapView;

    private LocationManager locationManager;
    private LocationListener myLocationListener;

    private Point myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{ACCESS_FINE_LOCATION}, 100);
        }
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.mapview);
        MapKit mapKit = MapKitFactory.getInstance();



        locationMapKit = mapKit.createUserLocationLayer(mapView.getMapWindow());
        locationMapKit.isVisible();
        locationMapKit.setObjectListener(this);
        SearchFactory.initialize(this);
        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED);
        mapView.getMap().addCameraListener(this);
        searchEdit = findViewById(R.id.findaddress);

        searchEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                 submitQuery(searchEdit.getText().toString());
                }
                return false;
            }
        });
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Есть связб", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Нет пробития", Toast.LENGTH_LONG).show();
        }

        mapView.getMap().move(new CameraPosition(new Point(47.3,39.7),11.0f,0.0f,0.0f),
                new Animation(Animation.Type.SMOOTH,10f),null
        );


        locationManager = MapKitFactory.getInstance().createLocationManager();

        myLocationListener = new LocationListener() {
            @Override
            public void onLocationUpdated(Location location) {
                if (myLocation == null) {
                    moveCamera(location.getPosition(), COMFORTABLE_ZOOM_LEVEL);
                }
                myLocation = location.getPosition();
            }

            @Override
            public void onLocationStatusUpdated(@NonNull LocationStatus locationStatus) {

            }
        };

    }



    private void subscribeToLocationUpdate() {
        if (locationManager != null && myLocationListener != null) {
            locationManager.subscribeForLocationUpdates(
                    DESIRED_ACCURACY,
                    MINIMAL_TIME,
                    MINIMAL_DISTANCE,
                    USE_IN_BACKGROUND,
                    FilteringMode.OFF,
                    myLocationListener
            );
        }
    }

    private void moveCamera(Point point, float zoom) {
        mapView.getMap().move(
                new CameraPosition(point, zoom, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 1),
                null);

    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void getCamerasCoordinates() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            List<CameraPoint> cameraLocations = NetworkUtil.getCameraLocations();
            runOnUiThread(() -> {
                setCameraIconsOnMap(cameraLocations);
            });
        });
    }


    private void setCameraIconsOnMap(List<CameraPoint> cameraLocations) {
        Bitmap icon = getBitmapFromVectorDrawable(this, R.drawable.camera_icon);
        MapObjectCollection placemarkss = mapView.getMap().getMapObjects();
        for (CameraPoint camera : cameraLocations) {
            double latitude = camera.getLatitude();
            double longitude = camera.getLongitude();

           placemarkss.addPlacemark(new Point(latitude, longitude), ImageProvider.fromBitmap(icon)).setUserData(camera);

        }
        MapObjectTapListener mapObjectTapListener = (mapObject, point) -> {
            createCameraInfoDialog((CameraPoint) Objects.requireNonNull(mapObject.getUserData()));
            return false;
        };
        placemarkss.addTapListener(mapObjectTapListener);

    }

    private void createCameraInfoDialog(CameraPoint camera) {
        //RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.camera_dialog, null);

        RelativeLayout relativeLayout = findViewById(R.id.included);
        relativeLayout.setClipToOutline(true);
        relativeLayout.setVisibility(View.VISIBLE);


        TextView addressText = findViewById(R.id.Address);
        TextView latitudeText = findViewById(R.id.Latitude);
        TextView longitudeText = findViewById(R.id.Longitude);
        TextView placesText = findViewById(R.id.Places);

        addressText.setText("Адрес: ");
        latitudeText.setText("Широта: ");
        longitudeText.setText("Долгота: ");
        placesText.setText("Количество мест: ");


        double latitude = camera.getLatitude();
        double longitude = camera.getLongitude();




        addressText.append(camera.getAddress());
        latitudeText.append(String.valueOf(latitude));
        longitudeText.append(String.valueOf(longitude));
        placesText.append(String.valueOf(camera.getParkingCount()));

        MaterialButton okBtn =  findViewById(R.id.OKbtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayout.setVisibility(View.INVISIBLE);

            }
        });

    }


    UserLocationLayer locationMapKit;
    EditText searchEdit;
    SearchManager searchManager;
    Session searchSession;

    private void submitQuery(String query){
      searchSession = searchManager.submit(query, VisibleRegionUtils.toPolygon(mapView.getMap().getVisibleRegion()), new SearchOptions(),this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
        getCamerasCoordinates();
        subscribeToLocationUpdate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MapKitFactory.getInstance().onStop();
        locationManager.unsubscribe(myLocationListener);
        mapView.onStop();
//        getCamerasCoordinates();
//        subscribeToLocationUpdate();
    }

    @Override
    public void onCameraPositionChanged(
            @NonNull Map map,
            @NonNull CameraPosition cameraPosition,
            @NonNull CameraUpdateReason cameraUpdateReason,
            boolean finished)
    {
         if(finished){
             submitQuery(searchEdit.getText().toString());
         }
    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {
      locationMapKit.setAnchor(
              new PointF(
                      (Float.parseFloat(String.valueOf(mapView.width() *0.5))),
                      (Float.parseFloat(String.valueOf(mapView.height() *0.5)))
              ),
              new PointF(
                      (Float.parseFloat(String.valueOf(mapView.width() *0.5))),
                      (Float.parseFloat(String.valueOf(mapView.height() *0.83)))
              )
      );
        userLocationView.getArrow().setIcon(ImageProvider.fromResource(this,R.drawable.baseline_arrow_drop_up_24));
        CompositeIcon picIcon = userLocationView.getPin().useCompositeIcon();
        picIcon.setIcon("icon",ImageProvider.fromResource(this,R.drawable.pngwing),
                new IconStyle().setAnchor(new PointF(0f,0f)).
                        setRotationType(RotationType.NO_ROTATION).
                        setZIndex(0f).setScale(1f)
        );
        picIcon.setIcon("pin",ImageProvider.fromResource(this,R.drawable.not),
                new IconStyle().setAnchor(new PointF(0.5f,0.5f)).
                        setRotationType(RotationType.ROTATE).setZIndex(1f).setScale(0.5f)
                );
            userLocationView.getAccuracyCircle().setFillColor(-0x66000001);
    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {

    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent objectEvent) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    List<MapObject> placemarks = new ArrayList<>();
    @Override
    public void onSearchResponse(@NonNull Response response) {
        MapObjectCollection mapObjets = mapView.getMap().getMapObjects();


        for (int i = 0; i < placemarks.size(); i++) {
                mapObjets.remove(placemarks.get(i));
        }
        placemarks.clear();

        for (int i = 0; i < response.getCollection().getChildren().size(); i++) {

            GeoObjectCollection.Item searchResult = response.getCollection().getChildren().get(i);
            Point resultLocation = Objects.requireNonNull(searchResult.getObj()).getGeometry().get(0).getPoint();
            if (response != null) {
                assert resultLocation != null;
               MapObject mapObject = mapObjets.addPlacemark(resultLocation, ImageProvider.fromResource(this, R.drawable.pngwing));
               placemarks.add(mapObject);
            }
        }
    }

    @Override
    public void onSearchError(@NonNull Error error) {
        String errorMessage = "Error";;

    }
}