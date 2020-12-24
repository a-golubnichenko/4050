package com.bigbrassband.jira.git;

import com.bigbrassband.jira.git.ao.model.SshKeyEntry;
import com.bigbrassband.jira.git.services.ssh.KeyManager;
import com.bigbrassband.jira.git.services.ssh.bean.SshKey;
import com.bigbrassband.jira.git.services.ssh.factories.IdentityNameProvider;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author isvirkina
 */
public class MockKeyManager implements KeyManager {

    private Map<Integer, SshKeyEntry> keys = new HashMap<>();

    public MockKeyManager() {
    }

    @Override
    public Collection<SshKeyEntry> getSshList() {
        return keys.values();
    }

    @Override
    public Set<Integer> getSshIdsList() {
        return keys.keySet();
    }

    @Override
    public List<String> getIdentityNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SshKeyEntry addSshKey(int id, String keyName, String fileName) throws IOException {
        return addSshKey(id, keyName, fileName, null);
    }

    public SshKeyEntry addSshKey(int id, String keyName, String fileName, String passphrase) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        assert (file.exists());

        List<String> lines = Files.readAllLines(Paths.get(file.getPath()), Charset.defaultCharset());

        String privateKey = StringUtils.join(lines, "\n");

        SshKeyEntry entry = new TestSshKeyEntry(id, keyName, privateKey);
        entry.setPassPhrase(passphrase);

        keys.put(id, entry);

        return entry;
    }

    @Override
    public SshKeyEntry addSshKey(SshKey entry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SshKeyEntry addSshKey(String keyName, String privateKey, String passPhrase) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteSshKey(Integer id) {
        keys.remove(id);
    }

    @Override
    public void setIdentityNameProvider(IdentityNameProvider identityNameProvider) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SshKeyEntry getKey(Integer keyId) {
        return keys.get(keyId);
    }

    @Override
    public SshKeyEntry getByPrivateKey(String privateKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean needPassphrase(String privateKey) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
