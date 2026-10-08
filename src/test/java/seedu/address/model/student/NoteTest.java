package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class NoteTest {

    private static final LocalDateTime TIMESTAMP = LocalDateTime.of(2026, 10, 8, 14, 30);

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Note(null, TIMESTAMP));
        assertThrows(NullPointerException.class, () -> new Note("Needs follow-up", null));
    }

    @Test
    public void constructor_blankText_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Note("", TIMESTAMP));
        assertThrows(IllegalArgumentException.class, () -> new Note("   ", TIMESTAMP));
        assertThrows(IllegalArgumentException.class, () -> new Note("\t\n", TIMESTAMP));
    }

    @Test
    public void isValidText_rejectsNullOrBlankText() {
        assertFalse(Note.isValidText(null));
        assertFalse(Note.isValidText(""));
        assertFalse(Note.isValidText("   "));
        assertTrue(Note.isValidText("Needs follow-up"));
    }

    @Test
    public void getters_returnsNoteDetails() {
        Note note = new Note("Needs follow-up", TIMESTAMP);

        assertEquals("Needs follow-up", note.getText());
        assertEquals(TIMESTAMP, note.getTimestamp());
    }

    @Test
    public void equals_comparesTextAndTimestamp() {
        Note note = new Note("Needs follow-up", TIMESTAMP);
        Note copy = new Note("Needs follow-up", LocalDateTime.of(2026, 10, 8, 14, 30));

        assertEquals(note, note);
        assertEquals(note, copy);
        assertEquals(note.hashCode(), copy.hashCode());
        assertNotEquals(note, null);
        assertNotEquals(note, "Needs follow-up");
        assertNotEquals(note, new Note("Participated actively", TIMESTAMP));
        assertNotEquals(note, new Note("Needs follow-up", TIMESTAMP.plusMinutes(1)));
        assertEquals(Note.class.getCanonicalName()
                + "{text=Needs follow-up, timestamp=2026-10-08T14:30}", note.toString());
    }
}
