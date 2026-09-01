/**
 * @author Group 9
 */
package org.example.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents equipment in the university lending system.
 * Equipment can be borrowed by students, academic staff, and professional staff.
 * Administrators can upload images for equipment, which are stored in the database.
 */
public class Equipment {
    private int id;
    private String name;
    private String description;
    private String category;
    private String condition;
    private LocalDate purchaseDate;
    private double purchasePrice;
    private String manufacturer;
    private String model;
    private String serialNumber;
    private String location;
    private boolean available;
    private List<byte[]> images;

    /**
     * Default constructor
     */
    public Equipment() {
        this.images = new ArrayList<>();
    }

    /**
     * Parameterized constructor
     *
     * @param id            Unique identifier for the equipment
     * @param name          Name of the equipment
     * @param description   Description of the equipment
     * @param category      Category of the equipment
     * @param condition     Condition of the equipment (e.g., "Good", "Fair", "Poor")
     * @param purchaseDate  Date when the equipment was purchased
     * @param purchasePrice Price at which the equipment was purchased
     * @param manufacturer  Manufacturer of the equipment
     * @param model         Model of the equipment
     * @param serialNumber  Serial number of the equipment
     * @param location      Location where the equipment is stored
     * @param available     Whether the equipment is available for borrowing
     */
    public Equipment(int id, String name, String description, String category, String condition,
                    LocalDate purchaseDate, double purchasePrice, String manufacturer, String model,
                    String serialNumber, String location, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.condition = condition;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
        this.location = location;
        this.available = available;
        this.images = new ArrayList<>();
    }

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<byte[]> getImages() {
        return images;
    }

    public void setImages(List<byte[]> images) {
        this.images = images;
    }

    /**
     * Add an image to the equipment
     *
     * @param image The image data as a byte array
     */
    public void addImage(byte[] image) {
        if (image != null) {
            images.add(image);
        }
    }

    /**
     * Remove an image from the equipment
     *
     * @param index The index of the image to remove
     * @return true if the image was removed successfully, false otherwise
     */
    public boolean removeImage(int index) {
        if (index >= 0 && index < images.size()) {
            images.remove(index);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", condition='" + condition + '\'' +
                ", purchaseDate=" + purchaseDate +
                ", purchasePrice=" + purchasePrice +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", location='" + location + '\'' +
                ", available=" + available +
                ", images=" + images.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipment equipment = (Equipment) o;
        return id == equipment.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}