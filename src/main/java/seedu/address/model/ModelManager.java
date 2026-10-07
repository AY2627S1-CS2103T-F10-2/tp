package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.person.Person;

/**
 * Represents the in-memory model of student and course class data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final CourseClassBook courseClassBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<CourseClass> filteredCourseClasses;

    /** Initializes the model with student data and an empty course class book. */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        this(addressBook, new CourseClassBook(), userPrefs);
    }

    /** Initializes the model with course class data and an empty student address book. */
    public ModelManager(ReadOnlyCourseClassBook courseClassBook, ReadOnlyUserPrefs userPrefs) {
        this(new AddressBook(), courseClassBook, userPrefs);
    }

    /** Initializes the model with both books and shared user preferences. */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyCourseClassBook courseClassBook,
            ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, courseClassBook, userPrefs);
        logger.fine("Initializing with address book: " + addressBook + " and course class book: " + courseClassBook);
        this.addressBook = new AddressBook(addressBook);
        this.courseClassBook = new CourseClassBook(courseClassBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        filteredCourseClasses = new FilteredList<>(this.courseClassBook.getCourseClassList());
    }

    public ModelManager() {
        this(new AddressBook(), new CourseClassBook(), new UserPrefs());
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

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
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
    public Optional<CourseClass> findCourseClassByGroup(ClassGroup classGroup) {
        requireNonNull(classGroup);
        return courseClassBook.findCourseClassByGroup(classGroup);
    }

    @Override
    public Optional<CourseClass> findCourseClassByGroup(ClassGroup classGroup, CourseCode moduleName) {
        requireAllNonNull(classGroup, moduleName);
        return courseClassBook.findCourseClassByGroup(classGroup, moduleName);
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
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && courseClassBook.equals(otherModelManager.courseClassBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons)
                && filteredCourseClasses.equals(otherModelManager.filteredCourseClasses);
    }

}
