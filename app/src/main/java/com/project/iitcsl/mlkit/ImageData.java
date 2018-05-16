package com.project.iitcsl.mlkit;

public class ImageData {
    String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public ImageData(String label, String details) {
        this.label = label;
        this.details = details;
    }

    String details;

}
