package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;

public class AddStudentCommandParserTest {

    private final AddStudentCommandParser parser = new AddStudentCommandParser();

    @Test
    public void parse_validFields_success() {
        AddStudentCommand expected = new AddStudentCommand(
                new Student(new StudentName("John Doe"), new Telehandle("birdman")),
                new ClassGroup("T05"), new CourseCode("CS2103T"));

        assertParseSuccess(parser, " n/John Doe t/birdman c/T05 m/CS2103T", expected);
        assertParseSuccess(parser, " c/T05 t/birdman m/CS2103T n/John Doe", expected);
        assertParseSuccess(parser, " n/John Doe t/birdman c/F10-2 m/CS2103T",
                new AddStudentCommand(
                        new Student(new StudentName("John Doe"), new Telehandle("birdman")),
                        new ClassGroup("F10-2"), new CourseCode("CS2103T")));
    }

    @Test
    public void parse_missingFieldsOrPreamble_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddStudentCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " n/John Doe t/birdman c/T05", expected);
        assertParseFailure(parser, " n/John Doe c/T05 m/CS2103T", expected);
        assertParseFailure(parser, " t/birdman c/T05 m/CS2103T", expected);
        assertParseFailure(parser, "unexpected n/John Doe t/birdman c/T05 m/CS2103T", expected);
    }

    @Test
    public void parse_duplicateOrInvalidFields_failure() {
        assertParseFailure(parser, " n/John Doe n/Jane Doe t/birdman c/T05 m/CS2103T",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME));
        assertParseFailure(parser, " n/John Doe t/birdman t/janedoe c/T05 m/CS2103T",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_TELEHANDLE));
        assertParseFailure(parser, " n/John Doe t/birdman c/T05 c/L14 m/CS2103T",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_CLASS_GROUP));
        assertParseFailure(parser, " n/John Doe t/birdman c/T05 m/CS2103T m/CS2101",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_MODULE_NAME));
        assertParseFailure(parser, " n/John2 t/birdman c/T05 m/CS2103T", StudentName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/John Doe t/abcd c/T05 m/CS2103T", Telehandle.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/John Doe t/birdman c/F10-22 m/CS2103T", ClassGroup.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/John Doe t/birdman c/T05 m/", CourseCode.MESSAGE_CONSTRAINTS);
    }
}
