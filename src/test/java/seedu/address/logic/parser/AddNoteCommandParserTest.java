package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddNoteCommand;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.student.Note;
import seedu.address.model.student.Telehandle;

public class AddNoteCommandParserTest {

    private final AddNoteCommandParser parser = new AddNoteCommandParser();

    @Test
    public void parse_validFields_success() {
        AddNoteCommand expected = new AddNoteCommand(
                new Telehandle("birdman"), new ClassGroup("T05"),
                new CourseCode("CS2103T"), "Needs follow-up");

        assertParseSuccess(parser, " t/birdman c/T05 m/CS2103T r/Needs follow-up", expected);
        assertParseSuccess(parser, " r/Needs follow-up m/CS2103T t/birdman c/T05", expected);
        assertParseSuccess(parser, " t/birdman c/T05 m/CS2103T r/  Needs follow-up  ", expected);
    }

    @Test
    public void parse_missingFieldsOrPreamble_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddNoteCommand.MESSAGE_USAGE);

        assertParseFailure(parser, " c/T05 m/CS2103T r/Needs follow-up", expected);
        assertParseFailure(parser, " t/birdman m/CS2103T r/Needs follow-up", expected);
        assertParseFailure(parser, " t/birdman c/T05 r/Needs follow-up", expected);
        assertParseFailure(parser, " t/birdman c/T05 m/CS2103T", expected);
        assertParseFailure(parser, "unexpected t/birdman c/T05 m/CS2103T r/Needs follow-up", expected);
    }

    @Test
    public void parse_duplicateFields_failure() {
        assertParseFailure(parser, " t/birdman t/janedoe c/T05 m/CS2103T r/Needs follow-up",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_TELEHANDLE));
        assertParseFailure(parser, " t/birdman c/T05 c/T06 m/CS2103T r/Needs follow-up",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_CLASS_GROUP));
        assertParseFailure(parser, " t/birdman c/T05 m/CS2103T m/CS2101 r/Needs follow-up",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_MODULE_NAME));
        assertParseFailure(parser, " t/birdman c/T05 m/CS2103T r/First r/Second",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NOTE));
    }

    @Test
    public void parse_invalidFields_failure() {
        assertParseFailure(parser, " t/abcd c/T05 m/CS2103T r/Needs follow-up", Telehandle.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/birdman c/F10-22 m/CS2103T r/Needs follow-up",
                ClassGroup.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/birdman c/T05 m/ r/Needs follow-up", CourseCode.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " t/birdman c/T05 m/CS2103T r/   ", Note.MESSAGE_CONSTRAINTS);
    }
}
