package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCourseClassCommand;
import seedu.address.logic.commands.FindCourseClassCommand;
import seedu.address.model.courseclass.NameContainsKeywordsPredicate;

public class CourseClassIndexAndFindParserTest {
    @Test
    public void delete_validIndex_success() {
        assertParseSuccess(new DeleteCourseClassCommandParser(), " 2 ",
                new DeleteCourseClassCommand(Index.fromOneBased(2)));
    }

    @Test
    public void delete_invalidIndex_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCourseClassCommand.MESSAGE_USAGE);
        for (String index : new String[] {"", "0", "-1", "abc", "1 2", "2147483648"}) {
            assertParseFailure(new DeleteCourseClassCommandParser(), index, expected);
        }
    }

    @Test
    public void find_keywordsSeparatedByWhitespace_success() {
        assertParseSuccess(new FindCourseClassCommandParser(), "  F10-2 \t F10-3 \n",
                new FindCourseClassCommand(new NameContainsKeywordsPredicate(List.of("F10-2", "F10-3"))));
    }

    @Test
    public void find_blankInput_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCourseClassCommand.MESSAGE_USAGE);
        assertParseFailure(new FindCourseClassCommandParser(), "", expected);
        assertParseFailure(new FindCourseClassCommandParser(), " \t ", expected);
    }
}
