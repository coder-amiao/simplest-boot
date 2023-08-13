package cn.soboys.simplest.jpa.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.EntityManager;

/**
 * @author 公众号 程序员三时
 * @version 1.0
 * @date 2023/7/20 15:21
 * @webSite https://github.com/coder-amiao
 * 每当你保存或更新实体时，createdDate 和 lastModifiedDate 字段就会自动更新。
 * @CreatedDate 只在实体首次持久化时设置，而 @LastModifiedDate 在每次更新实体时都会被设置。
 */
@EnableJpaAuditing
public class JpaConfig {
    //让Spring管理JPAQueryFactory
    @Bean
    public JPAQueryFactory jpaQueryFactory(@Autowired(required = false) EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }

}
