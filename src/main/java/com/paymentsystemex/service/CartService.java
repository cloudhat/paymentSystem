package com.paymentsystemex.service;

import com.paymentsystemex.auth.AuthenticationException;
import com.paymentsystemex.auth.principal.UserPrincipal;
import com.paymentsystemex.domain.Cart;
import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.product.Product;
import com.paymentsystemex.domain.product.ProductOption;
import com.paymentsystemex.dto.cart.CartRequest;
import com.paymentsystemex.dto.cart.CartResponse;
import com.paymentsystemex.repository.CartRepository;
import com.paymentsystemex.repository.MemberRepository;
import com.paymentsystemex.repository.ProductOptionRepository;
import com.paymentsystemex.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    @Transactional
    public Cart saveCart(CartRequest cartRequest, UserPrincipal userPrincipal) {
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);
        Product product = productRepository.getReferenceById(cartRequest.getProductId());
        ProductOption productOption = productOptionRepository.findById(cartRequest.getProductOptionId()).orElseThrow(AuthenticationException::new);

        Optional<Cart> alreadyExistingCart  = getAvailableCarts(member).stream()
                .filter(cart -> cartRequest.getProductOptionId().equals(cart.getProductOption().getId()))
                .findFirst();

        if(alreadyExistingCart.isPresent()){
            Cart cart = alreadyExistingCart.get();
            cart.updateQuantity(cartRequest.getQuantity());
            return cart;
        }

        if (productOption.isCurrentlyAvailable()) {
            return cartRepository.save(new Cart(member, product, productOption, cartRequest.getQuantity()));
        } else {
            throw new IllegalArgumentException();
        }
    }

    public List<CartResponse> getCartResponses(UserPrincipal userPrincipal) {
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);

        return this.getAvailableCarts(member).stream()
                .map(CartResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private List<Cart> getAvailableCarts(Member member) {
        return cartRepository.findByMemberId(member.getId()).stream()
                .filter(cart -> cart.getProductOption().isCurrentlyAvailable())
                .collect(Collectors.toList());
    }
}
