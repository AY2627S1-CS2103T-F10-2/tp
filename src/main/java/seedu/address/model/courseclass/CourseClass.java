package seedu.address.model.courseclass;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.AddressBook;
import seedu.address.model.student.Student;
import seedu.address.model.student.Telehandle;
import seedu.address.model.student.UniqueStudentList;
import seedu.address.model.tag.Tag;

/**
 * Represents a class in the system.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class CourseClass {

    // Identity fields
    private final Name name;
    private final CourseCode code;

    // Data fields
    private final Set<Tag> tags = new HashSet<>();
    private final AddressBook students;
    private final UniqueStudentList enrolledStudents;

    /**
     * Every field must be present and not null.
     */
    public CourseClass(Name name, CourseCode code, Set<Tag> tags) {
        requireAllNonNull(name, code, tags);
        this.name = name;
        this.code = code;
        this.tags.addAll(tags);
        this.students = new AddressBook();
        this.enrolledStudents = new UniqueStudentList();
    }

    /**
     * Every field must be present and not null.
     */
    public CourseClass(Name name, CourseCode code, Set<Tag> tags, AddressBook students) {
        requireAllNonNull(name, code, tags, students);
        this.name = name;
        this.code = code;
        this.tags.addAll(tags);
        this.students = students;
        this.enrolledStudents = new UniqueStudentList();
    }

    /**
     * Every field must be present and not null.
     *
     * @param name class name
     * @param code course code
     * @param tags class tags
     * @param students legacy student data
     * @param enrolledStudents students using the current student model
     */
    public CourseClass(Name name, CourseCode code, Set<Tag> tags, AddressBook students,
            UniqueStudentList enrolledStudents) {
        requireAllNonNull(name, code, tags, students, enrolledStudents);
        this.name = name;
        this.code = code;
        this.tags.addAll(tags);
        this.students = students;
        this.enrolledStudents = new UniqueStudentList(enrolledStudents);
    }

    /**
     * Every field must be present and not null.
     *
     * @param name class group name
     * @param code module code
     * @param tags class tags
     * @param students legacy student data
     * @param enrolledStudents students using the current student model
     */
    public CourseClass(Name name, CourseCode code, Set<Tag> tags, AddressBook students,
            List<Student> enrolledStudents) {
        requireAllNonNull(name, code, tags, students, enrolledStudents);
        this.name = name;
        this.code = code;
        this.tags.addAll(tags);
        this.students = students;
        this.enrolledStudents = new UniqueStudentList();
        this.enrolledStudents.setStudents(enrolledStudents);
    }

    public Name getName() {
        return name;
    }

    public CourseCode getCourseCode() {
        return code;
    }

    public AddressBook getStudents() {
        return students;
    }

    /**
     * Returns true if a student with the same telehandle is enrolled in this class.
     *
     * @param student student to check
     * @return true if the student is enrolled in this class
     */
    public boolean hasStudent(Student student) {
        return enrolledStudents.contains(student);
    }

    /** Returns true if a student with the given telehandle is enrolled in this class. */
    public boolean hasStudent(Telehandle telehandle) {
        return enrolledStudents.containsTelehandle(telehandle);
    }

    /**
     * Adds a student to this class.
     * The student must not already be enrolled in this class.
     *
     * @param student student to enrol
     */
    public void addStudent(Student student) {
        enrolledStudents.add(student);
    }

    /** Removes the student with the given telehandle from this class. */
    public void removeStudent(Telehandle telehandle) {
        enrolledStudents.remove(telehandle);
    }

    /**
     * Returns an unmodifiable view of the students enrolled in this class.
     *
     * @return an unmodifiable student list
     */
    public ObservableList<Student> getStudentList() {
        return enrolledStudents.asUnmodifiableObservableList();
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both course classes have the same class group and module.
     * This defines the identity of a course class.
     */
    public boolean isSameCourseClass(CourseClass otherCourseClass) {
        if (otherCourseClass == this) {
            return true;
        }

        return otherCourseClass != null
                && otherCourseClass.getName().equals(getName())
                && otherCourseClass.getCourseCode().equals(getCourseCode());
    }

    /**
     * Returns true if both course classes have the same identity and data fields.
     * This defines a stronger notion of equality between two course classes.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CourseClass otherCourseClass)) {
            return false;
        }

        return name.equals(otherCourseClass.name)
                && code.equals(otherCourseClass.code)
                && tags.equals(otherCourseClass.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, code, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("code", code)
                .add("tags", tags)
                .toString();
    }

}
