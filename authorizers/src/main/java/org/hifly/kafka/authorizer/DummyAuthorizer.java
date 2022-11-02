package org.hifly.kafka.authorizer;

import kafka.security.authorizer.AclAuthorizer;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.resource.ResourceType;
import org.apache.kafka.server.authorizer.Action;
import org.apache.kafka.server.authorizer.AuthorizableRequestContext;
import org.apache.kafka.server.authorizer.AuthorizationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DummyAuthorizer extends AclAuthorizer {

    private static Logger LOGGER = LoggerFactory.getLogger(DummyAuthorizer.class);

    @Override
    public void configure(final Map<String, ?> javaConfigs) {
        synchronized (DummyAuthorizer.class) {
            super.configure(javaConfigs);
        }
    }

    @Override
    public List<AuthorizationResult> authorize(final AuthorizableRequestContext requestContext, final List<Action> actions) {
        boolean toPrint = false;
        StringBuilder sb = new StringBuilder();

        sb.append("\n\n\n------");
        sb.append("\nRequest Context:");
        sb.append("\n    -clientId:" + requestContext.clientId());
        sb.append("\n    -listenerName:" + requestContext.listenerName());
        sb.append("\n    -requestType:" + requestContext.requestType());
        sb.append("\n    -requestVersion:" + requestContext.requestVersion());
        sb.append("\n    -clientAddress->hostname:" + requestContext.clientAddress().getHostName());
        sb.append("\n    -clientAddress->hostaddress:" + requestContext.clientAddress().getHostAddress());
        sb.append("\n    -clientAddress->address:" + requestContext.clientAddress().getAddress());
        sb.append("\n    -clientAddress->address:" + requestContext.clientAddress().getCanonicalHostName());
        sb.append("\n    -correlationId:" + requestContext.correlationId());
        sb.append("\n    -principal->type:" + requestContext.principal().getPrincipalType());
        sb.append("\n    -principal->name:" + requestContext.principal().getName());
        sb.append("\n    -securityProtocol->name:" + requestContext.securityProtocol().name());
        sb.append("\n\n\n------");
        sb.append("Actions:");


        List<AuthorizationResult> results;
        // see https://kafka.apache.org/protocol#protocol_api_keys

        //PRODUCE, FETCH, LISTOFFSETS, METADATA, OFFSETCOMMIT, OFFSETFETCH, FINDCOORDINATOR, HEARTBEAT, JOINGROUP, LEAVEGROUP, SYNCGROUP, CREATETOPICS, DELETETOPICS, INITPRODUCERID, OFFSETFORLEADEREPOCH  request
        if(requestContext.requestType() == 0 ||
                requestContext.requestType() == 1 ||
                requestContext.requestType() == 2 ||
                requestContext.requestType() == 3 ||
                requestContext.requestType() == 8 ||
                requestContext.requestType() == 9 ||
                requestContext.requestType() == 10 ||
                requestContext.requestType() == 11 ||
                requestContext.requestType() == 12 ||
                requestContext.requestType() == 13 ||
                requestContext.requestType() == 14 ||
                requestContext.requestType() == 19 ||
                requestContext.requestType() == 20 ||
                requestContext.requestType() == 22 ||
                requestContext.requestType() == 23) {
            results = noACLsForOperationsOnTopics(requestContext, actions);
        } else {
            results = super.authorize(requestContext, actions);
        }

        for(Action action: actions) {
            sb.append("\n    -action:" + action);
        }

        sb.append("\nAuthorizationResult:");
        for(AuthorizationResult result: results) {
            sb.append("\n    -result:" + result);
            if(result == AuthorizationResult.DENIED) {
                toPrint = true;
            }
        }

        if(toPrint) {
            System.out.println(sb);
        }


        return results;
    }

    private List<AuthorizationResult> noACLsForOperationsOnTopics(AuthorizableRequestContext requestContext, List<Action> actions) {

        List<AuthorizationResult> results = new ArrayList<>();

        boolean allowed = false;

        if(actions != null && !actions.isEmpty()) {
            for(Action action: actions) {

                //PRODUCE
                if(requestContext.requestType() == 0 &&
                        (action.resourcePattern().resourceType() == ResourceType.TOPIC) &&
                        (action.operation().code() == AclOperation.WRITE.code())) {
                    allowed = true;
                }

                //FETCH
                if(requestContext.requestType() == 1 &&
                        (action.resourcePattern().resourceType() == ResourceType.TOPIC) &&
                        (action.operation().code() == AclOperation.READ.code())) {
                    allowed = true;
                }

                //LISTOFFSETS
                if(requestContext.requestType() == 2 &&
                        (action.resourcePattern().resourceType() == ResourceType.TOPIC) &&
                        (action.operation().code() == AclOperation.DESCRIBE.code())) {
                    allowed = true;
                }

                //METADATA
                if(requestContext.requestType() == 3 &&
                        (action.resourcePattern().resourceType() == ResourceType.TOPIC) &&
                        (action.operation().code() == AclOperation.DESCRIBE.code() || action.operation().code() == AclOperation.CREATE.code())) {
                    allowed = true;
                }

                //OFFSETCOMMIT
                if(requestContext.requestType() == 8 &&
                        (action.resourcePattern().resourceType() == ResourceType.GROUP || action.resourcePattern().resourceType() == ResourceType.TOPIC) &&
                        (action.operation().code() == AclOperation.READ.code())) {
                    allowed = true;
                }

                //OFFSETFETCH
                if(requestContext.requestType() == 9 &&
                        (action.resourcePattern().resourceType() == ResourceType.TOPIC || action.resourcePattern().resourceType() == ResourceType.GROUP) &&
                        (action.operation().code() == AclOperation.DESCRIBE.code())) {
                    allowed = true;
                }

                //FINDCOORDINATOR
                if(requestContext.requestType() == 10 &&
                        (action.resourcePattern().resourceType() == ResourceType.GROUP) &&
                        (action.operation().code() == AclOperation.DESCRIBE.code())) {
                    allowed = true;
                }

                //JOINGROUP
                if(requestContext.requestType() == 11 &&
                        (action.resourcePattern().resourceType() == ResourceType.GROUP) &&
                        (action.operation().code() == AclOperation.READ.code())) {
                    allowed = true;
                }

                //HEARTBEAT
                if(requestContext.requestType() == 12 &&
                        (action.resourcePattern().resourceType() == ResourceType.GROUP) &&
                        (action.operation().code() == AclOperation.READ.code())) {
                    allowed = true;
                }

                //LEAVEGROUP
                if(requestContext.requestType() == 13 &&
                        (action.resourcePattern().resourceType() == ResourceType.GROUP) &&
                        (action.operation().code() == AclOperation.READ.code())) {
                    allowed = true;
                }

                //SYNCGROUP
                if(requestContext.requestType() == 14 &&
                        (action.resourcePattern().resourceType() == ResourceType.GROUP) &&
                        (action.operation().code() == AclOperation.READ.code())) {
                    allowed = true;
                }

                //CREATETOPICS
                if(requestContext.requestType() == 19 &&
                        (action.resourcePattern().resourceType() == ResourceType.CLUSTER || action.resourcePattern().resourceType() == ResourceType.TOPIC) &&
                        (action.operation().code() == AclOperation.CREATE.code())) {
                    allowed = true;
                }

                //CREATETOPICS
                if(requestContext.requestType() == 19 &&
                        (action.resourcePattern().resourceType() == ResourceType.TOPIC) &&
                        (action.operation().code() == AclOperation.DESCRIBE_CONFIGS.code())) {
                    allowed = true;
                }

                //DELETETOPICS
                if(requestContext.requestType() == 20 &&
                        (action.resourcePattern().resourceType() == ResourceType.TOPIC) &&
                        (action.operation().code() == AclOperation.DELETE.code() || action.operation().code() == AclOperation.DESCRIBE.code())) {
                    allowed = true;
                }

                //INITPRODUCERID
                if(requestContext.requestType() == 22 &&
                        (action.resourcePattern().resourceType() == ResourceType.CLUSTER) &&
                        (action.operation().code() == AclOperation.IDEMPOTENT_WRITE.code())) {
                    allowed = true;
                }

                //OFFSETFORLEADEREPOCH
                if(requestContext.requestType() == 23 &&
                        (action.resourcePattern().resourceType() == ResourceType.CLUSTER) &&
                        (action.operation().code() == AclOperation.CLUSTER_ACTION.code())) {
                    allowed = true;
                }

                //OFFSETFORLEADEREPOCH
                if(requestContext.requestType() == 23 &&
                        (action.resourcePattern().resourceType() == ResourceType.TOPIC) &&
                        (action.operation().code() == AclOperation.DESCRIBE.code())) {
                    allowed = true;
                }

            }
        } else {
            allowed = true;
        }

        for(Action action: actions) {
            results.add(allowed ? AuthorizationResult.ALLOWED : AuthorizationResult.DENIED);
        }

        return results;
    }

}