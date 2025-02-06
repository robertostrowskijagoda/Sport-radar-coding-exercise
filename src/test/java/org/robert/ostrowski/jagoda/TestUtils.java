package org.robert.ostrowski.jagoda;

import lombok.experimental.UtilityClass;

import java.time.Instant;

@UtilityClass
public class TestUtils {

    void waitUntilNanoTimeChanges() {
        long nano = Instant.now().getNano();
        while (nano == Instant.now().getNano());
    }
}
