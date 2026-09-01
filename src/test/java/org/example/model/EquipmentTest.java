/**
 * @author Group 9
 */
package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Equipment class.
 */
public class EquipmentTest {
    
    private Equipment equipment;
    
    @BeforeEach
    public void setUp() {
        // Create a new equipment instance before each test
        equipment = new Equipment(
                1,
                "3D Printer",
                "A high-quality 3D printer for creating prototypes",
                "Printer",
                "Good",
                LocalDate.of(2022, 1, 15),
                1500.00,
                "MakerBot",
                "Replicator+",
                "MB12345",
                "Engineering Lab",
                true
        );
    }
    
    @Test
    public void testEquipmentConstructor() {
        // Test that the equipment is created with the correct values
        assertEquals(1, equipment.getId());
        assertEquals("3D Printer", equipment.getName());
        assertEquals("A high-quality 3D printer for creating prototypes", equipment.getDescription());
        assertEquals("Printer", equipment.getCategory());
        assertEquals("Good", equipment.getCondition());
        assertEquals(LocalDate.of(2022, 1, 15), equipment.getPurchaseDate());
        assertEquals(1500.00, equipment.getPurchasePrice());
        assertEquals("MakerBot", equipment.getManufacturer());
        assertEquals("Replicator+", equipment.getModel());
        assertEquals("MB12345", equipment.getSerialNumber());
        assertEquals("Engineering Lab", equipment.getLocation());
        assertTrue(equipment.isAvailable());
    }
    
    @Test
    public void testEquipmentSetters() {
        // Test the setters
        equipment.setId(2);
        equipment.setName("Laser Cutter");
        equipment.setDescription("A precision laser cutter for detailed work");
        equipment.setCategory("Cutter");
        equipment.setCondition("Excellent");
        equipment.setPurchaseDate(LocalDate.of(2023, 3, 10));
        equipment.setPurchasePrice(2500.00);
        equipment.setManufacturer("Glowforge");
        equipment.setModel("Pro");
        equipment.setSerialNumber("GF67890");
        equipment.setLocation("Design Studio");
        equipment.setAvailable(false);
        
        // Verify the values were set correctly
        assertEquals(2, equipment.getId());
        assertEquals("Laser Cutter", equipment.getName());
        assertEquals("A precision laser cutter for detailed work", equipment.getDescription());
        assertEquals("Cutter", equipment.getCategory());
        assertEquals("Excellent", equipment.getCondition());
        assertEquals(LocalDate.of(2023, 3, 10), equipment.getPurchaseDate());
        assertEquals(2500.00, equipment.getPurchasePrice());
        assertEquals("Glowforge", equipment.getManufacturer());
        assertEquals("Pro", equipment.getModel());
        assertEquals("GF67890", equipment.getSerialNumber());
        assertEquals("Design Studio", equipment.getLocation());
        assertFalse(equipment.isAvailable());
    }
    
    @Test
    public void testAddImage() {
        // Test adding an image
        byte[] image1 = {1, 2, 3, 4, 5};
        byte[] image2 = {6, 7, 8, 9, 10};
        
        // Initially, there should be no images
        assertEquals(0, equipment.getImages().size());
        
        // Add the first image
        equipment.addImage(image1);
        assertEquals(1, equipment.getImages().size());
        assertArrayEquals(image1, equipment.getImages().get(0));
        
        // Add the second image
        equipment.addImage(image2);
        assertEquals(2, equipment.getImages().size());
        assertArrayEquals(image2, equipment.getImages().get(1));
    }
    
    @Test
    public void testRemoveImage() {
        // Test removing an image
        byte[] image1 = {1, 2, 3, 4, 5};
        byte[] image2 = {6, 7, 8, 9, 10};
        
        // Add two images
        equipment.addImage(image1);
        equipment.addImage(image2);
        assertEquals(2, equipment.getImages().size());
        
        // Remove the first image
        assertTrue(equipment.removeImage(0));
        assertEquals(1, equipment.getImages().size());
        assertArrayEquals(image2, equipment.getImages().get(0));
        
        // Remove the second image
        assertTrue(equipment.removeImage(0));
        assertEquals(0, equipment.getImages().size());
        
        // Try to remove a non-existent image
        assertFalse(equipment.removeImage(0));
        assertFalse(equipment.removeImage(-1));
    }
    
    @Test
    public void testEqualsAndHashCode() {
        // Test equals and hashCode methods
        Equipment sameEquipment = new Equipment(
                1,
                "Different Name", // Different name, but same ID
                "Different Description",
                "Different Category",
                "Different Condition",
                LocalDate.of(2023, 5, 20),
                3000.00,
                "Different Manufacturer",
                "Different Model",
                "Different Serial",
                "Different Location",
                false
        );
        
        Equipment differentEquipment = new Equipment(
                2, // Different ID
                "3D Printer",
                "A high-quality 3D printer for creating prototypes",
                "Printer",
                "Good",
                LocalDate.of(2022, 1, 15),
                1500.00,
                "MakerBot",
                "Replicator+",
                "MB12345",
                "Engineering Lab",
                true
        );
        
        // Test equals
        assertEquals(equipment, equipment); // Same object
        assertEquals(equipment, sameEquipment); // Same ID
        assertNotEquals(equipment, differentEquipment); // Different ID
        assertNotEquals(equipment, null); // Null
        assertNotEquals(equipment, "Not an Equipment"); // Different class
        
        // Test hashCode
        assertEquals(equipment.hashCode(), sameEquipment.hashCode()); // Same ID
        assertNotEquals(equipment.hashCode(), differentEquipment.hashCode()); // Different ID
    }
    
    @Test
    public void testToString() {
        // Test toString method
        String expectedString = "Equipment{" +
                "id=1" +
                ", name='3D Printer'" +
                ", description='A high-quality 3D printer for creating prototypes'" +
                ", category='Printer'" +
                ", condition='Good'" +
                ", purchaseDate=2022-01-15" +
                ", purchasePrice=1500.0" +
                ", manufacturer='MakerBot'" +
                ", model='Replicator+'" +
                ", serialNumber='MB12345'" +
                ", location='Engineering Lab'" +
                ", available=true" +
                ", images=0" +
                '}';
        
        assertEquals(expectedString, equipment.toString());
    }
}