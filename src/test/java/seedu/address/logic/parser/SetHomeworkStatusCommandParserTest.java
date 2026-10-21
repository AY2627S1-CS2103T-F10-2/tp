package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.SetHomeworkStatusCommand;
import seedu.address.model.courseclass.HomeworkStatus;

public class SetHomeworkStatusCommandParserTest {
    private final SetHomeworkStatusCommandParser parser = new SetHomeworkStatusCommandParser();

    @Test
    public void parse_validStatus_success() {
        assertParseSuccess(parser, "1 s/completed",
                new SetHomeworkStatusCommand(Index.fromOneBased(1), HomeworkStatus.COMPLETED));
        assertParseSuccess(parser, "2 s/ PENDING ",
                new SetHomeworkStatusCommand(Index.fromOneBased(2), HomeworkStatus.PENDING));
    }

    @Test
    public void parse_invalidIndex_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SetHomeworkStatusCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "0 s/pending", expected);
        assertParseFailure(parser, "abc s/pending", expected);
        assertParseFailure(parser, "1 2 s/pending", expected);
    }

    @Test
    public void parse_invalidOrMissingStatus_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, SetHomeworkStatusCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "1", expected);
        assertParseFailure(parser, "1 s/unknown", HomeworkStatus.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateStatus_failure() {
        assertParseFailure(parser, "1 s/pending s/completed",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_HOMEWORK_STATUS));
    }
}
