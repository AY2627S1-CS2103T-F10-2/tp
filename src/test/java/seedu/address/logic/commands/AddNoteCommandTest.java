package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
import seedu.address.model.student.Note;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;

public class AddNoteCommandTest {

    private static final LocalDateTime NOTE_TIME = LocalDateTime.of(2026, 10, 13, 12, 0);
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-10-13T04:00:00Z"), ZoneOffset.ofHours(8));

    private final Telehandle telehandle = new Telehandle("birdman");
    private final ClassGroup classGroup = new ClassGroup("T05");
    private final CourseCode moduleName = new CourseCode("CS2103T");

    @Test
    public void constructor_invalidFields_throwsException() {
        assertThrows(NullPointerException.class, () ->
                new AddNoteCommand(null, classGroup, moduleName, "Needs follow-up"));
        assertThrows(NullPointerException.class, () ->
                new AddNoteCommand(telehandle, null, moduleName, "Needs follow-up"));
        assertThrows(NullPointerException.class, () ->
                new AddNoteCommand(telehandle, classGroup, null, "Needs follow-up"));
        assertThrows(NullPointerException.class, () ->
                new AddNoteCommand(telehandle, classGroup, moduleName, null));
        assertThrows(IllegalArgumentException.class, () ->
                new AddNoteCommand(telehandle, classGroup, moduleName, "   "));
    }

    @Test
    public void execute_validTarget_appendsTimestampedNote() throws Exception {
        Model model = new ModelManager();
        CourseClass courseClass = createCourseClass(moduleName);
        Note existingNote = new Note("Participated actively", NOTE_TIME.minusDays(1));
        Student student = new Student(new StudentName("John Doe"), telehandle, List.of(existingNote));
        courseClass.addStudent(student);
        model.addCourseClass(courseClass);
        AddNoteCommand command = new AddNoteCommand(
                telehandle, classGroup, moduleName, "Needs follow-up", FIXED_CLOCK);

        CommandResult result = command.execute(model);

        Student updatedStudent = courseClass.getStudentList().getFirst();
        assertEquals(List.of(existingNote, new Note("Needs follow-up", NOTE_TIME)), updatedStudent.getNotes());
        assertEquals(String.format(AddNoteCommand.MESSAGE_SUCCESS,
                student.getName(), telehandle, classGroup, moduleName), result.getFeedbackToUser());
    }

    @Test
    public void execute_sameStudentInDifferentModules_updatesRequestedClassOnly() throws Exception {
        Model model = new ModelManager();
        CourseClass targetClass = createCourseClass(moduleName);
        CourseClass otherClass = createCourseClass(new CourseCode("CS2101"));
        Student student = new Student(new StudentName("John Doe"), telehandle);
        targetClass.addStudent(student);
        otherClass.addStudent(student);
        model.addCourseClass(targetClass);
        model.addCourseClass(otherClass);

        new AddNoteCommand(telehandle, classGroup, moduleName, "Needs follow-up", FIXED_CLOCK).execute(model);

        assertEquals(List.of(new Note("Needs follow-up", NOTE_TIME)),
                targetClass.getStudentList().getFirst().getNotes());
        assertTrue(otherClass.getStudentList().getFirst().getNotes().isEmpty());
    }

    @Test
    public void execute_missingClass_throwsCommandException() {
        Model model = new ModelManager();
        AddNoteCommand command = new AddNoteCommand(
                telehandle, classGroup, moduleName, "Needs follow-up", FIXED_CLOCK);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(String.format(AddNoteCommand.MESSAGE_CLASS_NOT_FOUND, classGroup, moduleName),
                exception.getMessage());
        assertTrue(model.getFilteredCourseClassList().isEmpty());
    }

    @Test
    public void execute_missingStudent_throwsCommandExceptionWithoutChangingClass() {
        Model model = new ModelManager();
        CourseClass courseClass = createCourseClass(moduleName);
        model.addCourseClass(courseClass);
        AddNoteCommand command = new AddNoteCommand(
                telehandle, classGroup, moduleName, "Needs follow-up", FIXED_CLOCK);

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(String.format(AddNoteCommand.MESSAGE_STUDENT_NOT_FOUND,
                telehandle, classGroup, moduleName), exception.getMessage());
        assertTrue(courseClass.getStudentList().isEmpty());
    }

    @Test
    public void equals_comparesEveryField() {
        AddNoteCommand command = new AddNoteCommand(telehandle, classGroup, moduleName, "Needs follow-up");

        assertTrue(command.equals(command));
        assertEquals(command, new AddNoteCommand(telehandle, classGroup, moduleName, "Needs follow-up"));
        assertFalse(command.equals(null));
        assertFalse(command.equals("command"));
        assertFalse(command.equals(new AddNoteCommand(
                new Telehandle("janedoe"), classGroup, moduleName, "Needs follow-up")));
        assertFalse(command.equals(new AddNoteCommand(
                telehandle, new ClassGroup("T06"), moduleName, "Needs follow-up")));
        assertFalse(command.equals(new AddNoteCommand(
                telehandle, classGroup, new CourseCode("CS2101"), "Needs follow-up")));
        assertFalse(command.equals(new AddNoteCommand(telehandle, classGroup, moduleName, "Different note")));
    }

    @Test
    public void toStringMethod_returnsExpectedRepresentation() {
        AddNoteCommand command = new AddNoteCommand(telehandle, classGroup, moduleName, "Needs follow-up");
        String expected = AddNoteCommand.class.getCanonicalName()
                + "{telehandle=" + telehandle
                + ", classGroup=" + classGroup
                + ", moduleName=" + moduleName
                + ", noteText=Needs follow-up}";

        assertEquals(expected, command.toString());
    }

    private CourseClass createCourseClass(CourseCode courseCode) {
        return new CourseClass(new Name(classGroup.value), courseCode, Set.of());
    }
}
