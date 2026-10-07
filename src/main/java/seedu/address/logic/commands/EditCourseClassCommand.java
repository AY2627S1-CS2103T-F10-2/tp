package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CODE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_COURSE_CLASSES;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
import seedu.address.model.tag.Tag;

/**
 * Edits the details of an existing course class in the course class book.
 */
public class EditCourseClassCommand extends Command {

    public static final String COMMAND_WORD = "eclass";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the course class identified "
            + "by the index number used in the displayed course class list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_CODE + "COURSE_CODE] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_CODE + "CS2103T";

    public static final String MESSAGE_EDIT_COURSE_CLASS_SUCCESS = "Edited course class: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_COURSE_CLASS =
            "This course class already exists in the course class book.";

    private final Index index;
    private final EditCourseClassDescriptor editCourseClassDescriptor;

    /**
     * @param index of the course class in the filtered course class list to edit
     * @param editCourseClassDescriptor details to edit the course class with
     */
    public EditCourseClassCommand(Index index, EditCourseClassDescriptor editCourseClassDescriptor) {
        requireNonNull(index);
        requireNonNull(editCourseClassDescriptor);

        this.index = index;
        this.editCourseClassDescriptor = new EditCourseClassDescriptor(editCourseClassDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<CourseClass> lastShownList = model.getFilteredCourseClassList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_COURSE_CLASS_DISPLAYED_INDEX);
        }

        CourseClass courseClassToEdit = lastShownList.get(index.getZeroBased());
        CourseClass editedCourseClass = createEditedCourseClass(courseClassToEdit, editCourseClassDescriptor);

        if (!courseClassToEdit.isSameCourseClass(editedCourseClass) && model.hasCourseClass(editedCourseClass)) {
            throw new CommandException(MESSAGE_DUPLICATE_COURSE_CLASS);
        }

        model.setCourseClass(courseClassToEdit, editedCourseClass);
        model.updateFilteredCourseClassList(PREDICATE_SHOW_ALL_COURSE_CLASSES);
        return new CommandResult(String.format(MESSAGE_EDIT_COURSE_CLASS_SUCCESS, Messages.format(editedCourseClass)));
    }

    /**
     * Creates and returns a {@code CourseClass} with the details of {@code courseClassToEdit}
     * edited with {@code editCourseClassDescriptor}.
     */
    private static CourseClass createEditedCourseClass(CourseClass courseClassToEdit,
            EditCourseClassDescriptor editCourseClassDescriptor) {
        requireNonNull(courseClassToEdit);

        Name updatedName = editCourseClassDescriptor.getName().orElse(courseClassToEdit.getName());
        CourseCode updatedCourseCode =
                editCourseClassDescriptor.getCourseCode().orElse(courseClassToEdit.getCourseCode());
        Set<Tag> updatedTags = editCourseClassDescriptor.getTags().orElse(courseClassToEdit.getTags());

        return new CourseClass(updatedName, updatedCourseCode, updatedTags, courseClassToEdit.getStudents(),
                courseClassToEdit.getStudentList());

    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCourseClassCommand otherEditCourseClassCommand)) {
            return false;
        }

        return index.equals(otherEditCourseClassCommand.index)
                && editCourseClassDescriptor.equals(otherEditCourseClassCommand.editCourseClassDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editCourseClassDescriptor", editCourseClassDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the course class with. Each non-empty field value will replace the
     * corresponding field value of the course class.
     */
    public static class EditCourseClassDescriptor {
        private Name name;
        private CourseCode courseCode;
        private Set<Tag> tags;

        public EditCourseClassDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditCourseClassDescriptor(EditCourseClassDescriptor toCopy) {
            setName(toCopy.name);
            setCourseCode(toCopy.courseCode);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, courseCode, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setCourseCode(CourseCode courseCode) {
            this.courseCode = courseCode;
        }

        public Optional<CourseCode> getCourseCode() {
            return Optional.ofNullable(courseCode);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditCourseClassDescriptor otherEditCourseClassDescriptor)) {
                return false;
            }

            return Objects.equals(name, otherEditCourseClassDescriptor.name)
                    && Objects.equals(courseCode, otherEditCourseClassDescriptor.courseCode)
                    && Objects.equals(tags, otherEditCourseClassDescriptor.tags);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("courseCode", courseCode)
                    .add("tags", tags)
                    .toString();
        }
    }
}
