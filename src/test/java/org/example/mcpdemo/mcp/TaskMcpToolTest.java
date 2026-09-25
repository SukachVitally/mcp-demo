package org.example.mcpdemo.mcp;

import com.jayway.jsonpath.JsonPath;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;
import org.example.mcpdemo.BaseFunctionalTest;
import org.example.mcpdemo.constant.TaskStatus;
import org.example.mcpdemo.repository.TaskRepository;
import org.example.mcpdemo.service.TaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TaskMcpToolTest extends BaseFunctionalTest {
    private static final long UNKNOWN_ID = -1L;

    @LocalServerPort
    int port;

    private McpSyncClient client;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void connect() {
        var transport = HttpClientStreamableHttpTransport
                .builder("http://localhost:" + port)
                .httpRequestCustomizer((builder, method, endpoint, body, context) ->
                        builder.header("X-API-key", "demo." + API_KEY_SECRET))
                .build();
        client = McpClient.sync(transport).build();
        client.initialize();
    }

    @AfterEach
    void close() {
        client.closeGracefully();
    }

    @Test
    void exposesExpectedTools() {
        var names = client.listTools().tools().stream().map(McpSchema.Tool::name).toList();

        assertThat(names).contains("add_task", "get_task", "update_task_status", "get_task_list");
    }

    @Test
    void addTask() {
        var result = call("add_task", Map.of("name", "Write report"));

        assertThat(result.isError()).isFalse();

        String json = text(result);
        Integer id = JsonPath.<Integer>read(json, "$.id");
        assertThat(JsonPath.<String>read(json, "$.name")).isEqualTo("Write report");
        assertThat(JsonPath.<String>read(json, "$.status")).isEqualTo("NEW");

        assertThat(taskRepository.findById(Long.valueOf(id))).get()
                .satisfies(t -> assertThat(t.getName()).isEqualTo("Write report"));
    }

    @Test
    void addTaskWithBlankNameFails() {
        var result = call("add_task", Map.of("name", " "));

        assertThat(result.isError()).isTrue();
    }

    @Test
    void addTaskWithTooLongNameFails() {
        var result = call("add_task", Map.of("name", "a".repeat(TaskService.NAME_MAX_LENGTH + 1)));

        assertThat(result.isError()).isTrue();
    }

    @Test
    void getTask() {
        var task = taskRepository.findAllByStatus(TaskStatus.NEW).getFirst();

        var result = call("get_task", Map.of("id", task.getId()));

        assertThat(result.isError()).isFalse();

        String json = text(result);
        assertThat(JsonPath.<String>read(json, "$.name")).isEqualTo(task.getName());
        assertThat(JsonPath.<String>read(json, "$.status")).isEqualTo("NEW");
    }

    @Test
    void getUnknownTaskFails() {
        var result = call("get_task", Map.of("id", UNKNOWN_ID));

        assertThat(result.isError()).isTrue();
        assertThat(text(result)).contains("Task with id " + UNKNOWN_ID + " is not found");
    }

    @Test
    void updateTaskStatus() {
        var task = taskRepository.findAllByStatus(TaskStatus.NEW).getFirst();

        var result = call("update_task_status", Map.of("id", task.getId(), "status", TaskStatus.DONE));

        assertThat(result.isError()).isFalse();
        assertThat(JsonPath.<String>read(text(result), "$.status")).isEqualTo("DONE");

        assertThat(taskRepository.findById(task.getId())).get()
                .satisfies(t -> assertThat(t.getStatus()).isEqualTo(TaskStatus.DONE));
    }

    @Test
    void updateUnknownTaskStatusFails() {
        var result = call("update_task_status", Map.of("id", UNKNOWN_ID, "status", TaskStatus.DONE));

        assertThat(result.isError()).isTrue();
    }

    @Test
    void getTaskList() {
        var result = call("get_task_list", Map.of());

        assertThat(result.isError()).isFalse();

        int size = JsonPath.read(text(result), "$.length()");
        assertThat(size).isEqualTo(3);
    }

    private McpSchema.CallToolResult call(String tool, Map<String, Object> arguments) {
        return client.callTool(McpSchema.CallToolRequest.builder(tool).arguments(arguments).build());
    }

    private static String text(McpSchema.CallToolResult result) {
        return ((McpSchema.TextContent) result.content().getFirst()).text();
    }
}
