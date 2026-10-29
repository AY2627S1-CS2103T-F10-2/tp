package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCourseClassCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
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
        String trimmedArgs = args.trim();
        int namePosition = findPrefixPosition(trimmedArgs, PREFIX_NAME);
        int codePosition = findPrefixPosition(trimmedArgs, PREFIX_CODE);

        // When the name comes first, parse the code before looking for tags. This keeps tag-like text in the
        // name, such as "Group t/05", from being mistaken for an actual tag.
        if (namePosition == 0 && codePosition > namePosition) {
            return parseNameBeforeCode(trimmedArgs, codePosition);
        }

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

    /** Parses the unambiguous form where the name precedes the course code. */
    private AddCourseClassCommand parseNameBeforeCode(String args, int codePosition) throws ParseException {
        if (countPrefixOccurrences(args, PREFIX_NAME) != 1) {
            throw new ParseException(Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        }
        if (countPrefixOccurrences(args, PREFIX_CODE) != 1) {
            throw new ParseException(Messages.getErrorMessageForDuplicatePrefixes(PREFIX_CODE));
        }

        String nameValue = args.substring(PREFIX_NAME.getPrefix().length(), codePosition).trim();
        ArgumentMultimap codeAndTags = ArgumentTokenizer.tokenize(
                " " + args.substring(codePosition), PREFIX_CODE, PREFIX_TAG);
        codeAndTags.verifyNoDuplicatePrefixesFor(PREFIX_CODE);

        Name name = ParserUtil.parseCourseClassName(nameValue);
        CourseCode code = ParserUtil.parseCourseCode(codeAndTags.getValue(PREFIX_CODE).orElseThrow());
        Set<Tag> tags = ParserUtil.parseTags(codeAndTags.getAllValues(PREFIX_TAG));

        return new AddCourseClassCommand(new CourseClass(name, code, tags));
    }

    /** Returns the position of a prefix at the start of an argument or after whitespace, or -1 if absent. */
    private static int findPrefixPosition(String args, Prefix prefix) {
        String prefixValue = prefix.getPrefix();
        if (args.startsWith(prefixValue)) {
            return 0;
        }
        int position = args.indexOf(" " + prefixValue);
        return position < 0 ? -1 : position + 1;
    }

    /** Returns the number of occurrences of a prefix at argument boundaries. */
    private static int countPrefixOccurrences(String args, Prefix prefix) {
        int count = 0;
        int fromIndex = 0;
        int position;
        while ((position = findPrefixPosition(args.substring(fromIndex), prefix)) >= 0) {
            count++;
            fromIndex += position + prefix.getPrefix().length();
            if (fromIndex >= args.length()) {
                break;
            }
        }
        return count;
    }

    /** Returns true if all required prefixes are present. */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
