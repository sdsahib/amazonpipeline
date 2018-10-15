package com.tekops.system.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEvents;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClientBuilder;
import com.amazonaws.services.cloudwatchevents.model.PutRuleRequest;
import com.amazonaws.services.cloudwatchevents.model.PutRuleResult;
import com.amazonaws.services.cloudwatchevents.model.RuleState;
import com.amazonaws.services.cloudwatchevents.model.PutTargetsRequest;
import com.amazonaws.services.cloudwatchevents.model.PutTargetsResult;
import com.amazonaws.services.cloudwatchevents.model.Target;

import java.util.Objects;

/**
 * Handles CloudWatch related AWS service calls.
 * Created by ravikumar on 18/08/18.
 * Updated by rapaul.
 */
@Service
@Slf4j
public class CloudWatchEventService {

    public boolean createEventRuleForCodePipelineTriggering(final String ruleName, final String repoArn) {
        final AmazonCloudWatchEvents cwe = AmazonCloudWatchEventsClientBuilder.defaultClient();
        PutRuleRequest request = new PutRuleRequest()
                .withName(ruleName)
                .withEventPattern(getEventPatternForCodePipelineTriggering(repoArn))
                .withState(RuleState.ENABLED);
        final PutRuleResult putRuleResult = cwe.putRule(request);
        log.info("Created CloudWatch rule. Here is the response from AWS -> " + putRuleResult);
        return Objects.nonNull(putRuleResult) && StringUtils.isNotBlank(putRuleResult.getRuleArn());
    }

    public void addTargetForCodePipelineTriggering(final String eventRuleName, final String pipelineRoleArn,
                                                               final String codePipelineArn) {
        final AmazonCloudWatchEvents cwe = AmazonCloudWatchEventsClientBuilder.defaultClient();
        final Target target = new Target()
                .withRoleArn(pipelineRoleArn)
                .withArn(codePipelineArn)
                .withId("id1"+ DateTime.now().getMillis());
        final PutTargetsRequest request = new PutTargetsRequest()
                .withTargets(target)
                .withRule(eventRuleName);
        final PutTargetsResult putTargetsResult = cwe.putTargets(request);
        log.info("Created CloudWatch rule target. Here is the response from AWS -> " + putTargetsResult);
    }

    public boolean createEventRuleForSnsNotification(final String ruleName, final String codePipelineName) {
        final AmazonCloudWatchEvents cwe = AmazonCloudWatchEventsClientBuilder.defaultClient();
        final PutRuleRequest request = new PutRuleRequest()
                .withName(ruleName)
                .withEventPattern(getEventPatternForSnsNotification(codePipelineName))
                .withState(RuleState.ENABLED);
        final PutRuleResult putRuleResult = cwe.putRule(request);
        log.info("Created CloudWatch rule. Here is the response from AWS -> " + putRuleResult);
        return Objects.nonNull(putRuleResult) && StringUtils.isNotBlank(putRuleResult.getRuleArn());
    }

    public void addTargetForSnsNotification(final String eventRuleName, final String snsTopicArn) {
        final AmazonCloudWatchEvents cwe = AmazonCloudWatchEventsClientBuilder.defaultClient();

        final Target target = new Target()
                .withArn(snsTopicArn)
                .withId("id1"+ DateTime.now().getMillis());

        PutTargetsRequest request = new PutTargetsRequest()
                .withTargets(target)
                .withRule(eventRuleName);
        final PutTargetsResult putTargetsResult = cwe.putTargets(request);
        log.info("Created CloudWatch rule target. Here is the response from AWS -> " + putTargetsResult);
    }

    private String getEventPatternForCodePipelineTriggering(final String repositoryArn) {
        final String pattern="{\n" +
                "  \"source\": [\n" +
                "    \"aws.codecommit\"\n" +
                "  ],\n" +
                "  \"detail-type\": [\n" +
                "    \"CodeCommit Repository State Change\"\n" +
                "  ],\n" +
                "  \"resources\": [\n" +
                "    \""+repositoryArn+"\"\n" +
                "  ],\n" +
                "  \"detail\": {\n" +
                "    \"event\": [\n" +
                "      \"referenceCreated\",\n" +
                "      \"referenceUpdated\"\n" +
                "    ],\n" +
                "    \"referenceType\": [\n" +
                "      \"branch\"\n" +
                "    ],\n" +
                "    \"referenceName\": [\n" +
                "      \"master\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        return pattern;
    }

    private String getEventPatternForSnsNotification(final String codePipelineName) {
        final String pattern="{\n" +
                "  \"source\": [\n" +
                "    \"aws.codepipeline\"\n" +
                "  ],\n" +
                "  \"detail-type\": [\n" +
                "    \"CodePipeline Pipeline Execution State Change\"\n" +
                "  ],\n" +
                "  \"detail\": {\n" +
                "    \"pipeline\": [\n" +
                "    \""+codePipelineName+"\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        return pattern;
    }
}
