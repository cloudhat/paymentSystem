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
import com.paymentsystemex.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @Transactional
    public CartResponse saveCart(CartRequest cartRequest, UserPrincipal userPrincipal) {
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);

        Optional<Cart> alreadyExistingCart = getAvailableCarts(member.getId()).stream()
                .filter(cart -> cartRequest.getProductOptionId().equals(cart.getProductOption().getId()))
                .findFirst();

        if (alreadyExistingCart.isPresent()) {
            Cart cart = alreadyExistingCart.get();
            cart.updateQuantity(cartRequest.getQuantity());
            return CartResponse.fromEntity(cart);
        }

        Product product = productRepository.getReferenceById(cartRequest.getProductId());
        ProductOption productOption = productRepository.findProductOptionById(cartRequest.getProductOptionId()).orElseThrow(AuthenticationException::new);
        if (productOption.isCurrentlyAvailable()) {
            Cart cart = new Cart(member, product, productOption, cartRequest.getQuantity());
            cartRepository.save(cart);

            return CartResponse.fromEntity(cart);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public List<CartResponse> getCartResponses(UserPrincipal userPrincipal) {
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);

        return this.getAvailableCarts(member.getId()).stream()
                .map(CartResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<Cart> getAvailableCarts(Long memberId) {
        return cartRepository.findByMemberId(memberId).stream()
                .filter(cart -> cart.getProductOption().isCurrentlyAvailable())
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateCart(Long cartId, UserPrincipal userPrincipal, int changeAmount) {
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);

        Optional<Cart> alreadyExistingCart = getAvailableCarts(member.getId()).stream()
                .filter(cart -> cartId.equals(cart.getId()))
                .findFirst();

        if (alreadyExistingCart.isEmpty()) {
            throw new EntityNotFoundException();
        }

        alreadyExistingCart.get().updateQuantity(changeAmount);
    }

    public void deleteCart(UserPrincipal userPrincipal, Long cartId) {
        Member member = memberRepository.findByEmail(userPrincipal.getUsername()).orElseThrow(AuthenticationException::new);

        Optional<Cart> alreadyExistingCart = getAvailableCarts(member.getId()).stream()
                .filter(cart -> cartId.equals(cart.getId()))
                .findFirst();

        if (alreadyExistingCart.isEmpty()) {
            throw new EntityNotFoundException();
        }

        cartRepository.delete(cartId, member.getId());
    }
}
