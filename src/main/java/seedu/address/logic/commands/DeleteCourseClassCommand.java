package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.courseClass.CourseClass;

/**
 * Deletes a course class identified using its displayed index from the course class book.
 */
public class DeleteCourseClassCommand extends Command {

    public static final String COMMAND_WORD = "dclass";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the course class identified by the index number used in the displayed course class list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_COURSE_CLASS_SUCCESS = "Deleted course class: %1$s";

    private final Index targetIndex;

    public DeleteCourseClassCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<CourseClass> lastShownList = model.getFilteredCourseClassList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_COURSE_CLASS_DISPLAYED_INDEX);
        }

        CourseClass courseClassToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deleteCourseClass(courseClassToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_COURSE_CLASS_SUCCESS,
                Messages.format(courseClassToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCourseClassCommand otherDeleteCourseClassCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteCourseClassCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
