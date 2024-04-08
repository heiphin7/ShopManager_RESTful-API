package api.shopmanager.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private int price;
    private String status;
    private List<Long> products;
}
