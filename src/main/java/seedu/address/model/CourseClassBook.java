package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.UniqueCourseClassList;

/**
 * Wraps all data at the course-class-book level.
 * Duplicates are not allowed (by .isSameCourseClass comparison).
 */
public class CourseClassBook implements ReadOnlyCourseClassBook {

    private final UniqueCourseClassList courseClasses = new UniqueCourseClassList();

    public CourseClassBook() {}

    /**
     * Creates a CourseClassBook using the course classes in the {@code toBeCopied}
     */
    public CourseClassBook(ReadOnlyCourseClassBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the course class list with {@code courseClasses}.
     * {@code courseClasses} must not contain duplicate course classes.
     */
    public void setCourseClasses(List<CourseClass> courseClasses) {
        this.courseClasses.setCourseClasses(courseClasses);
    }

    /**
     * Resets the existing data of this {@code CourseClassBook} with {@code newData}.
     */
    public void resetData(ReadOnlyCourseClassBook newData) {
        requireNonNull(newData);

        setCourseClasses(newData.getCourseClassList());
    }

    //// course class-level operations

    /**
     * Returns true if a course class with the same identity as {@code courseClass} exists in the course class book.
     */
    public boolean hasCourseClass(CourseClass courseClass) {
        requireNonNull(courseClass);
        return courseClasses.contains(courseClass);
    }

    /**
     * Returns the first course class whose name represents {@code classGroup}.
     * This lookup is retained for callers that do not need module disambiguation.
     *
     * @param classGroup class group to find
     * @return matching course class, or empty if no such class exists
     */
    public Optional<CourseClass> findCourseClassByGroup(ClassGroup classGroup) {
        requireNonNull(classGroup);
        return courseClasses.asUnmodifiableObservableList().stream()
                .filter(courseClass -> courseClass.getName().fullName.equals(classGroup.value))
                .findFirst();
    }

    /**
     * Returns the course class matching both a class group and module.
     *
     * @param classGroup class group to find
     * @param moduleName module containing the class group
     * @return matching course class, or empty if no such class exists
     */
    public Optional<CourseClass> findCourseClassByGroup(ClassGroup classGroup, CourseCode moduleName) {
        requireNonNull(classGroup);
        requireNonNull(moduleName);
        return courseClasses.asUnmodifiableObservableList().stream()
                .filter(courseClass -> courseClass.getName().fullName.equals(classGroup.value)
                        && courseClass.getCourseCode().equals(moduleName))
                .findFirst();
    }

    /**
     * Adds a course class to the course class book.
     * The course class must not already exist in the course class book.
     */
    public void addCourseClass(CourseClass courseClass) {
        courseClasses.add(courseClass);
    }

    /**
     * Replaces the given course class {@code target} in the list with {@code editedCourseClass}.
     * {@code target} must exist in the course class book.
     * The course class identity of {@code editedCourseClass} must not be the same as another existing course class in
     * the
     * course class book.
     */
    public void setCourseClass(CourseClass target, CourseClass editedCourseClass) {
        requireNonNull(editedCourseClass);

        courseClasses.setCourseClass(target, editedCourseClass);
    }

    /**
     * Removes {@code key} from this {@code CourseClassBook}.
     * {@code key} must exist in the course class book.
     */
    public void removeCourseClass(CourseClass key) {
        courseClasses.remove(key);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("courseClasses", courseClasses)
                .toString();
    }

    @Override
    public ObservableList<CourseClass> getCourseClassList() {
        return courseClasses.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CourseClassBook otherCourseClassBook)) {
            return false;
        }

        return courseClasses.equals(otherCourseClassBook.courseClasses);
    }

    @Override
    public int hashCode() {
        return courseClasses.hashCode();
    }
}
