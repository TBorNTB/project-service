package com.sejong.projectservice.application.yorkie.service;

import com.sejong.projectservice.application.yorkie.dto.request.CheckYorkieRequest;
import com.sejong.projectservice.application.yorkie.dto.response.CheckYorkieResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class YorkieService {

  public CheckYorkieResponse checkYorkie(CheckYorkieRequest checkYorkieRequest) {
    if (checkYorkieRequest.getMethod().equals(YorkieMethod.ActivateClient)
        || checkYorkieRequest.getMethod().equals(YorkieMethod.DeactivateClient)) {
      return new CheckYorkieResponse(true, String.format("Pass %s method", checkYorkieRequest.getMethod()));
    }

    String token = checkYorkieRequest.getToken();

    // attributes는 null 일 수도 있는 값이다.
    String yorkieDocId = Optional.ofNullable(checkYorkieRequest.getAttributes())
        .filter(att -> !att.isEmpty())
        .map(att -> att.get(0).key)
        .orElseThrow(() -> new IllegalArgumentException("Document ID not found"));

    checkDefaultAccessToken(yorkieDocId, token);

    return new CheckYorkieResponse(true, "Valid Token");
  }

  private String checkDefaultAccessToken(String yorkieDocId, String token) {
    
  }

  public enum YorkieMethod {
    ActivateClient,
    DeactivateClient,
    AttachDocument,
    DetachDocument,
    WatchDocuments,
    PushPull,
  }

  public enum Verb {
    r,
    rw
  }

  public static class DocumentAttribute {

    private String key;
    private Verb verb;

    public DocumentAttribute(String key, Verb verb) {
      this.key = key;
      this.verb = verb;
    }
  }
}
