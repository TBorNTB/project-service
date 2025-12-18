package com.sejong.projectservice.application.news.controller;

import com.sejong.projectservice.application.file.FileUploadRequest;
import com.sejong.projectservice.application.file.FileUploader;
import com.sejong.projectservice.application.file.PreSignedUrl;
import com.sejong.projectservice.application.news.dto.NewsReqDto;
import com.sejong.projectservice.application.news.dto.NewsResDto;
import com.sejong.projectservice.application.news.service.NewsService;
import com.sejong.projectservice.application.pagination.CursorPageReqDto;
import com.sejong.projectservice.application.pagination.OffsetPageReqDto;
import com.sejong.projectservice.core.common.pagination.CursorPageResponse;
import com.sejong.projectservice.core.common.pagination.OffsetPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Slf4j
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
    public ResponseEntity<NewsResDto> createNews(
            @RequestBody NewsReqDto newsReqDto,
            @Parameter(hidden = true) @RequestHeader("X-User-Id") String username) {
        log.info("--------------------------------");
        log.info("username : {}", username);
        newsReqDto.setWriterUsername(username);
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
                                                 @RequestBody NewsReqDto newsReqDto,
                                                 @Parameter(hidden = true)  @RequestHeader("X-User-Id") String username) {
        NewsResDto response = newsService.updateNews(newsId, newsReqDto, username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{newsId}")
    @Operation(summary = "뉴스 삭제")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteNews(@PathVariable Long newsId,
                                           @Parameter(hidden = true) @RequestHeader("X-User-Id") String username) {
        newsService.deleteNews(newsId, username);
        return ResponseEntity.ok().build();
    }

}
