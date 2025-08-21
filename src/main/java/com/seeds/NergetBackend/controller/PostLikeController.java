
package com.seeds.NergetBackend.controller;

import com.seeds.NergetBackend.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    record LikeResponse(boolean liked, int likeCount) {}
}
