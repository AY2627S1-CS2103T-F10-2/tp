package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;

public class JsonAdaptedStudentTest {

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
        Student expected = new Student(new StudentName("John Doe"), new Telehandle("birdman"));

        assertEquals(expected, new JsonAdaptedStudent("John Doe", "birdman").toModelType());
        assertEquals(expected, new JsonAdaptedStudent(expected).toModelType());
    }
}
