import jenkins.scm.api.SCMHead
import jenkins.scm.api.SCMRevision
import jenkins.scm.api.SCMRevisionAction
import jenkins.scm.api.SCMSource
import hudson.model.Run
import org.jenkinsci.plugins.github_branch_source.PullRequestSCMHead

void call(Map parameters = [:]) {
    githubNotify(
        /* groovylint-disable-next-line UnnecessaryGetter */
        context: parameters.context ?: getDefaultContext(),
        status: parameters.status ?: currentBuild.currentResult,
        description: parameters.description ?: currentBuild.description,
    )
}

// Reference: https://bit.ly/2qf7sy9
String getDefaultContext() {
    Run build = currentBuild.rawBuild
    SCMSource src = SCMSource.SourceByItem.findSource(build.parent)
    SCMRevision revision = src != null ? SCMRevisionAction.getRevision(src, build) : null
    SCMHead head = revision != null ? revision.head : null

    /* groovylint-disable-next-line Instanceof */
    if (head instanceof PullRequestSCMHead) {
        if (((PullRequestSCMHead) head).isMerge()) {
            'continuous-integration/jenkins/pr-merge'
        } else {
            'continuous-integration/jenkins/pr-head'
        }
    } else {
        'continuous-integration/jenkins/branch'
    }
}
