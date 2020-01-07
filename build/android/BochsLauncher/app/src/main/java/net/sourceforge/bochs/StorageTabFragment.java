package net.sourceforge.bochs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
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
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class StorageTabFragment extends Fragment implements OnClickListener {
    private SharedPreferences sPref;
    private final int floppyNum = Config.floppyNum;
    private final int ataNum = Config.ataNum;
    private final int FLOPPY_A = Config.FLOPPY_A;
    private final int FLOPPY_B = Config.FLOPPY_B;
    private final int ATA_0_MASTER = Config.ATA_0_MASTER;
    private final int ATA_0_SLAVE = Config.ATA_0_SLAVE;
    private final int ATA_1_MASTER = Config.ATA_1_MASTER;
    private final int ATA_1_SLAVE = Config.ATA_1_SLAVE;
    private final String NONE = Config.NONE;
    private final String DISK = Config.DISK;
    private final String CDROM = Config.CDROM;
    private final String FLOPPY = Config.FLOPPY;
    private final String VFAT = Config.VFAT;
    private TextView tvFloppy[] = new TextView[floppyNum];
    private CheckBox cbFloppy[] = new CheckBox[floppyNum];
    private Button btBrowseFloppy[] = new Button[floppyNum];
    private TextView tvAta[] = new TextView[ataNum];
    private CheckBox cbVvfatAta[] = new CheckBox[ataNum];
    private CheckBox cbAta[] = new CheckBox[ataNum];
    private Button btBrowseAta[] = new Button[ataNum];
    private Spinner spAtaType[] = new Spinner[ataNum];

    private String m_chosenDir = "";
    private boolean m_newFolderEnabled = true;
    final String SAVED_PATH = "saved_path";
    final int REQUEST_FILE = 1;
    private enum Requestor {ATA0_MASTER, ATA0_SLAVE, ATA1_MASTER, ATA1_SLAVE, FLOPPY_A, FLOPPY_B}
    private Requestor requestType = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_storage, container, false);
        setupView(rootView);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.storageButtonFloppyA:
                fileSelection(Requestor.FLOPPY_A, FLOPPY);
                break;
            case R.id.storageButtonFloppyB:
                fileSelection(Requestor.FLOPPY_B, FLOPPY);
                break;
            case R.id.storageButtonAta0m:
                if (cbVvfatAta[ATA_0_MASTER].isChecked())
                    dirSelection(Requestor.ATA0_MASTER);
                else
                    fileSelection(Requestor.ATA0_MASTER, Config.ataType[ATA_0_MASTER]);
                break;
            case R.id.storageButtonAta0s:
                if (cbVvfatAta[ATA_0_SLAVE].isChecked())
                    dirSelection(Requestor.ATA0_SLAVE);
                else
                    fileSelection(Requestor.ATA0_SLAVE, Config.ataType[ATA_0_SLAVE]);
                break;
            case R.id.storageButtonAta1m:
                if (cbVvfatAta[ATA_1_MASTER].isChecked())
                    dirSelection(Requestor.ATA1_MASTER);
                else
                    fileSelection(Requestor.ATA1_MASTER, Config.ataType[ATA_1_MASTER]);
                break;
            case R.id.storageButtonAta1s:
                if (cbVvfatAta[ATA_1_SLAVE].isChecked())
                    dirSelection(Requestor.ATA1_SLAVE);
                else
                    fileSelection(Requestor.ATA1_SLAVE, Config.ataType[ATA_1_SLAVE]);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FILE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String uriPath = uri.getPath();
            String filename = uriPath.substring(uriPath.lastIndexOf("/") + 1, uriPath.length());
            String filepathWOType = uriPath.substring(uriPath.lastIndexOf(":") + 1, uriPath.length());
            String filepath = filepathWOType.startsWith("/") ? filepathWOType
                    : Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filepathWOType;
            saveLastPath(filepath);
            switch (requestType) {
                case ATA0_MASTER:
                    tvAta[ATA_0_MASTER].setText(filename);
                    Config.ataImage[ATA_0_MASTER] = filepath;
                    Config.ataMode[ATA_0_MASTER] = getMode(filename);
                    break;
                case ATA0_SLAVE:
                    tvAta[ATA_0_SLAVE].setText(filename);
                    Config.ataImage[ATA_0_SLAVE] = filepath;
                    Config.ataMode[ATA_0_SLAVE] = getMode(filename);
                    break;
                case ATA1_MASTER:
                    tvAta[ATA_1_MASTER].setText(filename);
                    Config.ataImage[ATA_1_MASTER] = filepath;
                    Config.ataMode[ATA_1_MASTER] = getMode(filename);
                    break;
                case ATA1_SLAVE:
                    tvAta[ATA_1_SLAVE].setText(filename);
                    Config.ataImage[ATA_1_SLAVE] = filepath;
                    Config.ataMode[ATA_1_SLAVE] = getMode(filename);
                    break;
                case FLOPPY_A:
                    tvFloppy[FLOPPY_A].setText(filename);
                    Config.floppyImage[FLOPPY_A] = filepath;
                    break;
                case FLOPPY_B:
                    tvFloppy[FLOPPY_B].setText(filename);
                    Config.floppyImage[FLOPPY_B] = filepath;
                    break;
            }
        }
    }

    private void setupView(View rootView) {
        final List<String> typeList = Arrays.asList(DISK, CDROM);
        final List<String> bootList = Arrays.asList(DISK, CDROM, FLOPPY);
        cbFloppy[FLOPPY_A] = (CheckBox) rootView.findViewById(R.id.storageCheckBoxFloppyA);
        tvFloppy[FLOPPY_A] = (TextView) rootView.findViewById(R.id.storageTextViewFloppyA);
        btBrowseFloppy[FLOPPY_A] = (Button) rootView.findViewById(R.id.storageButtonFloppyA);
        cbFloppy[FLOPPY_B] = (CheckBox) rootView.findViewById(R.id.storageCheckBoxFloppyB);
        tvFloppy[FLOPPY_B] = (TextView) rootView.findViewById(R.id.storageTextViewFloppyB);
        btBrowseFloppy[FLOPPY_B] = (Button) rootView.findViewById(R.id.storageButtonFloppyB);
        cbAta[ATA_0_MASTER] = (CheckBox) rootView.findViewById(R.id.storageCheckBoxAta0m);
        tvAta[ATA_0_MASTER] = (TextView) rootView.findViewById(R.id.storageTextViewAta0m);
        cbVvfatAta[ATA_0_MASTER] = (CheckBox) rootView.findViewById(R.id.storageCheckBoxAta0mVvfat);
        btBrowseAta[ATA_0_MASTER] = (Button) rootView.findViewById(R.id.storageButtonAta0m);
        spAtaType[ATA_0_MASTER] = (Spinner) rootView.findViewById(R.id.storageSpinnerAta0m);
        cbAta[ATA_0_SLAVE] = (CheckBox) rootView.findViewById(R.id.storageCheckBoxAta0s);
        tvAta[ATA_0_SLAVE] = (TextView) rootView.findViewById(R.id.storageTextViewAta0s);
        cbVvfatAta[ATA_0_SLAVE] = (CheckBox) rootView.findViewById(R.id.storageCheckBoxAta0sVvfat);
        btBrowseAta[ATA_0_SLAVE] = (Button) rootView.findViewById(R.id.storageButtonAta0s);
        spAtaType[ATA_0_SLAVE] = (Spinner) rootView.findViewById(R.id.storageSpinnerAta0s);
        cbAta[ATA_1_MASTER] = (CheckBox) rootView.findViewById(R.id.storageCheckBoxAta1m);
        tvAta[ATA_1_MASTER] = (TextView) rootView.findViewById(R.id.storageTextViewAta1m);
        cbVvfatAta[ATA_1_MASTER] = (CheckBox) rootView.findViewById(R.id.storageCheckBoxAta1mVvfat);
        btBrowseAta[ATA_1_MASTER] = (Button) rootView.findViewById(R.id.storageButtonAta1m);
        spAtaType[ATA_1_MASTER] = (Spinner) rootView.findViewById(R.id.storageSpinnerAta1m);
        cbAta[ATA_1_SLAVE] = (CheckBox) rootView.findViewById(R.id.storageCheckBoxAta1s);
        tvAta[ATA_1_SLAVE] = (TextView) rootView.findViewById(R.id.storageTextViewAta1s);
        cbVvfatAta[ATA_1_SLAVE] = (CheckBox) rootView.findViewById(R.id.storageCheckBoxAta1sVvfat);
        btBrowseAta[ATA_1_SLAVE] = (Button) rootView.findViewById(R.id.storageButtonAta1s);
        spAtaType[ATA_1_SLAVE] = (Spinner) rootView.findViewById(R.id.storageSpinnerAta1s);

        // setup boot selection logic
        Spinner spBoot = (Spinner) rootView.findViewById(R.id.storageSpinnerBoot);
        SpinnerAdapter adapterBoot = new ArrayAdapter<String>(getActivity(), R.layout.spinner_row, bootList);
        spBoot.setAdapter(adapterBoot);
        spBoot.setSelection(bootList.indexOf(Config.boot));
        spBoot.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                Config.boot = bootList.get(p3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> p1) {

            }
        });

        // setup floppy logic
        for (int i = 0; i < floppyNum; i++) {
            cbFloppy[i].setChecked(Config.floppy[i]);
            tvFloppy[i].setText(MainActivity.getFileName(Config.floppyImage[i]));
            tvFloppy[i].setEnabled(Config.floppy[i]);
            btBrowseFloppy[i].setEnabled(Config.floppy[i]);

            final int j = i;

            cbFloppy[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                       @Override
                                                       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                           Config.floppy[j] = cbFloppy[j].isChecked();
                                                           btBrowseFloppy[j].setEnabled(Config.floppy[j]);
                                                           tvFloppy[j].setEnabled(Config.floppy[j]);
                                                       }
                                                   }
            );

            btBrowseFloppy[i].setOnClickListener(this);
        }

        //setup ata logic
        SpinnerAdapter adapterType = new ArrayAdapter<String>(getActivity(), R.layout.spinner_row, typeList);
        for (int i = 0; i < ataNum; i++) {
            cbAta[i].setChecked(Config.ata[i]);
            tvAta[i].setText(MainActivity.getFileName(Config.ataImage[i]));
            tvAta[i].setEnabled(Config.ata[i]);
            cbVvfatAta[i].setChecked(Config.ataMode[i].equals(VFAT));
            cbVvfatAta[i].setEnabled(Config.ata[i]);
            btBrowseAta[i].setEnabled(Config.ata[i]);
            spAtaType[i].setEnabled(Config.ata[i]);
            spAtaType[i].setAdapter(adapterType);
            spAtaType[i].setSelection(typeList.indexOf(Config.ataType[i]));

            final int j = i;

            cbAta[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        Config.ata[j] = cbAta[j].isChecked();
                                                        btBrowseAta[j].setEnabled(Config.ata[j]);
                                                        tvAta[j].setEnabled(Config.ata[j]);
                                                        spAtaType[j].setEnabled(Config.ata[j]);
                                                        if (!Config.ataType[j].equals(CDROM))
                                                            cbVvfatAta[j].setEnabled(Config.ata[j]);

                                                    }
                                                }
            );

            spAtaType[i].setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                    if (!Config.ataType[j].equals(typeList.get(p3))) {
                        Config.ataImage[j] = NONE;
                        tvAta[j].setText(NONE);
                    }
                    Config.ataType[j] = typeList.get(p3);
                    if (!Config.ataType[j].equals(CDROM) && cbAta[j].isChecked()) {
                        cbVvfatAta[j].setEnabled(true);
                    } else {
                        cbVvfatAta[j].setChecked(false);
                        cbVvfatAta[j].setEnabled(false);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> p1) {

                }
            });

            cbVvfatAta[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Config.ataMode[j] = isChecked ? VFAT : "";
                    Config.ataImage[j] = NONE;
                    tvAta[j].setText(NONE);
                }
            });


            btBrowseAta[i].setOnClickListener(this);
        }

    }

    private void dirSelection(final Requestor num) {
        // Create DirectoryChooserDialog and register a callback
        DirectoryChooserDialog directoryChooserDialog =
                new DirectoryChooserDialog(getActivity(),
                        new DirectoryChooserDialog.ChosenDirectoryListener() {
                            @Override
                            public void onChosenDir(String chosenDir) {
                                m_chosenDir = chosenDir;
                                switch (num) {
                                    case ATA0_MASTER:
                                        tvAta[ATA_0_MASTER].setText(chosenDir);
                                        Config.ataImage[ATA_0_MASTER] = chosenDir;
                                        break;
                                    case ATA0_SLAVE:
                                        tvAta[ATA_0_SLAVE].setText(chosenDir);
                                        Config.ataImage[ATA_0_SLAVE] = chosenDir;
                                        break;
                                    case ATA1_MASTER:
                                        tvAta[ATA_1_MASTER].setText(chosenDir);
                                        Config.ataImage[ATA_1_MASTER] = chosenDir;
                                        break;
                                    case ATA1_SLAVE:
                                        tvAta[ATA_1_SLAVE].setText(chosenDir);
                                        Config.ataImage[ATA_1_SLAVE] = chosenDir;
                                        break;
                                }
                            }
                        });
        // Toggle new folder button enabling
        directoryChooserDialog.setNewFolderEnabled(m_newFolderEnabled);
        // Load directory chooser dialog for initial 'm_chosenDir' directory.
        // The registered callback will be called upon final directory selection.
        directoryChooserDialog.chooseDirectory(m_chosenDir);
        m_newFolderEnabled = !m_newFolderEnabled;
    }

    private void fileSelection(final Requestor num, String type) {
        // Set up extension
/*        String extension[] = null;
        switch (type) {
            case DISK:
                extension = new String[]{".img", ".vmdk", ".vhd", ".vdi"};
                break;
            case CDROM:
                extension = new String[]{".iso"};
                break;
            case FLOPPY:
                extension = new String[]{".img", ".ima"};
                break;
        }*/
        requestType = num;

        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_FILE);
    }

    private String getMode(String str) {
        String result = "";
        if (str.endsWith(".vmdk"))
            result = "vmware4";
        else if (str.endsWith(".vhd"))
            result = "vpc";
        else if (str.endsWith(".vdi"))
            result = "vbox";
        return result;
    }

    private void saveLastPath(String filePath) {
        String dirPath;
        if (filePath.contains("/"))
            dirPath = filePath.substring(0, filePath.lastIndexOf('/'));
        else
            dirPath = filePath;
        sPref = getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_PATH, dirPath);
        ed.apply();
    }

    private String getLastPath() {
        sPref = getActivity().getPreferences(MODE_PRIVATE);
        return sPref.getString(SAVED_PATH, null);
    }

}
