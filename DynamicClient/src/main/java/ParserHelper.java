import Dynamic.*;

import java.util.Calendar;
import java.util.Optional;

public class ParserHelper {
    public static Optional<Latitude> latitude(String typeTag, String value) {
        Optional<LatitudeType> type = Optional.empty();
        switch (typeTag) {
            case "E" -> type = Optional.of(LatitudeType.E);
            case "W" -> type = Optional.of(LatitudeType.W);
            default -> {}
        }
        if (type.isEmpty()) {
            return Optional.empty();
        }
        try {
            var floatVal = Float.parseFloat(value);
            return Optional.of(new Latitude(type.get(), floatVal));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    public static Optional<Longitude> longitude(String typeTag, String value) {
        Optional<LongitudeType> type = Optional.empty();
        switch (typeTag) {
            case "N" -> type = Optional.of(LongitudeType.N);
            case "S" -> type = Optional.of(LongitudeType.S);
            default -> {}
        }
        if (type.isEmpty()) {
            return Optional.empty();
        }
        try {
            var floatVal = Float.parseFloat(value);
            return Optional.of(new Longitude(type.get(), floatVal));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    public static Optional<Reading> reading(String typeTag, String value) {
        Optional<ReadingType> type = Optional.empty();
        switch (typeTag) {
            case "temperature" -> type = Optional.of(ReadingType.Temperature);
            case "humidity" -> type = Optional.of(ReadingType.Humidity);
            case "airQuality" -> type = Optional.of(ReadingType.AirQuality);
            default -> {}
        }
        if (type.isEmpty()) {
            return Optional.empty();
        }
        try {
            var timestamp = Calendar.getInstance().getTimeInMillis();
            var floatVal = Float.parseFloat(value);
            return Optional.of(new Reading(type.get(), floatVal, timestamp));
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}
