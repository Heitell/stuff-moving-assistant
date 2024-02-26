package ru.svrd.stuff_moving_assistant

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import ru.svrd.stuff_moving_assistant.domain.moving_box.MovingBox
import ru.svrd.stuff_moving_assistant.domain.moving_box.MovingBoxExtras
import ru.svrd.stuff_moving_assistant.domain.moving_box.MovingBoxService
import ru.svrd.stuff_moving_assistant.infrastructure.repository.MovingBoxRepository
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class MovingBoxServiceTest {

    @Mock
    private lateinit var movingBoxRepository: MovingBoxRepository

    @Test
    fun newBoxTest() {
        val sessionId = 1L
        val title = "Test"
        val movingBox = MovingBox(
            0,
            sessionId,
            title,
            null,
            null,
            false,
            LocalDate.now(),
            LocalDate.now(),
            MovingBoxExtras(listOf("socks", "books", "furniture"))
        )

        `when`(movingBoxRepository.saveNewMovingBox(sessionId, title)).thenReturn(movingBox)
        val movingBoxService = MovingBoxService(movingBoxRepository)
        val movingBoxActual = movingBoxService.newBox(sessionId, title)
        assertEquals(sessionId, movingBoxActual.sessionId)
    }
}