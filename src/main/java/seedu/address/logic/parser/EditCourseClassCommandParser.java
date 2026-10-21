package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCourseClassCommand;
import seedu.address.logic.commands.EditCourseClassCommand.EditCourseClassDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditCourseClassCommand object
 */
public class EditCourseClassCommandParser implements Parser<EditCourseClassCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the EditCourseClassCommand
     * and returns an EditCourseClassCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditCourseClassCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_CODE, PREFIX_TAG);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    EditCourseClassCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_CODE);

        EditCourseClassDescriptor editCourseClassDescriptor = new EditCourseClassDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editCourseClassDescriptor.setName(ParserUtil.parseCourseClassName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_CODE).isPresent()) {
            editCourseClassDescriptor.setCourseCode(
                    ParserUtil.parseCourseCode(argMultimap.getValue(PREFIX_CODE).get()));
        }
        ParserUtil.parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG))
                .ifPresent(editCourseClassDescriptor::setTags);

        if (!editCourseClassDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCourseClassCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCourseClassCommand(index, editCourseClassDescriptor);
    }

}
