package ru.svrd.stuff_moving_assistant

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper
import ru.svrd.stuff_moving_assistant.application.api.MovingControllerV1
import ru.svrd.stuff_moving_assistant.domain.moving_box.MovingBoxService
import ru.svrd.stuff_moving_assistant.domain.moving_session.CreateMovingSessionDto
import ru.svrd.stuff_moving_assistant.domain.moving_session.CreateMovingSessionResponse
import ru.svrd.stuff_moving_assistant.domain.moving_session.MovingSessionService
import java.time.LocalDate

@WebMvcTest(MovingControllerV1::class)
class MovingBoxControllerTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mvc: MockMvc

    @MockBean
    lateinit var movingSessionService: MovingSessionService

    @MockBean
    lateinit var  movingBoxService: MovingBoxService

    @Test
    fun createNewMovingSessionTest() {
        `when`(movingSessionService.newSession("test", ownerId = 1)).thenReturn(
            CreateMovingSessionResponse(
                id = 10L,
                title = "test",
                createdAt = LocalDate.now()
            )
        )

        val body = CreateMovingSessionDto("test")
        mvc.perform(post("/api/v1/moving/session").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(200))
    }
}