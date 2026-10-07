package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddCourseClassCommand;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
import seedu.address.model.courseclass.exceptions.CourseClassNotFoundException;
import seedu.address.model.courseclass.exceptions.DuplicateCourseClassException;
import seedu.address.testutil.TypicalPersons;

public class ModelManagerCourseClassTest {
    private final CourseClass first = createClass("F10-2", "CS2103T");
    private final CourseClass second = createClass("F10-3", "CS2103T");

    @Test
    public void equality_courseClassFiltersMustMatch() {
        CourseClassBook book = new CourseClassBook();
        book.addCourseClass(first);
        book.addCourseClass(second);
        ModelManager firstModel = new ModelManager(book, new UserPrefs());
        ModelManager secondModel = new ModelManager(book, new UserPrefs());
        secondModel.updateFilteredCourseClassList(first::equals);
        assertNotEquals(firstModel, secondModel);
    }

    @Test
    public void bookEquality_hashCodeAndDiagnosticString() {
        CourseClassBook book = new CourseClassBook();
        book.addCourseClass(first);
        CourseClassBook copy = new CourseClassBook(book);
        assertTrue(book.equals(book));
        assertEquals(book, copy);
        assertEquals(book.hashCode(), copy.hashCode());
        assertNotEquals(book, null);
        assertNotEquals(book, "book");
        copy.addCourseClass(second);
        assertNotEquals(book, copy);
        assertEquals(CourseClassBook.class.getCanonicalName() + "{courseClasses=" + List.of(first) + "}",
                book.toString());
        AddressBook students = new AddressBook();
        AddressBook studentCopy = new AddressBook(students);
        assertNotEquals(students, "book");
        assertEquals(students.hashCode(), studentCopy.hashCode());
    }

    @Test
    public void execute_studentAndClassCommands_shareModelWithIndependentFilters() throws Exception {
        Model model = new ModelManager();
        new AddCommand(TypicalPersons.ALICE).execute(model);
        new AddCourseClassCommand(first).execute(model);
        model.updateFilteredPersonList(unused -> false);
        assertEquals(List.of(first), model.getFilteredCourseClassList());
        model.updateFilteredCourseClassList(unused -> false);
        new AddCourseClassCommand(second).execute(model);
        assertTrue(model.getFilteredPersonList().isEmpty());
        assertEquals(List.of(first, second), model.getFilteredCourseClassList());
        assertTrue(model.hasPerson(TypicalPersons.ALICE));
    }

    @Test
    public void constructor_bothBooks_copiesDataAndIncludesClassStateInEquality() {
        AddressBook students = new AddressBook();
        students.addPerson(TypicalPersons.ALICE);
        CourseClassBook classes = new CourseClassBook();
        classes.addCourseClass(first);
        ModelManager model = new ModelManager(students, classes, new UserPrefs());
        assertEquals(model, new ModelManager(students, classes, new UserPrefs()));
        assertNotEquals(model, new ModelManager(students, new UserPrefs()));
        assertNotEquals(model, new ModelManager(classes, new UserPrefs()));
        students.addPerson(TypicalPersons.BOB);
        classes.addCourseClass(second);
        assertEquals(List.of(TypicalPersons.ALICE), model.getFilteredPersonList());
        assertEquals(List.of(first), model.getFilteredCourseClassList());
        assertThrows(NullPointerException.class, () -> new ModelManager(null, classes, new UserPrefs()));
        assertThrows(NullPointerException.class, () -> new ModelManager(students, null, new UserPrefs()));
        assertThrows(NullPointerException.class, () -> new ModelManager(students, classes, null));
    }

    @Test
    public void addCourseClass_filteredList_showsAllClasses() {
        Model model = new ModelManager();
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
        Model model = new ModelManager();
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
        CourseClass sameGroupDifferentModule = createClass("F10-2", "CS2101");
        assertFalse(book.hasCourseClass(sameGroupDifferentModule));
        book.addCourseClass(sameGroupDifferentModule);
        assertEquals(List.of(first, second, sameGroupDifferentModule), book.getCourseClassList());
        assertThrows(DuplicateCourseClassException.class, () -> book.addCourseClass(first));
        assertThrows(DuplicateCourseClassException.class, () -> book.setCourseClass(second, first));
        assertThrows(DuplicateCourseClassException.class, () -> book.setCourseClasses(List.of(first, first)));
        assertThrows(UnsupportedOperationException.class, () -> book.getCourseClassList().clear());

        CourseClass updatedFirst = createClass("F10-4", "CS2101");
        book.setCourseClass(first, updatedFirst);
        book.removeCourseClass(second);
        assertEquals(List.of(updatedFirst, sameGroupDifferentModule), book.getCourseClassList());
        assertThrows(CourseClassNotFoundException.class, () -> book.removeCourseClass(second));
        assertThrows(NullPointerException.class, () -> book.addCourseClass(null));
    }

    @Test
    public void findCourseClassByGroup_legacyLookupFindsFirstMatchingGroup() {
        CourseClassBook book = new CourseClassBook();
        book.addCourseClass(first);
        book.addCourseClass(createClass("F10-2", "CS2101"));

        assertEquals(first, book.findCourseClassByGroup(new ClassGroup("F10-2"))
                .orElseThrow());
        assertTrue(book.findCourseClassByGroup(new ClassGroup("F10-4")).isEmpty());

        ModelManager model = new ModelManager(book, new UserPrefs());
        assertEquals(first, model.findCourseClassByGroup(new ClassGroup("F10-2"))
                .orElseThrow());
    }

    private static CourseClass createClass(String name, String code) {
        return new CourseClass(new Name(name), new CourseCode(code), Set.of());
    }
}
