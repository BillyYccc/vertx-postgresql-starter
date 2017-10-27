## Example API Specification

This project provides an easy example of a CRUD REST service. The HTTP header should be like this `Content-Type: application/json; charset=utf-8`.

### JSON Objects defined

##### Book

```Json
{
    "bookid": 1,
    "title": "Novel Book",
    "category": "novel",
    "publicationdate": "2010-01-01",
}
```

##### Books
```Json
[
    {
        "bookid": 1,
        "title": "Book 1",
        "category": "science",
        "publicationdate": "2016-01-01"
    },
    {
        "bookid": 2,
        "title": "Book 2",
        "category": "literature",
        "publicationdate": "2017-02-01"
    }
]
```

### REST API Endpoints List

##### Get All books or some books by specific conditions

`GET /books`

Returns all Books by default, query parameters `title`, `category`,`publicationdate` can be used to filter the results.

Filter by query parameters:

`?title=effectivejava` 

`?category=computer`

`?publicationdate=2000-01-01`

##### Get a specific book by id

`GET /books/:id`

Return the specific Book by id.

##### Add a new book

`POST /books/:id`

Return the new added Book

##### Update an book or create one if not exists

`PUT /books/:id`

Return the update(or create) Book

##### Delete an existing book

`DELETE /books/:id`
