package com.softserve.itacademy.component.task;

import com.softserve.itacademy.controller.TaskController;
import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.model.TaskPriority;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.dto.TaskTransformer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {TaskController.class})
class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private ToDoService toDoService;

    @MockBean
    private StateService stateService;

    @MockBean
    private TaskTransformer taskTransformer;

    @Test
    void createFormShouldReturnCreateTaskView() throws Exception {
        ToDo todo = new ToDo();
        todo.setId(1L);

        when(toDoService.readById(1L)).thenReturn(todo);

        mvc.perform(get("/tasks/create/todos/{todo_id}", 1L)
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("create-task"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attributeExists("todo"))
                .andExpect(model().attributeExists("priorities"));
    }

    @Test
    void createTaskShouldRedirectToTodoTasksOnSuccess() throws Exception {
        mvc.perform(post("/tasks/create/todos/{todo_id}", 1L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Test task")
                        .param("priority", TaskPriority.LOW.name())
                        .param("todoId", "1")
                        .param("stateId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/1/tasks"));

        verify(taskService).create(any(TaskDto.class));
    }

    @Test
    void deleteTaskShouldRedirectToTodoTasks() throws Exception {
        mvc.perform(get("/tasks/{task_id}/delete/todos/{todo_id}", 5L, 1L)
                        .contentType(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/todos/1/tasks"));

        verify(taskService).delete(eq(5L));
    }
}