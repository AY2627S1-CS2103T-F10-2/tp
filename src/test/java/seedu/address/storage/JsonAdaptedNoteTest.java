package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.student.Note;

public class JsonAdaptedNoteTest {

    private static final LocalDateTime TIMESTAMP = LocalDateTime.of(2026, 10, 8, 14, 30);

    @Test
    public void toModelType_invalidText_rejectsMalformedData() {
        for (String text : new String[] {null, "", "   "}) {
            JsonAdaptedNote adaptedNote = new JsonAdaptedNote(text, TIMESTAMP.toString());
            assertThrows(IllegalValueException.class, adaptedNote::toModelType);
        }
    }

    @Test
    public void toModelType_invalidTimestamp_rejectsMalformedData() {
        for (String timestamp : new String[] {null, "", "not-a-timestamp", "2026-13-08T14:30"}) {
            JsonAdaptedNote adaptedNote = new JsonAdaptedNote("Needs follow-up", timestamp);
            assertThrows(IllegalValueException.class, adaptedNote::toModelType);
        }
    }

    @Test
    public void toModelType_validFields_preservesTextAndTimestamp() throws Exception {
        Note expected = new Note("Needs follow-up", TIMESTAMP);

        assertEquals(expected, new JsonAdaptedNote("Needs follow-up", TIMESTAMP.toString()).toModelType());
        assertEquals(expected, new JsonAdaptedNote(expected).toModelType());
    }
}
