package tests;

import com.google.gson.Gson;
import manager.HttpTaskServer;
import manager.KVServer;
import org.junit.jupiter.api.*;
import tasksTypes.Epic;
import tasksTypes.Subtask;
import tasksTypes.Task;
import tasksTypes.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class HttpTaskServerTest {
    private static KVServer kvServer;
    private static HttpTaskServer httpTaskServer;
    private HttpClient client;
    private Task task1;
    private Task task2;
    private Epic epic;
    private Epic epic1;
    private Subtask subtask;
    private Subtask subtask1;
    private Gson gson;
    private static final String BASE_URL = "http://localhost:8080/tasks/";

    @BeforeAll
    static void startServer() throws IOException {
            kvServer = new KVServer();
            kvServer.start();
            httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
    }

    @BeforeEach
    public void BeforeEach() {
        client = HttpClient.newHttpClient();
        gson = new Gson();
        task1 = new Task("Задача1", "Описание1", TaskStatus.NEW,
                LocalDateTime.of(2022, 7, 9, 22, 15), 60);
        task2 = new Task("Задача2", "Описание2", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, 7, 20, 12, 0), 90);
        epic = new Epic("Epic1", "Descr1", null,0);
        epic1 = new Epic("Epic2", "Descr2", null,0);
        subtask = new Subtask("Subtask", "Descr", TaskStatus.NEW,
                LocalDateTime.of(2022,8,20,15,30), 120, epic.getId());
        subtask1 = new Subtask(subtask.getId(), "Subtask", "Descr", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022,8,20,15,30), 120, epic.getId());
    }

    @AfterEach
    public void AfterEach() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(BASE_URL + "task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        url = URI.create(BASE_URL + "epic");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        url = URI.create(BASE_URL + "subtask");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @AfterAll
    static void stopServer() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    /*
    Не смог добиться, что бы тесты, запускаемые все вместе, были зеленые: если запускать по одному,
    то тесты зеленые, если сразу все - то красные. В дебаге не смог увидеть отличий между одиночным и групповым запуском
    Прошу помочь: указать, в каком направлении смотреть для решения проблемы. Спасибо!
     */

    @Test
    public void httpGetPrioritiesTest() throws IOException, InterruptedException {
        URI url = URI.create(BASE_URL + "task");
        task1.setId(0);
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        task1.setId(1);
        String json1 = gson.toJson(task2);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(body1).build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url1 = URI.create(BASE_URL + "priorities");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        String jsonP = response2.body();
        List<Task> priorities = gson.fromJson(jsonP, List.class);
        assertEquals(200, response2.statusCode(), "Неверный статус-код при получении приоритетов.");
        assertEquals(2, priorities.size(), "Неверное количество задач в списке приоритетов.");
    }

    @Test
    public void httpGetHistoryTest() throws IOException, InterruptedException {
        URI url = URI.create(BASE_URL + "task");
        task1.setId(0);
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI url1 = URI.create(BASE_URL + "task?id=" + task1.getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).GET().build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        URI url2 = URI.create(BASE_URL + "history");
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        json = response2.body();
        List<Task> history = gson.fromJson(json, List.class);
        assertEquals(200, response2.statusCode(), "Неверный статус-код при получении истории.");
        assertEquals(1, history.size(),"Неверное количество задач в истории.");
    }

    @Test
    public void httpDeleteAllTaskTypesTest() throws IOException, InterruptedException {
        URI url4 = URI.create(BASE_URL + "epic");
        epic.setId(0);
        String json1 = gson.toJson(epic);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).POST(body1).build();
        HttpResponse<String> response1 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "Неверный статус-код при создании задачи.");

        URI url2 = URI.create(BASE_URL + "subtask");
        subtask.setId(1);
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode(), "Неверный статус-код при создании подзадачи.");

        URI url3 = URI.create(BASE_URL + "subtask?id=" + subtask.getId());
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response3.statusCode(), "Неверный статус-код при получении подзадачи.");

        URI url5 = URI.create(BASE_URL + "epic?id=" + epic.getId());
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response5.statusCode(), "Неверный статус-код при получении эпика'.");

        HttpRequest request8 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());
        String json8 = response8.body();
        List<Task> subtasks = gson.fromJson(json8, List.class);
        assertEquals(200, response8.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(1, subtasks.size(),"Неверное количество задач в списке SubTask.");

        HttpRequest request9 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response9 = client.send(request9, HttpResponse.BodyHandlers.ofString());
        String json9 = response9.body();
        List<Task> epics = gson.fromJson(json9, List.class);
        assertEquals(200, response9.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(1, epics.size(),"Неверное количество задач в списке Epic.");

        URI url10 = URI.create(BASE_URL + "all");
        HttpRequest request10 = HttpRequest.newBuilder().uri(url10).DELETE().build();
        HttpResponse<String> response10 = client.send(request10, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response10.statusCode(), "Ошибка при удалении всех задач");

        HttpRequest request11 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response11 = client.send(request11, HttpResponse.BodyHandlers.ofString());
        String json11 = response11.body();
        List<Task> subtasksEmpty = gson.fromJson(json11, List.class);
        assertEquals(200, response11.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(0, subtasksEmpty.size(),"Неверное количество задач в списке Task.");

        HttpRequest request12 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response12 = client.send(request12, HttpResponse.BodyHandlers.ofString());
        String json12 = response12.body();
        List<Task> epicsEmpty = gson.fromJson(json12, List.class);
        assertEquals(200, response12.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(0, epicsEmpty.size(),"Неверное количество задач в списке Epic.");

        URI url6 = URI.create(BASE_URL + "history");
        HttpRequest request6 = HttpRequest.newBuilder().uri(url6).GET().build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
        String json6 = response6.body();
        List<Task> history = gson.fromJson(json6, List.class);
        assertEquals(200, response6.statusCode(), "Неверный статус-код при получении истории.");
        assertEquals(0, history.size(),"Неверное количество задач в истории.");

        URI url7 = URI.create(BASE_URL + "priorities");
        HttpRequest request7 = HttpRequest.newBuilder().uri(url7).GET().build();
        HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());
        String jsonP = response7.body();
        List<Task> priorities = gson.fromJson(jsonP, List.class);
        assertEquals(200, response7.statusCode(), "Неверный статус-код при получении приоритетов.");
        assertEquals(0, priorities.size(), "Неверное количество задач в списке приоритетов.");
    }

    @Test
    public void httpDeleteEpicByIdTest() throws IOException, InterruptedException {
        URI url4 = URI.create(BASE_URL + "epic");
        epic.setId(0);
        String json1 = gson.toJson(epic);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).POST(body1).build();
        HttpResponse<String> response1 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "Неверный статус-код при создании задачи.");

        URI url5 = URI.create(BASE_URL + "epic?id=" + epic.getId());
        HttpRequest request5 = HttpRequest.newBuilder().uri(url5).GET().build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        String jsonEpic = response5.body();
        Task epicReceived = gson.fromJson(jsonEpic, Epic.class);
        assertEquals(200, response5.statusCode(), "Неверный статус-код при получении эпика'.");
        assertNotEquals(epic, epicReceived, "Задачи не совпадают.");

        URI url2 = URI.create(BASE_URL + "subtask");
        subtask.setId(1);
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode(), "Неверный статус-код при создании подзадачи.");

        URI url3 = URI.create(BASE_URL + "subtask?id=" + subtask.getId());
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response3.statusCode(), "Неверный статус-код при получении подзадачи.");

        HttpRequest request6 = HttpRequest.newBuilder().uri(url5).DELETE().build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response6.statusCode(), "Ошибка при удалении задачи");

        HttpRequest request12 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response12 = client.send(request12, HttpResponse.BodyHandlers.ofString());
        String json12 = response12.body();
        List<Task> epicsEmpty = gson.fromJson(json12, List.class);
        assertEquals(200, response12.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(0, epicsEmpty.size(),"Неверное количество задач в списке Epic.");

        HttpRequest request11 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response11 = client.send(request11, HttpResponse.BodyHandlers.ofString());
        String json11 = response11.body();
        List<Task> subtasksEmpty = gson.fromJson(json11, List.class);
        assertEquals(200, response11.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(0, subtasksEmpty.size(),"Неверное количество задач в списке Task.");

        URI url6 = URI.create(BASE_URL + "history");
        HttpRequest request8 = HttpRequest.newBuilder().uri(url6).GET().build();
        HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());
        String json8 = response8.body();
        List<Task> history = gson.fromJson(json8, List.class);
        assertEquals(200, response8.statusCode(), "Неверный статус-код при получении истории.");
        assertEquals(0, history.size(),"Неверное количество задач в истории.");

        URI url7 = URI.create(BASE_URL + "priorities");
        HttpRequest request7 = HttpRequest.newBuilder().uri(url7).GET().build();
        HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());
        String jsonP = response7.body();
        List<Task> priorities = gson.fromJson(jsonP, List.class);
        assertEquals(200, response7.statusCode(), "Неверный статус-код при получении приоритетов.");
        assertEquals(0, priorities.size(), "Неверное количество задач в списке приоритетов.");
    }

    @Test
    public void httpDeleteAllEpicsTest() throws IOException, InterruptedException {
        URI url4 = URI.create(BASE_URL + "epic");
        epic.setId(0);
        String json1 = gson.toJson(epic);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).POST(body1).build();
        HttpResponse<String> response1 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "Неверный статус-код при создании задачи.");

        URI url2 = URI.create(BASE_URL + "subtask");
        subtask.setId(1);
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode(), "Неверный статус-код при создании подзадачи.");

        HttpRequest request5 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response5 = client.send(request5, HttpResponse.BodyHandlers.ofString());
        String json5 = response5.body();
        List<Task> epics = gson.fromJson(json5, List.class);
        assertEquals(200, response5.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(1, epics.size(),"Неверное количество задач в списке Epic.");

        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        String json3 = response3.body();
        List<Task> subtasks = gson.fromJson(json3, List.class);
        assertEquals(200, response3.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(1, subtasks.size(),"Неверное количество задач в списке subTask.");

        HttpRequest request6 = HttpRequest.newBuilder().uri(url4).DELETE().build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response6.statusCode(), "Ошибка при удалении задачи");

        HttpRequest request12 = HttpRequest.newBuilder().uri(url4).GET().build();
        HttpResponse<String> response12 = client.send(request12, HttpResponse.BodyHandlers.ofString());
        String json12 = response12.body();
        List<Task> epicsEmpty = gson.fromJson(json12, List.class);
        assertEquals(200, response12.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(0, epicsEmpty.size(),"Неверное количество задач в списке Epic.");

        HttpRequest request11 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response11 = client.send(request11, HttpResponse.BodyHandlers.ofString());
        String json11 = response11.body();
        List<Task> subtasksEmpty = gson.fromJson(json11, List.class);
        assertEquals(200, response11.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(0, subtasksEmpty.size(),"Неверное количество задач в списке subTask.");

        URI url6 = URI.create(BASE_URL + "history");
        HttpRequest request8 = HttpRequest.newBuilder().uri(url6).GET().build();
        HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());
        String json8 = response8.body();
        List<Task> history = gson.fromJson(json8, List.class);
        assertEquals(200, response8.statusCode(), "Неверный статус-код при получении истории.");
        assertEquals(0, history.size(),"Неверное количество задач в истории.");

        URI url7 = URI.create(BASE_URL + "priorities");
        HttpRequest request7 = HttpRequest.newBuilder().uri(url7).GET().build();
        HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());
        String jsonP = response7.body();
        List<Task> priorities = gson.fromJson(jsonP, List.class);
        assertEquals(200, response7.statusCode(), "Неверный статус-код при получении приоритетов.");
        assertEquals(0, priorities.size(), "Неверное количество задач в списке приоритетов.");
    }

    @Test
    public void httpDeleteSubtaskByIdTest() throws IOException, InterruptedException {
        URI url4 = URI.create(BASE_URL + "epic");
        epic.setId(0);
        String json1 = gson.toJson(epic);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).POST(body1).build();
        HttpResponse<String> response1 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "Неверный статус-код при создании задачи.");

        URI url2 = URI.create(BASE_URL + "subtask");
        subtask.setId(1);
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode(), "Неверный статус-код при создании подзадачи.");

        URI url3 = URI.create(BASE_URL + "subtask?id=" + subtask.getId());
        HttpRequest request3 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        String jsonSubtask = response3.body();
        Task subtaskReceived = gson.fromJson(jsonSubtask, Subtask.class);
        assertEquals(200, response3.statusCode(), "Неверный статус-код при получении подзадачи.");
        assertEquals(subtask, subtaskReceived, "Задачи не совпадают.");

        HttpRequest request6 = HttpRequest.newBuilder().uri(url3).DELETE().build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response6.statusCode(), "Ошибка при удалении задачи");

        HttpRequest request11 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response11 = client.send(request11, HttpResponse.BodyHandlers.ofString());
        String json11 = response11.body();
        List<Task> subtasksEmpty = gson.fromJson(json11, List.class);
        assertEquals(200, response11.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(0, subtasksEmpty.size(),"Неверное количество задач в списке Task.");

        URI url6 = URI.create(BASE_URL + "history");
        HttpRequest request8 = HttpRequest.newBuilder().uri(url6).GET().build();
        HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());
        String json8 = response8.body();
        List<Task> history = gson.fromJson(json8, List.class);
        assertEquals(200, response8.statusCode(), "Неверный статус-код при получении истории.");
        assertEquals(1, history.size(),"Неверное количество задач в истории.");

        URI url7 = URI.create(BASE_URL + "priorities");
        HttpRequest request7 = HttpRequest.newBuilder().uri(url7).GET().build();
        HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());
        String jsonP = response7.body();
        List<Task> priorities = gson.fromJson(jsonP, List.class);
        assertEquals(200, response7.statusCode(), "Неверный статус-код при получении приоритетов.");
        assertEquals(0, priorities.size(), "Неверное количество задач в списке приоритетов.");
    }

    @Test
    public void httpDeleteAllSubtasksTest() throws IOException, InterruptedException {
        URI url4 = URI.create(BASE_URL + "epic");
        epic.setId(0);
        String json1 = gson.toJson(epic);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).POST(body1).build();
        HttpResponse<String> response1 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "Неверный статус-код при создании задачи.");

        URI url2 = URI.create(BASE_URL + "subtask");
        subtask.setId(1);
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode(), "Неверный статус-код при создании подзадачи.");

        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        String json3 = response3.body();
        List<Task> subtasks = gson.fromJson(json3, List.class);
        assertEquals(200, response3.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(1, subtasks.size(),"Неверное количество задач в списке Task.");

        HttpRequest request6 = HttpRequest.newBuilder().uri(url2).DELETE().build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response6.statusCode(), "Ошибка при удалении задачи");

        HttpRequest request11 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response11 = client.send(request11, HttpResponse.BodyHandlers.ofString());
        String json11 = response11.body();
        List<Task> subtasksEmpty = gson.fromJson(json11, List.class);
        assertEquals(200, response11.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(0, subtasksEmpty.size(),"Неверное количество задач в списке Task.");

        URI url6 = URI.create(BASE_URL + "history");
        HttpRequest request8 = HttpRequest.newBuilder().uri(url6).GET().build();
        HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());
        String json8 = response8.body();
        List<Task> history = gson.fromJson(json8, List.class);
        assertEquals(200, response8.statusCode(), "Неверный статус-код при получении истории.");
        assertEquals(1, history.size(),"Неверное количество задач в истории.");

        URI url7 = URI.create(BASE_URL + "priorities");
        HttpRequest request7 = HttpRequest.newBuilder().uri(url7).GET().build();
        HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());
        String jsonP = response7.body();
        List<Task> priorities = gson.fromJson(jsonP, List.class);
        assertEquals(200, response7.statusCode(), "Неверный статус-код при получении приоритетов.");
        assertEquals(0, priorities.size(), "Неверное количество задач в списке приоритетов.");
    }

    @Test
    public void  httpDeleteTaskByIdTest() throws IOException, InterruptedException {
        URI url = URI.create(BASE_URL + "task");
        task1.setId(0);
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        URI url1 = URI.create(BASE_URL + "task?id=" + task1.getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        String jsonTask = response1.body();
        Task taskReceived = gson.fromJson(jsonTask, Task.class);
        assertEquals(200, response1.statusCode(), "Неверный статус-код при получении подзадачи.");
        assertEquals(task1, taskReceived, "Задачи не совпадают.");

        HttpRequest request6 = HttpRequest.newBuilder().uri(url1).DELETE().build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response6.statusCode(), "Ошибка при удалении задачи");

        HttpRequest request11 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response11 = client.send(request11, HttpResponse.BodyHandlers.ofString());
        String json11 = response11.body();
        List<Task> tasksEmpty = gson.fromJson(json11, List.class);
        assertEquals(200, response11.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(0, tasksEmpty.size(),"Неверное количество задач в списке Task.");

        URI url6 = URI.create(BASE_URL + "history");
        HttpRequest request9 = HttpRequest.newBuilder().uri(url6).GET().build();
        HttpResponse<String> response9 = client.send(request9, HttpResponse.BodyHandlers.ofString());
        String json9 = response9.body();
        List<Task> history = gson.fromJson(json9, List.class);
        assertEquals(200, response9.statusCode(), "Неверный статус-код при получении истории.");
        assertEquals(0, history.size(),"Неверное количество задач в истории.");

        URI url7 = URI.create(BASE_URL + "priorities");
        HttpRequest request7 = HttpRequest.newBuilder().uri(url7).GET().build();
        HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());
        String jsonP = response7.body();
        List<Task> priorities = gson.fromJson(jsonP, List.class);
        assertEquals(200, response7.statusCode(), "Неверный статус-код при получении приоритетов.");
        assertEquals(0, priorities.size(), "Неверное количество задач в списке приоритетов.");
    }

    @Test
    public void httpDeleteAllTasksTest() throws IOException, InterruptedException {
        URI url = URI.create(BASE_URL + "task");
        task1.setId(0);
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request8 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response8 = client.send(request8, HttpResponse.BodyHandlers.ofString());
        String json8 = response8.body();
        List<Task> tasks = gson.fromJson(json8, List.class);
        assertEquals(200, response8.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(1, tasks.size(),"Неверное количество задач в списке Task.");

        HttpRequest request6 = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response6 = client.send(request6, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response6.statusCode(), "Ошибка при удалении задачи");

        HttpRequest request11 = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response11 = client.send(request11, HttpResponse.BodyHandlers.ofString());
        String json11 = response11.body();
        List<Task> tasksEmpty = gson.fromJson(json11, List.class);
        assertEquals(200, response11.statusCode(), "Неверный статус-код при получении списка.");
        assertEquals(0, tasksEmpty.size(),"Неверное количество задач в списке Task.");

        URI url6 = URI.create(BASE_URL + "history");
        HttpRequest request9 = HttpRequest.newBuilder().uri(url6).GET().build();
        HttpResponse<String> response9 = client.send(request9, HttpResponse.BodyHandlers.ofString());
        String json9 = response9.body();
        List<Task> history = gson.fromJson(json9, List.class);
        assertEquals(200, response9.statusCode(), "Неверный статус-код при получении истории.");
        assertEquals(0, history.size(),"Неверное количество задач в истории.");

        URI url7 = URI.create(BASE_URL + "priorities");
        HttpRequest request7 = HttpRequest.newBuilder().uri(url7).GET().build();
        HttpResponse<String> response7 = client.send(request7, HttpResponse.BodyHandlers.ofString());
        String jsonP = response7.body();
        List<Task> priorities = gson.fromJson(jsonP, List.class);
        assertEquals(200, response7.statusCode(), "Неверный статус-код при получении приоритетов.");
        assertEquals(0, priorities.size(), "Неверное количество задач в списке приоритетов.");
    }

    @Test
    public void  httpUpdateEpicTest() throws IOException, InterruptedException {
        URI url4 = URI.create(BASE_URL + "epic");
        epic.setId(0);
        String json1 = gson.toJson(epic);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).POST(body1).build();
        HttpResponse<String> response1 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "Неверный статус-код при создании задачи.");

        URI url2 = URI.create(BASE_URL + "subtask");
        subtask.setId(1);
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode(), "Неверный статус-код при создании подзадачи.");

        String json3 = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(json3);
        HttpRequest request3 = HttpRequest.newBuilder().uri(url4).POST(body3).build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response3.statusCode(), "Неверный статус-код при обновлении эпика.");

        URI url3 = URI.create(BASE_URL + "epic?id=" + epic.getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response4 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        String jsonEpic = response4.body();
        Epic epicReceived = gson.fromJson(jsonEpic, Epic.class);
        assertEquals(201, response2.statusCode(), "Неверный статус-код при получении эпика'.");
        assertNotEquals(epic1, epicReceived, "Эпики не совпадают.");
    }

    @Test
    public void httpUpdateSubtaskTest() throws IOException, InterruptedException {
        URI url4 = URI.create(BASE_URL + "epic");
        epic.setId(0);
        String json1 = gson.toJson(epic);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request4 = HttpRequest.newBuilder().uri(url4).POST(body1).build();
        HttpResponse<String> response1 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response1.statusCode(), "Неверный статус-код при создании задачи.");

        URI url2 = URI.create(BASE_URL + "subtask");
        subtask.setId(1);
        String json2 = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).POST(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode(), "Неверный статус-код при создании подзадачи.");

        String json3 = gson.toJson(subtask1);
        final HttpRequest.BodyPublisher body3 = HttpRequest.BodyPublishers.ofString(json3);
        HttpRequest request3 = HttpRequest.newBuilder().uri(url2).POST(body3).build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response3.statusCode(), "Неверный статус-код при обновлении эпика.");

        URI url3 = URI.create(BASE_URL + "subtask?id=" + subtask.getId());
        HttpRequest request1 = HttpRequest.newBuilder().uri(url3).GET().build();
        HttpResponse<String> response4 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        String jsonSubtask = response4.body();
        Subtask subtaskReceived = gson.fromJson(jsonSubtask, Subtask.class);
        assertEquals(201, response2.statusCode(), "Неверный статус-код при получении эпика'.");
        assertNotEquals(subtask1, subtaskReceived, "Эпики не совпадают.");
    }

    @Test
    public void  httpUpdateTaskTest() throws IOException, InterruptedException {
        URI url = URI.create(BASE_URL + "task");
        task1.setId(0);
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный статус-код при обновлении задачи.");

        String json1 = gson.toJson(task2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(body2).build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response2.statusCode(), "Неверный статус-код при обновлении задачи.");

        URI url1 = URI.create(BASE_URL + "task?id=" + task1.getId());
        HttpRequest request4 = HttpRequest.newBuilder().uri(url1).GET().build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        String jsonTask = response4.body();
        Task taskReceived = gson.fromJson(jsonTask, Task.class);
        assertEquals(200, response4.statusCode(), "Неверный статус-код при получении задачи.");
        assertNotEquals(task2, taskReceived, "Задачи не совпадают.");
    }
}
