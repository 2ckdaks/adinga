package com.adinga.notification_service.service;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 최근 알림을 메모리에 보관하는 간단한 링버퍼.
 * - 선입선출(FIFO), 최대 N개 유지
 * - 프로세스 재시작 시 초기화됨(영속 필요하면 DB/Redis로 교체)
 */
@Service
public class RecentNotificationStore {

    private static final int MAX_SIZE = 200; // 필요 시 조절
    private final Deque<Item> q = new ConcurrentLinkedDeque<>();

    @Data @Builder
    public static class Item {
        private Long ruleId;         // null 허용
        private String ruleName;     // null 허용
        private Instant occurredAt;  // null 가능 → API 응답 전에 보정
    }

    public void add(Long ruleId, String ruleName, Instant occurredAt) {
        var item = Item.builder()
                .ruleId(ruleId)
                .ruleName(ruleName)
                .occurredAt(occurredAt != null ? occurredAt : Instant.now())
                .build();

        q.addLast(item);
        // 사이즈 초과 시 앞에서 제거
        while (q.size() > MAX_SIZE) {
            q.pollFirst();
        }
    }

    /** 최신순(desc)로 반환 */
    public java.util.List<Item> list() {
        var out = new ArrayList<Item>(q.size());
        // Deque은 앞이 오래된 것 → 뒤에서부터 꺼내 최신순 반환
        q.descendingIterator().forEachRemaining(out::add);
        return out;
    }

    public void clear() {
        q.clear();
    }
}
