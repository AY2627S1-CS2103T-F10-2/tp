package seedu.address.model.courseclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_validNames_success() {
        for (String name : new String[] {"F", "F111", "123", "Tutorial 1", "École", "E\u0301cole",
            "Tutor's group", "F10-2", "L01/L02"}) {
            assertTrue(Name.isValidName(name));
            assertEquals(name, new Name(name).fullName);
            assertEquals(name, new Name(name).toString());
        }
    }

    @Test
    public void constructor_invalidNames_throwsIllegalArgumentException() {
        for (String name : new String[] {"", " ", " F111", "F111!", "T_05", "-T05", "F\n111"}) {
            assertFalse(Name.isValidName(name));
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Name(name));
            assertEquals(Name.MESSAGE_CONSTRAINTS, exception.getMessage());
        }
    }

    @Test
    public void constructor_nullName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void isValidName_nullName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));
    }

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

    @Test
    public void equals_comparesNameValue() {
        Name name = new Name("F111");
        Name sameName = new Name("F111");

        assertTrue(name.equals(name));
        assertTrue(name.equals(sameName));
        assertTrue(sameName.equals(name));
        assertFalse(name.equals(null));
        assertFalse(name.equals("F111"));
        assertFalse(name.equals(new Name("F112")));
        assertFalse(name.equals(new Name("f111")));
    }

    @Test
    public void hashCode_equalNames_haveSameHashCode() {
        Name name = new Name("F111");

        assertEquals(name.hashCode(), new Name("F111").hashCode());
        assertEquals(name.fullName.hashCode(), name.hashCode());
    }
}
