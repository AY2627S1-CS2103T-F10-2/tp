package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;

import seedu.address.logic.commands.FindStudentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.StudentNameContainsKeywordsPredicate;

/** Parses arguments for {@link FindStudentCommand}. */
public class FindStudentCommandParser implements Parser<FindStudentCommand> {

    @Override
    public FindStudentCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FindStudentCommand.MESSAGE_USAGE));
        }

        String[] nameKeywords = trimmedArgs.split("\\s+");
        return new FindStudentCommand(new StudentNameContainsKeywordsPredicate(List.of(nameKeywords)));
    }
}
