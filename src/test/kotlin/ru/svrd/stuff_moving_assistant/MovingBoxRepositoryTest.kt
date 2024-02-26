package ru.svrd.stuff_moving_assistant

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.svrd.stuff_moving_assistant.infrastructure.repository.MovingBoxJDBCRepository

@JdbcTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/session.sql")
class MovingBoxRepositoryTest {

    companion object{
        @JvmStatic
        @Container
        private val postgreDb = PostgreSQLContainer("postgres:15-alpine")
            .withDatabaseName("stuff-moving-assistant")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry){
            registry.add("spring.datasource.url", postgreDb::getJdbcUrl)
            registry.add("spring.datasource.username", postgreDb::getUsername)
            registry.add("spring.datasource.password", postgreDb::getPassword)
        }
    }

    @Autowired
    private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

    var objectMapper = jacksonObjectMapper()

    @Test
    fun saveNewMovingBoxTest() {
        val testRepo = MovingBoxJDBCRepository(namedParameterJdbcTemplate, objectMapper)
        val movingBox = testRepo.saveNewMovingBox(1L, "Test")
        assertEquals(1L, movingBox!!.sessionId)
    }
}