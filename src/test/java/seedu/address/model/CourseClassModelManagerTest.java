package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.courseClass.CourseClass;
import seedu.address.model.courseClass.CourseCode;
import seedu.address.model.courseClass.Name;
import seedu.address.model.courseClass.exceptions.CourseClassNotFoundException;
import seedu.address.model.courseClass.exceptions.DuplicateCourseClassException;

public class CourseClassModelManagerTest {
    private final CourseClass first = createClass("F10-2", "CS2103T");
    private final CourseClass second = createClass("F10-3", "CS2103T");

    @Test
    public void addCourseClass_filteredList_showsAllClasses() {
        CourseClassModel model = new CourseClassModelManager();
        model.addCourseClass(first);
        model.updateFilteredCourseClassList(unused -> false);
        assertTrue(model.getFilteredCourseClassList().isEmpty());
        model.addCourseClass(second);
        assertEquals(List.of(first, second), model.getFilteredCourseClassList());
        assertThrows(UnsupportedOperationException.class, () -> model.getFilteredCourseClassList().clear());
        assertThrows(NullPointerException.class, () -> model.updateFilteredCourseClassList(null));
    }

    @Test
    public void setCourseClassBook_copiesDataAndUpdatesExistingFilteredList() {
        CourseClassModel model = new CourseClassModelManager();
        var filteredList = model.getFilteredCourseClassList();
        CourseClassBook replacement = new CourseClassBook();
        replacement.addCourseClass(first);
        model.setCourseClassBook(replacement);
        replacement.addCourseClass(second);
        assertEquals(List.of(first), filteredList);
    }

    @Test
    public void bookOperations_enforceIdentityAndRejectInvalidReplacements() {
        CourseClassBook book = new CourseClassBook();
        book.addCourseClass(first);
        book.addCourseClass(second);
        CourseClass sameName = createClass("F10-2", "CS2101");
        assertTrue(book.hasCourseClass(sameName));
        assertThrows(DuplicateCourseClassException.class, () -> book.addCourseClass(sameName));
        assertThrows(DuplicateCourseClassException.class, () -> book.setCourseClass(second, sameName));
        assertThrows(DuplicateCourseClassException.class, () -> book.setCourseClasses(List.of(first, sameName)));
        assertEquals(List.of(first, second), book.getCourseClassList());
        assertThrows(UnsupportedOperationException.class, () -> book.getCourseClassList().clear());

        book.setCourseClass(first, sameName);
        book.removeCourseClass(second);
        assertEquals(List.of(sameName), book.getCourseClassList());
        assertThrows(CourseClassNotFoundException.class, () -> book.removeCourseClass(second));
        assertThrows(NullPointerException.class, () -> book.addCourseClass(null));
    }

    private static CourseClass createClass(String name, String code) {
        return new CourseClass(new Name(name), new CourseCode(code), Set.of());
    }
}
