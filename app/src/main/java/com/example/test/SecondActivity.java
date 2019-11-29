package com.example.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.example.test.util.AMapUtil;
import com.example.test.util.BusRouteOverlay;
import com.example.test.util.DrivingRouteOverlay;
import com.example.test.util.ToastUtil;
import com.example.test.util.WalkRouteOverlay;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.mode;
import static com.example.test.R.id.check_location;
import static com.example.test.R.id.run;

//import static com.example.test.R.id.put;

public class SecondActivity extends AppCompatActivity implements LocationSource, AMap.OnMapTouchListener, AMapLocationListener, RouteSearch.OnRouteSearchListener {
    /**
     * 设置地图基础变量
     */
    //定义地图控件
    MapView mMapView = null;
    //定义地图对象
    AMap aMap;
    //设置公交位置坐标变量
    LatLng bus_location;

    /**
     * 设置自动定位变量
     * */
    //定位设置
    //设置位置改变监听事件
    private OnLocationChangedListener mListener;
    //声明AMapLocationClient类对象
    private AMapLocationClient mlocationClient;
    //设置地图定位选项
    private AMapLocationClientOption mLocationOption;
    //选择是否地图随位置移动
    boolean useMoveToLocationWithMapMode = true;
    //自定义定位小蓝点的Marker
    Marker locationMarker;
    //设置定位坐标全局变量
    LatLng my_location;
    //坐标和经纬度转换工具
    Projection projection;
    //设置定位信息样式
    MyLocationStyle myLocationStyle;


    /**
     * 绘制公交线路变量
     */
    //设置公交车路线绘制
    BusLineOverlay busLineOverlay;
    //设置车站队列
    List<BusStationItem> busStationItemList;
    //设置位置点列表
    List<LatLonPoint> pointList = new ArrayList<>();
    //设置位置点
    List<LatLng> pointList2=new ArrayList<>();
    //定义公交车对象
    Bus temp_bus;
    //设置网络获取公交车坐标中间变量
    static LatLng result_state;
    //设置公交车位置全局变量
    LatLng bus_position_result;


    /**
     * 设置变量使得公交缓慢移动，设置汽车移动
     */
    //设置路线坐标
    private double[] coords;
    //设置汽车坐标
    private List<LatLng> carsLatLng;
    //设置目标坐标队列
    private List<LatLng> goLatLng;
    //设置显示标记队列
    private List<Marker> showMarks;
    //设置移动标记队列
    private List<SmoothMoveMarker> smoothMarkers;
    //
    private double lng = 0.0;
    //
    private double lat = 0.0;
    SmoothMoveMarker smoothMarker;

    /**
     * 设置显示最近距离
     */
    private RadioGroup radioOption;
    //定义是否显示最近站点位置，是
    private boolean is_show_recent_station=true;
    //定义最近站点Marker
    private Marker recentest_station_marker;


    /**
     * 设置定位Marker变量方便自动定位，和纠正自己位置
     */
    //设置自我定位点变量
    private Marker self_location_marker;
    //记录屏幕中心位置大头针
    private LatLng screen_center_location;
    //设置地图加载是否完成
    boolean is_mapload_finished=false;
    //是否显示中心点改变
    boolean is_show_center_change=false;


   // ArrayList<BitmapDescriptor> icons=new ArrayList<>(R.drawable.amap_notacion_bus,R.drawable.amap_notacion_bus2);
    /**
     * 设置路径规划模块功能
     */
    //设置终点可移动Marker用于路径规划
    Marker target_point_marker=null;
    //设置终点位置坐标
    LatLng target_point;
    //设置是否显示target_point_marker
    boolean is_show_target_maker=false;

