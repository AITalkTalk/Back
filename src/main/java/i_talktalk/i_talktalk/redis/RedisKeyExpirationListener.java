package i_talktalk.i_talktalk.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import i_talktalk.i_talktalk.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisKeyExpirationListener implements MessageListener {

    private final QuizService quizService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("TTL 발생!!!");
        String expiredKey = new String(message.getBody(), StandardCharsets.UTF_8);
        if (expiredKey.startsWith("quiz:solved:")) {
            Long userId = Long.parseLong(expiredKey.replace("quiz:solved:", ""));
            String backupKey = "quiz:solved:backup:" + userId;
            try {
                quizService.handleSolvedQuizzes(userId, backupKey);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
