package org.ulpc.dacd.control;

import org.ulpc.dacd.model.Location;

public interface WeatherRepository {
     void updateAndSave(Location... locations);
}
