package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;

import seedu.address.logic.commands.FindCourseClassCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.courseclass.NameContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCourseClassCommand object
 */
public class FindCourseClassCommandParser implements Parser<FindCourseClassCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCourseClassCommand
     * and returns a FindCourseClassCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCourseClassCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCourseClassCommand.MESSAGE_USAGE));
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");

        return new FindCourseClassCommand(new NameContainsKeywordsPredicate(List.of(nameKeywords)));
    }

}
