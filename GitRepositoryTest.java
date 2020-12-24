package com.bigbrassband.jira.git;

import com.bigbrassband.jira.git.services.props.GProperties;
import com.bigbrassband.jira.git.services.props.GitProperties;
import com.bigbrassband.jira.git.ao.MockGitRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GitRepositoryTest {

    @Test
    public void oldSettingsShouldBeStored() {
        MockGitRepository entity = new MockGitRepository();
        entity.setWebLinkType("some");
        entity.setViewFormat("some");
        entity.setMaxMinsToCommitEmail(1441);
        entity.setFileAddedFormat("some");
        entity.setFileModifiedFormat("some");
        entity.setFileDeletedFormat("some");
        entity.setChangesetFormat("some");

        GProperties properties = new GitProperties();
        properties.setMaxMinsToCommitEmail(null);
        properties.setWebLinkType("");
        GProperties.Util.fillEntityFromProperties(entity, properties, new MockJiraUtils());

        Assert.assertEquals("", entity.getWebLinkType());
        Assert.assertEquals("some", entity.getViewFormat());
        Assert.assertEquals("some", entity.getFileAddedFormat() );
        Assert.assertEquals("some", entity.getFileModifiedFormat());
        Assert.assertEquals("some", entity.getFileDeletedFormat());
        Assert.assertEquals("some", entity.getChangesetFormat());
        Assert.assertEquals(new Integer(1441), entity.getMaxMinsToCommitEmail());
    }
}
