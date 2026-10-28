package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteNoteCommand;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.student.Telehandle;

public class DeleteNoteCommandParserTest {

    private final DeleteNoteCommandParser parser = new DeleteNoteCommandParser();

    @Test
    public void parse_validFields_success() {
        DeleteNoteCommand expected = new DeleteNoteCommand(new Telehandle("birdman"),
                new ClassGroup("T05"), new CourseCode("CS2103T"), Index.fromOneBased(2));
        DeleteNoteCommand firstNote = new DeleteNoteCommand(new Telehandle("birdman"),
                new ClassGroup("T05"), new CourseCode("CS2103T"), Index.fromOneBased(1));

        assertParseSuccess(parser, " t/birdman c/T05 m/CS2103T i/2", expected);
        assertParseSuccess(parser, " i/2 m/CS2103T c/T05 t/birdman", expected);
        assertParseSuccess(parser, " t/birdman c/T05 m/CS2103T i/1", firstNote);
    }

    @Test
    public void parse_missingFieldsOrPreamble_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteNoteCommand.MESSAGE_USAGE);

        assertParseFailure(parser, " c/T05 m/CS2103T i/2", expected);
        assertParseFailure(parser, " t/birdman m/CS2103T i/2", expected);
        assertParseFailure(parser, " t/birdman c/T05 i/2", expected);
        assertParseFailure(parser, " t/birdman c/T05 m/CS2103T", expected);
        assertParseFailure(parser, "unexpected t/birdman c/T05 m/CS2103T i/2", expected);
    }

    @Test
    public void parse_duplicateFields_failure() {
        assertParseFailure(parser, " t/birdman t/janedoe c/T05 m/CS2103T i/2",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_TELEHANDLE));
        assertParseFailure(parser, " t/birdman c/T05 c/T06 m/CS2103T i/2",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_CLASS_GROUP));
        assertParseFailure(parser, " t/birdman c/T05 m/CS2103T m/CS2101 i/2",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_MODULE_NAME));
        assertParseFailure(parser, " t/birdman c/T05 m/CS2103T i/1 i/2",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_INDEX));
    }

    @Test
    public void parse_invalidFields_failure() {
        assertParseFailure(parser, " t/abcd c/T05 m/CS2103T i/2", Telehandle.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/birdman c/F111! m/CS2103T i/2", ClassGroup.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/birdman c/T05 m/ i/2", CourseCode.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/birdman c/T05 m/CS2103T i/0", ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, " t/birdman c/T05 m/CS2103T i/-1", ParserUtil.MESSAGE_INVALID_INDEX);
        assertParseFailure(parser, " t/birdman c/T05 m/CS2103T i/two", ParserUtil.MESSAGE_INVALID_INDEX);
    }
}
