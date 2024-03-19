package com.paymentsystemex.controller;


import com.paymentsystemex.auth.principal.AuthenticationPrincipal;
import com.paymentsystemex.auth.principal.UserPrincipal;
import com.paymentsystemex.domain.member.Member;
import com.paymentsystemex.dto.MemberRequest;
import com.paymentsystemex.dto.MemberResponse;
import com.paymentsystemex.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class MemberController {
    private MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/members")
    public ResponseEntity<Void> createMember(@RequestBody MemberRequest request) {
        MemberResponse member = memberService.createMember(request);
        return ResponseEntity.created(URI.create("/members/" + member.getId())).build();
    }

}

