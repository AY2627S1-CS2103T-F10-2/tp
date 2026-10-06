package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.CourseClassModel;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;

public class AddCourseClassCommandIntegrationTest {
    @Test
    public void execute_parsedCommand_addsClassAndRejectsDuplicate() throws Exception {
        Model model = new ModelManager();
        Command command = new AddressBookParser().parseCommand("aclass n/F10-2 c/CS2103T t/tutorial");
        command.execute(model);

        CourseClassModel courseClassModel = model.getCourseClassModel();
        assertEquals(1, courseClassModel.getCourseClassBook().getCourseClassList().size());
        assertEquals("F10-2", courseClassModel.getFilteredCourseClassList().get(0).getName().fullName);
        CommandException exception = assertThrows(CommandException.class, () -> command.execute(model));
        assertEquals(AddCourseClassCommand.MESSAGE_DUPLICATE_CLASS, exception.getMessage());
        assertTrue(model.getAddressBook().getPersonList().isEmpty());
    }
}
