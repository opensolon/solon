package org.noear.solon.sessionstate.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.noear.snack4.ONode;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JwtSecretStoreTest {
    @TempDir
    Path tempDir;

    @Test
    public void createsReusesAndPreservesMapEntries() throws Exception {
        Path settingsFile = tempDir.resolve(".solon/settings.json");

        String firstSecret = JwtSecretStore.loadOrCreate(settingsFile);
        assertFalse(firstSecret.trim().isEmpty());

        Map<?, ?> firstSettings = readSettings(settingsFile);
        assertEquals(firstSecret, firstSettings.get(JwtSecretStore.SETTINGS_KEY));

        Map<String, Object> settingsWithFutureValue = new LinkedHashMap<>();
        settingsWithFutureValue.put("future.setting", "preserved");
        settingsWithFutureValue.put(JwtSecretStore.SETTINGS_KEY, firstSecret);
        Files.write(settingsFile,
                ONode.serialize(settingsWithFutureValue).getBytes(StandardCharsets.UTF_8));

        String secondSecret = JwtSecretStore.loadOrCreate(settingsFile);
        assertEquals(firstSecret, secondSecret);

        Map<?, ?> secondSettings = readSettings(settingsFile);
        assertEquals("preserved", secondSettings.get("future.setting"));
        assertEquals(firstSecret, secondSettings.get(JwtSecretStore.SETTINGS_KEY));
    }

    @Test
    public void rejectsMalformedSettingsInsteadOfRegeneratingSecret() throws Exception {
        Path settingsFile = tempDir.resolve(".solon/settings.json");
        Files.createDirectories(settingsFile.getParent());
        Files.write(settingsFile, "{not-json}".getBytes(StandardCharsets.UTF_8));

        assertThrows(IllegalStateException.class, () -> JwtSecretStore.loadOrCreate(settingsFile));
    }

    @Test
    public void rejectsInvalidPersistedSecret() throws Exception {
        Path settingsFile = tempDir.resolve(".solon/settings.json");
        writeSettings(settingsFile, "not-base64");

        assertThrows(IllegalStateException.class, () -> JwtSecretStore.loadOrCreate(settingsFile));
    }

    @Test
    public void tightensPermissionsOnExistingSettingsFile() throws Exception {
        Path settingsFile = tempDir.resolve(".solon/settings.json");
        writeSettings(settingsFile, JwtUtils.createKey());

        try {
            Files.setPosixFilePermissions(settingsFile,
                    PosixFilePermissions.fromString("rw-r--r--"));
            JwtSecretStore.loadOrCreate(settingsFile);

            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(settingsFile);
            assertEquals(PosixFilePermissions.fromString("rw-------"), permissions);
        } catch (UnsupportedOperationException ignored) {
            // The permission assertion is only applicable to POSIX filesystems.
        }
    }

    @Test
    public void usesSolonSettingsUnderUserHome() throws Exception {
        String previousUserHome = System.getProperty("user.home");
        try {
            System.setProperty("user.home", tempDir.toString());
            String secret = JwtSecretStore.loadOrCreate();
            Path settingsFile = tempDir.resolve(".solon/settings.json");

            assertEquals(secret, readSettings(settingsFile).get(JwtSecretStore.SETTINGS_KEY));
        } finally {
            if (previousUserHome == null) {
                System.clearProperty("user.home");
            } else {
                System.setProperty("user.home", previousUserHome);
            }
        }
    }

    private Map<?, ?> readSettings(Path settingsFile) throws Exception {
        String json = new String(Files.readAllBytes(settingsFile), StandardCharsets.UTF_8);
        return ONode.deserialize(json, Map.class);
    }

    private void writeSettings(Path settingsFile, String secret) throws Exception {
        Files.createDirectories(settingsFile.getParent());
        Map<String, Object> settings = new LinkedHashMap<>();
        settings.put(JwtSecretStore.SETTINGS_KEY, secret);
        Files.write(settingsFile,
                ONode.serialize(settings).getBytes(StandardCharsets.UTF_8));
    }
}
