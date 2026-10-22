package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/** Restores the course-class data to the state before the most recent successful change. */
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Undoes the most recent successful data change.\n"
            + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "The most recent change has been undone.";
    public static final String MESSAGE_NO_HISTORY = "There is no change to undo.";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (model.undoCourseClassBook().isEmpty()) {
            throw new CommandException(MESSAGE_NO_HISTORY);
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
