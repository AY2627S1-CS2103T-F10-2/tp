package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.StringJoiner;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentNameContainsKeywordsPredicate;

/** Finds enrolled students by name across all course classes. */
public class FindStudentCommand extends Command {

    public static final String COMMAND_WORD = "fstudent";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds enrolled students whose names contain any specified keyword (case-insensitive).\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " Alex Bernice";

    public static final String MESSAGE_NO_MATCHES = "No matching student enrolments found.";
    public static final String MESSAGE_MATCHES_FOUND = "%1$d matching student enrolment(s) found:%n%2$s";

    private final StudentNameContainsKeywordsPredicate predicate;

    /**
     * Creates a command that finds students matching the specified predicate.
     */
    public FindStudentCommand(StudentNameContainsKeywordsPredicate predicate) {
        this.predicate = requireNonNull(predicate);
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        int matchCount = 0;
        StringJoiner matches = new StringJoiner(System.lineSeparator());
        for (CourseClass courseClass : model.getCourseClassBook().getCourseClassList()) {
            for (Student student : courseClass.getStudentList()) {
                if (predicate.test(student)) {
                    matchCount++;
                    matches.add(String.format("%d. %s (@%s) — %s, %s", matchCount,
                            student.getName(), student.getTelehandle(),
                            courseClass.getName(), courseClass.getCourseCode()));
                }
            }
        }

        if (matchCount == 0) {
            return new CommandResult(MESSAGE_NO_MATCHES);
        }
        return new CommandResult(String.format(MESSAGE_MATCHES_FOUND, matchCount, matches));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof FindStudentCommand otherCommand)) {
            return false;
        }
        return predicate.equals(otherCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("predicate", predicate).toString();
    }
}
