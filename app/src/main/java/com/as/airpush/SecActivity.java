package com.as.airpush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.Random;

import static com.as.airpush.FragmentAdd.curFlag;
import static com.as.airpush.MainActivity.getJson;
import static com.as.airpush.MainActivity.messageList;
import static com.as.airpush.MainActivity.moduleMap;
import static com.as.airpush.MainActivity.notiList;
import static com.as.airpush.MainActivity.url;

public class SecActivity extends AppCompatActivity implements Runnable{


    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton floatButton;

//    List<MyMessage> messageList;
    private RecyclerView recyclerView;
    public static MessageAdapter adapter;
    GridLayoutManager layoutManager;
    public static SwipeRefreshLayout swipeRefreshLayout;

    private boolean refreshFlag;

    String TAG = "SecAvtivity";

    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sec);
//        Log.e(TAG, "onCreate: ----------------------------->>>" );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Log.e(TAG, "onCreate: ----------------------------->>>>>>" );
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            |  View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.parseColor("#320671ab"));
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.toolbar_logo);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.colLayout);
        collapsingToolbarLayout.setTitle("Hello, AirPush!");

        floatButton = (FloatingActionButton) findViewById(R.id.floatButton);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        if (!refreshFlag) {
            swipeRefreshLayout.setRefreshing(true);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateNoti();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);


        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentAdd fragmentAdd = new FragmentAdd();
//                fragmentAdd.show(getFragmentManager(), "Test!");

                showPopupMenu(v);

            }
        });

        new Thread(this).start();


        handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case 0:
//                        new MainActivity.MyAsync().execute( url + "/info");
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };

    }

    @Override
    public void run() {

        while(true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!refreshFlag) {
                swipeRefreshLayout.setRefreshing(false);
                refreshFlag = true;
            }

            updateNoti();

        }
    }

    public void updateNoti(){

        SecActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });

        String json = getJson(url + "/noti");
        int flag = 0;
        try {

            JSONObject jb = new JSONObject(json);
            flag = jb.getInt("argc");
            Log.e(TAG, "getNoti: "+ flag );

            if(flag > 0){

                JSONArray jsonArray = jb.getJSONArray("args");

                Message message = new Message();
                message.what = 0;
//                handler.sendMessage(message);

                SecActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new MainActivity.MyAsync().execute( url + "/info");
                        adapter.notifyDataSetChanged();

                    }
                });

                notiList.clear();
                for(int i = 0; i < jsonArray.length(); i++){
                    notiList.add(jsonArray.getString(i));
                    Log.e(TAG, "getNoti: "+ jsonArray.getString(i) );
                }

                for (int j = 0; j < flag; j++) {
                    Intent intent = new Intent(this, SecActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this , 0, intent, 0);

                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification notification = new NotificationCompat.Builder(this)
                            .setContentTitle(notiList.get(j))
                            .setContentText(notiList.get(j))
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_round ))
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .build();
                    manager.notify(new Random().nextInt(1000), notification);
                }
//                manager.can

            }

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);

        Menu menu_more = popupMenu.getMenu();

        int n = 0;

        if (!moduleMap.isEmpty()) {
            for (String str: moduleMap.keySet()) {
                menu_more.add(1, n, n, str);
                n++;
            }
        }else{
            for(int i = 0; i < 4; i++){
                menu_more.add(1, i, i, "Menu "+ i);
            }
        }

//        popupMenu.getMenuInflater().inflate(R.menu.menu_local, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                curFlag = item.getItemId();

                FragmentAdd fragmentAdd = new FragmentAdd();
                fragmentAdd.show(getFragmentManager(), "Test!");

                return true;
            }
        });
        popupMenu.show();
    }
}
