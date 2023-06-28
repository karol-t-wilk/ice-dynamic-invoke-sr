import Dynamic.Position;

public class PositionFormatter {
    public static String format(Position position) {
        var longTag = "";
        switch (position.longitude.type) {
            case N -> longTag = "N";
            case S -> longTag = "S";
        }
        var latTag = "";
        switch (position.latitude.type) {
            case E -> latTag = "E";
            case W -> latTag = "W";
        }
        return "%s %.5f, %s %.5f".formatted(longTag, position.longitude.value, latTag, position.latitude.value);
    }
}
