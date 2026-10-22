package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteStudentCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.CourseClassBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyCourseClassBook;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;
    private DeleteStudentCommand pendingDeleteStudentCommand;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        if (pendingDeleteStudentCommand != null) {
            return executePendingDeleteConfirmation(commandText);
        }

        return executeCommand(addressBookParser.parseCommand(commandText));
    }

    /**
     * Executes a command and persists the updated course-class book when appropriate.
     */
    private CommandResult executeCommand(Command command) throws CommandException {
        CourseClassBook stateBefore = new CourseClassBook(model.getCourseClassBook());
        List<? extends ReadOnlyCourseClassBook> historyBefore = model.getUndoHistory();
        try {
            CommandResult commandResult = command.execute(model);

            if (command instanceof DeleteStudentCommand deleteStudentCommand
                    && !deleteStudentCommand.isConfirmed()) {
                pendingDeleteStudentCommand = deleteStudentCommand;
                return commandResult;
            }

            if (command.isMutating() && !courseClassBooksEqualForUndo(stateBefore, model.getCourseClassBook())) {
                model.addUndoSnapshot(stateBefore);
            }
            saveCourseClassBook();
            return commandResult;
        } catch (CommandException e) {
            model.setCourseClassBook(stateBefore);
            model.setUndoHistory(historyBefore);
            throw e;
        }
    }

    /**
     * Handles the confirmation response for a pending student deletion.
     */
    private CommandResult executePendingDeleteConfirmation(String commandText) throws CommandException {
        String confirmation = commandText.trim();
        if (confirmation.equalsIgnoreCase("yes")) {
            Command command = pendingDeleteStudentCommand.confirm();
            pendingDeleteStudentCommand = null;
            return executeCommand(command);
        }
        if (confirmation.equalsIgnoreCase("no")) {
            pendingDeleteStudentCommand = null;
            return new CommandResult(DeleteStudentCommand.MESSAGE_CANCELLED);
        }
        throw new CommandException(DeleteStudentCommand.MESSAGE_EXPECTED_CONFIRMATION);
    }

    /**
     * Saves the current course-class book and translates storage failures into command errors.
     */
    private void saveCourseClassBook() throws CommandException {
        try {
            storage.saveCourseClassBookData(model.getCourseClassBook(), model.getUndoHistory());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }
    }

    /** Compares all persisted course-class data, including students omitted by CourseClass#equals. */
    private boolean courseClassBooksEqualForUndo(ReadOnlyCourseClassBook first, ReadOnlyCourseClassBook second) {
        if (first.getCourseClassList().size() != second.getCourseClassList().size()) {
            return false;
        }
        for (int i = 0; i < first.getCourseClassList().size(); i++) {
            CourseClass firstClass = first.getCourseClassList().get(i);
            CourseClass secondClass = second.getCourseClassList().get(i);
            if (!firstClass.equals(secondClass)
                    || !firstClass.getStudentList().equals(secondClass.getStudentList())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ObservableList<CourseClass> getFilteredCourseClassList() {
        return model.getFilteredCourseClassList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
