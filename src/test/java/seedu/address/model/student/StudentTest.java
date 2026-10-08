package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class StudentTest {

    private final StudentName name = new StudentName("John Doe");
    private final Telehandle telehandle = new Telehandle("john_doe");
    private final Note note = new Note("Needs follow-up", LocalDateTime.of(2026, 10, 8, 14, 30));

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        List<Note> notesWithNullElement = Arrays.asList((Note) null);

        assertThrows(NullPointerException.class, () -> new Student(null, telehandle));
        assertThrows(NullPointerException.class, () -> new Student(name, null));
        assertThrows(NullPointerException.class, () -> new Student(name, telehandle, null));
        assertThrows(NullPointerException.class, () -> new Student(name, telehandle, notesWithNullElement));
    }

    @Test
    public void getters_returnsStudentDetails() {
        Student student = new Student(name, telehandle);

        assertEquals(name, student.getName());
        assertEquals(telehandle, student.getTelehandle());
        assertTrue(student.getNotes().isEmpty());
    }

    @Test
    public void constructor_notesAreDefensivelyCopiedAndUnmodifiable() {
        List<Note> notes = new ArrayList<>(List.of(note));
        Student student = new Student(name, telehandle, notes);

        assertEquals(List.of(note), student.getNotes());

        notes.clear();
        assertEquals(List.of(note), student.getNotes());
        assertThrows(UnsupportedOperationException.class, () -> student.getNotes().add(note));
    }

    @Test
    public void isSameStudent_comparesTelehandles() {
        Student student = new Student(name, telehandle);
        Student sameStudent = new Student(new StudentName("Jane Doe"), telehandle);
        Student sameStudentWithNotes = new Student(name, telehandle, List.of(note));
        Student differentStudent = new Student(name, new Telehandle("janedoe"));

        assertTrue(student.isSameStudent(student));
        assertTrue(student.isSameStudent(sameStudent));
        assertTrue(student.isSameStudent(sameStudentWithNotes));
        assertFalse(student.isSameStudent(differentStudent));
        assertFalse(student.isSameStudent(null));
    }

    @Test
    public void equals_comparesNameTelehandleAndNotes() {
        Student student = new Student(name, telehandle);
        Student copy = new Student(new StudentName("John Doe"), new Telehandle("john_doe"));

        assertEquals(student, copy);
        assertEquals(student.hashCode(), copy.hashCode());
        assertNotEquals(student, null);
        assertNotEquals(student, "John Doe");
        assertNotEquals(student, new Student(new StudentName("Jane Doe"), telehandle));
        assertNotEquals(student, new Student(name, new Telehandle("janedoe")));
        assertNotEquals(student, new Student(name, telehandle, List.of(note)));
        assertEquals(Student.class.getCanonicalName()
                + "{name=" + name + ", telehandle=" + telehandle + ", notes=[]}", student.toString());
    }
}
