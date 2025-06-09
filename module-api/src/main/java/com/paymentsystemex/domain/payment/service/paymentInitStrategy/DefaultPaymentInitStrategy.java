package com.paymentsystemex.domain.payment.service.paymentInitStrategy;

import core.domain.order.entity.coupon.Coupon;
import core.domain.order.entity.coupon.Coupons;
import core.domain.member.entity.Member;
import core.domain.member.entity.address.Address;
import core.domain.order.entity.orderPriceHistory.OrderPriceHistory;
import core.domain.order.entity.orderProduct.OrderProduct;
import core.domain.order.entity.Orders;
import core.domain.order.entity.orderPriceHistory.PriceType;
import core.domain.order.entity.payment.Payment;
import core.domain.order.dto.PaymentRequest;
import core.domain.cart.repository.CartRepository;
import core.domain.order.repository.CouponRepository;
import core.domain.member.repository.MemberRepository;
import com.paymentsystemex.domain.payment.service.paymentInitStrategy.pricePolicy.CouponPricePolicy;
import com.paymentsystemex.domain.payment.service.paymentInitStrategy.pricePolicy.DeliveryFeePolicy;
import com.paymentsystemex.domain.payment.service.paymentInitStrategy.pricePolicy.PricePolicy;
import com.paymentsystemex.domain.payment.service.paymentInitStrategy.pricePolicy.ProductPricePolicy;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DefaultPaymentInitStrategy extends PaymentInitStrategy {
    private final MemberRepository memberRepository;

    private final CouponRepository couponRepository;

    public DefaultPaymentInitStrategy(CartRepository cartRepository, MemberRepository memberRepository, CouponRepository couponRepository) {
        super(cartRepository);
        this.memberRepository = memberRepository;
        this.couponRepository = couponRepository;
    }


    @Override
    public String getEventType() {
        return "DEFAULT";
    }

    @Override
    protected Payment initPayment(PaymentRequest paymentRequest, Member member, Orders orders) {
        List<OrderProduct> orderProducts = orders.getOrderProducts();
        List<Coupon> couponList = couponRepository.findByIdInAndMemberId(paymentRequest.getCouponIdList(), member.getId());
        Address address = memberRepository.findAddressById(paymentRequest.getAddressId(), member.getId()).orElseThrow(EntityNotFoundException::new);

        PricePolicy pricePolicy = new ProductPricePolicy(orderProducts)
                .setNextPricePolicy(new CouponPricePolicy(new Coupons(couponList)))
                .setNextPricePolicy(new DeliveryFeePolicy(address));

        List<OrderPriceHistory> orderPriceHistoryList = pricePolicy.getOrderPriceList(0, orders);

        int totalPayAmount = orderPriceHistoryList.stream()
                .mapToInt(OrderPriceHistory::getAmount).sum();

        int totalDiscountAmount = orderPriceHistoryList.stream()
                .filter(history -> history.getAmount() < 0)
                .mapToInt(OrderPriceHistory::getAmount)
                .sum();

        return new Payment(totalPayAmount, totalDiscountAmount, paymentRequest.getPaymentMethod(), paymentRequest.getEventType(), orders, member);
    }

    @Override
    protected OrderPriceHistory cancelPayment(Orders orders) {
        Payment payment = orders.getNormalPayment();

        payment.changeStatusToCancel();
        couponRepository.bulkRollbackAsUnused(orders.getId(), orders.getMember().getId());

        return new OrderPriceHistory(orders, PriceType.CANCEL, "결제취소", payment.getTotalPayAmount());
    }

}
