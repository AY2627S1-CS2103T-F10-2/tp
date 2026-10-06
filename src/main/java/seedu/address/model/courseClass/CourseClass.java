package seedu.address.model.courseClass;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.AddressBook;
import seedu.address.model.tag.Tag;

/**
 * Represents a class in the system.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class CourseClass {

    // Identity fields
    private final Name name;
    private final CourseCode code;

    // Data fields
    private final Set<Tag> tags = new HashSet<>();
    private final AddressBook students;

    /**
     * Every field must be present and not null.
     */
    public CourseClass(Name name, CourseCode code, Set<Tag> tags) {
        requireAllNonNull(name, code, tags);
        this.name = name;
        this.code = code;
        this.tags.addAll(tags);
        this.students = new AddressBook();
    }

    /**
     * Every field must be present and not null.
     */
    public CourseClass(Name name, CourseCode code, Set<Tag> tags, AddressBook students) {
        requireAllNonNull(name, code, tags);
        this.name = name;
        this.code = code;
        this.tags.addAll(tags);
        this.students = students;
    }

    public Name getName() {
        return name;
    }

    public CourseCode getCourseCode() {
        return code;
    }

    public AddressBook getStudents() {
        return students;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSameCourseClass(CourseClass otherCourseClass) {
        if (otherCourseClass == this) {
            return true;
        }

        return otherCourseClass != null
                && otherCourseClass.getName().equals(getName());
    }

    /**
     * Returns true if both course classes have the same identity and data fields.
     * This defines a stronger notion of equality between two course classes.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CourseClass otherCourseClass)) {
            return false;
        }

        return name.equals(otherCourseClass.name)
                && code.equals(otherCourseClass.code)
                && tags.equals(otherCourseClass.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, code, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("code", code)
                .add("tags", tags)
                .toString();
    }

}
