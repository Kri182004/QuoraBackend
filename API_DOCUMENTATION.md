# Quora Backend API Documentation

## Base URL
```
http://localhost:8082
```

## Authentication
Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## Response Format
All responses follow a consistent JSON format. Error responses include appropriate HTTP status codes and error messages.

---

## 🔐 Authentication Endpoints

### Register User
**POST** `/api/auth/register`

Creates a new user account and returns authentication details.

**Request Body:**
```json
{
  "username": "string",
  "email": "string", 
  "password": "string"
}
```

**Response (201):**
```json
{
  "userId": "6921b5434d7c84b0d59386f8",
  "username": "newuser",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "message": "User registered successfully!"
}
```

**Example:**
```bash
curl -X POST http://localhost:8082/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Login User
**POST** `/api/auth/login`

Authenticates existing user and returns JWT token.

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response (200):**
```json
{
  "userId": "6921b5434d7c84b0d59386f8",
  "username": "johndoe",
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "message": "Login successful!"
}
```

**Example:**
```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

---

## 👤 User Management Endpoints

### Get User Profile
**GET** `/api/users/{userId}/profile`

Retrieves detailed user profile including questions and answers.

**Headers:** `Authorization: Bearer <token>`

**Response (200):**
```json
{
  "id": "string",
  "username": "string",
  "questionsAsked": [
    {
      "id": "string",
      "title": "string",
      "content": "string",
      "createdAt": "2023-01-01T00:00:00Z",
      "upvotes": 0,
      "downvotes": 0
    }
  ],
  "answersGiven": [
    {
      "id": "string",
      "content": "string",
      "questionId": "string",
      "createdAt": "2023-01-01T00:00:00Z",
      "upvotes": 0,
      "downvotes": 0
    }
  ]
}
```

### Get User by ID
**GET** `/api/users/{id}`

Returns basic user information.

**Headers:** `Authorization: Bearer <token>`

**Response (200):**
```json
{
  "id": "string",
  "username": "string",
  "email": "string"
}
```

---

## ❓ Question Endpoints

### Create Question
**POST** `/api/questions`

Creates a new question.

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "title": "string",
  "content": "string",
  "topicNames": ["string"]
}
```

**Response (201):**
```json
{
  "id": "string",
  "title": "string", 
  "content": "string",
  "username": "string",
  "createdAt": "2023-01-01T00:00:00Z",
  "upvotes": 0,
  "downvotes": 0,
  "topics": ["string"]
}
```

### Get All Questions
**GET** `/api/questions`

Retrieves all questions.

**Response (200):**
```json
[
  {
    "id": "string",
    "title": "string",
    "content": "string", 
    "username": "string",
    "createdAt": "2023-01-01T00:00:00Z",
    "upvotes": 0,
    "downvotes": 0,
    "topics": ["string"]
  }
]
```

### Get Question by ID
**GET** `/api/questions/{id}`

Retrieves a specific question with answers.

**Response (200):**
```json
{
  "question": {
    "id": "string",
    "title": "string",
    "content": "string",
    "username": "string",
    "createdAt": "2023-01-01T00:00:00Z",
    "upvotes": 0,
    "downvotes": 0,
    "topics": ["string"]
  },
  "answers": [
    {
      "id": "string",
      "content": "string",
      "username": "string", 
      "createdAt": "2023-01-01T00:00:00Z",
      "upvotes": 0,
      "downvotes": 0
    }
  ]
}
```

### Update Question
**PUT** `/api/questions/{questionId}`

Updates an existing question (owner only).

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "title": "string",
  "content": "string",
  "topicNames": ["string"]
}
```

### Delete Question
**DELETE** `/api/questions/{questionId}`

Deletes a question (owner only).

**Headers:** `Authorization: Bearer <token>`

### Vote on Question
**POST** `/api/questions/{questionId}/vote`

Vote on a question (upvote/downvote).

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "voteType": "UPVOTE" // or "DOWNVOTE"
}
```

### Search Questions
**GET** `/api/questions/search?query={searchTerm}`

Search questions by title/content.

**Parameters:**
- `query` (required): Search term

### Get Questions by User
**GET** `/api/questions/user/{userId}`

Get all questions asked by a specific user.

---

## 💬 Answer Endpoints

### Create Answer
**POST** `/api/answers?questionId={questionId}`

Add an answer to a question.

**Headers:** `Authorization: Bearer <token>`

**Parameters:**
- `questionId` (required): ID of the question to answer

**Request Body:**
```json
{
  "content": "string"
}
```

**Response (201):**
```json
{
  "id": "string",
  "content": "string",
  "username": "string",
  "questionId": "string",
  "createdAt": "2023-01-01T00:00:00Z",
  "upvotes": 0,
  "downvotes": 0
}
```

### Update Answer
**PUT** `/api/answers/{answerId}`

Update an existing answer (owner only).

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "content": "string"
}
```

