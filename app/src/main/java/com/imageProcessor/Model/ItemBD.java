package com.imageProcessor.Model;

public class ItemBD {
    private int id;
    private String operation;
    private byte[] image;

    public ItemBD(String operation, byte[] image, int id) {
        this.operation = operation;
        this.image = image;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
