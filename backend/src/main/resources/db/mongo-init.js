// Create the database
db = db.getSiblingDB('ecfr_db');

// Create collections
db.createCollection('regulations');
db.createCollection('agencies');

// Create indexes for regulations collection
db.regulations.createIndex({ "agency": 1 });
db.regulations.createIndex({ "agencyCode": 1 });
db.regulations.createIndex({ "partNumber": 1, "sectionNumber": 1 });
db.regulations.createIndex({ "effectiveDate": 1 });
db.regulations.createIndex({ "lastUpdated": 1 });
db.regulations.createIndex({ "status": 1 });
db.regulations.createIndex({ "keywords": 1 });
db.regulations.createIndex({ "checksum": 1 });

// Create compound indexes for common queries
db.regulations.createIndex({ "agency": 1, "effectiveDate": 1 });
db.regulations.createIndex({ "agency": 1, "status": 1 });
db.regulations.createIndex({ "agencyCode": 1, "partNumber": 1 });

// Create indexes for agencies collection
db.agencies.createIndex({ "code": 1 }, { unique: true });
db.agencies.createIndex({ "name": 1 });
db.agencies.createIndex({ "parentAgency": 1 });
db.agencies.createIndex({ "lastUpdated": 1 });

// Create text indexes for search functionality
db.regulations.createIndex(
    { 
        "title": "text",
        "content": "text",
        "keywords": "text"
    },
    {
        weights: {
            title: 10,
            keywords: 5,
            content: 1
        },
        name: "regulations_text_search"
    }
);

db.agencies.createIndex(
    { 
        "name": "text",
        "description": "text"
    },
    {
        weights: {
            name: 10,
            description: 5
        },
        name: "agencies_text_search"
    }
);

// Create a user for the application (if needed in production)
// db.createUser({
//     user: "ecfr_user",
//     pwd: "ecfr_password",
//     roles: [
//         { role: "readWrite", db: "ecfr_db" }
//     ]
// });

print('MongoDB initialization completed successfully'); 