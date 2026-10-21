package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.courseclass.HomeworkStatus;
import seedu.address.storage.CourseClassBookStorageData;
import seedu.address.storage.JsonCourseClassBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class UndoLogicIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void execute_multipleChanges_undoesInLastInFirstOutOrder() throws Exception {
        JsonCourseClassBookStorage bookStorage = createBookStorage();
        Logic logic = createLogic(new ModelManager(), bookStorage);

        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("aclass n/L01 c/CS2101");
        logic.execute("dclass 2");

        logic.execute(UndoCommand.COMMAND_WORD);
        assertEquals(2, logic.getFilteredCourseClassList().size());
        assertEquals("L01", logic.getFilteredCourseClassList().get(1).getName().fullName);

        logic.execute(UndoCommand.COMMAND_WORD);
        assertEquals(1, logic.getFilteredCourseClassList().size());
        assertEquals("T05", logic.getFilteredCourseClassList().get(0).getName().fullName);
    }

    @Test
    public void execute_studentDeletion_canBeUndone() throws Exception {
        JsonCourseClassBookStorage bookStorage = createBookStorage();
        Logic logic = createLogic(new ModelManager(), bookStorage);
        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("add n/John Doe t/birdman c/T05 m/CS2103T");

        logic.execute("dstudent t/birdman c/T05 m/CS2103T");
        logic.execute("yes");
        assertTrue(logic.getFilteredCourseClassList().get(0).getStudentList().isEmpty());

        logic.execute(UndoCommand.COMMAND_WORD);

        assertEquals(1, logic.getFilteredCourseClassList().get(0).getStudentList().size());
        assertEquals("birdman", logic.getFilteredCourseClassList().get(0).getStudentList().get(0)
                .getTelehandle().value);
    }

    @Test
    public void execute_homeworkStatusChange_canBeUndone() throws Exception {
        JsonCourseClassBookStorage bookStorage = createBookStorage();
        Logic logic = createLogic(new ModelManager(), bookStorage);
        logic.execute("aclass n/T05 c/CS2103T");

        logic.execute("hwork 1 s/completed");
        assertEquals(HomeworkStatus.COMPLETED, logic.getFilteredCourseClassList().get(0).getHomeworkStatus());

        logic.execute(UndoCommand.COMMAND_WORD);

        assertEquals(HomeworkStatus.NOT_SET, logic.getFilteredCourseClassList().get(0).getHomeworkStatus());
    }

    @Test
    public void execute_classEdit_canBeUndone() throws Exception {
        JsonCourseClassBookStorage bookStorage = createBookStorage();
        Logic logic = createLogic(new ModelManager(), bookStorage);
        logic.execute("aclass n/T05 c/CS2103T");

        logic.execute("eclass 1 n/L01");
        assertEquals("L01", logic.getFilteredCourseClassList().get(0).getName().fullName);

        logic.execute(UndoCommand.COMMAND_WORD);

        assertEquals("T05", logic.getFilteredCourseClassList().get(0).getName().fullName);
    }

    @Test
    public void execute_studentAddition_canBeUndone() throws Exception {
        JsonCourseClassBookStorage bookStorage = createBookStorage();
        Logic logic = createLogic(new ModelManager(), bookStorage);
        logic.execute("aclass n/T05 c/CS2103T");

        logic.execute("add n/John Doe t/birdman c/T05 m/CS2103T");
        assertEquals(1, logic.getFilteredCourseClassList().get(0).getStudentList().size());

        logic.execute(UndoCommand.COMMAND_WORD);

        assertTrue(logic.getFilteredCourseClassList().get(0).getStudentList().isEmpty());
    }

    @Test
    public void execute_noteAddition_canBeUndone() throws Exception {
        JsonCourseClassBookStorage bookStorage = createBookStorage();
        Logic logic = createLogic(new ModelManager(), bookStorage);
        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("add n/John Doe t/birdman c/T05 m/CS2103T");

        logic.execute("note t/birdman c/T05 m/CS2103T o/Needs follow-up");
        assertEquals(1, logic.getFilteredCourseClassList().get(0).getStudentList().get(0)
                .getNotes().size());

        logic.execute(UndoCommand.COMMAND_WORD);

        assertTrue(logic.getFilteredCourseClassList().get(0).getStudentList().get(0)
                .getNotes().isEmpty());
    }

    @Test
    public void execute_noteDeletion_canBeUndone() throws Exception {
        JsonCourseClassBookStorage bookStorage = createBookStorage();
        Logic logic = createLogic(new ModelManager(), bookStorage);
        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("add n/John Doe t/birdman c/T05 m/CS2103T");
        logic.execute("note t/birdman c/T05 m/CS2103T o/Needs follow-up");

        logic.execute("dnote t/birdman c/T05 m/CS2103T i/1");
        assertTrue(logic.getFilteredCourseClassList().get(0).getStudentList().get(0)
                .getNotes().isEmpty());

        logic.execute(UndoCommand.COMMAND_WORD);

        assertEquals("Needs follow-up", logic.getFilteredCourseClassList().get(0).getStudentList().get(0)
                .getNotes().get(0).getText());
    }

    @Test
    public void execute_clearCourseClasses_canBeUndone() throws Exception {
        JsonCourseClassBookStorage bookStorage = createBookStorage();
        Logic logic = createLogic(new ModelManager(), bookStorage);
        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("aclass n/L01 c/CS2101");

        logic.execute("cclass");
        assertTrue(logic.getFilteredCourseClassList().isEmpty());

        logic.execute(UndoCommand.COMMAND_WORD);

        assertEquals(2, logic.getFilteredCourseClassList().size());
    }

    @Test
    public void execute_successfulNoOpChange_doesNotCreateUndoEntry() throws Exception {
        JsonCourseClassBookStorage bookStorage = createBookStorage();
        Logic logic = createLogic(new ModelManager(), bookStorage);
        logic.execute("aclass n/T05 c/CS2103T");

        logic.execute("hwork 1 s/not-set");
        logic.execute(UndoCommand.COMMAND_WORD);

        assertTrue(logic.getFilteredCourseClassList().isEmpty());
        assertThrows(CommandException.class, () -> logic.execute(UndoCommand.COMMAND_WORD));
    }

    @Test
    public void execute_afterRestart_usesPersistedUndoHistory() throws Exception {
        JsonCourseClassBookStorage bookStorage = createBookStorage();
        Logic firstLogic = createLogic(new ModelManager(), bookStorage);
        firstLogic.execute("aclass n/T05 c/CS2103T");
        firstLogic.execute("add n/John Doe t/birdman c/T05 m/CS2103T");
        firstLogic.execute("note t/birdman c/T05 m/CS2103T o/Needs follow-up");
        firstLogic.execute("hwork 1 s/completed");
        firstLogic.execute("aclass n/L01 c/CS2101");
        firstLogic.execute("dclass 2");

        CourseClassBookStorageData savedData = bookStorage.readCourseClassBookData().orElseThrow();
        ModelManager restartedModel = new ModelManager(savedData.getCourseClassBook(), new UserPrefs(),
                savedData.getUndoHistory());
        Logic restartedLogic = createLogic(restartedModel, bookStorage);

        restartedLogic.execute(UndoCommand.COMMAND_WORD);

        assertEquals(2, restartedLogic.getFilteredCourseClassList().size());
        assertEquals(List.of("T05", "L01"), restartedLogic.getFilteredCourseClassList().stream()
                .map(courseClass -> courseClass.getName().fullName).toList());
        assertEquals(1, restartedLogic.getFilteredCourseClassList().get(0).getStudentList().size());
        assertEquals("birdman", restartedLogic.getFilteredCourseClassList().get(0).getStudentList().get(0)
                .getTelehandle().value);
        assertEquals("Needs follow-up", restartedLogic.getFilteredCourseClassList().get(0).getStudentList().get(0)
                .getNotes().get(0).getText());
        assertEquals(HomeworkStatus.COMPLETED,
                restartedLogic.getFilteredCourseClassList().get(0).getHomeworkStatus());

        CourseClassBookStorageData persisted = bookStorage.readCourseClassBookData().orElseThrow();
        assertEquals(List.of("T05", "L01"), persisted.getCourseClassBook().getCourseClassList().stream()
                .map(courseClass -> courseClass.getName().fullName).toList());
        assertEquals(5, persisted.getUndoHistory().size());
    }

    @Test
    public void execute_failedOrCancelledChange_doesNotCreateUndoEntry() throws Exception {
        JsonCourseClassBookStorage bookStorage = createBookStorage();
        Logic logic = createLogic(new ModelManager(), bookStorage);
        logic.execute("aclass n/T05 c/CS2103T");
        logic.execute("add n/John Doe t/birdman c/T05 m/CS2103T");

        assertThrows(CommandException.class, () -> logic.execute("dclass 2"));
        logic.execute("dstudent t/birdman c/T05 m/CS2103T");
        logic.execute("no");

        logic.execute(UndoCommand.COMMAND_WORD);
        assertEquals(1, logic.getFilteredCourseClassList().size());
        assertTrue(logic.getFilteredCourseClassList().get(0).getStudentList().isEmpty());
        logic.execute(UndoCommand.COMMAND_WORD);
        assertTrue(logic.getFilteredCourseClassList().isEmpty());
        assertThrows(CommandException.class, () -> logic.execute(UndoCommand.COMMAND_WORD));
    }

    private JsonCourseClassBookStorage createBookStorage() {
        return new JsonCourseClassBookStorage(temporaryFolder.resolve("classes.json"));
    }

    private Logic createLogic(ModelManager model, JsonCourseClassBookStorage bookStorage) {
        return new LogicManager(model, new StorageManager(bookStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
    }
}
