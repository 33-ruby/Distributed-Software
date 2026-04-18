import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootTest(classes = RedisInitTest.RedisConfig.class)
public class RedisInitTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void initStock() {
        String key = "seckill:stock:1";
        redisTemplate.opsForValue().set(key, "10");

        String stock = redisTemplate.opsForValue().get(key);
        System.out.println(">>> Redis 库存初始化完成，当前库存：" + stock);
    }

    @Configuration
    static class RedisConfig {

        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            return new LettuceConnectionFactory();
        }

        @Bean
        public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
            RedisTemplate<String, String> template = new RedisTemplate<>();
            template.setConnectionFactory(factory);

            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new StringRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setHashValueSerializer(new StringRedisSerializer());

            template.afterPropertiesSet();
            return template;
        }
    }
}