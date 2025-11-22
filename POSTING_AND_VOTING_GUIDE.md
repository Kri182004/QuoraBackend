# Quora Backend - Posting & Voting Complete Guide

## 🔗 Base URL
```
http://localhost:8082
```

## 🔐 Authentication Required
All posting and voting operations require JWT authentication:
```
Authorization: Bearer <your_jwt_token>
```

---

## 📝 CREATING CONTENT

### 1. 🔍 Create Question

**Endpoint:** `POST /api/questions`

**Required Headers:**
```
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Required Fields:**
- `title` (string) - **REQUIRED**
  - Minimum: 10 characters
  - Maximum: 100 characters
  - Cannot be blank
- `questionBody` (string) - **REQUIRED**
  - Cannot be blank
  - Contains the main question content

**Optional Fields:**
- `topicNames` (string array) - Topics to associate with the question

**Request Body:**
```json
{
  "title": "How to learn React effectively?",
  "questionBody": "I am new to React and want to learn it effectively. What are the best resources and learning path?",
  "topicNames": ["React", "JavaScript", "Frontend"]
}
```

**Example Request:**
```bash
curl -X POST 'http://localhost:8082/api/questions' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...' \
  -d '{
    "title": "How to learn React effectively?",
    "questionBody": "I am new to React and want to learn it effectively. What are the best resources?",
    "topicNames": ["React", "JavaScript"]
  }'
```

**Success Response (201):**
```json
{
  "id": "6921ba7eedffe3677c306648",
  "title": "How to learn React effectively?",
  "questionBody": "I am new to React and want to learn it effectively...",
  "userId": "6921b9a4516e5b80073bfd53",
  "username": "gaurav",
  "createdAt": "2025-11-22T18:58:30.296829",
  "updatedAt": "2025-11-22T18:58:30.296847",
  "topics": [
    {"id": "6921ba7eedffe3677c306646", "name": "React"},
    {"id": "6921ba7eedffe3677c306647", "name": "JavaScript"}
  ]
}
```

**Validation Errors (400):**
- Title too short (< 10 characters)
- Title too long (> 100 characters)
- Title or questionBody is blank/empty
- Invalid JSON format

---

### 2. 💬 Create Answer

**Endpoint:** `POST /api/answers?questionId={questionId}`

**Required Headers:**
```
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Required Query Parameter:**
- `questionId` (string) - ID of the question to answer

**Required Fields:**
- `content` (string) - **REQUIRED**
  - Minimum: 10 characters
  - Maximum: 1000 characters
  - Cannot be blank

**Request Body:**
```json
{
  "content": "I recommend starting with the official React documentation, then building small projects. Also try tutorials on freeCodeCamp and practice with Create React App."
}
```

**Example Request:**
```bash
curl -X POST 'http://localhost:8082/api/answers?questionId=6921ba7eedffe3677c306648' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...' \
  -d '{
    "content": "I recommend starting with the official React documentation and building small projects."
  }'
```

**Success Response (201):**
```json
{
  "id": "6921bb2eedffe3677c306649",
  "content": "I recommend starting with the official React documentation...",
  "username": "john_doe",
  "questionId": "6921ba7eedffe3677c306648",
  "createdAt": "2025-11-22T19:02:15.123456",
  "upvotes": 0,
  "downvotes": 0
}
```

**Validation Errors (400):**
- Content too short (< 10 characters)
- Content too long (> 1000 characters)
- Content is blank/empty
- Question ID not found

---

### 3. 💭 Create Comment

**Endpoint:** `POST /api/comments`

**Required Headers:**
```
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Required Fields:**
- `content` (string) - **REQUIRED**
  - Cannot be blank

**Optional Fields (choose one):**
- `questionId` (string) - Comment on a question
- `answerId` (string) - Comment on an answer
- `parentCommentId` (string) - Reply to another comment

**Request Body Examples:**

**Comment on Question:**
```json
{
  "content": "Great question! I'm also interested in learning React.",
  "questionId": "6921ba7eedffe3677c306648"
}
```

**Comment on Answer:**
```json
{
  "content": "This is really helpful, thanks for sharing!",
  "answerId": "6921bb2eedffe3677c306649"
}
```

**Reply to Comment:**
```json
{
  "content": "I agree with your point about documentation.",
  "parentCommentId": "6921bc1eedffe3677c30664a"
}
```

**Example Request:**
```bash
curl -X POST 'http://localhost:8082/api/comments' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...' \
  -d '{
    "content": "Great question! I have the same doubt.",
    "questionId": "6921ba7eedffe3677c306648"
  }'
