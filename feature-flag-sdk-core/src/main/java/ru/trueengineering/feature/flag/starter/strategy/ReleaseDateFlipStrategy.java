package ru.trueengineering.feature.flag.starter.strategy;

import org.springframework.stereotype.Service;
import ru.trueengineering.feature.flag.starter.model.FeatureFlagContext;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Стратегия на основе даты релиза.
 * Открывает фичу после указанной даты
 */
@Service
public class ReleaseDateFlipStrategy implements FeatureFlagStrategy {

    private static final String DATE_PATTERN = "yyyy-MM-dd-HH:mm";

    private static final String RELEASE_DATE_KEY = "releaseDate";

    @Override
    public String getClassName() {
        return "org.ff4j.strategy.time.ReleaseDateFlipStrategy";
    }

    @Override
    public boolean evaluate(FeatureFlagContext executionContext, Map<String, String> initParams) {

        if (initParams == null || initParams.isEmpty()) {
            return true;
        }

        String releaseDateTimeString = initParams.get(RELEASE_DATE_KEY);

        if (releaseDateTimeString == null) {
            return true;
        }

        LocalDateTime releaseDate =
                LocalDateTime.parse(releaseDateTimeString, DateTimeFormatter.ofPattern(DATE_PATTERN));

        return LocalDateTime.now(ZoneOffset.UTC).isAfter(releaseDate);
    }
}
