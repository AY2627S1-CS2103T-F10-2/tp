package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.CourseClassBook;
import seedu.address.model.courseClass.CourseClass;
import seedu.address.model.courseClass.CourseCode;
import seedu.address.model.courseClass.Name;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.TypicalPersons;

public class JsonCourseClassBookStorageTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void saveAndRead_roundTrip_preservesClassFieldsAndStudents() throws Exception {
        Path file = temporaryFolder.resolve("nested/classes.json");
        JsonCourseClassBookStorage storage = new JsonCourseClassBookStorage(file);
        assertTrue(storage.readCourseClassBook().isEmpty());
        CourseClassBook book = new CourseClassBook();
        CourseClass courseClass = new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"),
                Set.of(new Tag("tutorial")));
        courseClass.getStudents().addPerson(TypicalPersons.ALICE);
        book.addCourseClass(courseClass);
        storage.saveCourseClassBook(book);
        var restored = storage.readCourseClassBook().orElseThrow();
        assertEquals(book, restored);
        assertEquals(courseClass.getStudents(), restored.getCourseClassList().get(0).getStudents());
    }

    @Test
    public void read_invalidOrDuplicateClasses_throwsDataLoadingException() throws Exception {
        Path file = temporaryFolder.resolve("classes.json");
        JsonCourseClassBookStorage storage = new JsonCourseClassBookStorage(file);
        String valid = "{\"name\":\"F10-2\",\"courseCode\":\"CS2103T\",\"tags\":[]}";
        for (String json : new String[] {"{}", "{\"courseClasses\":[null]}",
            "{\"courseClasses\":[{\"name\":\"F10-2\"}]}",
            "{\"courseClasses\":[" + valid + "," + valid + "]}"}) {
            Files.writeString(file, json);
            assertThrows(DataLoadingException.class, storage::readCourseClassBook);
        }
    }
}
