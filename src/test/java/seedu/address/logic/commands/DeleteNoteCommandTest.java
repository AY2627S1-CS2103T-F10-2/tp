package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
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

public class DeleteNoteCommandTest {

    private static final Telehandle TELEHANDLE = new Telehandle("birdman");
    private static final ClassGroup CLASS_GROUP = new ClassGroup("T05");
    private static final CourseCode MODULE = new CourseCode("CS2103T");
    private static final Note FIRST_NOTE = new Note("Participated actively",
            LocalDateTime.of(2026, 10, 8, 14, 30));
    private static final Note SECOND_NOTE = new Note("Needs follow-up",
            LocalDateTime.of(2026, 10, 9, 9, 0));
    private static final Note THIRD_NOTE = new Note("Asked for clarification",
            LocalDateTime.of(2026, 10, 10, 10, 0));

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        Index index = Index.fromOneBased(1);

        assertThrows(NullPointerException.class, () ->
                new DeleteNoteCommand(null, CLASS_GROUP, MODULE, index));
        assertThrows(NullPointerException.class, () ->
                new DeleteNoteCommand(TELEHANDLE, null, MODULE, index));
        assertThrows(NullPointerException.class, () ->
                new DeleteNoteCommand(TELEHANDLE, CLASS_GROUP, null, index));
        assertThrows(NullPointerException.class, () ->
                new DeleteNoteCommand(TELEHANDLE, CLASS_GROUP, MODULE, null));
    }

    @Test
    public void execute_validIndex_deletesOnlySelectedNote() throws Exception {
        Model model = new ModelManager();
        CourseClass courseClass = createCourseClass(MODULE);
        Student student = createStudent(List.of(FIRST_NOTE, SECOND_NOTE, THIRD_NOTE));
        courseClass.addStudent(student);
        model.addCourseClass(courseClass);
        DeleteNoteCommand command = new DeleteNoteCommand(
                TELEHANDLE, CLASS_GROUP, MODULE, Index.fromOneBased(2));

        CommandResult result = command.execute(model);

        Student updatedStudent = courseClass.getStudentList().getFirst();
        assertEquals(List.of(FIRST_NOTE, THIRD_NOTE), updatedStudent.getNotes());
        assertEquals(String.format(DeleteNoteCommand.MESSAGE_SUCCESS, 2,
                student.getName(), TELEHANDLE, CLASS_GROUP, MODULE), result.getFeedbackToUser());
    }

    @Test
    public void execute_sameStudentInDifferentModules_updatesRequestedClassOnly() throws Exception {
        Model model = new ModelManager();
        CourseClass targetClass = createCourseClass(MODULE);
        CourseClass otherClass = createCourseClass(new CourseCode("CS2101"));
        Student student = createStudent(List.of(FIRST_NOTE, SECOND_NOTE));
        targetClass.addStudent(student);
        otherClass.addStudent(student);
        model.addCourseClass(targetClass);
        model.addCourseClass(otherClass);

        new DeleteNoteCommand(TELEHANDLE, CLASS_GROUP, MODULE, Index.fromOneBased(1)).execute(model);

        assertEquals(List.of(SECOND_NOTE), targetClass.getStudentList().getFirst().getNotes());
        assertEquals(List.of(FIRST_NOTE, SECOND_NOTE), otherClass.getStudentList().getFirst().getNotes());
    }

    @Test
    public void execute_invalidIndex_throwsCommandExceptionWithoutChangingStudent() {
        Model model = new ModelManager();
        CourseClass courseClass = createCourseClass(MODULE);
        Student student = createStudent(List.of(FIRST_NOTE, SECOND_NOTE));
        courseClass.addStudent(student);
        model.addCourseClass(courseClass);
        DeleteNoteCommand command = new DeleteNoteCommand(
                TELEHANDLE, CLASS_GROUP, MODULE, Index.fromOneBased(3));

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(String.format(DeleteNoteCommand.MESSAGE_INVALID_NOTE_INDEX, 3, TELEHANDLE, 2),
                exception.getMessage());
        assertEquals(List.of(FIRST_NOTE, SECOND_NOTE), courseClass.getStudentList().getFirst().getNotes());
    }

    @Test
    public void execute_studentWithoutNotes_throwsCommandException() {
        Model model = new ModelManager();
        CourseClass courseClass = createCourseClass(MODULE);
        courseClass.addStudent(createStudent(List.of()));
        model.addCourseClass(courseClass);
        DeleteNoteCommand command = new DeleteNoteCommand(
                TELEHANDLE, CLASS_GROUP, MODULE, Index.fromOneBased(1));

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(String.format(DeleteNoteCommand.MESSAGE_INVALID_NOTE_INDEX, 1, TELEHANDLE, 0),
                exception.getMessage());
    }

    @Test
    public void execute_missingClass_throwsCommandException() {
        Model model = new ModelManager();
        DeleteNoteCommand command = new DeleteNoteCommand(
                TELEHANDLE, CLASS_GROUP, MODULE, Index.fromOneBased(1));

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(String.format(DeleteNoteCommand.MESSAGE_CLASS_NOT_FOUND, CLASS_GROUP, MODULE),
                exception.getMessage());
        assertTrue(model.getFilteredCourseClassList().isEmpty());
    }

    @Test
    public void execute_missingStudent_throwsCommandExceptionWithoutChangingClass() {
        Model model = new ModelManager();
        CourseClass courseClass = createCourseClass(MODULE);
        model.addCourseClass(courseClass);
        DeleteNoteCommand command = new DeleteNoteCommand(
                TELEHANDLE, CLASS_GROUP, MODULE, Index.fromOneBased(1));

        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));

        assertEquals(String.format(DeleteNoteCommand.MESSAGE_STUDENT_NOT_FOUND,
                TELEHANDLE, CLASS_GROUP, MODULE), exception.getMessage());
        assertTrue(courseClass.getStudentList().isEmpty());
    }

    @Test
    public void equals_comparesEveryField() {
        DeleteNoteCommand command = new DeleteNoteCommand(
                TELEHANDLE, CLASS_GROUP, MODULE, Index.fromOneBased(1));

        assertTrue(command.equals(command));
        assertEquals(command, new DeleteNoteCommand(
                TELEHANDLE, CLASS_GROUP, MODULE, Index.fromOneBased(1)));
        assertFalse(command.equals(null));
        assertFalse(command.equals("command"));
        assertFalse(command.equals(new DeleteNoteCommand(
                new Telehandle("janedoe"), CLASS_GROUP, MODULE, Index.fromOneBased(1))));
        assertFalse(command.equals(new DeleteNoteCommand(
                TELEHANDLE, new ClassGroup("T06"), MODULE, Index.fromOneBased(1))));
        assertFalse(command.equals(new DeleteNoteCommand(
                TELEHANDLE, CLASS_GROUP, new CourseCode("CS2101"), Index.fromOneBased(1))));
        assertFalse(command.equals(new DeleteNoteCommand(
                TELEHANDLE, CLASS_GROUP, MODULE, Index.fromOneBased(2))));
    }

    @Test
    public void toStringMethod_returnsExpectedRepresentation() {
        DeleteNoteCommand command = new DeleteNoteCommand(
                TELEHANDLE, CLASS_GROUP, MODULE, Index.fromOneBased(2));
        String expected = DeleteNoteCommand.class.getCanonicalName()
                + "{telehandle=" + TELEHANDLE
                + ", classGroup=" + CLASS_GROUP
                + ", moduleName=" + MODULE
                + ", noteIndex=" + Index.fromOneBased(2) + "}";

        assertEquals(expected, command.toString());
    }

    private CourseClass createCourseClass(CourseCode courseCode) {
        return new CourseClass(new Name(CLASS_GROUP.value), courseCode, Set.of());
    }

    private Student createStudent(List<Note> notes) {
        return new Student(new StudentName("John Doe"), TELEHANDLE, notes);
    }
}
