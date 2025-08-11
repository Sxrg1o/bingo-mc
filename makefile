# Makefile for building and deploying a Minecraft plugin to an EC2 server.

# --- Configuration ---
# Load environment variables from .env file (must contain IPV4=...)
include .env
export

# SSH and Server Configuration
KEY_FILE := msv-key-pair.pem
SSH_USER := ubuntu
REMOTE_IP := $(IPV4)
REMOTE_DEST_DIR := ~/main-server/plugins/
REMOTE_SERVER_DIR := ~/main-server

# Gradle Project Configuration
# Finds the primary JAR file, ignoring sources and plain JARs.
JAR_FILE := $(shell find build/libs -name "*.jar" ! -name "*-plain.jar" ! -name "*-sources.jar" | head -n 1)

# --- Targets ---

# Define targets that are not files to ensure they always run.
.PHONY: all build upload deploy reload clean

# Default target that runs when you just type 'make'
all: deploy

# Target: deploy
# Builds, uploads, and restarts the plugin on the server.
deploy: build upload restart
	@echo "✅ Deployment complete!"

# Target: build
# Compiles the Java project using the Gradle wrapper.
build:
	@echo "▶️  Building project with Gradle..."
	@./gradlew build
	@echo "✅ Project built successfully. JAR is at $(JAR_FILE)"

# Target: upload
# Uploads the compiled JAR file to the server using scp.
# This target depends on 'build' completing successfully.
upload: build
	@echo "▶️  Uploading $(JAR_FILE) to $(REMOTE_IP)..."
	@scp -i $(KEY_FILE) $(JAR_FILE) $(SSH_USER)@$(REMOTE_IP):$(REMOTE_DEST_DIR)
	@echo "✅ Upload complete."

# Target: restart
# Connects to the server via SSH and handles server state.
# - If the 'minecraft' screen exists, it sends the 'restart' command.
# - If the screen does NOT exist, it starts the server in a new screen.
restart:
	@echo "▶️  Connecting to server to reload plugin..."
	@ssh -i $(KEY_FILE) $(SSH_USER)@$(REMOTE_IP) ' \
			if screen -ls | grep -q "\.minecraft"; then \
				echo "Server is running. Sending restart command..."; \
				screen -S minecraft -p 0 -X stuff "restart\n"; \
			else \
				echo "Server not found. Starting server..."; \
				cd $(REMOTE_SERVER_DIR) && screen -dmS minecraft java -Xms2G -Xmx2G -jar server.jar nogui; \
				echo "✅ Server started in screen session '\''minecraft'\''."; \
			fi; \
		'

# Target: clean
# Removes the build directory created by Gradle.
clean:
	@echo "▶️  Cleaning project..."
	@./gradlew clean
	@echo "✅ Build directory cleaned."

