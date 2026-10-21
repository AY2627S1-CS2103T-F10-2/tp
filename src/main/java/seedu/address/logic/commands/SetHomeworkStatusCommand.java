package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_COURSE_CLASSES;

import java.util.Objects;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.CliSyntax;
import seedu.address.model.Model;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.HomeworkStatus;

/** Sets the homework status shared by all students in a course class. */
public class SetHomeworkStatusCommand extends Command {

    public static final String COMMAND_WORD = "hwork";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sets the homework status for a course class.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + CliSyntax.PREFIX_HOMEWORK_STATUS + "STATUS\n"
            + "Example: " + COMMAND_WORD + " 1 " + CliSyntax.PREFIX_HOMEWORK_STATUS + "completed";

    public static final String MESSAGE_SUCCESS = "Set homework status for %1$s to %2$s.";

    private static final Logger logger = LogsCenter.getLogger(SetHomeworkStatusCommand.class);

    private final Index index;
    private final HomeworkStatus homeworkStatus;

    /** Creates a command to set a class's homework status. */
    public SetHomeworkStatusCommand(Index index, HomeworkStatus homeworkStatus) {
        this.index = requireNonNull(index);
        this.homeworkStatus = requireNonNull(homeworkStatus);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (index.getZeroBased() >= model.getFilteredCourseClassList().size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_COURSE_CLASS_DISPLAYED_INDEX);
        }

        CourseClass targetClass = model.getFilteredCourseClassList().get(index.getZeroBased());
        HomeworkStatus previousStatus = targetClass.getHomeworkStatus();
        targetClass.setHomeworkStatus(homeworkStatus);
        model.updateFilteredCourseClassList(PREDICATE_SHOW_ALL_COURSE_CLASSES);

        logger.info(String.format("Homework status updated: action=set, classGroup=%s, courseCode=%s, "
                        + "previousStatus=%s, newStatus=%s",
                targetClass.getName().fullName, targetClass.getCourseCode().value,
                previousStatus.getSerializedValue(), homeworkStatus.getSerializedValue()));

        return new CommandResult(String.format(MESSAGE_SUCCESS,
                targetClass.getName().fullName, homeworkStatus.getSerializedValue()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SetHomeworkStatusCommand otherCommand)) {
            return false;
        }
        return index.equals(otherCommand.index) && homeworkStatus.equals(otherCommand.homeworkStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, homeworkStatus);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("homeworkStatus", homeworkStatus)
                .toString();
    }
}
