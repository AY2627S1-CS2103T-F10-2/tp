package seedu.address.model.courseclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_nameWithinLengthLimit_success() {
        for (int length : new int[] {49, 50}) {
            String name = "F".repeat(length);
            assertTrue(Name.isValidName(name));
            assertEquals(name, new Name(name).fullName);
        }
    }

    @Test
    public void constructor_nameExceedsLengthLimit_throwsIllegalArgumentException() {
        String name = "F".repeat(51);
        assertFalse(Name.isValidName(name));
        assertThrows(IllegalArgumentException.class, () -> new Name(name));
    }
}
