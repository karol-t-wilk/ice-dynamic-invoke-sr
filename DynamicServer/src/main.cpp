#include "../gen/Dynamic.h"
#include <Ice/Ice.h>
#include <iostream>

class Station {
public:
  std::string name;
  Dynamic::Position position;
  Dynamic::Readings readings = {};

  Station(std::string name, Dynamic::Position position)
      : name{name}, position{position} {}
};

static std::vector<std::string> const STATIC_IDS = {
    Dynamic::Stations::ice_staticId(), Ice::Object::ice_staticId()};

class StationsI : public Ice::Blobject {
private:
  std::vector<Station> stations = {};

public:
  bool ice_invoke(std::vector<Ice::Byte> inParams,
                  std::vector<Ice::Byte> &outParams,
                  const Ice::Current &current) override {
    std::cout << "Got a request for operation: " << current.operation << '\n';
    auto communicator = current.adapter->getCommunicator();

    Ice::InputStream in{communicator, inParams};
    Ice::OutputStream out{communicator};
    // ================== ICE internals START ==================
    if (current.operation == "ice_id") {
      out.startEncapsulation();
      out.write(Dynamic::Stations::ice_staticId());
      out.endEncapsulation();
      out.finished(outParams);
      return true;
    } else if (current.operation == "ice_ids") {
      out.startEncapsulation();
      out.write(STATIC_IDS);
      out.endEncapsulation();
      out.finished(outParams);
      return true;
    } else if (current.operation == "ice_isA") {
      in.startEncapsulation();
      std::string id;
      in.read(id);
      in.endEncapsulation();
      auto res = std::any_of(begin(STATIC_IDS), end(STATIC_IDS),
                             [&](std::string const &s) { return s == id; });
      out.startEncapsulation();
      out.write(res);
      out.endEncapsulation();
      out.finished(outParams);
      return true;
    } else if (current.operation == "ice_ping") {
      out.startEncapsulation();
      out.endEncapsulation();
      out.finished(outParams);
      return true;

    }
    // ================== ICE internals END ==================
    // ================== Interface defined operations START ==================
    else if (current.operation == "addStation") {
      in.startEncapsulation();

      std::string name;
      in.read(name);

      Dynamic::Position position;
      in.read(position);

      in.endEncapsulation();

      if (name.empty()) {
        std::cout << "Station name is empty!\n";
        auto ex = Dynamic::StationNameEmpty{};
        out.startEncapsulation();
        out.writeException(ex);
        out.endEncapsulation();
        out.finished(outParams);
        return false;
      }

      if (std::any_of(begin(stations), end(stations),
                      [&](Station &station) { return station.name == name; })) {
        std::cout << "Station " << name << " already exists!\n";
        auto ex = Dynamic::StationAlreadyExists{};
        out.startEncapsulation();
        out.writeException(ex);
        out.endEncapsulation();
        out.finished(outParams);
        return false;
      }

      std::cout << "Creating station " << name << "...\n";
      stations.emplace_back(name, position);
      out.startEncapsulation();
      out.endEncapsulation();
      out.finished(outParams);
      return true;

    } else if (current.operation == "addReadings") {
      in.startEncapsulation();
      std::string name;
      in.read(name);
      Dynamic::Readings readings;
      in.read(readings);
      in.endEncapsulation();

      auto station =
          std::find_if(begin(stations), end(stations),
                       [&](Station &station) { return station.name == name; });

      if (station == end(stations)) {
        std::cout << "Station " << name << "does not exist!\n";
        auto ex = Dynamic::StationNotFound{};
        out.startEncapsulation();
        out.writeException(ex);
        out.endEncapsulation();
        out.finished(outParams);
        return false;
      }

      std::cout << "Adding " << readings.size() << "readings to station "
                << name << "...\n";
      station->readings.insert(end(station->readings), begin(readings),
                               end(readings));
      out.startEncapsulation();
      out.endEncapsulation();
      out.finished(outParams);
      return true;
    } else if (current.operation == "getReadings") {
      in.startEncapsulation();
      std::string name;
      in.read(name);
      in.endEncapsulation();

      auto station =
          std::find_if(begin(stations), end(stations),
                       [&](Station &station) { return station.name == name; });

      if (station == end(stations)) {
        std::cout << "Station " << name << "does not exist!\n";
        auto ex = Dynamic::StationNotFound{};
        out.startEncapsulation();
        out.writeException(ex);
        out.endEncapsulation();
        out.finished(outParams);
        return false;
      }

      std::cout << "Fetching readings for station " << name << "...\n";
      out.startEncapsulation();
      out.write(station->readings);
      out.endEncapsulation();
      out.finished(outParams);
      return true;
    } else if (current.operation == "getPosition") {
      in.startEncapsulation();
      std::string name;
      in.read(name);
      in.endEncapsulation();

      auto station =
          std::find_if(begin(stations), end(stations),
                       [&](Station &station) { return station.name == name; });

      if (station == end(stations)) {
        std::cout << "Station " << name << "does not exist!\n";
        auto ex = Dynamic::StationNotFound{};
        out.startEncapsulation();
        out.writeException(ex);
        out.endEncapsulation();
        out.finished(outParams);
        return false;
      }

      std::cout << "Fetching position of station " << name << "...\n";
      out.startEncapsulation();
      out.write(station->position);
      out.endEncapsulation();
      out.finished(outParams);
      return true;
    }
    // ================== Interface defined operations END ==================
    // ================== Additional operations START ==================
    else if (current.operation == "getStationList") {
      in.startEncapsulation();
      in.endEncapsulation();

      auto res = Ice::StringSeq{};
      res.reserve(stations.size());
      std::transform(begin(stations), end(stations), std::back_inserter(res),
                     [](Station s) { return s.name; });
      out.startEncapsulation();
      out.write(res);
      out.endEncapsulation();
      out.finished(outParams);
      return true;
    }
    // ================== Additional operations END ==================
    else {
      throw Ice::OperationNotExistException(__FILE__, __LINE__, current.id,
                                            current.facet, current.operation);
    }
  }
};

int main(int argc, char *argv[]) {
  try {
    auto quitHandler = Ice::CtrlCHandler{};
    auto communicator = Ice::CommunicatorHolder{Ice::initialize()};

    quitHandler.setCallback([&](int) {
      std::cout << "Exiting... ";
      communicator->shutdown();
      std::cout << "Thank you.\n";
    });

    auto servant = std::make_shared<StationsI>();
    auto id = Ice::Identity{"stations", "Dynamic"};

    auto adapter = communicator->createObjectAdapterWithEndpoints(
        "stations",
        "tcp -h 127.0.0.2 -p 10000 -z : udp -h 127.0.0.2 -p 10000 -z");
    adapter->add(servant, id);

    adapter->activate();

    std::cout << "Starting DynamicServer...\n";
    communicator->waitForShutdown();

    return 0;
  } catch (std::exception const &e) {
    std::cerr << e.what() << '\n';
    return 1;
  }
}
