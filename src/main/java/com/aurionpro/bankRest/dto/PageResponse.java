package com.aurionpro.bankRest.dto;

import lombok.*;

import java.util.List;


@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Getter
@Setter
public class PageResponse<T> {
	private int totalPage;
	private int size;
	private Long totalElements;
	private List<T> content;
	private boolean isLastPage;
}
