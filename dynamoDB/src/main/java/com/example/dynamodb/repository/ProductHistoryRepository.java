package com.example.dynamodb.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.dynamodb.entity.ProductHistory;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
@RequiredArgsConstructor
public class ProductHistoryRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<ProductHistory> historyTable;

    @PostConstruct
    public void init() {
        this.historyTable = enhancedClient.table("ProductHistory-pmk", TableSchema.fromBean(ProductHistory.class));
    }

    /**
     * 상품 조회 이력 저장
     * DynamoDB 특성상 PK(userId)와 SK(viewTime)가 동일할 경우 데이터가 덮어씌워집니다 (Insert + Update -> Upsert).
     *
     * 여기서 고려할 사항.
     * 어떤 사람이 상품 조회를 찰나의 순간에 여러 번 한다면(SK인 viewTime값이 같은 경우) 기존 데이터는 덮어쓰기 당합니다.
     * 누가 찰나의 순간에 상품을 조회할 수 있겠냐고 생각하시겠지만,
     * 매크로를 쓴다던가, 이벤트에 눈이 멀어 실제로 광클을 한다건가, 아니면 애플리케이션이 네트워크 불안 등 문제로 여러 번 요청할 수도 있습니다.
     * 그렇기 때문에 viewTime이 큰 차이가 없는 경우 기존 조회 내역이 없어질 수 있습니다.
     *
     * 해결은
     * 1. viewTime 생성할 때마다 시간 + UUID 같은 중복 배제 값을 줘서 처리하기
     * 2. 또는 다른 제품을 찰나의 순간에 조회하는 경우도 있을 수 있으므로, viewTime 뒤에 제품 ID 추가하여 중복 값을 배제하기 등을 고려할 수 있습니다.
     */
    public void save(ProductHistory history) {
        // putItem() 메서드: PK + SK가 같으면 덮어쓰기로 동작
        historyTable.putItem(history);

    }

    /**
     * 사용자별 최근 조회 이력 쿼리
     * getItem()은 단 건 조회만 가능합니다.
     * 이 조회는 다중 조회이므로 QueryConditional를 이용해야 합니다.
     * @param userId 사용자 ID
     * @param scanIndexForward true: 오름차순(과거순), false: 내림차순(최신순)
     */
    public List<ProductHistory> findByUserId(String userId, boolean scanIndexForward) {
        // 키
        Key key = Key.builder()
                .partitionValue(userId)  // SK가 없어도 동일 사용자의 모든 데이터는 조회 가능
                .build();

        // 쿼리 조건 (Key와 일치하는 값을 조회하는 WHERE 문: WHERE PK = 'userId')
        QueryConditional queryConditional = QueryConditional.keyEqualTo(key);

        // 쿼리 실행
        // scanIndexForward=true: 과거순
        // scanIndexForward=false: 최신순
        return historyTable.query(req -> req
                        .queryConditional(queryConditional)
                        .scanIndexForward(scanIndexForward))
                .items()  // 쿼리 결과 반환 (ProductHistory가 여러 개 있는 SdkIterable 타입: 스트림 처리해서 List로 바꿔서 반환)
                .stream()
                .collect(Collectors.toList());
    }
}
