package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_HOMEWORK_STATUS;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SetHomeworkStatusCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.courseclass.HomeworkStatus;

/** Parses arguments for {@link SetHomeworkStatusCommand}. */
public class SetHomeworkStatusCommandParser implements Parser<SetHomeworkStatusCommand> {

    @Override
    public SetHomeworkStatusCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_HOMEWORK_STATUS);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    SetHomeworkStatusCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_HOMEWORK_STATUS);
        if (argMultimap.getValue(PREFIX_HOMEWORK_STATUS).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    SetHomeworkStatusCommand.MESSAGE_USAGE));
        }

        HomeworkStatus homeworkStatus = ParserUtil.parseHomeworkStatus(
                argMultimap.getValue(PREFIX_HOMEWORK_STATUS).get());
        return new SetHomeworkStatusCommand(index, homeworkStatus);
    }
}
