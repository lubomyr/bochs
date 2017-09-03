package net.sourceforge.bochs;

import android.Manifest;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import net.sourceforge.bochs.adapter.TabsPagerAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
    private String appPath;
    private String configPath;
    private ViewPager viewPager;
    private ActionBar actionBar;
    private SharedPreferences sPref;
    private AdView mAdView;

    final String SAVED_PATH = "saved_path";
    private final int REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        appPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Android/data/" + getPackageName() + "/files/";
        configPath = appPath + "bochsrc.txt";

        if (!Config.configLoaded)
            Config.setDefaulValues();

        verifyStoragePermissions();
        setupTabs();
        setAds();
    }

    private void setAds() {
        MobileAds.initialize(getApplicationContext(),
                getString(R.string.app_id));

        mAdView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLeftApplication ()
            {
                if (mAdView != null) {
                    mAdView.destroy();
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void setupTabs() {
        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        TabsPagerAdapter mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Tab titles
        String[] tabs = {getString(R.string.storage), getString(R.string.hardware), getString(R.string.misc)};

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.start:
                save();
                break;
            case R.id.download:
                downloadImages();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    static String getFileName(String path) {
        String result;
        if (path.contains("/"))
            result = path.substring(path.lastIndexOf("/") + 1, path.length());
        else
            result = path;
        return result;
    }

    private void save() {
        try {
            Config.writeConfig(configPath);
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, getString(R.string.config_not_saved), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(MainActivity.this, getString(R.string.config_saved), Toast.LENGTH_SHORT).show();

        // run bochs app
        //ComponentName cn = new ComponentName("net.sourceforge.bochs", "net.sourceforge.bochs.MainActivity");
        Intent intent = new Intent(this, SDLActivity.class);
        //intent.setComponent(cn);
        startActivity(intent);
    }

    private void checkConfig() {
        if (!Config.configLoaded) {
            Config.configLoaded = true;
            try {
                Config.readConfig(configPath);
            } catch (FileNotFoundException e) {
                Toast.makeText(MainActivity.this, getString(R.string.config_not_found), Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(MainActivity.this, getString(R.string.config_loaded), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean createDirIfNotExists() {
        boolean ret = true;

        File file = new File(appPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                ret = false;
            }
        }
        return ret;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    createDirIfNotExists();
                    checkConfig();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void verifyStoragePermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            String[] PERMISSIONS_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};

            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return;
        }
        createDirIfNotExists();
        checkConfig();
    }

    private void downloadImages() {
        final String urls[] = {
                "https://sourceforge.net/projects/libsdl-android/files/Bochs/mulinux13r2.img/download",
                "https://sourceforge.net/projects/libsdl-android/files/Bochs/FreeDos.vdi/download",
                "https://sourceforge.net/projects/libsdl-android/files/Bochs/tinycore-2.1-x86.vdi/download",
                "https://sourceforge.net/projects/libsdl-android/files/Bochs/LucidPuppy-520.vdi/download",
        };
        final String names[] = {
                "muLinux - 34 Mb",
                "FreeDOS - 114 Mb",
                "Tiny Core Linux - 123 Mb",
                "Puppy Linux - 690 Mb",
        };

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.download_disk_images))
                .setItems(names, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        String destination = Uri.parse(urls[i]).getPathSegments()
                                .get(Uri.parse(urls[i]).getPathSegments().size() - 2);
                        Log.d("BOCHS", "Downloading image " + urls[i] + " to " + destination);
                        DownloadManager downloader = (DownloadManager) MainActivity.this
                                .getSystemService(Context.DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urls[i]));
                        request.setTitle(names[i]);
                        request.setDescription(urls[i]);
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                destination);
                        downloader.enqueue(request);
                        dialog.dismiss();
                        setPathToDownload();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void setPathToDownload() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_PATH, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        ed.apply();
    }

}
