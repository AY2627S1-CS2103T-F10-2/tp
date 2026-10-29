package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.CourseClassBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.HomeworkStatus;
import seedu.address.model.courseclass.Name;
import seedu.address.model.student.Note;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;
import seedu.address.model.tag.Tag;

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
    public void pathOverloads_saveAndReadCourseClassBook() throws Exception {
        Path defaultFile = temporaryFolder.resolve("default/classes.json");
        Path alternateFile = temporaryFolder.resolve("alternate/classes.json");
        JsonCourseClassBookStorage storage = new JsonCourseClassBookStorage(defaultFile);
        CourseClassBook book = new CourseClassBook();
        book.addCourseClass(new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"), Set.of()));

        storage.saveCourseClassBook(book, alternateFile);

        assertEquals(book, storage.readCourseClassBook(alternateFile).orElseThrow());
    }

    @Test
    public void saveCourseClassBook_atomicMoveUnsupported_usesRegularMove() throws Exception {
        Path file = temporaryFolder.resolve("classes.json");
        JsonCourseClassBookStorage storage = new JsonCourseClassBookStorage(file) {
            @Override
            Path moveFile(Path source, Path target, CopyOption... options) throws IOException {
                if (List.of(options).contains(StandardCopyOption.ATOMIC_MOVE)) {
                    throw new AtomicMoveNotSupportedException(source.toString(), target.toString(), "test");
                }
                return super.moveFile(source, target, options);
            }
        };
        CourseClassBook book = new CourseClassBook();
        book.addCourseClass(new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"), Set.of()));

        storage.saveCourseClassBook(book);

        assertEquals(book, storage.readCourseClassBook().orElseThrow());
    }

    @Test
    public void serializableBook_legacyConstructor_convertsCourseClasses() throws Exception {
        CourseClass courseClass = new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"), Set.of());
        JsonSerializableCourseClassBook serializable = new JsonSerializableCourseClassBook(
                List.of(new JsonAdaptedCourseClass(courseClass)));

        assertEquals(List.of(courseClass), serializable.toModelType().getCourseClassList());
        assertTrue(serializable.toUndoHistory().isEmpty());
    }

    @Test
    public void toUndoHistory_nullEntry_throwsIllegalValueException() {
        JsonSerializableCourseClassBook serializable = new JsonSerializableCourseClassBook(
                List.of(), Collections.singletonList((JsonSerializableCourseClassBook) null));

        assertThrows(IllegalValueException.class, serializable::toUndoHistory);
    }

    @Test
    public void saveAndRead_roundTrip_preservesClassFieldsAndStudents() throws Exception {
        Path file = temporaryFolder.resolve("nested/classes.json");
        JsonCourseClassBookStorage storage = new JsonCourseClassBookStorage(file);
        assertTrue(storage.readCourseClassBook().isEmpty());
        CourseClassBook book = new CourseClassBook();
        CourseClass courseClass = new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"),
                Set.of(new Tag("tutorial")));
        courseClass.setHomeworkStatus(HomeworkStatus.PENDING);
        Note note = new Note("Needs follow-up", LocalDateTime.of(2026, 10, 8, 14, 30));
        Student student = new Student(new StudentName("John Doe"), new Telehandle("birdman"), List.of(note));
        courseClass.addStudent(student);
        book.addCourseClass(courseClass);
        storage.saveCourseClassBook(book);
        var restored = storage.readCourseClassBook().orElseThrow();
        assertEquals(book, restored);
        assertEquals(List.of(student), restored.getCourseClassList().get(0).getStudentList());
        assertEquals(HomeworkStatus.PENDING, restored.getCourseClassList().get(0).getHomeworkStatus());
    }

    @Test
    public void saveAndRead_roundTrip_preservesUndoHistory() throws Exception {
        Path file = temporaryFolder.resolve("history/classes.json");
        JsonCourseClassBookStorage storage = new JsonCourseClassBookStorage(file);
        CourseClassBook currentBook = new CourseClassBook();
        currentBook.addCourseClass(new CourseClass(new Name("F10-3"), new CourseCode("CS2103T"), Set.of()));

        CourseClass previousClass = new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"), Set.of());
        previousClass.setHomeworkStatus(HomeworkStatus.COMPLETED);
        Student previousStudent = new Student(new StudentName("John Doe"), new Telehandle("birdman"),
                List.of(new Note("Follow up", LocalDateTime.of(2026, 10, 8, 14, 30))));
        previousClass.addStudent(previousStudent);
        CourseClassBook previousBook = new CourseClassBook();
        previousBook.addCourseClass(previousClass);

        storage.saveCourseClassBookData(currentBook, List.of(previousBook));

        CourseClassBookStorageData restored = storage.readCourseClassBookData().orElseThrow();
        assertEquals(currentBook, restored.getCourseClassBook());
        CourseClass restoredHistoryClass = restored.getUndoHistory().getFirst().getCourseClassList().getFirst();
        assertEquals(previousClass.getName(), restoredHistoryClass.getName());
        assertEquals(previousClass.getCourseCode(), restoredHistoryClass.getCourseCode());
        assertEquals(HomeworkStatus.COMPLETED, restoredHistoryClass.getHomeworkStatus());
        assertEquals(List.of(previousStudent), restoredHistoryClass.getStudentList());
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
    public void read_legacyClassWithoutHomeworkStatus_defaultsToNotSet() throws Exception {
        Path file = temporaryFolder.resolve("legacy-classes.json");
        JsonCourseClassBookStorage storage = new JsonCourseClassBookStorage(file);
        String json = "{\"courseClasses\":[{\"name\":\"F10-2\",\"courseCode\":\"CS2103T\","
                + "\"tags\":[],\"enrolledStudents\":[]}]}";
        Files.writeString(file, json);

        CourseClass restored = storage.readCourseClassBook().orElseThrow()
                .getCourseClassList().getFirst();

        assertEquals(HomeworkStatus.NOT_SET, restored.getHomeworkStatus());
        assertTrue(storage.readCourseClassBookData().orElseThrow().getUndoHistory().isEmpty());
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
