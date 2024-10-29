/*
 * This file is generated by jOOQ.
 */
package lt.liutikas.bananacar_notification_svc.db.jooq.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import org.jooq.JSONB;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class JooqRide implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private String bananacarRideId;
    private JSONB locations;
    private String bananacarUrl;
    private LocalDateTime departsOn;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    public JooqRide() {}

    public JooqRide(JooqRide value) {
        this.id = value.id;
        this.bananacarRideId = value.bananacarRideId;
        this.locations = value.locations;
        this.bananacarUrl = value.bananacarUrl;
        this.departsOn = value.departsOn;
        this.createdOn = value.createdOn;
        this.updatedOn = value.updatedOn;
    }

    public JooqRide(
        UUID id,
        String bananacarRideId,
        JSONB locations,
        String bananacarUrl,
        LocalDateTime departsOn,
        LocalDateTime createdOn,
        LocalDateTime updatedOn
    ) {
        this.id = id;
        this.bananacarRideId = bananacarRideId;
        this.locations = locations;
        this.bananacarUrl = bananacarUrl;
        this.departsOn = departsOn;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    /**
     * Getter for <code>public.ride.id</code>.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Setter for <code>public.ride.id</code>.
     */
    public JooqRide setId(UUID id) {
        this.id = id;
        return this;
    }

    /**
     * Getter for <code>public.ride.bananacar_ride_id</code>.
     */
    public String getBananacarRideId() {
        return this.bananacarRideId;
    }

    /**
     * Setter for <code>public.ride.bananacar_ride_id</code>.
     */
    public JooqRide setBananacarRideId(String bananacarRideId) {
        this.bananacarRideId = bananacarRideId;
        return this;
    }

    /**
     * Getter for <code>public.ride.locations</code>.
     */
    public JSONB getLocations() {
        return this.locations;
    }

    /**
     * Setter for <code>public.ride.locations</code>.
     */
    public JooqRide setLocations(JSONB locations) {
        this.locations = locations;
        return this;
    }

    /**
     * Getter for <code>public.ride.bananacar_url</code>.
     */
    public String getBananacarUrl() {
        return this.bananacarUrl;
    }

    /**
     * Setter for <code>public.ride.bananacar_url</code>.
     */
    public JooqRide setBananacarUrl(String bananacarUrl) {
        this.bananacarUrl = bananacarUrl;
        return this;
    }

    /**
     * Getter for <code>public.ride.departs_on</code>.
     */
    public LocalDateTime getDepartsOn() {
        return this.departsOn;
    }

    /**
     * Setter for <code>public.ride.departs_on</code>.
     */
    public JooqRide setDepartsOn(LocalDateTime departsOn) {
        this.departsOn = departsOn;
        return this;
    }

    /**
     * Getter for <code>public.ride.created_on</code>.
     */
    public LocalDateTime getCreatedOn() {
        return this.createdOn;
    }

    /**
     * Setter for <code>public.ride.created_on</code>.
     */
    public JooqRide setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    /**
     * Getter for <code>public.ride.updated_on</code>.
     */
    public LocalDateTime getUpdatedOn() {
        return this.updatedOn;
    }

    /**
     * Setter for <code>public.ride.updated_on</code>.
     */
    public JooqRide setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final JooqRide other = (JooqRide) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        }
        else if (!this.id.equals(other.id))
            return false;
        if (this.bananacarRideId == null) {
            if (other.bananacarRideId != null)
                return false;
        }
        else if (!this.bananacarRideId.equals(other.bananacarRideId))
            return false;
        if (this.locations == null) {
            if (other.locations != null)
                return false;
        }
        else if (!this.locations.equals(other.locations))
            return false;
        if (this.bananacarUrl == null) {
            if (other.bananacarUrl != null)
                return false;
        }
        else if (!this.bananacarUrl.equals(other.bananacarUrl))
            return false;
        if (this.departsOn == null) {
            if (other.departsOn != null)
                return false;
        }
        else if (!this.departsOn.equals(other.departsOn))
            return false;
        if (this.createdOn == null) {
            if (other.createdOn != null)
                return false;
        }
        else if (!this.createdOn.equals(other.createdOn))
            return false;
        if (this.updatedOn == null) {
            if (other.updatedOn != null)
                return false;
        }
        else if (!this.updatedOn.equals(other.updatedOn))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = prime * result + ((this.bananacarRideId == null) ? 0 : this.bananacarRideId.hashCode());
        result = prime * result + ((this.locations == null) ? 0 : this.locations.hashCode());
        result = prime * result + ((this.bananacarUrl == null) ? 0 : this.bananacarUrl.hashCode());
        result = prime * result + ((this.departsOn == null) ? 0 : this.departsOn.hashCode());
        result = prime * result + ((this.createdOn == null) ? 0 : this.createdOn.hashCode());
        result = prime * result + ((this.updatedOn == null) ? 0 : this.updatedOn.hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("JooqRide (");

        sb.append(id);
        sb.append(", ").append(bananacarRideId);
        sb.append(", ").append(locations);
        sb.append(", ").append(bananacarUrl);
        sb.append(", ").append(departsOn);
        sb.append(", ").append(createdOn);
        sb.append(", ").append(updatedOn);

        sb.append(")");
        return sb.toString();
    }
}