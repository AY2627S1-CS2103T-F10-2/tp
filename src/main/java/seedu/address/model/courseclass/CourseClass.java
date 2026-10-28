package seedu.address.model.courseclass;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
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
    private final ObservableMap<Telehandle, HomeworkStatus> studentHomeworkStatuses =
            FXCollections.observableHashMap();
    private final ObservableMap<Telehandle, HomeworkStatus> unmodifiableStudentHomeworkStatuses =
            FXCollections.unmodifiableObservableMap(studentHomeworkStatuses);

    /** Creates a deep copy of {@code source}, including its students. */
    public CourseClass(CourseClass source) {
        this(source.name, source.code, source.tags, source.getStudentList(), source.getHomeworkStatus(),
                source.getStudentHomeworkStatuses());
    }

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
        this(name, code, tags, students, homeworkStatus, Map.of());
    }

    /** Creates a course class with class-wide and individual homework statuses. */
    public CourseClass(Name name, CourseCode code, Set<Tag> tags, List<Student> students,
            HomeworkStatus homeworkStatus, Map<Telehandle, HomeworkStatus> studentHomeworkStatuses) {
        requireAllNonNull(name, code, tags, students, homeworkStatus);
        requireAllNonNull(studentHomeworkStatuses);
        this.name = name;
        this.code = code;
        this.tags.addAll(tags);
        this.students = new UniqueStudentList();
        this.students.setStudents(students);
        this.homeworkStatus.set(homeworkStatus);
        studentHomeworkStatuses.forEach(this::setStudentHomeworkStatus);
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

    /** Returns the effective homework status for an enrolled student. */
    public HomeworkStatus getHomeworkStatus(Telehandle telehandle) {
        requireAllNonNull(telehandle);
        return studentHomeworkStatuses.getOrDefault(telehandle, getHomeworkStatus());
    }

    /** Returns the individual homework status for a student, or the class default if none is set. */
    public HomeworkStatus getHomeworkStatus(Student student) {
        requireAllNonNull(student);
        return getHomeworkStatus(student.getTelehandle());
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

    /** Returns an immutable copy of the individual homework status overrides. */
    public Map<Telehandle, HomeworkStatus> getStudentHomeworkStatuses() {
        return Collections.unmodifiableMap(new HashMap<>(studentHomeworkStatuses));
    }

    /** Returns an observable view of the individual homework status overrides for UI updates. */
    public ObservableMap<Telehandle, HomeworkStatus> studentHomeworkStatusesProperty() {
        return unmodifiableStudentHomeworkStatuses;
    }

    /** Sets an individual homework status override for an enrolled student. */
    public void setStudentHomeworkStatus(Telehandle telehandle, HomeworkStatus homeworkStatus) {
        requireAllNonNull(telehandle, homeworkStatus);
        if (!hasStudent(telehandle)) {
            throw new IllegalArgumentException("Student must be enrolled in the course class.");
        }
        studentHomeworkStatuses.put(telehandle, homeworkStatus);
    }

    /** Clears an individual override so the student inherits the class-wide status. */
    public void clearStudentHomeworkStatus(Telehandle telehandle) {
        requireAllNonNull(telehandle);
        if (!hasStudent(telehandle)) {
            throw new IllegalArgumentException("Student must be enrolled in the course class.");
        }
        studentHomeworkStatuses.remove(telehandle);
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
        HomeworkStatus previousStatus = studentHomeworkStatuses.remove(target.getTelehandle());
        if (previousStatus != null) {
            studentHomeworkStatuses.put(editedStudent.getTelehandle(), previousStatus);
        }
    }

    /** Removes the student with the given telehandle from this class. */
    public void removeStudent(Telehandle telehandle) {
        students.remove(telehandle);
        studentHomeworkStatuses.remove(telehandle);
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
                && getHomeworkStatus().equals(otherCourseClass.getHomeworkStatus())
                && getStudentHomeworkStatuses().equals(otherCourseClass.getStudentHomeworkStatuses());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, code, tags, getHomeworkStatus(), getStudentHomeworkStatuses());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("code", code)
                .add("tags", tags)
                .add("homeworkStatus", getHomeworkStatus())
                .add("studentHomeworkStatuses", getStudentHomeworkStatuses())
                .toString();
    }

}
