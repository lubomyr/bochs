package net.sourceforge.bochs.entity;

public class SoundCard {
    private String value;
    private String name;

    public SoundCard(String value, String name) {
        this.value = value;
        this.name = name;
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
}
