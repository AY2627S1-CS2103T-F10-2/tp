package seedu.address.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;

public class SampleDataUtilTest {
    @Test
    public void sampleCourseClassBook_containsTutorialAndCreatesIndependentBooks() {
        var book = SampleDataUtil.getSampleCourseClassBook();
        assertEquals(1, book.getCourseClassList().size());
        var courseClass = book.getCourseClassList().get(0);
        assertEquals("F10-2", courseClass.getName().fullName);
        assertEquals("CS2103T", courseClass.getCourseCode().value);
        assertEquals(Set.of(new Tag("tutorial")), courseClass.getTags());
        assertTrue(courseClass.getStudents().getPersonList().isEmpty());
        assertNotSame(book, SampleDataUtil.getSampleCourseClassBook());
    }

    @Test
    public void sampleAddressBook_containsAllSamplePersonsAndDeduplicatesTags() {
        assertEquals(Arrays.asList(SampleDataUtil.getSamplePersons()),
                SampleDataUtil.getSampleAddressBook().getPersonList());
        assertEquals(6, SampleDataUtil.getSamplePersons().length);
        assertEquals(Set.of(new Tag("friends")), SampleDataUtil.getTagSet("friends", "friends"));
        assertEquals(Set.of(), SampleDataUtil.getTagSet());
    }
}
