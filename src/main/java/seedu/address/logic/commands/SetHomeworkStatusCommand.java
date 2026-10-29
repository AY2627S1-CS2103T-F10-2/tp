package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
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
import seedu.address.model.student.Telehandle;

/** Sets the homework status for a course class or an individual enrolled student. */
public class SetHomeworkStatusCommand extends Command {

    public static final String COMMAND_WORD = "hwork";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sets the homework status for a course class.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + CliSyntax.PREFIX_TELEHANDLE + "TELEHANDLE] "
            + CliSyntax.PREFIX_HOMEWORK_STATUS + "STATUS\n"
            + "Example: " + COMMAND_WORD + " 1 " + CliSyntax.PREFIX_HOMEWORK_STATUS + "completed";

    public static final String MESSAGE_SUCCESS = "Set homework status for %1$s to %2$s.";
    public static final String MESSAGE_STUDENT_SUCCESS = "Set homework status for %1$s in %2$s to %3$s.";
    public static final String MESSAGE_STUDENT_NOT_IN_CLASS = "The specified student is not enrolled in this class.";
    public static final String INHERIT_STATUS = "inherit";

    private static final Logger logger = LogsCenter.getLogger(SetHomeworkStatusCommand.class);

    private final Index index;
    private final HomeworkStatus homeworkStatus;
    private final Optional<Telehandle> studentTelehandle;
    private final boolean clearStudentOverride;

    /** Creates a command to set a class's homework status. */
    public SetHomeworkStatusCommand(Index index, HomeworkStatus homeworkStatus) {
        this.index = requireNonNull(index);
        this.homeworkStatus = requireNonNull(homeworkStatus);
        this.studentTelehandle = Optional.empty();
        this.clearStudentOverride = false;
    }

    /** Creates a command to set an individual student's homework status. */
    public SetHomeworkStatusCommand(Index index, Telehandle studentTelehandle, HomeworkStatus homeworkStatus) {
        this.index = requireNonNull(index);
        this.studentTelehandle = Optional.of(requireNonNull(studentTelehandle));
        this.homeworkStatus = requireNonNull(homeworkStatus);
        this.clearStudentOverride = false;
    }

    private SetHomeworkStatusCommand(Index index, Telehandle studentTelehandle,
            HomeworkStatus homeworkStatus, boolean clearStudentOverride) {
        this.index = requireNonNull(index);
        this.studentTelehandle = Optional.of(requireNonNull(studentTelehandle));
        this.homeworkStatus = homeworkStatus;
        this.clearStudentOverride = clearStudentOverride;
    }

    /** Creates a command to clear an individual student's override. */
    public static SetHomeworkStatusCommand clearStudentOverride(Index index, Telehandle studentTelehandle) {
        requireNonNull(index);
        requireNonNull(studentTelehandle);
        return new SetHomeworkStatusCommand(index, studentTelehandle, null, true);
    }

    @Override
    public boolean isMutating() {
        return true;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (index.getZeroBased() >= model.getFilteredCourseClassList().size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_COURSE_CLASS_DISPLAYED_INDEX);
        }

        CourseClass targetClass = model.getFilteredCourseClassList().get(index.getZeroBased());
        if (studentTelehandle.isEmpty()) {
            HomeworkStatus previousStatus = targetClass.getHomeworkStatus();
            targetClass.setHomeworkStatus(homeworkStatus);

            logger.info(String.format("Homework status updated: scope=class, classGroup=%s, courseCode=%s, "
                            + "previousStatus=%s, newStatus=%s",
                    targetClass.getName().fullName, targetClass.getCourseCode().value,
                    previousStatus.getSerializedValue(), homeworkStatus.getSerializedValue()));

            return new CommandResult(String.format(MESSAGE_SUCCESS,
                    targetClass.getName().fullName, homeworkStatus.getSerializedValue()));
        }

        Telehandle targetStudent = studentTelehandle.get();
        if (!targetClass.hasStudent(targetStudent)) {
            throw new CommandException(MESSAGE_STUDENT_NOT_IN_CLASS);
        }
        HomeworkStatus previousStatus = targetClass.getHomeworkStatus(targetStudent);
        if (clearStudentOverride) {
            targetClass.clearStudentHomeworkStatus(targetStudent);
        } else {
            targetClass.setStudentHomeworkStatus(targetStudent, homeworkStatus);
        }
        String newStatus = clearStudentOverride
                ? targetClass.getHomeworkStatus().getSerializedValue() : homeworkStatus.getSerializedValue();
        logger.info(String.format("Homework status updated: scope=student, classGroup=%s, courseCode=%s, "
                        + "previousStatus=%s, newStatus=%s",
                targetClass.getName().fullName, targetClass.getCourseCode().value,
                previousStatus.getSerializedValue(), newStatus));
        return new CommandResult(String.format(MESSAGE_STUDENT_SUCCESS, targetStudent.value,
                targetClass.getName().fullName, newStatus));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SetHomeworkStatusCommand otherCommand)) {
            return false;
        }
        return index.equals(otherCommand.index) && Objects.equals(homeworkStatus, otherCommand.homeworkStatus)
                && studentTelehandle.equals(otherCommand.studentTelehandle)
                && clearStudentOverride == otherCommand.clearStudentOverride;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, homeworkStatus, studentTelehandle, clearStudentOverride);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this)
                .add("index", index)
                .add("homeworkStatus", homeworkStatus);
        if (studentTelehandle.isPresent()) {
            builder.add("studentTelehandle", studentTelehandle);
            builder.add("clearStudentOverride", clearStudentOverride);
        }
        return builder.toString();
    }
}
