package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_HOMEWORK_STATUS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEHANDLE;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SetHomeworkStatusCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.Telehandle;

/** Parses arguments for {@link SetHomeworkStatusCommand}. */
public class SetHomeworkStatusCommandParser implements Parser<SetHomeworkStatusCommand> {

    @Override
    public SetHomeworkStatusCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_HOMEWORK_STATUS, PREFIX_TELEHANDLE);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    SetHomeworkStatusCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_HOMEWORK_STATUS, PREFIX_TELEHANDLE);
        if (argMultimap.getValue(PREFIX_HOMEWORK_STATUS).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    SetHomeworkStatusCommand.MESSAGE_USAGE));
        }

        String status = argMultimap.getValue(PREFIX_HOMEWORK_STATUS).get();
        boolean individual = argMultimap.getValue(PREFIX_TELEHANDLE).isPresent();
        if (individual) {
            Telehandle telehandle = ParserUtil.parseTelehandle(argMultimap.getValue(PREFIX_TELEHANDLE).get());
            if (status.trim().equalsIgnoreCase(SetHomeworkStatusCommand.INHERIT_STATUS)) {
                return SetHomeworkStatusCommand.clearStudentOverride(index, telehandle);
            }
            return new SetHomeworkStatusCommand(index, telehandle, ParserUtil.parseHomeworkStatus(status));
        }
        return new SetHomeworkStatusCommand(index, ParserUtil.parseHomeworkStatus(status));
    }
}
