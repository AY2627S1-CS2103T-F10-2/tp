package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindStudentCommand;
import seedu.address.model.student.StudentNameContainsKeywordsPredicate;

public class FindStudentCommandParserTest {

    @Test
    public void parse_keywordsSeparatedByWhitespace_success() {
        assertParseSuccess(new FindStudentCommandParser(), " Alex  \t Bernice ",
                new FindStudentCommand(new StudentNameContainsKeywordsPredicate(List.of("Alex", "Bernice"))));
    }

    @Test
    public void parse_blankInput_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindStudentCommand.MESSAGE_USAGE);
        assertParseFailure(new FindStudentCommandParser(), "", expected);
        assertParseFailure(new FindStudentCommandParser(), " \t ", expected);
    }
}
