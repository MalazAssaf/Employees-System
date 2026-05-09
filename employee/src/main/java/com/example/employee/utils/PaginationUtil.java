package com.example.employee.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.employee.dtos.response.PaginatedResponse;
import com.example.employee.shared.CustomResponseException;

public class PaginationUtil {

  public static Pageable createPageable(int requestedPage, int size) {
    if (requestedPage < 1) {
      throw CustomResponseException.badRequestException("Page must be >= 1");
    }
    if (size <= 0) {
      throw CustomResponseException.badRequestException("Size must be greater than 0");
    }
    int zeroBasedPage = requestedPage - 1;
    return PageRequest.of(zeroBasedPage, size);
  }

  public static <T> PaginatedResponse<T> buildResponse(Page<T> page, int requestedPage, int size, String baseUrl) {

    if (requestedPage < 1) {
      throw CustomResponseException.badRequestException("Page must be >= 1");
    }
    if (size <= 0) {
      throw CustomResponseException.badRequestException("Size must be greater than 0");
    }
    if (baseUrl == null || baseUrl.isBlank()) {
      throw CustomResponseException.badRequestException("baseUrl must not be empty");
    }

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
