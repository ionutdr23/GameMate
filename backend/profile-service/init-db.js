db = connect("localhost:27017/admin");

db.createUser({
    user: "devuser",
    pwd: "devpassword",
    roles: [
        { role: "readWrite", db: "profileservice" },
        { role: "dbAdmin", db: "profileservice" }
    ]
});
