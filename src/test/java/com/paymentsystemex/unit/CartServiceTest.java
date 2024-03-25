package com.paymentsystemex.unit;

import com.paymentsystemex.auth.principal.UserPrincipal;
import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.member.RoleType;
import com.paymentsystemex.domain.product.Product;
import com.paymentsystemex.domain.product.ProductOption;
import com.paymentsystemex.repository.CartRepository;
import com.paymentsystemex.repository.MemberRepository;
import com.paymentsystemex.repository.ProductOptionRepository;
import com.paymentsystemex.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

@DisplayName("즐겨찾기 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    CartRepository cartRepository;

    @Mock
    ProductOptionRepository productOptionRepository;

    @InjectMocks
    CartService cartService;

    private String email = "example@example.com";
    private UserPrincipal userPrincipal;

    private Member member;


    private Product product;
    Long productId = 1L;

    private ProductOption availableProductOption;
    private ProductOption unAvailableProductOption;

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime yesterday = now.minusDays(1);
    LocalDateTime tomorrow = now.plusDays(1);

    private Long availableProductOptionId = 1L;
    private Long unAvailableProductOptionId = 2L;

    @BeforeEach
    public void setGivenData() {
        userPrincipal = new UserPrincipal(email, RoleType.ROLE_MEMBER.name());
        member = new Member(email, "test_example", 30);
        product = new Product(productId, null, "기본 티셔츠");
        availableProductOption = new ProductOption(availableProductOptionId, product, "검정색", 10000, yesterday, tomorrow);
        unAvailableProductOption = new ProductOption(unAvailableProductOptionId, product, "흰색", 10000, tomorrow, tomorrow);

    }

    @DisplayName("기존에 동일 상품 옵션의 장바구니가 존재하는 경우")
    @Test
    public void saveCartAlreadyExist() {
        when(memberRepository.findByEmail(email)).thenReturn(Optional.ofNullable(member));
        when(productOptionRepository.findById(availableProductOptionId)).thenReturn(Optional.ofNullable(availableProductOption));

    }
}
