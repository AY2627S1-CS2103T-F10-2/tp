package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MODULE_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEHANDLE;

import java.util.stream.Stream;

import seedu.address.logic.commands.DeleteStudentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.student.Telehandle;

/** Parses arguments for {@link DeleteStudentCommand}. */
public class DeleteStudentCommandParser implements Parser<DeleteStudentCommand> {

    @Override
    public DeleteStudentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_TELEHANDLE, PREFIX_CLASS_GROUP, PREFIX_MODULE_NAME);

        if (!arePrefixesPresent(argMultimap, PREFIX_TELEHANDLE, PREFIX_CLASS_GROUP, PREFIX_MODULE_NAME)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    DeleteStudentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_TELEHANDLE, PREFIX_CLASS_GROUP, PREFIX_MODULE_NAME);

        Telehandle telehandle = ParserUtil.parseTelehandle(argMultimap.getValue(PREFIX_TELEHANDLE).get());
        ClassGroup classGroup = ParserUtil.parseClassGroup(argMultimap.getValue(PREFIX_CLASS_GROUP).get());
        CourseCode moduleName = ParserUtil.parseCourseCode(argMultimap.getValue(PREFIX_MODULE_NAME).get());

        return new DeleteStudentCommand(telehandle, classGroup, moduleName);
    }

    /** Returns true if every required prefix has a value. */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
