package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HelpWindowTest {

    @Test
    public void userGuideUrl_pointsToProjectUserGuide() {
        assertEquals("https://AY2627S1-CS2103T-F10-2.github.io/tp/UserGuide.html", HelpWindow.USERGUIDE_URL);
    }
}
