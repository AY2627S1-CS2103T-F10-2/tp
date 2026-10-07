package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TelehandleTest {

    @Test
    public void constructor_validTelehandles_success() {
        for (String telehandle : new String[] {"abcde", "birdman", "bird@man", "a".repeat(32)}) {
            assertTrue(Telehandle.isValidTelehandle(telehandle));
            assertEquals(telehandle, new Telehandle(telehandle).toString());
        }
    }

    @Test
    public void constructor_invalidTelehandles_throwsIllegalArgumentException() {
        for (String telehandle : new String[] {"", "abcd", "a".repeat(33), "bird man", "@birdman"}) {
            assertFalse(Telehandle.isValidTelehandle(telehandle));
            assertThrows(IllegalArgumentException.class, () -> new Telehandle(telehandle));
        }
    }

    @Test
    public void constructor_nullTelehandle_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Telehandle(null));
        assertFalse(Telehandle.isValidTelehandle(null));
    }

    @Test
    public void equals_sameValue_returnsTrue() {
        Telehandle first = new Telehandle("birdman");
        Telehandle second = new Telehandle("birdman");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, null);
        assertNotEquals(first, "birdman");
        assertNotEquals(first, new Telehandle("chanaden"));
    }
}
