package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;
import seedu.address.storage.JsonCourseClassBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class AddStudentLogicIntegrationTest {

    @TempDir
    public Path temporaryFolder;

    @Test
    public void execute_addStudent_savesAndReloadsEnrollment() throws Exception {
        Model model = new ModelManager();
        JsonCourseClassBookStorage classStorage = createClassStorage();
        Logic logic = createLogic(model, classStorage);
        Student expectedStudent = new Student(new StudentName("John Doe"), new Telehandle("birdman"));

        logic.execute("aclass n/T05 c/CS2103T");
        CommandResult result = logic.execute("add n/John Doe t/birdman c/T05");

        assertEquals(String.format(AddStudentCommand.MESSAGE_SUCCESS, "John Doe", "T05"),
                result.getFeedbackToUser());
        assertEquals(List.of(expectedStudent), model.findCourseClassByGroup(
                new ClassGroup("T05")).orElseThrow().getStudentList());
        assertEquals(List.of(expectedStudent), classStorage.readCourseClassBook().orElseThrow()
                .getCourseClassList().get(0).getStudentList());
    }

    @Test
    public void execute_sameStudentInDifferentClasses_succeeds() throws Exception {
        Model model = new ModelManager();
        JsonCourseClassBookStorage classStorage = createClassStorage();
        Logic logic = createLogic(model, classStorage);

        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("aclass n/L14 c/CS2103T");
        logic.execute("add n/John Doe t/birdman c/T05");
        logic.execute("add n/John Doe t/birdman c/L14");

        assertEquals(1, model.findCourseClassByGroup(new ClassGroup("T05")).orElseThrow().getStudentList().size());
        assertEquals(1, model.findCourseClassByGroup(new ClassGroup("L14")).orElseThrow().getStudentList().size());
    }

    @Test
    public void execute_duplicateEnrollment_failsWithoutChangingData() throws Exception {
        Model model = new ModelManager();
        JsonCourseClassBookStorage classStorage = createClassStorage();
        Logic logic = createLogic(model, classStorage);

        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("add n/John Doe t/birdman c/T05");

        CommandException exception = assertThrows(CommandException.class, () ->
                logic.execute("add n/John Doe t/birdman c/T05"));

        assertEquals(String.format(AddStudentCommand.MESSAGE_DUPLICATE_STUDENT, "T05"), exception.getMessage());
        assertEquals(1, model.findCourseClassByGroup(new ClassGroup("T05")).orElseThrow().getStudentList().size());
    }

    @Test
    public void execute_invalidStudentInput_failsBeforeChangingData() throws Exception {
        Model model = new ModelManager();
        JsonCourseClassBookStorage classStorage = createClassStorage();
        Logic logic = createLogic(model, classStorage);

        logic.execute("aclass n/T05 c/CS2103T");

        assertThrows(ParseException.class, () -> logic.execute("add n/John2 t/birdman c/T05"));
        assertEquals(0, model.findCourseClassByGroup(new ClassGroup("T05")).orElseThrow().getStudentList().size());
    }

    private JsonCourseClassBookStorage createClassStorage() {
        return new JsonCourseClassBookStorage(temporaryFolder.resolve("classes.json"));
    }

    private Logic createLogic(Model model, JsonCourseClassBookStorage classStorage) {
        return new LogicManager(model, new StorageManager(classStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }
}
