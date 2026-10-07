package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;

public class AddStudentCommandIntegrationTest {

    @Test
    public void execute_parsedCommand_addsStudentAndRejectsDuplicate() throws Exception {
        Model model = new ModelManager();
        model.addCourseClass(new CourseClass(new Name("T05"), new CourseCode("CS2103T"), Set.of()));

        Command command = new AddressBookParser().parseCommand("add n/John Doe t/birdman c/T05 m/CS2103T");
        command.execute(model);

        assertEquals(1, model.findCourseClassByGroup(
                new seedu.address.model.courseclass.ClassGroup("T05"), new CourseCode("CS2103T"))
                .orElseThrow().getStudentList().size());
        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_nonExistentClass_throwsCommandException() throws Exception {
        Model model = new ModelManager();
        Command command = new AddressBookParser().parseCommand("add n/John Doe t/birdman c/T05 m/CS2103T");

        assertThrows(CommandException.class, () -> command.execute(model));
    }

    @Test
    public void execute_sameGroupInDifferentModules_addsToRequestedClass() throws Exception {
        Model model = new ModelManager();
        model.addCourseClass(new CourseClass(new Name("T05"), new CourseCode("CS2103T"), Set.of()));
        model.addCourseClass(new CourseClass(new Name("T05"), new CourseCode("CS2101"), Set.of()));

        Command command = new AddressBookParser().parseCommand(
                "add n/John Doe t/birdman c/T05 m/CS2101");
        command.execute(model);

        assertEquals(0, model.findCourseClassByGroup(new seedu.address.model.courseclass.ClassGroup("T05"),
                new CourseCode("CS2103T")).orElseThrow().getStudentList().size());
        assertEquals(1, model.findCourseClassByGroup(new seedu.address.model.courseclass.ClassGroup("T05"),
                new CourseCode("CS2101")).orElseThrow().getStudentList().size());
    }
}
