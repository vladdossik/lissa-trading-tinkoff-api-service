package lissa.trading.tinkoff_stock_service.model;

import com.google.protobuf.ProtocolStringList;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class UserAccount {
    String accountId;
    String tariff;
    boolean premStatus;
    ProtocolStringList qualifiers;
}
