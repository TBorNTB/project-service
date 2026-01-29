package com.sejong.projectservice.domains.news.controller;

import com.sejong.projectservice.domains.news.dto.NewsReqDto;
import com.sejong.projectservice.domains.news.dto.NewsResDto;
import com.sejong.projectservice.domains.news.service.NewsService;
import com.sejong.projectservice.support.common.file.FileUploadRequest;
import com.sejong.projectservice.support.common.file.FileUploader;
import com.sejong.projectservice.support.common.file.PreSignedUrl;
import com.sejong.projectservice.support.common.pagination.CursorPageReqDto;
import com.sejong.projectservice.support.common.pagination.CursorPageRes;
import com.sejong.projectservice.support.common.pagination.OffsetPageReqDto;
import com.sejong.projectservice.support.common.pagination.OffsetPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        newsReqDto.setWriterUsername(username);
        NewsResDto response = newsService.createNews(newsReqDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/files/presigned-url")
    @Operation(summary = "파일 업로드용 PreSigned URL 생성")
    @SecurityRequirement(name = "bearerAuth")
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
    public CursorPageRes<List<NewsResDto>> getCursorNews(
            @ParameterObject @Valid CursorPageReqDto cursorPageReqDto) {

        return newsService.getCursorNews(cursorPageReqDto);
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
                                                 @Parameter(hidden = true) @RequestHeader("X-User-Id") String username) {
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
