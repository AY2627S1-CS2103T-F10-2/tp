package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.CourseClassBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;

public class UndoCommandTest {

    @Test
    public void execute_withoutHistory_throwsCommandException() {
        ModelManager model = new ModelManager();

        CommandException exception = assertThrows(CommandException.class, () -> new UndoCommand().execute(model));

        assertEquals(UndoCommand.MESSAGE_NO_HISTORY, exception.getMessage());
    }

    @Test
    public void execute_withHistory_restoresMostRecentState() throws Exception {
        CourseClassBook original = new CourseClassBook();
        original.addCourseClass(new CourseClass(new Name("T05"), new CourseCode("CS2103T"), Set.of()));
        ModelManager model = new ModelManager(new CourseClassBook(), new UserPrefs());
        model.addUndoSnapshot(original);
        model.addCourseClass(new CourseClass(new Name("L01"), new CourseCode("CS2101"), Set.of()));

        CommandResult result = new UndoCommand().execute(model);

        assertEquals(UndoCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(original, model.getCourseClassBook());
        assertEquals(0, model.getUndoHistory().size());
    }
}
