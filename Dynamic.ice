module Dynamic {
  enum LongitudeType {N, S};
  enum LatitudeType {E, W};

  class Longitude {
    LongitudeType type;
    float value;
  };

  struct Latitude {
    LatitudeType type;
    float value;
  };

  struct Position {
    Longitude longitude;
    Latitude latitude;
  };

  enum ReadingType {Temperature, Humidity, AirQuality};

  struct Reading {
    ReadingType type;
    float value;
    long timestamp;
  };

  sequence<Reading> Readings;

  exception StationAlreadyExists {};
  exception StationNameEmpty {};
  exception StationNotFound {};

  interface Stations {
    void addStation(string name, Position position) throws StationAlreadyExists, StationNameEmpty;
    void addReadings(string stationName, Readings readings) throws StationNotFound;
    idempotent Readings getReadings(string stationName) throws StationNotFound;
    idempotent Position getPosition(string stationName) throws StationNotFound;
  };
};
