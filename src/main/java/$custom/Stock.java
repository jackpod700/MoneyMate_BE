package $custom;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class Stock {
    private String isin;
    private String ticker;
    private String exchangeId;
    private String name;
    private String currency;
}
