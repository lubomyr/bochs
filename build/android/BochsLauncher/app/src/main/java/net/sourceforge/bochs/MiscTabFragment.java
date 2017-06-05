package net.sourceforge.bochs;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MiscTabFragment extends Fragment {
    private TextView tvRomImage;
    private TextView tvVgaRomImage;
    private CheckBox cbFullscreen;
    private SeekBar sbVgaUpdateFreq;
    private TextView tvVgaUpdateFreq;
    private CheckBox cbSpeaker;

    private enum Requestor {ROM, VGAROM}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_misc, container, false);
        setupView(rootView);

        return rootView;
    }

    private void setupView(View rootView) {
        final List<String> syncList = Arrays.asList("none", "slowdown", "realtime", "both");
        final int minValueVgaUpdateFreq = 5;
        Spinner spClockSync = (Spinner) rootView.findViewById(R.id.miscSpinnerClockSync);
        Button btRomImage = (Button) rootView.findViewById(R.id.miscButtonRomImage);
        Button btVgaRomImage = (Button) rootView.findViewById(R.id.miscButtonVgaRomImage);
        tvRomImage = (TextView) rootView.findViewById(R.id.miscTextViewRomImage);
        tvVgaRomImage = (TextView) rootView.findViewById(R.id.miscTextViewVgaRomImage);
        cbFullscreen = (CheckBox) rootView.findViewById(R.id.miscCheckBoxFullscreen);
        sbVgaUpdateFreq = (SeekBar) rootView.findViewById(R.id.miscSeekBarVgaUpdateFreq);
        tvVgaUpdateFreq = (TextView) rootView.findViewById(R.id.miscTextViewVgaUpdateFreq);
        cbSpeaker = (CheckBox) rootView.findViewById(R.id.miscCheckBoxSpeaker);
        SpinnerAdapter syncAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_row, syncList);
        spClockSync.setAdapter(syncAdapter);
        tvRomImage.setText(MainActivity.getFileName(Config.romImage));
        tvVgaRomImage.setText(MainActivity.getFileName(Config.vgaRomImage));
        cbFullscreen.setChecked(Config.fullscreen);
        cbSpeaker.setChecked(Config.useSpeaker);
        spClockSync.setSelection(syncList.indexOf(Config.clockSync));
        sbVgaUpdateFreq.setProgress(Config.vgaUpdateFreq - minValueVgaUpdateFreq);
        tvVgaUpdateFreq.setText(String.valueOf(Config.vgaUpdateFreq));

        cbFullscreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        Config.fullscreen = cbFullscreen.isChecked();
                                                    }
                                                }
        );

        cbSpeaker.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        Config.useSpeaker = cbSpeaker.isChecked();
                                                    }
                                                }
        );

        spClockSync.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                Config.clockSync = syncList.get(p3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> p1) {
            }
        });

        sbVgaUpdateFreq.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
                tvVgaUpdateFreq.setText(String.valueOf(minValueVgaUpdateFreq + sbVgaUpdateFreq.getProgress()));
                Config.vgaUpdateFreq = minValueVgaUpdateFreq + sbVgaUpdateFreq.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar p1) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar p1) {
            }
        });

        OnClickListener miscOnClick = new OnClickListener() {

            @Override
            public void onClick(View p1) {
                switch (p1.getId()) {
                    case R.id.miscButtonRomImage:
                        fileSelection(Requestor.ROM);
                        break;
                    case R.id.miscButtonVgaRomImage:
                        fileSelection(Requestor.VGAROM);
                        break;
                }
            }
        };

        btRomImage.setOnClickListener(miscOnClick);
        btVgaRomImage.setOnClickListener(miscOnClick);
    }

    private void fileSelection(final Requestor num) {
        String appPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/Android/data/" + getActivity().getPackageName() + "/files/";
        FileChooser filechooser = new FileChooser(getActivity(), appPath, null);
        filechooser.setFileListener(new FileChooser.FileSelectedListener() {
            @Override
            public void fileSelected(final File file) {
                String filename = file.getAbsolutePath();
                Log.d("File", filename);
                switch (num) {
                    case ROM:
                        tvRomImage.setText(file.getName());
                        Config.romImage = filename;
                        break;
                    case VGAROM:
                        tvVgaRomImage.setText(file.getName());
                        Config.vgaRomImage = filename;
                        break;
                }

            }
        });
        filechooser.showDialog();
    }

}
