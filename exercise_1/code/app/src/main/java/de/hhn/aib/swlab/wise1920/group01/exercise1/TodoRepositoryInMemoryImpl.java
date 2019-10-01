package de.hhn.aib.swlab.wise1920.group01.exercise1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class TodoRepositoryInMemoryImpl implements TodoRepository {

    private List<Todo> list;
    private MutableLiveData<List<Todo>> liveData;

    public TodoRepositoryInMemoryImpl() {
        list = new ArrayList<>();
        liveData = new MutableLiveData<>();
        liveData.setValue(list);
    }

    @Override
    public void addTodo(Todo todo) {
        list.add(todo);
        liveData.postValue(list);
    }

    @Override
    public LiveData<List<Todo>> getTodos() {
        return liveData;
    }
}