```

**Success Response (201):**
```json
{
  "id": "6921bc1eedffe3677c30664a",
  "content": "Great question! I have the same doubt.",
  "username": "alice",
  "createdAt": "2025-11-22T19:05:30.789012",
  "questionId": "6921ba7eedffe3677c306648",
  "answerId": null,
  "parentCommentId": null
}
```

---

## 🗳️ VOTING SYSTEM

### 4. ⬆️ Vote on Question

**Endpoint:** `POST /api/questions/{questionId}/vote`

**Required Headers:**
```
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Required Fields:**
- `voteType` (string) - **REQUIRED**
  - Valid values: `"UPVOTE"` or `"DOWNVOTE"`

**Request Body:**
```json
{
  "voteType": "UPVOTE"
}
```

**Example Request:**
```bash
curl -X POST 'http://localhost:8082/api/questions/6921ba7eedffe3677c306648/vote' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...' \
  -d '{
    "voteType": "UPVOTE"
  }'
```

**Success Response (200):**
```
HTTP 200 OK
(No body content)
```

**Vote Types:**
- `UPVOTE` - Like/support the question
- `DOWNVOTE` - Dislike/oppose the question

---

### 5. ⬆️ Vote on Answer

**Endpoint:** `POST /api/answers/{answerId}/vote`

**Required Headers:**
```
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Required Fields:**
- `voteType` (string) - **REQUIRED**
  - Valid values: `"UPVOTE"` or `"DOWNVOTE"`

**Request Body:**
```json
{
  "voteType": "UPVOTE"
}
```

**Example Request:**
```bash
curl -X POST 'http://localhost:8082/api/answers/6921bb2eedffe3677c306649/vote' \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...' \
  -d '{
    "voteType": "DOWNVOTE"
  }'
```

**Success Response (200):**
```
HTTP 200 OK
(No body content)
```

---

## 📚 READING CONTENT

### 6. 📖 Get All Questions

**Endpoint:** `GET /api/questions`

**No authentication required**

**Example Request:**
```bash
curl 'http://localhost:8082/api/questions'
```

**Response:**
```json
[
  {
    "id": "6921ba7eedffe3677c306648",
    "title": "How to learn React effectively?",
    "questionBody": "I am new to React and want to learn it effectively...",
    "username": "gaurav",
    "createdAt": "2025-11-22T18:58:30.296",
    "upvotes": 5,
    "downvotes": 1,
    "topics": [
      {"id": "6921ba7eedffe3677c306646", "name": "React"},
      {"id": "6921ba7eedffe3677c306647", "name": "JavaScript"}
    ]
  }
]
```

### 7. 📖 Get Specific Question with Answers

**Endpoint:** `GET /api/questions/{questionId}`

**No authentication required**

**Example Request:**
```bash
curl 'http://localhost:8082/api/questions/6921ba7eedffe3677c306648'
```

**Response:**
```json
{
  "question": {
    "id": "6921ba7eedffe3677c306648",
    "title": "How to learn React effectively?",
    "questionBody": "I am new to React and want to learn it effectively...",
    "username": "gaurav",
    "createdAt": "2025-11-22T18:58:30.296",
    "upvotes": 5,
    "downvotes": 1,
    "topics": ["React", "JavaScript"]
  },
  "answers": [
    {
      "id": "6921bb2eedffe3677c306649",
      "content": "I recommend starting with the official React documentation...",
      "username": "john_doe",
      "createdAt": "2025-11-22T19:02:15.123",
      "upvotes": 3,
      "downvotes": 0
    }
  ]
}
```

### 8. 📖 Get Answers for Question

**Endpoint:** `GET /api/answers?questionId={questionId}`

**No authentication required**

**Example Request:**
```bash
curl 'http://localhost:8082/api/answers?questionId=6921ba7eedffe3677c306648'
```

### 9. 📖 Get Comments

**Endpoint:** `GET /api/comments?questionId={questionId}&answerId={answerId}`

**No authentication required**

**Query Parameters:**
- `questionId` (optional) - Get comments for this question
- `answerId` (optional) - Get comments for this answer

**Example Request:**
```bash
# Comments on a question
curl 'http://localhost:8082/api/comments?questionId=6921ba7eedffe3677c306648'

# Comments on an answer
curl 'http://localhost:8082/api/comments?answerId=6921bb2eedffe3677c306649'
```

---

## ✏️ UPDATING CONTENT

### 10. ✏️ Update Question

**Endpoint:** `PUT /api/questions/{questionId}`

**Required Headers:**
```
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Note:** Only the question author can update their question.

