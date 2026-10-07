package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEHANDLE;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.student.Student;

/** Adds a student to an existing class group in a specified module. */
public class AddStudentCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a student to a class group in a module. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_TELEHANDLE + "TELEHANDLE "
            + PREFIX_CLASS_GROUP + "CLASS_GROUP "
            + PREFIX_MODULE_NAME + "MODULE_NAME\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_TELEHANDLE + "birdman "
            + PREFIX_CLASS_GROUP + "T05 "
            + PREFIX_MODULE_NAME + "CS2103T";

    public static final String MESSAGE_SUCCESS = "%1$s has been successfully added to %2$s in %3$s";
    public static final String MESSAGE_CLASS_NOT_FOUND = "Class group not found: %1$s in %2$s";
    public static final String MESSAGE_DUPLICATE_STUDENT = "Student is already enrolled in %1$s in %2$s";

    private final Student toAdd;
    private final ClassGroup classGroup;
    private final CourseCode moduleName;

    /**
     * Creates an {@code AddStudentCommand}.
     *
     * @param student student to add
     * @param classGroup class group to add the student to
     * @param moduleName module containing the class group
     */
    public AddStudentCommand(Student student, ClassGroup classGroup, CourseCode moduleName) {
        requireNonNull(student);
        requireNonNull(classGroup);
        requireNonNull(moduleName);
        toAdd = student;
        this.classGroup = classGroup;
        this.moduleName = moduleName;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        CourseClass targetClass = model.findCourseClassByGroup(classGroup, moduleName)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_CLASS_NOT_FOUND,
                        classGroup, moduleName)));

        if (targetClass.hasStudent(toAdd)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_STUDENT, classGroup, moduleName));
        }

        targetClass.addStudent(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd.getName(), classGroup, moduleName));
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
                && classGroup.equals(otherAddStudentCommand.classGroup)
                && moduleName.equals(otherAddStudentCommand.moduleName);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .add("classGroup", classGroup)
                .add("moduleName", moduleName)
                .toString();
    }
}
