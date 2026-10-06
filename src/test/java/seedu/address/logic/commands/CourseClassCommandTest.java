package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCourseClassCommand.EditCourseClassDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
import seedu.address.model.tag.Tag;

public class CourseClassCommandTest {
    private final Model model = new ModelManager();
    private final CourseClass first = new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"),
            Set.of(new Tag("tutorial")));
    private final CourseClass second = new CourseClass(new Name("F10-3"), new CourseCode("CS2103T"), Set.of());

    @Test
    public void add_duplicateHiddenByFilter_preservesDataAndFilter() {
        model.addCourseClass(first);
        model.updateFilteredCourseClassList(unused -> false);
        CourseClass duplicate = new CourseClass(first.getName(), new CourseCode("CS2101"), Set.of());
        CommandException error = assertThrows(CommandException.class, () ->
                new AddCourseClassCommand(duplicate).execute(model));
        assertEquals(AddCourseClassCommand.MESSAGE_DUPLICATE_CLASS, error.getMessage());
        assertEquals(List.of(first), model.getCourseClassBook().getCourseClassList());
        assertEquals(List.of(), model.getFilteredCourseClassList());
    }

    @Test
    public void edit_duplicateHiddenByFilter_preservesDataAndFilter() {
        model.addCourseClass(first);
        model.addCourseClass(second);
        model.updateFilteredCourseClassList(second::equals);
        EditCourseClassDescriptor descriptor = new EditCourseClassDescriptor();
        descriptor.setName(first.getName());
        CommandException error = assertThrows(CommandException.class, () ->
                new EditCourseClassCommand(Index.fromOneBased(1), descriptor).execute(model));
        assertEquals(EditCourseClassCommand.MESSAGE_DUPLICATE_COURSE_CLASS, error.getMessage());
        assertEquals(List.of(first, second), model.getCourseClassBook().getCourseClassList());
        assertEquals(List.of(second), model.getFilteredCourseClassList());
    }

    @Test
    public void delete_invalidDisplayedIndex_preservesDataAndFilter() {
        model.addCourseClass(first);
        model.addCourseClass(second);
        model.updateFilteredCourseClassList(first::equals);
        assertThrows(CommandException.class, () ->
                new DeleteCourseClassCommand(Index.fromOneBased(2)).execute(model));
        assertEquals(List.of(first, second), model.getCourseClassBook().getCourseClassList());
        assertEquals(List.of(first), model.getFilteredCourseClassList());
    }

    @Test
    public void edit_codeOnly_preservesNameTagsAndStudentBook() throws Exception {
        model.addCourseClass(first);
        EditCourseClassDescriptor descriptor = new EditCourseClassDescriptor();
        descriptor.setCourseCode(new CourseCode("CS2101"));
        new EditCourseClassCommand(Index.fromOneBased(1), descriptor).execute(model);
        CourseClass edited = model.getFilteredCourseClassList().get(0);
        assertEquals(first.getName(), edited.getName());
        assertEquals(first.getTags(), edited.getTags());
        assertEquals(new CourseCode("CS2101"), edited.getCourseCode());
        assertSame(first.getStudents(), edited.getStudents());
    }

    @Test
    public void edit_descriptorIsCopiedAndTagsAreUnmodifiable() throws Exception {
        model.addCourseClass(first);
        EditCourseClassDescriptor descriptor = new EditCourseClassDescriptor();
        Set<Tag> tags = new HashSet<>(Set.of(new Tag("lab")));
        descriptor.setTags(tags);
        EditCourseClassCommand command = new EditCourseClassCommand(Index.fromOneBased(1), descriptor);
        tags.clear();
        descriptor.setTags(Set.of(new Tag("changed")));
        command.execute(model);
        assertEquals(Set.of(new Tag("lab")), model.getFilteredCourseClassList().get(0).getTags());
        assertThrows(UnsupportedOperationException.class, () -> descriptor.getTags().orElseThrow().clear());
    }
}
