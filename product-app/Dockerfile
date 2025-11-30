FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

ARG NEXT_PUBLIC_API_BASE
ENV NEXT_PUBLIC_API_BASE=${NEXT_PUBLIC_API_BASE}

ARG NEXT_PUBLIC_RESOURCE_PATH
ENV NEXT_PUBLIC_RESOURCE_PATH=${NEXT_PUBLIC_RESOURCE_PATH}

RUN echo ">>> API BASE: $NEXT_PUBLIC_API_BASE"
RUN echo ">>> PATH: $NEXT_PUBLIC_RESOURCE_PATH"

RUN npm run build


FROM node:20-alpine AS runner

WORKDIR /app

COPY --from=builder /app/package*.json ./
COPY --from=builder /app/.next ./.next
COPY --from=builder /app/node_modules ./node_modules
COPY --from=builder /app/public ./public

ENV NODE_ENV=production

EXPOSE 3000
CMD ["npm", "start"]