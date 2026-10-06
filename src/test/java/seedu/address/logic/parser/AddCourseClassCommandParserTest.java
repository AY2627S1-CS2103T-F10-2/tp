package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCourseClassCommand;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
import seedu.address.model.tag.Tag;

public class AddCourseClassCommandParserTest {
    private final AddCourseClassCommandParser parser = new AddCourseClassCommandParser();

    @Test
    public void parse_validFields_success() {
        CourseClass courseClass = new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"),
                Set.of(new Tag("tutorial")));
        assertParseSuccess(parser, " c/CS2103T n/F10-2 t/tutorial t/tutorial",
                new AddCourseClassCommand(courseClass));
        assertParseSuccess(parser, " n/F10-2 c/CS2103T", new AddCourseClassCommand(
                new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"), Set.of())));
    }

    @Test
    public void parse_missingFieldsOrPreamble_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCourseClassCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " n/F10-2", expected);
        assertParseFailure(parser, " c/CS2103T", expected);
        assertParseFailure(parser, "unexpected n/F10-2 c/CS2103T", expected);
    }

    @Test
    public void parse_duplicateOrInvalidFields_failure() {
        assertParseFailure(parser, " n/F10-2 n/F10-3 c/CS2103T",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME));
        assertParseFailure(parser, " n/F10-2 c/CS2103T c/CS2101",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_CODE));
        assertParseFailure(parser, " n/ c/CS2103T", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/F10-2 c/", CourseCode.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/F10-2 c/CS2103T t/invalid*tag", Tag.MESSAGE_CONSTRAINTS);
    }
}
