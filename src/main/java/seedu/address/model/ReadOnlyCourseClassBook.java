package seedu.address.model;

import javafx.collections.ObservableList;
import seedu.address.model.courseclass.CourseClass;

/**
 * Unmodifiable view of an course class book
 */
public interface ReadOnlyCourseClassBook {

    /**
     * Returns an unmodifiable view of the course classes list.
     * This list will not contain any duplicate course classes.
     */
    ObservableList<CourseClass> getCourseClassList();

}
