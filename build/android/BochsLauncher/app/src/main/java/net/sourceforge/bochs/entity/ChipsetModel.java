package net.sourceforge.bochs.entity;

public class ChipsetModel {
    private String value;

    public ChipsetModel(String value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
