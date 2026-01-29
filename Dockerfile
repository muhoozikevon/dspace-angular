# Production multi-stage Dockerfile for dspace/dspace-angular
# Builds the main app and the ai/agent-ui app, then runs the UI with PM2

FROM docker.io/node:22-alpine AS build

# Ensure Python and other build tools are available
RUN apk --no-cache add python3 make g++

WORKDIR /app

# Copy package files and install dependencies (including workspaces)
COPY package.json package-lock.json ./
RUN npm ci --omit=dev

# Increase available memory for production build
ENV NODE_OPTIONS="--max_old_space_size=4096"

# Copy source and build the main application for production
COPY . /app/
RUN npm run build:prod

# Build the AI agent UI (if present) and copy its dist into main dist
RUN if [ -d "ai/agent-ui" ]; then \
			npm --prefix ai/agent-ui run build || true; \
			if [ -d "ai/agent-ui/dist" ]; then \
				mkdir -p dist/agent-ui && cp -R ai/agent-ui/dist/* dist/agent-ui/ || true; \
			fi; \
		fi

# Final image to run the application using PM2
FROM docker.io/node:22-alpine

# Install PM2 to manage the Node process in production
RUN npm install --global pm2

# Copy built artifacts and config
COPY --chown=node:node --from=build /app/dist /app/dist
COPY --chown=node:node config /app/config
COPY --chown=node:node docker/dspace-ui.json /app/dspace-ui.json

WORKDIR /app
USER node
ENV NODE_ENV=production
EXPOSE 4000

ENTRYPOINT [ "pm2-runtime", "start", "dspace-ui.json" ]
CMD ["--json"]