### Delete Answer
**DELETE** `/api/answers/{answerId}`

Delete an answer (owner only).

**Headers:** `Authorization: Bearer <token>`

### Vote on Answer
**POST** `/api/answers/{answerId}/vote`

Vote on an answer (upvote/downvote).

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "voteType": "UPVOTE" // or "DOWNVOTE"
}
```

---

## 💭 Comment Endpoints

### Create Comment
**POST** `/api/comments`

Add a comment to a question or answer.

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "content": "string",
  "questionId": "string", // optional
  "answerId": "string"    // optional
}
```

**Response (201):**
```json
{
  "id": "string",
  "content": "string",
  "username": "string",
  "createdAt": "2023-01-01T00:00:00Z",
  "questionId": "string",
  "answerId": "string"
}
```

### Get Comments
**GET** `/api/comments?questionId={questionId}&answerId={answerId}`

Get comments for a question or answer.

**Parameters:**
- `questionId` (optional): Get comments for this question
- `answerId` (optional): Get comments for this answer

### Delete Comment
**DELETE** `/api/comments/{commentId}`

Delete a comment (owner only).

**Headers:** `Authorization: Bearer <token>`

---

## 🏷️ Topic Endpoints

### Get All Topics
**GET** `/api/topics`

Retrieve all available topics.

**Response (200):**
```json
[
  {
    "id": "string",
    "name": "string",
    "description": "string"
  }
]
```

### Create Topic
**POST** `/api/topics`

Create a new topic.

**Headers:** `Authorization: Bearer <token>`

**Request Body:**
```json
{
  "name": "string",
  "description": "string"
}
```

### Follow Topic
**POST** `/api/topics/{topicId}/follow`

Follow a topic to see related content in feed.

**Headers:** `Authorization: Bearer <token>`

### Unfollow Topic
**DELETE** `/api/topics/{topicId}/follow`

Unfollow a topic.

**Headers:** `Authorization: Bearer <token>`

### Get Questions by Topic
**GET** `/api/topics/{topicName}/questions`

Get all questions related to a specific topic.

---

## 📰 Feed & Search Endpoints

### Get User Feed
**GET** `/api/feed?page={page}&size={size}`

Get personalized feed based on followed topics.

**Headers:** `Authorization: Bearer <token>`

**Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

**Response (200):**
```json
{
  "content": [
    {
      "id": "string",
      "title": "string",
      "content": "string",
      "username": "string",
      "createdAt": "2023-01-01T00:00:00Z",
      "upvotes": 0,
      "downvotes": 0,
      "topics": ["string"]
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 100,
  "totalPages": 10,
  "last": false,
  "first": true
}
```

### Search Questions
**GET** `/api/search?query={searchTerm}`

Search questions using Elasticsearch.

**Parameters:**
- `query` (required): Search term

---

## ❌ Error Handling

### Common Error Responses

**400 Bad Request:**
```json
{
  "message": "Validation error message"
}
```

**401 Unauthorized:**
```json
{
  "message": "Invalid credentials"
}
```

**403 Forbidden:**
```json
{
  "message": "Access denied"
}
```

**404 Not Found:**
```json
{
  "message": "Resource not found"
}
```

**500 Internal Server Error:**
```json
{
  "message": "Internal server error"
}
```

---

## 🔧 CORS Configuration

CORS is enabled for all origins. You can make requests from any domain without CORS issues.

**Allowed:**
- Origins: `*`
- Methods: `GET, POST, PUT, DELETE, OPTIONS`
- Headers: `*`
- Credentials: `true`

---

## 📝 Example Integration

### Frontend Authentication Flow

1. **Register/Login User:**
```javascript
const response = await fetch('http://localhost:8082/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username: 'john', password: 'pass123' })
});

const { userId, username, token } = await response.json();

// Store for future requests
localStorage.setItem('token', token);
localStorage.setItem('userId', userId);
```

2. **Make Authenticated Requests:**
```javascript
const token = localStorage.getItem('token');

const response = await fetch('http://localhost:8082/api/feed', {
  headers: { 'Authorization': `Bearer ${token}` }
});

const feedData = await response.json();
```

3. **Create Question:**
```javascript
const response = await fetch('http://localhost:8082/api/questions', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({
    title: 'How to use React?',
    content: 'I am new to React and want to learn...',
    topicNames: ['React', 'JavaScript']
  })
});
```

---

## 📚 Notes

- All timestamps are in ISO 8601 format
- IDs are MongoDB ObjectId strings
- JWT tokens expire after 24 hours
- Pagination uses 0-based indexing
- Vote types: `UPVOTE` or `DOWNVOTE`
- Authentication required for most endpoints except public views
- Users can only edit/delete their own content
- Search is powered by Elasticsearch for better performance

This API follows RESTful conventions and includes comprehensive error handling for robust frontend integration.