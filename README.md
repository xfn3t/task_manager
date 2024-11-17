# Task Tracker API

Welcome to the Task Tracker API! This API allows you to manage tasks, statuses, comments, and user authentication. Below you will find detailed information about the available endpoints and how to use them.

## Start woth docker-compose

Clone repository to local

```shell
git clone https://github.com/xfn3t/task_manager
```

Go to project

```shell
cd target_manager
```

Start in docker-compose

```shell
docker-compose up --build
```


## Table of Contents

- [Tasks](#tasks)
- [Statuses](#statuses)
- [Comments](#comments)
- [Authentication](#authentication)

## Tasks

### Get Tasks

**Endpoint:** `GET /api/tasks`

**Description:** Retrieves a list of tasks belonging to the current user or all tasks if the user is an admin. You can filter tasks by the `visible` parameter.

**Parameters:**
- `visible` (optional): Filter tasks by visibility.

**Responses:**
- `200 OK`: Successfully retrieved the list of tasks.
- `404 Not Found`: No tasks found.

### Get Task by ID

**Endpoint:** `GET /api/tasks/{taskId}`

**Description:** Retrieves a task by its ID if it belongs to the current user.

**Parameters:**
- `taskId` (path): ID of the task.

**Responses:**
- `200 OK`: Successfully retrieved the task.
- `404 Not Found`: Task not found.

### Create Task

**Endpoint:** `POST /api/tasks`

**Description:** Creates a new task for the current user.

**Request Body:**
```json
{
  "title": "New Task",
  "description": "Task description",
  "visibility": true,
  "statusId": 1,
  "priorityId": 2,
  "userId": 3,
  "executorsIds": [3, 4],
  "commentsIds": []
}
```

**Responses:**
- `200 OK`: Successfully created the task.
- `400 Bad Request`: Error creating the task.

### Update Task

**Endpoint:** `PUT /api/tasks/{taskId}`

**Description:** Updates an existing task that belongs to the current user.

**Parameters:**
- `taskId` (path): ID of the task.

**Request Body:**
```json
{
  "title": "Updated Task",
  "description": "Updated description",
  "visibility": false,
  "statusId": 2,
  "priorityId": 1,
  "userId": 3,
  "executorsIds": [4, 5],
  "commentsIds": [1, 2]
}
```

**Responses:**
- `200 OK`: Successfully updated the task.
- `400 Bad Request`: Error updating the task.

### Delete Task

**Endpoint:** `DELETE /api/tasks/{taskId}`

**Description:** Deletes a task by its ID if it belongs to the current user.

**Parameters:**
- `taskId` (path): ID of the task.

**Responses:**
- `204 No Content`: Successfully deleted the task.
- `400 Bad Request`: Error deleting the task.

### Update Task Status

**Endpoint:** `PATCH /api/tasks/{taskId}/status`

**Description:** Updates the status of a task if the user has access.

**Parameters:**
- `taskId` (path): ID of the task.

**Request Body:**
```json
{
  "id": 2
}
```

**Responses:**
- `200 OK`: Successfully updated the task status.
- `400 Bad Request`: Error updating the task status.

## Statuses

### Get All Statuses

**Endpoint:** `GET /api/status`

**Description:** Retrieves a list of all statuses.

**Responses:**
- `200 OK`: Successfully retrieved the list of statuses.
- `404 Not Found`: No statuses found.

### Get Status by ID

**Endpoint:** `GET /api/status/{statusId}`

**Description:** Retrieves a status by its ID.

**Parameters:**
- `statusId` (path): ID of the status.

**Responses:**
- `200 OK`: Successfully retrieved the status.
- `404 Not Found`: Status not found.

### Add Status

**Endpoint:** `POST /api/status`

**Description:** Creates a new status. The `isGlobal` parameter determines if the status is local to the user or global.

**Request Body:**
```json
{
  "title": "New Status",
  "isGlobal": false,
  "userId": 1
}
```

**Responses:**
- `200 OK`: Successfully created the status.
- `400 Bad Request`: Invalid input.

### Update Status

**Endpoint:** `PUT /api/status/{statusId}`

**Description:** Updates an existing status by its ID.

**Parameters:**
- `statusId` (path): ID of the status.

**Request Body:**
```json
{
  "title": "Updated Status",
  "isGlobal": true,
  "userId": null
}
```

**Responses:**
- `200 OK`: Successfully updated the status.
- `400 Bad Request`: Invalid input.
- `404 Not Found`: Status not found.

### Delete Status

**Endpoint:** `DELETE /api/status/{statusId}`

**Description:** Deletes a status by its ID.

**Parameters:**
- `statusId` (path): ID of the status.

**Responses:**
- `204 No Content`: Successfully deleted the status.
- `404 Not Found`: Status not found.

## Comments

### Get All Comments

**Endpoint:** `GET /api/comments`

**Description:** Retrieves a list of all comments made by the current user.

**Responses:**
- `200 OK`: Successfully retrieved the comments.

### Get All Task Comments

**Endpoint:** `GET /api/comments/{taskId}`

**Description:** Retrieves a list of all comments for a specific task if the user has access.

**Parameters:**
- `taskId` (path): ID of the task.

**Responses:**
- `200 OK`: Successfully retrieved the task comments.
- `404 Not Found`: Task comments not found.

### Add Comment

**Endpoint:** `POST /api/comments/{taskId}`

**Description:** Adds a new comment to a specific task.

**Parameters:**
- `taskId` (path): ID of the task.

**Request Body:**
```json
{
  "content": "This is a new comment."
}
```

**Responses:**
- `200 OK`: Successfully added the comment.
- `400 Bad Request`: Error adding the comment.

### Reply to Comment

**Endpoint:** `POST /api/comments/{taskId}/{parentCommentId}/reply`

**Description:** Adds a reply to a specific comment for a task.

**Parameters:**
- `taskId` (path): ID of the task.
- `parentCommentId` (path): ID of the parent comment.

**Request Body:**
```json
{
  "content": "This is a reply to your comment."
}
```

**Responses:**
- `200 OK`: Successfully added the reply.
- `400 Bad Request`: Error adding the reply.

## Authentication

### Sign In

**Endpoint:** `POST /auth/login`

**Description:** Authenticates a user.

**Request Body:**
```json
{
  "username": "user",
  "password": "password"
}
```

**Responses:**
- `200 OK`: Successfully authenticated.
- `401 Unauthorized`: Invalid credentials.
- `500 Internal Server Error`: Server error.

### Sign Up

**Endpoint:** `POST /auth/signup`

**Description:** Registers a new user.

**Request Body:**
```json
{
  "username": "newuser",
  "password": "newpassword",
  "email": "newuser@example.com"
}
```

**Responses:**
- `200 OK`: Successfully registered.
- `400 Bad Request`: Invalid request.
- `500 Internal Server Error`: Server error.
=
