package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;

public class DeleteStudentCommandTest {

    private static final ClassGroup CLASS_GROUP = new ClassGroup("T05");
    private static final CourseCode MODULE = new CourseCode("CS2103T");
    private static final Telehandle TELEHANDLE = new Telehandle("birdman");
    private static final Student STUDENT = new Student(new StudentName("John Doe"), TELEHANDLE);

    private ModelManager model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        CourseClass courseClass = new CourseClass(new Name("T05"), MODULE, Set.of());
        courseClass.addStudent(STUDENT);
        model.addCourseClass(courseClass);
    }

    @Test
    public void execute_withoutConfirmation_returnsPreviewWithoutChangingData() throws Exception {
        DeleteStudentCommand command = new DeleteStudentCommand(TELEHANDLE, CLASS_GROUP, MODULE);

        CommandResult result = command.execute(model);

        assertEquals(String.format(DeleteStudentCommand.MESSAGE_CONFIRMATION,
                "John Doe", "birdman", "T05", "CS2103T"), result.getFeedbackToUser());
        assertTrue(model.findCourseClassByGroup(CLASS_GROUP, MODULE).orElseThrow().hasStudent(TELEHANDLE));
    }

    @Test
    public void execute_afterConfirmation_removesStudent() throws Exception {
        DeleteStudentCommand command = new DeleteStudentCommand(TELEHANDLE, CLASS_GROUP, MODULE);

        CommandResult result = command.confirm().execute(model);

        assertEquals(String.format(DeleteStudentCommand.MESSAGE_SUCCESS,
                "birdman", "T05", "CS2103T"), result.getFeedbackToUser());
        assertFalse(model.findCourseClassByGroup(CLASS_GROUP, MODULE).orElseThrow().hasStudent(TELEHANDLE));
    }

    @Test
    public void execute_missingClass_throwsCommandException() {
        DeleteStudentCommand command = new DeleteStudentCommand(TELEHANDLE,
                new ClassGroup("L14"), MODULE);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(String.format(DeleteStudentCommand.MESSAGE_CLASS_NOT_FOUND, "L14", "CS2103T"),
                exception.getMessage());
    }

    @Test
    public void execute_missingStudent_throwsCommandException() {
        DeleteStudentCommand command = new DeleteStudentCommand(new Telehandle("janedoe"), CLASS_GROUP, MODULE);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(String.format(DeleteStudentCommand.MESSAGE_STUDENT_NOT_FOUND,
                "janedoe", "T05", "CS2103T"), exception.getMessage());
    }

    @Test
    public void equals_comparesAllFieldsAndConfirmationState() {
        DeleteStudentCommand command = new DeleteStudentCommand(TELEHANDLE, CLASS_GROUP, MODULE);

        assertTrue(command.equals(command));
        assertEquals(command, new DeleteStudentCommand(TELEHANDLE, CLASS_GROUP, MODULE));
        assertFalse(command.equals(command.confirm()));
        assertFalse(command.equals(null));
        assertFalse(command.equals("command"));
    }

    @Test
    public void toStringMethod_returnsExpectedRepresentation() {
        DeleteStudentCommand command = new DeleteStudentCommand(TELEHANDLE, CLASS_GROUP, MODULE);

        String expected = DeleteStudentCommand.class.getCanonicalName()
                + "{telehandle=birdman, classGroup=T05, moduleName=CS2103T, confirmed=false}";
        assertEquals(expected, command.toString());
    }
}
