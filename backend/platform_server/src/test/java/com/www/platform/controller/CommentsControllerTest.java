package com.www.platform.controller;

import com.www.core.auth.entity.Users;
import com.www.core.common.Response;
import com.www.core.common.TokenChecker;
import com.www.core.file.entity.Episode;
import com.www.core.platform.entity.Comments;
import com.www.platform.dto.CommentsDto;
import com.www.platform.dto.CommentsResponseDto;
import com.www.platform.service.CommentsLikeDislikeService;
import com.www.platform.service.CommentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = CommentsController.class)
public class CommentsControllerTest {

    @MockBean
    private CommentsService commentsService;
    @MockBean
    private CommentsLikeDislikeService commentsLikeDislikeService;
    @MockBean
    private TokenChecker tokenChecker;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext context,
               RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(print())
                .build();
    }

    @DisplayName("댓글 페이징 조회")
    @Test
    void getComments() throws Exception {
        //given
        Users user = Users.builder()
                .idx(1)
                .id("id123")
                .name("철수")
                .e_pw("1q2w3e4r")
                .gender(0)
                .email("test@email.com")
                .build();
        Episode episode = Episode.builder()
                .idx(1)
                .ep_no(1)
                .title("에피소드 제목")
                .author_comment("작가의 말")
                .build();

        int episodeIdx = 1;
        String page = "1";
        int pageNumber = Integer.parseInt(page);
        List<Comments> commentsList = new ArrayList<>();
        for (int idx = 5; idx >= 1; idx--) {
            commentsList.add(Comments.builder()
                    .idx(idx)
                    .users(user)
                    .ep(episode)
                    .content("댓글 내용 " + idx)
                    .build());
        }
        CommentsResponseDto commentsResponseDto = CommentsResponseDto.builder()
                .comments(commentsList.stream()
                        .map(CommentsDto::new)
                        .collect(Collectors.toList()))
                .total_pages(1)
                .build();

        Response<CommentsResponseDto> response = new Response<>();
        response.setCode(0);
        response.setMsg("request complete : get comments by page request");
        response.setData(commentsResponseDto);
        given(commentsService.getCommentsByPageRequest(episodeIdx, pageNumber))
                .willReturn(response);

        //when
        ResultActions result = mockMvc.perform(get("/episodes/{ep_idx}/comments", episodeIdx)
                .param("page", page)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("code").value(0))
                .andExpect(jsonPath("msg").value("request complete : get comments by page request"))
                .andExpect(jsonPath("data.comments[0].idx").value(5))
                .andExpect(jsonPath("data.total_pages").value(1));
    }
}