package seedu.address.model.courseclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.courseclass.exceptions.CourseClassNotFoundException;
import seedu.address.model.courseclass.exceptions.DuplicateCourseClassException;

public class UniqueCourseClassListTest {
    private final UniqueCourseClassList classes = new UniqueCourseClassList();
    private final CourseClass first = createClass("F10-2", "CS2103T");
    private final CourseClass second = createClass("F10-3", "CS2103T");

    @Test
    public void containsAndAdd_enforceIdentity() {
        assertFalse(classes.contains(first));
        classes.add(first);
        CourseClass sameName = createClass("F10-2", "CS2101");
        assertTrue(classes.contains(sameName));
        assertThrows(DuplicateCourseClassException.class, () -> classes.add(sameName));
        assertEquals(List.of(first), classes.asUnmodifiableObservableList());
    }

    @Test
    public void setCourseClass_replacesDataAndAllowsNewIdentity() {
        classes.add(first);
        CourseClass updated = createClass("F10-2", "CS2101");
        classes.setCourseClass(first, updated);
        classes.setCourseClass(updated, second);
        assertEquals(List.of(second), classes.asUnmodifiableObservableList());
    }

    @Test
    public void setCourseClass_missingOrDuplicateTarget_leavesListUnchanged() {
        classes.setCourseClasses(List.of(first, second));
        assertThrows(CourseClassNotFoundException.class, () -> classes.setCourseClass(
                createClass("missing", "CS2101"), first));
        assertThrows(DuplicateCourseClassException.class, () -> classes.setCourseClass(second, first));
        assertEquals(List.of(first, second), classes.asUnmodifiableObservableList());
    }

    @Test
    public void remove_requiresExactFields() {
        classes.add(first);
        assertThrows(CourseClassNotFoundException.class, () -> classes.remove(createClass("F10-2", "CS2101")));
        classes.remove(first);
        assertTrue(classes.asUnmodifiableObservableList().isEmpty());
        assertThrows(CourseClassNotFoundException.class, () -> classes.remove(first));
    }

    @Test
    public void setCourseClasses_invalidReplacement_leavesListUnchanged() {
        classes.add(first);
        assertThrows(DuplicateCourseClassException.class, () ->
                classes.setCourseClasses(
                        List.of(second, second)));
        assertThrows(NullPointerException.class, () ->
                classes.setCourseClasses(
                        Arrays.asList(second, null)));
        assertEquals(List.of(first), classes.asUnmodifiableObservableList());
    }

    @Test
    public void setCourseClasses_copiesListAndMaintainsObservableView() {
        var view = classes.asUnmodifiableObservableList();
        UniqueCourseClassList replacement = new UniqueCourseClassList();
        replacement.add(first);
        classes.setCourseClasses(replacement);
        replacement.add(second);
        assertEquals(List.of(first), view);
        assertThrows(UnsupportedOperationException.class, () -> view.clear());
    }

    @Test
    public void operations_nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> classes.contains(null));
        assertThrows(NullPointerException.class, () -> classes.add(null));
        assertThrows(NullPointerException.class, () -> classes.remove(null));
        assertThrows(NullPointerException.class, () -> classes.setCourseClass(null, first));
        assertThrows(NullPointerException.class, () -> classes.setCourseClass(first, null));
        assertThrows(NullPointerException.class, () -> classes.setCourseClasses((List<CourseClass>) null));
        assertThrows(NullPointerException.class, () -> classes.setCourseClasses((UniqueCourseClassList) null));
    }

    private static CourseClass createClass(String name, String code) {
        return new CourseClass(new Name(name), new CourseCode(code), Set.of());
    }
}
