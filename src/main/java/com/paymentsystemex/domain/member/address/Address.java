package com.paymentsystemex.domain.member.address;

import com.paymentsystemex.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String addressDetail;

    @Column
    private boolean isDefault;

    @Enumerated(EnumType.STRING)
    private DeliveryCharge deliveryCharge;

}