**Request Body:**
```json
{
  "title": "How to learn React effectively in 2024?",
  "questionBody": "Updated question content with more specific details...",
  "topicNames": ["React", "JavaScript", "2024"]
}
```

### 11. ✏️ Update Answer

**Endpoint:** `PUT /api/answers/{answerId}`

**Required Headers:**
```
Content-Type: application/json
Authorization: Bearer <jwt_token>
```

**Note:** Only the answer author can update their answer.

**Request Body:**
```json
{
  "content": "Updated answer content with more details and examples..."
}
```

---

## 🗑️ DELETING CONTENT

### 12. 🗑️ Delete Question

**Endpoint:** `DELETE /api/questions/{questionId}`

**Required Headers:**
```
Authorization: Bearer <jwt_token>
```

**Note:** Only the question author can delete their question.

### 13. 🗑️ Delete Answer

**Endpoint:** `DELETE /api/answers/{answerId}`

**Required Headers:**
```
Authorization: Bearer <jwt_token>
```

**Note:** Only the answer author can delete their answer.

### 14. 🗑️ Delete Comment

**Endpoint:** `DELETE /api/comments/{commentId}`

**Required Headers:**
```
Authorization: Bearer <jwt_token>
```

**Note:** Only the comment author can delete their comment.

---

## ❌ ERROR HANDLING

### Common HTTP Status Codes

**400 Bad Request:**
- Missing required fields
- Invalid field values (too short/long)
- Malformed JSON

**401 Unauthorized:**
- Missing or invalid JWT token
- Token expired

**403 Forbidden:**
- Trying to modify content you don't own
- Insufficient permissions

**404 Not Found:**
- Question/Answer/Comment ID doesn't exist
- Invalid endpoint

**500 Internal Server Error:**
- Database connection issues
- Server-side errors

### Example Error Response:
```json
{
  "timestamp": "2025-11-22T13:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Title must be between 10 and 100 characters",
  "path": "/api/questions"
}
```

---

## 🎯 VALIDATION RULES SUMMARY

| Field | Min Length | Max Length | Required | Notes |
|-------|------------|------------|----------|-------|
| Question `title` | 10 chars | 100 chars | ✅ Yes | Cannot be blank |
| Question `questionBody` | - | - | ✅ Yes | Cannot be blank |
| Answer `content` | 10 chars | 1000 chars | ✅ Yes | Cannot be blank |
| Comment `content` | - | - | ✅ Yes | Cannot be blank |
| `voteType` | - | - | ✅ Yes | Must be "UPVOTE" or "DOWNVOTE" |
| `topicNames` | - | - | ❌ No | Array of strings |

---

## 🚀 Frontend Integration Examples

### React/JavaScript Example:

```javascript
// Create Question
const createQuestion = async (questionData) => {
  const response = await fetch('http://localhost:8082/api/questions', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    },
    body: JSON.stringify({
      title: questionData.title,
      questionBody: questionData.content,
      topicNames: questionData.topics
    })
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }
  
  return await response.json();
};

// Upvote Question
const upvoteQuestion = async (questionId) => {
  const response = await fetch(`http://localhost:8082/api/questions/${questionId}/vote`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    },
    body: JSON.stringify({ voteType: 'UPVOTE' })
  });
  
  if (!response.ok) {
    throw new Error('Failed to vote');
  }
};

// Add Answer
const addAnswer = async (questionId, content) => {
  const response = await fetch(`http://localhost:8082/api/answers?questionId=${questionId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    },
    body: JSON.stringify({ content })
  });
  
  return await response.json();
};
```

---

## 📋 Quick Reference Checklist

### ✅ Before Creating Questions:
- [ ] JWT token is valid and not expired
- [ ] Title is between 10-100 characters
- [ ] Question body is not empty
- [ ] Topics array is properly formatted

### ✅ Before Adding Answers:
- [ ] JWT token is valid
- [ ] Content is between 10-1000 characters
- [ ] Question ID exists and is valid

### ✅ Before Voting:
- [ ] JWT token is valid
- [ ] Vote type is exactly "UPVOTE" or "DOWNVOTE"
- [ ] Target (question/answer) ID is valid

### ✅ Before Adding Comments:
- [ ] JWT token is valid
- [ ] Content is not empty
- [ ] At least one target ID (questionId, answerId, or parentCommentId) is provided

---

This guide covers all the essential posting and voting functionality. The API is designed to be RESTful and follows standard HTTP conventions for easy integration with any frontend framework.