package org.sfsteam.glassclink;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toolbar;


public class GlassActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_glass);
                break;
            case 2:
                mTitle = getString(R.string.title_beer_mug);
                break;
            case 3:
                mTitle = getString(R.string.title_shot);
                break;
            case 4:
                mTitle = getString(R.string.title_whiskey);
                break;
            case 5:
                mTitle = getString(R.string.title_plastic_cup);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        private static final int SHAKE_SENSITIVITY = 15;

        private SensorManager sensorManager;
        private float accel = SensorManager.GRAVITY_EARTH * 2.0f;
        private float accelPrevious = SensorManager.GRAVITY_EARTH * 2.0f;

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ImageView glass = (ImageView) rootView.findViewById(R.id.imageView);
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    glass.setImageURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.wine));
                    break;
                case 2:
                    glass.setImageURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.beer));
                    break;
                case 3:
                    glass.setImageURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.shot));
                    break;
                case 4:
                    glass.setImageURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.whiskey));
                    break;
                case 5:
                    glass.setImageURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.plastic));

            }

            sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            sensorManager.registerListener(
                    sensorListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);

            glass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onShake();
                }
            });

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((GlassActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        protected void onShake() {
            try {
                String fname = null;
                switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                    case 1:
                        fname = "wine_s";
                        break;
                    case 2:
                        fname = "beer_s";
                        break;
                    case 3:
                        fname = "shot_s";
                        break;
                    case 4:
                        fname = "whis_s";
                        break;
                    case 5:
                        fname = "plastic_s";
                }

                int resId = getResources().getIdentifier(fname, "raw", getActivity().getPackageName());
                MediaPlayer mp = MediaPlayer.create(getActivity(), resId);
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp1) {
                        mp1.release();

                    }

                    ;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onResume() {
            super.onResume();

            sensorManager.registerListener(
                    sensorListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onStop() {
            sensorManager.unregisterListener(sensorListener);

            super.onStop();
        }

        private final SensorEventListener sensorListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                accelPrevious = accel;
                accel = (float) Math.sqrt((double) (x * x + y * y + z * z));
                if (accel - accelPrevious > SHAKE_SENSITIVITY) {
                    onShake();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };
    }

}
