package eu.randomred.trupsbackend.util;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

public class PaginationUtil {

    private PaginationUtil() {}

    public static <T> ResponseEntity<List<T>> buildResponse(Page<T> response) {
        return new ResponseEntity<>(response.stream().toList(), buildHeaders(response), HttpStatus.OK);
    }

    private static <T> HttpHeaders buildHeaders(Page<T> response) {
        return new HttpHeaders(MultiValueMap.fromSingleValue(Map.of(
                "X-Total-Count", String.valueOf(response.getTotalElements()),
                "X-Current-Page", String.valueOf(response.getPageable().getPageNumber()),
                "X-Page-Size", String.valueOf(response.getPageable().getPageSize())
        )));
    }

}
