package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.HomeworkStatus;
import seedu.address.model.courseclass.Name;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;

public class SetHomeworkStatusCommandTest {
    private final Model model = new ModelManager();
    private final CourseClass courseClass = new CourseClass(new Name("F10-2"),
            new CourseCode("CS2103T"), Set.of());

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SetHomeworkStatusCommand(null, HomeworkStatus.PENDING));
        assertThrows(NullPointerException.class, () -> new SetHomeworkStatusCommand(Index.fromOneBased(1), null));
    }

    @Test
    public void execute_validIndex_updatesClassStatus() throws Exception {
        model.addCourseClass(courseClass);

        CommandResult result = new SetHomeworkStatusCommand(Index.fromOneBased(1), HomeworkStatus.COMPLETED)
                .execute(model);

        assertEquals(HomeworkStatus.COMPLETED, courseClass.getHomeworkStatus());
        assertEquals(String.format(SetHomeworkStatusCommand.MESSAGE_SUCCESS, "F10-2", "completed"),
                result.getFeedbackToUser());
    }

    @Test
    public void execute_individualStatus_doesNotChangeOtherStudents() throws Exception {
        Student alice = new Student(new StudentName("Alice Tan"), new Telehandle("alice_tan"));
        Student benson = new Student(new StudentName("Benson Meier"), new Telehandle("benson"));
        courseClass.addStudent(alice);
        courseClass.addStudent(benson);
        model.addCourseClass(courseClass);
        new SetHomeworkStatusCommand(Index.fromOneBased(1), HomeworkStatus.PENDING).execute(model);

        CommandResult result = new SetHomeworkStatusCommand(Index.fromOneBased(1),
                alice.getTelehandle(), HomeworkStatus.COMPLETED).execute(model);

        assertEquals(HomeworkStatus.COMPLETED, courseClass.getHomeworkStatus(alice));
        assertEquals(HomeworkStatus.PENDING, courseClass.getHomeworkStatus(benson));
        assertEquals(String.format(SetHomeworkStatusCommand.MESSAGE_STUDENT_SUCCESS,
                "alice_tan", "F10-2", "completed"), result.getFeedbackToUser());
    }

    @Test
    public void execute_inheritStatus_clearsIndividualOverride() throws Exception {
        Student alice = new Student(new StudentName("Alice Tan"), new Telehandle("alice_tan"));
        courseClass.addStudent(alice);
        courseClass.setHomeworkStatus(HomeworkStatus.PENDING);
        courseClass.setStudentHomeworkStatus(alice.getTelehandle(), HomeworkStatus.COMPLETED);
        model.addCourseClass(courseClass);

        new SetHomeworkStatusCommand(Index.fromOneBased(1), alice.getTelehandle(), HomeworkStatus.PENDING)
                .execute(model);
        CommandResult result = SetHomeworkStatusCommand.clearStudentOverride(Index.fromOneBased(1),
                alice.getTelehandle()).execute(model);

        assertEquals(HomeworkStatus.PENDING, courseClass.getHomeworkStatus(alice));
        assertTrue(courseClass.getStudentHomeworkStatuses().isEmpty());
        assertEquals(String.format(SetHomeworkStatusCommand.MESSAGE_STUDENT_SUCCESS,
                "alice_tan", "F10-2", "pending"), result.getFeedbackToUser());
    }

    @Test
    public void execute_individualStatusForUnknownStudent_throwsCommandException() throws Exception {
        model.addCourseClass(courseClass);

        CommandException exception = assertThrows(CommandException.class, () ->
                new SetHomeworkStatusCommand(Index.fromOneBased(1), new Telehandle("alice_tan"),
                        HomeworkStatus.COMPLETED).execute(model));

        assertEquals(SetHomeworkStatusCommand.MESSAGE_STUDENT_NOT_IN_CLASS, exception.getMessage());
    }

    @Test
    public void execute_validIndex_preservesCourseClassFilter() throws Exception {
        CourseClass otherClass = new CourseClass(new Name("F10-3"), new CourseCode("CS2103T"), Set.of());
        model.addCourseClass(courseClass);
        model.addCourseClass(otherClass);
        model.updateFilteredCourseClassList(candidate -> candidate.isSameCourseClass(courseClass));

        new SetHomeworkStatusCommand(Index.fromOneBased(1), HomeworkStatus.COMPLETED).execute(model);

        assertEquals(1, model.getFilteredCourseClassList().size());
        assertEquals(courseClass, model.getFilteredCourseClassList().getFirst());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        model.addCourseClass(courseClass);

        CommandException exception = assertThrows(CommandException.class, () ->
                new SetHomeworkStatusCommand(Index.fromOneBased(2), HomeworkStatus.COMPLETED).execute(model));

        assertEquals("The course class index provided is invalid.", exception.getMessage());
        assertEquals(HomeworkStatus.NOT_SET, courseClass.getHomeworkStatus());
    }

    @Test
    public void equals_comparesIndexAndStatus() {
        SetHomeworkStatusCommand command = new SetHomeworkStatusCommand(
                Index.fromOneBased(1), HomeworkStatus.PENDING);

        assertTrue(command.equals(command));
        assertEquals(command, new SetHomeworkStatusCommand(Index.fromOneBased(1), HomeworkStatus.PENDING));
        assertEquals(command.hashCode(), new SetHomeworkStatusCommand(
                Index.fromOneBased(1), HomeworkStatus.PENDING).hashCode());
        assertFalse(command.equals(null));
        assertFalse(command.equals("command"));
        assertFalse(command.equals(new SetHomeworkStatusCommand(Index.fromOneBased(2), HomeworkStatus.PENDING)));
        assertFalse(command.equals(new SetHomeworkStatusCommand(Index.fromOneBased(1), HomeworkStatus.COMPLETED)));
    }

    @Test
    public void toStringMethod_returnsExpectedRepresentation() {
        SetHomeworkStatusCommand command = new SetHomeworkStatusCommand(
                Index.fromOneBased(1), HomeworkStatus.PENDING);

        assertEquals(SetHomeworkStatusCommand.class.getCanonicalName()
                + "{index=" + Index.fromOneBased(1) + ", homeworkStatus=PENDING}", command.toString());
    }
}
