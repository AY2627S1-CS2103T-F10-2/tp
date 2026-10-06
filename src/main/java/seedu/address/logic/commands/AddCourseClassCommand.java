package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.CourseClassModel;
import seedu.address.model.Model;
import seedu.address.model.courseClass.CourseClass;

/**
 * Adds a class to the system.
 */
public class AddCourseClassCommand extends Command {

    public static final String COMMAND_WORD = "aclass";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a class to the system. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_CODE + "COURSE_CODE "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "F10-2 "
            + PREFIX_CODE + "CS2103T "
            + PREFIX_TAG + "tutorial";

    public static final String MESSAGE_SUCCESS = "New class added: %1$s";
    public static final String MESSAGE_DUPLICATE_CLASS = "This class already exists in the system.";

    private final CourseClass toAdd;

    /**
     * Creates an AddCourseClassCommand to add the specified {@code CourseClass}.
     */
    public AddCourseClassCommand(CourseClass courseClass) {
        requireNonNull(courseClass);
        toAdd = courseClass;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        return execute(model.getCourseClassModel());
    }

    /** Executes the command using the course class model. */
    public CommandResult execute(CourseClassModel model) throws CommandException {
        requireNonNull(model);

        if (model.hasCourseClass(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_CLASS);
        }

        model.addCourseClass(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCourseClassCommand otherAddClassCommand)) {
            return false;
        }

        return toAdd.equals(otherAddClassCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
