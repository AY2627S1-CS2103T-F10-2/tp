package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEHANDLE;

import java.time.Clock;
import java.time.LocalDateTime;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.student.Note;
import seedu.address.model.student.Student;
import seedu.address.model.student.Telehandle;

/** Adds a timestamped note to a student enrolled in a specified class. */
public class AddNoteCommand extends Command {

    public static final String COMMAND_WORD = "note";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a note to a student enrolled in a class. "
            + "Parameters: "
            + PREFIX_TELEHANDLE + "TELEHANDLE "
            + PREFIX_CLASS_GROUP + "CLASS_GROUP "
            + PREFIX_MODULE_NAME + "MODULE_NAME "
            + PREFIX_NOTE + "OBSERVATION\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_TELEHANDLE + "birdman "
            + PREFIX_CLASS_GROUP + "T05 "
            + PREFIX_MODULE_NAME + "CS2103T "
            + PREFIX_NOTE + "Needs follow-up";

    public static final String MESSAGE_SUCCESS = "Note added to %1$s (@%2$s) in %3$s in %4$s";
    public static final String MESSAGE_CLASS_NOT_FOUND = "Class group not found: %1$s in %2$s";
    public static final String MESSAGE_STUDENT_NOT_FOUND =
            "Student @%1$s is not enrolled in %2$s in %3$s";

    private final Telehandle telehandle;
    private final ClassGroup classGroup;
    private final CourseCode moduleName;
    private final String noteText;
    private final Clock clock;

    /** Creates a command that adds a note using the current system time. */
    public AddNoteCommand(Telehandle telehandle, ClassGroup classGroup, CourseCode moduleName, String noteText) {
        this(telehandle, classGroup, moduleName, noteText, Clock.systemDefaultZone());
    }

    /** Creates a command using {@code clock}; exposed within the package for deterministic tests. */
    AddNoteCommand(Telehandle telehandle, ClassGroup classGroup, CourseCode moduleName, String noteText,
            Clock clock) {
        requireNonNull(telehandle);
        requireNonNull(classGroup);
        requireNonNull(moduleName);
        requireNonNull(noteText);
        requireNonNull(clock);
        checkArgument(Note.isValidText(noteText), Note.MESSAGE_CONSTRAINTS);
        this.telehandle = telehandle;
        this.classGroup = classGroup;
        this.moduleName = moduleName;
        this.noteText = noteText;
        this.clock = clock;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        CourseClass targetClass = model.findCourseClassByGroup(classGroup, moduleName)
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_CLASS_NOT_FOUND,
                        classGroup, moduleName)));

        Student targetStudent = targetClass.getStudentList().stream()
                .filter(student -> student.getTelehandle().equals(telehandle))
                .findFirst()
                .orElseThrow(() -> new CommandException(String.format(MESSAGE_STUDENT_NOT_FOUND,
                        telehandle, classGroup, moduleName)));

        Note note = new Note(noteText, LocalDateTime.now(clock));
        targetClass.setStudent(targetStudent, targetStudent.withNote(note));

        return new CommandResult(String.format(MESSAGE_SUCCESS,
                targetStudent.getName(), telehandle, classGroup, moduleName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AddNoteCommand otherAddNoteCommand)) {
            return false;
        }

        return telehandle.equals(otherAddNoteCommand.telehandle)
                && classGroup.equals(otherAddNoteCommand.classGroup)
                && moduleName.equals(otherAddNoteCommand.moduleName)
                && noteText.equals(otherAddNoteCommand.noteText);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("telehandle", telehandle)
                .add("classGroup", classGroup)
                .add("moduleName", moduleName)
                .add("noteText", noteText)
                .toString();
    }
}
