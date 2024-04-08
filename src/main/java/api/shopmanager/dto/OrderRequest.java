package api.shopmanager.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    /*
    * Здесь, такие поля как price и created_at и completed_at
    * не будут указываться, потому что
    * price будет считываться автоматический, и created_at и completed_at также
    */

    private String status;
    private List<Long> products;
}
