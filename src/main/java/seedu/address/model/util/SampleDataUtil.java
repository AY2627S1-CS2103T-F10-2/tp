package seedu.address.model.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.address.model.tag.Tag;

/**
 * Contains utility methods for populating the application with sample data.
 */
public class SampleDataUtil {
    private SampleDataUtil() {}

    /** Returns a sample course class book for a new installation. */
    public static seedu.address.model.ReadOnlyCourseClassBook getSampleCourseClassBook() {
        seedu.address.model.CourseClassBook book = new seedu.address.model.CourseClassBook();
        book.addCourseClass(new seedu.address.model.courseclass.CourseClass(
                new seedu.address.model.courseclass.Name("F10-2"),
                new seedu.address.model.courseclass.CourseCode("CS2103T"), Set.of(new Tag("tutorial"))));
        return book;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
