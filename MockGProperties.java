package com.bigbrassband.jira.git;

import com.bigbrassband.jira.git.services.integration.IntegrationType;
import com.bigbrassband.jira.git.services.props.GProperties;
import com.bigbrassband.jira.git.services.props.GitPropertyKey;

import java.util.Date;
import java.util.Random;

/**
 *
 * Created by ababilo on 5/20/16.
 */
public class MockGProperties implements GProperties {

    private Integer id;
    private String origin;
    private String root;
    private String realRoot;
    private Boolean absoluteRoot;
    private String displayName;
    private Boolean revisionIndexing = GitPropertyKey.GIT_REVISION_INDEXING_KEY.getDefaultBooleanValue();
    private Boolean enableFetches = GitPropertyKey.GIT_ENABLE_FETCHES.getDefaultBooleanValue();
    private Boolean sendCommitEmails = GitPropertyKey.GIT_SEND_COMMIT_EMAILS.getDefaultBooleanValue();
    private Integer maxMinsToCommitEmail = GitPropertyKey.GIT_MAX_MINS_TO_COMMIT_EMAIL.getDefaultIntValue();
    private Boolean disabled = GitPropertyKey.GIT_REPOSITORY_DISABLED.getDefaultBooleanValue();
    private Boolean hosted = GitPropertyKey.GIT_REPOSITORY_HOSTED.getDefaultBooleanValue();
    private Boolean enableSmartCommits = GitPropertyKey.GIT_REPOSITORY_SMARTCOMMITS_ENABLED.getDefaultBooleanValue();
    private Boolean enableGitViewer = GitPropertyKey.GIT_VIEWER_ENABLED.getDefaultBooleanValue();
    private Boolean enableSourcesDiffView = GitPropertyKey.GIT_SOURCES_DIFF_VIEW_ENABLED.getDefaultBooleanValue();
    private Boolean disableSslVerification = GitPropertyKey.GIT_DISABLE_SSL_VERIFICATION.getDefaultBooleanValue();
    private Boolean limitGitData = GitPropertyKey.GIT_LIMIT_GIT_DATA.getDefaultBooleanValue();
    private Boolean global = GitPropertyKey.GIT_REPOSITORY_GLOBAL.getDefaultBooleanValue();
    private Boolean commitsValidationRequired = GitPropertyKey.GIT_COMMITS_VALIDATION_REQUIRED.getDefaultBooleanValue();
    private Date initDate;
    private String changeSetFormat;
    private String webLinkType;
    private String viewFormat;
    private String fileAddedFormat;
    private String fileDeletedFormat;
    private String fileModifiedFormat;
    private String mergeRequestFormat;
    private String branchLinkFormat;
    private String username;
    private String password;
    private String pat;
    private String mainBranch;
    private String repositoryKey;
    private Integer sshKeyId;
    private Integer trackedFolderId;
    private String tagsFilter;
    private Boolean requireUserPat;
    private IntegrationType integrationType;
    private String apiPath;
    private String apiFilter;
    private Integer folderDepth;
    private String tfsCollection;
    private String awsRegion;
    private Boolean refSpecHeads = GitPropertyKey.GIT_REF_SPEC_HEADS.getDefaultBooleanValue();
    private Boolean refSpecTags = GitPropertyKey.GIT_REF_SPEC_TAGS.getDefaultBooleanValue();
    private Boolean refSpecNotes = GitPropertyKey.GIT_REF_SPEC_NOTES.getDefaultBooleanValue();
    private Boolean refSpecChanges = GitPropertyKey.GIT_REF_SPEC_CHANGES.getDefaultBooleanValue();
    private String refSpecCustom;
    private Boolean trustFolderStat;
    private State state;

    public MockGProperties() {
        this.id = new Random().nextInt();
        this.initDate = new Date();
    }

    public MockGProperties(int id) {
        this.id = id;
    }

    @Override
    public String getOrigin() {
        return origin;
    }

    @Override
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    @Override
    public String getRoot() {
        return root;
    }

    @Override
    public void setRoot(String root) {
        this.root = root;
    }

    @Override
    public String getRealRoot() {
        return realRoot;
    }

    @Override
    public void setRealRoot(String realRoot) {
        this.realRoot = realRoot;
    }

    @Override
    public Boolean isAbsoluteRoot() {
        return absoluteRoot;
    }

