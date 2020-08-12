package no.fint.consumer.admin

import no.fint.consumer.event.ConsumerEventUtil
import no.fint.consumer.event.SynchronousEvents
import no.fint.event.model.DefaultActions
import no.fint.event.model.Event
import no.fint.event.model.HeaderConstants
import no.fint.test.utils.MockMvcSpecification
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import java.util.concurrent.BlockingQueue

class AdminControllerSpec extends MockMvcSpecification {
    private AdminController healthController
    private MockMvc mockMvc
    private SynchronousEvents synchronousEvents
    private BlockingQueue queue

    void setup() {
        synchronousEvents = Mock()
        queue = Mock()
        healthController = new AdminController(
                synchronousEvents: synchronousEvents,
                consumerEventUtil: Mock(ConsumerEventUtil)
        )
        mockMvc = MockMvcBuilders.standaloneSetup(healthController).build()
    }

    def "Check response on healthcheck"() {
        when:
        def response = mockMvc.perform(get("/admin/health").header(HeaderConstants.ORG_ID, "mock.no").header(HeaderConstants.CLIENT, "mock"))

        then:
        1 * synchronousEvents.register(_ as Event) >> queue
        1 * queue.poll(_, _) >> new Event(action: DefaultActions.HEALTH.name())
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.action')
                .value(equalTo('HEALTH')))
    }

    def "Check response on healthcheck is empty"() {
        when:
        def response = mockMvc.perform(get("/admin/health").header(HeaderConstants.ORG_ID, "mock.no").header(HeaderConstants.CLIENT, "mock"))

        then:
        1 * synchronousEvents.register(_ as Event) >> queue
        1 * queue.poll(_, _) >> null
        response.andExpect(status().is5xxServerError())
                .andExpect(jsonPath('$.action').value(equalTo('HEALTH')))
                .andExpect(jsonPath('$.message').value(equalTo('No response from adapter')))
    }
}
