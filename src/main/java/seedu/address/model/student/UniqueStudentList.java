package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.student.exceptions.DuplicateStudentException;
import seedu.address.model.student.exceptions.StudentNotFoundException;

/**
 * A list of students that enforces uniqueness by telehandle and does not allow nulls.
 *
 * <p>Students with the same name but different telehandles are allowed.</p>
 */
public class UniqueStudentList implements Iterable<Student> {

    private final ObservableList<Student> internalList = FXCollections.observableArrayList();
    private final ObservableList<Student> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /** Creates an empty student list. */
    public UniqueStudentList() {}

    /**
     * Creates a student list containing the students in {@code toBeCopied}.
     *
     * @param toBeCopied list to copy
     */
    public UniqueStudentList(UniqueStudentList toBeCopied) {
        requireNonNull(toBeCopied);
        setStudents(toBeCopied.asUnmodifiableObservableList());
    }

    /**
     * Returns true if the list contains a student with the same telehandle as {@code toCheck}.
     *
     * @param toCheck student to look for
     * @return true if a student with the same identity exists
     */
    public boolean contains(Student toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameStudent);
    }

    /** Returns true if the list contains a student with the given telehandle. */
    public boolean containsTelehandle(Telehandle telehandle) {
        requireNonNull(telehandle);
        return internalList.stream().anyMatch(student -> student.getTelehandle().equals(telehandle));
    }

    /**
     * Adds a student to the list.
     *
     * @param toAdd student to add
     * @throws DuplicateStudentException if a student with the same telehandle already exists
     */
    public void add(Student toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateStudentException();
        }
        internalList.add(toAdd);
    }

    /** Removes the student with the given telehandle from the list. */
    public void remove(Telehandle telehandle) {
        requireNonNull(telehandle);
        boolean removed = internalList.removeIf(student -> student.getTelehandle().equals(telehandle));
        if (!removed) {
            throw new StudentNotFoundException();
        }
    }

    /**
     * Replaces the contents of this list with {@code students}.
     *
     * @param students replacement students
     * @throws DuplicateStudentException if duplicate telehandles are present
     */
    public void setStudents(List<Student> students) {
        requireAllNonNull(students);
        if (!studentsAreUnique(students)) {
            throw new DuplicateStudentException();
        }
        internalList.setAll(students);
    }

    /**
     * Returns the backing list as an unmodifiable observable list.
     *
     * @return an unmodifiable student list
     */
    public ObservableList<Student> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Student> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof UniqueStudentList otherUniqueStudentList)) {
            return false;
        }

        return internalList.equals(otherUniqueStudentList.internalList);
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    @Override
    public String toString() {
        return internalList.toString();
    }

    /** Returns true if every student in {@code students} has a unique telehandle. */
    private boolean studentsAreUnique(List<Student> students) {
        for (int i = 0; i < students.size() - 1; i++) {
            for (int j = i + 1; j < students.size(); j++) {
                if (students.get(i).isSameStudent(students.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
