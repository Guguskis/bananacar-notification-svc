package lt.liutikas.bananacar_notification_svc.adapter.web.bananacar.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BananacarRideSearchResponse {

    @JsonProperty("data")
    private final List<BananacarRide> rides;

    @JsonProperty("last_page")
    private final Integer lastPage;

}
