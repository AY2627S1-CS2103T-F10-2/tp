package seedu.address.model.courseclass;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.courseclass.exceptions.CourseClassNotFoundException;
import seedu.address.model.courseclass.exceptions.DuplicateCourseClassException;

/**
 * A list of course classes that enforces uniqueness between its elements and does not allow nulls.
 * A course class is considered unique by comparing its class group and module using
 * {@code CourseClass#isSameCourseClass(CourseClass)}. As such,
 * adding and updating of
 * course classes uses CourseClass#isSameCourseClass(CourseClass) for equality so as to ensure that the course class
 * being added or updated is
 * unique in terms of identity in the UniqueCourseClassList. However, the removal of a course class uses
 * CourseClass#equals(Object) so
 * as to ensure that the course class with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see CourseClass#isSameCourseClass(CourseClass)
 */
public class UniqueCourseClassList implements Iterable<CourseClass> {

    private final ObservableList<CourseClass> internalList = FXCollections.observableArrayList();
    private final ObservableList<CourseClass> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent course class as the given argument.
     */
    public boolean contains(CourseClass toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameCourseClass);
    }

    /**
     * Adds a course class to the list.
     * The course class must not already exist in the list.
     */
    public void add(CourseClass toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateCourseClassException();
        }
        internalList.add(toAdd);
    }

    /**
     * Replaces the course class {@code target} in the list with {@code editedCourseClass}.
     * {@code target} must exist in the list.
     * The course class identity of {@code editedCourseClass} must not be the same as another existing course class in
     * the
     * list.
     */
    public void setCourseClass(CourseClass target, CourseClass editedCourseClass) {
        requireAllNonNull(target, editedCourseClass);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new CourseClassNotFoundException();
        }

        if (!target.isSameCourseClass(editedCourseClass) && contains(editedCourseClass)) {
            throw new DuplicateCourseClassException();
        }

        internalList.set(index, editedCourseClass);
    }

    /**
     * Removes the equivalent course class from the list.
     * The course class must exist in the list.
     */
    public void remove(CourseClass toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new CourseClassNotFoundException();
        }
    }

    public void setCourseClasses(UniqueCourseClassList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code courseClasses}.
     * {@code courseClasses} must not contain duplicate course classes.
     */
    public void setCourseClasses(List<CourseClass> courseClasses) {
        requireAllNonNull(courseClasses);
        if (!courseClassesAreUnique(courseClasses)) {
            throw new DuplicateCourseClassException();
        }

        internalList.setAll(courseClasses);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<CourseClass> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<CourseClass> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UniqueCourseClassList otherUniqueCourseClassList)) {
            return false;
        }

        return internalList.equals(otherUniqueCourseClassList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    /**
     * Returns true if {@code courseClasses} contains only unique course classes.
     */
    private boolean courseClassesAreUnique(List<CourseClass> courseClasses) {
        for (int i = 0; i < courseClasses.size() - 1; i++) {
            for (int j = i + 1; j < courseClasses.size(); j++) {
                if (courseClasses.get(i).isSameCourseClass(courseClasses.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
