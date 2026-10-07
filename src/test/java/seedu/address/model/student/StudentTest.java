package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class StudentTest {

    private final StudentName name = new StudentName("John Doe");
    private final Telehandle telehandle = new Telehandle("john_doe");

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Student(null, telehandle));
        assertThrows(NullPointerException.class, () -> new Student(name, null));
    }

    @Test
    public void getters_returnsStudentDetails() {
        Student student = new Student(name, telehandle);

        assertEquals(name, student.getName());
        assertEquals(telehandle, student.getTelehandle());
    }

    @Test
    public void isSameStudent_comparesTelehandles() {
        Student student = new Student(name, telehandle);
        Student sameStudent = new Student(new StudentName("Jane Doe"), telehandle);
        Student differentStudent = new Student(name, new Telehandle("janedoe"));

        assertTrue(student.isSameStudent(student));
        assertTrue(student.isSameStudent(sameStudent));
        assertFalse(student.isSameStudent(differentStudent));
        assertFalse(student.isSameStudent(null));
    }

    @Test
    public void equals_comparesNameAndTelehandle() {
        Student student = new Student(name, telehandle);
        Student copy = new Student(new StudentName("John Doe"), new Telehandle("john_doe"));

        assertEquals(student, copy);
        assertEquals(student.hashCode(), copy.hashCode());
        assertNotEquals(student, null);
        assertNotEquals(student, "John Doe");
        assertNotEquals(student, new Student(new StudentName("Jane Doe"), telehandle));
        assertNotEquals(student, new Student(name, new Telehandle("janedoe")));
        assertEquals(Student.class.getCanonicalName() + "{name=" + name + ", telehandle=" + telehandle + "}",
                student.toString());
    }
}
