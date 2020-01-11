package net.sourceforge.bochs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class Config {
    static final String NONE = "none";
    static final String DISK = "disk";
    static final String CDROM = "cdrom";
    static final String FLOPPY = "floppy";
    static final String VFAT = "vvfat";
    static final int floppyNum = 2;
    static final int ataNum = 4;
    static boolean useSb16 = false;
    static boolean useEs1370 = false;
    static boolean useNe2000 = false;
    static boolean useRtl8029 = false;
    static boolean useE1000 = false;
    static boolean useVoodoo = false;
    static Boolean[] floppy = new Boolean[floppyNum];
    static String[] floppyImage = new String[floppyNum];
    static Boolean[] ata = new Boolean[ataNum];
    static String[] ataImage = new String[ataNum];
    static String[] ataType = new String[ataNum];
    static String[] ataMode = new String[ataNum];
    static String boot = DISK;

    static String romImage = "BIOS-bochs-latest";
    static String vgaRomImage = "VGABIOS-lgpl-latest-cirrus";
    static int megs = 32;
    static String vgaExtension = "cirrus";
    static String voodooModel = "voodoo1";
    static int vgaUpdateFreq = 15;
    static String chipset = "i440fx";
    static String[] slot = {"", "", "", "", ""};
    static String cpuModel = "bx_generic";
    static private String mac = "b0:c4:20:00:00:00";
    static private String ethmod = "slirp";
    static boolean fullscreen = false;
    static boolean useSpeaker = true;
    static String clockSync = NONE;

    static boolean configLoaded = false;
    static final int FLOPPY_A = 0;
    static final int FLOPPY_B = 1;
    static final int ATA_0_MASTER = 0;
    static final int ATA_0_SLAVE = 1;
    static final int ATA_1_MASTER = 2;
    static final int ATA_1_SLAVE = 3;

    static void setDefaulValues() {
        for (int i=0; i < floppyNum; i++) {
            floppy[i] = false;
            floppyImage[i] = NONE;
        }
        for (int i=0; i < ataNum; i++) {
            ata[i] = false;
            ataImage[i] = NONE;
            ataType[i] = DISK;
            ataMode[i] = "";
        }
    }

    static void readConfig(String path) throws FileNotFoundException {
        File file = new File(path);
        Scanner sc = new Scanner(file).useDelimiter("[\n]");
        while (sc.hasNext()) {
            String str = sc.next() + "\n";
            if (str.startsWith("floppya:")) {
                parseFloppyConfig(FLOPPY_A, str);
            }

            if (str.startsWith("floppyb:")) {
                parseFloppyConfig(FLOPPY_B, str);
            }

            if (str.startsWith("ata0-master:")) {
                parseAtaConfig(ATA_0_MASTER, str);
            }

            if (str.startsWith("ata0-slave:")) {
                parseAtaConfig(ATA_0_SLAVE, str);
            }

            if (str.startsWith("ata1-master:")) {
                parseAtaConfig(ATA_1_MASTER, str);
            }

            if (str.startsWith("ata1-slave:")) {
                parseAtaConfig(ATA_1_SLAVE, str);
            }

            if (str.startsWith("boot:")) {
                boot = str.substring(6, str.length() - 1);
            }

            if (str.startsWith("romimage:")) {
                if (str.contains("file=")) {
                    String str2 = str.substring(str.indexOf("file="), str.length() - 1);
                    romImage = str2.contains(",") ?
                            str2.substring(5, str2.indexOf(",")) : str2.substring(5);
                }
            }

            if (str.startsWith("vgaromimage:")) {
                if (str.contains("file=")) {
                    String str2 = str.substring(str.indexOf("file="), str.length() - 1);
                    vgaRomImage = str2.contains(",") ?
                            str2.substring(5, str2.indexOf(",")) : str2.substring(5);
                }
            }

            if (str.startsWith("vga:")) {
                if (str.contains("extension=")) {
                    String str2 = str.substring(str.indexOf("extension="), str.length() - 1);
                    vgaExtension = str2.contains(",") ?
                            str2.substring(10, str2.indexOf(",")) : str2.substring(10);
                }
                if (str.contains("update_freq=")) {
                    String str2 = str.substring(str.indexOf("update_freq="), str.length() - 1);
                    vgaUpdateFreq = str2.contains(",") ?
                            Integer.parseInt(str2.substring(12, str2.indexOf(","))) : Integer.parseInt(str2.substring(12));
                }
            }

            if (str.startsWith("pci:")) {
                if (str.contains("chipset=")) {
                    String str2 = str.substring(str.indexOf("chipset="), str.length() - 1);
                    chipset = str2.contains(",") ?
                            str2.substring(8, str2.indexOf(",")) : str2.substring(8);
                }
                if (str.contains("slot1=")) {
                    String str2 = str.substring(str.indexOf("slot1="), str.length() - 1);
                    slot[0] = str2.contains(",") ?
                            str2.substring(6, str2.indexOf(",")) : str2.substring(6);
                }
                if (str.contains("slot2=")) {
                    String str2 = str.substring(str.indexOf("slot2="), str.length() - 1);
                    slot[1] = str2.contains(",") ?
                            str2.substring(6, str2.indexOf(",")) : str2.substring(6);
                }
                if (str.contains("slot3=")) {
                    String str2 = str.substring(str.indexOf("slot3="), str.length() - 1);
                    slot[2] = str2.contains(",") ?
                            str2.substring(6, str2.indexOf(",")) : str2.substring(6);
                }
                if (str.contains("slot4=")) {
                    String str2 = str.substring(str.indexOf("slot4="), str.length() - 1);
                    slot[3] = str2.contains(",") ?
                            str2.substring(6, str2.indexOf(",")) : str2.substring(6);
                }
                if (str.contains("slot5=")) {
                    String str2 = str.substring(str.indexOf("slot5="), str.length() - 1);
                    slot[4] = str2.contains(",") ?
                            str2.substring(6, str2.indexOf(",")) : str2.substring(6);
                }
            }

            if (str.startsWith("cpu:")) {
                if (str.contains("model=")) {
                    String str2 = str.substring(str.indexOf("model="), str.length() - 1);
                    cpuModel = str2.contains(",") ?
                            str2.substring(6, str2.indexOf(",")) : str2.substring(6);
                }
            }

            if (str.startsWith("ne2k:")) {
                useNe2000 = true;
                if (str.contains("mac=")) {
                    String str2 = str.substring(str.indexOf("mac="), str.length() - 1);
                    mac = str2.contains(",") ?
                            str2.substring(4, str2.indexOf(",")) : str2.substring(4);
                }
                if (str.contains("ethmod=")) {
                    String str2 = str.substring(str.indexOf("ethmod="), str.length() - 1);
                    ethmod = str2.contains(",") ?
                            str2.substring(7, str2.indexOf(",")) : str2.substring(7);
                }
            }

            if (str.startsWith("e1000:")) {
                useE1000 = true;
                if (str.contains("mac=")) {
                    String str2 = str.substring(str.indexOf("mac="), str.length() - 1);
                    mac = str2.contains(",") ?
                            str2.substring(4, str2.indexOf(",")) : str2.substring(4);
                }
                if (str.contains("ethmod=")) {
                    String str2 = str.substring(str.indexOf("ethmod="), str.length() - 1);
                    ethmod = str2.contains(",") ?
                            str2.substring(7, str2.indexOf(",")) : str2.substring(7);
                }
            }

            if (str.startsWith("sb16:")) {
                useSb16 = true;
            }

            if (str.startsWith("es1370:")) {
                useEs1370 = true;
            }

            if (str.startsWith("voodoo:")) {
                useVoodoo = true;
                if (str.contains("model=")) {
                    String str2 = str.substring(str.indexOf("model="), str.length() - 1);
                    voodooModel = str2.contains(",") ?
                            str2.substring(6, str2.indexOf(",")) : str2.substring(6);
                }
            }

            if (str.startsWith("speaker:")) {
                useSpeaker = true;
            }

            if (str.startsWith("megs:")) {
                megs = Integer.parseInt(str.substring(6, str.length() - 1));
            }

            if (str.startsWith("display_library:")) {
                if (str.contains("options=")) {
                    String str2 = str.substring(str.indexOf("options="), str.length() - 1);
                    fullscreen = str2.contains("fullscreen");
                }
            }

            if (str.startsWith("clock:")) {
                if (str.contains("sync=")) {
                    String str2 = str.substring(str.indexOf("sync="), str.length() - 1);
                    clockSync = str2.contains(",") ?
                            str2.substring(5, str2.indexOf(",")) : str2.substring(5);
                }
            }
        }
        sc.close();
    }

    static private void parseFloppyConfig(int n, String str) {
        floppy[n] = true;
        if (str.contains("1_44=")) {
            String str2 = str.substring(str.indexOf("1_44="), str.length() - 1);
            floppyImage[n] = str2.contains(",") ?
                    str2.substring(5, str2.indexOf(",")) : str2.substring(5);
        }
        if (str.contains("image=")) {
            String str2 = str.substring(str.indexOf("image="), str.length() - 1);
            floppyImage[n] = str2.contains(",") ?
                    str2.substring(6, str2.indexOf(",")) : str2.substring(6);
        }
    }

    static private void parseAtaConfig(int n, String str) {
        ata[n] = true;
        if (str.contains("type=")) {
            String str2 = str.substring(str.indexOf("type="), str.length() - 1);
            ataType[n] = str2.contains(",") ?
                    str2.substring(5, str2.indexOf(",")) : str2.substring(5);
        }
        if (str.contains("mode=")) {
            String str2 = str.substring(str.indexOf("mode="), str.length() - 1);
            ataMode[n] = str2.contains(",") ?
                    str2.substring(5, str2.indexOf(",")) : str2.substring(5);
        }
        if (str.contains("path=")) {
            String str2 = str.substring(str.indexOf("path="), str.length() - 1);
            ataImage[n] = str2.contains(",") ?
                    str2.substring(5, str2.indexOf(",")) : str2.substring(5);
            ataImage[n] = ataImage[n].replace("\"", "");
        }
    }

    static void writeConfig(String path) throws IOException {
        File file = new File(path);
        FileWriter fw = new FileWriter(file);
        if (fullscreen)
            fw.write("display_library: sdl, options=fullscreen\n");
        fw.write("romimage: file=" + romImage + "\n");
        fw.write("vgaromimage: file=" + vgaRomImage + "\n");
        fw.write("cpu: model=" + cpuModel + "\n");
        fw.write("vga: extension=" + vgaExtension + ", update_freq=" + vgaUpdateFreq + "\n");
        fw.write("pci: enabled=1, chipset=" + chipset);
        for (int i = 0; i < slot.length; i++) {
            String label[] = {"slot1", "slot2", "slot3", "slot4", "slot5"};
            if (!slot[i].equals("")) {
                fw.write(", " + label[i] + "=" + slot[i]);
            }
        }
        fw.write("\n");
        if (useRtl8029)
            fw.write("ne2k: mac=" + mac + ", ethmod=" + ethmod + ", script=\"\"\n");
        else if (useNe2000)
            fw.write("ne2k: ioaddr=0x300, irq=10, mac=" + mac + ", ethmod=" + ethmod + ", script=\"\"\n");
        if (useE1000)
            fw.write("e1000: mac=" + mac + ", ethmod=" + ethmod + ", script=\"\"\n");

        for (int i = 0; i < floppyNum; i++) {
            String label[] = {"floppya", "floppyb"};
            if (floppy[i]) {
                fw.write(label[i]+": image=" + floppyImage[i] + ", status=inserted\n");
            }
        }

        fw.write("ata0: enabled=1, ioaddr1=0x1f0, ioaddr2=0x3f0, irq=14\n");
        fw.write("ata1: enabled=1, ioaddr1=0x170, ioaddr2=0x370, irq=15\n");
        for (int i = 0; i < ataNum; i++) {
            String label[] = {"ata0-master", "ata0-slave", "ata1-master", "ata1-slave"};
            if (ata[i]) {
                fw.write(label[i] + ": type=" + ataType[i]);
                if (ataType[i].equals("cdrom")) {
                    fw.write(", status=inserted");
                }
                if (!ataMode[i].equals("") && !ataType[i].equals("cdrom")) {
                    fw.write(", mode=" + ataMode[i]);
                }
                fw.write(", path=\"" + ataImage[i] + "\"\n");
            }
        }

        fw.write("boot: " + boot + "\n");
        fw.write("megs: " + megs + "\n");
        fw.write("sound: waveoutdrv=sdl\n");
        if (useSpeaker)
            fw.write("speaker: enabled=1, mode=sound\n");
        if (useSb16)
            fw.write("sb16: wavemode=1, dmatimer=500000\n");
        if (useEs1370)
            fw.write("es1370: enabled=1, wavemode=1\n");
        if (useVoodoo)
            fw.write("voodoo: enabled=1, model=" + voodooModel + "\n");
        fw.write("mouse: enabled=1\n");
        fw.write("clock: sync=" + clockSync + ", time0=local\n");
        fw.write("debug: action=ignore\n");
        fw.write("info: action=ignore\n");
        fw.write("error: action=ignore\n");
        fw.write("panic: action=report\n");
        fw.write("log: bochsout.txt\n");
        fw.close();
    }
}
