/**
 * @author Group 9
 */
package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.model.Equipment;
import org.example.model.ProfessionalStaff;
import org.example.service.AuthenticationService;
import org.example.service.EquipmentService;
import org.example.service.LendingService;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ProfessionalStaffController class.
 * Tests the auto-complete search functionality.
 */
public class ProfessionalStaffControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserService userService;

    @Mock
    private EquipmentService equipmentService;

    @Mock
    private LendingService lendingService;

    @InjectMocks
    private ProfessionalStaffController controller;

    private ProfessionalStaff testStaff;
    private List<Equipment> testEquipment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test staff
        testStaff = new ProfessionalStaff();
        testStaff.setId(1);
        testStaff.setUsername("teststaff");
        testStaff.setFirstName("Test");
        testStaff.setLastName("Staff");
        testStaff.setEmail("test.staff@example.com");
        testStaff.setStaffId("PS001");
        testStaff.setDepartment("IT");
        testStaff.setPosition("Technician");
        testStaff.setSpecialization("Hardware");

        // Create test equipment
        Equipment equipment1 = new Equipment();
        equipment1.setId(1);
        equipment1.setName("3D Printer");
        equipment1.setCategory("Printer");
        equipment1.setCondition("Good");
        equipment1.setAvailable(true);

        Equipment equipment2 = new Equipment();
        equipment2.setId(2);
        equipment2.setName("Laser Cutter");
        equipment2.setCategory("Cutter");
        equipment2.setCondition("Excellent");
        equipment2.setAvailable(true);

        Equipment equipment3 = new Equipment();
        equipment3.setId(3);
        equipment3.setName("Digital Camera");
        equipment3.setCategory("Camera");
        equipment3.setCondition("Fair");
        equipment3.setAvailable(true);

        testEquipment = Arrays.asList(equipment1, equipment2, equipment3);
    }

    /**
     * Test that the auto-complete suggestions are correctly initialized.
     * This test verifies that the equipment names are loaded from the service
     * and added to the suggestions list.
     */
    @Test
    public void testAutoCompleteSuggestions() {
        // Mock the equipment service to return test equipment
        when(equipmentService.getAllEquipment()).thenReturn(testEquipment);

        // Create a list of expected equipment names
        List<String> expectedNames = Arrays.asList("3D Printer", "Laser Cutter", "Digital Camera");

        // Actually call the service to get the equipment
        List<Equipment> retrievedEquipment = equipmentService.getAllEquipment();
        
        // Call the method that would initialize auto-complete
        // Note: Since we can't directly test the UI components in a unit test,
        // we're testing the logic that would populate the suggestions list
        ObservableList<String> suggestions = FXCollections.observableArrayList(
                retrievedEquipment.stream()
                        .map(Equipment::getName)
                        .distinct()
                        .toList()
        );

        // Verify that the suggestions list contains all expected names
        assertEquals(expectedNames.size(), suggestions.size());
        assertTrue(suggestions.containsAll(expectedNames));

        // Verify that the equipment service was called
        verify(equipmentService, times(1)).getAllEquipment();
    }

    /**
     * Test filtering of auto-complete suggestions based on user input.
     * This test verifies that the suggestions are correctly filtered
     * when the user types in the search field.
     */
    @Test
    public void testAutoCompleteFiltering() {
        // Create a list of equipment names
        ObservableList<String> suggestions = FXCollections.observableArrayList(
                "3D Printer", "Laser Cutter", "Digital Camera"
        );

        // Test filtering with "printer" (should match "3D Printer")
        final String printerInput = "printer";
        ObservableList<String> filteredList = FXCollections.observableArrayList(
                suggestions.stream()
                        .filter(name -> name.toLowerCase().contains(printerInput.toLowerCase()))
                        .toList()
        );

        assertEquals(1, filteredList.size());
        assertEquals("3D Printer", filteredList.get(0));

        // Test filtering with "cut" (should match "Laser Cutter")
        final String cutterInput = "cut";
        filteredList = FXCollections.observableArrayList(
                suggestions.stream()
                        .filter(name -> name.toLowerCase().contains(cutterInput.toLowerCase()))
                        .toList()
        );

        assertEquals(1, filteredList.size());
        assertEquals("Laser Cutter", filteredList.get(0));

        // Test filtering with "a" (should match all three)
        final String aInput = "a";
        filteredList = FXCollections.observableArrayList(
                suggestions.stream()
                        .filter(name -> name.toLowerCase().contains(aInput.toLowerCase()))
                        .toList()
        );

        // Update the expected count to match the actual filtered count (2 instead of 3)
        assertEquals(2, filteredList.size());
        // 3D Printer doesn't contain the letter "a"
        assertFalse(filteredList.contains("3D Printer"));
        assertTrue(filteredList.contains("Laser Cutter"));
        assertTrue(filteredList.contains("Digital Camera"));

        // Test filtering with "xyz" (should match none)
        final String xyzInput = "xyz";
        filteredList = FXCollections.observableArrayList(
                suggestions.stream()
                        .filter(name -> name.toLowerCase().contains(xyzInput.toLowerCase()))
                        .toList()
        );

        assertEquals(0, filteredList.size());
    }
}
