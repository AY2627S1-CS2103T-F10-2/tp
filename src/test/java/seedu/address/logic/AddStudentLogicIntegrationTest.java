package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteStudentCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseCode;
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
        CommandResult result = logic.execute("add n/John Doe t/birdman c/T05 m/CS2103T");

        assertEquals(String.format(AddStudentCommand.MESSAGE_SUCCESS, "John Doe", "T05", "CS2103T"),
                result.getFeedbackToUser());
        assertEquals(List.of(expectedStudent), model.findCourseClassByGroup(
                new ClassGroup("T05"), new CourseCode("CS2103T")).orElseThrow().getStudentList());
        assertEquals(List.of(expectedStudent), classStorage.readCourseClassBook().orElseThrow()
                .getCourseClassList().get(0).getStudentList());
    }

    @Test
    public void execute_addStudentToHyphenatedClassGroup_succeeds() throws Exception {
        Model model = new ModelManager();
        JsonCourseClassBookStorage classStorage = createClassStorage();
        Logic logic = createLogic(model, classStorage);

        logic.execute("aclass n/F10-2 c/CS2103T");
        CommandResult result = logic.execute("add n/John Doe t/birdman c/F10-2 m/CS2103T");

        assertEquals(String.format(AddStudentCommand.MESSAGE_SUCCESS, "John Doe", "F10-2", "CS2103T"),
                result.getFeedbackToUser());
        assertEquals(1, model.findCourseClassByGroup(new ClassGroup("F10-2"), new CourseCode("CS2103T"))
                .orElseThrow().getStudentList().size());
    }

    @Test
    public void execute_sameStudentInDifferentClasses_succeeds() throws Exception {
        Model model = new ModelManager();
        JsonCourseClassBookStorage classStorage = createClassStorage();
        Logic logic = createLogic(model, classStorage);

        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("aclass n/T05 c/CS2101");
        logic.execute("add n/John Doe t/birdman c/T05 m/CS2103T");
        logic.execute("add n/John Doe t/birdman c/T05 m/CS2101");

        assertEquals(1, model.findCourseClassByGroup(new ClassGroup("T05"), new CourseCode("CS2103T"))
                .orElseThrow().getStudentList().size());
        assertEquals(1, model.findCourseClassByGroup(new ClassGroup("T05"), new CourseCode("CS2101"))
                .orElseThrow().getStudentList().size());
    }

    @Test
    public void execute_duplicateEnrollment_failsWithoutChangingData() throws Exception {
        Model model = new ModelManager();
        JsonCourseClassBookStorage classStorage = createClassStorage();
        Logic logic = createLogic(model, classStorage);

        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("add n/John Doe t/birdman c/T05 m/CS2103T");

        CommandException exception = assertThrows(CommandException.class, () ->
                logic.execute("add n/John Doe t/birdman c/T05 m/CS2103T"));

        assertEquals(String.format(AddStudentCommand.MESSAGE_DUPLICATE_STUDENT, "T05", "CS2103T"),
                exception.getMessage());
        assertEquals(1, model.findCourseClassByGroup(new ClassGroup("T05"), new CourseCode("CS2103T"))
                .orElseThrow().getStudentList().size());
    }

    @Test
    public void execute_invalidStudentInput_failsBeforeChangingData() throws Exception {
        Model model = new ModelManager();
        JsonCourseClassBookStorage classStorage = createClassStorage();
        Logic logic = createLogic(model, classStorage);

        logic.execute("aclass n/T05 c/CS2103T");

        assertThrows(ParseException.class, () ->
                logic.execute("add n/John2 t/birdman c/T05 m/CS2103T"));
        assertEquals(0, model.findCourseClassByGroup(new ClassGroup("T05"), new CourseCode("CS2103T"))
                .orElseThrow().getStudentList().size());
    }

    @Test
    public void execute_deleteStudent_requiresConfirmationAndRemovesOnlyRequestedEnrollment() throws Exception {
        Model model = new ModelManager();
        JsonCourseClassBookStorage classStorage = createClassStorage();
        Logic logic = createLogic(model, classStorage);

        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("aclass n/T05 c/CS2101");
        logic.execute("add n/John Doe t/birdman c/T05 m/CS2103T");
        logic.execute("add n/John Doe t/birdman c/T05 m/CS2101");

        CommandResult preview = logic.execute("dstudent t/birdman c/T05 m/CS2103T");

        assertEquals(String.format(DeleteStudentCommand.MESSAGE_CONFIRMATION,
                "John Doe", "birdman", "T05", "CS2103T"), preview.getFeedbackToUser());
        assertEquals(1, model.findCourseClassByGroup(new ClassGroup("T05"), new CourseCode("CS2103T"))
                .orElseThrow().getStudentList().size());

        CommandResult deletion = logic.execute("yes");

        assertEquals(String.format(DeleteStudentCommand.MESSAGE_SUCCESS,
                "birdman", "T05", "CS2103T"), deletion.getFeedbackToUser());
        assertEquals(0, model.findCourseClassByGroup(new ClassGroup("T05"), new CourseCode("CS2103T"))
                .orElseThrow().getStudentList().size());
        assertEquals(1, model.findCourseClassByGroup(new ClassGroup("T05"), new CourseCode("CS2101"))
                .orElseThrow().getStudentList().size());
        assertEquals(0, classStorage.readCourseClassBook().orElseThrow().getCourseClassList().stream()
                .filter(courseClass -> courseClass.getCourseCode().equals(new CourseCode("CS2103T")))
                .findFirst().orElseThrow().getStudentList().size());
    }

    @Test
    public void execute_deleteStudent_noCancelsWithoutChangingData() throws Exception {
        Model model = new ModelManager();
        JsonCourseClassBookStorage classStorage = createClassStorage();
        Logic logic = createLogic(model, classStorage);

        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("add n/John Doe t/birdman c/T05 m/CS2103T");
        logic.execute("dstudent t/birdman c/T05 m/CS2103T");

        CommandResult cancellation = logic.execute("no");

        assertEquals(DeleteStudentCommand.MESSAGE_CANCELLED, cancellation.getFeedbackToUser());
        assertEquals(1, model.findCourseClassByGroup(new ClassGroup("T05"), new CourseCode("CS2103T"))
                .orElseThrow().getStudentList().size());
    }

    @Test
    public void execute_invalidConfirmation_keepsPendingDeletion() throws Exception {
        Model model = new ModelManager();
        JsonCourseClassBookStorage classStorage = createClassStorage();
        Logic logic = createLogic(model, classStorage);

        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("add n/John Doe t/birdman c/T05 m/CS2103T");
        logic.execute("dstudent t/birdman c/T05 m/CS2103T");

        CommandException exception = assertThrows(CommandException.class, () -> logic.execute("maybe"));

        assertEquals(DeleteStudentCommand.MESSAGE_EXPECTED_CONFIRMATION, exception.getMessage());
        assertEquals(1, model.findCourseClassByGroup(new ClassGroup("T05"), new CourseCode("CS2103T"))
                .orElseThrow().getStudentList().size());
        logic.execute("no");
    }

    private JsonCourseClassBookStorage createClassStorage() {
        return new JsonCourseClassBookStorage(temporaryFolder.resolve("classes.json"));
    }

    private Logic createLogic(Model model, JsonCourseClassBookStorage classStorage) {
        return new LogicManager(model, new StorageManager(classStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
    }
}
