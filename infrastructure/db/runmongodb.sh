export MONGODB_VERSION=6.0
docker run --name applicants-mongodb  -d mongodb/mongodb-community-server:$MONGODB_VERSION
