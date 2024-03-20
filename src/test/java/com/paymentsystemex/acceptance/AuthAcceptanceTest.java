package com.paymentsystemex.acceptance;

import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.dto.token.TokenResponse;
import com.paymentsystemex.repository.MemberRepository;
import com.paymentsystemex.utils.AcceptanceTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.paymentsystemex.acceptance.commonStep.AuthStep.자체_서비스_로그인_요청;
import static org.assertj.core.api.Assertions.assertThat;

public class AuthAcceptanceTest extends AcceptanceTest {

    public static final String EMAIL = "admin@email.com";
    public static final String PASSWORD = "password";
    public static final Integer AGE = 20;

    @Autowired
    private MemberRepository memberRepository;

    /**
     * Given 회원 가입을 하고
     * When 자체 서비스 로그인을 하면
     * Then 액세스 토큰을 발급 해준다
     */
    @DisplayName("Bearer Auth")
    @Test
    void bearerAuth() {
        //given
        memberRepository.save(new Member(EMAIL, PASSWORD, AGE));

        //when
        ExtractableResponse<Response> response = 자체_서비스_로그인_요청(EMAIL, PASSWORD);
        var tokenResponse = response.as(TokenResponse.class);

        //then
        assertThat(tokenResponse.getAccessToken()).isNotBlank();
    }
}
