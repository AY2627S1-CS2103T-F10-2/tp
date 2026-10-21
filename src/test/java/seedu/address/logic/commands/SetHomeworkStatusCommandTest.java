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
