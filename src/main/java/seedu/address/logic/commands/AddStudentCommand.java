package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEHANDLE;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.student.Student;

/** Adds a student to an existing class group. */
public class AddStudentCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a student to a class group. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_TELEHANDLE + "TELEHANDLE "
            + PREFIX_CLASS_GROUP + "CLASS_GROUP\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_TELEHANDLE + "birdman "
            + PREFIX_CLASS_GROUP + "T05";

    public static final String MESSAGE_SUCCESS = "%1$s has been successfully added to %2$s";
    public static final String MESSAGE_CLASS_NOT_FOUND = "Class group not found: %1$s";
    public static final String MESSAGE_DUPLICATE_STUDENT = "Student is already enrolled in %1$s";

    private final Student toAdd;
    private final ClassGroup classGroup;

    /**
     * Creates an {@code AddStudentCommand}.
     *
     * @param student student to add
     * @param classGroup class group to add the student to
     */
    public AddStudentCommand(Student student, ClassGroup classGroup) {
        requireNonNull(student);
        requireNonNull(classGroup);
        toAdd = student;
        this.classGroup = classGroup;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        CourseClass targetClass = model.findCourseClassByGroup(classGroup)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_CLASS_NOT_FOUND, classGroup)));

        if (targetClass.hasStudent(toAdd)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_STUDENT, classGroup));
        }

        targetClass.addStudent(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd.getName(), classGroup));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddStudentCommand otherAddStudentCommand)) {
            return false;
        }

        return toAdd.equals(otherAddStudentCommand.toAdd)
                && classGroup.equals(otherAddStudentCommand.classGroup);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .add("classGroup", classGroup)
                .toString();
    }
}
