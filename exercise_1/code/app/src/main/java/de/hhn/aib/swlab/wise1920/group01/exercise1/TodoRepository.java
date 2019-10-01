package de.hhn.aib.swlab.wise1920.group01.exercise1;

import androidx.lifecycle.LiveData;

import java.util.List;

interface TodoRepository {
    void addTodo(Todo todo);

    LiveData<List<Todo>> getTodos();
}
