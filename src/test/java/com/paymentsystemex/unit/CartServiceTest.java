package com.paymentsystemex.unit;

import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.domain.product.Product;
import com.paymentsystemex.domain.product.ProductOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("즐겨찾기 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    private String email = "example@example.com";
    private Member member;
    private Product productA;
    private Product productB;

    private ProductOption availableProductOptionA;
    private ProductOption unAvailableProductOptionB;


    @BeforeEach
    public void setGivenData(){
        
        member = new Member(email, "test_example", 30);
    }
}
