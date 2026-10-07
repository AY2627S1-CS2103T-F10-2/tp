package seedu.address.model.student.exceptions;

/** Signals that a requested student does not exist in a student list. */
public class StudentNotFoundException extends RuntimeException {
    /** Creates a {@code StudentNotFoundException}. */
    public StudentNotFoundException() {
        super("The student could not be found.");
    }
}
