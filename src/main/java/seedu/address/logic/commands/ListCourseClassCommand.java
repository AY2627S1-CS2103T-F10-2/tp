package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_COURSE_CLASSES;

import seedu.address.model.Model;

/**
 * Lists all course classes in the course class book to the user.
 */
public class ListCourseClassCommand extends Command {

    public static final String COMMAND_WORD = "lclass";

    public static final String MESSAGE_SUCCESS = "Listed all course classes.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredCourseClassList(PREDICATE_SHOW_ALL_COURSE_CLASSES);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
