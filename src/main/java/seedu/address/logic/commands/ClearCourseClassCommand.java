package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.CourseClassBook;
import seedu.address.model.Model;

/**
 * Clears the course class book.
 */
public class ClearCourseClassCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Course class book has been cleared!";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.setCourseClassBook(new CourseClassBook());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
