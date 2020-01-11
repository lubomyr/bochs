package net.sourceforge.bochs.entity;

public class VoodooModel {
    private String value;
    private String description;

    public VoodooModel(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}