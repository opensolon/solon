/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.sessionstate.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.noear.snack4.ONode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Stores generated default secrets in the Solon settings file under the home
 * directory of the operating-system user running the Solon process.
 *
 * <p>The file is intentionally a top-level map so other Solon defaults can be
 * added later without changing this storage format.</p>
 */
final class JwtSecretStore {
    static final String SETTINGS_KEY = "server.session.state.jwt.secret";

    private static final String SETTINGS_DIR = ".solon";
    private static final String SETTINGS_FILE = "settings.json";
    private static final ReentrantLock LOCAL_LOCK = new ReentrantLock();

    private JwtSecretStore() {
    }

    static String loadOrCreate() {
        String userHome = System.getProperty("user.home");
        if (userHome == null || userHome.trim().length() == 0) {
            throw new IllegalStateException("Unable to resolve the user home directory for the JWT secret");
        }

        return loadOrCreate(Paths.get(userHome, SETTINGS_DIR, SETTINGS_FILE));
    }

    static String loadOrCreate(Path settingsFile) {
        Path target = settingsFile.toAbsolutePath();
        Path parent = target.getParent();
        if (parent == null) {
            throw new IllegalStateException("Unable to resolve the settings directory for the JWT secret");
        }

        LOCAL_LOCK.lock();
        try {
            Files.createDirectories(parent);
            setPrivatePermissions(parent, true);

            Path lockFile = target.resolveSibling(target.getFileName().toString() + ".lock");
            try (FileChannel channel = FileChannel.open(lockFile,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                 FileLock ignored = channel.lock()) {
                Map<String, Object> settings = readSettings(target);
                String configuredSecret = readSecret(settings);
                if (configuredSecret != null) {
                    return configuredSecret;
                }

                String generatedSecret = JwtUtils.createKey();
                settings.put(SETTINGS_KEY, generatedSecret);
                writeSettings(target, parent, settings);
                return generatedSecret;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load or persist the JWT secret at " + target, e);
        } finally {
            LOCAL_LOCK.unlock();
        }
    }

    private static Map<String, Object> readSettings(Path target) throws IOException {
        if (!Files.exists(target)) {
            return new LinkedHashMap<>();
        }
        if (!Files.isRegularFile(target)) {
            throw new IllegalStateException("JWT settings path is not a regular file: " + target);
        }
        // Protect an existing settings file before reading its persisted secret.
        setPrivatePermissions(target, false);

        String json = new String(Files.readAllBytes(target), StandardCharsets.UTF_8).trim();
        if (json.length() == 0) {
            return new LinkedHashMap<>();
        }

        final Object decoded;
        try {
            decoded = ONode.deserialize(json, Map.class);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Unable to parse Solon settings JSON: " + target, e);
        }

        if (!(decoded instanceof Map)) {
            throw new IllegalStateException("Solon settings JSON must contain a top-level object: " + target);
        }

        Map<String, Object> settings = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) decoded).entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                throw new IllegalStateException("Solon settings JSON contains a non-string key: " + target);
            }
            settings.put((String) entry.getKey(), entry.getValue());
        }
        return settings;
    }

    private static String readSecret(Map<String, Object> settings) {
        Object value = settings.get(SETTINGS_KEY);
        if (value == null) {
            return null;
        }
        if (!(value instanceof String)) {
            throw new IllegalStateException("Solon setting '" + SETTINGS_KEY + "' must be a string");
        }

        String secret = (String) value;
        if (secret.trim().length() == 0) {
            throw new IllegalStateException("Solon setting '" + SETTINGS_KEY + "' must not be empty");
        }

        try {
            Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        } catch (RuntimeException e) {
            throw new IllegalStateException("Solon setting '" + SETTINGS_KEY + "' must be a valid Base64 HMAC key");
        }
        return secret;
    }

    private static void writeSettings(Path target, Path parent, Map<String, Object> settings) throws IOException {
        byte[] content = ONode.serialize(settings).getBytes(StandardCharsets.UTF_8);
        Path temporary = Files.createTempFile(parent, ".settings-", ".tmp");
        try {
            Files.write(temporary, content, StandardOpenOption.WRITE);
            setPrivatePermissions(temporary, false);
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
            setPrivatePermissions(target, false);
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static void setPrivatePermissions(Path path, boolean directory) throws IOException {
        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString(directory ? "rwx------" : "rw-------");
            Files.setPosixFilePermissions(path, permissions);
        } catch (UnsupportedOperationException ignored) {
            // Non-POSIX filesystems use their platform's normal permission model.
        }
    }
}
