package com.paymentsystemex.service.payment.paymentInitStrategy;

import com.paymentsystemex.domain.coupon.Coupon;
import com.paymentsystemex.domain.coupon.Coupons;
import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.member.address.Address;
import com.paymentsystemex.domain.order.OrderPriceHistory;
import com.paymentsystemex.domain.order.OrderProduct;
import com.paymentsystemex.domain.order.Orders;
import com.paymentsystemex.domain.order.PriceType;
import com.paymentsystemex.domain.payment.Payment;
import com.paymentsystemex.dto.payment.PaymentRequest;
import com.paymentsystemex.repository.CartRepository;
import com.paymentsystemex.repository.CouponRepository;
import com.paymentsystemex.repository.MemberRepository;
import com.paymentsystemex.service.payment.paymentInitStrategy.pricePolicy.CouponPricePolicy;
import com.paymentsystemex.service.payment.paymentInitStrategy.pricePolicy.DeliveryFeePolicy;
import com.paymentsystemex.service.payment.paymentInitStrategy.pricePolicy.PricePolicy;
import com.paymentsystemex.service.payment.paymentInitStrategy.pricePolicy.ProductPricePolicy;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
    @Transactional
    protected OrderPriceHistory cancelPayment(Orders orders) {
        Payment payment = orders.getNormalPayment();

        payment.changeStatusToCancel();
        couponRepository.bulkRollbackAsUnused(orders.getId(), orders.getMember().getId());

        return new OrderPriceHistory(orders, PriceType.CANCEL, "결제취소", payment.getTotalPayAmount());
    }

}
