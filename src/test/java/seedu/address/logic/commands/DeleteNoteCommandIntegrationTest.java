package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyCourseClassBook;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
import seedu.address.model.student.Note;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;
import seedu.address.storage.JsonCourseClassBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class DeleteNoteCommandIntegrationTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void execute_parsedCommand_savesDeletionForReload() throws Exception {
        Path dataFile = temporaryFolder.resolve("courseclasses.json");
        JsonCourseClassBookStorage courseClassBookStorage = new JsonCourseClassBookStorage(dataFile);
        StorageManager storage = new StorageManager(courseClassBookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json")));
        Model model = new ModelManager();
        CourseClass courseClass = new CourseClass(
                new Name("T05"), new CourseCode("CS2103T"), Set.of());
        Note firstNote = new Note("Participated actively", LocalDateTime.of(2026, 10, 8, 14, 30));
        Note secondNote = new Note("Needs follow-up", LocalDateTime.of(2026, 10, 9, 9, 0));
        courseClass.addStudent(new Student(new StudentName("John Doe"),
                new Telehandle("birdman"), List.of(firstNote, secondNote)));
        model.addCourseClass(courseClass);
        Logic logic = new LogicManager(model, storage);

        logic.execute("dnote t/birdman c/T05 m/CS2103T i/1");

        ReadOnlyCourseClassBook reloadedBook = courseClassBookStorage.readCourseClassBook().orElseThrow();
        Student reloadedStudent = reloadedBook.getCourseClassList().getFirst().getStudentList().getFirst();
        assertEquals(List.of(secondNote), reloadedStudent.getNotes());
    }
}
