import Dynamic.*;
import com.zeroc.Ice.*;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (var communicator = Util.initialize()) {
            var base = communicator.stringToProxy("Dynamic/stations:tcp -h 127.0.0.2 -p 10000 -z : udp -h 127.0.0.2 -p 10000 -z");
            var proxy = StationsPrx.checkedCast(base);
            if (proxy == null) {
                System.err.println("Invalid proxy!");
                return;
            }

            System.out.println("Operations:");
            System.out.println("tryEmptyName");
            System.out.println("addStation <name> <N|S> <longitude> <E|W> <latitude>");
            System.out.println("addReadings <name> <temperature|humidity|airQuality> <value> [<temperature|humidity|airQuality> <value2> ...]");
            System.out.println("getReadings <name>");
            System.out.println("getPosition <name>");
            System.out.println("getStationList # this one is not in the interface!");
            System.out.println("exit");

            main: while (true) {
                var scanner = new Scanner(System.in);
                System.out.print("> ");
                var command = Arrays.stream(scanner.nextLine().split("\\s+"))
                        .filter(s -> !s.isEmpty())
                        .toArray(String[]::new);

                if (command.length == 0) {
                    System.out.println("Empty command!");
                    continue;
                }
                switch (command[0]) {
                    case "exit" -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    case "tryEmptyName" -> addStation(communicator, proxy, "", new Position(
                                new Longitude(LongitudeType.N, 15.26f),
                                new Latitude(LatitudeType.E, 17.52f)));
                    case "addStation" -> parseAddStationCommand(command)
                                .ifPresent(p -> addStation(communicator, proxy, p.getValue0(), p.getValue1()));
                    case "addReadings" -> parseAddReadingsCommand(command)
                                .ifPresent(p -> addReadings(communicator, proxy, p.getValue0(), p.getValue1()));
                    case "getReadings" -> parseGetReadingsOrPositionCommand(command)
                                .ifPresent(p -> getReadings(communicator, proxy, p));
                    case "getPosition" -> parseGetReadingsOrPositionCommand(command)
                                .ifPresent(p -> getPosition(communicator, proxy, p));
                    case "getStationList" -> getStationList(communicator, proxy);
                    default -> System.out.println("Unrecognized command.");
                }
            }
        }
    }

    private static Optional<Pair<String, Position>> parseAddStationCommand(String[] command) {
        if (command.length != 6) {
            System.out.println("Wrong number of command arguments.");
            return Optional.empty();
        }
        var name = command[1];
        var longitude = ParserHelper.longitude(command[2], command[3]);
        var latitude = ParserHelper.latitude(command[4], command[5]);
        if (longitude.isEmpty() || latitude.isEmpty()) {
            System.out.println("Could not parse longitude and latitude.");
            return Optional.empty();
        }
        var position = new Position(longitude.get(), latitude.get());
        return Optional.of(Pair.with(name, position));
    }
    private static void addStation(Communicator communicator, StationsPrx proxy, String name, Position position) {
        var out = new OutputStream(communicator);
        out.startEncapsulation();
        out.writeString(name);
        Position.ice_write(out, position);
        out.endEncapsulation();
        var res = proxy.ice_invoke("addStation", OperationMode.Normal, out.finished());
        if (res.returnValue) {
            System.out.println("Operation successful!");
        } else {
            var in = new InputStream(communicator, res.outParams);
            in.startEncapsulation();
            try {
                in.throwException(new AddStationExceptionFactory());
            } catch (StationNameEmpty e) {
                System.out.println("Station name is empty");
            } catch (StationAlreadyExists e) {
                System.out.printf("Station with name %s already exists!%n", name);
            } catch (UserException e) {
                throw new RuntimeException(e);
            } finally {
                in.endEncapsulation();
            }
        }
    }

    private static Optional<Pair<String, Reading[]>> parseAddReadingsCommand(String[] command) {
        if (command.length % 2 != 0) {
            System.out.println("Wrong number of command arguments.");
            return Optional.empty();
        }
        var name = command[1];
        var readings = new ArrayList<Reading>();
        for (int i = 2; i + 1 < command.length; i += 2) {
            var reading = ParserHelper.reading(command[i], command[i + 1]);
            if (reading.isEmpty()) {
                System.out.println("Could not parse reading.");
                return Optional.empty();
            }
            readings.add(reading.get());
        }

        return Optional.of(Pair.with(name, readings.toArray(Reading[]::new)));
    }

    private static void addReadings(Communicator communicator, StationsPrx proxy, String name, Reading[] readings) {
        var out = new OutputStream(communicator);
        out.startEncapsulation();
        out.writeString(name);
        ReadingsHelper.write(out, readings);
        out.endEncapsulation();
        var res = proxy.ice_invoke("addReadings", OperationMode.Normal, out.finished());
        if (res.returnValue) {
            System.out.println("Operation successful!");
        } else {
            var in = new InputStream(communicator, res.outParams);
            in.startEncapsulation();
            try {
                in.throwException(new AddReadingGetReadingsExceptionFactory());
            } catch (StationNotFound e) {
                System.out.printf("Station with name %s not found.%n", name);
            } catch (UserException e) {
                throw new RuntimeException(e);
            } finally {
                in.endEncapsulation();
            }
        }
    }

    private static Optional<String> parseGetReadingsOrPositionCommand(String[] command) {
        if (command.length != 2) {
            System.out.println("Wrong number of command arguments.");
            return Optional.empty();
        }

        var name = command[1];

        return Optional.of(name);
    }

    private static void getReadings(Communicator communicator, StationsPrx proxy, String name) {
        var out = new OutputStream(communicator);
        out.startEncapsulation();
        out.writeString(name);
        out.endEncapsulation();

        var res = proxy.ice_invoke("getReadings", OperationMode.Idempotent, out.finished());

        var in = new InputStream(communicator, res.outParams);
        in.startEncapsulation();
        if (res.returnValue) {
            var readings = ReadingsHelper.read(in);

            in.endEncapsulation();
            ReadingsPrinter.print(readings);
        } else {
            try {
                in.throwException(new AddReadingGetReadingsExceptionFactory());
            } catch (StationNotFound e) {
                System.out.printf("Station with name %s not found.%n", name);
            } catch (UserException e) {
                throw new RuntimeException(e);
            } finally {
                in.endEncapsulation();
            }
        }
    }

    private static void getPosition(Communicator communicator, StationsPrx proxy, String name) {
        var out = new OutputStream(communicator);
        out.startEncapsulation();
        out.writeString(name);
        out.endEncapsulation();

        var res = proxy.ice_invoke("getPosition", OperationMode.Idempotent, out.finished());

        var in = new InputStream(communicator, res.outParams);
        in.startEncapsulation();
        if (res.returnValue) {
            var position = Position.ice_read(in);
            in.endEncapsulation();
            System.out.printf("station position is %s%n", PositionFormatter.format(position));
        } else {
            try {
                in.throwException(new AddReadingGetReadingsExceptionFactory());
            } catch (StationNotFound e) {
                System.out.printf("Station with name %s not found.%n", name);
            } catch (UserException e) {
                throw new RuntimeException(e);
            } finally {
                in.endEncapsulation();
            }
        }
    }

    private static void getStationList(Communicator communicator, StationsPrx proxy) {
        var out = new OutputStream(communicator);
        out.startEncapsulation();
        out.endEncapsulation();

        var res = proxy.ice_invoke("getStationList", OperationMode.Idempotent, out.finished());
        var in = new InputStream(communicator, res.outParams);
        in.startEncapsulation();
        var stationNames = in.readStringSeq();
        in.endEncapsulation();

        System.out.printf("Found these stations: %s%n", String.join(", ", stationNames));
    }
}