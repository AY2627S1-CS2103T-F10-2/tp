package seedu.address.model;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.person.Person;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /** {@code Predicate} that always evaluates to true for course classes. */
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
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

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
     * The identity of {@code editedCourseClass} must not match another existing course class in the book.
     */
    void setCourseClass(CourseClass target, CourseClass editedCourseClass);

    /** Returns an unmodifiable view of the filtered course class list */
    ObservableList<CourseClass> getFilteredCourseClassList();

    /**
     * Updates the filter of the filtered course class list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredCourseClassList(Predicate<CourseClass> predicate);
}
