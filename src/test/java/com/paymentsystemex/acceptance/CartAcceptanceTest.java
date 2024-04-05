package com.paymentsystemex.acceptance;

import com.paymentsystemex.acceptance.commonStep.MemberSteps;
import com.paymentsystemex.utils.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CartAcceptanceTest extends AcceptanceTest {

    public static final String EMAIL1 = "email1@email.com";
    public static final String PASSWORD1 = "password1";
    public static final int AGE = 20;

    @DisplayName("신규 장바구니를 생성한다")
    @Test
    void createCart(){

        //given
        String accessToken = MemberSteps.회원가입_및_토큰_받기(EMAIL1, PASSWORD1, AGE);

        //when

    }

//    @DisplayName("장바구니 목록을 조회한다")
//    @Test
//    void getCartList(){
//
//    }
//
//    @DisplayName("장바구니의 수량을 수정한다")
//    @Test
//    void updateQuantity(){
//
//    }
//
//    @DisplayName("장바구니를 삭제한다")
//    @Test
//    void deleteCart(){
//
//    }

}
