package net.sourceforge.bochs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import net.sourceforge.bochs.entity.ChipsetModel;
import net.sourceforge.bochs.entity.CpuModel;
import net.sourceforge.bochs.entity.EthernetCard;
import net.sourceforge.bochs.entity.SoundCard;
import net.sourceforge.bochs.entity.VgaCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class HardwareTabFragment extends Fragment {
    private TextView tvCpuDescription;
    private Spinner spVga;
    private TextView tvVgaDescription;
    private Spinner spSound;
    private TextView tvSoundDescription;
    private Spinner spEthernet;
    private TextView tvEthernetDescription;
    private TextView tvMemory;
    private Spinner[] spSlot = new Spinner[5];
    private ArrayAdapter slotAdapter[] = new ArrayAdapter[5];
    private List<CpuModel> cpuModels = new ArrayList<>();
    private List<ChipsetModel> chipsetModels = new ArrayList<>();
    private List<VgaCard> vgaCards = new ArrayList<>();
    private List<SoundCard> soundCards = new ArrayList<>();
    private List<EthernetCard> ethernetCards = new ArrayList<>();
    private List<String>[] slotList = new List[5];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_hardware, container, false);
        setupView(rootView);

        return rootView;
    }

    private void setupView(View rootView) {
        final int memoryStep = 8;
        final int minValueMemory = 8;
        for (int i = 0; i < 5; i++)
            slotList[i] = new ArrayList<>();
        updateSlotLists();

        if (cpuModels.size() == 0)
            readCpuList();

        Spinner spCpuModel = (Spinner) rootView.findViewById(R.id.hardwareSpinnerCpuModel);
        Spinner spChipsetModel = (Spinner) rootView.findViewById(R.id.hardwareSpinnerChipset);
        tvCpuDescription = (TextView) rootView.findViewById(R.id.hardwareTextViewCpuDesc);
        spVga = (Spinner) rootView.findViewById(R.id.hardwareSpinnerVga);
        tvVgaDescription = (TextView) rootView.findViewById(R.id.hardwareTextViewVgaDesc);
        spSound = (Spinner) rootView.findViewById(R.id.hardwareSpinnerSound);
        tvSoundDescription = (TextView) rootView.findViewById(R.id.hardwareTextViewSoundDesc);
        spEthernet = (Spinner) rootView.findViewById(R.id.hardwareSpinnerEthernet);
        tvEthernetDescription = (TextView) rootView.findViewById(R.id.hardwareTextViewEthernetDesc);
        SeekBar sbMemory = (SeekBar) rootView.findViewById(R.id.hardwareSeekBarMemory);
        tvMemory = (TextView) rootView.findViewById(R.id.hardwareTextViewMemory);
        spSlot[0] = (Spinner) rootView.findViewById(R.id.hardwareSpinnerSlot1);
        spSlot[1] = (Spinner) rootView.findViewById(R.id.hardwareSpinnerSlot2);
        spSlot[2] = (Spinner) rootView.findViewById(R.id.hardwareSpinnerSlot3);
        spSlot[3] = (Spinner) rootView.findViewById(R.id.hardwareSpinnerSlot4);
        spSlot[4] = (Spinner) rootView.findViewById(R.id.hardwareSpinnerSlot5);
        SpinnerAdapter cpuModelAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_row, getCpuModelSelectorList());
        SpinnerAdapter chipsetModelAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_row, getChipsetSelectorList());
        for (int i = 0; i < 5; i++)
            slotAdapter[i] = new ArrayAdapter<String>(getActivity(), R.layout.spinner_row, slotList[i]);
        SpinnerAdapter vgaAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_row, getVgaCardSelectorList());
        SpinnerAdapter soundAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_row, getSoundCardSelectorList());
        SpinnerAdapter ethernetAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_row, getEthernetCardSelectorList());
        spCpuModel.setAdapter(cpuModelAdapter);
        spChipsetModel.setAdapter(chipsetModelAdapter);
        spVga.setAdapter(vgaAdapter);
        spSound.setAdapter(soundAdapter);
        spEthernet.setAdapter(ethernetAdapter);
        for (int i = 0; i < 5; i++)
            spSlot[i].setAdapter(slotAdapter[i]);
        int selectedCpuModel = getCpuModelSelectorList().indexOf(Config.cpuModel);
        spCpuModel.setSelection(selectedCpuModel);
        int selectedChipset = getChipsetSelectorList().indexOf(Config.chipset);
        spChipsetModel.setSelection(selectedChipset);
        tvCpuDescription.setText(cpuModels.get(selectedCpuModel).getDescription());
        //rbI430fx.setChecked(Config.chipset.equals("i430fx"));
        //rbI440fx.setChecked(Config.chipset.equals("i440fx"));
        sbMemory.setProgress((Config.megs / memoryStep) - minValueMemory);
        tvMemory.setText(String.format("%s mb", Config.megs));
        Integer[] selectedSlot = new Integer[5];
        for (int i = 0; i < spSlot.length; i++) {
            selectedSlot[i] = slotList[i].indexOf(Config.slot[i]);
            spSlot[i].setSelection((selectedSlot[i] == -1) ? 0 : selectedSlot[i]);
        }
        checkVga();
        checkSound();
        checkEthernet();

        spCpuModel.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                Config.cpuModel = getCpuModelSelectorList().get(p3);
                int num = getCpuModelSelectorList().indexOf(Config.cpuModel);
                tvCpuDescription.setText(cpuModels.get(num).getDescription());
            }

            @Override
            public void onNothingSelected(AdapterView<?> p1) {
            }
        });

        spChipsetModel.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                Config.chipset = getChipsetSelectorList().get(p3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> p1) {
            }
        });

        spVga.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                tvVgaDescription.setText(vgaCards.get(p3).getDescription());
                switch (p3) {
                    case 0:
                        Config.vgaExtension = "vbe";
                        Config.vgaRomImage = "VGABIOS-lgpl-latest";
                        setFreePciSlot("pcivga");
                        setFreePciSlot("cirrus");
                        break;
                    case 1:
                        Config.vgaExtension = "vbe";
                        Config.vgaRomImage = "VGABIOS-lgpl-latest";
                        setFreePciSlot("cirrus");
                        spSlot[0].setSelection(slotList[0].indexOf("pcivga"));
                        slotAdapter[0].notifyDataSetChanged();
                        break;
                    case 2:
                        Config.vgaExtension = "cirrus";
                        Config.vgaRomImage = "VGABIOS-lgpl-latest-cirrus";
                        setFreePciSlot("pcivga");
                        setFreePciSlot("cirrus");
                        break;
                    case 3:
                        Config.vgaExtension = "cirrus";
                        Config.vgaRomImage = "VGABIOS-lgpl-latest-cirrus";
                        setFreePciSlot("pcivga");
                        spSlot[0].setSelection(slotList[0].indexOf("cirrus"));
                        slotAdapter[0].notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> p1) {
            }
        });

        spSound.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                tvSoundDescription.setText(soundCards.get(p3).getDescription());
                switch (p3) {
                    case 0:
                        Config.useSb16 = false;
                        Config.useEs1370 = false;
                        setFreePciSlot("es1370");
                        break;
                    case 1:
                        Config.useSb16 = true;
                        Config.useEs1370 = false;
                        setFreePciSlot("es1370");
                        break;
                    case 2:
                        Config.useSb16 = false;
                        Config.useEs1370 = true;
                        spSlot[2].setSelection(slotList[2].indexOf("es1370"));
                        slotAdapter[2].notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> p1) {
            }
        });

        spEthernet.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                tvEthernetDescription.setText(ethernetCards.get(p3).getDescription());
                switch (p3) {
                    case 0:
                        Config.useNe2000 = false;
                        Config.useRtl8029 = false;
                        Config.useE1000 = false;
                        setFreePciSlot("ne2k");
                        setFreePciSlot("e1000");
                        break;
                    case 1:
                        Config.useNe2000 = true;
                        Config.useRtl8029 = false;
                        Config.useE1000 = false;
                        setFreePciSlot("ne2k");
                        setFreePciSlot("e1000");
                        break;
                    case 2:
                        Config.useNe2000 = false;
                        Config.useRtl8029 = true;
                        Config.useE1000 = false;
                        setFreePciSlot("e1000");
                        spSlot[1].setSelection(slotList[1].indexOf("ne2k"));
                        slotAdapter[1].notifyDataSetChanged();
                        break;
                    case 3:
                        Config.useNe2000 = false;
                        Config.useRtl8029 = false;
                        Config.useE1000 = true;
                        setFreePciSlot("ne2k");
                        spSlot[1].setSelection(slotList[1].indexOf("e1000"));
                        slotAdapter[1].notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> p1) {
            }
        });

        for (int i=0; i<=4; i++) {
            final int finalI = i;
            spSlot[i].setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                    String str = slotList[finalI].get(p3);
                    Config.slot[finalI] = (p3 == 0) ? "" : str;
                    setOnInConfig(str);
                    updateSlotLists();
                    checkVga();
                    checkSound();
                    checkEthernet();
                }

                @Override
                public void onNothingSelected(AdapterView<?> p1) {
                }
            });
        }

        sbMemory.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar p1, int progress, boolean p3) {
                progress = progress * memoryStep;
                tvMemory.setText(String.format("%s mb", (minValueMemory + progress)));
                Config.megs = minValueMemory + progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar p1) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar p1) {
            }
        });

