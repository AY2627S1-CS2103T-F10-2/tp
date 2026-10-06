package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.courseClass.CourseClass;

/**
 * Represents the in-memory model of the course class book data.
 */
public class CourseClassModelManager implements CourseClassModel {
    private static final Logger logger = LogsCenter.getLogger(CourseClassModelManager.class);

    private final CourseClassBook courseClassBook;
    private final UserPrefs userPrefs;
    private final FilteredList<CourseClass> filteredCourseClasses;

    /**
     * Initializes a CourseClassModelManager with the given course classBook and userPrefs.
     */
    public CourseClassModelManager(ReadOnlyCourseClassBook courseClassBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(courseClassBook, userPrefs);

        logger.fine("Initializing with course class book: " + courseClassBook + " and user prefs " + userPrefs);

        this.courseClassBook = new CourseClassBook(courseClassBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredCourseClasses = new FilteredList<>(this.courseClassBook.getCourseClassList());
    }

    public CourseClassModelManager() {
        this(new CourseClassBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== CourseClassBook ================================================================================

    @Override
    public void setCourseClassBook(ReadOnlyCourseClassBook courseClassBook) {
        this.courseClassBook.resetData(courseClassBook);
    }

    @Override
    public ReadOnlyCourseClassBook getCourseClassBook() {
        return courseClassBook;
    }

    @Override
    public boolean hasCourseClass(CourseClass courseClass) {
        requireNonNull(courseClass);
        return courseClassBook.hasCourseClass(courseClass);
    }

    @Override
    public void deleteCourseClass(CourseClass target) {
        courseClassBook.removeCourseClass(target);
    }

    @Override
    public void addCourseClass(CourseClass courseClass) {
        courseClassBook.addCourseClass(courseClass);
        updateFilteredCourseClassList(PREDICATE_SHOW_ALL_COURSE_CLASSES);
    }

    @Override
    public void setCourseClass(CourseClass target, CourseClass editedCourseClass) {
        requireAllNonNull(target, editedCourseClass);

        courseClassBook.setCourseClass(target, editedCourseClass);
    }

    //=========== Filtered CourseClass List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code CourseClass} backed by the internal list of
     * {@code courseClassBook}
     */
    @Override
    public ObservableList<CourseClass> getFilteredCourseClassList() {
        return filteredCourseClasses;
    }

    @Override
    public void updateFilteredCourseClassList(Predicate<CourseClass> predicate) {
        requireNonNull(predicate);
        filteredCourseClasses.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CourseClassModelManager otherCourseClassModelManager)) {
            return false;
        }

        return courseClassBook.equals(otherCourseClassModelManager.courseClassBook)
                && userPrefs.equals(otherCourseClassModelManager.userPrefs)
                && filteredCourseClasses.equals(otherCourseClassModelManager.filteredCourseClasses);
    }

}
