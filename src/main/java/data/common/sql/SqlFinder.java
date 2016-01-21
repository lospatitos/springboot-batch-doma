package data.common.sql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class SqlFinder {

    @Cacheable("sql")
    public String get(String path) {
        Resource resource = new ClassPathResource(path);
        log.info("load {}", resource);
        try (InputStream stream = resource.getInputStream()) {
            return StreamUtils.copyToString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalArgumentException(path + " is not found!", e);
        }
    }
}
