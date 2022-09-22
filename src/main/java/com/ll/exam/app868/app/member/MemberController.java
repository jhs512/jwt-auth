package com.ll.exam.app868.app.member;

import com.ll.exam.app868.app.security.context.MemberContext;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {
    @GetMapping("/test")
    public String test(@AuthenticationPrincipal MemberContext memberContext) {
        System.out.println(memberContext.getId());
        System.out.println(memberContext.getUsername());
        System.out.println(memberContext.getAuthorities());
        return "ok";
    }
}
