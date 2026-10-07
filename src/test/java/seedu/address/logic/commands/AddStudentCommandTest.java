package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;

public class AddStudentCommandTest {

    private final Student firstStudent = new Student(new StudentName("John Doe"), new Telehandle("birdman"));
    private final Student secondStudent = new Student(new StudentName("Jane Doe"), new Telehandle("janedoe"));
    private final ClassGroup firstClassGroup = new ClassGroup("T05");
    private final ClassGroup secondClassGroup = new ClassGroup("F10-2");
    private final CourseCode firstModule = new CourseCode("CS2103T");
    private final CourseCode secondModule = new CourseCode("CS2101");

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                new AddStudentCommand(null, firstClassGroup, firstModule));
        assertThrows(NullPointerException.class, () ->
                new AddStudentCommand(firstStudent, null, firstModule));
        assertThrows(NullPointerException.class, () ->
                new AddStudentCommand(firstStudent, firstClassGroup, null));
    }

    @Test
    public void equals_comparesEveryField() {
        AddStudentCommand command = new AddStudentCommand(firstStudent, firstClassGroup, firstModule);

        assertTrue(command.equals(command));
        assertEquals(command, new AddStudentCommand(firstStudent, firstClassGroup, firstModule));
        assertFalse(command.equals(null));
        assertFalse(command.equals("command"));
        assertFalse(command.equals(new AddStudentCommand(secondStudent, firstClassGroup, firstModule)));
        assertFalse(command.equals(new AddStudentCommand(firstStudent, secondClassGroup, firstModule)));
        assertFalse(command.equals(new AddStudentCommand(firstStudent, firstClassGroup, secondModule)));
    }

    @Test
    public void toStringMethod_returnsExpectedRepresentation() {
        AddStudentCommand command = new AddStudentCommand(firstStudent, firstClassGroup, firstModule);
        String expected = AddStudentCommand.class.getCanonicalName() + "{toAdd=" + firstStudent
                + ", classGroup=" + firstClassGroup + ", moduleName=" + firstModule + "}";

        assertEquals(expected, command.toString());
    }
}