/*        OnClickListener hwOnClick = new OnClickListener() {

            @Override
            public void onClick(View p1) {
                Config.chipset = (p1.getId() == R.id.hardwareRadioButtonI430fx) ? "i430fx" : "i440fx";
            }
        };*/

        //rbI430fx.setOnClickListener(hwOnClick);
        //rbI440fx.setOnClickListener(hwOnClick);
    }

    private void readCpuList() {
        Scanner sc = new Scanner(getResources().openRawResource(R.raw.data_json)).useDelimiter("[\n]");
        StringBuilder sb = new StringBuilder();
        while (sc.hasNext()) {
            sb.append(sc.next()).append("\n");
        }
        sc.close();

        JSONObject dataJsonObj = null;
        try {
            dataJsonObj = new JSONObject(sb.toString());
            JSONArray cpulist = dataJsonObj.getJSONArray("cpulist");
            for (int i = 0; i < cpulist.length(); i++) {
                JSONObject model = cpulist.getJSONObject(i);
                String value = model.getString("value");
                String description = model.getString("description");
                String required = model.getString("required");
                cpuModels.add(new CpuModel(value, description, required));
            }
            JSONArray chipsetlist = dataJsonObj.getJSONArray("chipsetlist");
            for (int i = 0; i < chipsetlist.length(); i++) {
                JSONObject model = chipsetlist.getJSONObject(i);
                String value = model.getString("value");
                chipsetModels.add(new ChipsetModel(value));
            }
            JSONArray vgalist = dataJsonObj.getJSONArray("vgalist");
            for (int i = 0; i < vgalist.length(); i++) {
                JSONObject model = vgalist.getJSONObject(i);
                String value = model.getString("value");
                String description = model.getString("description");
                vgaCards.add(new VgaCard(value, description));
            }
            JSONArray soundlist = dataJsonObj.getJSONArray("soundlist");
            for (int i = 0; i < soundlist.length(); i++) {
                JSONObject model = soundlist.getJSONObject(i);
                String value = model.getString("value");
                String description = model.getString("description");
                soundCards.add(new SoundCard(value, description));
            }
            JSONArray ethernetlist = dataJsonObj.getJSONArray("ethernetlist");
            for (int i = 0; i < ethernetlist.length(); i++) {
                JSONObject model = ethernetlist.getJSONObject(i);
                String value = model.getString("value");
                String description = model.getString("description");
                ethernetCards.add(new EthernetCard(value, description));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<String> getCpuModelSelectorList() {
        List<String> result = new ArrayList<>();
        for (CpuModel cm : cpuModels) {
            result.add(cm.getValue());
        }
        return result;
    }

    private List<String> getChipsetSelectorList() {
        List<String> result = new ArrayList<>();
        for (ChipsetModel cm : chipsetModels) {
            result.add(cm.getValue());
        }
        return result;
    }

    private List<String> getVgaCardSelectorList() {
        List<String> result = new ArrayList<>();
        for (VgaCard vc : vgaCards) {
            result.add(vc.getValue());
        }
        return result;
    }

    private List<String> getSoundCardSelectorList() {
        List<String> result = new ArrayList<>();
        for (SoundCard sc : soundCards) {
            result.add(sc.getValue());
        }
        return result;
    }

    private List<String> getEthernetCardSelectorList() {
        List<String> result = new ArrayList<>();
        for (EthernetCard ec : ethernetCards) {
            result.add(ec.getValue());
        }
        return result;
    }

    private int getFreePciSlot() {
        for (int i = 0; i < Config.slot.length; i++) {
            if (Config.slot[i].equals("")) {
                return i;
            }
        }
        return -1;
    }

    private boolean checkPciSlotFor(String str) {
        for (int i = 0; i < Config.slot.length; i++) {
            if (Config.slot[i].equals(str)) {
                return true;
            }
        }
        return false;
    }

    private void setFreePciSlot(String str) {
        for (int i = 0; i < Config.slot.length; i++) {
            if (Config.slot[i].equals(str)) {
                Config.slot[i] = "";
                spSlot[i].setSelection(0);
                slotAdapter[i].notifyDataSetChanged();
            }
        }
    }

    private void checkVga() {
        if (Config.vgaExtension.equals("vbe")) {
            spVga.setSelection(checkPciSlotFor("pcivga") ? 1 : 0);
        } else if (Config.vgaExtension.equals("cirrus")) {
            spVga.setSelection(checkPciSlotFor("cirrus") ? 3 : 2);
        }
    }

    private void checkSound() {
        if (Config.useEs1370 && checkPciSlotFor("es1370"))
            spSound.setSelection(2);
        else if (Config.useSb16)
            spSound.setSelection(1);
        else
            spSound.setSelection(0);
    }

    private void checkEthernet() {
        if (Config.useE1000 && checkPciSlotFor("e1000"))
            spEthernet.setSelection(3);
        else if (checkPciSlotFor("ne2k"))
            spEthernet.setSelection(2);
        else if (Config.useNe2000)
            spEthernet.setSelection(1);
        else
            spEthernet.setSelection(0);
    }

    private void setOnInConfig(String str) {
        switch (str) {
            case "voodoo":
                Config.useVoodoo = true;
                break;
            case "es1370":
                Config.useEs1370 = true;
                break;
            case "ne2k":
                Config.useNe2000 = true;
                Config.useRtl8029 = true;
                break;
            case "e1000":
                Config.useE1000 = true;
                break;
            case "pcivga":
                Config.vgaExtension = "vbe";
                Config.vgaRomImage = "VGABIOS-lgpl-latest";
                break;
            case "cirrus":
                Config.vgaExtension = "cirrus";
                Config.vgaRomImage = "VGABIOS-lgpl-latest-cirrus";
                break;
        }
    }

    private List<String> getSlotList(int num) {
        List<String> list = new ArrayList<>();
        final List<String> fullSlotList = Arrays.asList("none", "pcivga", "cirrus", "voodoo",
                "es1370", "ne2k", "e1000");
        list.addAll(fullSlotList);
        for (int i = 0; i < 5; i++) {
            list.remove(Config.slot[i]);
        }
        if (!Config.slot[num].isEmpty())
            list.add(Config.slot[num]);
        return list;
    }

    private void updateSlotLists() {
        for (int i = 0; i < 5; i++) {
            slotList[i].clear();
            slotList[i].addAll(getSlotList(i));
            if (slotAdapter[i] != null) {
                slotAdapter[i].notifyDataSetChanged();
                int selectedSlot = slotList[i].indexOf(Config.slot[i]);
                spSlot[i].setSelection((selectedSlot == -1) ? 0 : selectedSlot);
            }
        }
    }

}
