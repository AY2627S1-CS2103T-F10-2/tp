package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void isValidTagName_acceptsCurrentSupportedCharacters() {
        for (String tag : new String[] {"tutorial", "F10-2", "#", "Lab #2", "study group"}) {
            assertTrue(Tag.isValidTagName(tag), tag);
        }
    }

    @Test
    public void isValidTagName_rejectsBlankAndUnsupportedCharacters() {
        for (String tag : new String[] {"", " ", "-", "friend*", "lab\n1", "lab\t1"}) {
            assertFalse(Tag.isValidTagName(tag), tag);
        }
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));
    }

}
