package com.example.awsNotifyPractice2.repository;

import com.example.awsNotifyPractice2.entity.Member;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotifyRepository {
    private final DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<Member> memberTable;

    @PostConstruct
    public void init() {
        memberTable = enhancedClient.table("Member-pmk", TableSchema.fromBean(Member.class));
    }

    public void save(Member memberData) {
        memberTable.putItem(memberData);
    }

}
