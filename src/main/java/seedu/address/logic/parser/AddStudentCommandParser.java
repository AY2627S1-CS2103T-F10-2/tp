package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLASS_GROUP;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TELEHANDLE;

import java.util.stream.Stream;

import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;

/** Parses arguments for {@link AddStudentCommand}. */
public class AddStudentCommandParser implements Parser<AddStudentCommand> {

    /**
     * Parses the given arguments and returns an {@code AddStudentCommand}.
     *
     * @param args command arguments
     * @return parsed command
     * @throws ParseException if the arguments are invalid
     */
    @Override
    public AddStudentCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_NAME, PREFIX_TELEHANDLE, PREFIX_CLASS_GROUP);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_TELEHANDLE, PREFIX_CLASS_GROUP)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddStudentCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_TELEHANDLE, PREFIX_CLASS_GROUP);

        StudentName name = ParserUtil.parseStudentName(argMultimap.getValue(PREFIX_NAME).get());
        Telehandle telehandle = ParserUtil.parseTelehandle(argMultimap.getValue(PREFIX_TELEHANDLE).get());
        ClassGroup classGroup = ParserUtil.parseClassGroup(argMultimap.getValue(PREFIX_CLASS_GROUP).get());

        return new AddStudentCommand(new Student(name, telehandle), classGroup);
    }

    /** Returns true if every required prefix has a value. */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
