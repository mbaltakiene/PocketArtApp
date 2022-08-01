package com.example.android.pocketartapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.android.pocketartapp.R;
import com.example.android.pocketartapp.utils.FirebaseUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailsActivity extends AppCompatActivity {

    /**
     * ViewPager that controls Fragments with paintings details
     */
    @BindView(R.id.pager)
    ViewPager mPager;
    /**
     * Paintings data
     */
    private List<Painting> mPaintings;
    /**
     * PagerAdapter for Fragments
     */
    private PagerAdapter mPagerAdapter;
    /**
     * Current position of the pager
     */
    private int mCurrentPosition;
    /**
     * Initial position of the pager
     */
    private int mStartingPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        if (getIntent() != null) {
            mStartingPosition = getIntent().getIntExtra(MainActivity.STARTING_POSITION_KEY, 0);
        }
        if (savedInstanceState == null) {
            mCurrentPosition = mStartingPosition;
        } else {
            mCurrentPosition = savedInstanceState.getInt(MainActivity.CURRENT_POSITION_KEY);
        }

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(mCurrentPosition);

        // fix for a disappearing status bar
        // example taken from:
        // https://www.programcreek.com/java-api-examples/?class=android.support.v4.view.ViewCompat&method=setOnApplyWindowInsetsListener
        ViewCompat.setOnApplyWindowInsetsListener(mPager, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v,
                                                          WindowInsetsCompat insets) {
                insets = ViewCompat.onApplyWindowInsets(v, insets);
                if (insets.isConsumed()) {
                    return insets;
                }

                boolean consumed = false;
                for (int i = 0, count = mPager.getChildCount(); i < count; i++) {
                    ViewCompat.dispatchApplyWindowInsets(mPager.getChildAt(i), insets);
                    if (insets.isConsumed()) {
                        consumed = true;
                    }
                }
                return consumed ? insets.consumeSystemWindowInsets() : insets;
            }
        });

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mPaintings != null) {
                    mCurrentPosition = position % mPaintings.size();
                }
            }
        });


        DatabaseReference mDatabase = FirebaseUtils.getDatabase();

        // Values are retrieved asyncronously from Firebase Realtime Database
        mDatabase.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Get paintings in datasnapshot
                        collectData(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra(MainActivity.CURRENT_POSITION_KEY, mCurrentPosition);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MainActivity.CURRENT_POSITION_KEY, mCurrentPosition);
    }

    private void collectData(DataSnapshot dataSnapshot) {
        mPaintings = new ArrayList<>();
        for (DataSnapshot entry : dataSnapshot.getChildren()) {
            //Get single painting
            Painting painting = entry.getValue(Painting.class);
            //Add to data list
            mPaintings.add(painting);
        }
        mPagerAdapter.notifyDataSetChanged();
        mPager.setCurrentItem(mStartingPosition, false);
    }

    class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PaintingDetailsFragment.newInstance(mPaintings.get(position));
        }

        @Override
        public int getCount() {
            return (mPaintings != null) ? mPaintings.size() : 0;
        }
    }
}
