package com.ll.exam.app868;


import com.ll.exam.app868.app.security.filter.JwtAuthorizationFilter;
import com.ll.exam.app868.app.security.jwt.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AppTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    JwtAuthorizationFilter jwtAuthorizationFilter;

    @Test
    @DisplayName("로그인을 하면 JWT 키가 발급된다.")
    void t1() throws Exception {
        // When
        ResultActions resultActions = mvc
                .perform(
                        post("/member/login")
                                .content("""
                                        {
                                            "username": "user1",
                                            "password": "1234"
                                        }
                                        """.stripIndent())
                )
                .andDo(print());

        // Then
        resultActions
                .andExpect(status().is2xxSuccessful());

        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();


        String authentication = response.getHeader("Authentication");
        jwtAuthorizationFilter.forceAuthentication(authentication);

//        String username = jwtProvider.getUsername(authentication);
//        String authorities = jwtProvider.getAuthorities(authentication);
//        Long id = jwtProvider.getId(authentication);

//        System.out.println(authorities);
//        System.out.println(username);
//        System.out.println(id);
    }

}