    //设置路径规划变量路径起点和路径终点坐标
    LatLng start_location,end_location;
    //设置起点和终点最近站点变量
    BusStationItem start_station,end_station;
    //设置busRouteOverlay重新规划路线,获得路线规划
    BusRouteOverlay my_route_overlay;
    //定义中间站点
    List<BusStationItem> Intermediate_site=new ArrayList<BusStationItem>();
    //定义中间站点Mark
    List<Marker> Intermediate_te_marker=new ArrayList<Marker>();
    //定义添加
    //设置道路搜索
    private RouteSearch mRouteSearch;
    //设置步行查询语句
    private WalkRouteOverlay walkRouteOverlay1,walkRouteOverlay2;
    //设置步行搜索结果
    private WalkRouteResult mWalkRouteResult;
    //设置驾驶搜索结果
    private DriveRouteResult mDriveRouteResult;
    //设置返回步行路径!===返回不成功下次再试
    private WalkPath walkPath1=new WalkPath();
    private WalkPath walkPath2=new WalkPath();
    //检测是否改变成功
    boolean is_wark_path_changed=true;
    //设置信号变量使得能够添加线路,是否为第二条线路
    Boolean is_the_second_line=false;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        /*
        * 设置界面控件
        * */
        //获取toolbar工具
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //设置浮动条
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        //设置高德地图
        //初始化地图
        mMapView = (MapView) findViewById(R.id.map);
        //初始化地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
            setUpMap();
        }

        //设置定位
        //set_locaction();
        //初始化公交车
        LatLng temp_postion = new LatLng(30.5539600000,103.9963020000);
        temp_bus = new Bus("ceshi", 123456, 1, temp_postion, 10);
        //绘制公交线路地图
        draw_busline();
        //更改中心
        change_center();
        //初始化UI控件
        initView();
        //显示定位大头针
        set_self_location_marker();
        //设置汽车奔跑
       // set_bus_run2();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * 设置地图显示和定位模块
     */
    //销毁地图
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //消除地图
        mMapView.onDestroy();
        //销毁位置监听
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }
    //初始化地图之前的准备
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
        useMoveToLocationWithMapMode = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        //激活定位
        deactivate();
        useMoveToLocationWithMapMode = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }


    //设置地图
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            //设置地图加载监听器
            @Override
            public void onMapLoaded() {

            }
        });
        aMap.setOnMapTouchListener(this);
        //设置道路搜索监听
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        //隐藏logo
        UiSettings uiSettings =  aMap.getUiSettings();
        uiSettings.setLogoBottomMargin(-50);//隐藏logo

        //设置屏幕中心移动定位监听
        final AMap.OnCameraChangeListener camerchange=new AMap.OnCameraChangeListener(){

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                //Button set_location=(Button)findViewById(R.id.check_location);
                //获取屏幕中心点
                if(!is_show_center_change) {//检查中心点是否改变
                    //改变大头针位置
                screen_center_location = cameraPosition.target;
                //根据按钮返回，决定是否显示变化
                    //改变大头针可见状态
                    TextView show_location_view=(TextView)findViewById(R.id.show_location) ;
                    show_location_view.setText(screen_center_location.toString());
                }

            }
        };
        //添加屏幕移动监听
        aMap.setOnCameraChangeListener(camerchange);
        //添加拖拽事件
        set_target_Marker();
    }


    //设置定位函数
    private void set_locaction() {
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle = new MyLocationStyle();
        //定位一次，且将视角移动到地图中心点。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        //设置当前定位图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(1000);
        //设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }
    /**
     * 定位成功后回调函数
     */
    @Override
    //设置自动定位后的返回函数
    //设置定位函数，定位成功后返回位置信息
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            //如果定位成功则取出定位坐标信息并添加小蓝点
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                LatLng latLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                //设置当前位置为全局变量
                my_location= latLng;
                //Log.i("位置坐标", "纬度：" + my_location.latitude + ",经度" + my_location.longitude)
                //显示最近站点
                //draw_marker(LatLng mylocation);
                //展示自定义定位小蓝点
                if (locationMarker == null) {
                    //首次定位
                    locationMarker = aMap.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker))
                            .anchor(0.5f, 0.5f));


                    //设置终点坐标
                    if(target_point_marker==null){
                        //获得终点marker
                        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.amap_end);
                        Bitmap bitmap2=ScaleBitmap(bitmap1,0.5f);
                        target_point_marker= aMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap2))//添加标记
                                .draggable(true)//设置标记可拖动
                               // .position(new LatLng(30.675242449184851f,104.1484981364249))
                                .anchor(0.5f, 0.5f));
                        target_point_marker.setVisible(is_show_target_maker);
                    }
                    //首次定位,选择移动到地图中心点并修改级别到15级
                   // aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                } else {
                    //定位标记为空
                    //如果二次使用sdk中没有的模式，让地图和小蓝点一起移动到中心点（类似导航锁车时的效果）
                    if (useMoveToLocationWithMapMode) {
                        //二次以后定位，使用sdk中没有的模式，让地图和小蓝点一起移动到中心点（类似导航锁车时的效果）
                        //移动到位置
                      //  startMoveLocationAndMap(latLng);
                    } else {//如果是首次就开始定位
                        //开始定位
                      //  startChangeLocation(latLng);
                    }

                }
                //显示最近站点
                //draw_marker(my_location);
            } else {//否则返回定位失败
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }

        }
    }


    /**
     * 修改自定义定位小蓝点的位置
     *
     * @param latLng
     */
    private void startChangeLocation(LatLng latLng) {
        //如果定位标记不为空
        if (locationMarker != null) {
            //取出定位坐标
            LatLng curLatlng = locationMarker.getPosition();
            //取出坐标为空或者与输入定位不同更改标记坐标
            if (curLatlng == null || !curLatlng.equals(latLng)) {
                locationMarker.setPosition(latLng);
            }
        }
    }

    /**
     * 同时修改自定义定位小蓝点和地图的位置
     *
     * @param latLng
     */
    private void startMoveLocationAndMap(LatLng latLng) {

        //将小蓝点提取到屏幕上
        if (projection == null) {
            //获取经纬度
            projection = aMap.getProjection();
        }
        //如果定位标记与获取的经纬度都为不为空
        if (locationMarker != null && projection != null) {
            //设置标记坐标
            LatLng markerLocation = locationMarker.getPosition();
            //获取坐标点
            Point screenPosition = aMap.getProjection().toScreenLocation(markerLocation);
            //将小蓝点输出到屏幕上
            locationMarker.setPositionByPixels(screenPosition.x, screenPosition.y);

        }

        //移动地图，移动结束后，将小蓝点放到放到地图上
        myCancelCallback.setTargetLatlng(latLng);
        //动画移动的时间，最好不要比定位间隔长，如果定位间隔2000ms 动画移动时间最好小于2000ms，可以使用1000ms
        //如果超过了，需要在myCancelCallback中进行处理被打断的情况
        aMap.animateCamera(CameraUpdateFactory.changeLatLng(latLng), 1000, myCancelCallback);

    }

    //
    MyCancelCallback myCancelCallback = new MyCancelCallback();

    @Override
    public void onTouch(MotionEvent motionEvent) {
       // i("amap", "onTouch 关闭地图和小蓝点一起移动的模式");
        useMoveToLocationWithMapMode = false;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Second Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    if (drivePath == null) {
                        return;
                    }
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            SecondActivity.this, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                   // drivingRouteOverlay.zoomToSpan();

                }else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(SecondActivity.this, "路径不存在");
                }
        } else {
            ToastUtil.show(SecondActivity.this, "无查询结果");
            }
    } else {
        ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        if(is_wark_path_changed) {//检查是否需要改变，如果是则改变线路
            if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
                if (result != null && result.getPaths() != null) {
                    if (result.getPaths().size() > 0) {
                        mWalkRouteResult = result;
                        final WalkPath walkPath = mWalkRouteResult.getPaths()
                                .get(0);
                        if (walkPath == null) {
                            return;
                        }
                        //检查而更新线路是否为1
                        if (!is_the_second_line){//不是第二条，绘制第一条
                                if (walkRouteOverlay1 != null) {
                                    walkRouteOverlay1.removeFromMap();
                                }
                            //绘制步行路线
                            walkRouteOverlay1 = new WalkRouteOverlay(
                                    this, aMap, walkPath,
                                    mWalkRouteResult.getStartPos(),
                                    mWalkRouteResult.getTargetPos());

                            walkRouteOverlay1.addToMap();
                            //更改下一个为绘制第二条
                           is_the_second_line=true;
                            //  walkRouteOverlay.zoomToSpan();
                        }else {//如果是第二条线路的话更新第二条
                            if (walkRouteOverlay2 != null) {
                                walkRouteOverlay2.removeFromMap();
                            }
                            //绘制步行路线
                            walkRouteOverlay2 = new WalkRouteOverlay(
                                    this, aMap, walkPath,
                                    mWalkRouteResult.getStartPos(),
                                    mWalkRouteResult.getTargetPos());

                            walkRouteOverlay2.addToMap();
                            //更改下一次更新路线为1

                                    is_the_second_line=false;
                                          //  is_wark_path_changed=true;
                        }
                    } else if (result != null && result.getPaths() == null) {
                        ToastUtil.show(SecondActivity.this, "路径不存在");
                    }
                } else {
                    ToastUtil.show(SecondActivity.this, "无查询结果");
                }
            } else {
                ToastUtil.showerror(this.getApplicationContext(), errorCode);
            }
        }
        if(is_the_second_line) {//如果是第二段线路，改变mark
            //更改公交标志
            for (Marker temp_mark : Intermediate_te_marker) {
                //更换中间点
                temp_mark.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.amap_notacion_bus2));
            }
        }

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    /**
     * 监控地图动画移动情况，如果结束或者被打断，都需要执行响应的操作
     */
    class MyCancelCallback implements AMap.CancelableCallback {

        LatLng targetLatlng;

        public void setTargetLatlng(LatLng latlng) {
            this.targetLatlng = latlng;
        }

        @Override
        public void onFinish() {
            if (locationMarker != null && targetLatlng != null) {
                locationMarker.setPosition(targetLatlng);
            }
        }

        @Override
        public void onCancel() {
            if (locationMarker != null && targetLatlng != null) {
                locationMarker.setPosition(targetLatlng);
            }
        }
    }

    ;

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //是指定位间隔
            mLocationOption.setInterval(2000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            //开始定位
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位，销毁定位线程
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }



    /**
     * 设置view控件，添加按钮变量
     */
    //初始化view控件
    private void initView() {
        //获取显示按钮
        final Button show_car_button = (Button) findViewById(R.id.put);
        //获取启动按钮
        final Button show_recent_station_button = (Button) findViewById(run);
        //获取修改位置按钮
        final Button check_location_button = (Button) findViewById(check_location);
        //获取再次获得最近站点按钮
        final Button reshow_station_button = (Button) findViewById(R.id.reshow_rencten_station);
        //获取是否显示终点按钮
        final Button show_end_marker_button = (Button) findViewById(R.id.show_end_marker);
        //设置路径规划显示按钮
        final Button show_route = (Button) findViewById(R.id.show_line_route);
        //设置回到制定区域按钮；
       // final Button return_button=(Button)findViewById(R.id.return_school);
        //final Button wait_button=(Button)findViewById(R.id.get_wait);

        //获取输出文本框
        final TextView show_location_view = (TextView) findViewById(R.id.show_location);


        //设置监听事件

        show_car_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("输出《《《《","点击汽车奔跑");
                set_bus_run2();
            }

        });
        show_recent_station_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_show_recent_station) {
                    //
                    /*
                    if (my_location != null) {
                        //绘制最近点
                        draw_marker(my_location);
                        is_show_recent_station = false;
                        show_recent_station_button.setText("隐藏最近");
                    } else {
                        //如果定位失败则隐藏
                        if(screen_center_location!=null){
                            draw_marker(screen_center_location);
                        }

                        //定位没有显示错误
                        Toast.makeText(SecondActivity.this, "未定位，请打开网络定位", Toast.LENGTH_LONG);
                    }*/
                is_show_recent_station = false;
                   // recentest_station_marker.setVisible(true);
                show_recent_station_button.setText("显示最近");
                } else {
                  //  recentest_station_marker.setVisible(false);
                    //recentest_station_marker.remove();
                    is_show_recent_station = true;
                    show_recent_station_button.setText("隐藏最近");

                }
                recentest_station_marker.setVisible(is_show_recent_station);
            }
        });
        //设置按钮是否显示中间点位置变化
        check_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_show_center_change) {
                    is_show_center_change = false;
                    //显示da大头针
                  check_location_button.setText("固定隐藏");
                    self_location_marker.setVisible(true);
                } else {
                    is_show_center_change = true;
                    check_location_button.setText("修改定位");
                    self_location_marker.setVisible(false);
                }
            }
        });
        reshow_station_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //改变最近站点位置
                //显示最近站点
                if (screen_center_location != null) {
                    if (recentest_station_marker != null) {
                        //找寻最近站点
                        BusStationItem temp_station1 = get_most_recent_point(busStationItemList, screen_center_location);
                        LatLng temp_location = LocationToLat(temp_station1.getLatLonPoint());
                        //设置其为可见
                        recentest_station_marker.setVisible(true);
                        //更改名称
                        recentest_station_marker.setTitle(temp_station1.getBusStationName());
                        //更新位置
                        recentest_station_marker.setPosition(temp_location);
                    } else {
                        draw_marker(screen_center_location);
                    }
                } else {
                    //设置文本提示
                    show_location_view.setText("请移动界面，定位中心屏幕");
                }
            }
        });
        //设置按钮显示隐藏终点定位Marker
        show_end_marker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_show_target_maker) {
                    show_end_marker_button.setText("定位终点");
                    is_show_target_maker = false;
                } else {
                    show_end_marker_button.setText("隐藏终点");
                    is_show_target_maker = true;
                }
                if(target_point_marker!=null) {
                    target_point_marker.setVisible(is_show_target_maker);
                }else {
                    Log.i("设置《《《《", "target_point_marker为空");
                }
            }
        });
        show_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("设置《《《《", "确认点击事件");
                //输出信息提示拖拽获取目标点
                Toast.makeText(SecondActivity.this, "长按终点标记拖拽，选定目的地", Toast.LENGTH_LONG);
                //显示路径规划
                //draw_bus_route();
                //显示步行路线
               // final LatLonPoint star_point = AMapUtil.convertToLatLonPoint(new LatLng(30.677069535416102, 104.14154872298242));
               // final LatLonPoint end_point = AMapUtil.convertToLatLonPoint(new LatLng(30.679264479556, 104.154222160577));
               // LatLonPoint star_point1 = AMapUtil.convertToLatLonPoint(new LatLng(30.673902176549172, 104.1415010371615));
               // LatLonPoint end_point2 = AMapUtil.convertToLatLonPoint(new LatLng(30.6765664050754, 104.15110141038895));
                //显示路径规划
                draw_bus_route();
               // show_bus_line(star_point1,end_point2);
              //  get_work_path(star_point1, end_point2);
                //get_work_path(star_point,end_point);
            }


        });
        /*
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_center();
            }
        });
        wait_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Handler mHandler1 = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        String return_data=msg.obj.toString();
                        //解析字符串
                        Gson gson = new Gson();
                        //将其转化为bus对象
                        BusOrder temp_bus2 = gson.fromJson(return_data, BusOrder.class);
                        show_location_view.setText(return_data);
                        Log.i("输出《《《《",return_data);

                    }
                };

                Log.i("输出《《《","勤查快递吧VB度 ");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //创建okhttp
                            OkHttpClient client = new OkHttpClient();
                            //创建post请求
                            RequestBody requestBody = new FormEncodingBuilder()
                                    .add("user_name", "18117842737")
                                    .add("station_num", "29")
                                    .build();
                            //创建请求对象
                            Request request = new Request.Builder()
                                    .url("http://118.24.49.94/send_bus_order.php")
                                    .post(requestBody)
                                    .build();
                            //创建回执对象
                            Response response = client.newCall(request).execute();
                            Message msg=new Message();
                            if(response.isSuccessful()) {
                                //创建回执对象字符串
                                String responseData = response.body().string();
                                msg.obj=responseData;
                                mHandler1.sendMessage(msg);
                                Log.i("输出《《《",responseData);
                            }else{
                                mHandler.sendMessage(msg);
                                throw new IOException("Unexpected code " + response);
                            }

                        }  catch (Exception e) {
                        e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
        */
    }


    /**
     * 设置绘制公交线路函数
     */
    //绘制公交线路
    private void draw_busline() {
        //aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        //设置车站
        busStationItemList = new ArrayList<>();
        //创建站点
        BusStationItem item1 = new BusStationItem();
        BusStationItem item2 = new BusStationItem();
        BusStationItem item3 = new BusStationItem();
        BusStationItem item4 = new BusStationItem();
        BusStationItem item5 = new BusStationItem();
        BusStationItem item6 = new BusStationItem();
        BusStationItem item7 = new BusStationItem();
        //设置车站名称和id(用于比较查找车站)
        item1.setBusStationName("体育学院");
        item1.setBusStationId("1");
        item2.setBusStationName("艺术学院");
        item2.setBusStationId("2");
        item3.setBusStationName("友谊林");
        item3.setBusStationId("3");
        item4.setBusStationName("制造试验楼");
        item4.setBusStationId("4");
        item5.setBusStationName("计算机学院");
        item5.setBusStationId("5");
        item6.setBusStationName("一号运动场");
        item6.setBusStationId("6");
        item7.setBusStationName("川大东南门");
        item7.setBusStationId("7");


        //设置车站坐标
        item1.setLatLonPoint(new LatLonPoint(30.5539600000,103.9963020000));
        item2.setLatLonPoint(new LatLonPoint(30.5557010000,103.9950470000));
        item3.setLatLonPoint(new LatLonPoint(30.5592320000,103.9958880000));
        item4.setLatLonPoint(new LatLonPoint(30.5598080000,103.9985610000));
        item5.setLatLonPoint(new LatLonPoint(30.5585790000,104.0033470000));
        item6.setLatLonPoint(new LatLonPoint(30.5569810000,104.0056800000));
        item7.setLatLonPoint(new LatLonPoint(30.5554750000,104.0069890000));

        //将站点信息加入车站
        busStationItemList.add(item1);
        busStationItemList.add(item2);
        busStationItemList.add(item3);
        busStationItemList.add(item4);
        busStationItemList.add(item5);
        busStationItemList.add(item6);
        busStationItemList.add(item7);

        //设置线路关键位置点列表
        //List<LatLonPoint> pointList = new ArrayList<>();
        pointList.add(new LatLonPoint(30.5539600000,103.9963020000));
        pointList.add(new LatLonPoint(30.5542520000,103.9961250000));
        pointList.add(new LatLonPoint(30.5546600000,103.9958870000));
        pointList.add(new LatLonPoint(30.5557010000,103.9950470000));
        pointList.add(new LatLonPoint(30.5560220000,103.9948030000));
        pointList.add(new LatLonPoint(30.5564330000,103.9946800000));
        pointList.add(new LatLonPoint(30.5569180000,103.9946900000));
        pointList.add(new LatLonPoint(30.5572690000,103.9947330000));
        pointList.add(new LatLonPoint(30.5584380000,103.9951780000));
        pointList.add(new LatLonPoint(30.5588650000,103.9954390000));
        pointList.add(new LatLonPoint(30.5590940000,103.9956780000));
        pointList.add(new LatLonPoint(30.5592320000,103.9958880000));
        pointList.add(new LatLonPoint(30.5593950000,103.9961070000));
        pointList.add(new LatLonPoint(30.5595890000,103.9965680000));
        pointList.add(new LatLonPoint(30.5596990000,103.9969170000));
        pointList.add(new LatLonPoint(30.5597620000,103.9974880000));
        pointList.add(new LatLonPoint(30.5597770000,103.9982670000));
        pointList.add(new LatLonPoint(30.5598080000,103.9985610000));
        pointList.add(new LatLonPoint(30.5598950000,103.9990920000));
        pointList.add(new LatLonPoint(30.5602040000,104.0001650000));
        pointList.add(new LatLonPoint(30.5603960000,104.0011270000));
        pointList.add(new LatLonPoint(30.5603170000,104.0014390000));
        pointList.add(new LatLonPoint(30.5602060000,104.0017120000));
        pointList.add(new LatLonPoint(30.5599520000,104.0020720000));
        pointList.add(new LatLonPoint(30.5597300000,104.0023020000));
        pointList.add(new LatLonPoint(30.5589270000,104.0028600000));
        pointList.add(new LatLonPoint(30.5587850000,104.0030220000));
        pointList.add(new LatLonPoint(30.5585790000,104.0033470000));
        pointList.add(new LatLonPoint(30.5579890000,104.0046160000));
        pointList.add(new LatLonPoint(30.5576800000,104.0050660000));
        pointList.add(new LatLonPoint(30.5569810000,104.0056800000));
        pointList.add(new LatLonPoint(30.5554750000,104.0069890000));


        //设置公交线
        BusLineItem busLineItem = new BusLineItem();
        //公交线路关键点描述
        busLineItem.setDirectionsCoordinates(pointList);
        //设置公交线路站点描述
        busLineItem.setBusStations(busStationItemList);
        //设置公交线路重载
        busLineOverlay = new BusLineOverlay(this, aMap, busLineItem);
        //将公交线路添加到地图中
        busLineOverlay.addToMap();
    }


    /**
     * 设置公交车绘制模块，显示移动汽车
     * @param
     * @param
     */
    //设置函数获取后台公交车位置信息
    //设置实时显示公交动画
    //设置函数获取后台公交车位置信息
    //设置函数模拟汽车奔跑
    private void set_bus_run2(){
        //
        ToLatLng pointList3=new ToLatLng();
         pointList2= pointList3.toLatLng(pointList);
        //设置汽车移动
        if(smoothMarker==null) {
            smoothMarker = new SmoothMoveMarker(aMap);

            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.car_up);
            // 设置滑动的图标
            smoothMarker.setDescriptor(icon);
        }
            //初始驾驶点
            LatLng drivePoint = pointList2.get(0);
            //设置路线点和起点
            Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(pointList2, drivePoint);
            pointList2.set(pair.first, drivePoint);
            List<LatLng> subList = pointList2.subList(pair.first, pointList2.size());

            // 设置滑动的轨迹点
            smoothMarker.setPoints(subList);
            // 设置滑动的总时间
            smoothMarker.setTotalDuration(100);
            // 开始滑动
            smoothMarker.startSmoothMove();

    }
    private void set_bus_run(){
        new Thread(mRunnable).start();
    }
    //设置控制句柄方便更新
    Handler mHandler = new Handler(){

        public void handleMessage(Message msg) {
            //模拟操作
            //将返回对象转化为字符串
            String return_data=msg.obj.toString();
            //设置GSON
            Gson gson = new Gson();
            //将其转化为bus对象
            Bus temp_bus2 = gson.fromJson(return_data, Bus.class);
            //判断临时变量是否为空
            if(result_state==null) {
                //如果result_state没有初始化，则进行初始化
                //提取json对象result参数获得位置坐标
                result_state= temp_bus2.getBus_position().ToLatLng();
            }else{
                //提取json对象result参数获得位置坐标
                LatLng temp= temp_bus2.getBus_position().ToLatLng();
                //将前面的result_state作为起点，temp作为终点
               // Log.i("输出坐标<<<<<<",result_state.toString());
               // Log.i("输出坐标《《《《《",temp.toString());
                draw_bus(result_state,temp);
                //设置临时数据
                result_state=temp;
            }
        }

    };
    //设置显示函数和延迟时间
    Runnable mRunnable = new Runnable() {

        public void run(){

            while(true){

                try{

                    //创建okhttp
                    OkHttpClient client = new OkHttpClient();
                    //创建post请求
                    RequestBody requestBody = new FormEncodingBuilder()
                            .add("bus_name", temp_bus.getBus_name())
                            .add("bus_num", String.valueOf(temp_bus.getBus_num()))
                            .build();
                    //创建请求对象
                    Request request = new Request.Builder()
                            .url("http://118.24.49.94/location_point_simulation.php")
                            .post(requestBody)
                            .build();
                    //创建回执对象
                    Response response = client.newCall(request).execute();
                    //创建回执对象字符串
                    String responseData = response.body().string();
                    Message msg=new Message();
                    //判断返回成功并输出
                    if (response.isSuccessful()) {

                        msg.obj=responseData;
                        //getPositionHandler.sendMessage(msg);
                        //输出位置信息
                        mHandler.sendMessage(msg);

                    } else {
                        //1表示网络错误
                        int statu_temp = 1;
                        mHandler.sendMessage(msg);
                        throw new IOException("Unexpected code " + response);
                    }
                    //设置睡眠时间为5000ms
                    Thread.sleep(10*1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            }

        };
    //设置绘制汽车动画
    private void draw_bus(LatLng start,LatLng end) {

        //初始化数据
        initData(start,end);
        //开始跑
        if (smoothMarkers != null) {//清空缓慢移动列表
            for (int i = 0; i < smoothMarkers.size(); i++) {
                smoothMarkers.get(i).destroy();
            }
        }
        //初始化显示移动列表
        if (showMarks == null) {
            showMarks = new ArrayList<Marker>();
        }
        //清空显示标记
        for (int j = 0; j < showMarks.size(); j++) {
            showMarks.get(j).remove();
        }
        //初始化缓慢移动标记
        smoothMarkers = null;
        //设置缓慢移动标记队列
        smoothMarkers = new ArrayList<SmoothMoveMarker>();
        //设置标记图标
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.car_up);
        //将坐标点添加到线路坐标中并且设置每一辆车移动路线
        for (int i = 0; i < carsLatLng.size(); i++) {
            //设置新坐标集合
            double[] newoords = {Double.valueOf(carsLatLng.get(i).longitude), Double.valueOf(carsLatLng.get(i).latitude),
                    Double.valueOf(goLatLng.get(i).longitude), Double.valueOf(goLatLng.get(i).latitude)};
            coords = newoords;
            //设置平滑移动
            movePoint(icon);
        }
    }
        //初始化view
        //initView();
    //初始化汽车数据
    private void initData(LatLng start,LatLng end) {
        if(start==null||end==null){
            start=new LatLng(30.6709270000, 104.1438450000);
            end=new LatLng(30.6773060000, 104.1526730000);

        }
        //设置起点坐标
        LatLng car1 = start;
        // LatLng car2 = new LatLng(30.6804640000, 104.1526650000);
        // LatLng car3 = new LatLng(30.6726920000, 104.1482290000);
        //设置车辆坐标
        carsLatLng = new ArrayList<>();
        //添加车辆坐标
        carsLatLng.add(car1);
        //carsLatLng.add(car2);
        // carsLatLng.add(car3);

        //设置目标坐标
        LatLng go1 = end;
        //LatLng go2 = new LatLng(30.6804640000, 104.1526650000);
        //LatLng go3 = new LatLng(30.682742898003472, 104.15393963237848);
        //设置目标坐标数组
        goLatLng = new ArrayList<>();
        //添加目标坐标
        goLatLng.add(go1);
        //goLatLng.add(go2);
        //goLatLng.add(go3);
    }

    //平滑移动
    public void movePoint(BitmapDescriptor bitmap) {
        // 获取轨迹坐标点
        List<LatLng> points = readLatLngs();
//        LatLngBounds bounds = new LatLngBounds(points.get(0), points.get(points.size() - 2));
//        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

//        SparseArrayCompat sparseArrayCompat = new SparseArrayCompat();//�ȸ��¼��ϣ����hashmap
        // 设置平移滑动
        SmoothMoveMarker smoothMarker = new SmoothMoveMarker(aMap);
        //将平滑移动对象添加到集中
        smoothMarkers.add(smoothMarker);
        //查看平滑移动数量
        int num = smoothMarkers.size() - 1;
        // 设置滑动的图标
        smoothMarkers.get(num).setDescriptor(bitmap);
        //初始驾驶点
        LatLng drivePoint = points.get(0);
        //设置路线点和起点
        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
        points.set(pair.first, drivePoint);
        List<LatLng> subList = points.subList(pair.first, points.size());

        // 设置滑动的轨迹点
        smoothMarkers.get(num).setPoints(subList);
        // 设置滑动的总时间
        smoothMarkers.get(num).setTotalDuration(20);
        // 开始滑动
        smoothMarkers.get(num).startSmoothMove();
    }

    //获取路线
    private List<LatLng> readLatLngs() {
        List<LatLng> points = new ArrayList<LatLng>();
        for (int i = 0; i < coords.length; i += 2) {
            points.add(new LatLng(coords[i + 1], coords[i]));
        }
        //返回路线坐标
        return points;
    }



    /**
     * 设置功能计算距离最近站点并绘制maker
     */
    //类型转换
    public LatLng LocationToLat(LatLonPoint var1){
        LatLng result=new LatLng(var1.getLatitude(),var1.getLongitude());
        return result;
    }
    //计算最近站点
    public BusStationItem get_most_recent_point( List<BusStationItem> busStationItemList,LatLng mylocation)
    {
        //设置返回点并初始化
        BusStationItem min_point=busStationItemList.get(1);
        //设置最小距离
        float min_distance=0,temp_distance;
        int min_index=0;
        for(int i=0;i<busStationItemList.size();i++) {
            //类型转换
            LatLng temp = LocationToLat(busStationItemList.get(i).getLatLonPoint());
            //计算距离
            temp_distance = AMapUtils.calculateLineDistance(temp, mylocation);
            //计算距离
            if (min_distance==0) {
                min_distance = temp_distance;
            }else if(temp_distance<min_distance) {
                min_distance = temp_distance;
                min_index=i;
            }

        }
        return busStationItemList.get(min_index);
    }
    //绘制最近点Marker
    public void draw_marker(LatLng mylocation){
        //获取最近站点信息
        BusStationItem temp_station=get_most_recent_point( busStationItemList,mylocation);

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.amap_notacion_bus);
        //设置MarkerOptions
        MarkerOptions my_mark_options=new MarkerOptions();
        //设置mark选项temp_station.getBusStationName()
        my_mark_options.title("最近站点:"+temp_station.getBusStationName())
                .icon(icon)
                .anchor(0.5f, 0.5f)//设置偏差
                .position(LocationToLat(temp_station.getLatLonPoint()))//转换为lat
                .draggable(true);

        recentest_station_marker= aMap.addMarker(my_mark_options);
        // 设置marker旋转90度
        recentest_station_marker.setRotateAngle(0);
      //  recentest_station_marker.setPosition(new LatLng(30.677192643229168,104.1542938671875));
        //marker.setPositionByPixels(400, 400);
        //AMap.InfoWindowAdapter.getInfoWindow(Marker){}
        recentest_station_marker.showInfoWindow();// 设置默认显示一个infowinfow
        //设置信息窗口回调
        AMap.InfoWindowAdapter showAapter=new AMap.InfoWindowAdapter() {
            @Override
           // 定制展示marker信息的View。
            public View getInfoWindow(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(
                        R.layout.custom_info_window, null);

                render(marker, infoWindow);
                return infoWindow;
            }
            //提定制展示marker信息的View。
            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        };
        //设置信息展示
        aMap.setInfoWindowAdapter(showAapter);

    }

    /**
     * 在屏幕中心添加一个Marker
     */
    private void addMarkerInScreenCenter(Marker screenMarker,BitmapDescriptor bitmap) {
        //设置MarkerOptions
        LatLng latLng = aMap.getCameraPosition().target;
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
        screenMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f,0.5f)
                .icon(bitmap));
        //设置Marker在屏幕上,不跟随地图移动
        screenMarker.setPositionByPixels(screenPosition.x,screenPosition.y);

    }
    /**
     * 监听自定义infowindow窗口的infowindow事件回调
     */
    /**
     * 自定义infowinfow窗口渲染函数
     */
    public void render(Marker marker, View view) {
        //设置显示
        ImageView imageView = (ImageView) view.findViewById(R.id.badge);
        //设置图片
        imageView.setImageResource(R.drawable.badge_wa);
        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
                    titleText.length(), 0);
            titleUi.setTextSize(15);
            titleUi.setText(titleText);

        } else {
            titleUi.setText("");
        }
        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
                    snippetText.length(), 0);
            snippetUi.setTextSize(20);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }
    }


    /**
     *设置大头针，方便自定义位置
     */
    //设置图片缩放
    public Bitmap ScaleBitmap(Bitmap var1,float var2){
        int width = var1.getWidth();
        int height = var1.getHeight();
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(var2,var2);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(var1, 0, 0, width, height, matrix,
                true);
        return newbm;
    }
    public void set_self_location_marker(){



        //设置self_loction_marker基本参数
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.setFlat(true);
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(new LatLng(0, 0));

        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.icon_loaction_start);
        Bitmap bitmap2=ScaleBitmap(bitmap1, 0.5f);


        //设置图片
        markerOptions
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap2));
        //添加marker
        self_location_marker = aMap.addMarker(markerOptions);
        self_location_marker.setVisible(is_show_center_change);
        //监听地图加载是否完成，如果完成就设置其在屏幕中间
        mMapView.post(new Runnable() {
            @Override
            public void run() {
               // mMapView.getHeight(); //height is ready
                //设置marker总是在屏幕中间
                self_location_marker.setPositionByPixels(mMapView.getWidth() / 2,
                        mMapView.getHeight() / 2);
                //if(screen_center_location==null){
                  //  Log.i("显示《《《《《",my_location.toString());
                //}
                is_mapload_finished=true;
            }

        });

    }

    /**
     * 路径规划功能的具体模块实现
     */
    //设置终点拖拽方法实现
    public void set_target_Marker(){
        aMap.setOnMarkerDragListener(new AMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {//长按拖动开始
            //Log.i("输出《《《《《","开始得到经纬度");
            }

            @Override
            public void onMarkerDrag(Marker marker) {
                //拖动中

               // Log.i("输出《《过程《《经度《《《",marker.getPosition().latitude + "");
                //Log.i("输出《《过程《《纬度《《《",marker.getPosition().longitude + "");
                //show_location_view.setText(marker.getPosition().latitude + "");
                //show_location_view.setText(marker.getPosition().longitude + "");
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                //拖动结束
                //show_location_view.setText(marker.getPosition().latitude + "");
                //show_location_view.setText(marker.getPosition().longitude + "");
               // Log.i("输出《《结果《《经度《《《",marker.getPosition().latitude + "");
               // Log.i("输出《《结果《《纬度《《《",marker.getPosition().longitude + "");
                //获得最终拖拽结果
                target_point=marker.getPosition();
            }
        });
    }
    //标注线路规划
     //   1.找到当前位置最近车站
    //    2.找到终点位置最近车站
    //    3.找到亮车站之间的站点
    //    4.画出当前位置到最近车站的道路规划
    //    5.画出目标位置到最近车站的道路规划
    //    6.画出中间车站
    //BusRouteOverlay<=BusPath <=List<BusStep> e = new ArrayList();<=List<RouteBusLineItem> b = new ArrayList();<=List<BusStationItem>
    public void draw_bus_route(){
        //设置临时
        List<Marker> temp_en_Marker = new ArrayList<Marker>();
        //获取距离起点最近车站信息
        //获取终点坐标
        if(target_point!=null){
            //目标位置已经获取
            end_location=target_point;
        }else {//目标位置没有获取
            //输出信息提示拖拽获取目标点
            Toast.makeText(SecondActivity.this,"长按终点标记拖拽，选定目的地",Toast.LENGTH_LONG);
        }
        //获取起点位置,优先获取大头针定义点
        if(screen_center_location!=null){
            //获取起点位置
            start_location=screen_center_location;

        }else{//大头针未定位移动
            //检查是否自动定位坐标是否存在
            if(my_location!=null){
                //存在使用my_location
                start_location=my_location;
            }
        }
        start_station=get_most_recent_point( busStationItemList,start_location);
        //获取终点最近站点信息
        end_station=get_most_recent_point( busStationItemList,end_location);
        //查找公交站点队列获取，中间站点的信息
        //直接通过Id判断站点先后顺序，获取中间站点
        //获取起点index
        int start_index=Integer.parseInt(start_station.getBusStationId());
        //获取终点index
        int end_index=Integer.parseInt(end_station.getBusStationId());
        //获取起点位置
        //busStationItemList;
        Log.i("输出《《《《","起点站点位置"+start_index);
        Log.i("输出《《《《","终点站点位置"+end_index);
        //记录队列长度
        int list_leng=busStationItemList.size();
        //使用队列前先清空
        //根据index大小判断遍历方向
        if(start_index==end_index) {
           //先判断是否是同一个站点，如果是则输出提示
            Toast.makeText(SecondActivity.this,"站点过近，请重新选择或者步行",Toast.LENGTH_LONG);
        }else if (start_index < end_index) {//正向则正向遍历
            for (int i = start_index-2; i < end_index-1; i++) {
                //将公交站点顺序入list
                Intermediate_site.add(busStationItemList.get(i));
                //获取Marker
               // Marker temp_mark=busLineOverlay.GetMarker(i);

                //添加站点mark
                temp_en_Marker.add(busLineOverlay.GetMarker(i));
            }
        } else {//反向则反向遍历
            for(int i=end_index-1;i<start_index-1;i++){
                //将公交站点顺序入list
                Intermediate_site.add(busStationItemList.get(i));
                //添加站点mark
                temp_en_Marker.add(busLineOverlay.GetMarker(i));
            }
        }
        Log.i("输出《《《《","总公交中转长度交站长度"+Intermediate_site.size());
        //测试使用busBusRoutOverlay
        //根据终起点和中间站点设置绘制线路图测量距离
       // BusRouteOverlay<=BusPath <=List<BusStep> e = new ArrayList();<=List<RouteBusLineItem> b = new ArrayList();<=List<BusStationItem>
        //定义RouteBusLineItem
        Intermediate_site=busStationItemList;
        /*
        //设置起点和终点
        LatLonPoint star_point= AMapUtil.convertToLatLonPoint(new LatLng(30.677069535416102,104.14154872298242));
        LatLonPoint end_point=AMapUtil.convertToLatLonPoint(new LatLng(30.679264479556,104.154222160577));;

        //创建对应List<RouteBusLineItem>
        List<RouteBusLineItem> temp_Route=new ArrayList<>();
        //创建List<BusStep>
        List<BusStep> temp_busstep=new ArrayList<>();
        //创建BusPath
        BusPath temp_buspath=new BusPath();
        //添加变量
        //设置temp_Route
        RouteBusLineItem temp_route1=new RouteBusLineItem();
        //定义中间站点
        temp_route1.setPassStations(Intermediate_site);
        temp_route1.setPolyline(pointList);
        //设置启始和终结站点
        temp_route1.setDepartureBusStation(busStationItemList.get(0));
        temp_route1.setArrivalBusStation(busStationItemList.get(busStationItemList.size()-1));
        temp_Route.add(temp_route1);

        //设置busstep
        BusStep temp_busstep1=new BusStep();
        //设置公交线路
        temp_busstep1.setBusLines(temp_Route);
        //设置步行线路
        temp_busstep.add(temp_busstep1);
        temp_buspath.setSteps(temp_busstep);

        my_route_overlay=new BusRouteOverlay(SecondActivity.this,aMap,temp_buspath,star_point,end_point);
        //添加
        my_route_overlay.addToMap();
        //获取步行方案
        */
        //首先更改中间站点标志
        if(!Intermediate_te_marker.isEmpty()){
            for (Marker temp_mark : Intermediate_te_marker) {
                //将上一组Mark换回来
                temp_mark.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.amap_bus));;
            }
        }
        Intermediate_te_marker=temp_en_Marker;

        //显示到最近站点的步行路线
        //设置查询到最近站点的步行路线
        //绘制步行路线

        //得到中间点列表

       // List<LatLonPoint> pass_location=AMapUtil.convertArrList3( Intermediate_site);
        show_walk_line(AMapUtil.convertToLatLonPoint(start_location), start_station.getLatLonPoint());
        //绘制公交路线
       // show_bus_line(start_station.getLatLonPoint(),end_station.getLatLonPoint(),pass_location);
        //绘制最后步行路线
        show_walk_line( end_station.getLatLonPoint(),AMapUtil.convertToLatLonPoint(end_location));
        //更改中间站点标志



    }
    //设置查询函数更新线路在walkRoutteSearched中绘出线路
    public void show_walk_line(LatLonPoint startpoint ,LatLonPoint end_station){
             RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startpoint, end_station);
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
            //在查询成功中更新UI
        }
    //更新在查询返回中公交线路
    public void show_bus_line(LatLonPoint start_station ,LatLonPoint end_station,List<LatLonPoint> bus_position){
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                start_station, end_station);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, bus_position,
                null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示
        mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询

    }
    //改变中间站ioc
    public void change_markerioc(List<Marker> marker_list) {
        //如果不为空则先清除再更改图标
        if(!Intermediate_te_marker.isEmpty()){
            for (Marker temp_mark : Intermediate_te_marker) {
                //将上一组Mark换回来
                temp_mark.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.amap_bus));;
            }
        }
        for (Marker temp_mark : marker_list) {
            //更换中间点
            temp_mark.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.amap_notacion_bus2));
            //temp_mark.getOptions();
        }
        //更改新marker
        Intermediate_te_marker=marker_list;
    }
    //修改地图中心
    public void change_center(){
        //修改地图显示中心和级别
        LatLng temp=new LatLng(30.5568630000,104.0003990000);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp, 15));
        if(target_point_marker==null){
            //获得终点marker
            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.amap_end);
            Bitmap bitmap2=ScaleBitmap(bitmap1,0.5f);
            target_point_marker= aMap.addMarker(new MarkerOptions().position(temp)
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap2))//添加标记
                    .draggable(true)//设置标记可拖动
                    // .position(new LatLng(30.675242449184851f,104.1484981364249))
                    .anchor(0.5f, 0.5f));
            target_point_marker.setVisible(is_show_target_maker);
        }else{
                           target_point_marker.setPosition(temp);
            }
    }

}