    @Override
    public void setAbsoluteRoot(Boolean absoluteRoot) {
        this.absoluteRoot = absoluteRoot;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public Boolean getRevisionIndexing() {
        return revisionIndexing;
    }

    @Override
    public void setRevisionIndexing(Boolean revisionIndexing) {
        this.revisionIndexing = revisionIndexing;
    }

    @Override
    public Boolean getEnableFetches() {
        return enableFetches;
    }

    @Override
    public void setEnableFetches(Boolean enableFetches) {
        this.enableFetches = enableFetches;
    }

    @Override
    public Boolean getSendCommitEmails() {
        return sendCommitEmails;
    }

    @Override
    public void setSendCommitEmails(Boolean sendCommitEmails) {
        this.sendCommitEmails = sendCommitEmails;
    }

    @Override
    public Integer getMaxMinsToCommitEmail() {
        return maxMinsToCommitEmail;
    }

    @Override
    public void setMaxMinsToCommitEmail(Integer maxMinsToCommitEmail) {
        this.maxMinsToCommitEmail = maxMinsToCommitEmail;
    }

    @Override
    public Boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Boolean isSmartCommitsEnabled() {
        return enableSmartCommits;
    }

    @Override
    public void setSmartCommitsEnabled(Boolean enableSmartCommits) {
        this.enableSmartCommits = enableSmartCommits;
    }

    @Override
    public Boolean isGitViewerEnabled() {
        return enableGitViewer;
    }

    @Override
    public Boolean isSourcesDiffViewEnabled() {
        return enableSourcesDiffView;
    }

    @Override
    public void setGitViewerEnabled(Boolean enableGitViewer) {
        this.enableGitViewer = enableGitViewer;
    }

    @Override
    public void setSourcesDiffViewEnabled(Boolean sourcesDiffViewEnabled) {
        this.enableSourcesDiffView = sourcesDiffViewEnabled;
    }

    @Override
    public Boolean isGlobal() {
        return global;
    }

    @Override
    public void setGlobal(Boolean global) {
        this.global = global;
    }

    /**
     * @deprecated (method getRevisionCacheSize is deprecated, use the suitable method in global settings)
     */
    @Deprecated
    @Override
    public Integer getRevisionCacheSize() {
        return null;
    }

    /**
     * @deprecated (method setRevisionCacheSize is deprecated. Use the suitable method in global settings)
     */
    @Deprecated
    @Override
    public void setRevisionCacheSize(Integer revisionCacheSize) {

    }

    @Override
    public String getChangesetFormat() {
        return changeSetFormat;
    }

    @Override
    public void setChangesetFormat(String changeSetFormat) {
        this.changeSetFormat = changeSetFormat;
    }

    @Override
    public String getWebLinkType() {
        return webLinkType;
    }

    @Override
    public void setWebLinkType(String webLinkType) {
        this.webLinkType = webLinkType;
    }

    @Override
    public String getViewFormat() {
        return viewFormat;
    }

    @Override
    public void setViewFormat(String viewFormat) {
        this.viewFormat = viewFormat;
    }

    @Override
    public String getFileAddedFormat() {
        return fileAddedFormat;
    }

    @Override
    public void setFileAddedFormat(String fileAddedFormat) {
        this.fileAddedFormat = fileAddedFormat;
    }

    @Override
    public String getFileDeletedFormat() {
        return fileDeletedFormat;
    }

    @Override
    public void setFileDeletedFormat(String fileDeletedFormat) {
        this.fileDeletedFormat = fileDeletedFormat;
    }

    @Override
    public String getFileModifiedFormat() {
        return fileModifiedFormat;
    }

    @Override
    public void setFileModifiedFormat(String fileModifiedFormat) {
        this.fileModifiedFormat = fileModifiedFormat;
    }

    @Override
    public String getMergeRequestFormat() {
        return mergeRequestFormat;
    }

    @Override
    public void setMergeRequestFormat(String mergeRequestFormat) {
        this.mergeRequestFormat = mergeRequestFormat;
    }

    @Override
    public String getBranchLinkFormat() {
        return branchLinkFormat;
    }

    @Override
    public void setBranchLinkFormat(String branchLinkFormat) {
        this.branchLinkFormat = branchLinkFormat;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPat() {
        return pat;
    }

    @Override
    public void setPat(String pat) {
        this.pat = pat;
    }

    @Override
    public String getMainBranch() {
        return mainBranch;
    }

    @Override
    public void setMainBranch(String mainBranch) {
        this.mainBranch = mainBranch;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getSshKeyId() {
        return sshKeyId;
    }

    @Override
    public void setSshKeyId(Integer sshKeyId) {
        this.sshKeyId = sshKeyId;
    }

    @Override
    public String getRepositoryKey() {
        return repositoryKey;
    }

    @Override
    public Boolean isHosted() {
        return hosted;
    }

    @Override
    public Date getInitDate() {
        return initDate;
    }

    @Override
    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    @Override
    public Boolean isCommitsValidationRequired() {
        return commitsValidationRequired;
    }

    @Override
    public void setCommitsValidationRequired(Boolean commitsValidationRequired) {
        this.commitsValidationRequired = commitsValidationRequired;
    }

    @Override
    public String getTagsFilter() {
        return tagsFilter;
    }

    @Override
    public void setTagsFilter(String tagsFilter) {
        this.tagsFilter = tagsFilter;
    }

    @Override
    public Boolean isRequireUserPat() {
        return requireUserPat;
    }

    @Override
    public void setRequireUserPat(Boolean requireUserPat) {
        this.requireUserPat = requireUserPat;
    }

    @Override
     public Integer getTrackedFolderId() {
        return trackedFolderId;
    }

    @Override
    public void setTrackedFolderId(Integer trackedFolderId) {
        this.trackedFolderId = trackedFolderId;
    }

    @Override
    public void setRepositoryKey(String repositoryKey) {
        this.repositoryKey = repositoryKey;
    }

    @Override
    public void setHosted(Boolean hosted) {
        this.hosted = hosted;
    }

    @Override
    public IntegrationType getIntegrationType() {
        return integrationType;
    }

    @Override
    public Boolean isDisableSslVerification() {
        return disableSslVerification;
    }

    @Override
    public Boolean isLimitGitData() {
        return limitGitData;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setIntegrationType(IntegrationType integrationType) {
        this.integrationType = integrationType;
    }

    @Override
    public void setDisableSslVerification(Boolean disableSslVerification) {
        this.disableSslVerification = disableSslVerification;
    }

    @Override
    public void setLimitGitData(Boolean limitGitData) {
        this.limitGitData = limitGitData;
    }

    @Override
    public String getGitLabScanQueryParams() {
        return null;
    }

    @Override
    public void setGitLabScanQueryParams(String queryParams) {
        // do nothing
    }

    @Override
    public String getApiPath() {
        return apiPath;
    }

    @Override
    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    @Override
    public String getApiFilter() {
        return apiFilter;
    }

    @Override
    public void setApiFilter(String apiFilter) {
        this.apiFilter = apiFilter;
    }

    @Override
    public Integer getFolderDepth() {
        return folderDepth;
    }

    @Override
    public void setFolderDepth(Integer folderDepth) {
        this.folderDepth = folderDepth;
    }

    @Override
    public String getTfsCollection() {
        return tfsCollection;
    }

    @Override
    public void setTfsCollection(String tfsCollection) {
        this.tfsCollection = tfsCollection;
    }

    @Override
    public String getAwsRegion() {
        return awsRegion;
    }

    @Override
    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }

    @Override
    public Boolean isRefSpecHeads() {
        return refSpecHeads;
    }

    @Override
    public void setRefSpecHeads(Boolean refSpecHeads) {
        this.refSpecHeads = refSpecHeads;
    }

    @Override
    public Boolean isRefSpecTags() {
        return refSpecTags;
    }

    @Override
    public void setRefSpecTags(Boolean refSpecTags) {
        this.refSpecTags = refSpecTags;
    }

    @Override
    public Boolean isRefSpecNotes() {
        return refSpecNotes;
    }

    @Override
    public void setRefSpecNotes(Boolean refSpecNotes) {
        this.refSpecNotes = refSpecNotes;
    }

    @Override
    public Boolean isRefSpecChanges() {
        return refSpecChanges;
    }

    @Override
    public void setRefSpecChanges(Boolean refSpecChanges) {
        this.refSpecChanges = refSpecChanges;
    }

    @Override
    public String getRefSpecCustom() {
        return refSpecCustom;
    }

    @Override
    public void setRefSpecCustom(String refSpecCustom) {
        this.refSpecCustom = refSpecCustom;
    }

    public Boolean isTrustFolderStat() {
        return trustFolderStat;
    }

    @Override
    public void setTrustFolderStat(Boolean trustFolderStat) {
        this.trustFolderStat = trustFolderStat;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }
}