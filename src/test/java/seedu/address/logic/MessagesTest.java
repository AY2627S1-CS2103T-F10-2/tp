package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.Prefix;

public class MessagesTest {
    @Test
    public void duplicatePrefixes_deduplicatesFieldsAndRequiresAtLeastOnePrefix() {
        Prefix name = new Prefix("n/");
        assertEquals(Messages.MESSAGE_DUPLICATE_FIELDS + "n/",
                Messages.getErrorMessageForDuplicatePrefixes(name, name));
        assertThrows(AssertionError.class, Messages::getErrorMessageForDuplicatePrefixes);
    }
}
