package lt.liutikas.bananacar_notification_svc.adapter.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lt.liutikas.bananacar_notification_svc.application.port.in.FetchRidesByBananacarRideIdPort;
import lt.liutikas.bananacar_notification_svc.application.port.out.CreateRidesPort;
import lt.liutikas.bananacar_notification_svc.db.jooq.tables.records.JooqRideRecord;
import lt.liutikas.bananacar_notification_svc.domain.Location;
import lt.liutikas.bananacar_notification_svc.domain.Ride;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import static lt.liutikas.bananacar_notification_svc.db.jooq.Tables.RIDE;

@Repository
@RequiredArgsConstructor
public class RidesRepository implements FetchRidesByBananacarRideIdPort, CreateRidesPort {

    private final DSLContext dslContext;
    private final ObjectMapper objectMapper;

    @Override
    public List<Ride> fetch(List<String> rideIds) {

        return dslContext.selectFrom(RIDE)
                .where(RIDE.BANANACAR_RIDE_ID.in(rideIds))
                .fetch()
                .map(this::toRide);
    }

    @Override
    public void save(List<Ride> rides) {

        List<JooqRideRecord> rideRecords = rides.stream()
                .map(this::toRideRecord)
                .toList();

        dslContext.batchInsert(rideRecords).execute();
    }

    private String toJson(Object obj) {

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to stringify obj", e);
        }
    }

    private JooqRideRecord toRideRecord(Ride ride) {

        String locationsJson = toJson(ride.getLocations());

        JooqRideRecord rideRecord = new JooqRideRecord();
        rideRecord.setId(UUID.randomUUID());
        rideRecord.setBananacarRideId(ride.getBananacarRideId());
        rideRecord.setLocations(JSONB.valueOf(locationsJson));
        rideRecord.setDepartsOn(ride.getDepartsOn());
        rideRecord.setBananacarUrl(ride.getBananacarUrl().toString());
        rideRecord.setCreatedOn(ride.getCreatedOn());
        rideRecord.setUpdatedOn(ride.getUpdatedOn());

        return rideRecord;
    }

    private Ride toRide(JooqRideRecord record) {

        List<Location> locations = fromJson(record.getLocations().data(), List.class);

        URL bananacarUrl = toUrl(record.getBananacarUrl());

        return Ride.builder()
                .id(record.getId())
                .bananacarRideId(record.getBananacarRideId())
                .locations(locations)
                .departsOn(record.getDepartsOn())
                .bananacarUrl(bananacarUrl)
                .createdOn(record.getCreatedOn())
                .updatedOn(record.getUpdatedOn())
                .build();
    }

    private URL toUrl(String url) {

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private <T> T fromJson(String json, Class<T> clazz) {

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to parse JSON", e);
        }
    }
}
