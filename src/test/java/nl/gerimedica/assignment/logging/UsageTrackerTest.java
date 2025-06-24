package nl.gerimedica.assignment.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UsageTrackerTest {

    private UsageTracker usageTracker;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        usageTracker = new UsageTracker();

        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testRecordIncrementsCounterAndLogs() {
        usageTracker.record("Test context A");
        usageTracker.record("Test context B");

        String logOutput = outContent.toString();

        assertTrue(Pattern.compile("Counter: 1.*Context: Test context A").matcher(logOutput).find());
        assertTrue(Pattern.compile("Counter: 2.*Context: Test context B").matcher(logOutput).find());
    }
}
