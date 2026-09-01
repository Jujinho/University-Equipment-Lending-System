/**
 * @author Group 9
 */
package org.example.db;

import org.example.model.Equipment;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository class for Equipment entities.
 * Handles database operations for equipment, including image management.
 */
public class EquipmentRepository {
    
    /**
     * Get equipment by ID
     *
     * @param id The equipment ID
     * @return An Optional containing the Equipment if found, or empty if not found
     */
    public Optional<Equipment> getEquipmentById(int id) {
        String sql = "SELECT * FROM equipment WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Equipment equipment = extractEquipmentFromResultSet(rs);
                    loadEquipmentImages(equipment);
                    return Optional.of(equipment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting equipment by ID: " + e.getMessage());
        }
        
        return Optional.empty();
    }
    
    /**
     * Get all equipment
     *
     * @return A list of all equipment
     */
    public List<Equipment> getAllEquipment() {
        List<Equipment> equipmentList = new ArrayList<>();
        String sql = "SELECT * FROM equipment";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Equipment equipment = extractEquipmentFromResultSet(rs);
                loadEquipmentImages(equipment);
                equipmentList.add(equipment);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all equipment: " + e.getMessage());
        }
        
        return equipmentList;
    }
    
    /**
     * Get equipment by name (partial match)
     *
     * @param name The name to search for
     * @return A list of equipment with names containing the search term
     */
    public List<Equipment> getEquipmentByName(String name) {
        List<Equipment> equipmentList = new ArrayList<>();
        String sql = "SELECT * FROM equipment WHERE name ILIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Equipment equipment = extractEquipmentFromResultSet(rs);
                    loadEquipmentImages(equipment);
                    equipmentList.add(equipment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting equipment by name: " + e.getMessage());
        }
        
        return equipmentList;
    }
    
    /**
     * Get equipment by category
     *
     * @param category The category to search for
     * @return A list of equipment in the specified category
     */
    public List<Equipment> getEquipmentByCategory(String category) {
        List<Equipment> equipmentList = new ArrayList<>();
        String sql = "SELECT * FROM equipment WHERE category = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Equipment equipment = extractEquipmentFromResultSet(rs);
                    loadEquipmentImages(equipment);
                    equipmentList.add(equipment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting equipment by category: " + e.getMessage());
        }
        
        return equipmentList;
    }
    
    /**
     * Get equipment by condition
     *
     * @param condition The condition to search for
     * @return A list of equipment in the specified condition
     */
    public List<Equipment> getEquipmentByCondition(String condition) {
        List<Equipment> equipmentList = new ArrayList<>();
        String sql = "SELECT * FROM equipment WHERE condition = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, condition);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Equipment equipment = extractEquipmentFromResultSet(rs);
                    loadEquipmentImages(equipment);
                    equipmentList.add(equipment);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting equipment by condition: " + e.getMessage());
        }
        
        return equipmentList;
    }
    
    /**
     * Get available equipment
     *
     * @return A list of available equipment
     */
    public List<Equipment> getAvailableEquipment() {
        List<Equipment> equipmentList = new ArrayList<>();
        String sql = "SELECT * FROM equipment WHERE available = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Equipment equipment = extractEquipmentFromResultSet(rs);
                loadEquipmentImages(equipment);
                equipmentList.add(equipment);
            }
        } catch (SQLException e) {
            System.err.println("Error getting available equipment: " + e.getMessage());
        }
        
        return equipmentList;
    }
    
    /**
     * Create a new equipment
     *
     * @param equipment The equipment to create
     * @return true if the equipment was created successfully, false otherwise
     */
    public boolean createEquipment(Equipment equipment) {
        String sql = "INSERT INTO equipment (name, description, category, condition, purchase_date, " +
                "purchase_price, manufacturer, model, serial_number, location, available) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, equipment.getName());
                stmt.setString(2, equipment.getDescription());
                stmt.setString(3, equipment.getCategory());
                stmt.setString(4, equipment.getCondition());
                
                if (equipment.getPurchaseDate() != null) {
                    stmt.setDate(5, Date.valueOf(equipment.getPurchaseDate()));
                } else {
                    stmt.setNull(5, Types.DATE);
                }
                
                stmt.setDouble(6, equipment.getPurchasePrice());
                stmt.setString(7, equipment.getManufacturer());
                stmt.setString(8, equipment.getModel());
                stmt.setString(9, equipment.getSerialNumber());
                stmt.setString(10, equipment.getLocation());
                stmt.setBoolean(11, equipment.isAvailable());
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating equipment failed, no rows affected.");
                }
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int equipmentId = generatedKeys.getInt(1);
                        equipment.setId(equipmentId);
                        
                        // Save images if any
                        if (!equipment.getImages().isEmpty()) {
                            saveEquipmentImages(conn, equipment);
                        }
                        
                        conn.commit();
                        return true;
                    } else {
                        throw new SQLException("Creating equipment failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating equipment: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Update an equipment
     *
     * @param equipment The equipment to update
     * @return true if the equipment was updated successfully, false otherwise
     */
    public boolean updateEquipment(Equipment equipment) {
        String sql = "UPDATE equipment SET name = ?, description = ?, category = ?, condition = ?, " +
                "purchase_date = ?, purchase_price = ?, manufacturer = ?, model = ?, " +
                "serial_number = ?, location = ?, available = ? WHERE id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, equipment.getName());
                stmt.setString(2, equipment.getDescription());
                stmt.setString(3, equipment.getCategory());
                stmt.setString(4, equipment.getCondition());
                
                if (equipment.getPurchaseDate() != null) {
                    stmt.setDate(5, Date.valueOf(equipment.getPurchaseDate()));
                } else {
                    stmt.setNull(5, Types.DATE);
                }
                
                stmt.setDouble(6, equipment.getPurchasePrice());
                stmt.setString(7, equipment.getManufacturer());
                stmt.setString(8, equipment.getModel());
                stmt.setString(9, equipment.getSerialNumber());
                stmt.setString(10, equipment.getLocation());
                stmt.setBoolean(11, equipment.isAvailable());
                stmt.setInt(12, equipment.getId());
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Updating equipment failed, no rows affected.");
                }
                
                // Update images if needed
                if (!equipment.getImages().isEmpty()) {
                    // Delete existing images
                    deleteEquipmentImages(conn, equipment.getId());
                    
                    // Save new images
                    saveEquipmentImages(conn, equipment);
                }
                
                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating equipment: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error rolling back transaction: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error resetting auto-commit: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Delete an equipment
     *
     * @param id The ID of the equipment to delete
     * @return true if the equipment was deleted successfully, false otherwise
     */
    public boolean deleteEquipment(int id) {
        String sql = "DELETE FROM equipment WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting equipment: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Add an image to an equipment
     *
     * @param equipmentId The ID of the equipment
     * @param imageData   The image data as a byte array
     * @return true if the image was added successfully, false otherwise
     */
    public boolean addEquipmentImage(int equipmentId, byte[] imageData) {
        String sql = "INSERT INTO equipment_images (equipment_id, image) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, equipmentId);
            stmt.setBytes(2, imageData);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding equipment image: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete an image from an equipment
     *
     * @param imageId The ID of the image to delete
     * @return true if the image was deleted successfully, false otherwise
     */
    public boolean deleteEquipmentImage(int imageId) {
        String sql = "DELETE FROM equipment_images WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, imageId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting equipment image: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all images for an equipment
     *
     * @param equipmentId The ID of the equipment
     * @return A list of image data as byte arrays
     */
    public List<byte[]> getEquipmentImages(int equipmentId) {
        List<byte[]> images = new ArrayList<>();
        String sql = "SELECT image FROM equipment_images WHERE equipment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, equipmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    byte[] imageData = rs.getBytes("image");
                    images.add(imageData);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting equipment images: " + e.getMessage());
        }
        
        return images;
    }
    
    /**
     * Get all images with their IDs for an equipment
     *
     * @param equipmentId The ID of the equipment
     * @return A map where keys are image IDs and values are image data as byte arrays
     */
    public Map<Integer, byte[]> getEquipmentImagesWithIds(int equipmentId) {
        Map<Integer, byte[]> images = new HashMap<>();
        String sql = "SELECT id, image FROM equipment_images WHERE equipment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, equipmentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int imageId = rs.getInt("id");
                    byte[] imageData = rs.getBytes("image");
                    images.put(imageId, imageData);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting equipment images with IDs: " + e.getMessage());
        }
        
        return images;
    }
    
    /**
     * Extract an equipment from a ResultSet
     *
     * @param rs The ResultSet containing equipment data
     * @return The extracted Equipment
     * @throws SQLException If a database access error occurs
     */
    private Equipment extractEquipmentFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String category = rs.getString("category");
        String condition = rs.getString("condition");
        Date purchaseDate = rs.getDate("purchase_date");
        double purchasePrice = rs.getDouble("purchase_price");
        String manufacturer = rs.getString("manufacturer");
        String model = rs.getString("model");
        String serialNumber = rs.getString("serial_number");
        String location = rs.getString("location");
        boolean available = rs.getBoolean("available");
        
        Equipment equipment = new Equipment(
                id, name, description, category, condition,
                purchaseDate != null ? purchaseDate.toLocalDate() : null,
                purchasePrice, manufacturer, model, serialNumber, location, available
        );
        
        return equipment;
    }
    
    /**
     * Load images for an equipment
     *
     * @param equipment The equipment to load images for
     */
    private void loadEquipmentImages(Equipment equipment) {
        List<byte[]> images = getEquipmentImages(equipment.getId());
        for (byte[] image : images) {
            equipment.addImage(image);
        }
    }
    
    /**
     * Save images for an equipment
     *
     * @param conn      The database connection
     * @param equipment The equipment to save images for
     * @throws SQLException If a database access error occurs
     */
    private void saveEquipmentImages(Connection conn, Equipment equipment) throws SQLException {
        String sql = "INSERT INTO equipment_images (equipment_id, image) VALUES (?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (byte[] image : equipment.getImages()) {
                stmt.setInt(1, equipment.getId());
                stmt.setBytes(2, image);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }
    
    /**
     * Delete all images for an equipment
     *
     * @param conn        The database connection
     * @param equipmentId The ID of the equipment
     * @throws SQLException If a database access error occurs
     */
    private void deleteEquipmentImages(Connection conn, int equipmentId) throws SQLException {
        String sql = "DELETE FROM equipment_images WHERE equipment_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, equipmentId);
            stmt.executeUpdate();
        }
    }
}