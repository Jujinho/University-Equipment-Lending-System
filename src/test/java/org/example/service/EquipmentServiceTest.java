/**
 * @author Group 9
 */
package org.example.service;

import org.example.db.EquipmentRepository;
import org.example.model.Equipment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the EquipmentService class.
 */
public class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    private EquipmentService equipmentService;

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        equipmentService = new EquipmentService(equipmentRepository);
    }

    @Test
    public void testGetEquipmentById() {
        // Create test data
        Equipment equipment = new Equipment(
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

        // Mock the repository methods
        when(equipmentRepository.getEquipmentById(1)).thenReturn(Optional.of(equipment));

        // Call the service method
        Optional<Equipment> result = equipmentService.getEquipmentById(1);

        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(equipment, result.get());

        // Verify that the repository methods were called
        verify(equipmentRepository).getEquipmentById(1);
    }

    @Test
    public void testGetAllEquipment() {
        // Create test data
        List<Equipment> equipmentList = new ArrayList<>();
        equipmentList.add(new Equipment(1, "3D Printer", "Description", "Printer", "Good", LocalDate.now(), 1500.00, "MakerBot", "Replicator+", "MB12345", "Lab", true));
        equipmentList.add(new Equipment(2, "Laser Cutter", "Description", "Cutter", "Excellent", LocalDate.now(), 2500.00, "Glowforge", "Pro", "GF67890", "Lab", true));

        // Mock the repository methods
        when(equipmentRepository.getAllEquipment()).thenReturn(equipmentList);

        // Call the service method
        List<Equipment> result = equipmentService.getAllEquipment();

        // Verify the result
        assertEquals(2, result.size());
        assertEquals(equipmentList, result);

        // Verify that the repository methods were called
        verify(equipmentRepository).getAllEquipment();
    }

    @Test
    public void testGetEquipmentByName() {
        // Create test data
        List<Equipment> equipmentList = new ArrayList<>();
        equipmentList.add(new Equipment(1, "3D Printer", "Description", "Printer", "Good", LocalDate.now(), 1500.00, "MakerBot", "Replicator+", "MB12345", "Lab", true));

        // Mock the repository methods
        when(equipmentRepository.getEquipmentByName("3D")).thenReturn(equipmentList);

        // Call the service method
        List<Equipment> result = equipmentService.getEquipmentByName("3D");

        // Verify the result
        assertEquals(1, result.size());
        assertEquals(equipmentList, result);

        // Verify that the repository methods were called
        verify(equipmentRepository).getEquipmentByName("3D");
    }

    @Test
    public void testCreateEquipment() {
        // Create test data
        Equipment equipment = new Equipment(
                0,
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

        // Mock the repository methods
        when(equipmentRepository.createEquipment(equipment)).thenReturn(true);

        // Call the service method
        boolean result = equipmentService.createEquipment(equipment);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(equipmentRepository).createEquipment(equipment);
    }

    @Test
    public void testUpdateEquipment() {
        // Create test data
        Equipment equipment = new Equipment(
                1,
                "3D Printer",
                "Updated description",
                "Printer",
                "Excellent",
                LocalDate.of(2022, 1, 15),
                1500.00,
                "MakerBot",
                "Replicator+",
                "MB12345",
                "Engineering Lab",
                true
        );

        // Mock the repository methods
        when(equipmentRepository.updateEquipment(equipment)).thenReturn(true);

        // Call the service method
        boolean result = equipmentService.updateEquipment(equipment);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(equipmentRepository).updateEquipment(equipment);
    }

    @Test
    public void testDeleteEquipment() {
        // Mock the repository methods
        when(equipmentRepository.deleteEquipment(1)).thenReturn(true);

        // Call the service method
        boolean result = equipmentService.deleteEquipment(1);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(equipmentRepository).deleteEquipment(1);
    }

    @Test
    public void testAddEquipmentImage_ByteArray() throws IOException {
        // Create test data
        byte[] imageData = "test image data".getBytes();

        // Mock the repository methods
        when(equipmentRepository.addEquipmentImage(1, imageData)).thenReturn(true);

        // Call the service method
        boolean result = equipmentService.addEquipmentImage(1, imageData);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(equipmentRepository).addEquipmentImage(1, imageData);
    }

    @Test
    public void testAddEquipmentImage_File() throws IOException {
        // Create a temporary image file
        Path imagePath = tempDir.resolve("test_image.jpg");
        byte[] imageData = "test image data".getBytes();
        Files.write(imagePath, imageData);
        File imageFile = imagePath.toFile();

        // Mock the repository methods
        when(equipmentRepository.addEquipmentImage(1, imageData)).thenReturn(true);

        // Call the service method
        boolean result = equipmentService.addEquipmentImage(1, imageFile);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(equipmentRepository).addEquipmentImage(1, imageData);
    }

    @Test
    public void testAddMultipleEquipmentImages() throws IOException {
        // Create temporary image files
        Path imagePath1 = tempDir.resolve("test_image1.jpg");
        Path imagePath2 = tempDir.resolve("test_image2.jpg");
        byte[] imageData1 = "test image data 1".getBytes();
        byte[] imageData2 = "test image data 2".getBytes();
        Files.write(imagePath1, imageData1);
        Files.write(imagePath2, imageData2);
        
        List<File> imageFiles = new ArrayList<>();
        imageFiles.add(imagePath1.toFile());
        imageFiles.add(imagePath2.toFile());

        // Mock the repository methods
        when(equipmentRepository.addEquipmentImage(1, imageData1)).thenReturn(true);
        when(equipmentRepository.addEquipmentImage(1, imageData2)).thenReturn(true);

        // Call the service method
        int result = equipmentService.addMultipleEquipmentImages(1, imageFiles);

        // Verify the result
        assertEquals(2, result);

        // Verify that the repository methods were called
        verify(equipmentRepository).addEquipmentImage(1, imageData1);
        verify(equipmentRepository).addEquipmentImage(1, imageData2);
    }

    @Test
    public void testDeleteEquipmentImage() {
        // Mock the repository methods
        when(equipmentRepository.deleteEquipmentImage(1)).thenReturn(true);

        // Call the service method
        boolean result = equipmentService.deleteEquipmentImage(1);

        // Verify the result
        assertTrue(result);

        // Verify that the repository methods were called
        verify(equipmentRepository).deleteEquipmentImage(1);
    }

    @Test
    public void testGetEquipmentImages() {
        // Create test data
        List<byte[]> images = new ArrayList<>();
        images.add("image1".getBytes());
        images.add("image2".getBytes());

        // Mock the repository methods
        when(equipmentRepository.getEquipmentImages(1)).thenReturn(images);

        // Call the service method
        List<byte[]> result = equipmentService.getEquipmentImages(1);

        // Verify the result
        assertEquals(2, result.size());
        assertArrayEquals(images.get(0), result.get(0));
        assertArrayEquals(images.get(1), result.get(1));

        // Verify that the repository methods were called
        verify(equipmentRepository).getEquipmentImages(1);
    }

    @Test
    public void testGetEquipmentImagesWithIds() {
        // Create test data
        Map<Integer, byte[]> images = new HashMap<>();
        images.put(1, "image1".getBytes());
        images.put(2, "image2".getBytes());

        // Mock the repository methods
        when(equipmentRepository.getEquipmentImagesWithIds(1)).thenReturn(images);

        // Call the service method
        Map<Integer, byte[]> result = equipmentService.getEquipmentImagesWithIds(1);

        // Verify the result
        assertEquals(2, result.size());
        assertArrayEquals(images.get(1), result.get(1));
        assertArrayEquals(images.get(2), result.get(2));

        // Verify that the repository methods were called
        verify(equipmentRepository).getEquipmentImagesWithIds(1);
    }

    @Test
    public void testSearchEquipment_ByName() {
        // Create test data
        List<Equipment> equipmentList = new ArrayList<>();
        equipmentList.add(new Equipment(1, "3D Printer", "Description", "Printer", "Good", LocalDate.now(), 1500.00, "MakerBot", "Replicator+", "MB12345", "Lab", true));

        // Mock the repository methods
        when(equipmentRepository.getEquipmentByName("3D")).thenReturn(equipmentList);

        // Call the service method
        List<Equipment> result = equipmentService.searchEquipment("3D", null, null);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals(equipmentList, result);

        // Verify that the repository methods were called
        verify(equipmentRepository).getEquipmentByName("3D");
        verify(equipmentRepository, never()).getAllEquipment();
    }

    @Test
    public void testSearchEquipment_ByCategory() {
        // Create test data
        List<Equipment> allEquipment = new ArrayList<>();
        Equipment printer = new Equipment(1, "3D Printer", "Description", "Printer", "Good", LocalDate.now(), 1500.00, "MakerBot", "Replicator+", "MB12345", "Lab", true);
        Equipment cutter = new Equipment(2, "Laser Cutter", "Description", "Cutter", "Excellent", LocalDate.now(), 2500.00, "Glowforge", "Pro", "GF67890", "Lab", true);
        allEquipment.add(printer);
        allEquipment.add(cutter);

        // Mock the repository methods
        when(equipmentRepository.getAllEquipment()).thenReturn(allEquipment);

        // Call the service method
        List<Equipment> result = equipmentService.searchEquipment(null, "Printer", null);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals(printer, result.get(0));

        // Verify that the repository methods were called
        verify(equipmentRepository, never()).getEquipmentByName(anyString());
        verify(equipmentRepository).getAllEquipment();
    }

    @Test
    public void testGetAllCategories() {
        // Create test data
        List<Equipment> allEquipment = new ArrayList<>();
        allEquipment.add(new Equipment(1, "3D Printer", "Description", "Printer", "Good", LocalDate.now(), 1500.00, "MakerBot", "Replicator+", "MB12345", "Lab", true));
        allEquipment.add(new Equipment(2, "Laser Cutter", "Description", "Cutter", "Excellent", LocalDate.now(), 2500.00, "Glowforge", "Pro", "GF67890", "Lab", true));
        allEquipment.add(new Equipment(3, "Another Printer", "Description", "Printer", "Fair", LocalDate.now(), 1000.00, "HP", "LaserJet", "HP12345", "Lab", true));

        // Mock the repository methods
        when(equipmentRepository.getAllEquipment()).thenReturn(allEquipment);

        // Call the service method
        List<String> result = equipmentService.getAllCategories();

        // Verify the result
        assertEquals(2, result.size());
        assertTrue(result.contains("Printer"));
        assertTrue(result.contains("Cutter"));

        // Verify that the repository methods were called
        verify(equipmentRepository).getAllEquipment();
    }

    @Test
    public void testGetAllConditions() {
        // Create test data
        List<Equipment> allEquipment = new ArrayList<>();
        allEquipment.add(new Equipment(1, "3D Printer", "Description", "Printer", "Good", LocalDate.now(), 1500.00, "MakerBot", "Replicator+", "MB12345", "Lab", true));
        allEquipment.add(new Equipment(2, "Laser Cutter", "Description", "Cutter", "Excellent", LocalDate.now(), 2500.00, "Glowforge", "Pro", "GF67890", "Lab", true));
        allEquipment.add(new Equipment(3, "Another Printer", "Description", "Printer", "Fair", LocalDate.now(), 1000.00, "HP", "LaserJet", "HP12345", "Lab", true));

        // Mock the repository methods
        when(equipmentRepository.getAllEquipment()).thenReturn(allEquipment);

        // Call the service method
        List<String> result = equipmentService.getAllConditions();

        // Verify the result
        assertEquals(3, result.size());
        assertTrue(result.contains("Good"));
        assertTrue(result.contains("Excellent"));
        assertTrue(result.contains("Fair"));

        // Verify that the repository methods were called
        verify(equipmentRepository).getAllEquipment();
    }
}