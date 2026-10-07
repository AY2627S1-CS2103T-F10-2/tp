package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteStudentCommand;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.student.Telehandle;

public class DeleteStudentCommandParserTest {

    private final DeleteStudentCommandParser parser = new DeleteStudentCommandParser();

    @Test
    public void parse_validFields_success() {
        DeleteStudentCommand expected = new DeleteStudentCommand(new Telehandle("birdman"),
                new ClassGroup("T05"), new CourseCode("CS2103T"));

        assertParseSuccess(parser, " t/birdman c/T05 m/CS2103T", expected);
        assertParseSuccess(parser, " m/CS2103T c/T05 t/birdman", expected);
    }

    @Test
    public void parse_missingFieldsOrPreamble_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteStudentCommand.MESSAGE_USAGE);

        assertParseFailure(parser, " t/birdman c/T05", expected);
        assertParseFailure(parser, " t/birdman m/CS2103T", expected);
        assertParseFailure(parser, " c/T05 m/CS2103T", expected);
        assertParseFailure(parser, "unexpected t/birdman c/T05 m/CS2103T", expected);
    }

    @Test
    public void parse_duplicateOrInvalidFields_failure() {
        assertParseFailure(parser, " t/birdman t/janedoe c/T05 m/CS2103T",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_TELEHANDLE));
        assertParseFailure(parser, " t/birdman c/T05 c/L14 m/CS2103T",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_CLASS_GROUP));
        assertParseFailure(parser, " t/birdman c/T05 m/CS2103T m/CS2101",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_MODULE_NAME));
        assertParseFailure(parser, " t/abcd c/T05 m/CS2103T", Telehandle.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/birdman c/F10-22 m/CS2103T", ClassGroup.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/birdman c/T05 m/", CourseCode.MESSAGE_CONSTRAINTS);
    }
}
