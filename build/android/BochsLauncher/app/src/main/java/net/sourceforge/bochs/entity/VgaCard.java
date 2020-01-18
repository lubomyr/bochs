package net.sourceforge.bochs.entity;

public class VgaCard {
    private String value;
    private String name;
    private String vgaExtension;
    private String vgaRomImage;
    private String chipset;

    public VgaCard(String value, String name, String vgaExtension, String vgaRomImage, String chipset) {
        this.value = value;
        this.name = name;
        this.vgaExtension = vgaExtension;
        this.vgaRomImage = vgaRomImage;
        this.chipset = chipset;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getVgaExtension() {
        return vgaExtension;
    }

    public void setVgaExtension(String vgaExtension) {
        this.vgaExtension = vgaExtension;
    }

    public String getVgaRomImage() {
        return vgaRomImage;
    }

    public void setVgaRomImage(String vgaRomImage) {
        this.vgaRomImage = vgaRomImage;
    }

    public String getChipset() {
        return chipset;
    }

    public void setChipset(String chipset) {
        this.chipset = chipset;
    }
}
