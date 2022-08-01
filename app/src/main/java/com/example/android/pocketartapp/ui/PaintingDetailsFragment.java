package com.example.android.pocketartapp.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.pocketartapp.R;
import com.example.android.pocketartapp.utils.QueryUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.graphics.Color.TRANSPARENT;
import static android.transition.TransitionSet.ORDERING_TOGETHER;

public class PaintingDetailsFragment extends Fragment {

    /* Permission request code identifier */
    private static final int PERMISSION_REQUEST_CODE = 200;
    /**
     * Collapsing toolbar
     */
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    /**
     * Toolbar
     */
    @BindView(R.id.details_toolbar)
    Toolbar mToolbar;

    /**
     * TexView for Painting description
     */
    @BindView(R.id.description_text_view)
    TextView mDescriptionView;

    /**
     * TextView for Painting title
     */
    @BindView(R.id.title_text_view)
    TextView mTitleView;

    /**
     * TextView for Painting author
     */
    @BindView(R.id.author_text_view)
    TextView mAuthorView;

    /**
     * TextView for Painting year
     */
    @BindView(R.id.year_text_view)
    TextView mDateView;

    /**
     * TextView for Painting location
     */
    @BindView(R.id.location_text_view)
    TextView mLocationView;

    /**
     * TextView for Painting technique
     */
    @BindView(R.id.technique_text_view)
    TextView mTechniqueView;
    /**
     * ImageView for the Painting
     */
    @BindView(R.id.image_view)
    ImageView mImageView;
    /**
     * Painting image url
     */
    private String mImageUrl;
    /**
     * Painting title
     */
    private String mTitle;
    /**
     * Painting author
     */
    private String mAuthor;
    /**
     * Painting description
     */
    private String mDescription;
    /**
     * Painting technique
     */
    private String mTechnique;
    /**
     * Painting location
     */
    private String mLocation;
    /**
     * Painting date
     */
    private String mDate;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment
     */
    public PaintingDetailsFragment() {
    }

    public static PaintingDetailsFragment newInstance(Painting painting) {

        Bundle args = new Bundle();
        args.putString(QueryUtils.PAINTING_TITLE, painting.getTitle());
        args.putString(QueryUtils.PAINTING_AUTHOR, painting.getAuthor());
        args.putString(QueryUtils.PAINTING_URL, painting.getUrl());
        args.putString(QueryUtils.PAINTING_LOCATION, painting.getLocation());
        args.putString(QueryUtils.PAINTING_DESCRIPTION, painting.getDescription());
        args.putString(QueryUtils.PAINTING_TECHNIQUE, painting.getTechnique());
        args.putString(QueryUtils.PAINTING_DATE, painting.getDate());

        PaintingDetailsFragment fragment = new PaintingDetailsFragment();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                TransitionSet transitionSet = new TransitionSet();
                transitionSet.setOrdering(ORDERING_TOGETHER);
                transitionSet.addTransition(new ChangeBounds()).
                        addTransition(new ChangeTransform()).
                        addTransition(new ChangeImageTransform());
                fragment.setSharedElementEnterTransition(transitionSet);
            }
            fragment.setEnterTransition(new Fade());
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageUrl = getArguments().getString(QueryUtils.PAINTING_URL);
        mTitle = getArguments().getString(QueryUtils.PAINTING_TITLE);
        mAuthor = getArguments().getString(QueryUtils.PAINTING_AUTHOR);
        mDescription = getArguments().getString(QueryUtils.PAINTING_DESCRIPTION);
        mTechnique = getArguments().getString(QueryUtils.PAINTING_TECHNIQUE);
        mLocation = getArguments().getString(QueryUtils.PAINTING_LOCATION);
        mDate = getArguments().getString(QueryUtils.PAINTING_DATE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide(Gravity.BOTTOM);
            slide.addTarget(R.id.image_view);
            slide.setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.interpolator.linear_out_slow_in));
            slide.setDuration(250);
            getActivity().getWindow().setEnterTransition(slide);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            getActivity().getWindow().setStatusBarColor(TRANSPARENT);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.details_menu, menu);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_painting_details, container, false);

        final AppBarLayout appBarLayout = (AppBarLayout) rootView.findViewById(R.id.appbar_details);
        //Collapse appbar in D-pad navigation mode
        appBarLayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.hasFocus()) {
                    appBarLayout.setExpanded(true);
                } else {
                    appBarLayout.setExpanded(false);
                }
            }
        });
        ButterKnife.bind(this, rootView);

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbar.inflateMenu(R.menu.details_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_share) {
                    share();
                    return true;
                }
                return false;
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        // Views for the painting Palette colors
        final View vibrantColor = rootView.findViewById(R.id.vibrant);
        final View vibrantDarkColor = rootView.findViewById(R.id.dark_vibrant);
        final View vibrantLightColor = rootView.findViewById(R.id.light_vibrant);
        final View mutedColor = rootView.findViewById(R.id.muted);
        final View mutedDarkColor = rootView.findViewById(R.id.dark_muted);
        final View mutedLightColor = rootView.findViewById(R.id.light_muted);


        mImageView = rootView.findViewById(R.id.image_view);
        if (!TextUtils.isEmpty(mImageUrl)) {
            Picasso.with(getContext())
                    .load(mImageUrl)
                    .into(mImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                            if (bitmap != null) {
                                Palette p = Palette.from(bitmap).generate();
                                vibrantColor.setBackgroundColor(p.getVibrantColor(getResources().getColor(R.color.colorDarkGray)));
                                vibrantDarkColor.setBackgroundColor(p.getDarkVibrantColor(getResources().getColor(R.color.colorDarkGray)));
                                vibrantLightColor.setBackgroundColor(p.getLightVibrantColor(getResources().getColor(R.color.colorDarkGray)));
                                mutedColor.setBackgroundColor(p.getMutedColor(getResources().getColor(R.color.colorDarkGray)));
                                mutedDarkColor.setBackgroundColor(p.getDarkMutedColor(getResources().getColor(R.color.colorDarkGray)));
                                mutedLightColor.setBackgroundColor(p.getLightMutedColor(getResources().getColor(R.color.colorDarkGray)));
                                mCollapsingToolbar.setStatusBarScrimColor(p.getDarkMutedColor(getResources().getColor(R.color.colorDarkGray)));
                                mCollapsingToolbar.setContentScrimColor(p.getMutedColor(getResources().getColor(R.color.colorLightGray)));
                            }
                        }

                        @Override
                        public void onError() {
                            //do nothing
                        }
                    });
        }

        mImageView.setContentDescription(mTitle);
        mDescriptionView.setText(mDescription);
        mTitleView.setText(mTitle);
        mAuthorView.setText(mAuthor);
        mDateView.setText(mDate);
        mLocationView.setText(mLocation);
        mTechniqueView.setText(mTechnique);
        return rootView;
    }

    // Share button functionality
    private void share() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously*
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            // Permission has already been granted
            Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                    bitmap, mTitle, mTitle + getString(R.string.by) + mAuthor);
            Uri imageUri = Uri.parse(path);
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("image/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            if (sharingIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
            }
        }
    }
}
