package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.CourseClassBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonCourseClassBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

public class MainAppTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void initModel_missingData_usesSampleClasses() throws Exception {
        assertEquals(SampleDataUtil.getSampleCourseClassBook(), initializeModel().getCourseClassBook());
    }

    @Test
    public void initModel_savedData_preservesClassesAndPreferences() throws Exception {
        CourseClassBook book = new CourseClassBook();
        book.addCourseClass(new CourseClass(new Name("Saved class"), new CourseCode("CS2101"), Set.of()));
        new JsonCourseClassBookStorage(temporaryFolder.resolve("classes.json")).saveCourseClassBook(book);
        assertEquals(book, initializeModel().getCourseClassBook());
    }

    @Test
    public void initModel_invalidData_startsWithEmptyBook() throws Exception {
        Files.writeString(temporaryFolder.resolve("classes.json"), "invalid JSON");
        assertTrue(initializeModel().getCourseClassBook().getCourseClassList().isEmpty());
    }

    @Test
    public void initPrefs_missingSavedAndInvalidData_usesAppropriatePreferences() throws Exception {
        MainApp app = new MainApp();
        JsonUserPrefsStorage storage = new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"));
        assertEquals(new UserPrefs(), app.initPrefs(storage));
        assertEquals(new UserPrefs(), storage.readUserPrefs().orElseThrow());
        UserPrefs saved = new UserPrefs();
        saved.setGuiSettings(new GuiSettings(800, 600, 10, 20));
        storage.saveUserPrefs(saved);
        assertEquals(saved, app.initPrefs(storage));
        Files.writeString(storage.getUserPrefsFilePath(), "invalid JSON");
        assertEquals(new UserPrefs(), app.initPrefs(storage));
    }

    @Test
    public void initPrefs_unwritablePath_stillReturnsDefaults() throws Exception {
        Path parentFile = temporaryFolder.resolve("file");
        Files.writeString(parentFile, "not a directory");
        assertEquals(new UserPrefs(), new MainApp().initPrefs(
                new JsonUserPrefsStorage(parentFile.resolve("prefs.json"))));
    }

    @Test
    public void stop_savesPreferencesAndHandlesWriteFailure() throws Exception {
        MainApp app = new MainApp();
        Path prefsFile = temporaryFolder.resolve("prefs.json");
        JsonUserPrefsStorage prefsStorage = new JsonUserPrefsStorage(prefsFile);
        app.storage = new StorageManager(new JsonCourseClassBookStorage(temporaryFolder.resolve("classes.json")),
                prefsStorage);
        app.model = initializeModel();
        app.model.setGuiSettings(new GuiSettings(800, 650, 10, 20));
        app.stop();
        assertEquals(app.model.getUserPrefs(), prefsStorage.readUserPrefs().orElseThrow());
        Files.delete(prefsFile);
        Files.createDirectory(prefsFile);
        app.stop();
        assertTrue(Files.isDirectory(prefsFile));
    }

    private Model initializeModel() throws Exception {
        Storage storage = new StorageManager(new JsonCourseClassBookStorage(temporaryFolder.resolve("classes.json")),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        UserPrefs prefs = new UserPrefs();
        Method method = MainApp.class.getDeclaredMethod("initModelManager", Storage.class, ReadOnlyUserPrefs.class);
        method.setAccessible(true);
        Model model = (Model) method.invoke(new MainApp(), storage, prefs);
        assertEquals(prefs, model.getUserPrefs());
        return model;
    }
}
