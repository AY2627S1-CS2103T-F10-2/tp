package seedu.address.model.student.exceptions;

/**
 * Signals that an operation would create a duplicate student identity.
 */
public class DuplicateStudentException extends RuntimeException {
    /** Creates a {@code DuplicateStudentException}. */
    public DuplicateStudentException() {
        super("Operation would result in duplicate students");
    }
}
