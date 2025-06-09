package core.domain.member.entity.address;

import lombok.Getter;

@Getter
public enum DeliveryCharge {

    METROPOLITAN_AREA(0),
    PROVINCE(1000),
    JEJU(5000);

    private final int amount;

    DeliveryCharge(int amount) {
        this.amount = amount;
    }
}
