package ru.svrd.stuff_moving_assistant

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.svrd.stuff_moving_assistant.domain.moving_box.CreateMovingBoxDto
import ru.svrd.stuff_moving_assistant.domain.moving_box.MovingBox
import ru.svrd.stuff_moving_assistant.domain.moving_box.MovingBoxExtras
import ru.svrd.stuff_moving_assistant.domain.moving_session.CreateMovingSessionDto
import ru.svrd.stuff_moving_assistant.domain.moving_session.CreateMovingSessionResponse

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class IntegrationTest {

    @LocalServerPort
    var port: Int? = null

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    companion object {
        @JvmStatic
        @Container
        private val postgreDb = PostgreSQLContainer("postgres:15-alpine")
            .withDatabaseName("stuff-moving-assistant")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry){
            registry.add("spring.datasource.master.hikari.jdbc-url", postgreDb::getJdbcUrl)
            registry.add("spring.datasource.master.hikari.username", postgreDb::getUsername)
            registry.add("spring.datasource.master.hikari.password", postgreDb::getPassword)
        }
    }

    @Test
    fun createNewMovingBoxTest() {
        val sessionBody = CreateMovingSessionDto("test")
        val apiSession = testRestTemplate.postForEntity(
            "http://localhost:$port/api/v1/moving/session",
            sessionBody,
            CreateMovingSessionResponse::class.java)

        val movingBoxBody = CreateMovingBoxDto("test")
        val movingBox = testRestTemplate.postForEntity(
            "http://localhost:$port/api/v1/moving/session/${apiSession.body!!.id}/newBox",
            movingBoxBody,
            MovingBox::class.java
        )

        Assertions.assertEquals(200, movingBox.statusCode.value())
        Assertions.assertNotNull(movingBox.body!!.id)
        Assertions.assertEquals("test", movingBox.body!!.title)
    }

    @Test
    fun getBoxesInSessionTest() {
        val sessionBody = CreateMovingSessionDto("test")
        val apiSession = testRestTemplate.postForEntity(
            "http://localhost:$port/api/v1/moving/session",
            sessionBody,
            CreateMovingSessionResponse::class.java)

        val movingBoxBody = CreateMovingBoxDto("test")
        val movingBox = testRestTemplate.postForEntity(
            "http://localhost:$port/api/v1/moving/session/${apiSession.body!!.id}/newBox",
            movingBoxBody,
            MovingBox::class.java
        )
        Assertions.assertEquals(200, movingBox.statusCode.value())

        val movingBoxBody2 = CreateMovingBoxDto("test2")
        val movingBox2 = testRestTemplate.postForEntity(
            "http://localhost:$port/api/v1/moving/session/${apiSession.body!!.id}/newBox",
            movingBoxBody2,
            MovingBox::class.java
        )
        Assertions.assertEquals(200, movingBox2.statusCode.value())

        val actualBoxesInSession = testRestTemplate.getForEntity(
            "http://localhost:$port/api/v1/moving/session/${apiSession.body!!.id}/boxes",
            List::class.java
        )
        Assertions.assertEquals(200, actualBoxesInSession.statusCode.value())
        Assertions.assertEquals(2, actualBoxesInSession.body!!.size)
    }

    @Test
    fun editBoxItemsTest() {
        val sessionBody = CreateMovingSessionDto("test")
        val apiSession = testRestTemplate.postForEntity(
            "http://localhost:$port/api/v1/moving/session",
            sessionBody,
            CreateMovingSessionResponse::class.java)

        val movingBoxBody = CreateMovingBoxDto("test")
        val movingBox = testRestTemplate.postForEntity(
            "http://localhost:$port/api/v1/moving/session/${apiSession.body!!.id}/newBox",
            movingBoxBody,
            MovingBox::class.java
        )

        val movingBoxExtras = MovingBoxExtras(listOf("soks", "books", "TV"))
        val editMovingBoxExtras = testRestTemplate.postForEntity(
            "http://localhost:$port/api/v1/moving/session/${movingBox.body!!.sessionId}/box/${movingBox.body!!.id}/editItems",
            movingBoxExtras,
            MovingBox::class.java
        )
        Assertions.assertEquals(200, editMovingBoxExtras.statusCode.value())
        Assertions.assertEquals(listOf("soks", "books", "TV"), editMovingBoxExtras.body!!.extras.items)
    }
}