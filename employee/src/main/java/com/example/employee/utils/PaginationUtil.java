package com.example.employee.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.employee.dtos.response.PaginatedResponse;

public class PaginationUtil {

  public static Pageable createPageable(int requestedPage, int size) {
    int zeroBasedPage = Math.max(requestedPage - 1, 0);
    return PageRequest.of(zeroBasedPage, size);
  }

  public static <T> PaginatedResponse<T> buildResponse(Page<T> page, int requestedPage, int size, String baseUrl) {

    String nextUrl = page.hasNext()
        ? String.format("%s?page=%d&size=%d", baseUrl, requestedPage + 1, size)
        : null;

    String preUrl = page.hasPrevious()
        ? String.format("%s?page=%d&size=%d", baseUrl, requestedPage - 1, size)
        : null;

    return new PaginatedResponse<>(
        page.getContent(),
        page.getNumber() + 1,
        page.getTotalPages(),
        page.getNumberOfElements(),
        page.hasNext(),
        page.hasPrevious(),
        nextUrl,
        preUrl);
  }
}
