package cn.djzhao.firmonitor;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.djzhao.firmonitor.db.AppListItem;
import cn.djzhao.firmonitor.dialog.AddItemDialog;
import cn.djzhao.firmonitor.util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private SwipeRefreshLayout swipeRefresh;
    private TextView emptyTxt;
    private RecyclerView itemList;

    private Context mContext = this;
    private FloatingActionButton fab;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();
    }

    @Override
    protected void findViewById() {
        swipeRefresh = findViewById(R.id.content_swipe_refresh);
        emptyTxt = findViewById(R.id.content_empty_txt);
        itemList = findViewById(R.id.content_recycler_view);
        fab = findViewById(R.id.fab);
        drawer = findViewById(R.id.drawer_layout);
    }

    @Override
    protected void initView() {
        fab.setOnClickListener(this);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        Snackbar.make(swipeRefresh, "Replace with your own action", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                addNewApp();
                break;
        }
    }

    /**
     * 通过key获取value
     *
     * @param key    需要取值的键
     * @param target 需要解析的字符串
     * @return 所取的值
     */
    private String getValue(String key, String target) {
        String result = null;
        int index = target.indexOf("\"" + key + "\"");
        int start = index + key.length() + 4;
        int end = target.indexOf("\"", start);
        if ("created_at".equals(key)) {
            start = start - 1;
            end = target.indexOf(",", start);
        }
        result = target.substring(start, end);
        return result;
    }

    /**
     * 存储APP到数据库
     *
     * @param value 需要解析的字符串
     */
    private boolean saveApp(String value) {

        String short_url = getValue("short", value);
        AppListItem first = DataSupport.where("short_url = ?", short_url).findFirst(AppListItem.class);
        if (first != null) {
            Snackbar.make(fab, "请勿重复添加", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        AppListItem item = new AppListItem();
        item.setShort_url(short_url);
        item.setAppIconUrl(getValue("icon_url", value));
        item.setAppName(getValue("name", value));
        item.setAppVersion(getValue("version", value));
        item.setPlatform(getValue("type", value));
        Long time = Long.valueOf(getValue("created_at", value) + "000");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); // 2019-02-20 23:34
        Date date = new Date(time);
        item.setUpdateTime(sdf.format(date));
        return item.save();
    }

    private void addNewApp() {
        final AddItemDialog addItemDialog = new AddItemDialog(this);
        addItemDialog.setOnCancelClickedListener(new AddItemDialog.onCancelClickedListener() {
            @Override
            public void onClick() {
                addItemDialog.dismiss();
            }
        });
        addItemDialog.setOnAddClickedListener(new AddItemDialog.onAddClickedListener() {
            @Override
            public void onClick(String title) {
                String url = "https://download.fir.im/";
                showProgressDialog("添加中，请等待...");
                HttpUtil.sendOkHttpRequest(url + title, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        closeProgressDialog();
                        Snackbar.make(fab, "网络异常", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String string = response.body().string();

                        if (string.indexOf("errors") != -1) {
                            Snackbar.make(fab, "不存在此APP", Snackbar.LENGTH_SHORT).show();
                        } else {
                            if (saveApp(string)) {
                                addItemDialog.dismiss();
                            }
                        }
                        closeProgressDialog();
                    }
                });

            }
        });
        addItemDialog.show();
    }
}
