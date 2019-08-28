package com.calendardatepicker.month;


public class DateItem {

    private int id;
    private String name;
    private boolean isSelected = false;
    private boolean canSelect;

    public boolean isCanSelect() {
        return canSelect;
    }

    public void setCanSelect(boolean canSelect) {
        this.canSelect = canSelect;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
