package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 */
public abstract class Command {

    /**
     * Returns whether this command changes the course-class data.
     *
     * <p>Commands that change data override this method so that the logic component can save a snapshot
     * before executing them.</p>
     */
    public boolean isMutating() {
        return false;
    }

    /**
     * Executes the command and returns the result message.
     *
     * @param model the model which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    public abstract CommandResult execute(Model model) throws CommandException;

}
