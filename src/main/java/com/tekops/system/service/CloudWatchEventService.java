package com.tekops.system.service;

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



/**
 * Created by ravikumar on 18/08/18.
 */
@Service
public class CloudWatchEventService {

    public PutRuleResult createEventRule(String name, String repoArn) {
        final AmazonCloudWatchEvents cwe =
                AmazonCloudWatchEventsClientBuilder.defaultClient();

        PutRuleRequest request = new PutRuleRequest()
                .withName(name)
                .withEventPattern(getEventPattern(repoArn))
                .withState(RuleState.ENABLED);

        return cwe.putRule(request);
    }

    public PutTargetsResult addTarget(String eventRuleName, String pipelineRoleArn, String pipelineArn){
        final AmazonCloudWatchEvents cwe =
                AmazonCloudWatchEventsClientBuilder.defaultClient();

        Target target = new Target()
                .withRoleArn(pipelineRoleArn)
                .withArn(pipelineArn)
                .withId("id1"+ DateTime.now().getMillis());

        PutTargetsRequest request = new PutTargetsRequest()
                .withTargets(target)
                .withRule(eventRuleName);

        return cwe.putTargets(request);
    }

    private String getEventPattern(String repoArn) {
        String pattern="{\n" +
                "  \"source\": [\n" +
                "    \"aws.codecommit\"\n" +
                "  ],\n" +
                "  \"detail-type\": [\n" +
                "    \"CodeCommit Repository State Change\"\n" +
                "  ],\n" +
                "  \"resources\": [\n" +
                "    \""+repoArn+"\"\n" +
                "  ],\n" +
                "  \"detail\": {\n" +
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
}
