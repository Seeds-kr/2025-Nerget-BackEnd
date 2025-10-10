package com.seeds.NergetBackend.global.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {

    /**
     * 로그인 페이지 (홈)
     */
    @GetMapping("/")
    public String login() {
        return "login";
    }

    /**
     * 사진 4장 선택 페이지
     */
    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    /**
     * AI 분석 진행 페이지
     */
    @GetMapping("/analyzing")
    public String analyzing(@RequestParam String jobId, Model model) {
        model.addAttribute("jobId", jobId);
        return "analyzing";
    }

    /**
     * 후보 스와이프 페이지
     */
    @GetMapping("/swipe")
    public String swipe(@RequestParam String jobId, Model model) {
        model.addAttribute("jobId", jobId);
        return "swipe";
    }

    /**
     * MBTI 결과 페이지
     */
    @GetMapping("/result")
    public String result(@RequestParam(required = false) String mbti, Model model) {
        model.addAttribute("mbti", mbti);
        return "result";
    }

    /**
     * 홈 피드 페이지
     */
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    /**
     * 검색 페이지
     */
    @GetMapping("/search")
    public String search() {
        return "search";
    }

    /**
     * 게시물 업로드 페이지
     */
    @GetMapping("/post/upload")
    public String postUpload() {
        return "post-upload";
    }

    /**
     * 커뮤니티 피드 페이지
     */
    @GetMapping("/community")
    public String community() {
        return "community";
    }

    /**
     * 마이페이지
     */
    @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }
}

