package lt.liutikas.bananacar_notification_svc.adapter.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BananacarRideSearchResponse {

    @JsonProperty("data")
    private List<BananacarRide> rides;
}
