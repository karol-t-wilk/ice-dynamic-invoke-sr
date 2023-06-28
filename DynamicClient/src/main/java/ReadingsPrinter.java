import Dynamic.Reading;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadingsPrinter {
    static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static void print(Reading[] readings) {
        System.out.printf("Found %d readings:%n", readings.length);
        for (var reading : readings) {
            String type = "";
            switch (reading.type) {
                case Temperature -> type = "temperature";
                case Humidity -> type = "humidity";
                case AirQuality -> type = "air quality";
            }
            var value = reading.value;
            var time = new Date(reading.timestamp);

            System.out.printf("  - %s: %s, value: %f%n", formatter.format(time), type, value);
        }
    }
}
