package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCourseClassCommand;
import seedu.address.logic.commands.EditCourseClassCommand.EditCourseClassDescriptor;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
import seedu.address.model.tag.Tag;

public class EditCourseClassCommandParserTest {
    private final EditCourseClassCommandParser parser = new EditCourseClassCommandParser();

    @Test
    public void parse_allFields_success() {
        EditCourseClassDescriptor descriptor = new EditCourseClassDescriptor();
        descriptor.setName(new Name("F10-3"));
        descriptor.setCourseCode(new CourseCode("CS2101"));
        descriptor.setTags(Set.of(new Tag("tutorial"), new Tag("lab")));
        assertParseSuccess(parser, "1 t/tutorial c/CS2101 n/F10-3 t/lab t/lab",
                new EditCourseClassCommand(Index.fromOneBased(1), descriptor));
    }

    @Test
    public void parse_courseCodeLengthLimit() {
        String code = "C".repeat(50);
        EditCourseClassDescriptor descriptor = new EditCourseClassDescriptor();
        descriptor.setCourseCode(new CourseCode(code));
        assertParseSuccess(parser, "1 c/" + code, new EditCourseClassCommand(Index.fromOneBased(1), descriptor));
        assertParseFailure(parser, "1 c/" + code + "C", ParserUtil.MESSAGE_NEW_COURSE_CODE_CONSTRAINTS);
    }

    @Test
    public void parse_onlyCode_success() {
        EditCourseClassDescriptor descriptor = new EditCourseClassDescriptor();
        descriptor.setCourseCode(new CourseCode("CS2101"));
        assertParseSuccess(parser, "2 c/CS2101", new EditCourseClassCommand(Index.fromOneBased(2), descriptor));
    }

    @Test
    public void parse_emptyTag_clearsTags() {
        EditCourseClassDescriptor descriptor = new EditCourseClassDescriptor();
        descriptor.setTags(Set.of());
        assertParseSuccess(parser, "1 t/", new EditCourseClassCommand(Index.fromOneBased(1), descriptor));
    }

    @Test
    public void parse_invalidIndex_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCourseClassCommand.MESSAGE_USAGE);
        for (String index : new String[] {"", "0", "-1", "abc", "2147483648"}) {
            assertParseFailure(parser, index + " n/F10-2", expected);
        }
    }

    @Test
    public void parse_noFields_failure() {
        assertParseFailure(parser, "1", EditCourseClassCommand.MESSAGE_NOT_EDITED);
    }

    @Test
    public void parse_invalidFields_failure() {
        assertParseFailure(parser, "1 n/", ParserUtil.MESSAGE_COURSE_CLASS_NAME_CONSTRAINTS);
        assertParseFailure(parser, "1 n/F10*2", ParserUtil.MESSAGE_COURSE_CLASS_NAME_CONSTRAINTS);
        assertParseFailure(parser, "1 n/T i/5", ParserUtil.MESSAGE_COURSE_CLASS_NAME_CONSTRAINTS);
        assertParseFailure(parser, "1 n/T o/5", ParserUtil.MESSAGE_COURSE_CLASS_NAME_CONSTRAINTS);
        assertParseFailure(parser, "1 c/", ParserUtil.MESSAGE_NEW_COURSE_CODE_CONSTRAINTS);
        assertParseFailure(parser, "1 c/CS*", ParserUtil.MESSAGE_NEW_COURSE_CODE_CONSTRAINTS);
        assertParseFailure(parser, "1 t/tutorial*", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 t/ t/lab", Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_nameContainsReservedUnrecognizedPrefix_failure() {
        for (String prefix : new String[] {"m/", "i/", "o/"}) {
            assertParseFailure(parser, "1 n/Group " + prefix + "05", ParserUtil.MESSAGE_COURSE_CLASS_NAME_CONSTRAINTS);
        }
    }

    @Test
    public void parse_nameFollowedByTagAndCode_success() {
        EditCourseClassDescriptor descriptor = new EditCourseClassDescriptor();
        descriptor.setName(new Name("Group"));
        descriptor.setTags(Set.of(new Tag("05")));
        descriptor.setCourseCode(new CourseCode("CS2101"));
        assertParseSuccess(parser, "1 n/Group t/05 c/CS2101",
                new EditCourseClassCommand(Index.fromOneBased(1), descriptor));
    }

    @Test
    public void parse_nameContainsPunctuation_success() {
        for (String name : new String[] {"Tutor's group", "F10-2", "L01/L02", "Group-t/05", "Group math/05"}) {
            EditCourseClassDescriptor descriptor = new EditCourseClassDescriptor();
            descriptor.setName(new Name(name));
            assertParseSuccess(parser, "1 n/" + name,
                    new EditCourseClassCommand(Index.fromOneBased(1), descriptor));
        }
    }

    @Test
    public void parse_duplicateFields_failure() {
        assertParseFailure(parser, "1 n/F10-2 n/F10-3",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NAME));
        assertParseFailure(parser, "1 c/CS2103T c/CS2101",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_CODE));
    }
}
