package cn.luzai.aircraftpojo.entity.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 幂等表实体类
 * 对应表：airline-a/b/c.idempotency
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Idempotency {

    /**
     * 客户端幂等Token（UUID）
     */
    private String clientToken;

    /**
     * 请求体哈希（MD5）
     */
    private String requestHash;

    /**
     * 首次响应结果（JSON）
     */
    private String responseBody;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 过期时间（24小时）
     */
    private LocalDateTime expireAt;
}
