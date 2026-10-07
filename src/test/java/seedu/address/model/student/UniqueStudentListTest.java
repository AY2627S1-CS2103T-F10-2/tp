package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.student.exceptions.DuplicateStudentException;

public class UniqueStudentListTest {

    private final UniqueStudentList students = new UniqueStudentList();
    private final Student first = createStudent("John Doe", "john_doe");
    private final Student second = createStudent("Jane Doe", "janedoe");

    @Test
    public void containsAndAdd_enforceTelehandleIdentity() {
        assertFalse(students.contains(first));
        students.add(first);
        assertTrue(students.contains(createStudent("Different Name", "john_doe")));
        assertThrows(DuplicateStudentException.class, () -> students.add(
                createStudent("Different Name", "john_doe")));
        students.add(second);
        assertEquals(List.of(first, second), students.asUnmodifiableObservableList());
    }

    @Test
    public void setStudents_replacesDataAndRejectsInvalidReplacement() {
        students.add(first);
        students.setStudents(List.of(second));
        assertEquals(List.of(second), students.asUnmodifiableObservableList());

        assertThrows(DuplicateStudentException.class, () -> students.setStudents(List.of(first, first)));
        assertThrows(NullPointerException.class, () -> students.setStudents(Arrays.asList(first, null)));
        assertEquals(List.of(second), students.asUnmodifiableObservableList());
    }

    @Test
    public void listView_isUnmodifiableAndFollowsContents() {
        var view = students.asUnmodifiableObservableList();
        students.add(first);

        assertEquals(List.of(first), view);
        assertThrows(UnsupportedOperationException.class, () -> view.clear());
    }

    @Test
    public void operations_nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> students.contains(null));
        assertThrows(NullPointerException.class, () -> students.add(null));
        assertThrows(NullPointerException.class, () -> students.setStudents(null));
    }

    @Test
    public void iteratorAndDiagnostics_reflectListContents() {
        students.add(first);
        students.add(second);
        UniqueStudentList copy = new UniqueStudentList(students);

        List<Student> iteratedStudents = new java.util.ArrayList<>();
        for (Student student : students) {
            iteratedStudents.add(student);
        }

        assertEquals(List.of(first, second), iteratedStudents);
        assertTrue(students.equals(students));
        assertEquals(students, copy);
        assertEquals(students.hashCode(), copy.hashCode());
        assertEquals(students.asUnmodifiableObservableList().toString(), students.toString());
        assertNotEquals(students, null);
        assertNotEquals(students, "students");
        assertNotEquals(students, new UniqueStudentList());
    }

    private static Student createStudent(String name, String telehandle) {
        return new Student(new StudentName(name), new Telehandle(telehandle));
    }
}
