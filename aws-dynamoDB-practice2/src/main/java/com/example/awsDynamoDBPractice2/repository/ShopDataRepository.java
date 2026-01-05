package com.example.awsDynamoDBPractice2.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.awsDynamoDBPractice2.entity.ShopData;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
@RequiredArgsConstructor
public class ShopDataRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<ShopData> shopTable;

    @PostConstruct
    public void init() {
        shopTable = enhancedClient.table("ShopData-pmk", TableSchema.fromBean(ShopData.class));
    }

    // 트랜잭션으로 ShopData 일괄 저장
    public void save(List<ShopData> shopDataList) {
        enhancedClient.transactWriteItems(builder ->
                shopDataList.forEach(shopData ->
                        builder.addPutItem(shopTable, shopData)  // 엔티티 하나씩 전달해서 저장
                )
        );
    }

    public List<ShopData> findById(String orderId) {
        Key key = Key.builder()
                .partitionValue(orderId)
                .build();
        QueryConditional conditional = QueryConditional.keyEqualTo(key);
        return shopTable.query(req -> req.queryConditional(conditional))
                .items()  // <-- 결과는 SdkIterable<ShopData> 타입
                .stream()  // <-- 스트림 처리해서
                .toList();  // <-- List<ShopData>로 변환
    }
}