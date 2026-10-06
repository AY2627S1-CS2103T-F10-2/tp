package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.courseClass.CourseClass;
import seedu.address.model.courseClass.CourseCode;
import seedu.address.model.courseClass.Name;
import seedu.address.storage.JsonCourseClassBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.TypicalPersons;

public class CourseClassLogicIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void execute_classCommands_updatesDisplayedListAndSavedData() throws Exception {
        Model model = new ModelManager();
        JsonCourseClassBookStorage bookStorage =
                new JsonCourseClassBookStorage(temporaryFolder.resolve("classes.json"));
        Logic logic = new LogicManager(model, new StorageManager(bookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
        logic.execute("aclass n/F10-2 c/CS2103T t/tutorial");
        logic.execute("aclass n/F10-3 c/CS2103T");
        assertEquals(2, logic.getFilteredCourseClassList().size());

        logic.execute("fclass F10-3");
        assertEquals("F10-3", logic.getFilteredCourseClassList().get(0).getName().fullName);
        logic.execute("eclass 1 n/F10-4 c/CS2101 t/");
        assertEquals(2, logic.getFilteredCourseClassList().size());
        assertEquals("CS2101", logic.getFilteredCourseClassList().get(1).getCourseCode().value);
        assertThrows(CommandException.class, () -> logic.execute("eclass 2 n/F10-2"));
        assertThrows(CommandException.class, () -> logic.execute("dclass 3"));
        assertThrows(ParseException.class, () -> logic.execute("eclass 1 c/"));
        assertThrows(ParseException.class, () -> logic.execute("eclass 1 n/F10-5 n/F10-6"));

        logic.execute("fclass F10-4");
        logic.execute("dclass 1");
        logic.execute("lclass");
        assertEquals("F10-2", logic.getFilteredCourseClassList().get(0).getName().fullName);
        assertEquals(model.getCourseClassBook(), bookStorage.readCourseClassBook().orElseThrow());
        logic.execute("cclass");
        assertTrue(logic.getFilteredCourseClassList().isEmpty());
        assertTrue(bookStorage.readCourseClassBook().orElseThrow().getCourseClassList().isEmpty());
    }

    @Test
    public void execute_edit_preservesClassStudents() throws Exception {
        Model model = new ModelManager();
        CourseClass courseClass = new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"), Set.of());
        courseClass.getStudents().addPerson(TypicalPersons.ALICE);
        model.addCourseClass(courseClass);
        JsonCourseClassBookStorage bookStorage =
                new JsonCourseClassBookStorage(temporaryFolder.resolve("classes.json"));
        Logic logic = new LogicManager(model, new StorageManager(bookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
        logic.execute("eclass 1 n/F10-3");
        assertEquals(courseClass.getStudents(), logic.getFilteredCourseClassList().get(0).getStudents());
        assertEquals(courseClass.getStudents(),
                bookStorage.readCourseClassBook().orElseThrow().getCourseClassList().get(0).getStudents());
    }
}
