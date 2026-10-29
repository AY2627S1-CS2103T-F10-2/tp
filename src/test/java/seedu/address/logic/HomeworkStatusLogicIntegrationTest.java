package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.SetHomeworkStatusCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.courseclass.HomeworkStatus;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;
import seedu.address.storage.JsonCourseClassBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class HomeworkStatusLogicIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void execute_homeworkStatus_appliesToClassAndPersists() throws Exception {
        Model model = new ModelManager();
        JsonCourseClassBookStorage classStorage = new JsonCourseClassBookStorage(
                temporaryFolder.resolve("classes.json"));
        Logic logic = new LogicManager(model, new StorageManager(classStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));

        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("add n/John Doe t/john_doe c/T05 m/CS2103T");
        logic.execute("add n/Jane Doe t/jane_doe c/T05 m/CS2103T");
        logic.execute("aclass n/T06 c/CS2101");
        logic.execute("add n/Alice Tan t/alice_tan c/T06 m/CS2101");
        logic.execute("hwork 2 s/pending");
        var result = logic.execute("hwork 1 s/completed");

        assertEquals(String.format(SetHomeworkStatusCommand.MESSAGE_SUCCESS, "T05", "completed"),
                result.getFeedbackToUser());
        assertEquals(HomeworkStatus.COMPLETED, model.getFilteredCourseClassList().getFirst().getHomeworkStatus());
        assertEquals(List.of(new Student(new StudentName("John Doe"), new Telehandle("john_doe")),
                        new Student(new StudentName("Jane Doe"), new Telehandle("jane_doe"))),
                model.getFilteredCourseClassList().getFirst().getStudentList());
        assertEquals(HomeworkStatus.PENDING, model.getFilteredCourseClassList().get(1).getHomeworkStatus());
        assertEquals(List.of(new Student(new StudentName("Alice Tan"), new Telehandle("alice_tan"))),
                model.getFilteredCourseClassList().get(1).getStudentList());
        assertEquals(HomeworkStatus.COMPLETED, classStorage.readCourseClassBook().orElseThrow()
                .getCourseClassList().getFirst().getHomeworkStatus());
        assertEquals(HomeworkStatus.PENDING, classStorage.readCourseClassBook().orElseThrow()
                .getCourseClassList().get(1).getHomeworkStatus());
    }

    @Test
    public void execute_individualHomeworkStatus_appliesOnlyToTargetStudentAndPersists() throws Exception {
        Model model = new ModelManager();
        JsonCourseClassBookStorage classStorage = new JsonCourseClassBookStorage(
                temporaryFolder.resolve("individual-classes.json"));
        Logic logic = new LogicManager(model, new StorageManager(classStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("individual-preferences.json"))));

        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("add n/John Doe t/john_doe c/T05 m/CS2103T");
        logic.execute("add n/Jane Doe t/jane_doe c/T05 m/CS2103T");
        logic.execute("hwork 1 s/pending");
        logic.execute("hwork 1 t/john_doe s/completed");

        var courseClass = model.getFilteredCourseClassList().getFirst();
        assertEquals(HomeworkStatus.COMPLETED, courseClass.getHomeworkStatus(new Telehandle("john_doe")));
        assertEquals(HomeworkStatus.PENDING, courseClass.getHomeworkStatus(new Telehandle("jane_doe")));

        var restoredClass = classStorage.readCourseClassBook().orElseThrow().getCourseClassList().getFirst();
        assertEquals(HomeworkStatus.COMPLETED, restoredClass.getHomeworkStatus(new Telehandle("john_doe")));
        assertEquals(HomeworkStatus.PENDING, restoredClass.getHomeworkStatus(new Telehandle("jane_doe")));
    }
}
