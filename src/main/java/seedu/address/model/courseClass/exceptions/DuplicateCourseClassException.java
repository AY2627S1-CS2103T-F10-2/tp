package seedu.address.model.courseClass.exceptions;

/**
 * Signals that the operation will result in duplicate course classes with the same identity.
 */
public class DuplicateCourseClassException extends RuntimeException {
    public DuplicateCourseClassException() {
        super("Operation would result in duplicate course class");
    }
}
