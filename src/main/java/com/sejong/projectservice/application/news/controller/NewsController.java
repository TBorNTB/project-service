package com.sejong.archiveservice.application.news.controller;

import com.sejong.archiveservice.application.config.security.UserContext;
import com.sejong.archiveservice.application.file.FileUploadRequest;
import com.sejong.archiveservice.application.file.FileUploader;
import com.sejong.archiveservice.application.file.PreSignedUrl;
import com.sejong.archiveservice.application.news.dto.NewsReqDto;
import com.sejong.archiveservice.application.news.dto.NewsResDto;
import com.sejong.archiveservice.application.news.service.NewsService;
import com.sejong.archiveservice.application.pagination.CursorPageReqDto;
import com.sejong.archiveservice.application.pagination.OffsetPageReqDto;
import com.sejong.archiveservice.core.common.pagination.CursorPageResponse;
import com.sejong.archiveservice.core.common.pagination.OffsetPageResponse;
import com.sejong.archiveservice.core.news.News;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Tag(name = "News", description = "뉴스 관련 API")
public class NewsController {

    private final NewsService newsService;
    private final FileUploader fileUploader;

    @GetMapping("/health")
    public String healthCheck() {
        return "ok";
    }

    @PostMapping()
    @Operation(summary = "뉴스 생성")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<NewsResDto> createNews(@RequestBody NewsReqDto newsReqDto) {
        UserContext currentUser = getCurrentUser();
        newsReqDto.setWriter(currentUser.getUsername());

        NewsResDto response = newsService.createNews(newsReqDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/files/presigned-url")
    @Operation(summary = "파일 업로드용 PreSigned URL 생성")
//    @SecurityRequirement(name = "bearerAuth") TODO: 보안 설정
    public ResponseEntity<PreSignedUrl> preSignedUrl(@RequestBody FileUploadRequest request) {
        PreSignedUrl preSignedUrl = fileUploader.generatePreSignedUrl(
                request.fileName(),
                request.contentType(), // "image/jpeg"
                request.fileType()     // "image"
        );
        return ResponseEntity.ok(preSignedUrl);
    }

    @GetMapping("/offset")
    @Operation(summary = "뉴스 조회 (오프셋 기반 페이지네이션)")
    public ResponseEntity<OffsetPageResponse<List<NewsResDto>>> getOffsetNews(
            @ParameterObject @Valid OffsetPageReqDto offsetPageReqDto) {

        OffsetPageResponse<List<NewsResDto>> offsetNews = newsService.getOffsetNews(offsetPageReqDto);
        return ResponseEntity.ok(offsetNews);
    }

    @GetMapping("/cursor")
    @Operation(summary = "뉴스 조회 (커서 기반 페이지네이션)")
    public ResponseEntity<CursorPageResponse<List<NewsResDto>>> getCursorNews(
            @ParameterObject @Valid CursorPageReqDto cursorPageReqDto) {

        CursorPageResponse<List<NewsResDto>> cursorNews = newsService.getCursorNews(cursorPageReqDto);
        return ResponseEntity.ok(cursorNews);
    }

    @GetMapping("/{newsId}")
    @Operation(summary = "뉴스 조회")
    public ResponseEntity<NewsResDto> getNews(@PathVariable Long newsId) {
        NewsResDto response = newsService.findById(newsId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{newsId}")
    @Operation(summary = "뉴스 수정")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<NewsResDto> updateNews(@PathVariable Long newsId,
                                                 @RequestBody NewsReqDto newsReqDto) {
        String writerId = getCurrentUser().getUsername();

        NewsResDto response = newsService.updateNews(newsId, newsReqDto, writerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{newsId}")
    @Operation(summary = "뉴스 삭제")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteNews(@PathVariable Long newsId) {
        String writerId = getCurrentUser().getUsername();
        newsService.deleteNews(newsId, writerId);
        return ResponseEntity.ok().build();
    }

    private UserContext getCurrentUser() {
        return (UserContext) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
}
