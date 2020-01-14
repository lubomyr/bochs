package net.sourceforge.bochs;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.sourceforge.bochs.adapter.ViewPagerAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static String appPath;
    private String configPath;
    private SharedPreferences sPref;
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

        //initToolbar();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Tab layout setup ( divider beetwin tab)
        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(ContextCompat.getColor(this, R.color.colorBorder));
            drawable.setSize(2, 1);
            ((LinearLayout) root).setDividerPadding(20);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }

        ImageView startBtn = (ImageView) findViewById(R.id.start);
        startBtn.setOnClickListener(this);

        ImageView downloadBtn = (ImageView) findViewById(R.id.download);
        downloadBtn.setOnClickListener(this);

        setupViewPager(viewPager);

        verifyStoragePermissions();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                save();
                break;
            case R.id.download:
                downloadImages();
                break;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Tab titles
        String[] tabs = {getString(R.string.storage), getString(R.string.hardware), getString(R.string.misc)};

        StorageTabFragment storageTabFragment =  new StorageTabFragment();
        adapter.addFragment(storageTabFragment, tabs[0]);
        HardwareTabFragment hardwareTabFragment =  new HardwareTabFragment();
        adapter.addFragment(hardwareTabFragment, tabs[1]);
        MiscTabFragment miscTabFragment =  new MiscTabFragment();
        adapter.addFragment(miscTabFragment, tabs[2]);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
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
            try {
                Config.readConfig(configPath);
            } catch (FileNotFoundException e) {
                Toast.makeText(MainActivity.this, getString(R.string.config_not_found), Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(MainActivity.this, getString(R.string.config_loaded), Toast.LENGTH_SHORT).show();
            Config.configLoaded = true;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    createDirIfNotExists();
                    checkConfig();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void verifyStoragePermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            String[] PERMISSIONS_STORAGE = new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };

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
