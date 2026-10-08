package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.student.Note;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;

public class JsonAdaptedStudentTest {

    private static final LocalDateTime TIMESTAMP = LocalDateTime.of(2026, 10, 8, 14, 30);

    @Test
    public void toModelType_invalidFields_rejectsMalformedData() {
        for (String name : new String[] {null, "", "John2"}) {
            assertThrows(IllegalValueException.class, () ->
                    new JsonAdaptedStudent(name, "birdman").toModelType());
        }
        for (String telehandle : new String[] {null, "abcd", "@birdman"}) {
            assertThrows(IllegalValueException.class, () ->
                    new JsonAdaptedStudent("John Doe", telehandle).toModelType());
        }
    }

    @Test
    public void toModelType_validFields_returnsStudent() throws Exception {
        Note note = new Note("Needs follow-up", TIMESTAMP);
        Student expected = new Student(new StudentName("John Doe"), new Telehandle("birdman"), List.of(note));

        assertEquals(expected, new JsonAdaptedStudent("John Doe", "birdman",
                List.of(new JsonAdaptedNote("Needs follow-up", TIMESTAMP.toString()))).toModelType());
        assertEquals(expected, new JsonAdaptedStudent(expected).toModelType());
    }

    @Test
    public void toModelType_missingNotes_returnsStudentWithEmptyNotes() throws Exception {
        Student restored = new JsonAdaptedStudent("John Doe", "birdman").toModelType();

        assertTrue(restored.getNotes().isEmpty());
    }

    @Test
    public void toModelType_nullNote_rejectsMalformedData() {
        List<JsonAdaptedNote> notesWithNullElement = Arrays.asList((JsonAdaptedNote) null);
        JsonAdaptedStudent adaptedStudent = new JsonAdaptedStudent("John Doe", "birdman", notesWithNullElement);

        assertThrows(IllegalValueException.class, adaptedStudent::toModelType);
    }
}
