package seedu.address.model.student;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a student managed by the application.
 * Guarantees: details are present and not null, and field values are validated.
 */
public class Student {

    private final StudentName name;
    private final Telehandle telehandle;
    private final List<Note> notes;

    /**
     * Every field must be present and valid.
     *
     * @param name student's name
     * @param telehandle student's telehandle
     */
    public Student(StudentName name, Telehandle telehandle) {
        this(name, telehandle, List.of());
    }

    /**
     * Every field must be present and valid.
     *
     * @param name student's name
     * @param telehandle student's telehandle
     * @param notes notes recorded for the student
     */
    public Student(StudentName name, Telehandle telehandle, List<Note> notes) {
        requireAllNonNull(name, telehandle, notes);
        this.name = name;
        this.telehandle = telehandle;
        this.notes = List.copyOf(notes);
    }

    /** Returns this student's name. */
    public StudentName getName() {
        return name;
    }

    /** Returns this student's telehandle. */
    public Telehandle getTelehandle() {
        return telehandle;
    }

    /** Returns the student's notes as an unmodifiable list. */
    public List<Note> getNotes() {
        return notes;
    }

    /**
     * Returns true if both students have the same telehandle.
     * This defines student identity for enrolment purposes.
     *
     * @param otherStudent student to compare with
     * @return true if both students have the same telehandle
     */
    public boolean isSameStudent(Student otherStudent) {
        if (otherStudent == this) {
            return true;
        }

        return otherStudent != null && otherStudent.getTelehandle().equals(getTelehandle());
    }

    /**
     * Returns true if both students have the same name, telehandle and notes.
     *
     * @param other object to compare with
     * @return true if both students have the same details
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Student otherStudent)) {
            return false;
        }

        return name.equals(otherStudent.name)
                && telehandle.equals(otherStudent.telehandle)
                && notes.equals(otherStudent.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, telehandle, notes);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("telehandle", telehandle)
                .add("notes", notes)
                .toString();
    }
}
