package seedu.address.model.courseclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ClassGroupTest {

    @Test
    public void constructor_validClassGroups_success() {
        for (String classGroup : new String[] {"T05", "L14", "R40", "B32", "F10-2"}) {
            assertTrue(ClassGroup.isValidClassGroup(classGroup));
            assertEquals(classGroup, new ClassGroup(classGroup).toString());
        }
    }

    @Test
    public void constructor_invalidClassGroups_throwsIllegalArgumentException() {
        for (String classGroup : new String[] {"t05", "F1", "F100", "F10-", "F10-22", "F10-2-3", "T0A", ""}) {
            assertFalse(ClassGroup.isValidClassGroup(classGroup));
            assertThrows(IllegalArgumentException.class, () -> new ClassGroup(classGroup));
        }
    }

    @Test
    public void constructor_nullClassGroup_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ClassGroup(null));
        assertFalse(ClassGroup.isValidClassGroup(null));
    }
}
