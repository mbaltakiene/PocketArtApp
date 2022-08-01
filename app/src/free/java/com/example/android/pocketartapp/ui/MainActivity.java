package com.example.android.pocketartapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pocketartapp.BuildConfig;
import com.example.android.pocketartapp.PaintingsWidgetService;
import com.example.android.pocketartapp.R;
import com.example.android.pocketartapp.ui.FetchDataTask.AsyncTaskCompleteListener;
import com.example.android.pocketartapp.utils.FirebaseUtils;
import com.example.android.pocketartapp.utils.QueryUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AsyncTaskCompleteListener {

    /**
     * Position selected on the RecyclerView and passed to the DetailsActivity
     */
    public final static String STARTING_POSITION_KEY = "startingPositionKey";
    /**
     * Position received form the DetailsActivity, for the RecyclerView to be repositioned
     */
    public final static String CURRENT_POSITION_KEY = "currentPositionKey";
    /**
     * Data key to retrieve teh data on saved instance state
     */
    public final static String DATA_KEY = "dataKey";
    /**
     * Request code to receive the current position from the DetailsActivity
     */
    public final static int REQUEST_CODE = 1;
    /**
     * Logging tag
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    /**
     * Paintings data JSON url
     */
    private static final String PAINTINGS_API_URL = "http://testandroidapp.eu3.org/data/catalog.json";
    /**
     * Constant to set RecycleView layout parameter onSaveInstanceState
     */
    private static final String RECYCLER_VIEW_STATE = "viewState";

    /**
     * RecyclerView adapter
     */
    PaintingsListAdapter mAdapter;
    /**
     * RecyclerView
     */
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    /**
     * TextView when there is no data to display
     */
    @BindView(R.id.empty_text_view)
    TextView mNoDataTextView;

    /**
     * Refreshing layout on swipe
     */
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    /**
     * Firebase authorisation
     */
    private FirebaseAuth mAuth;

    /**
     * Paintings data
     */
    private ArrayList<Painting> mPaintings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshLayout();
                    }
                }
        );

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mAdapter = new PaintingsListAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            Parcelable state = savedInstanceState.getParcelable(RECYCLER_VIEW_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(state);
            mPaintings = savedInstanceState.getParcelableArrayList(DATA_KEY);
            mAdapter.setData(mPaintings);
            showDataView();

            if (BuildConfig.FLAVOR.equals(getString(R.string.free))) {
                showAdBanner();
            }

        } else {
            mAuth = FirebaseAuth.getInstance();
            signInAnonymously();
            refreshLayout();
        }

    }

    private void showAdBanner(){
        // Banner ad
        AdView mAdView = (AdView) findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable state = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(RECYCLER_VIEW_STATE, state);
        outState.putParcelableArrayList(DATA_KEY, mPaintings);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra(CURRENT_POSITION_KEY, 0);
                mRecyclerView.scrollToPosition(position);
            }
        }
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.GONE);
        mNoDataTextView.setVisibility(View.VISIBLE);
    }

    private void showDataView() {
        mNoDataTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Update the contents of the screen
     */
    private void refreshLayout() {
        if (!QueryUtils.isConnected(this)) {
            Log.i("noInternet", "offline");
            DatabaseReference mDatabase = FirebaseUtils.getDatabase();
            // Values are retrieved asyncronously from Firebase Realtime Database
            mDatabase.addValueEventListener(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get paintings in datasnapshot
                            if (dataSnapshot != null) {
                                collectData(dataSnapshot);
                                showDataView();
                            } else {
                                showErrorMessage();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });

        } else {
            showDataView();
            if (BuildConfig.FLAVOR.equals(getString(R.string.free))) {
                showAdBanner();
            }
            FetchDataTask dataTask = new FetchDataTask(this);
            dataTask.execute(PAINTINGS_API_URL);
        }
        mRefreshLayout.setRefreshing(false);
    }

    private void collectData(DataSnapshot dataSnapshot) {
        mPaintings = new ArrayList<>();
        for (DataSnapshot entry : dataSnapshot.getChildren()) {
            //Get single painting
            Painting painting = entry.getValue(Painting.class);
            //Add to data list
            mPaintings.add(painting);
        }
        mAdapter.setData(mPaintings);
        PaintingsWidgetService.startActionSetPaintingToWidget(this);
    }

    @Override
    public void onTaskComplete(Object result) {
        mPaintings = (ArrayList<Painting>) result;
        mAdapter.setData((ArrayList<Painting>) mPaintings);
        PaintingsWidgetService.startActionSetPaintingToWidget(this);
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in successful
                            Log.i(LOG_TAG, "Authentication successful.");
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
