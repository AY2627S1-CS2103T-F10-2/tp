package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.CourseClassBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
import seedu.address.model.student.Note;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.TypicalPersons;

public class JsonCourseClassBookStorageTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void storageManager_delegatesPathsAndRoundTripsBooksAndPreferences() throws Exception {
        Path classFile = temporaryFolder.resolve("classes.json");
        Path prefsFile = temporaryFolder.resolve("prefs.json");
        StorageManager manager = new StorageManager(new JsonCourseClassBookStorage(classFile),
                new JsonUserPrefsStorage(prefsFile));
        assertEquals(classFile, manager.getCourseClassBookFilePath());
        assertEquals(prefsFile, manager.getUserPrefsFilePath());
        assertTrue(manager.readCourseClassBook().isEmpty());
        CourseClassBook book = new CourseClassBook();
        book.addCourseClass(new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"), Set.of()));
        manager.saveCourseClassBook(book);
        assertEquals(book, manager.readCourseClassBook().orElseThrow());
        UserPrefs prefs = new UserPrefs();
        manager.saveUserPrefs(prefs);
        assertEquals(prefs, manager.readUserPrefs().orElseThrow());
    }

    @Test
    public void saveAndRead_roundTrip_preservesClassFieldsAndStudents() throws Exception {
        Path file = temporaryFolder.resolve("nested/classes.json");
        JsonCourseClassBookStorage storage = new JsonCourseClassBookStorage(file);
        assertTrue(storage.readCourseClassBook().isEmpty());
        CourseClassBook book = new CourseClassBook();
        CourseClass courseClass = new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"),
                Set.of(new Tag("tutorial")));
        courseClass.getStudents().addPerson(TypicalPersons.ALICE);
        Note note = new Note("Needs follow-up", LocalDateTime.of(2026, 10, 8, 14, 30));
        Student student = new Student(new StudentName("John Doe"), new Telehandle("birdman"), List.of(note));
        courseClass.addStudent(student);
        book.addCourseClass(courseClass);
        storage.saveCourseClassBook(book);
        var restored = storage.readCourseClassBook().orElseThrow();
        assertEquals(book, restored);
        assertEquals(courseClass.getStudents(), restored.getCourseClassList().get(0).getStudents());
        assertEquals(List.of(student), restored.getCourseClassList().get(0).getStudentList());
    }

    @Test
    public void read_studentWithoutNotes_restoresEmptyNotes() throws Exception {
        Path file = temporaryFolder.resolve("classes.json");
        JsonCourseClassBookStorage storage = new JsonCourseClassBookStorage(file);
        String json = "{\"courseClasses\":[{\"name\":\"F10-2\",\"courseCode\":\"CS2103T\","
                + "\"tags\":[],\"enrolledStudents\":[{\"name\":\"John Doe\","
                + "\"telehandle\":\"birdman\"}]}]}";
        Files.writeString(file, json);

        Student restored = storage.readCourseClassBook().orElseThrow()
                .getCourseClassList().get(0).getStudentList().get(0);

        assertTrue(restored.getNotes().isEmpty());
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
