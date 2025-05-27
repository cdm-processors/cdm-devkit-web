# Базовый образ
FROM node:20-alpine

WORKDIR /app
COPY package*.json ./
RUN npm install --legacy-peer-deps
RUN npm install axios --legacy-peer-deps
COPY . .
EXPOSE 3000

CMD ["npm", "start"]
