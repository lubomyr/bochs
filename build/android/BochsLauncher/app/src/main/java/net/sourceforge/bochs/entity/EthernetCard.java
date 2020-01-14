package net.sourceforge.bochs.entity;

public class EthernetCard {
    private String value;
    private String name;

    public EthernetCard(String value, String name) {
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
