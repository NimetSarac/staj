package com.example.demo.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;        // asıl veri listesi
    private int currentPage;        // şu anki sayfa (0'dan başlar)
    private int totalPages;         // toplam sayfa sayısı
    private long totalElements;     // toplam kayıt sayısı
    private int pageSize;           // sayfa başına kayıt sayısı
    private boolean isFirst;        // ilk sayfa mı
    private boolean isLast;         // son sayfa mı
}