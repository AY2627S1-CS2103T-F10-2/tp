package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class StudentNameTest {

    @Test
    public void constructor_validNames_success() {
        for (String name : new String[] {"John Doe", "Anne-Marie", "O'Connor", "Élodie Tan"}) {
            assertTrue(StudentName.isValidName(name));
            assertEquals(name, new StudentName(name).toString());
        }
    }

    @Test
    public void constructor_invalidNames_throwsIllegalArgumentException() {
        for (String name : new String[] {"", " ", "John2", "John#", " John", "John ", "John  Doe"}) {
            assertFalse(StudentName.isValidName(name));
            assertThrows(IllegalArgumentException.class, () -> new StudentName(name));
        }
    }

    @Test
    public void constructor_nullName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudentName(null));
        assertFalse(StudentName.isValidName(null));
    }

    @Test
    public void constructor_nameWithinLengthLimit_success() {
        for (int length : new int[] {1, 49, 50}) {
            String name = "A".repeat(length);
            assertTrue(StudentName.isValidName(name));
            assertEquals(name, new StudentName(name).fullName);
        }
    }

    @Test
    public void constructor_nameExceedsLengthLimit_throwsIllegalArgumentException() {
        for (String name : new String[] {"A".repeat(51), "A ".repeat(10000) + "A"}) {
            assertFalse(StudentName.isValidName(name));
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> new StudentName(name));
            assertEquals(StudentName.MESSAGE_CONSTRAINTS, exception.getMessage());
        }
    }

    @Test
    public void equals_sameValue_returnsTrue() {
        StudentName first = new StudentName("John Doe");
        StudentName second = new StudentName("John Doe");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, null);
        assertNotEquals(first, "John Doe");
        assertNotEquals(first, new StudentName("Jane Doe"));
    }
}
