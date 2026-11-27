package se.magnus.microservices.core.review;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.mysql.MySQLContainer;

public abstract class MySqlTestBase {

  // MySQL 컨테이너는 WSL2 등에서 느리게 뜨므로 시작 타임아웃을 늘림
  @ServiceConnection
  static final MySQLContainer database =
    new MySQLContainer("mysql:9.2.0").withStartupTimeoutSeconds(300);

  static {
    database.start();
  }
}
