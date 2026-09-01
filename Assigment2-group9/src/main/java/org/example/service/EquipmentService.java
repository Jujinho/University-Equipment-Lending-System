/**
 * @author Group 9
 */
package org.example.service;

import org.example.db.EquipmentRepository;
import org.example.model.Equipment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for equipment-related operations.
 */
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    /**
     * Constructor
     *
     * @param equipmentRepository The EquipmentRepository to use for equipment operations
     */
    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    /**
     * Get equipment by ID
     *
     * @param id The equipment ID
     * @return An Optional containing the Equipment if found, or empty if not found
     */
    public Optional<Equipment> getEquipmentById(int id) {
        return equipmentRepository.getEquipmentById(id);
    }

    /**
     * Get all equipment
     *
     * @return A list of all equipment
     */
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.getAllEquipment();
    }

    /**
     * Get equipment by name (partial match)
     *
     * @param name The name to search for
     * @return A list of equipment with names containing the search term
     */
    public List<Equipment> getEquipmentByName(String name) {
        return equipmentRepository.getEquipmentByName(name);
    }

    /**
     * Get equipment by category
     *
     * @param category The category to search for
     * @return A list of equipment in the specified category
     */
    public List<Equipment> getEquipmentByCategory(String category) {
        return equipmentRepository.getEquipmentByCategory(category);
    }

    /**
     * Get equipment by condition
     *
     * @param condition The condition to search for
     * @return A list of equipment in the specified condition
     */
    public List<Equipment> getEquipmentByCondition(String condition) {
        return equipmentRepository.getEquipmentByCondition(condition);
    }

    /**
     * Get available equipment
     *
     * @return A list of available equipment
     */
    public List<Equipment> getAvailableEquipment() {
        return equipmentRepository.getAvailableEquipment();
    }

    /**
     * Create a new equipment
     *
     * @param equipment The equipment to create
     * @return true if the equipment was created successfully, false otherwise
     */
    public boolean createEquipment(Equipment equipment) {
        return equipmentRepository.createEquipment(equipment);
    }

    /**
     * Update an equipment
     *
     * @param equipment The equipment to update
     * @return true if the equipment was updated successfully, false otherwise
     */
    public boolean updateEquipment(Equipment equipment) {
        return equipmentRepository.updateEquipment(equipment);
    }

    /**
     * Delete an equipment
     *
     * @param id The ID of the equipment to delete
     * @return true if the equipment was deleted successfully, false otherwise
     */
    public boolean deleteEquipment(int id) {
        return equipmentRepository.deleteEquipment(id);
    }
    
    /**
     * Add an image to an equipment
     *
     * @param equipmentId The ID of the equipment
     * @param imageFile   The image file to add
     * @return true if the image was added successfully, false otherwise
     */
    public boolean addEquipmentImage(int equipmentId, File imageFile) {
        try {
            byte[] imageData = Files.readAllBytes(imageFile.toPath());
            return equipmentRepository.addEquipmentImage(equipmentId, imageData);
        } catch (IOException e) {
            System.err.println("Error reading image file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Add multiple images to an equipment
     *
     * @param equipmentId The ID of the equipment
     * @param imageFiles  List of image files to add
     * @return number of images successfully added
     */
    public int addMultipleEquipmentImages(int equipmentId, List<File> imageFiles) {
        int successCount = 0;
        
        for (File file : imageFiles) {
            try {
                byte[] imageData = Files.readAllBytes(file.toPath());
                boolean success = equipmentRepository.addEquipmentImage(equipmentId, imageData);
                if (success) {
                    successCount++;
                }
            } catch (IOException e) {
                System.err.println("Error reading image file " + file.getName() + ": " + e.getMessage());
                // Continue with next file even if one fails
            }
        }
        
        return successCount;
    }

    /**
     * Add an image to an equipment
     *
     * @param equipmentId The ID of the equipment
     * @param imageData   The image data as a byte array
     * @return true if the image was added successfully, false otherwise
     */
    public boolean addEquipmentImage(int equipmentId, byte[] imageData) {
        return equipmentRepository.addEquipmentImage(equipmentId, imageData);
    }

    /**
     * Delete an image from an equipment
     *
     * @param imageId The ID of the image to delete
     * @return true if the image was deleted successfully, false otherwise
     */
    public boolean deleteEquipmentImage(int imageId) {
        return equipmentRepository.deleteEquipmentImage(imageId);
    }

    /**
     * Get all images for an equipment
     *
     * @param equipmentId The ID of the equipment
     * @return A list of image data as byte arrays
     */
    public List<byte[]> getEquipmentImages(int equipmentId) {
        return equipmentRepository.getEquipmentImages(equipmentId);
    }

    /**
     * Get all images with their IDs for an equipment
     *
     * @param equipmentId The ID of the equipment
     * @return A map where keys are image IDs and values are image data as byte arrays
     */
    public Map<Integer, byte[]> getEquipmentImagesWithIds(int equipmentId) {
        return equipmentRepository.getEquipmentImagesWithIds(equipmentId);
    }

    /**
     * Search for equipment by various criteria
     *
     * @param name      The name to search for (partial match, can be null)
     * @param category  The category to search for (exact match, can be null)
     * @param condition The condition to search for (exact match, can be null)
     * @return A list of equipment matching the criteria
     */
    public List<Equipment> searchEquipment(String name, String category, String condition) {
        List<Equipment> result;

        // Start with all equipment
        if (name != null && !name.isEmpty()) {
            result = equipmentRepository.getEquipmentByName(name);
        } else {
            result = equipmentRepository.getAllEquipment();
        }

        // Filter by category if provided
        if (category != null && !category.isEmpty()) {
            List<Equipment> filteredResult = new ArrayList<>();
            for (Equipment e : result) {
                if (category.equals(e.getCategory())) {
                    filteredResult.add(e);
                }
            }
            result = filteredResult;
        }

        // Filter by condition if provided
        if (condition != null && !condition.isEmpty()) {
            List<Equipment> filteredResult = new ArrayList<>();
            for (Equipment e : result) {
                if (condition.equals(e.getCondition())) {
                    filteredResult.add(e);
                }
            }
            result = filteredResult;
        }

        return result;
    }

    /**
     * Get all available categories
     *
     * @return A list of all available categories
     */
    public List<String> getAllCategories() {
        return equipmentRepository.getAllEquipment().stream()
                .map(Equipment::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get all available conditions
     *
     * @return A list of all available conditions
     */
    public List<String> getAllConditions() {
        return equipmentRepository.getAllEquipment().stream()
                .map(Equipment::getCondition)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
