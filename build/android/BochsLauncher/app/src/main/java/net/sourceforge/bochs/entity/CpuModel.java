package net.sourceforge.bochs.entity;

public class CpuModel {
    private String value;
    private String description;
    private String reqFeat;

    public CpuModel(String value, String description, String reqFeat) {
        this.value = value;
        this.description = description;
        this.reqFeat = reqFeat;
    }

    public void setReqFeat(String reqFeat) {
        this.reqFeat = reqFeat;
    }

    public String getReqFeat() {
        return reqFeat;
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
