package seedu.address.model.courseclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.tag.Tag;

public class CourseClassTest {
    private final Name name = new Name("F10-2");
    private final CourseCode code = new CourseCode("CS2103T");

    @Test
    public void constructor_nullFields_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CourseClass(null, code, Set.of()));
        assertThrows(NullPointerException.class, () -> new CourseClass(name, null, Set.of()));
        assertThrows(NullPointerException.class, () -> new CourseClass(name, code, null));
        assertThrows(NullPointerException.class, () -> new CourseClass(name, code, Set.of(), null));
    }

    @Test
    public void getTags_defensiveCopyAndUnmodifiableView() {
        Set<Tag> tags = new HashSet<>(Set.of(new Tag("tutorial")));
        CourseClass courseClass = new CourseClass(name, code, tags);
        tags.clear();
        assertEquals(Set.of(new Tag("tutorial")), courseClass.getTags());
        assertThrows(UnsupportedOperationException.class, () -> courseClass.getTags().clear());
        assertTrue(courseClass.getStudents().getPersonList().isEmpty());
    }

    @Test
    public void isSameCourseClass_comparesNamesRegardlessOfCodeOrTags() {
        CourseClass courseClass = new CourseClass(name, code, Set.of());
        assertTrue(courseClass.isSameCourseClass(courseClass));
        assertTrue(courseClass.isSameCourseClass(new CourseClass(name, new CourseCode("CS2101"),
                Set.of(new Tag("tutorial")))));
        assertFalse(courseClass.isSameCourseClass(null));
        assertFalse(courseClass.isSameCourseClass(new CourseClass(new Name("F10-3"), code, Set.of())));
    }

    @Test
    public void equals_comparesNameCodeAndTags() {
        CourseClass courseClass = new CourseClass(name, code, Set.of());
        CourseClass copy = new CourseClass(name, code, Set.of(), new AddressBook());
        assertEquals(courseClass, copy);
        assertEquals(courseClass.hashCode(), copy.hashCode());
        assertNotEquals(courseClass, null);
        assertNotEquals(courseClass, "F10-2");
        assertNotEquals(courseClass, new CourseClass(new Name("F10-3"), code, Set.of()));
        assertNotEquals(courseClass, new CourseClass(name, new CourseCode("CS2101"), Set.of()));
        assertNotEquals(courseClass, new CourseClass(name, code, Set.of(new Tag("tutorial"))));
    }
}
