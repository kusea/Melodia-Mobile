# Multi-stage Dockerfile for Melodia Backend API
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json tsconfig.json ./
RUN npm ci

COPY src ./src
RUN npm run build

# Production stage
FROM node:20-alpine AS runner

WORKDIR /app

ENV NODE_ENV=production
ENV PORT=3000

COPY package*.json ./
RUN npm ci --only=production

COPY --from=builder /app/dist ./dist
COPY src/database/schema.sql ./dist/database/schema.sql
COPY src/database/seed.sql ./dist/database/seed.sql

EXPOSE 3000

CMD ["node", "dist/server.js"]
