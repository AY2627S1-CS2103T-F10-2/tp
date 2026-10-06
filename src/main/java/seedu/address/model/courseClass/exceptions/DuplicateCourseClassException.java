package seedu.address.model.courseClass.exceptions;

/**
 * Signals that the operation will result in duplicate Course Class (Course Class are considered duplicates if they have the same
 * identity).
 */
public class DuplicateCourseClassException extends RuntimeException {
    public DuplicateCourseClassException() {
        super("Operation would result in duplicate course class");
    }
}
