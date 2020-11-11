package com.ms.todo_app.model;

public class TodoItems {
    public String todo;
    public Boolean isCompleted;

    public TodoItems(){}
    public TodoItems(String todo, Boolean isCompleted) {
        this.todo = todo;
        this.isCompleted = isCompleted;
    }

    public String getTodoitems() {
        return todo;
    }

    public void setTodoitems(String todo) {
        this.todo = todo;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        this.isCompleted = completed;
    }


}
