## Example API Specification

This project provides an easy example of a CRUD REST service. The HTTP header should be like this `Content-Type: application/json; charset=utf-8`.

### JSON Objects defined

##### Book

```Json
{
    "id": 1,
    "title": "Novel Book",
    "category": "novel",
    "publicationDate": "2010-01-01"
}
```

##### Books
```Json
[
    {
        "id": 1,
        "title": "Book 1",
        "category": "science",
        "publicationDate": "2016-01-01"
    },
    {
        "id": 2,
        "title": "Book 2",
        "category": "literature",
        "publicationDate": "2017-02-01"
    }
]
```

### REST API Endpoints List

##### Get All books or some books by specific conditions

`GET /books`

Returns all Books by default, query parameters `title`, `category`,`publicationDate` can be used to filter the results.

Filter by query parameters:

`?title=effectivejava` 

`?category=computer`

`?publicationDate=2000-01-01`

##### Get a specific book by id

`GET /books/:id`

Return the specific Book by id.

##### Add a new book

`POST /books`

Request Body:
```Json
{
        "id": 1,
        "title": "Thinking in java",
        "category": "java",
        "publicationDate": "2006-02-20"
}
```

Return the new added Book

##### Update a book or create one if not exists

`PUT /books/:id`

Request Body:
```Json
{
        "title": "Thinking in java",
        "category": "java",
        "publicationDate": "2006-02-20"
}
```

If the book with the id does not exist, return the created book
If the book with the id exists, return the updated book
This API can not change id of any existed book

##### Delete an existing book

`DELETE /books/:id`
