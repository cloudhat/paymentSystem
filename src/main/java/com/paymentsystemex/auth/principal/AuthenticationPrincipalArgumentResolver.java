package com.paymentsystemex.auth.principal;


import com.paymentsystemex.auth.AuthenticationException;
import com.paymentsystemex.auth.token.JwtTokenProvider;
import com.paymentsystemex.repository.MemberRepository;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {
    private JwtTokenProvider jwtTokenProvider;

    public AuthenticationPrincipalArgumentResolver(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticationPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        AuthenticationPrincipal authenticationPrincipal = parameter.getParameterAnnotation(AuthenticationPrincipal.class);
        String authorization = webRequest.getHeader("Authorization");
        if (!StringUtils.hasText(authorization) && !authenticationPrincipal.required()) {
            return new AnonymousPrincipal();
        }
        if (!"bearer".equalsIgnoreCase(authorization.split(" ")[0])) {
            throw new AuthenticationException();
        }
        String token = authorization.split(" ")[1];

        try {
            String username = jwtTokenProvider.getPrincipal(token);
            String role = jwtTokenProvider.getRoles(token);

            return new UserPrincipal(username, role);
        }
        catch (Exception e) {
            throw new AuthenticationException();
        }
    }
}
