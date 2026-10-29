package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEHANDLE;

import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.student.Student;
import seedu.address.model.student.Telehandle;

/** Deletes a numbered note from a student enrolled in a specified class. */
public class DeleteNoteCommand extends Command {

    public static final String COMMAND_WORD = "dnote";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a numbered note from a student enrolled in a class. "
            + "Parameters: "
            + PREFIX_TELEHANDLE + "TELEHANDLE "
            + PREFIX_CLASS_GROUP + "CLASS_GROUP "
            + PREFIX_MODULE_NAME + "MODULE_NAME "
            + PREFIX_INDEX + "NOTE_INDEX\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_TELEHANDLE + "birdman "
            + PREFIX_CLASS_GROUP + "T05 "
            + PREFIX_MODULE_NAME + "CS2103T "
            + PREFIX_INDEX + "2";

    public static final String MESSAGE_SUCCESS = "Note %1$d deleted from %2$s (@%3$s) in %4$s in %5$s";
    public static final String MESSAGE_CLASS_NOT_FOUND = "Class group not found: %1$s in %2$s";
    public static final String MESSAGE_STUDENT_NOT_FOUND =
            "Student @%1$s is not enrolled in %2$s in %3$s";
    public static final String MESSAGE_INVALID_NOTE_INDEX =
            "Note index %1$d is invalid for @%2$s, who has %3$d note(s)";

    private static final Logger logger = LogsCenter.getLogger(DeleteNoteCommand.class);

    private final Telehandle telehandle;
    private final ClassGroup classGroup;
    private final CourseCode moduleName;
    private final Index noteIndex;

    /** Creates a command that deletes the note at {@code noteIndex}. */
    public DeleteNoteCommand(Telehandle telehandle, ClassGroup classGroup, CourseCode moduleName,
            Index noteIndex) {
        this.telehandle = requireNonNull(telehandle);
        this.classGroup = requireNonNull(classGroup);
        this.moduleName = requireNonNull(moduleName);
        this.noteIndex = requireNonNull(noteIndex);
    }

    @Override
    public boolean isMutating() {
        return true;
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

        if (noteIndex.getZeroBased() >= targetStudent.getNotes().size()) {
            throw new CommandException(String.format(MESSAGE_INVALID_NOTE_INDEX,
                    noteIndex.getOneBased(), telehandle, targetStudent.getNotes().size()));
        }

        Student updatedStudent = targetStudent.withoutNoteAt(noteIndex.getZeroBased());
        targetClass.setStudent(targetStudent, updatedStudent);

        logger.info(() -> String.format(
                "action=deleteStudentNote telehandle=%s classGroup=%s courseCode=%s "
                        + "noteIndex=%d remainingNoteCount=%d",
                telehandle, classGroup, moduleName, noteIndex.getOneBased(), updatedStudent.getNotes().size()));

        return new CommandResult(String.format(MESSAGE_SUCCESS, noteIndex.getOneBased(),
                targetStudent.getName(), telehandle, classGroup, moduleName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteNoteCommand otherDeleteNoteCommand)) {
            return false;
        }
        return telehandle.equals(otherDeleteNoteCommand.telehandle)
                && classGroup.equals(otherDeleteNoteCommand.classGroup)
                && moduleName.equals(otherDeleteNoteCommand.moduleName)
                && noteIndex.equals(otherDeleteNoteCommand.noteIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("telehandle", telehandle)
                .add("classGroup", classGroup)
                .add("moduleName", moduleName)
                .add("noteIndex", noteIndex)
                .toString();
    }
}
