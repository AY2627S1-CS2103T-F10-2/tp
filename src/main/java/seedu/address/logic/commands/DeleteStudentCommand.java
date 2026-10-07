package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEHANDLE;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.student.Student;
import seedu.address.model.student.Telehandle;

/** Removes a student from a specified class group. */
public class DeleteStudentCommand extends Command {

    public static final String COMMAND_WORD = "dstudent";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Removes a student from a class group in a module. "
            + "Parameters: "
            + PREFIX_TELEHANDLE + "TELEHANDLE "
            + PREFIX_CLASS_GROUP + "CLASS_GROUP "
            + PREFIX_MODULE_NAME + "MODULE_NAME\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_TELEHANDLE + "birdman "
            + PREFIX_CLASS_GROUP + "T05 "
            + PREFIX_MODULE_NAME + "CS2103T";

    public static final String MESSAGE_SUCCESS = "Student @%1$s has been removed from %2$s in %3$s";
    public static final String MESSAGE_CONFIRMATION =
            "Remove %1$s (@%2$s) from %3$s in %4$s? Type yes to confirm or no to cancel.";
    public static final String MESSAGE_CANCELLED = "Student deletion cancelled. No records were changed.";
    public static final String MESSAGE_EXPECTED_CONFIRMATION = "Please type yes to confirm or no to cancel.";
    public static final String MESSAGE_CLASS_NOT_FOUND = "Class group not found: %1$s in %2$s";
    public static final String MESSAGE_STUDENT_NOT_FOUND =
            "Student @%1$s is not enrolled in %2$s in %3$s";

    private final Telehandle telehandle;
    private final ClassGroup classGroup;
    private final CourseCode moduleName;
    private final boolean confirmed;

    /** Creates a command to remove a student from a class group. */
    public DeleteStudentCommand(Telehandle telehandle, ClassGroup classGroup, CourseCode moduleName) {
        this(telehandle, classGroup, moduleName, false);
    }

    private DeleteStudentCommand(Telehandle telehandle, ClassGroup classGroup, CourseCode moduleName,
            boolean confirmed) {
        requireNonNull(telehandle);
        requireNonNull(classGroup);
        requireNonNull(moduleName);
        this.telehandle = telehandle;
        this.classGroup = classGroup;
        this.moduleName = moduleName;
        this.confirmed = confirmed;
    }

    /** Returns a copy of this command that will perform the deletion. */
    public DeleteStudentCommand confirm() {
        return new DeleteStudentCommand(telehandle, classGroup, moduleName, true);
    }

    /** Returns whether the deletion has been confirmed. */
    public boolean isConfirmed() {
        return confirmed;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        CourseClass targetClass = model.findCourseClassByGroup(classGroup, moduleName)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_CLASS_NOT_FOUND,
                        classGroup, moduleName)));

        if (!targetClass.hasStudent(telehandle)) {
            throw new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND,
                    telehandle, classGroup, moduleName));
        }

        Student studentToDelete = targetClass.getStudentList().stream()
                .filter(student -> student.getTelehandle().equals(telehandle))
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND,
                        telehandle, classGroup, moduleName)));

        if (!confirmed) {
            return new CommandResult(String.format(MESSAGE_CONFIRMATION,
                    studentToDelete.getName(), telehandle, classGroup, moduleName));
        }

        targetClass.removeStudent(telehandle);
        return new CommandResult(String.format(MESSAGE_SUCCESS, telehandle, classGroup, moduleName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof DeleteStudentCommand otherDeleteStudentCommand)) {
            return false;
        }

        return telehandle.equals(otherDeleteStudentCommand.telehandle)
                && classGroup.equals(otherDeleteStudentCommand.classGroup)
                && moduleName.equals(otherDeleteStudentCommand.moduleName)
                && confirmed == otherDeleteStudentCommand.confirmed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("telehandle", telehandle)
                .add("classGroup", classGroup)
                .add("moduleName", moduleName)
                .add("confirmed", confirmed)
                .toString();
    }
}
