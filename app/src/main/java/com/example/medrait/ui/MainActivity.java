package com.example.medrait.ui;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;


import com.example.medrait.BuildConfig;
import com.example.medrait.R;
import com.example.medrait.adapter.PhotoAdapter;
import com.example.medrait.model.PhotoModel;
import com.example.medrait.model.event.SearchEvent;
import com.example.medrait.service.FlickrService;
import com.example.medrait.util.AppUtil;
import com.example.medrait.util.RowClickListener;
import com.example.medrait.util.ScreenStateManager;
import com.google.android.material.navigation.NavigationView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import timber.log.Timber;

public class MainActivity
        extends AbstractBaseActivity
        implements RowClickListener<PhotoModel>,
        SwipeRefreshLayout.OnRefreshListener{

    private int page = 1;
    private boolean isLoading;
    private final FlickrService flickrService = FlickrService.INSTANCE;
    private PhotoAdapter adapter;
    private ScreenStateManager screenStateManager;

    private SearchView searchView;
    private ArrayList<PhotoModel> photoModelArrayList;
    protected Toolbar toolbar;
    protected SwipeRefreshLayout swipe;
    protected LinearLayout linear;
    protected RecyclerView recycler;

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = findViewById(R.id.toolbar);
        swipe = findViewById(R.id.swipe);
        linear =findViewById(R.id.linear);
        recycler =findViewById(R.id.recycler);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        photoModelArrayList= new ArrayList<>();

        swipe.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent));
        swipe.setOnRefreshListener(this);
        whiteNotificationBar(recycler);

        adapter = new PhotoAdapter();
        adapter.setRowClickListener(this);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new GridLayoutManager(this,2));
        adapter.notifyDataSetChanged();

        setScrollListener();


        screenStateManager = new ScreenStateManager(linear);

        sendRequest();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Parcelable listState = recycler.getLayoutManager().onSaveInstanceState();
        // putting recyclerview position
        outState.putParcelable("1", listState);
        super.onSaveInstanceState(outState);


    }

    @Override
    public void onRowClicked(int row, PhotoModel item) {
    }

    @Override
    public void onRefresh() {
        page = 1;
        sendRequest();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SearchEvent event) {
        isLoading = false;

        // fired by pull to refresh
        if (swipe.isRefreshing()) {
            swipe.setRefreshing(false);
            adapter.clear();
        }

        if (isScreenEmpty()) {
            if (event.exception != null) {
                screenStateManager.showError(R.string.errorMessage);
            } else if (AppUtil.isNullOrEmpty(event.item)) {
                screenStateManager.showEmpty(R.string.emptyText);
            } else {
                screenStateManager.hideAll();
                adapter.addAll(event.item);
            }
        } else {
            adapter.remove(adapter.getItemCount() - 1); //remove progress item
            if (event.exception != null) {
                showSnack(R.string.errorMessage);
            } else if (AppUtil.isNullOrEmpty(event.item)) {
                showSnack(R.string.emptyText);
            } else {
                adapter.addAll(event.item);
            }
        }
    }

    private void sendRequest() {
        Timber.i("sendRequest: " + page);
        if (AppUtil.isConnected()) {
            isLoading = true;
            flickrService.searchAsync(page++);
            if (isScreenEmpty()) screenStateManager.showLoading();
            else
                adapter.addAll(null); // add null , so the adapter will check view_type and show progress bar at bottom
        } else {
            swipe.setRefreshing(false);
            if (isScreenEmpty()) screenStateManager.showConnectionError();
            else showConnectionError();
        }
    }

    private void setScrollListener() {
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0 && totalItemCount >= FlickrService.PAGE_SIZE) {
                    sendRequest();
                }
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recent_photos, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }


    private boolean isScreenEmpty() {
        return adapter == null || adapter.getItemCount() == 0;
    }



}