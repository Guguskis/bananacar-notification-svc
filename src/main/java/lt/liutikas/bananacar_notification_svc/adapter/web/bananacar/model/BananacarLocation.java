package lt.liutikas.bananacar_notification_svc.adapter.web.bananacar.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BananacarLocation {

    private final String city;
    private final int ord;
}
