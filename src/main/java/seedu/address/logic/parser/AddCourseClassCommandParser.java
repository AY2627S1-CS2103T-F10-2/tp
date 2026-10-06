package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCourseClassCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.courseClass.CourseClass;
import seedu.address.model.courseClass.CourseCode;
import seedu.address.model.courseClass.Name;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCourseClassCommand object.
 */
public class AddCourseClassCommandParser implements Parser<AddCourseClassCommand> {

    /**
     * Parses the arguments for an AddCourseClassCommand.
     * @throws ParseException if the user input does not conform to the expected format.
     */
    public AddCourseClassCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CODE, PREFIX_TAG);

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_CODE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddCourseClassCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CODE);
        Name name = ParserUtil.parseCourseClassName(argMultimap.getValue(PREFIX_NAME).get());
        CourseCode code = ParserUtil.parseCourseCode(argMultimap.getValue(PREFIX_CODE).get());
        Set<Tag> tags = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));

        return new AddCourseClassCommand(new CourseClass(name, code, tags));
    }

    /** Returns true if all required prefixes are present. */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
