package lt.liutikas.bananacar_notification_svc.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RideUTest {

    @Nested
    class IsRouteMatch {

        @CsvSource(value = {
                "Vilnius;Klaipeda;Vilnius;Klaipėda",
                "Vilnius;Klaipėda;Vilnius;Klaipeda",
                "Klaipeda;Vilnius;Klaipėda;Vilnius",
                "Klaipėda;Vilnius;Klaipėda;Vilnius",
        }, delimiter = ';')
        @ParameterizedTest
        void true_whenCityWithDiacriticalMarks(
                String rideOriginCity,
                String rideDestinationCity,
                String originCity,
                String destinationCity
        ) {

            Ride ride = Ride.builder()
                    .locations(List.of(
                            Location.builder()
                                    .city(rideOriginCity)
                                    .type(LocationType.ORIGIN)
                                    .build(),
                            Location.builder()
                                    .city(rideDestinationCity)
                                    .type(LocationType.DESTINATION)
                                    .build()
                    ))
                    .build();

            boolean routeMatch = ride.isRouteMatch(originCity, destinationCity);

            assertTrue(routeMatch);
        }
    }
}
