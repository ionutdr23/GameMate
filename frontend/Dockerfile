# Build stage
FROM node:20-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Production stage
FROM nginx:1.25.4-alpine-slim
RUN rm -rf /usr/share/nginx/html/*
COPY --from=build /app/dist /usr/share/nginx/html
RUN adduser -D -H -u 1001 nginxuser
USER nginxuser
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
