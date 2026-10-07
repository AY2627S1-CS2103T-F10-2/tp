package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCourseClassCommand;
import seedu.address.logic.commands.AddStudentCommand;
import seedu.address.logic.commands.ClearCourseClassCommand;
import seedu.address.logic.commands.DeleteCourseClassCommand;
import seedu.address.logic.commands.DeleteStudentCommand;
import seedu.address.logic.commands.EditCourseClassCommand;
import seedu.address.logic.commands.EditCourseClassCommand.EditCourseClassDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCourseClassCommand;
import seedu.address.logic.commands.FindStudentCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCourseClassCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.courseclass.ClassGroup;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
import seedu.address.model.courseclass.NameContainsKeywordsPredicate;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.StudentNameContainsKeywordsPredicate;
import seedu.address.model.student.Telehandle;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        CourseClass courseClass =
                new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"), Set.of());
        AddCourseClassCommand command = (AddCourseClassCommand) parser.parseCommand("aclass n/F10-2 c/CS2103T");
        assertEquals(new AddCourseClassCommand(courseClass), command);
    }

    @Test
    public void parseCommand_addStudent() throws Exception {
        AddStudentCommand expected = new AddStudentCommand(
                new Student(new StudentName("John Doe"), new Telehandle("birdman")),
                new ClassGroup("T05"), new CourseCode("CS2103T"));

        assertEquals(expected, parser.parseCommand("add n/John Doe t/birdman c/T05 m/CS2103T"));
    }

    @Test
    public void parseCommand_deleteStudent() throws Exception {
        DeleteStudentCommand expected = new DeleteStudentCommand(
                new Telehandle("birdman"), new ClassGroup("T05"), new CourseCode("CS2103T"));

        assertEquals(expected, parser.parseCommand("dstudent t/birdman c/T05 m/CS2103T"));
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCourseClassCommand.COMMAND_WORD) instanceof ClearCourseClassCommand);
        assertTrue(parser.parseCommand(ClearCourseClassCommand.COMMAND_WORD + " 3") instanceof ClearCourseClassCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCourseClassCommand command = (DeleteCourseClassCommand) parser.parseCommand(
                DeleteCourseClassCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCourseClassCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        CourseClass courseClass =
                new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"), Set.of());
        EditCourseClassDescriptor descriptor = new EditCourseClassDescriptor();
        descriptor.setName(courseClass.getName());
        descriptor.setCourseCode(courseClass.getCourseCode());
        EditCourseClassCommand command = (EditCourseClassCommand) parser.parseCommand(
                EditCourseClassCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + "n/F10-2 c/CS2103T");
        assertEquals(new EditCourseClassCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCourseClassCommand command = (FindCourseClassCommand) parser.parseCommand(
                FindCourseClassCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCourseClassCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_findStudent() throws Exception {
        List<String> keywords = List.of("Alex", "Bernice");
        FindStudentCommand command = (FindStudentCommand) parser.parseCommand(
                FindStudentCommand.COMMAND_WORD + " " + String.join(" ", keywords));
        assertEquals(new FindStudentCommand(new StudentNameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCourseClassCommand.COMMAND_WORD) instanceof ListCourseClassCommand);
        assertTrue(parser.parseCommand(ListCourseClassCommand.COMMAND_WORD + " 3") instanceof ListCourseClassCommand);
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }

    @Test
    public void parseCommand_unprefixedClassCommands_throwsParseException() {
        for (String command : List.of("delete", "clear", "edit", "list", "find")) {
            assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand(command));
        }
    }
}
