package seedu.address.model.courseclass;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.student.Student;
import seedu.address.model.student.Telehandle;
import seedu.address.model.student.UniqueStudentList;
import seedu.address.model.tag.Tag;

/**
 * Represents a class in the system.
 * Guarantees: details are present and not null, field values are validated.
 */
public class CourseClass {

    // Identity fields
    private final Name name;
    private final CourseCode code;

    // Data fields
    private final Set<Tag> tags = new HashSet<>();
    private final UniqueStudentList students;
    private final ObjectProperty<HomeworkStatus> homeworkStatus = new SimpleObjectProperty<>();

    /**
     * Every field must be present and not null.
     */
    public CourseClass(Name name, CourseCode code, Set<Tag> tags) {
        this(name, code, tags, List.of(), HomeworkStatus.NOT_SET);
    }

    /**
     * Every field must be present and not null.
     */
    public CourseClass(Name name, CourseCode code, Set<Tag> tags, List<Student> students) {
        this(name, code, tags, students, HomeworkStatus.NOT_SET);
    }

    /**
     * Every field must be present and not null.
     *
     * @param name class group name
     * @param code module code
     * @param tags class tags
     * @param students students enrolled in the class
     * @param homeworkStatus status of the homework assigned to the class
     */
    public CourseClass(Name name, CourseCode code, Set<Tag> tags, List<Student> students,
            HomeworkStatus homeworkStatus) {
        requireAllNonNull(name, code, tags, students, homeworkStatus);
        this.name = name;
        this.code = code;
        this.tags.addAll(tags);
        this.students = new UniqueStudentList();
        this.students.setStudents(students);
        this.homeworkStatus.set(homeworkStatus);
    }

    public Name getName() {
        return name;
    }

    public CourseCode getCourseCode() {
        return code;
    }

    /** Returns the homework status shared by all students in this class. */
    public HomeworkStatus getHomeworkStatus() {
        return homeworkStatus.get();
    }

    /** Returns the observable homework status shared by all students in this class. */
    public ReadOnlyObjectProperty<HomeworkStatus> homeworkStatusProperty() {
        return homeworkStatus;
    }

    /** Updates the homework status shared by all students in this class. */
    public void setHomeworkStatus(HomeworkStatus homeworkStatus) {
        requireAllNonNull(homeworkStatus);
        this.homeworkStatus.set(homeworkStatus);
    }

    /**
     * Returns true if a student with the same telehandle is enrolled in this class.
     *
     * @param student student to check
     * @return true if the student is enrolled in this class
     */
    public boolean hasStudent(Student student) {
        return students.contains(student);
    }

    /** Returns true if a student with the given telehandle is enrolled in this class. */
    public boolean hasStudent(Telehandle telehandle) {
        return students.containsTelehandle(telehandle);
    }

    /**
     * Adds a student to this class.
     * The student must not already be enrolled in this class.
     *
     * @param student student to enrol
     */
    public void addStudent(Student student) {
        students.add(student);
    }

    /** Replaces {@code target} with {@code editedStudent} in this class. */
    public void setStudent(Student target, Student editedStudent) {
        students.setStudent(target, editedStudent);
    }

    /** Removes the student with the given telehandle from this class. */
    public void removeStudent(Telehandle telehandle) {
        students.remove(telehandle);
    }

    /**
     * Returns an unmodifiable view of the students enrolled in this class.
     *
     * @return an unmodifiable student list
     */
    public ObservableList<Student> getStudentList() {
        return students.asUnmodifiableObservableList();
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
     * Returns true if both course classes have the same identity and class metadata.
     * Class metadata consists of the class group, module, and tags. Student enrolment data is not included.
     * This defines a stronger notion of equality than {@link #isSameCourseClass(CourseClass)}.
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
                && tags.equals(otherCourseClass.tags)
                && getHomeworkStatus().equals(otherCourseClass.getHomeworkStatus());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, code, tags, getHomeworkStatus());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("code", code)
                .add("tags", tags)
                .add("homeworkStatus", getHomeworkStatus())
                .toString();
    }

}
