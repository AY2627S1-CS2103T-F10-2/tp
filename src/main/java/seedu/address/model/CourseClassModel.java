package seedu.address.model;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.courseClass.CourseClass;

/**
 * The API of the course class Model component.
 */
public interface CourseClassModel {
    /** {@code Predicate} that always evaluates to true */
    Predicate<CourseClass> PREDICATE_SHOW_ALL_COURSE_CLASSES = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces course class book data with the data in {@code courseClassBook}.
     */
    void setCourseClassBook(ReadOnlyCourseClassBook courseClassBook);

    /** Returns the CourseClassBook */
    ReadOnlyCourseClassBook getCourseClassBook();

    /**
     * Returns true if a course class with the same identity as {@code courseClass} exists in the course class book.
     */
    boolean hasCourseClass(CourseClass courseClass);

    /**
     * Deletes the given course class.
     * The course class must exist in the course class book.
     */
    void deleteCourseClass(CourseClass target);

    /**
     * Adds the given course class.
     * {@code courseClass} must not already exist in the course class book.
     */
    void addCourseClass(CourseClass courseClass);

    /**
     * Replaces the given course class {@code target} with {@code editedCourseClass}.
     * {@code target} must exist in the course class book.
     * The course class identity of {@code editedCourseClass} must not be the same as another existing course class in
     * the
     * course class book.
     */
    void setCourseClass(CourseClass target, CourseClass editedCourseClass);

    /** Returns an unmodifiable view of the filtered courseClass list */
    ObservableList<CourseClass> getFilteredCourseClassList();

    /**
     * Updates the filter of the filtered course class list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredCourseClassList(Predicate<CourseClass> predicate);
}
